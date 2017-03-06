package org.de_studio.recentappswitcher.base.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.OrderedRealmCollection;

/**
 * Created by HaiNguyen on 1/7/17.
 */

public class ServiceItemsAdapter extends ItemsAdapter {
    float mScale, iconScale;
    public ServiceItemsAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Item> data, boolean autoUpdate, PackageManager packageManager, IconPackManager.IconPack iconPack, float mScale, float iconScale) {
        super(context, data, autoUpdate, packageManager, iconPack, Cons.ITEM_TYPE_ICON_ONLY);
        this.mScale = mScale;
        this.iconScale = iconScale;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams((int) (Cons.ICON_SIZE_DEFAULT * mScale * iconScale), (int) (Cons.ICON_SIZE_DEFAULT * mScale * iconScale)));
        imageView.setId(R.id.item_icon);

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        frameLayout.addView(imageView);
        return new ViewHolder(frameLayout);
    }
}
