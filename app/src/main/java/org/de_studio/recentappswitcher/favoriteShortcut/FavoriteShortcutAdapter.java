package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.service.EdgeSettingDialogFragment;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by hai on 2/14/2016.
 */
public class FavoriteShortcutAdapter extends BaseAdapter {
    private static final String LOG_TAG = FavoriteShortcutAdapter.class.getSimpleName();
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private IconPackManager.IconPack iconPack;
    private int iconPadding;
    private float mIconScale;
    private int dragPosition;
    private Realm myRealm;

    public FavoriteShortcutAdapter(Context context) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        mIconScale = sharedPreferences.getFloat(EdgeSettingDialogFragment.ICON_SCALE, 1f);
        String iconPackPacka = sharedPreferences.getString(EdgeSettingDialogFragment.ICON_PACK_PACKAGE_NAME_KEY, "none");
        if (!iconPackPacka.equals("none")) {
            IconPackManager iconPackManager = new IconPackManager();
            iconPackManager.setContext(mContext);
            iconPack = iconPackManager.getInstance(iconPackPacka);
        }
        iconPadding = (int)mContext.getResources().getDimension(R.dimen.icon_padding);
        myRealm = Realm.getInstance(mContext);
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
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id",position).findFirst();
        if (shortcut != null && shortcut.getType() == Shortcut.TYPE_FOLDER) {
            return 1;
        }
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams((int) (mContext.getResources().getDimension(R.dimen.icon_size) * mIconScale + mContext.getResources().getDimension(R.dimen.icon_padding_x_2)),
                    (int) (mContext.getResources().getDimension(R.dimen.icon_size) * mIconScale + mContext.getResources().getDimension(R.dimen.icon_padding_x_2))));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(iconPadding,iconPadding,iconPadding,iconPadding);
        } else {
            imageView = (ImageView) convertView;
        }
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id",position).findFirst();
        if (shortcut == null) {
            imageView.setImageResource(R.drawable.ic_add_circle_outline_white_48dp);
            imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
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
                    Log.e(LOG_TAG, "NameNotFound " + e);
                }
            }else if (shortcut.getType() == Shortcut.TYPE_ACTION) {
                switch (shortcut.getAction()) {
                    case Shortcut.ACTION_WIFI:
                        imageView.setImageResource(R.drawable.ic_action_wifi_on);
                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color. black));
                        break;
                    case Shortcut.ACTION_BLUETOOTH:
                        imageView.setImageResource(R.drawable.ic_action_bluetooth_on);
                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_ROTATION:
                        imageView.setImageResource(R.drawable.ic_action_rotate_on);
                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_POWER_MENU:
                        imageView.setImageResource(R.drawable.ic_action_power_menu);
                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_HOME:
                        imageView.setImageResource(R.drawable.ic_icon_home);
                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_BACK:
                        imageView.setImageResource(R.drawable.ic_icon_back);
                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_NOTI:
                        imageView.setImageResource(R.drawable.ic_icon_noti);
                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_LAST_APP:
                        imageView.setImageResource(R.drawable.ic_icon_last_app);
                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_CALL_LOGS:
                        imageView.setImageResource(R.drawable.ic_icon_call_log);
                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_DIAL:
                        imageView.setImageResource(R.drawable.ic_icon_dial);
                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_CONTACT:
                        imageView.setImageResource(R.drawable.ic_icon_contact);
                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_RECENT:
                        imageView.setImageResource(R.drawable.ic_action_recent2);
                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_NONE:
                        imageView.setImageDrawable(null);
                }
            } else if (shortcut.getType() == Shortcut.TYPE_CONTACT) {
                String thumbnaiUri = shortcut.getThumbnaiUri();
                if (thumbnaiUri != null) {
                    Uri uri = Uri.parse(thumbnaiUri);
                    imageView.setImageURI(uri);
                } else {
                    imageView.setImageResource(R.drawable.ic_icon_home);
                    imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                }
            } else if (shortcut.getType() == Shortcut.TYPE_FOLDER) {
//                Log.e(LOG_TAG, "begin read bimap = " + System.currentTimeMillis());
                File myDir = mContext.getFilesDir();
                String fname = "folder-"+ position +".png";
                File file = new File (myDir, fname);
                if (!file.exists()) {
//                    Log.e(LOG_TAG, "create bitmap");
                    imageView.setImageBitmap(Utility.getFolderThumbnail(myRealm, position, mContext));
                } else {
//                    Log.e(LOG_TAG, "read bitmap");
                    try {
                        imageView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "read thumbnail exeption" + e);
                        e.printStackTrace();
                    }
                }
                imageView.setColorFilter(null);
//                Log.e(LOG_TAG, "finish read bimap = " + System.currentTimeMillis());

            }

        }
        imageView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.setBackgroundResource(R.color.grey);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        v.setBackground(null);
                        break;
                    case DragEvent.ACTION_DROP:
                        changePosition(dragPosition,position);
                        View view = (View) event.getLocalState();
                        view.setVisibility(View.VISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setBackground(null);
                        notifyDataSetChanged();

                    default:
                        break;
                }
                return true;
            }
        });
        imageView.setVisibility(View.VISIBLE);
        return imageView;
    }

    public void setDragPosition(int position) {
        dragPosition = position;
    }

    public void changePosition(int dragPosition, int dropPosition) {
        Realm myRealm = Realm.getInstance(mContext);
        Shortcut dropTemp = myRealm.where(Shortcut.class).equalTo("id", dropPosition).findFirst();
        Shortcut dragTemp = myRealm.where(Shortcut.class).equalTo("id", dragPosition).findFirst();
        Shortcut shortcut5000 = myRealm.where(Shortcut.class).equalTo("id",500).findFirst();
        Shortcut shortcut1500 = myRealm.where(Shortcut.class).equalTo("id", 1500).findFirst();
        myRealm.beginTransaction();
        if (shortcut5000 != null) {
            shortcut5000.removeFromRealm();
        }
        if (shortcut1500 != null) {
            shortcut1500.removeFromRealm();
        }

        try {
            dropTemp.setId(500);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            dragTemp.setId(dropPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            dropTemp.setId(dragPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dragTemp != null && dragTemp.getType() == Shortcut.TYPE_FOLDER) {
            int size =(int ) myRealm.where(Shortcut.class).greaterThan("id", (dragPosition + 1) * 1000 - 1).lessThan("id", (dragPosition + 2) * 1000).count();
            RealmResults realmResults = myRealm.where(Shortcut.class).greaterThan("id",1499).lessThan("id", 2000).findAll();
            realmResults.clear();
            Shortcut shortcut;
            for (int i = 0; i < size; i++) {
                shortcut = myRealm.where(Shortcut.class).equalTo("id",(1+ dragPosition)*1000 + i).findFirst();
                if (shortcut != null) {
                    shortcut.setId(1500+ i);
                }
            }

        }
        if (dropTemp != null && dropTemp.getType() == Shortcut.TYPE_FOLDER) {
            int size =(int ) myRealm.where(Shortcut.class).greaterThan("id", (dropPosition + 1) * 1000 - 1).lessThan("id", (dropPosition + 2) * 1000).count();
            Shortcut shortcut;
            for (int i = 0; i < size; i++) {
                shortcut = myRealm.where(Shortcut.class).equalTo("id",(1+ dropPosition)*1000 + i).findFirst();
                if (shortcut != null) {
                    shortcut.setId((dragPosition + 1) * 1000 + i);
                }
            }
            Utility.getFolderThumbnail(myRealm, dragPosition, mContext);
        }
        if (dragTemp != null && dragTemp.getType() == Shortcut.TYPE_FOLDER) {
            int size =(int ) myRealm.where(Shortcut.class).greaterThan("id", 1499).lessThan("id", 2000).count();
            Shortcut shortcut;
            for (int i = 0; i < size; i++) {
                shortcut = myRealm.where(Shortcut.class).equalTo("id",1500 + i).findFirst();
                if (shortcut != null) {
                    shortcut.setId((dropPosition + 1) * 1000 + i);
                }
            }
            Utility.getFolderThumbnail(myRealm, dropPosition, mContext);
        }

        myRealm.commitTransaction();
        notifyDataSetChanged();
    }
    public void removeDragItem() {
        Realm myRealm = Realm.getInstance(mContext);
        myRealm.beginTransaction();
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id", dragPosition).findFirst();
        if (shortcut != null) {
            if (shortcut.getType() == Shortcut.TYPE_FOLDER) {
                int startId = (shortcut.getId() +1)*1000;
                myRealm.where(Shortcut.class).greaterThan("id",startId -1).lessThan("id",startId + 1000).findAll().clear();
            }
            shortcut.setType(Shortcut.TYPE_ACTION);
            shortcut.setAction(Shortcut.ACTION_NONE);
        } else {
            Shortcut shortcut1 = new Shortcut();
            shortcut1.setType(Shortcut.TYPE_ACTION);
            shortcut1.setAction(Shortcut.ACTION_NONE);
            shortcut1.setId(dragPosition);
            myRealm.copyToRealm(shortcut1);

        }

        myRealm.commitTransaction();
        notifyDataSetChanged();
    }

}
