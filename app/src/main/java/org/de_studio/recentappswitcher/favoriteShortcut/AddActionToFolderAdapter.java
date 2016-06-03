package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 6/3/16.
 */
public class AddActionToFolderAdapter extends BaseAdapter {
    private static final String LOG_TAG = AddActionToFolderAdapter.class.getSimpleName();
    private Context mContext;
    private int mPosition;
    private String[] stringArray;
    private Realm myRealm;
    public AddActionToFolderAdapter(Context mContext, Realm realm, int position) {
        super();
        this.mContext = mContext;
        myRealm = realm;
        mPosition = position;
        stringArray = mContext.getResources().getStringArray(R.array.setting_shortcut_array);
    }

    @Override
    public int getCount() {
        return stringArray.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View returnView = convertView;
        if (returnView == null) {
            returnView = LayoutInflater.from(mContext).inflate(R.layout.item_dialog_favorite_app, parent, false);
        }
        ImageView icon = (ImageView) returnView.findViewById(R.id.add_favorite_list_item_image_view);
        TextView label = (TextView) returnView.findViewById(R.id.add_favorite_list_item_label_text_view);
        CheckBox checkBox = (CheckBox) returnView.findViewById(R.id.add_favorite_list_item_check_box);
        final String item = stringArray[position];
        label.setText(item);
        int startId = (mPosition +1)* 1000;
        int action = Utility.getActionFromLabel(mContext, item);
        checkBox.setChecked(myRealm.where(Shortcut.class).greaterThan("id", startId -1).lessThan("id",startId+1000) .equalTo("action",action).findFirst()!=null);


        if (item.equalsIgnoreCase(mContext.getResources().getString(R.string.setting_shortcut_wifi))) {
            icon.setImageResource(R.drawable.ic_action_wifi_on);
        }else if (item.equalsIgnoreCase(mContext.getResources().getString(R.string.setting_shortcut_bluetooth))) {
            icon.setImageResource(R.drawable.ic_action_bluetooth_on);
        }else if (item.equalsIgnoreCase(mContext.getResources().getString(R.string.setting_shortcut_rotation))) {
            icon.setImageResource(R.drawable.ic_action_rotate_on);
        }else if (item.equalsIgnoreCase(mContext.getResources().getString(R.string.setting_shortcut_power_menu))) {
            icon.setImageResource(R.drawable.ic_action_power_menu);
        }else if (item.equalsIgnoreCase(mContext.getResources().getString(R.string.setting_shortcut_home))) {
            icon.setImageResource(R.drawable.ic_icon_home);
        }else if (item.equalsIgnoreCase(mContext.getResources().getString(R.string.setting_shortcut_back))) {
            icon.setImageResource(R.drawable.ic_icon_back);
        }else if (item.equalsIgnoreCase(mContext.getResources().getString(R.string.setting_shortcut_recent))) {
            icon.setImageResource(R.drawable.ic_action_recent2);
        }else if (item.equalsIgnoreCase(mContext.getResources().getString(R.string.setting_shortcut_noti))) {
            icon.setImageResource(R.drawable.ic_icon_noti);
        }else if (item.equalsIgnoreCase(mContext.getResources().getString(R.string.setting_shortcut_dial))) {
            icon.setImageResource(R.drawable.ic_icon_dial);
        }else if (item.equalsIgnoreCase(mContext.getResources().getString(R.string.setting_shortcut_call_log))) {
            icon.setImageResource(R.drawable.ic_icon_call_log);
        }else if (item.equalsIgnoreCase(mContext.getResources().getString(R.string.setting_shortcut_contact))) {
            icon.setImageResource(R.drawable.ic_icon_contact);
        }else if (item.equalsIgnoreCase(mContext.getResources().getString(R.string.setting_shortcut_last_app))) {
            icon.setImageResource(R.drawable.ic_icon_last_app);
        }else if (item.equalsIgnoreCase(mContext.getResources().getString(R.string.setting_shortcut_none))) {
            icon.setImageDrawable(null);
        }else if (item.equalsIgnoreCase(mContext.getResources().getString(R.string.setting_shortcut_folder))) {
            icon.setImageResource(R.drawable.ic_icon_contact);
        }

        return returnView;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return stringArray[position];
    }
}
