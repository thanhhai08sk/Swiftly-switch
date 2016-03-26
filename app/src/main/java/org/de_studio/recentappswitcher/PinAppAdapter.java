package org.de_studio.recentappswitcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortListView;

import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;
import org.de_studio.recentappswitcher.service.EdgeGestureService;
import org.de_studio.recentappswitcher.service.EdgeSettingDialogFragment;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by hai on 3/25/2016.
 */
public class PinAppAdapter extends BaseAdapter implements DragSortListView.DropListener {
    private Context mContext;
    private Realm pinRealm;
    private Shortcut shortcut;
    private Drawable defaultDrawable;
    private SharedPreferences sharedPreferences;
    private IconPackManager.IconPack iconPack;
    private PackageManager packageManager;
    private static final String LOG_TAG = PinAppAdapter.class.getSimpleName();
    public PinAppAdapter(Context context) {
        super();
        mContext = context;
        packageManager = context.getPackageManager();
        pinRealm = Realm.getInstance(new RealmConfiguration.Builder(mContext).name("pinApp.realm").build());
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
        return (int)pinRealm.where(Shortcut.class).count();
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
            view = LayoutInflater.from(mContext).inflate(R.layout.pin_app_list_view_item, parent, false);
        }
        ImageView icon = (ImageView) view.findViewById(R.id.pin_app_list_item_icon_image_view);
        TextView label = (TextView) view.findViewById(R.id.pin_app_list_item_label_text_view);
        shortcut = pinRealm.where(Shortcut.class).equalTo("id",position).findFirst();
        CharSequence title= "";
        try {
            title = packageManager.getApplicationLabel(packageManager.getApplicationInfo(shortcut.getPackageName(), 0));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, "NamenotFound");
        }

        if (shortcut != null) {
                try {
                    defaultDrawable = mContext.getPackageManager().getApplicationIcon(shortcut.getPackageName());
                    if (iconPack!=null) {

                        icon.setImageDrawable(iconPack.getDrawableIconForPackage(shortcut.getPackageName(), defaultDrawable));
                    } else {
                        icon.setImageDrawable(defaultDrawable);

                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(LOG_TAG, "NameNotFound " + e);
                }
            label.setText(title);
        }
//        view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Toast.makeText(mContext, "id = " + shortcut.getId(), Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "id = " + pinRealm.where(Shortcut.class).equalTo("id",position).findFirst().getId(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void drop(int from, int to) {
        RealmResults<Shortcut> results = pinRealm.where(Shortcut.class).findAll();
        String[] shortcuts = new String[results.size()];
        String tem;
        int i = 0;
        for (Shortcut shortcut : results) {
            shortcuts[i] = shortcut.getPackageName();
            i++;
        }
        if (from > to) {
            tem = shortcuts[from];
            for (int j = from; j > to; j--) {
                shortcuts[j] = shortcuts[j -1];
            }
            shortcuts[to]= tem;
        }else if (from < to) {
            tem = shortcuts[from];
            for (int k = from; k < to; k++) {
                shortcuts[k] = shortcuts[k + 1];
            }
            shortcuts[to] = tem;
        }
        pinRealm.beginTransaction();
        pinRealm.clear(Shortcut.class);
        int m = 0;
        for (String packageName : shortcuts) {
            Shortcut shortcut1 = new Shortcut();
            shortcut1.setPackageName(packageName);
            shortcut1.setId(m);
            m++;
            pinRealm.copyToRealm(shortcut1);
        }
        pinRealm.commitTransaction();
        notifyDataSetChanged();
        mContext.stopService(new Intent(mContext, EdgeGestureService.class));
        mContext.startService(new Intent(mContext, EdgeGestureService.class));
    }
}
