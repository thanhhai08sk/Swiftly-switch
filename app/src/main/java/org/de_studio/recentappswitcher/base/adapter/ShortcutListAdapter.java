package org.de_studio.recentappswitcher.base.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.R;

import java.util.List;

/**
 * Created by HaiNguyen on 11/25/16.
 */

public class ShortcutListAdapter extends BaseAdapter {
    private static final String TAG = ShortcutListAdapter.class.getSimpleName();
    private Context context;
    private List<ResolveInfo> resolveInfos;
    private PackageManager packageManager;


    public ShortcutListAdapter(Context context, List<ResolveInfo> arrayList) {
        super();
        this.context = context;
        this.resolveInfos = arrayList;
        packageManager = context.getPackageManager();
    }

    public void setData(List<ResolveInfo> resolveInfoList) {
        this.resolveInfos = resolveInfoList;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (resolveInfos == null) {
            return 0;
        }
        return resolveInfos.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_choose_shortcut_shortcut_list, parent, false);
        }
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView label = (TextView) view.findViewById(R.id.label);
        icon.setImageDrawable(resolveInfos.get(position).loadIcon(packageManager));
        label.setText(resolveInfos.get(position).loadLabel(packageManager));
        return view;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

}
