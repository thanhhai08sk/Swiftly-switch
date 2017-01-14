package org.de_studio.recentappswitcher.base;

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
import org.de_studio.recentappswitcher.model.Slot;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/12/16.
 */

public class SlotsAdapter extends RealmRecyclerViewAdapter<Slot, SlotsAdapter.ViewHolder> {
    private static final String TAG = SlotsAdapter.class.getSimpleName();
    PackageManager packageManager;
    IconPackManager.IconPack iconPack;
    private final PublishSubject<Integer> onClickSubject = PublishSubject.create();
    int itemType;


    public SlotsAdapter(@NonNull Context context, @Nullable OrderedRealmCollection data, boolean autoUpdate, IconPackManager.IconPack iconPack, int itemType) {
        super(context, data, autoUpdate);
        this.iconPack = iconPack;
        packageManager = context.getPackageManager();
        this.itemType = itemType;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Slot slot = getItem(position);
        if (slot != null) {
            switch (itemType) {
                case Cons.ITEM_TYPE_ICON_LABEL:
                    Utility.setSlotLabel(slot, context, holder.label);
                    break;
            }
            Utility.setSlotIcon(slot, context, holder.icon, packageManager, iconPack, true);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSubject.onNext(holder.getAdapterPosition());
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
//                view = LayoutInflater.from(context).inflate(R.layout.item_icon_only, parent, false);
                view = inflater.inflate(R.layout.item_icon_only, parent, false);
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
    public Observable<Integer> getKeyClicked() {
        return onClickSubject.asObservable();
    }


}
