package org.de_studio.recentappswitcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.service.EdgeSettingDialogFragment;

/**
 * Created by hai on 3/5/2016.
 */
public class OuterRingAdapter extends BaseAdapter {
    private static final String LOG_TAG = OuterRingAdapter.class.getSimpleName();
    private Context mContext;
    private String[] listAction;
    private SharedPreferences sharedPreferences;

    public OuterRingAdapter(Context context) {
        super();
        mContext = context;
        sharedPreferences = context.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE,0);
        listAction = mContext.getResources().getStringArray(R.array.outer_setting_list_action);
//        listAction = new String[]{MainActivity.ACTION_NONE,
//        MainActivity.ACTION_HOME,
//        MainActivity.ACTION_BACK,
//        MainActivity.ACTION_NOTI,
//        MainActivity.ACTION_WIFI,
//        MainActivity.ACTION_BLUETOOTH,
//        MainActivity.ACTION_ROTATE,
//        MainActivity.ACTION_POWER_MENU};
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return listAction[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.outer_list_item, parent, false);
        }
        TextView title = (TextView) view.findViewById(R.id.outer_item_title_text_view);
        ImageView icon = (ImageView) view.findViewById(R.id.outer_item_action_icon_image_view);
        TextView label = (TextView) view.findViewById(R.id.outer_item_action_label_text_view);

        title.setText(listAction[position]);

        String cuttentAction= MainActivity.ACTION_NONE;
        switch (position) {
            case 0:
                cuttentAction = sharedPreferences.getString(EdgeSettingDialogFragment.ACTION_1_KEY, MainActivity.ACTION_HOME);
                break;
            case 1:
                cuttentAction = sharedPreferences.getString(EdgeSettingDialogFragment.ACTION_2_KEY, MainActivity.ACTION_BACK);
                break;
            case 2:
                cuttentAction = sharedPreferences.getString(EdgeSettingDialogFragment.ACTION_3_KEY, MainActivity.ACTION_NONE);
                break;
            case 3:
                cuttentAction = sharedPreferences.getString(EdgeSettingDialogFragment.ACTION_4_KEY, MainActivity.ACTION_NOTI);
                break;
        }
        label.setText(Utility.getLabelForOuterSetting(mContext, cuttentAction));
        icon.setImageBitmap(Utility.getBitmapForOuterSetting(mContext, cuttentAction));
        return view;
    }
}
