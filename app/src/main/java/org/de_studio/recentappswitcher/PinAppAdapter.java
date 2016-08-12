package org.de_studio.recentappswitcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;

import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;
import org.de_studio.recentappswitcher.service.EdgeGestureService;
import org.de_studio.recentappswitcher.service.EdgeSetting;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by hai on 3/25/2016.
 */
public class PinAppAdapter extends BaseAdapter implements DragSortListView.DropListener ,DragSortListView.RemoveListener{
    private Context mContext;
    private static final String TAG = PinAppAdapter.class.getSimpleName();
    private Realm pinRealm;
    private Shortcut shortcut;
    private Drawable defaultDrawable;
    private SharedPreferences sharedPreferences;
    private IconPackManager.IconPack iconPack;
    private PackageManager packageManager;
    private static final String LOG_TAG = PinAppAdapter.class.getSimpleName();
    public PinAppAdapter(Context context) {
        super();
        mContext = context;
        packageManager = context.getPackageManager();
        pinRealm = Realm.getInstance(new RealmConfiguration.Builder(mContext)
                .name("pinApp.realm")
                .schemaVersion(EdgeGestureService.CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        sharedPreferences = mContext.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        String iconPackPacka = sharedPreferences.getString(EdgeSetting.ICON_PACK_PACKAGE_NAME_KEY, "none");
        if (!iconPackPacka.equals("none")) {
            IconPackManager iconPackManager = new IconPackManager();
            iconPackManager.setContext(mContext);
            iconPack = iconPackManager.getInstance(iconPackPacka);
        }

    }
    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_circle_favorite, parent, false);
        }
        ImageView icon = (ImageView) view.findViewById(R.id.item_icon);
        TextView label = (TextView) view.findViewById(R.id.item_label);


        shortcut = pinRealm.where(Shortcut.class).equalTo("id",position).findFirst();
        CharSequence title= "";

        if (shortcut != null) {
            switch (shortcut.getType()) {
                case Shortcut.TYPE_CONTACT:
                    title = shortcut.getName();
                    break;
                default:
                    title = shortcut.getLabel();
                    break;
            }


            Utility.setImageForShortcut(shortcut, packageManager, icon, mContext, iconPack, pinRealm, false);
            label.setText(title);
        } else {
            icon.setImageResource(R.drawable.ic_add_circle_outline_white_48dp);
            icon.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
            label.setText(R.string.recent_app);
        }
        return view;
    }

    @Override
    public void drop(int from, int to) {
        RealmResults<Shortcut> results = pinRealm.where(Shortcut.class).findAll();
        Shortcut[] shortcuts = new Shortcut[results.size()];
        Shortcut tem;
        int i = 0;
        for (Shortcut shortcut : results) {
            shortcuts[i] = pinRealm.copyFromRealm(shortcut);
            i++;
        }
        if (from > to) {
            tem = shortcuts[from];
            for (int j = from; j > to; j--) {
                shortcuts[j] = shortcuts[j -1];
            }
            shortcuts[to]= tem;
        }else if (from < to) {
            tem = shortcuts[from];
            for (int k = from; k < to; k++) {
                shortcuts[k] = shortcuts[k + 1];
            }
            shortcuts[to] = tem;
        }
        pinRealm.beginTransaction();
        pinRealm.delete(Shortcut.class);
        int m = 0;
        for (Shortcut shortcut : shortcuts) {
//            Shortcut shortcut1 = new Shortcut();
//            shortcut1.setPackageName(shortcut);
//            shortcut1.setId(m);
            Log.e(TAG,  "\nm = " + m);
            shortcut.setId(m);
            m++;
            pinRealm.copyToRealm(shortcut);
        }
        pinRealm.commitTransaction();
        notifyDataSetChanged();
        mContext.stopService(new Intent(mContext, EdgeGestureService.class));
        mContext.startService(new Intent(mContext, EdgeGestureService.class));
    }

    public void remove(int id) {
        Log.e(LOG_TAG, "remove " + id);
        pinRealm.beginTransaction();
        pinRealm.where(Shortcut.class).equalTo("id",id).findFirst().deleteFromRealm();
        RealmResults<Shortcut> results = pinRealm.where(Shortcut.class).findAll().sort("id", Sort.ASCENDING);
        for (int i = 0; i < results.size(); i++) {
            Log.e(LOG_TAG, "id = " + results.get(i).getId());
            if (results.get(i).getId() >= id) {
                Log.e(LOG_TAG, "when i = " + i + "result id = " + results.get(i).getId());
                Shortcut shortcut = results.get(i);
                int oldId = shortcut.getId();
                shortcut.setId(oldId - 1);
            }

//                            results.get(i).setId(results.get(i).getId() - 1);
        }
        pinRealm.commitTransaction();
        notifyDataSetChanged();
        mContext.stopService(new Intent(mContext, EdgeGestureService.class));
        mContext.startService(new Intent(mContext, EdgeGestureService.class));
    }
}
