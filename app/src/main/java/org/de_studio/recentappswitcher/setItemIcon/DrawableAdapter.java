package org.de_studio.recentappswitcher.setItemIcon;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.de_studio.recentappswitcher.R;

import java.util.ArrayList;

import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;


/**
 * Created by HaiNguyen on 3/17/17.
 */

public class DrawableAdapter  extends RecyclerView.Adapter<DrawableAdapter.ViewHolder>{
    Context context;
    ArrayList<SetItemIconView.BitmapInfo> allItems;
    PublishSubject<SetItemIconView.BitmapInfo> itemClickSJ = PublishSubject.create();
    PublishSubject<ItemInfo> loadSJ = PublishSubject.create();


    public DrawableAdapter(Context context, ArrayList<SetItemIconView.BitmapInfo> allItems) {
        this.context = context;
        this.allItems = allItems;
        loadSJ
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .map(new Func1<ItemInfo, ItemInfo>() {
                    @Override
                    public ItemInfo call(ItemInfo itemInfo) {
                        try {
                            itemInfo.drawable = ResourcesCompat.getDrawable(itemInfo.res, itemInfo.resId, null);
                            return itemInfo;
                        } catch (Exception e) {
                            return itemInfo;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ItemInfo>() {
                    @Override
                    public void call(ItemInfo itemInfo) {
                        if (itemInfo.icon != null) {
                            itemInfo.icon.setImageDrawable(itemInfo.drawable);
                        }
                    }
                });
    }

    public void updateData(ArrayList<SetItemIconView.BitmapInfo> items) {
        allItems = items;
        allItems = (ArrayList<SetItemIconView.BitmapInfo>) items.clone();
        notifyDataSetChanged();
    }

    public void clearData() {
        allItems.clear();
    }

    public void addItem(SetItemIconView.BitmapInfo item) {
        allItems.add(item);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_drawable_icon_only, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SetItemIconView.BitmapInfo info = allItems.get(position);
        loadSJ.onNext(new ItemInfo(info.res, info.resId, holder.imageView));
//        try {
//            Drawable drawable = ResourcesCompat.getDrawable(info.res, info.resId, null);
//            holder.imageView.setImageDrawable(drawable);
//            holder.imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    itemClickSJ.onNext(info);
//                }
//            });
//        } catch (Exception e) {
//            holder.imageView.setImageDrawable(null);
//        }
    }

    @Override
    public int getItemCount() {
        if (allItems != null) {
            return allItems.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
        }
    }

    public PublishSubject<SetItemIconView.BitmapInfo> onItemClick() {
        return itemClickSJ;
    }

    static class ItemInfo {
        public Resources res;
        public int resId;
        public ImageView icon;
        public Drawable drawable;

        public ItemInfo(Resources res, int resId, ImageView icon) {
            this.res = res;
            this.resId = resId;
            this.icon = icon;
        }
    }

}
