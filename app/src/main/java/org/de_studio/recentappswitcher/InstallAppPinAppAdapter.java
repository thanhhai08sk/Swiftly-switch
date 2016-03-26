package org.de_studio.recentappswitcher;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by hai on 3/26/2016.
 */
public class InstallAppPinAppAdapter extends BaseAdapter {
    private static final String LOG_TAG = AppsListArrayAdapter.class.getSimpleName();
    private Context context;
    private Realm pinRealm;
    private PackageManager packageManager;
    static private ArrayList<AppInfors> mAppInfosArrayList;
    public InstallAppPinAppAdapter(Context context, ArrayList<AppInfors> inforsArrayList) {
        super();
        pinRealm = Realm.getInstance(new RealmConfiguration.Builder(context).name("pinApp.realm").build());
        this.context = context;
        mAppInfosArrayList = inforsArrayList;
        packageManager = context.getPackageManager();
    }

    @Override
    public int getCount() {
        return mAppInfosArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AppInfors appInfors = mAppInfosArrayList.get(position);
        View returnView = convertView;
        if (returnView == null) {
            returnView = LayoutInflater.from(context).inflate(R.layout.dialog_favorite_app_item, parent, false);
        }
        ImageView icon = (ImageView) returnView.findViewById(R.id.add_favorite_list_item_image_view);
        TextView label = (TextView) returnView.findViewById(R.id.add_favorite_list_item_label_text_view);
        CheckBox checkBox = (CheckBox) returnView.findViewById(R.id.add_favorite_list_item_check_box);
        checkBox.setChecked(pinRealm.where(Shortcut.class).equalTo("packageName",appInfors.packageName).findFirst()!=null);
        try {
            icon.setImageDrawable(packageManager.getApplicationIcon(appInfors.packageName));

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, "name not found");
        }
        label.setText(appInfors.label);

        return returnView;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mAppInfosArrayList.get(position);
    }
}
