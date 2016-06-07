package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.de_studio.recentappswitcher.AppInfors;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.service.EdgeSetting;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by hai on 2/23/2016.
 */
public class AppListAdapter extends BaseAdapter {
    private Context mContext;
    static private ArrayList<AppInfors> mAppInfosArrayList;
    private static final String LOG_TAG = AppListAdapter.class.getSimpleName();
    private int mPosition,mode;
    private String mPackageSelected;
    private AppChangeListener listener = null;
    private SharedPreferences sharedPreferences;
    private IconPackManager.IconPack iconPack;
    private Realm myRealm;

    public AppListAdapter(Context context, ArrayList<AppInfors> appInforses, int position, int mode) {
        super();
        this.mode = mode;
        mPosition = position;
        mContext = context;
        mAppInfosArrayList = appInforses;
        if (mode == FavoriteSettingActivity.MODE_GRID || mode == FavoriteSettingActivity.MODE_FOLDER) {
            myRealm = Realm.getDefaultInstance();
        } else {
            myRealm = Realm.getInstance(new RealmConfiguration.Builder(mContext).name("circleFavo.realm").build());
        }
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id",mPosition).findFirst();
        if (shortcut != null ) {
            if (shortcut.getType() == Shortcut.TYPE_APP) {
                mPackageSelected = shortcut.getPackageName();
            }else mPackageSelected = null;

        }
        sharedPreferences = mContext.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        String iconPackPacka = sharedPreferences.getString(EdgeSetting.ICON_PACK_PACKAGE_NAME_KEY, "none");
        if (!iconPackPacka.equals("none")) {
            IconPackManager iconPackManager = new IconPackManager();
            iconPackManager.setContext(mContext);
            iconPack = iconPackManager.getInstance(iconPackPacka);
        }

    }

    public void setmPositionAndMode(int position) {
        mPosition = position;
//        Realm myRealm = Realm.getInstance(mContext);
//        if (mode == FavoriteSettingActivity.MODE_GRID) {
//            myRealm = Realm.getDefaultInstance();
//        } else {
//            myRealm = Realm.getInstance(new RealmConfiguration.Builder(mContext).name("circleFavo.realm").build());
//        }

        
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id",mPosition).findFirst();
        if (shortcut != null ) {
            if (shortcut.getType() == Shortcut.TYPE_APP) {
                mPackageSelected = shortcut.getPackageName();
            }else mPackageSelected = null;

        }else mPackageSelected = null;
        AppListAdapter.this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mAppInfosArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mAppInfosArrayList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_choose_shortcut_app_list, parent, false);
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.choose_app_image_view);
        TextView textView = (TextView) view.findViewById(R.id.choose_app_title_text_view);
//        Realm myRealm = Realm.getInstance(mContext);
//        Realm myRealm = Realm.getDefaultInstance();
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id",mPosition).findFirst();
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.choose_app_radio_button);
        if (shortcut != null) {
            if (shortcut.getType()== Shortcut.TYPE_APP && mPackageSelected !=null && mAppInfosArrayList.get(position).packageName.equalsIgnoreCase(mPackageSelected)){
                radioButton.setChecked(true);
            }else radioButton.setChecked(false);
        }else radioButton.setChecked(false);
        try {
            Drawable defaultDrawable = mContext.getPackageManager().getApplicationIcon(mAppInfosArrayList.get(position).packageName);
            if (iconPack!=null) {

                imageView.setImageDrawable(iconPack.getDrawableIconForPackage(mAppInfosArrayList.get(position).packageName, defaultDrawable));
            } else {
                imageView.setImageDrawable(defaultDrawable);

            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, "NameNotFound " + e);
        }

        textView.setText(mAppInfosArrayList.get(position).label);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Realm myRealm = Realm.getInstance(mContext);
//                Realm myRealm;
//                if (mode == FavoriteSettingActivity.MODE_GRID) {
//                    myRealm = Realm.getDefaultInstance();
//                } else {
//                    myRealm = Realm.getInstance(new RealmConfiguration.Builder(mContext).name("circleFavo.realm").build());
//                }

                myRealm.beginTransaction();
                RealmResults<Shortcut> oldShortcut = myRealm.where(Shortcut.class).equalTo("id",mPosition).findAll();
                Log.e(LOG_TAG, "mPosition = " + mPosition);
                oldShortcut.clear();
//                Shortcut shortcut = myRealm.createObject(Shortcut.class);
                Shortcut shortcut = new Shortcut();
                shortcut.setType(Shortcut.TYPE_APP);
                shortcut.setId(mPosition);
                shortcut.setLabel(mAppInfosArrayList.get(position).label);
                shortcut.setPackageName(mAppInfosArrayList.get(position).packageName);
                myRealm.copyToRealm(shortcut);
                myRealm.commitTransaction();
                mPackageSelected = mAppInfosArrayList.get(position).packageName;
                AppListAdapter.this.notifyDataSetChanged();
                listener.onAppChange();
            }
        });
        return view;
    }

    public interface AppChangeListener{
        void onAppChange();
    }

    public void registerListener(AppChangeListener listener) {
        this.listener = listener;
    }

}
