package org.de_studio.recentappswitcher.base.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/3/16.
 */

public class ItemsAdapter extends RealmRecyclerViewAdapter<Item,ItemsAdapter
        .ViewHolder> {
    private static final String TAG = ItemsAdapter.class.getSimpleName();
    PackageManager packageManager;
    IconPackManager.IconPack iconPack;
    private final PublishSubject<Item> onClickSubject = PublishSubject.create();
    int itemType;


    public ItemsAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Item> data, boolean autoUpdate, PackageManager packageManager, IconPackManager.IconPack iconPack, int itemType) {
        super(context, data, autoUpdate);
        this.packageManager = packageManager;
        this.iconPack = iconPack;
        this.itemType = itemType;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Item item = getItem(position);
        if (item != null) {
            switch (itemType) {
                case Cons.ITEM_TYPE_ICON_LABEL:
                    holder.label.setText(item.label);
                    break;
            }
            Utility.setItemIcon(item,context,holder.icon,packageManager, iconPack);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSubject.onNext(item);
                }
            });
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (itemType) {
            case Cons.ITEM_TYPE_ICON_LABEL:
                view = LayoutInflater.from(context).inflate(R.layout.item_circle_favorite, parent, false);
                break;
            case Cons.ITEM_TYPE_ICON_ONLY:
                view = LayoutInflater.from(context).inflate(R.layout.item_icon_only, parent, false);
                break;
        }
        return new ViewHolder(view);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView label;
        public ImageView icon;
        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            label = (TextView) view.findViewById(R.id.item_label);
            icon = (ImageView) view.findViewById(R.id.item_icon);
        }
    }
    public Observable<Item> getKeyClicked() {
        return onClickSubject.asObservable();
    }


}
