package org.de_studio.recentappswitcher;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.service.EdgeSettingDialogFragment;

public class OuterRingSettingActivity extends AppCompatActivity {
    private static final String LOG_TAG = OuterRingSettingActivity.class.getSimpleName();
    private ListView mListView;
    private OuterRingAdapter mAdapter;
    private String[] listAction;
    private SharedPreferences sharedPreferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        listAction = new String[]{MainActivity.ACTION_NONE,
                MainActivity.ACTION_HOME,
                MainActivity.ACTION_BACK,
                MainActivity.ACTION_RECENT,
                MainActivity.ACTION_NOTI,
                MainActivity.ACTION_WIFI,
                MainActivity.ACTION_BLUETOOTH,
                MainActivity.ACTION_ROTATE,
                MainActivity.ACTION_LAST_APP,
                MainActivity.ACTION_DIAL,
                MainActivity.ACTION_CONTACT,
                MainActivity.ACTION_CALL_LOGS,
                MainActivity.ACTION_POWER_MENU};
        setContentView(R.layout.activity_outter_ring_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.outer_ring_setting_list_view);
        mAdapter = new OuterRingAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                CharSequence[] listActionlabel = getResources().getStringArray(R.array.outer_ring_list_available_action);
                CharSequence currentLabel = ((TextView) view.findViewById(R.id.outer_item_action_label_text_view)).getText();
                int currentChecked = 0;
                while (!currentLabel.equals(listActionlabel[currentChecked])) {
                    currentChecked++;
                }
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(OuterRingSettingActivity.this);
                builder.setSingleChoiceItems(listActionlabel, currentChecked, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    switch (position) {
                                        case 0:
                                            sharedPreferences.edit().putString(EdgeSettingDialogFragment.ACTION_1_KEY, listAction[which]).commit();
                                            break;
                                        case 1:
                                            sharedPreferences.edit().putString(EdgeSettingDialogFragment.ACTION_2_KEY, listAction[which]).commit();
                                            break;
                                        case 2:
                                            sharedPreferences.edit().putString(EdgeSettingDialogFragment.ACTION_3_KEY, listAction[which]).commit();
                                            break;
                                        case 3:
                                            sharedPreferences.edit().putString(EdgeSettingDialogFragment.ACTION_4_KEY, listAction[which]).commit();
                                            break;
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    Log.e(LOG_TAG, "ArrayIndexOutOfBounds when set outer ring action");
                                }

                            }
                        }

                );
                builder.setPositiveButton(

                        getResources()

                                .

                                        getString(R.string.edge_dialog_ok_button),

                        new DialogInterface.OnClickListener()

                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }

                ).

                        setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                 @Override
                                                 public void onDismiss(DialogInterface dialog) {
                                                     mAdapter.notifyDataSetChanged();
                                                 }
                                             }

                        );
                builder.create().

                        show();
            }
        });

    }

}
