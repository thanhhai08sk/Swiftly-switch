package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;

import io.realm.Realm;

/**
 * Created by hai on 2/14/2016.
 */
public class FavoriteShortcutAdapter extends BaseAdapter {
    private static final String LOG_TAG = FavoriteShortcutAdapter.class.getSimpleName();
    private Context mContext;

    public FavoriteShortcutAdapter(Context context) {
        mContext = context;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams((int) mContext.getResources().getDimension(R.dimen.icon_size), (int) mContext.getResources().getDimension(R.dimen.icon_size)));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
                    imageView.setImageDrawable(mContext.getPackageManager().getApplicationIcon(shortcut.getPackageName()));
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(LOG_TAG, "NameNotFound " + e);
                }
            }else if (shortcut.getType() == Shortcut.TYPE_SETTING) {
                switch (shortcut.getAction()) {
                    case Shortcut.ACTION_WIFI:
                        imageView.setImageResource(R.drawable.ic_action_wifi_on);
                        break;
                    case Shortcut.ACTION_BLUETOOTH:
                        imageView.setImageResource(R.drawable.ic_action_bluetooth_on);
                        break;
                    case Shortcut.ACTION_ROTATION:
                        imageView.setImageResource(R.drawable.ic_action_rotate_on);
                        break;
                    case Shortcut.ACTION_POWER_MENU:
                        imageView.setImageResource(R.drawable.ic_action_power_menu);
                        break;
                }
            }

        }

        return imageView;
    }
}
