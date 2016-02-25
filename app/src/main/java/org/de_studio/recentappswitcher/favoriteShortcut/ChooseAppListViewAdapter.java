package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.de_studio.recentappswitcher.AppInfors;
import org.de_studio.recentappswitcher.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by hai on 2/23/2016.
 */
public class ChooseAppListViewAdapter extends BaseAdapter {
    private Context mContext;
    static private ArrayList<AppInfors> mAppInfosArrayList;
    private static final String LOG_TAG = ChooseAppListViewAdapter.class.getSimpleName();
    private int mPosition;
    private String mPackageSelected;

    public ChooseAppListViewAdapter(Context context, ArrayList<AppInfors> appInforses, int position) {
        super();
        mPosition = position;
        mContext = context;
        mAppInfosArrayList = appInforses;
        Realm myRealm = Realm.getInstance(mContext);
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id",mPosition).findFirst();
        if (shortcut != null ) {
            if (shortcut.getType() == Shortcut.TYPE_APP) {
                mPackageSelected = shortcut.getPackageName();
            }else mPackageSelected = null;

        }

    }

    public void setmPosition(int position) {
        mPosition = position;
        Realm myRealm = Realm.getInstance(mContext);
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id",mPosition).findFirst();
        if (shortcut != null ) {
            if (shortcut.getType() == Shortcut.TYPE_APP) {
                mPackageSelected = shortcut.getPackageName();
            }else mPackageSelected = null;

        }else mPackageSelected = null;
        ChooseAppListViewAdapter.this.notifyDataSetChanged();
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
            view = inflater.inflate(R.layout.choose_shortcut_app_list_item, parent, false);
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.choose_app_image_view);
        TextView textView = (TextView) view.findViewById(R.id.choose_app_title_text_view);
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.choose_app_radio_button);
        if (mPackageSelected !=null & mAppInfosArrayList.get(position).packageName.equalsIgnoreCase(mPackageSelected)){
            radioButton.setChecked(true);
        }else radioButton.setChecked(false);
        try {
            imageView.setImageDrawable(mContext.getPackageManager().getApplicationIcon(mAppInfosArrayList.get(position).packageName));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, "NameNotFound " + e);
        }

        textView.setText(mAppInfosArrayList.get(position).label);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm myRealm = Realm.getInstance(mContext);
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
                ChooseAppListViewAdapter.this.notifyDataSetChanged();
            }
        });
        return view;
    }
}
