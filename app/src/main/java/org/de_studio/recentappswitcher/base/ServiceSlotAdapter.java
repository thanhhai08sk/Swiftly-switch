package org.de_studio.recentappswitcher.base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.model.Slot;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by HaiNguyen on 12/31/16.
 */

public class ServiceSlotAdapter extends RealmRecyclerViewAdapter<Slot, ServiceSlotAdapter.ViewHolder> {PackageManager packageManager;
    IconPackManager.IconPack iconPack;
//    private final PublishSubject<Integer> onClickSubject = PublishSubject.create();
    float mScale, iconScale;


    public ServiceSlotAdapter(@NonNull Context context, @Nullable OrderedRealmCollection data, boolean autoUpdate, IconPackManager.IconPack iconPack, float mScale, float iconScale) {
        super(context, data, autoUpdate);
        this.iconPack = iconPack;
        packageManager = context.getPackageManager();
        this.mScale = mScale;
        this.iconScale = iconScale;
    }


    @Override
    public void onBindViewHolder(final ServiceSlotAdapter.ViewHolder holder, final int position) {
        final Slot slot = getItem(position);
        if (slot != null) {
            Utility.setSlotIcon(slot, context, holder.icon, packageManager, iconPack);
//            holder.view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    onClickSubject.onNext(holder.getAdapterPosition());
//                }
//            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams((int) (Cons.DEFAULT_ICON_WIDTH * mScale * iconScale), (int) (Cons.DEFAULT_ICON_WIDTH * mScale * iconScale)));
        imageView.setId(R.id.item_icon);

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        frameLayout.addView(imageView);
//        frameLayout.setBackgroundColor(Color.BLUE);
        return new ViewHolder(frameLayout);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ImageView icon;
        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            icon = (ImageView) view.findViewById(R.id.item_icon);
        }
    }
//    public Observable<Integer> getKeyClicked() {
//        return onClickSubject.asObservable();
//    }


}
