package org.de_studio.recentappswitcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.service.EdgeSetting;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by hai on 1/5/2016.
 */
public class AppsListArrayAdapter extends BaseAdapter {
    private static final String LOG_TAG = AppsListArrayAdapter.class.getSimpleName();
    private Context context;
    private PackageManager packageManager;
    static private ArrayList<AppInfors> mAppInfosArrayList;
    static int mMode;
    private static SharedPreferences sharedPreferenceExclude;

    public AppsListArrayAdapter(Context context, ArrayList<AppInfors> inforsArrayList, int mode) {
        super();
        this.context = context;
        mMode = mode;
        mAppInfosArrayList = inforsArrayList;
        packageManager = context.getPackageManager();
        sharedPreferenceExclude = context.getSharedPreferences(MainActivity.EXCLUDE_SHAREDPREFERENCE, 0);
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
        Set<String> excludeSet = sharedPreferenceExclude.getStringSet(EdgeSetting.EXCLUDE_KEY, null);
        if (excludeSet!=null && excludeSet.contains(appInfors.packageName)) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

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
