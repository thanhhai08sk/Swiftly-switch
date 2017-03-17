package org.de_studio.recentappswitcher.setItemIcon;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;

import java.util.SortedMap;

/**
 * Created by HaiNguyen on 3/17/17.
 */

public class DrawableAdapter  extends RecyclerView.Adapter<DrawableAdapter.ViewHolder>{
    SortedMap<String, String> sortedMap;
    IconPackManager.IconPack iconPack;
    Context context;
    PackageManager packageManager;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if (sortedMap != null) {
            return sortedMap.size();
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
}
