package org.de_studio.recentappswitcher;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Set;

/**
 * Created by hai on 1/5/2016.
 */
public class AppsListArrayAdapter extends BaseAdapter {
    private static final String LOG_TAG = AppsListArrayAdapter.class.getSimpleName();
    private Context context;
    private Set<PackageInfo> infos;
    private PackageInfo[] packArray;
    private PackageManager packageManager;
    public AppsListArrayAdapter(Context context, Set<PackageInfo> set){
        super();
        this.context = context;
        this.infos = set;
        this.packArray = set.toArray(new  PackageInfo[set.size()]);
        packageManager = context.getPackageManager();
    }

    @Override
    public int getCount() {
        return packArray.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PackageInfo packageInfo = packArray[position];
        View returnView = LayoutInflater.from(context).inflate(R.layout.add_favorite_app_fragment_item_of_list_view,parent,false);
        ImageView icon = (ImageView) returnView.findViewById(R.id.add_favorite_list_item_image_view);
        TextView label = (TextView) returnView.findViewById(R.id.add_favorite_list_item_label_text_view);
        CheckBox checkBox = (CheckBox) returnView.findViewById(R.id.add_favorite_list_item_check_box);
        try {
            icon.setImageDrawable(packageManager.getApplicationIcon(packageInfo.packageName));
        }catch (PackageManager.NameNotFoundException e){
            Log.e(LOG_TAG, "NameNotFound " + e);
        }
        label.setText(packageManager.getApplicationLabel(packageInfo.applicationInfo));

        return returnView;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return packArray[position];
    }
}
