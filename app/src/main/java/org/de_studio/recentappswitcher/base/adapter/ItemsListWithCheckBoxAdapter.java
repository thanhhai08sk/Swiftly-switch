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
import android.widget.TextView;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;
import io.realm.RealmList;

/**
 * Created by HaiNguyen on 12/3/16.
 */

public class ItemsListWithCheckBoxAdapter extends RealmBaseAdapter<Item> {
    PackageManager packageManager;
    IconPackManager.IconPack iconPack;
    RealmList<Item> checkedItems;
    Context context;

    public ItemsListWithCheckBoxAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Item> data, PackageManager packageManager, IconPackManager.IconPack iconPack, RealmList<Item> checkedItems) {
        super(data);
        this.packageManager = packageManager;
        this.iconPack = iconPack;
        this.checkedItems = checkedItems;
        this.context = context;
    }


    public void setCheckedItems(RealmList<Item> checkedItems) {
        this.checkedItems = checkedItems;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_items_list_check_box, parent, false);
        }
        Item item = getItem(position);
        ImageView icon = ((ImageView) view.findViewById(R.id.icon));
        TextView label = ((TextView) view.findViewById(R.id.label));
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_box);
        if (item != null) {
            Utility.setItemIcon(item, context, icon, packageManager, iconPack,false);
            label.setText(item.label);
            if (checkedItems != null) {
                checkBox.setChecked(checkedItems.contains(item));
            }
        }
        return view;
    }
}
