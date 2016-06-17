package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.MyRealmMigration;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.service.EdgeGestureService;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by HaiNguyen on 6/16/16.
 */
public class ShortcutListAdapter extends BaseAdapter {
    private static final String TAG = ShortcutListAdapter.class.getSimpleName();
    private Context context;
    private List<ResolveInfo> resolveInfos;
    private int mode;
    private AppListAdapter.AppChangeListener listener;
    private Realm myRealm;
    private PackageManager packageManager;


    public ShortcutListAdapter(Context context, int mode, List<ResolveInfo> arrayList) {
        super();
        this.context = context;
        this.resolveInfos = arrayList;
        this.mode = mode;
        packageManager = context.getPackageManager();
        if (mode == FavoriteSettingActivity.MODE_GRID) {
            myRealm = Realm.getInstance(new RealmConfiguration.Builder(context)
                    .name("default.realm")
                    .schemaVersion(EdgeGestureService.CURRENT_SCHEMA_VERSION)
                    .migration(new MyRealmMigration())
                    .build());
        } else {
            myRealm = Realm.getInstance(new RealmConfiguration.Builder(context)
                    .name("circleFavo.realm")
                    .schemaVersion(EdgeGestureService.CURRENT_SCHEMA_VERSION)
                    .migration(new MyRealmMigration())
                    .build());
        }
    }


    @Override
    public int getCount() {
        return resolveInfos.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_choose_shortcut_shortcut_list, parent, false);
        }
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView label = (TextView) view.findViewById(R.id.label);
        icon.setImageDrawable(resolveInfos.get(position).loadIcon(packageManager));
        label.setText(resolveInfos.get(position).loadLabel(packageManager));
        return view;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    public interface ShortcutChangeListener {
        void onSettingChange();
    }

    public void registerListener(AppListAdapter.AppChangeListener listener) {
        this.listener = listener;
    }

    public AppListAdapter.AppChangeListener getListener() {
        return listener;
    }
}
