package org.de_studio.recentappswitcher.base.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by HaiNguyen on 11/18/16.
 */

public class ItemsListAdapter extends RealmBaseAdapter<Item> {
    private static final String TAG = ItemsListAdapter.class.getSimpleName();
    PackageManager packageManager;
    IconPackManager.IconPack iconPack;
    int itemRes;
    Item currentItem;

    public ItemsListAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Item> data, PackageManager packageManager, IconPackManager.IconPack iconPack, int itemRes) {
        super(context, data);
        this.packageManager = packageManager;
        this.iconPack = iconPack;
        this.itemRes = itemRes;
    }

    public void setCurrentItem(Item item) {
        currentItem = item;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(itemRes, parent, false);
        }
        Item item = getItem(position);
        ImageView icon;
        TextView label;
        RadioButton radioButton = null;
        CheckBox checkBox = null;
        if (item != null) {
            switch (itemRes) {
                case R.layout.item_items_list_radio_button:
                    radioButton = ((RadioButton) view.findViewById(R.id.radio_button));
                    break;
                case R.layout.item_items_list_check_box:
                    checkBox = (CheckBox) view.findViewById(R.id.check_box);
                    break;
            }
            icon = ((ImageView) view.findViewById(R.id.icon));
            label = ((TextView) view.findViewById(R.id.label));

            Utility.setItemIcon(item, context, icon, packageManager, iconPack);
            label.setText(item.label);
            if (currentItem != null) {
                if (radioButton != null) {
                    radioButton.setChecked(currentItem.equals(item));
                }

                if (checkBox != null) {
                    checkBox.setChecked(currentItem.equals(item));
                }
            }
        }
        return view;
    }
}
