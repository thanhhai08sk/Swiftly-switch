package org.de_studio.recentappswitcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.de_studio.recentappswitcher.service.EdgeSettingDialogFragment;

/**
 * Created by hai on 3/5/2016.
 */
public class OuterRingAdapter extends BaseAdapter {
    private static final String LOG_TAG = OuterRingAdapter.class.getSimpleName();
    private Context mContext;
    private String[] listAction;
    private int mPosition;
    private SharedPreferences sharedPreferences;

    public OuterRingAdapter(Context context, int position) {
        super();
        mContext = context;
        mPosition = position;
        sharedPreferences = context.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE,0);
        listAction = new String[]{MainActivity.ACTION_NONE,
        MainActivity.ACTION_HOME,
        MainActivity.ACTION_BACK,
        MainActivity.ACTION_NOTI,
        MainActivity.ACTION_WIFI,
        MainActivity.ACTION_BLUETOOTH,
        MainActivity.ACTION_ROTATE,
        MainActivity.ACTION_POWER_MENU};
    }

    public void setmPosition(int position) {
        mPosition = position;
    }
    @Override
    public int getCount() {
        return listAction.length;
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
            view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.choose_shortcut_app_list_item, parent, false);
        }
        ImageView icon = (ImageView) view.findViewById(R.id.choose_app_image_view);
        TextView label = (TextView) view.findViewById(R.id.choose_app_title_text_view);
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.choose_app_radio_button);
        icon.setImageBitmap(Utility.getBitmapForOuterSetting(mContext, listAction[position]));
        label.setText(Utility.getLabelForOuterSetting(mContext, listAction[position]));
        String cuttentAction= MainActivity.ACTION_NONE;
        switch (mPosition) {
            case 1:
                cuttentAction = sharedPreferences.getString(EdgeSettingDialogFragment.ACTION_1_KEY, MainActivity.ACTION_HOME);
                break;
            case 2:
                cuttentAction = sharedPreferences.getString(EdgeSettingDialogFragment.ACTION_2_KEY, MainActivity.ACTION_BACK);
                break;
            case 3:
                cuttentAction = sharedPreferences.getString(EdgeSettingDialogFragment.ACTION_3_KEY, MainActivity.ACTION_NONE);
                break;
            case 4:
                cuttentAction = sharedPreferences.getString(EdgeSettingDialogFragment.ACTION_4_KEY, MainActivity.ACTION_NOTI);
                break;
        }
        radioButton.setChecked(cuttentAction.equals(listAction[position]));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mPosition) {
                    case 1:
                        sharedPreferences.edit().putString(EdgeSettingDialogFragment.ACTION_1_KEY, listAction[position]).commit();
                        break;
                    case 2:
                        sharedPreferences.edit().putString(EdgeSettingDialogFragment.ACTION_2_KEY,listAction[position]).commit();
                        break;
                    case 3:
                        sharedPreferences.edit().putString(EdgeSettingDialogFragment.ACTION_3_KEY,listAction[position]).commit();
                        break;
                    case 4:
                        sharedPreferences.edit().putString(EdgeSettingDialogFragment.ACTION_4_KEY,listAction[position]).commit();
                }
                OuterRingAdapter.this.notifyDataSetChanged();
            }
        });
        return view;
    }
}
