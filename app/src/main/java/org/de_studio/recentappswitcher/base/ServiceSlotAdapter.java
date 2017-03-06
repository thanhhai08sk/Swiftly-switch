package org.de_studio.recentappswitcher.base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by HaiNguyen on 12/31/16.
 */

public class ServiceSlotAdapter extends RealmRecyclerViewAdapter<Slot, ServiceSlotAdapter.ViewHolder> {PackageManager packageManager;
    IconPackManager.IconPack iconPack;
    private static final String TAG = ServiceSlotAdapter.class.getSimpleName();
    float mScale, iconScale;


    public ServiceSlotAdapter(@NonNull Context context, @Nullable OrderedRealmCollection data, boolean autoUpdate, IconPackManager.IconPack iconPack, float mScale, float iconScale) {
        super(context, data, autoUpdate);
        this.iconPack = iconPack;
        packageManager = context.getPackageManager();
        this.mScale = mScale;
        this.iconScale = iconScale;
    }

    public void updateIconsState() {
        long time = System.currentTimeMillis();
        if (getData() != null) {
            Slot slot = null;
            for (int i = 0; i < getData().size(); i++) {
                slot = getData().get(i);
                if (slot != null &&
                        slot.type.equals(Slot.TYPE_ITEM) &&
                        slot.stage1Item != null &&
                        slot.stage1Item.type.equals(Item.TYPE_ACTION) &&
                        (slot.stage1Item.action == Item.ACTION_WIFI ||
                                slot.stage1Item.action == Item.ACTION_BLUETOOTH ||
                                slot.stage1Item.action == Item.ACTION_RINGER_MODE ||
                                slot.stage1Item.action == Item.ACTION_ROTATION ||
                                slot.stage1Item.action == Item.ACTION_FLASH_LIGHT)) {

                    notifyItemChanged(i);
                }
            }
        }
        Log.e(TAG, "updateIconsState: time = " + (System.currentTimeMillis() - time));
    }


    @Override
    public void onBindViewHolder(final ServiceSlotAdapter.ViewHolder holder, final int position) {
//        Log.e(TAG, "onBindViewHolder: "+ position + "\ntime = " + System.currentTimeMillis());
        final Slot slot = getItem(position);
        if (slot != null) {
            Utility.setSlotIcon(slot, context, holder.icon, packageManager, iconPack, false, true);
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
        imageView.setLayoutParams(new ViewGroup.LayoutParams((int) (Cons.ICON_SIZE_DEFAULT * mScale * iconScale), (int) (Cons.ICON_SIZE_DEFAULT * mScale * iconScale)));
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
