package org.de_studio.recentappswitcher.service;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.R;

/**
 * Created by hai on 1/2/2016.
 */
public class EdgeSettingDialogFragment extends DialogFragment {
    private static final String LOG_TAG = EdgeSettingDialogFragment.class.getSimpleName();
    public static final String EDGE_POSITION_KEY = "position";
    public static final String EDGE_SENSIIVE_KEY = "sensitive";
    public static final String EDGE_LENGTH_KEY = "length";
    public static final String EDGE_NUMBER_KEY = "number_of_edge";
    private static int edgeNumber;
    private  float mScale;
    private static SharedPreferences sharedPreferences;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        edgeNumber = args.getInt(EDGE_NUMBER_KEY,1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (edgeNumber ==1){
            sharedPreferences = getContext().getSharedPreferences(MainActivity.EDGE_1_SHAREDPREFERENCE,0);
        }else if (edgeNumber ==2){
            sharedPreferences = getContext().getSharedPreferences(MainActivity.EDGE_2_SHAREDPREFERENCE,0);
        }
        Log.e(LOG_TAG,"edge number = " + edgeNumber);
        if (sharedPreferences == null){
            Log.e(LOG_TAG, "sharedPreference == null at onCreateview");
        }

        View rootView = inflater.inflate(R.layout.edge_setting_dialog, container, false);
        final ImageView edgeImage = (ImageView) rootView.findViewById(R.id.edge_dialog_edge_image_view);
        final TextView sensitiveNumberTextView = (TextView) rootView.findViewById(R.id.edge_dialog_sensitive_number_text);
        AppCompatSpinner positionSpinner = (AppCompatSpinner) rootView.findViewById(R.id.edge_dialog_position_spinner);
        positionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(edgeImage.getLayoutParams());
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                sharedPreferences.edit().putString(EDGE_POSITION_KEY,item).commit();
                switch (position){
                    case 0: lp.gravity = Gravity.TOP|Gravity.RIGHT;
                        break;
                    case 1: lp.gravity = Gravity.CENTER_VERTICAL|Gravity.RIGHT;
                        break;
                    case 2: lp.gravity = Gravity.BOTTOM|Gravity.RIGHT;
                        break;
                    case 3: lp.gravity = Gravity.TOP|Gravity.LEFT;
                        break;
                    case 4: lp.gravity = Gravity.CENTER_VERTICAL|Gravity.LEFT;
                        break;
                    case 5: lp.gravity = Gravity.BOTTOM|Gravity.LEFT;
                        break;
                }
                edgeImage.setLayoutParams(lp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        AppCompatSeekBar sensitiveSeekBar = (AppCompatSeekBar) rootView.findViewById(R.id.sensitive_seek_bar);
        sensitiveSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged;  //5 to 25
            ViewGroup.LayoutParams edgeParas;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress +5;
                edgeParas = edgeImage.getLayoutParams();
                edgeParas.width =(int)(progressChanged * mScale);
                edgeImage.setLayoutParams(edgeParas);
                sensitiveNumberTextView.setText(progressChanged + "dp");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sharedPreferences.edit().putInt(EDGE_SENSIIVE_KEY,progressChanged).commit();
            }
        });


        AppCompatSeekBar lengthSeekBar = (AppCompatSeekBar) rootView.findViewById(R.id.length_seek_bar);
        final TextView edgeLengthNumberText = (TextView) rootView.findViewById(R.id.edge_dialog_length_number_view);
        lengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged; // 75 to 200
            ViewGroup.LayoutParams edgeParas;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress + 75;
                edgeParas = edgeImage.getLayoutParams();
                edgeParas.height = (int) (progressChanged * mScale);
                edgeImage.setLayoutParams(edgeParas);
                edgeLengthNumberText.setText(progressChanged + "dp");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sharedPreferences.edit().putInt(EDGE_LENGTH_KEY,progressChanged).commit();
            }
        });





        return rootView;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScale = getResources().getDisplayMetrics().density;
    }
}
