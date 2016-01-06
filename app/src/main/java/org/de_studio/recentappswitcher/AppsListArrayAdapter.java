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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.de_studio.recentappswitcher.service.EdgeSettingDialogFragment;

import java.util.ArrayList;
import java.util.HashSet;
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
    private static SharedPreferences sharedPreferenceFavorite, sharedPreferenceExclude;
    public AppsListArrayAdapter(Context context, ArrayList<AppInfors> inforsArrayList, int mode){
        super();
        this.context = context;
        mMode = mode;
        mAppInfosArrayList = inforsArrayList;
        packageManager = context.getPackageManager();
        sharedPreferenceFavorite = context.getSharedPreferences(MainActivity.FAVORITE_SHAREDPREFERENCE, 0);
        sharedPreferenceExclude = context.getSharedPreferences(MainActivity.EXCLUDE_SHAREDPREFERENCE,0);
    }

    @Override
    public int getCount() {
        return mAppInfosArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AppInfors appInfors = mAppInfosArrayList.get(position);
        View returnView = LayoutInflater.from(context).inflate(R.layout.dialog_favorite_app_item,parent,false);
        ImageView icon = (ImageView) returnView.findViewById(R.id.add_favorite_list_item_image_view);
        TextView label = (TextView) returnView.findViewById(R.id.add_favorite_list_item_label_text_view);
        CheckBox checkBox = (CheckBox) returnView.findViewById(R.id.add_favorite_list_item_check_box);
        if (mMode == FavoriteOrExcludeDialogFragment.EXCLUDE_MODE){
            if (sharedPreferenceExclude.getStringSet(EdgeSettingDialogFragment.EXCLUDE_KEY,new HashSet<String>()).contains(appInfors.packageName)){
                checkBox.setChecked(true);
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String packageName = appInfors.packageName;
                    Set<String> set = sharedPreferenceExclude.getStringSet(EdgeSettingDialogFragment.EXCLUDE_KEY,null);
                    if (set == null){
                        set = new HashSet<String>();
                    }
                    if (isChecked & packageName !=null){
                            set.add(packageName);
                            sharedPreferenceExclude.edit().putStringSet(EdgeSettingDialogFragment.EXCLUDE_KEY,set).commit();



                    }else if (!isChecked & packageName!= null){
                        if (set.contains(packageName)){
                            set.remove(packageName);
                            sharedPreferenceExclude.edit().putStringSet(EdgeSettingDialogFragment.EXCLUDE_KEY,set).commit();
                        }
                    }
                    Log.e(LOG_TAG,"size of set = " + set.size());

                }
            });
        }else if (mMode == FavoriteOrExcludeDialogFragment.FAVORITE_MODE){
            if (sharedPreferenceFavorite.getStringSet(EdgeSettingDialogFragment.FAVORITE_KEY,new HashSet<String>()).contains(appInfors.packageName)){
                checkBox.setChecked(true);
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String packageName = appInfors.packageName;
                    Set<String> set = sharedPreferenceFavorite.getStringSet(EdgeSettingDialogFragment.FAVORITE_KEY,null);
                    if (set == null){
                        set = new HashSet<String>();
                    }
                    if (isChecked & packageName !=null){
                        if (set.size()==6){
                            Toast.makeText(context,context.getString(R.string.limit_for_favorite_app_is_6),Toast.LENGTH_SHORT).show();
                            buttonView.setChecked(false);
                        }else {
                            set.add(packageName);
                            sharedPreferenceFavorite.edit().putStringSet(EdgeSettingDialogFragment.FAVORITE_KEY,set).commit();
                        }


                    }else if (!isChecked & packageName!= null){
                        if (set.contains(packageName)){
                            set.remove(packageName);
                            sharedPreferenceFavorite.edit().putStringSet(EdgeSettingDialogFragment.FAVORITE_KEY,set).commit();
                        }
                    }
                    Log.e(LOG_TAG,"size of set = " + set.size());

                }
            });
        }

        icon.setImageDrawable(appInfors.iconDrawable);
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
