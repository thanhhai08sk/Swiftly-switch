package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.de_studio.recentappswitcher.AppInfors;
import org.de_studio.recentappswitcher.R;

import java.util.ArrayList;

/**
 * Created by hai on 2/23/2016.
 */
public class ChooseAppListViewAdapter extends BaseAdapter {
    private Context mContext;
    static private ArrayList<AppInfors> mAppInfosArrayList;
    private static final String LOG_TAG = ChooseAppListViewAdapter.class.getSimpleName();
    private int mPosition;

    public ChooseAppListViewAdapter(Context context, ArrayList<AppInfors> appInforses, int position) {
        super();
        mPosition = position;
        mContext = context;
        mAppInfosArrayList = appInforses;
    }

    @Override
    public int getCount() {
        return mAppInfosArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mAppInfosArrayList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.choose_shortcut_app_list_item, parent, false);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.choose_app_image_view);
        TextView textView = (TextView) view.findViewById(R.id.choose_app_title_text_view);
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.choose_app_radio_button);
        imageView.setImageDrawable(mAppInfosArrayList.get(position).iconDrawable);
        textView.setText(mAppInfosArrayList.get(position).label);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }
}
