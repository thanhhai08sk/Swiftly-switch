package org.de_studio.recentappswitcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.de_studio.recentappswitcher.service.EdgeSettingDialogFragment;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by hai on 3/3/2016.
 */
public class IconPackListAdapter extends BaseAdapter {

    private Context mContext;
    private HashMap<String,IconPackManager.IconPack> mHashMap;
    private SharedPreferences sharedPreferences;
    private String[] packageName;

    IconPackListAdapter(Context context, HashMap<String, IconPackManager.IconPack> hashMap) {
        super();
        mContext = context;
        mHashMap = hashMap;
        sharedPreferences = mContext.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        Set<String> set = mHashMap.keySet();
        packageName = new String[set.size()];
        set.toArray(packageName);

    }

    @Override
    public int getCount() {
        return mHashMap.size()+1;
    }

    @Override
    public Object getItem(int position) {
        return mHashMap;
    }

    @Override
    public long getItemId(int position) {
        if (position == 0) {
            return 0;
        }else return 1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_icon_pack, parent, false);
        }
        ImageView icon = (ImageView) view.findViewById(R.id.icon_pack_icon_image_view);
        final TextView label = (TextView) view.findViewById(R.id.icon_pack_label_text_view);
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.icon_pack_radio_button);
        if (getItemId(position) == 0) {
            label.setText(mContext.getString(R.string.icon_pack_system_icon_pack_label));
            icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_launcher));
            if (sharedPreferences.getString(EdgeSettingDialogFragment.ICON_PACK_PACKAGE_NAME_KEY, "none").equals("none")) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
        } else {
            PackageManager packageManager = mContext.getPackageManager();
            String iconPackPackageName = packageName[position - 1];
            if (iconPackPackageName.equals(sharedPreferences.getString(EdgeSettingDialogFragment.ICON_PACK_PACKAGE_NAME_KEY, "none"))) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
            try {
                icon.setImageDrawable(packageManager.getApplicationIcon(iconPackPackageName));
                label.setText(packageManager.getApplicationLabel(packageManager.getApplicationInfo(iconPackPackageName, 0)));
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("IconPackListAdapter", "name not found " + e);
            }


        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItemId(position) == 0) {
                    sharedPreferences.edit().putString(EdgeSettingDialogFragment.ICON_PACK_PACKAGE_NAME_KEY, "none").commit();
                    IconPackListAdapter.this.notifyDataSetChanged();
                } else {
                    sharedPreferences.edit().putString(EdgeSettingDialogFragment.ICON_PACK_PACKAGE_NAME_KEY, packageName[position - 1]).commit();
                    IconPackListAdapter.this.notifyDataSetChanged();
                }
            }
        });


        return view;
    }
}
