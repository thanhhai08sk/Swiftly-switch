package org.de_studio.recentappswitcher.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.MyRealmMigration;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by hai on 2/25/2016.
 */
public class FavoriteShortcutAdapter extends BaseAdapter {
    private static final String TAG = FavoriteShortcutAdapter.class.getSimpleName();
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private IconPackManager.IconPack iconPack;
    private int mBackgroundAt = -1;
    private Realm myRealm;
    private Shortcut shortcut;
    private Drawable defaultDrawable;
    private int iconPadding;
    private boolean backgroundMode = false;
    private float mIconScale;
    private PackageManager packageManager;
    private Shortcut[] shortcuts;

    public FavoriteShortcutAdapter(Context context) {
        mContext = context;
        iconPadding =(int) mContext.getResources().getDimension(R.dimen.icon_padding);
        setupShortcuts();
        sharedPreferences = mContext.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        mIconScale = sharedPreferences.getFloat(EdgeSetting.ICON_SCALE,1f);
        String iconPackPacka = sharedPreferences.getString(EdgeSetting.ICON_PACK_PACKAGE_NAME_KEY, "com.colechamberlin.stickers");
        if (!iconPackPacka.equals("none")) {
            IconPackManager iconPackManager = new IconPackManager();
            iconPackManager.setContext(mContext);
            iconPack = iconPackManager.getInstance(iconPackPacka);
        }
        backgroundMode = false;
        packageManager = mContext.getPackageManager();
    }

    @Override
    public int getCount() {
        return Utility.getSizeOfFavoriteGrid(mContext);
    }

    @Override
    public Object getItem(int position) {
        return getShortcut(position);
//        return myRealm.where(Shortcut.class).equalTo("id",position).findFirst();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setBackground(int position) {
        mBackgroundAt = position;
        backgroundMode = true;
        FavoriteShortcutAdapter.this.notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = (ImageView) convertView;
        if (backgroundMode && convertView != null) {
            if (position == mBackgroundAt) {
                if (imageView.getDrawable()!=null) {
                    imageView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.icon_background_square));
                }
            }else imageView.setBackground(null);
            return imageView;
        }
        if (imageView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams((int) (mContext.getResources().getDimension(R.dimen.icon_size) * mIconScale + mContext.getResources().getDimension(R.dimen.icon_padding_x_2)),
                    (int) (mContext.getResources().getDimension(R.dimen.icon_size) * mIconScale + mContext.getResources().getDimension(R.dimen.icon_padding_x_2))));
            imageView.setScaleType(ImageView.ScaleType.FIT_START);
            imageView.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
        }
//        shortcut = myRealm.where(Shortcut.class).equalTo("id",position).findFirst();
        shortcut = getShortcut(position);
        if (shortcut == null) {
            imageView.setImageResource(R.drawable.ic_add_circle_outline_white_48dp);
        } else {
            Utility.setImageForShortcut(shortcut,packageManager,imageView,mContext,iconPack,myRealm,true);
        }


        return imageView;
    }

    public void setupShortcuts() {
        myRealm = Realm.getInstance(new RealmConfiguration.Builder(mContext)
                .name("default.realm")
                .schemaVersion(EdgeGestureService. CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        RealmResults<Shortcut> results = myRealm.where(Shortcut.class).lessThan("id", 100).findAllSorted("id", Sort.ASCENDING);
        shortcuts = new Shortcut[results.size()];
        for (int i = 0; i < results.size(); i++) {
            shortcuts[i] = myRealm.copyFromRealm(results.get(i));
        }

        myRealm.close();
        myRealm = null;

    }

    private Shortcut getShortcut(int position) {
        if (position >= shortcuts.length || position == -1) {
            return null;
        } else {
            return shortcuts[position];
        }
    }

}
