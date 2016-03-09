package org.de_studio.recentappswitcher.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;

import io.realm.Realm;

/**
 * Created by hai on 2/25/2016.
 */
public class FavoriteShortcutAdapter extends BaseAdapter {
    private static final String LOG_TAG = FavoriteShortcutAdapter.class.getSimpleName();
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private IconPackManager.IconPack iconPack;
    private int mBackgroundAt = -1;

    public FavoriteShortcutAdapter(Context context) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        String iconPackPacka = sharedPreferences.getString(EdgeSettingDialogFragment.ICON_PACK_PACKAGE_NAME_KEY, "com.colechamberlin.stickers");
        if (!iconPackPacka.equals("none")) {
            IconPackManager iconPackManager = new IconPackManager();
            iconPackManager.setContext(mContext);
            iconPack = iconPackManager.getInstance(iconPackPacka);
        }
    }

    @Override
    public int getCount() {
        return Utility.getSizeOfFavoriteGrid(mContext);
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
        FavoriteShortcutAdapter.this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            int padding =(int) mContext.getResources().getDimension(R.dimen.icon_padding);
            imageView.setPadding(padding,padding,padding,padding);
            imageView.setLayoutParams(new GridView.LayoutParams((int) mContext.getResources().getDimension(R.dimen.icon_size), (int) mContext.getResources().getDimension(R.dimen.icon_size)));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView = (ImageView) convertView;
        }
        Realm myRealm = Realm.getInstance(mContext);
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id",position).findFirst();
        if (shortcut == null) {
            imageView.setImageResource(R.drawable.ic_add_circle_outline_white_48dp);
        } else {
            if (shortcut.getType() == Shortcut.TYPE_APP) {
                try {
                    Drawable defaultDrawable = mContext.getPackageManager().getApplicationIcon(shortcut.getPackageName());
                    if (iconPack!=null) {

                            imageView.setImageDrawable(iconPack.getDrawableIconForPackage(shortcut.getPackageName(), defaultDrawable));
                    } else {
                        imageView.setImageDrawable(defaultDrawable);

                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(LOG_TAG, "NameNotFound " + e);
                }
            }else if (shortcut.getType() == Shortcut.TYPE_SETTING) {
                switch (shortcut.getAction()) {
                    case Shortcut.ACTION_WIFI:
                        if (Utility.getWifiState(mContext)) {
                            imageView.setImageResource(R.drawable.ic_action_wifi_on);
                        } else {
                            imageView.setImageResource(R.drawable.ic_action_wifi_off);
                        }

                        break;
                    case Shortcut.ACTION_BLUETOOTH:
                        if (Utility.getBluetoothState(mContext)) {
                            imageView.setImageResource(R.drawable.ic_action_bluetooth_on);
                        } else {
                            imageView.setImageResource(R.drawable.ic_action_bluetooth_off);
                        }

                        break;
                    case Shortcut.ACTION_ROTATION:
                        if (Utility.getIsRotationAuto(mContext)) {
                            imageView.setImageResource(R.drawable.ic_action_rotate_on);
                        } else {
                            imageView.setImageResource(R.drawable.ic_action_rotate_lock);
                        }

                        break;
                    case Shortcut.ACTION_POWER_MENU:
                        imageView.setImageResource(R.drawable.ic_action_power_menu);
                        break;
                }
            }

        }
        if (position == mBackgroundAt) {
            imageView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.icon_background_square));
        }else imageView.setBackground(null);

        return imageView;
    }
}
