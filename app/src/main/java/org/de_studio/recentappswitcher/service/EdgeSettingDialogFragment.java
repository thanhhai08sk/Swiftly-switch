package org.de_studio.recentappswitcher.service;

import android.content.Intent;
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
    public static final String EDGE_ON_KEY = "is_on";
    public static final String FAVORITE_KEY = "favorite";
    public static final String EXCLUDE_KEY = "exclude";
    private static int edgeNumber;
    private  float mScale;
    private static SharedPreferences sharedPreferences;
    private ViewGroup.LayoutParams mEdgeParas;

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
        int currentLength = sharedPreferences.getInt(EDGE_LENGTH_KEY ,150);
        int currentSensitive = sharedPreferences.getInt(EDGE_SENSIIVE_KEY, 12);
        mEdgeParas = edgeImage.getLayoutParams();
        mEdgeParas.height = (int) (currentLength *mScale);
        mEdgeParas.width = (int) (currentSensitive * mScale);
        edgeImage.setLayoutParams(mEdgeParas);
        final TextView sensitiveNumberTextView = (TextView) rootView.findViewById(R.id.edge_dialog_sensitive_number_text);
        AppCompatSpinner positionSpinner = (AppCompatSpinner) rootView.findViewById(R.id.edge_dialog_position_spinner);
        String[] spinnerEntries = getResources().getStringArray(R.array.edge_dialog_spinner_array);
        int spinnerCurrentPosition =1;
        for (int i =0; i<spinnerEntries.length; i++) {
            if (spinnerEntries[i].equals(sharedPreferences.getString(EDGE_POSITION_KEY, spinnerEntries[1]))) {
                spinnerCurrentPosition = i;
            }
        }
        positionSpinner.setSelection(spinnerCurrentPosition, true);
        positionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(edgeImage.getLayoutParams());

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                sharedPreferences.edit().putString(EDGE_POSITION_KEY, item).commit();
                switch (position) {
                    case 0:
                        lp.gravity = Gravity.TOP | Gravity.RIGHT;
                        break;
                    case 1:
                        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                        break;
                    case 2:
                        lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                        break;
                    case 3:
                        lp.gravity = Gravity.TOP | Gravity.LEFT;
                        break;
                    case 4:
                        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                        break;
                    case 5:
                        lp.gravity = Gravity.BOTTOM | Gravity.LEFT;
                        break;
                }
                edgeImage.setLayoutParams(lp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        AppCompatSeekBar sensitiveSeekBar = (AppCompatSeekBar) rootView.findViewById(R.id.sensitive_seek_bar);
        sensitiveSeekBar.setProgress(currentSensitive - 5);
        sensitiveNumberTextView.setText(currentSensitive + "dp");
        sensitiveSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged;  //5 to 25
            ViewGroup.LayoutParams edgeParas;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress + 5;
                edgeParas = edgeImage.getLayoutParams();
                edgeParas.width = (int) (progressChanged * mScale);
                edgeImage.setLayoutParams(edgeParas);
                sensitiveNumberTextView.setText(progressChanged + "dp");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sharedPreferences.edit().putInt(EDGE_SENSIIVE_KEY, progressChanged).commit();
            }
        });


        AppCompatSeekBar lengthSeekBar = (AppCompatSeekBar) rootView.findViewById(R.id.length_seek_bar);
        lengthSeekBar.setProgress(currentLength - 75);
        final TextView edgeLengthNumberText = (TextView) rootView.findViewById(R.id.edge_dialog_length_number_view);
        edgeLengthNumberText.setText(currentLength+ "dp");
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
        getContext().stopService(new Intent(getContext(), EdgeGestureService.class));

        mScale = getResources().getDisplayMetrics().density;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(LOG_TAG, sharedPreferences.getString(EDGE_POSITION_KEY, "null") + "\n" + sharedPreferences.getInt(EDGE_SENSIIVE_KEY, 0) + "\n" + sharedPreferences.getInt(EDGE_LENGTH_KEY, 0));
        getContext().startService(new Intent(getContext(),EdgeGestureService.class));
    }
}
