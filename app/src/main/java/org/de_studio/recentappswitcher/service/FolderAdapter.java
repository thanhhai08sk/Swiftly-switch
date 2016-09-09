package org.de_studio.recentappswitcher.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

/**
 * Created by HaiNguyen on 6/1/16.
 */
public class FolderAdapter extends BaseAdapter {
    private static final String TAG = FolderAdapter.class.getSimpleName();
    private Context mContext;
    private int mPosition;
    private Realm myRealm;
    private int mBackgroundAt = -1;
    private Shortcut folderShortcut;
    private IconPackManager.IconPack iconPack;
    private float mIconScale;
    private int iconPadding;
    private boolean backgroundMode = false;
    private SharedPreferences sharedPreferences;
    private PackageManager packageManager;

    public FolderAdapter(Context context, int mPosition) {
        mContext = context;
        this.mPosition = mPosition;
        setFolderId(mPosition);
        myRealm = Realm.getInstance(new RealmConfiguration.Builder(mContext)
                .name("default.realm")
                .schemaVersion(EdgeGestureService. CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        sharedPreferences = context.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        packageManager = mContext.getPackageManager();
        String iconPackPacka = sharedPreferences.getString(EdgeSetting.ICON_PACK_PACKAGE_NAME_KEY, "none");
        if (!iconPackPacka.equals("none")) {
            IconPackManager iconPackManager = new IconPackManager();
            iconPackManager.setContext(mContext);
            iconPack = iconPackManager.getInstance(iconPackPacka);
        }
        mIconScale = sharedPreferences.getFloat(EdgeSetting.ICON_SCALE,1f);
        backgroundMode = false;
        iconPadding =(int) mContext.getResources().getDimension(R.dimen.icon_padding);
    }

    public void setFolderId(int folderId) {
        if (folderId != -1) {
            this.mPosition = folderId;
            folderShortcut = myRealm.where(Shortcut.class).equalTo("id", mPosition).findFirst();
        }

    }

    @Override
    public int getCount() {
        if (mPosition == -1) {
            return 0;
        }
        return (int) myRealm.where(Shortcut.class).greaterThan("id", (folderShortcut.getId()+1 ) * 1000 -1)
                .lessThan("id", (folderShortcut.getId() + 2)* 1000).count();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    public void setBackground(int position) {
        mBackgroundAt = position;
        backgroundMode = true;
        FolderAdapter.this.notifyDataSetChanged();
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView =(ImageView) convertView;
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
        int id = (folderShortcut.getId()+1)*1000 + position;
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id", id).findFirst();
        if (shortcut == null) {
            imageView.setImageResource(R.drawable.ic_add_circle_outline_white_48dp);

        } else {
            Utility.setImageForShortcut(shortcut,packageManager,imageView,mContext,iconPack,myRealm,true);
        }


        return imageView;
    }
}
