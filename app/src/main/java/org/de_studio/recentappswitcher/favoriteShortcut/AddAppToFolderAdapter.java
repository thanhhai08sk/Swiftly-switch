package org.de_studio.recentappswitcher.favoriteShortcut;

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

import org.de_studio.recentappswitcher.AppInfors;
import org.de_studio.recentappswitcher.R;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 5/31/16.
 */
public class AddAppToFolderAdapter extends BaseAdapter {

    private static final String LOG_TAG = AddAppToFolderAdapter.class.getSimpleName();
    private Context context;
    private Realm myRealm;
    private PackageManager packageManager;
    private int mPosition;
    static private ArrayList<AppInfors> mAppInfosArrayList;
    public AddAppToFolderAdapter(Context context, ArrayList<AppInfors> inforsArrayList, Realm realm, int position) {
        super();
        myRealm = realm;
        this.context = context;
        mAppInfosArrayList = inforsArrayList;
        packageManager = context.getPackageManager();
        mPosition = position;
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
        int startId = (mPosition +1)* 1000;
        checkBox.setChecked(myRealm.where(Shortcut.class).greaterThan("id", startId -1).lessThan("id",startId+1000) .equalTo("packageName",appInfors.packageName).findFirst()!=null);
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

    public void clear() {
        if (myRealm != null) {
            myRealm.close();
        }
    }
}
