package org.de_studio.recentappswitcher.service;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;

/**
 * Created by hai on 1/2/2016.
 */
public class EdgeSetting extends DialogFragment {
    private static final String LOG_TAG = EdgeSetting.class.getSimpleName();
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
    public static final String DISABLE_INDICATOR = "disable_indicator";
    public static final String ANIMATION_KEY = "disable_background_animation";
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
    public static final String GRID_DISTANCE_VERTICAL_FROM_EDGE_KEY = "grid_distance_vertical_from_edge";
    public static final String IS_ONLY_FAVORITE_KEY = "is_only_favorite";
    public static final String ICON_PACK_PACKAGE_NAME_KEY = "icon_pack_packa";
    public static final String ACTION_1_KEY = "action_1";
    public static final String ACTION_2_KEY = "action_2";
    public static final String ACTION_3_KEY = "action_3";
    public static final String ACTION_4_KEY = "action_4";
    public static final String IS_ACTIONS_STAY_PERMANENT = "is_permanent";
    public static final String VIBRATION_DURATION_KEY = "vibration_duration";
    public static final String HOLD_TIME_KEY  = "hold_time";
    public static final String ANI_TIME_KEY = "animation_time";
    public static final String IS_PIN_TO_TOP_KEY = "is_pin_to_top";
    public static final String BACKGROUND_COLOR_KEY = "background_color";
    public static final String GUIDE_COLOR_KEY = "guide_color";
    public static final String USE_GUIDE_KEY = "edge_guide";
    public static final String HAS_TELL_ABOUT_TRIAL_LIMIT = "has_tell_about_trial_limit";
    public static final String CONTACT_ACTION = "contact_action";
    public static final String CIRCLE_FAVORITE_MODE = "circle_fovorite_mode";
    public static final String SERVICE_ID = "service_id";
    public static final String ICON_SCALE = "icon_scale";
    public static final int ACTION_CHOOSE = 0;
    public static final int ACTION_CALL = 1;
    public static final int ACTION_SMS = 2;
    private static int edgeNumber;
    private  float mScale;
    private static SharedPreferences sharedPreferences,defaultSharedPreferences;
    private ViewGroup.LayoutParams mEdgeParas;
    private Context mContext;
    private String[] spinnerEntries;
    private float edgeInitX,edgeInitY;
    private int screenHeight;
    private int screenWidth;
    private FrameLayout edgeParent;
    private View tempEdge;
    private int statusbarHetght;

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
        spinnerEntries = getResources().getStringArray(R.array.edge_dialog_spinner_array);
//        caculateEdgeInit();
        Log.e(LOG_TAG,"edge number = " + edgeNumber);
        if (sharedPreferences == null){
            Log.e(LOG_TAG, "sharedPreference == null at onCreateview");
        }

        mScale = getResources().getDisplayMetrics().density;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;


        View rootView = inflater.inflate(R.layout.edge_setting_dialog, container, false);
        edgeParent = (FrameLayout) rootView.findViewById(R.id.edge_parent);
        AppCompatButton defaultButton = (AppCompatButton) rootView.findViewById(R.id.edge_dialog_default_button);
        AppCompatButton okButton = (AppCompatButton) rootView.findViewById(R.id.edge_dialog_ok_button);
        statusbarHetght = getStatusBarHeight();
        int currentLength = sharedPreferences.getInt(EDGE_LENGTH_KEY ,150);
        int currentOffset = sharedPreferences.getInt(EDGE_OFFSET_KEY, 0);
        int currentSensitive = sharedPreferences.getInt(EDGE_SENSIIVE_KEY, 12);
        int currentCircleSize = defaultSharedPreferences.getInt(ICON_DISTANCE_KEY,105);
        final TextView sensitiveNumberTextView = (TextView) rootView.findViewById(R.id.edge_dialog_sensitive_number_text);
        final AppCompatSpinner positionSpinner = (AppCompatSpinner) rootView.findViewById(R.id.edge_dialog_position_spinner);
        final AppCompatSpinner modeSpinner = (AppCompatSpinner) rootView.findViewById(R.id.edge_dialog_mode_spinner);
        final CheckBox showGuideCheckBox = (AppCompatCheckBox) rootView.findViewById(R.id.edge_dialog_show_guide_checkbox);
        int mode = sharedPreferences.getInt(EdgeSetting.CIRCLE_FAVORITE_MODE, 0);
        boolean isOnlyFavorite = sharedPreferences.getBoolean(EdgeSetting.IS_ONLY_FAVORITE_KEY, false);
        if (mode == 0) {
            if (isOnlyFavorite) {
                mode = 2;
            } else {
                mode = 1;
            }
        }
        modeSpinner.setSelection(mode-1);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        sharedPreferences.edit().putBoolean(EdgeSetting.IS_ONLY_FAVORITE_KEY, false)
                                .putInt(EdgeSetting.CIRCLE_FAVORITE_MODE,1).commit();
                        break;
                    case 1:
                        sharedPreferences.edit().putBoolean(EdgeSetting.IS_ONLY_FAVORITE_KEY, true)
                                .putInt(EdgeSetting.CIRCLE_FAVORITE_MODE, 2).commit();
                        break;
                    case 2:
                        sharedPreferences.edit().putInt(EdgeSetting.CIRCLE_FAVORITE_MODE, 3).commit();
                }
                mContext.stopService(new Intent(mContext, EdgeGestureService.class));
                mContext.startService(new Intent(mContext, EdgeGestureService.class));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        showGuideCheckBox.setChecked(sharedPreferences.getBoolean(EdgeSetting.USE_GUIDE_KEY, true));
        showGuideCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(USE_GUIDE_KEY,isChecked).commit();
                mContext.stopService(new Intent(mContext, EdgeGestureService.class));
                mContext.startService(new Intent(mContext, EdgeGestureService.class));
            }
        });
        int spinnerCurrentPosition =1;
        if (edgeNumber == 2) spinnerCurrentPosition = 5;
        for (int i =0; i<spinnerEntries.length; i++) {
            if (spinnerEntries[i].equals(sharedPreferences.getString(EDGE_POSITION_KEY, spinnerEntries[spinnerCurrentPosition]))) {
                spinnerCurrentPosition = i;
            }
        }
        positionSpinner.setSelection(spinnerCurrentPosition, true);
        positionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(edgeImage.getLayoutParams());

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                sharedPreferences.edit().putString(EDGE_POSITION_KEY, item).commit();


//                int currentLength = sharedPreferences.getInt(EDGE_LENGTH_KEY ,150);
//                int currentSensitive = sharedPreferences.getInt(EDGE_SENSIIVE_KEY, 12);
//
//                CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(edgeImage.getLayoutParams());
//
//                if (Utility.getPositionIntFromString(sharedPreferences.getString(EdgeSetting.EDGE_POSITION_KEY, spinnerEntries[1]), mContext) >= 30){
//                    lp.width = (int) (currentLength * mScale);
//                    lp.height = (int) (currentSensitive* mScale);
//                }else {
//                    lp.width = (int) (currentSensitive * mScale);
//                    lp.height = (int) (currentLength *mScale);
//                }
//                switch (position) {
//                    case 0:
//                        lp.gravity = Gravity.TOP | Gravity.RIGHT;
//                        break;
//                    case 1:
//                        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
//                        break;
//                    case 2:
//                        lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
//                        break;
//                    case 3:
//                        lp.gravity = Gravity.TOP | Gravity.LEFT;
//                        break;
//                    case 4:
//                        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
//                        break;
//                    case 5:
//                        lp.gravity = Gravity.BOTTOM | Gravity.LEFT;
//                        break;
//                    case 6:
//                        lp.gravity = Gravity.BOTTOM | Gravity.CENTER;
//                        break;
//                }
//                edgeImage.setLayoutParams(lp);
                updateEdgeView();
//                edgeImage.setX(100);
//                edgeImage.setY(2300);
                mContext.stopService(new Intent(mContext, EdgeGestureService.class));
                mContext.startService(new Intent(mContext, EdgeGestureService.class));
//
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
//                edgeParas = edgeImage.getLayoutParams();
//                if (Utility.getPositionIntFromString(sharedPreferences.getString(EdgeSetting.EDGE_POSITION_KEY, spinnerEntries[1]), mContext) >= 30) {
//                    edgeParas.height = (int) (progressChanged * mScale);
//                } else edgeParas.width = (int) (progressChanged * mScale);
//                edgeImage.setLayoutParams(edgeParas);
                sharedPreferences.edit().putInt(EDGE_SENSIIVE_KEY, progressChanged).commit();
                updateEdgeView();
                sensitiveNumberTextView.setText(progressChanged + "dp");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                sharedPreferences.edit().putInt(EDGE_SENSIIVE_KEY, progressChanged).commit();
//                updateEdgeView();
                mContext.stopService(new Intent(mContext, EdgeGestureService.class));
                mContext.startService(new Intent(mContext, EdgeGestureService.class));
            }
        });
        final AppCompatSeekBar lengthSeekBar = (AppCompatSeekBar) rootView.findViewById(R.id.length_seek_bar);
        lengthSeekBar.setProgress(currentLength - 40);
        final TextView edgeLengthNumberText = (TextView) rootView.findViewById(R.id.edge_dialog_length_number_view);
        edgeLengthNumberText.setText(currentLength+ "dp");
        lengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged; // 40 to 200
            ViewGroup.LayoutParams edgeParas;
            String[] spinnerEntries = getResources().getStringArray(R.array.edge_dialog_spinner_array);

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress + 40;
//                edgeParas = tempEdge.getLayoutParams();
//
//                if (Utility.getPositionIntFromString(sharedPreferences.getString(EdgeSetting.EDGE_POSITION_KEY, spinnerEntries[1]), mContext) >= 30) {
//                    edgeParas.width = (int) (progressChanged * mScale);
//                } else edgeParas.height = (int) (progressChanged * mScale);
//
//                tempEdge.setLayoutParams(edgeParas);
                edgeLengthNumberText.setText(progressChanged + "dp");
                sharedPreferences.edit().putInt(EDGE_LENGTH_KEY, progressChanged).commit();
                updateEdgeView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                sharedPreferences.edit().putInt(EDGE_LENGTH_KEY, progressChanged).commit();
                mContext.stopService(new Intent(mContext, EdgeGestureService.class));
                mContext.startService(new Intent(mContext, EdgeGestureService.class));
            }
        });




        final AppCompatSeekBar offsetSeekBar = (AppCompatSeekBar) rootView.findViewById(R.id.edge_dialog_offset_seek_bar);
        offsetSeekBar.setProgress(currentOffset + 300);
        final TextView edgeOffsetNumberText = (TextView) rootView.findViewById(R.id.edge_dialog_offset_number_view);
        edgeOffsetNumberText.setText(currentOffset+ "dp");
        offsetSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged; // -150 to 150
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress - 300;
                edgeOffsetNumberText.setText(progressChanged + "dp");
                caculateEdgeInit();
                if (Utility.getPositionIntFromString(sharedPreferences.getString(EdgeSetting.EDGE_POSITION_KEY, spinnerEntries[1]), mContext) >= 30){
                    tempEdge.setX(edgeInitX - progressChanged*mScale);
//                    edgeImage.setY(edgeInitY);
                }else {
                    Log.e(LOG_TAG, "processChanged  = " + progressChanged);
                    tempEdge.setY(edgeInitY - progressChanged*mScale - statusbarHetght);
//                    edgeImage.setX(edgeInitX);
                }
//                updateEdgeView();


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sharedPreferences.edit().putInt(EDGE_OFFSET_KEY, progressChanged).commit();
//                updateEdgeView();
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
                offsetSeekBar.setProgress(300);
                modeSpinner.setSelection(0);
                showGuideCheckBox.setChecked(true);
                sharedPreferences.edit().putBoolean(USE_GUIDE_KEY,true).commit();
                sharedPreferences.edit().putBoolean(IS_ONLY_FAVORITE_KEY,false).commit();
                sensitiveSeekBar.setProgress(7);
                sharedPreferences.edit().putInt(EDGE_SENSIIVE_KEY, 12).commit();
                lengthSeekBar.setProgress(110);
                sharedPreferences.edit().putInt(EDGE_LENGTH_KEY, 150).commit();
                circleSizeSeekBar.setProgress(10);
                defaultSharedPreferences.edit().putInt(ICON_DISTANCE_KEY,105).commit();
                mContext.stopService(new Intent(mContext, EdgeGestureService.class));
                mContext.startService(new Intent(mContext, EdgeGestureService.class));
//                updateEdgeView();

            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        updateEdgeView();
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext =  getActivity();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(LOG_TAG, sharedPreferences.getString(EDGE_POSITION_KEY, "null") + "\n" + sharedPreferences.getInt(EDGE_SENSIIVE_KEY, 0) + "\n" + sharedPreferences.getInt(EDGE_LENGTH_KEY, 0));
    }

    private synchronized   void updateEdgeView(){
        Log.e(LOG_TAG, "updateEdgeView");
        edgeParent.removeAllViews();
        tempEdge = new View(mContext);
        tempEdge.setBackgroundResource(R.color.colorAccent);
        int spinnerCurrentPosition = 1;
        if (edgeNumber == 2) spinnerCurrentPosition = 5;
        int currentLength = sharedPreferences.getInt(EDGE_LENGTH_KEY ,150);
        int currentSensitive = sharedPreferences.getInt(EDGE_SENSIIVE_KEY, 12);
        int currentOffset = sharedPreferences.getInt(EDGE_OFFSET_KEY,0);
        int position = Utility.getPositionIntFromString(sharedPreferences.getString(EdgeSetting.EDGE_POSITION_KEY, spinnerEntries[1]), mContext);
        int width, height;
        if (position >= 30){
            width = (int) (currentLength * mScale);
            height = (int) (currentSensitive* mScale);
        }else {
            width = (int) (currentSensitive * mScale);
            height = (int) (currentLength *mScale);

        }
        FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(width,height);
        switch (position){
            case 10:
                lp2.gravity =Gravity.RIGHT;
                break;
            case 11:
                lp2.gravity =Gravity.RIGHT;
                break;
            case 12:
                lp2.gravity =Gravity.RIGHT;
                break;
            case 20:
                lp2.gravity =Gravity.LEFT;
                break;
            case 21:
                lp2.gravity =Gravity.LEFT;
                break;
            case 22:
                lp2.gravity =Gravity.LEFT;
                break;
            case 31:
                lp2.gravity = Gravity.BOTTOM;
                break;

        }

        Log.e(LOG_TAG, "updateImageView curruntPosition = " + spinnerCurrentPosition);
        caculateEdgeInit();
        edgeParent.addView(tempEdge,lp2);
        if (position >= 30){
            tempEdge.setX(edgeInitX - currentOffset*mScale);
//            edgeImage.setY(edgeInitY - 100);

        }else {
            tempEdge.setY(edgeInitY - currentOffset*mScale - statusbarHetght);
//            edgeImage.setX(edgeInitX);
        }
//        edgeImage.setLayoutParams(lp2);






    }

    private void caculateEdgeInit() {
        int currentLength = sharedPreferences.getInt(EDGE_LENGTH_KEY ,150);
        int currentSensitive = sharedPreferences.getInt(EDGE_SENSIIVE_KEY, 12);
        int width,height;
        if (Utility.getPositionIntFromString(sharedPreferences.getString(EdgeSetting.EDGE_POSITION_KEY, spinnerEntries[1]), mContext) >= 30){
            width = (int) (currentLength * mScale);
            height = (int) (currentSensitive* mScale);

        }else {
            width = (int) (currentSensitive * mScale);
            height = (int) (currentLength *mScale);

        }
        int position = Utility.getPositionIntFromString(sharedPreferences.getString(EdgeSetting.EDGE_POSITION_KEY, spinnerEntries[1]), mContext);
        switch (position) {
            case 10:
                edgeInitX = screenWidth - width;
                edgeInitY = 0;
                break;
            case 11:
                edgeInitX = screenWidth - width;
                edgeInitY = screenHeight/2 - height/2;
                break;
            case 12:
                edgeInitX = screenWidth - width;
                edgeInitY = screenHeight - height;
                break;
            case 20:
                edgeInitX = 0;
                edgeInitY = 0;
                break;
            case 21:
                edgeInitX = 0;
                edgeInitY = screenHeight/2 - height/2;
                break;
            case 22:
                edgeInitX = 0;
                edgeInitY = screenHeight - height;
                break;
            case 31:
                edgeInitX = screenWidth/2 - width/2;
                edgeInitY = screenHeight - height;
                break;
        }
//        Log.e(TAG, "caculate " +
//                "\nedgeInitX = " + edgeInitX+
//                "\nedgeInitY = " + edgeInitY +
//                "\nheight = " + height +
//                "\nwidth = " + width +
//                "\nscreenHeight = " + screenHeight +
//                "\nscreenWidth = " + screenWidth);
    }


    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
