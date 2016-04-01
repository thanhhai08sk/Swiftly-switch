package org.de_studio.recentappswitcher.service;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatButton;
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
import org.de_studio.recentappswitcher.Utility;

/**
 * Created by hai on 1/2/2016.
 */
public class EdgeSettingDialogFragment extends DialogFragment {
    private static final String LOG_TAG = EdgeSettingDialogFragment.class.getSimpleName();
    public static final String EDGE_POSITION_KEY = "position";
    public static final String EDGE_SENSIIVE_KEY = "sensitive";
    public static final String EDGE_LENGTH_KEY = "length";
    public static final String EDGE_OFFSET_KEY = "off_set";
    public static final String EDGE_NUMBER_KEY = "number_of_edge";
    public static final String EDGE_ON_KEY = "is_on";
    public static final String FAVORITE_KEY = "favorite";
    public static final String EXCLUDE_KEY = "exclude";
    public static final String TRIAL_TIME_PASS_KEY = "trial_time_pass";
    public static final String BEGIN_DAY_KEY = "begin_trial_time";
    public static final String DISABLE_HAPTIC_FEEDBACK_KEY = "disable_haptic";
    public static final String HAPTIC_ON_ICON_KEY = "haptic_on_icon";
    public static final String DISABLE_CLOCK_KEY = "disable_clock";
    public static final String HOLD_TIME_ENABLE_KEY = "hold_time_enable";
    public static final String APP_OPEN_TIME_KEY = "app_open_time";
    public static final String HAS_REACT_FOR_VOTE_KEY = "has_react_for_vote";
    public static final String ICON_DISTANCE_KEY = "icon_distance";
    public static final String NUM_OF_RECENT_KEY = "num_of_recent";
    public static final String NUM_OF_GRID_ROW_KEY = "grid_row";
    public static final String NUM_OF_GRID_COLUMN_KEY = "grid_column";
    public static final String IS_CENTRE_FAVORITE = "is_centre";
    public static final String GAP_OF_SHORTCUT_KEY = "shortcut_gap";
    public static final String GRID_DISTANCE_FROM_EDGE_KEY = "grid_distance_from_edge";
    public static final String IS_ONLY_FAVORITE_KEY = "is_only_favorite";
    public static final String ICON_PACK_PACKAGE_NAME_KEY = "icon_pack_packa";
    public static final String ACTION_1_KEY = "action_1";
    public static final String ACTION_2_KEY = "action_2";
    public static final String ACTION_3_KEY = "action_3";
    public static final String ACTION_4_KEY = "action_4";
    public static final String IS_ACTIONS_STAY_PERMANENT = "is_permanent";
    public static final String VIBRATION_DURATION_KEY = "vibration_duration";
    public static final String HOLD_TIME_KEY  = "hold_time";
    public static final String IS_PIN_TO_TOP_KEY = "is_pin_to_top";
    public static final String BACKGROUND_COLOR_KEY = "background_color";
    private static int edgeNumber;
    private  float mScale;
    private static SharedPreferences sharedPreferences,defaultSharedPreferences;
    private ViewGroup.LayoutParams mEdgeParas;
    private Context mContext;
    private String[] spinnerEntries;
    private ImageView edgeImage;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        edgeNumber = args.getInt(EDGE_NUMBER_KEY,1);

    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (edgeNumber ==1){
            sharedPreferences = mContext.getSharedPreferences(MainActivity.EDGE_1_SHAREDPREFERENCE, 0);
        }else if (edgeNumber ==2){
            sharedPreferences = mContext.getSharedPreferences(MainActivity.EDGE_2_SHAREDPREFERENCE, 0);
        }
        defaultSharedPreferences = mContext.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE,0);
        Log.e(LOG_TAG,"edge number = " + edgeNumber);
        if (sharedPreferences == null){
            Log.e(LOG_TAG, "sharedPreference == null at onCreateview");
        }

        View rootView = inflater.inflate(R.layout.edge_setting_dialog, container, false);

        AppCompatButton defaultButton = (AppCompatButton) rootView.findViewById(R.id.edge_dialog_default_button);
        AppCompatButton okButton = (AppCompatButton) rootView.findViewById(R.id.edge_dialog_ok_button);
        edgeImage = (ImageView) rootView.findViewById(R.id.edge_dialog_edge_image_view);

        int currentLength = sharedPreferences.getInt(EDGE_LENGTH_KEY ,150);
        int currentOffset = sharedPreferences.getInt(EDGE_OFFSET_KEY, 0);
        int currentSensitive = sharedPreferences.getInt(EDGE_SENSIIVE_KEY, 12);
        int currentCircleSize = defaultSharedPreferences.getInt(ICON_DISTANCE_KEY,110);
        spinnerEntries = getResources().getStringArray(R.array.edge_dialog_spinner_array);
        final TextView sensitiveNumberTextView = (TextView) rootView.findViewById(R.id.edge_dialog_sensitive_number_text);
        final AppCompatSpinner positionSpinner = (AppCompatSpinner) rootView.findViewById(R.id.edge_dialog_position_spinner);
        final AppCompatSpinner modeSpinner = (AppCompatSpinner) rootView.findViewById(R.id.edge_dialog_mode_spinner);
        boolean isOnlyFavorite = sharedPreferences.getBoolean(EdgeSettingDialogFragment.IS_ONLY_FAVORITE_KEY, false);
        if (isOnlyFavorite) {
            modeSpinner.setSelection(1);
        } else {
            modeSpinner.setSelection(0);
        }
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        sharedPreferences.edit().putBoolean(EdgeSettingDialogFragment.IS_ONLY_FAVORITE_KEY, false).commit();
                        break;
                    case 1:
                        sharedPreferences.edit().putBoolean(EdgeSettingDialogFragment.IS_ONLY_FAVORITE_KEY,true).commit();
                }

                mContext.stopService(new Intent(mContext, EdgeGestureService.class));
                mContext.startService(new Intent(mContext, EdgeGestureService.class));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int spinnerCurrentPosition =1;
        if (edgeNumber == 2) spinnerCurrentPosition = 5;
        for (int i =0; i<spinnerEntries.length; i++) {
            if (spinnerEntries[i].equals(sharedPreferences.getString(EDGE_POSITION_KEY, spinnerEntries[spinnerCurrentPosition]))) {
                spinnerCurrentPosition = i;
            }
        }
        updateEdgeView();
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
                    case 6:
                        lp.gravity = Gravity.BOTTOM | Gravity.CENTER;
                        break;
                }
                edgeImage.setLayoutParams(lp);
                updateEdgeView();
                mContext.stopService(new Intent(mContext, EdgeGestureService.class));
                mContext.startService(new Intent(mContext, EdgeGestureService.class));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        final AppCompatSeekBar sensitiveSeekBar = (AppCompatSeekBar) rootView.findViewById(R.id.sensitive_seek_bar);
        sensitiveSeekBar.setProgress(currentSensitive - 5);
        sensitiveNumberTextView.setText(currentSensitive + "dp");
        sensitiveSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged;  //5 to 25
            ViewGroup.LayoutParams edgeParas;
            String[] spinnerEntries = getResources().getStringArray(R.array.edge_dialog_spinner_array);

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress + 5;
                edgeParas = edgeImage.getLayoutParams();
                if (Utility.getPositionIntFromString(sharedPreferences.getString(EdgeSettingDialogFragment.EDGE_POSITION_KEY, spinnerEntries[1]), mContext) >= 30) {
                    edgeParas.height = (int) (progressChanged * mScale);
                } else edgeParas.width = (int) (progressChanged * mScale);
                edgeImage.setLayoutParams(edgeParas);
                sensitiveNumberTextView.setText(progressChanged + "dp");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sharedPreferences.edit().putInt(EDGE_SENSIIVE_KEY, progressChanged).commit();
                mContext.stopService(new Intent(mContext, EdgeGestureService.class));
                mContext.startService(new Intent(mContext, EdgeGestureService.class));
            }
        });
        final AppCompatSeekBar lengthSeekBar = (AppCompatSeekBar) rootView.findViewById(R.id.length_seek_bar);
        lengthSeekBar.setProgress(currentLength - 75);
        final TextView edgeLengthNumberText = (TextView) rootView.findViewById(R.id.edge_dialog_length_number_view);
        edgeLengthNumberText.setText(currentLength+ "dp");
        lengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged; // 75 to 200
            ViewGroup.LayoutParams edgeParas;
            String[] spinnerEntries = getResources().getStringArray(R.array.edge_dialog_spinner_array);

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress + 75;
                edgeParas = edgeImage.getLayoutParams();

                if (Utility.getPositionIntFromString(sharedPreferences.getString(EdgeSettingDialogFragment.EDGE_POSITION_KEY, spinnerEntries[1]), mContext) >= 30) {
                    edgeParas.width = (int) (progressChanged * mScale);
                } else edgeParas.height = (int) (progressChanged * mScale);

                edgeImage.setLayoutParams(edgeParas);
                edgeLengthNumberText.setText(progressChanged + "dp");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sharedPreferences.edit().putInt(EDGE_LENGTH_KEY, progressChanged).commit();
                mContext.stopService(new Intent(mContext, EdgeGestureService.class));
                mContext.startService(new Intent(mContext, EdgeGestureService.class));
            }
        });




        final AppCompatSeekBar offsetSeekBar = (AppCompatSeekBar) rootView.findViewById(R.id.edge_dialog_offset_seek_bar);
        offsetSeekBar.setProgress(currentOffset + 150);
        final TextView edgeOffsetNumberText = (TextView) rootView.findViewById(R.id.edge_dialog_offset_number_view);
        edgeOffsetNumberText.setText(currentOffset+ "dp");
        offsetSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged; // -150 to 150
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress - 150;
                sharedPreferences.edit().putInt(EDGE_OFFSET_KEY, progressChanged).commit();
                updateEdgeView();
                edgeOffsetNumberText.setText(progressChanged + "dp");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mContext.stopService(new Intent(mContext, EdgeGestureService.class));
                mContext.startService(new Intent(mContext, EdgeGestureService.class));
//                sharedPreferences.edit().putInt(EDGE_OFFSET_KEY, progressChanged).commit();
            }
        });




        final AppCompatSeekBar circleSizeSeekBar = (AppCompatSeekBar) rootView.findViewById(R.id.circle_size_seek_bar);
        final TextView circleSizeNumberText = (TextView) rootView.findViewById(R.id.edge_dialog_circle_size_number_view);
        circleSizeSeekBar.setProgress(currentCircleSize - 95);
        circleSizeNumberText.setText(currentCircleSize + "dp");
        circleSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged; //95 to 130
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress + 95;
                circleSizeNumberText.setText(progressChanged + "dp");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                defaultSharedPreferences.edit().putInt(ICON_DISTANCE_KEY,progressChanged).commit();
                mContext.stopService(new Intent(mContext, EdgeGestureService.class));
                mContext.startService(new Intent(mContext, EdgeGestureService.class));
            }
        });

        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edgeNumber == 1) {
                    positionSpinner.setSelection(1);
                    sharedPreferences.edit().putString(EDGE_POSITION_KEY, (String) positionSpinner.getItemAtPosition(1)).commit();

                } else if (edgeNumber == 2) {
                    positionSpinner.setSelection(5);
                    sharedPreferences.edit().putString(EDGE_POSITION_KEY, (String) positionSpinner.getItemAtPosition(5)).commit();
                }
                sharedPreferences.edit().putInt(EDGE_OFFSET_KEY, 0).commit();
                offsetSeekBar.setProgress(150);
                modeSpinner.setSelection(0);
                sharedPreferences.edit().putBoolean(IS_ONLY_FAVORITE_KEY,false).commit();
                sensitiveSeekBar.setProgress(7);
                sharedPreferences.edit().putInt(EDGE_SENSIIVE_KEY, 12).commit();
                lengthSeekBar.setProgress(75);
                sharedPreferences.edit().putInt(EDGE_LENGTH_KEY, 150).commit();
                circleSizeSeekBar.setProgress(15);
                defaultSharedPreferences.edit().putInt(ICON_DISTANCE_KEY,110).commit();
                mContext.stopService(new Intent(mContext, EdgeGestureService.class));
                mContext.startService(new Intent(mContext, EdgeGestureService.class));

            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext =  getActivity();

        mScale = getResources().getDisplayMetrics().density;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(LOG_TAG, sharedPreferences.getString(EDGE_POSITION_KEY, "null") + "\n" + sharedPreferences.getInt(EDGE_SENSIIVE_KEY, 0) + "\n" + sharedPreferences.getInt(EDGE_LENGTH_KEY, 0));
    }

    private void updateEdgeView(){
        int spinnerCurrentPosition = 1;
        if (edgeNumber == 2) spinnerCurrentPosition = 5;
        int currentLength = sharedPreferences.getInt(EDGE_LENGTH_KEY ,150);
        int currentSensitive = sharedPreferences.getInt(EDGE_SENSIIVE_KEY, 12);
        int currentOffset = sharedPreferences.getInt(EDGE_OFFSET_KEY,0);

        for (int i =0; i<spinnerEntries.length; i++) {
            if (spinnerEntries[i].equals(sharedPreferences.getString(EDGE_POSITION_KEY, spinnerEntries[spinnerCurrentPosition]))) {
                spinnerCurrentPosition = i;
            }
        }
        CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(edgeImage.getLayoutParams());

        if (Utility.getPositionIntFromString(sharedPreferences.getString(EdgeSettingDialogFragment.EDGE_POSITION_KEY, spinnerEntries[1]), mContext) >= 30){
            lp.width = (int) (currentLength * mScale);
            lp.height = (int) (currentSensitive* mScale);
            if (currentOffset > 0) {
                lp.rightMargin = (int) (currentOffset * mScale);
            } else {
                lp.leftMargin = (int) (-currentOffset * mScale);
            }
        }else {
            lp.width = (int) (currentSensitive * mScale);
            lp.height = (int) (currentLength *mScale);
            if (currentOffset > 0) {
                lp.bottomMargin = (int) (currentOffset * mScale);
            } else {
                lp.topMargin = (int) (-currentOffset * mScale);
            }
        }
        switch (spinnerCurrentPosition){
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
            case 6:
                lp.gravity = Gravity.BOTTOM | Gravity.CENTER;
                break;

        }
        edgeImage.setLayoutParams(lp);


    }
}
