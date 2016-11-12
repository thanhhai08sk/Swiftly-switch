package org.de_studio.recentappswitcher.base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.model.Slot;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by HaiNguyen on 11/12/16.
 */

public class SlotsAdapter extends RealmBaseAdapter<Slot> {
    PackageManager packageManager;
    IconPackManager.IconPack iconPack;

    public SlotsAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Slot> data
            , IconPackManager.IconPack iconPack) {
        super(context, data);
        this.iconPack = iconPack;
        packageManager = context.getPackageManager();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = View.inflate(context, R.layout.item_circle_favorite, null);
        }
        Slot slot = getItem(position);
        TextView label = (TextView) view.findViewById(R.id.label);
        ImageView icon = (ImageView) view.findViewById(R.id.item_icon);
        if (slot != null) {
            Utility.setSlotIcon(slot, context, icon, packageManager, iconPack);
            Utility.setSlotLabel(slot, context, label);
        }
        return view;
    }
}
