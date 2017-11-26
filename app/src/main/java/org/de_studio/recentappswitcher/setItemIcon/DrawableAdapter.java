package org.de_studio.recentappswitcher.setItemIcon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.de_studio.recentappswitcher.R;

import java.util.ArrayList;

import rx.subjects.PublishSubject;


/**
 * Created by HaiNguyen on 3/17/17.
 */

public class DrawableAdapter  extends RecyclerView.Adapter<DrawableAdapter.ViewHolder>{
    Context context;
    ArrayList<SetItemIconView.BitmapInfo> allItems;
    PublishSubject<SetItemIconView.BitmapInfo> itemClickSJ = PublishSubject.create();


    public DrawableAdapter(Context context, ArrayList<SetItemIconView.BitmapInfo> allItems) {
        this.context = context;
        this.allItems = allItems;
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
        try {
            Drawable drawable = ResourcesCompat.getDrawable(info.res, info.resId, null);
            holder.imageView.setImageDrawable(drawable);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickSJ.onNext(info);
                }
            });
        } catch (Exception e) {
            holder.imageView.setImageDrawable(null);
        }
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

}
