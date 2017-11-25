package org.de_studio.recentappswitcher.base.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.model.Item;

import java.util.List;

import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 7/14/17.
 */

public class ItemsAdapter extends RecyclerView.Adapter<ItemsRealmAdapter.ViewHolder> {
    Context context;
    List<Item> data;
    PackageManager packageManager;
    IconPackManager.IconPack iconPack;
    PublishSubject<Item> itemClickSJ;

    public ItemsAdapter(Context context, List<Item> data, PackageManager packageManager, IconPackManager.IconPack iconPack, PublishSubject<Item> itemClickSJ) {
        this.itemClickSJ = itemClickSJ;
        this.context = context;
        this.data = data;
        this.packageManager = packageManager;
        this.iconPack = iconPack;

    }

    @Override
    public ItemsRealmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slot_icon_label, parent, false);
        return new ItemsRealmAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemsRealmAdapter.ViewHolder holder, int position) {
        final Item item = data.get(position);
        if (item != null) {
            holder.label.setText(item.label);
            Utility.setItemIcon(item, context, holder.icon, packageManager, iconPack, true);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickSJ.onNext(item);
                }
            });
        }
    }

    public void updateData(List<Item> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public Item getFirstResult() {
        if (data != null && data.size() > 0) {
            return data.get(0);
        }else return null;
    }
}
