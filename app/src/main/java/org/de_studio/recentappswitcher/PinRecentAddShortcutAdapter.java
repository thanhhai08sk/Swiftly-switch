package org.de_studio.recentappswitcher;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by HaiNguyen on 7/1/16.
 */
public class PinRecentAddShortcutAdapter extends BaseAdapter {

    private static final String TAG = PinRecentAddShortcutAdapter.class.getSimpleName();
    private Context context;
    private List<ResolveInfo> resolveInfos;
    private PackageManager packageManager;


    public PinRecentAddShortcutAdapter(Context context, List<ResolveInfo> arrayList) {
        super();
        this.context = context;
        this.resolveInfos = arrayList;
        packageManager = context.getPackageManager();

    }
    @Override
    public int getCount() {
        return resolveInfos.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_circle_favorite, parent, false);
        }
        ImageView icon = (ImageView) view.findViewById(R.id.item_icon);
        TextView label = (TextView) view.findViewById(R.id.item_label);
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
