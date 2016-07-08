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

/**
 * Created by HaiNguyen on 7/1/16.
 */
public class PinRecentAddAppAdapter extends BaseAdapter {

    private static final String TAG = PinRecentAddAppAdapter.class.getSimpleName();
    private Context context;
    private Realm myRealm;
    private PackageManager packageManager;
    static private ArrayList<AppInfors> mAppInfosArrayList;
    public PinRecentAddAppAdapter(Context context, ArrayList<AppInfors> inforsArrayList, Realm realm) {
        super();
        myRealm = realm;
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
            returnView = LayoutInflater.from(context).inflate(R.layout.item_dialog_favorite_app, parent, false);
        }
        ImageView icon = (ImageView) returnView.findViewById(R.id.add_favorite_list_item_image_view);
        TextView label = (TextView) returnView.findViewById(R.id.add_favorite_list_item_label_text_view);
        CheckBox checkBox = (CheckBox) returnView.findViewById(R.id.add_favorite_list_item_check_box);
        checkBox.setChecked(myRealm.where(Shortcut.class).equalTo("packageName",appInfors.packageName).findFirst()!=null);
        try {
            icon.setImageDrawable(packageManager.getApplicationIcon(appInfors.packageName));

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "name not found");
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