package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
import io.realm.Sort;

/**
 * Created by HaiNguyen on 5/31/16.
 */
public class FolderAdapter extends BaseAdapter {
    private static final String TAG = FolderAdapter.class.getSimpleName();
    private Context mContext;
    private int mPosition,dragPosition, startId;
    private Realm myRealm;
    private Shortcut folderShortcut;
    private IconPackManager.IconPack iconPack;
    private PackageManager packageManager;

    public FolderAdapter(Context context, int mPosition) {
        mContext = context;
        this.mPosition = mPosition;
        myRealm = Realm.getInstance(new RealmConfiguration.Builder()
                .name("default.realm")
                .schemaVersion(EdgeGestureService. CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        folderShortcut = myRealm.where(Shortcut.class).equalTo("id", mPosition).findFirst();
        packageManager = mContext.getPackageManager();
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        String iconPackPacka = sharedPreferences.getString(EdgeSetting.ICON_PACK_PACKAGE_NAME_KEY, "none");
        if (!iconPackPacka.equals("none")) {
            IconPackManager iconPackManager = new IconPackManager();
            iconPackManager.setContext(mContext);
            iconPack = iconPackManager.getInstance(iconPackPacka);
        }
        startId = (folderShortcut.getId()+1 ) * 1000;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_of_folder, parent, false);
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        TextView label = (TextView) view.findViewById(R.id.label);
        int id = (folderShortcut.getId()+1)*1000 + position;
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id", id).findFirst();
        if (shortcut == null) {
            imageView.setImageResource(R.drawable.ic_add_circle_outline_white_48dp);
            imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
        } else {
            if (shortcut.getType() == Shortcut.TYPE_CONTACT) {
                label.setText(shortcut.getName());
            } else {
                label.setText(shortcut.getLabel());
            }

            Utility.setImageForShortcut(shortcut, packageManager, imageView, mContext, iconPack, myRealm, false);
            }

        view.setOnDragListener(new View.OnDragListener() {
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

        view.setVisibility(View.VISIBLE);
        return view;
    }
    public void setDragPosition(int position) {
        this.dragPosition = position;
    }

    public void removeDragItem() {
        myRealm.beginTransaction();
        myRealm.where(Shortcut.class).equalTo("id",startId + dragPosition).findFirst().deleteFromRealm();

        RealmResults<Shortcut> results = myRealm.where(Shortcut.class).greaterThan("id", (folderShortcut.getId()+1 ) * 1000 -1)
                .lessThan("id", (folderShortcut.getId() + 2)* 1000).findAll().sort("id", Sort.ASCENDING);


        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).getId() >= startId + dragPosition) {
                Log.e(TAG, "when i = " + i + "result id = " + results.get(i).getId() +
                "\nDragPosition = " + dragPosition );
                Shortcut shortcut = results.get(i);
                int oldId = shortcut.getId();
                shortcut.setId(oldId - 1);
            }
        }
        myRealm.commitTransaction();
        Utility.getFolderThumbnail(myRealm, mPosition, mContext);
        notifyDataSetChanged();
    }

    public void changePosition(int dragPosition, int dropPosition) {
        Shortcut dropTemp = myRealm.where(Shortcut.class).equalTo("id",startId + dropPosition).findFirst();
        Shortcut dragTemp = myRealm.where(Shortcut.class).equalTo("id", startId + dragPosition).findFirst();
        Shortcut shortcut500 = myRealm.where(Shortcut.class).equalTo("id",500).findFirst();
        myRealm.beginTransaction();
        if (shortcut500 != null) {
            shortcut500.deleteFromRealm();
        }

        try {
            dropTemp.setId(500);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            dragTemp.setId(startId + dropPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            dropTemp.setId(startId + dragPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }

        myRealm.commitTransaction();
        Utility.getFolderThumbnail(myRealm, mPosition, mContext);
        notifyDataSetChanged();
    }

    public void clear() {
        if (myRealm != null) {
            myRealm.close();
        }
    }
}
