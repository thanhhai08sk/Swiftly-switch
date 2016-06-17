package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.view.DragEvent;
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
import org.de_studio.recentappswitcher.service.EdgeGestureService;
import org.de_studio.recentappswitcher.service.EdgeSetting;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by hai on 2/14/2016.
 */
public class FavoriteShortcutAdapter extends BaseAdapter {
    private static final String TAG = FavoriteShortcutAdapter.class.getSimpleName();
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private IconPackManager.IconPack iconPack;
    private int iconPadding;
    private float mIconScale;
    private int dragPosition;
    private Realm myRealm;
    private PackageManager packageManager;

    public FavoriteShortcutAdapter(Context context) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        mIconScale = sharedPreferences.getFloat(EdgeSetting.ICON_SCALE, 1f);
        String iconPackPacka = sharedPreferences.getString(EdgeSetting.ICON_PACK_PACKAGE_NAME_KEY, "none");
        packageManager = context.getPackageManager();
        if (!iconPackPacka.equals("none")) {
            IconPackManager iconPackManager = new IconPackManager();
            iconPackManager.setContext(mContext);
            iconPack = iconPackManager.getInstance(iconPackPacka);
        }
        iconPadding = (int)mContext.getResources().getDimension(R.dimen.icon_padding);
        myRealm = Realm.getInstance(new RealmConfiguration.Builder(mContext)
                .name("default.realm")
                .schemaVersion(EdgeGestureService.CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
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
            Utility.setImageForShortcut(shortcut,packageManager,imageView,mContext,iconPack,position,myRealm, false);
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
        Shortcut dropTemp = myRealm.where(Shortcut.class).equalTo("id", dropPosition).findFirst();
        Shortcut dragTemp = myRealm.where(Shortcut.class).equalTo("id", dragPosition).findFirst();
        Shortcut shortcut5000 = myRealm.where(Shortcut.class).equalTo("id",500).findFirst();
        Shortcut shortcut1500 = myRealm.where(Shortcut.class).equalTo("id", 1500).findFirst();
        myRealm.beginTransaction();
        if (shortcut5000 != null) {
            shortcut5000.deleteFromRealm();
        }
        if (shortcut1500 != null) {
            shortcut1500.deleteFromRealm();
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
            realmResults.deleteAllFromRealm();
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
        myRealm.beginTransaction();
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id", dragPosition).findFirst();
        if (shortcut != null) {
            if (shortcut.getType() == Shortcut.TYPE_FOLDER) {
                int startId = (shortcut.getId() +1)*1000;
                myRealm.where(Shortcut.class).greaterThan("id",startId -1).lessThan("id",startId + 1000).findAll().deleteAllFromRealm();
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
