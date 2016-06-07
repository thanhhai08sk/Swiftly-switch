package org.de_studio.recentappswitcher.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;

import java.io.IOException;

import io.realm.Realm;

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

    public FolderAdapter(Context context, int mPosition) {
        mContext = context;
        this.mPosition = mPosition;
        myRealm = Realm.getDefaultInstance();
        folderShortcut = myRealm.where(Shortcut.class).equalTo("id", mPosition).findFirst();
        sharedPreferences = context.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
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
    @Override
    public int getCount() {
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
            if (shortcut.getType() == Shortcut.TYPE_APP) {
                imageView.setColorFilter(null);
                try {
                    Drawable defaultDrawable = mContext.getPackageManager().getApplicationIcon(shortcut.getPackageName());
                    if (iconPack!=null) {
                        imageView.setImageDrawable(iconPack.getDrawableIconForPackage(shortcut.getPackageName(), defaultDrawable));
                    } else {
                        imageView.setImageDrawable(defaultDrawable);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "NameNotFound " + e);
                }
            }else if (shortcut.getType() == Shortcut.TYPE_ACTION) {
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
                    case Shortcut.ACTION_HOME:
                        imageView.setImageResource(R.drawable.ic_icon_home);
                        break;
                    case Shortcut.ACTION_BACK:
                        imageView.setImageResource(R.drawable.ic_icon_back);
                        break;
                    case Shortcut.ACTION_NOTI:
                        imageView.setImageResource(R.drawable.ic_icon_noti);
                        break;
                    case Shortcut.ACTION_LAST_APP:
                        imageView.setImageResource(R.drawable.ic_icon_last_app);
                        break;
                    case Shortcut.ACTION_CALL_LOGS:
                        imageView.setImageResource(R.drawable.ic_icon_call_log);
                        break;
                    case Shortcut.ACTION_DIAL:
                        imageView.setImageResource(R.drawable.ic_icon_dial);
                        break;
                    case Shortcut.ACTION_CONTACT:
                        imageView.setImageResource(R.drawable.ic_icon_contact);
                        break;
                    case Shortcut.ACTION_RECENT:
                        imageView.setImageResource(R.drawable.ic_action_recent2);
                        break;
                    case Shortcut.ACTION_NONE:
                        imageView.setImageDrawable(null);
                }
            } else if (shortcut.getType() == Shortcut.TYPE_CONTACT) {
                String thumbnaiUri = shortcut.getThumbnaiUri();
                if (thumbnaiUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(thumbnaiUri));
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), bitmap);
                        drawable.setCircular(true);
                        imageView.setImageDrawable(drawable);
                    } catch (IOException e) {
                        e.printStackTrace();
                        imageView.setImageResource(R.drawable.ic_icon_home);
                    }
                } else {
                    imageView.setImageResource(R.drawable.ic_icon_home);
                }


            }

        }


        return imageView;
    }
}
