package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.de_studio.recentappswitcher.R;

import io.realm.Realm;

/**
 * Created by hai on 2/14/2016.
 */
public class FavoriteShortcutAdapter extends BaseAdapter {
    private Context mContext;

    public FavoriteShortcutAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 16;
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
            imageView.setLayoutParams(new GridView.LayoutParams(R.dimen.icon_size, R.dimen.icon_size));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        Realm myRealm = Realm.getInstance(mContext);
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id",position).findFirst();
        if (shortcut == null) {
            imageView.setImageResource(R.drawable.ic_add_circle_outline_white_48dp);
        } else {
            imageView.setImageDrawable(shortcut.getDrawable());
        }

        return null;
    }
}
