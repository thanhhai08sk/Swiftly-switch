package org.de_studio.recentappswitcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.de_studio.recentappswitcher.favoriteShortcut.SetFavoriteShortcutActivity;
import org.de_studio.recentappswitcher.service.EdgeGestureService;
import org.de_studio.recentappswitcher.service.EdgeSettingDialogFragment;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String EDGE_1_SHAREDPREFERENCE = "org.de_studio.recentappswitcher_edge_1_shared_preference";
    public static final String EDGE_2_SHAREDPREFERENCE = "org.de_studio.recentappswitcher_edge_2_shared_preference";
    public static final String DEFAULT_SHAREDPREFERENCE = "org.de_studio.recentappswitcher_sharedpreferences";
    public static final String FAVORITE_SHAREDPREFERENCE = "org.de_studio.recentappswitcher_favorite_shared_preferences";
    public static final String EXCLUDE_SHAREDPREFERENCE = "org.de_studio.recentappswitcher_exclude_shared_preferences";
    public static final String PRO_VERSION_PACKAGE_NAME = "org.de_studio.recentappswitcher.pro";
    public static final String FREE_VERSION_PACKAGE_NAME = "org.de_studio.recentappswitcher.trial";
    public static final String ACTION_HOME = "home";
    public static final String ACTION_BACK = "back";
    public static final String ACTION_NOTI = "noti";
    public static final String ACTION_WIFI = "wifi";
    public static final String ACTION_BLUETOOTH = "bluetooth";
    public static final String ACTION_NONE = "none";
    public static final int REQUEST_CODE = 3243;
    public static final long trialTime = 1000*60*60*24*7;
    private SharedPreferences sharedPreferences1, sharedPreferences2, sharedPreferencesDefautl,sharedPreferences_favorite, sharedPreferences_exclude;
    private ArrayList<AppInfors> mAppInforsArrayList;
    Button step1Button;
    TextView step1Text;
    TextView step2Text;
    Button step2Button;
    Button step1GoToSettingButton, step2GoToSettingButton;
    TextView descriptionText;
    float mScale;
    private boolean isTrial = false, isOutOfTrial = false;
    private long trialTimePass, beginTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getPackageName().equals(FREE_VERSION_PACKAGE_NAME)) isTrial = true;
        setContentView(R.layout.activity_main);
        mScale = getResources().getDisplayMetrics().density;
        sharedPreferences1 = getSharedPreferences(EDGE_1_SHAREDPREFERENCE, 0);
        sharedPreferences2 = getSharedPreferences(EDGE_2_SHAREDPREFERENCE, 0);
        sharedPreferencesDefautl = getSharedPreferences(DEFAULT_SHAREDPREFERENCE, 0);
        sharedPreferences_favorite = getSharedPreferences(FAVORITE_SHAREDPREFERENCE,0);
        sharedPreferences_exclude = getSharedPreferences(EXCLUDE_SHAREDPREFERENCE,0);
        beginTime = sharedPreferencesDefautl.getLong(EdgeSettingDialogFragment.BEGIN_DAY_KEY,0);
        if (beginTime ==0){
            sharedPreferencesDefautl.edit().putLong(EdgeSettingDialogFragment.BEGIN_DAY_KEY,System.currentTimeMillis()).commit();
            beginTime = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - beginTime > trialTime) isOutOfTrial = true;
        Button buyProButton = (Button) findViewById(R.id.main_buy_pro_button);
        if (isTrial) buyProButton.setVisibility(View.VISIBLE);
        buyProButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("mbarket://details?id=" + PRO_VERSION_PACKAGE_NAME);
                Intent gotoMarket = new Intent(Intent.ACTION_VIEW, uri);
                gotoMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(gotoMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + PRO_VERSION_PACKAGE_NAME)));
                }
            }
        });
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.main_app_bar_layout);
        if (!sharedPreferencesDefautl.getBoolean(EdgeSettingDialogFragment.HAS_REACT_FOR_VOTE_KEY, false)){
            int timeOpen = sharedPreferencesDefautl.getInt(EdgeSettingDialogFragment.APP_OPEN_TIME_KEY,0);
            sharedPreferencesDefautl.edit().putInt(EdgeSettingDialogFragment.APP_OPEN_TIME_KEY, timeOpen +1).commit();
            if (timeOpen >=8){

                final LinearLayout doYouLoveLinearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.do_you_love_this_app, null);
                appBarLayout.addView(doYouLoveLinearLayout);
                Button yesButton = (Button) doYouLoveLinearLayout.findViewById(R.id.main_do_you_love_yes_button);
                Button noButton = (Button) doYouLoveLinearLayout.findViewById(R.id.main_do_you_love_no_button);
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.HAS_REACT_FOR_VOTE_KEY,true).commit();
                        appBarLayout.removeView(doYouLoveLinearLayout);
                    }
                });
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(R.string.please_vote_for_this_app)
                                .setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.HAS_REACT_FOR_VOTE_KEY,true).commit();
                                        appBarLayout.removeView(doYouLoveLinearLayout);
                                        Uri uri = Uri.parse("mbarket://details?id=" + getPackageName());
                                        Intent gotoMarket = new Intent(Intent.ACTION_VIEW, uri);
                                        gotoMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                        try {
                                            startActivity(gotoMarket);
                                        } catch (ActivityNotFoundException e) {
                                            startActivity(new Intent(Intent.ACTION_VIEW,
                                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.HAS_REACT_FOR_VOTE_KEY,true).commit();
                                        appBarLayout.removeView(doYouLoveLinearLayout);
                                        // d
                                    }
                                });
                        builder.show();
                    }
                });
            }
        }

        descriptionText = (TextView) findViewById(R.id.main_description_text_view);
        step1Button = (Button) findViewById(R.id.step1_button);
        Switch edge1Switch = (Switch) findViewById(R.id.edge_1_switch);
        Switch edge2Switch = (Switch) findViewById(R.id.edge_2_switch);
        Switch disableHapticSwitch = (Switch) findViewById(R.id.main_disable_haptic_feedback_switch);
        disableHapticSwitch.setChecked(sharedPreferencesDefautl.getBoolean(EdgeSettingDialogFragment.DISABLE_HAPTIC_FEEDBACK_KEY,false));
        edge1Switch.setChecked(sharedPreferences1.getBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, true));
        edge2Switch.setChecked(sharedPreferences2.getBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, false));
        if (isTrial){
            edge2Switch.setChecked(false);
        }
        edge1Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                sharedPreferences1.edit().putBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, isChecked).commit();

                stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                startService(new Intent(getApplicationContext(), EdgeGestureService.class));

            }
        });
        edge2Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isTrial) {
                    buttonView.setChecked(false);
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.main_edge_switch_2_trial_dialog_message)
                            .setPositiveButton(R.string.main_edge_switch_2_trial_buy_pro_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri uri = Uri.parse("mbarket://details?id=" + PRO_VERSION_PACKAGE_NAME);
                                    Intent gotoMarket = new Intent(Intent.ACTION_VIEW, uri);
                                    gotoMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                    try {
                                        startActivity(gotoMarket);
                                    } catch (ActivityNotFoundException e) {
                                        startActivity(new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("http://play.google.com/store/apps/details?id=" + PRO_VERSION_PACKAGE_NAME)));
                                    }
                                }
                            })
                            .setNegativeButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            });
                    builder.show();


                } else {
                    sharedPreferences2.edit().putBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, isChecked).commit();
                    stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                    startService(new Intent(getApplicationContext(), EdgeGestureService.class));
                }


            }
        });
        disableHapticSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.DISABLE_HAPTIC_FEEDBACK_KEY,isChecked).commit();
            }
        });
        step2Button = (Button) findViewById(R.id.step2_button);
        step1Text = (TextView) findViewById(R.id.step_1_text);
        step2Text = (TextView) findViewById(R.id.step_2_text);
        step1GoToSettingButton = (Button) findViewById(R.id.step1_go_to_setting_button);
        step2GoToSettingButton = (Button) findViewById(R.id.step2_go_to_setting_button);
        ImageButton favoriteInfoButton = (ImageButton) findViewById(R.id.main_favorite_info_image_button);
        ImageButton excludeInfoButton = (ImageButton) findViewById(R.id.main_exclude_info_image_button);
        final FrameLayout stepTextFrame = (FrameLayout) findViewById(R.id.step_text_frame_layout);
        ImageButton edge1SettingButton = (ImageButton) findViewById(R.id.edge_1_setting_image_button);
        ImageButton edge2SettingButton = (ImageButton) findViewById(R.id.edge_2_setting_image_button);
        ImageButton iconPackSettingButton = (ImageButton) findViewById(R.id.main_icon_pack_support_setting_button);
        edge1SettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentManager fragmentManager = getSupportFragmentManager();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                EdgeSettingDialogFragment newFragment = new EdgeSettingDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(EdgeSettingDialogFragment.EDGE_NUMBER_KEY, 1);
                newFragment.setArguments(bundle);
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
                android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, newFragment)
                        .addToBackStack(null).commit();

            }
        });
        edge2SettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentManager fragmentManager = getSupportFragmentManager();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                EdgeSettingDialogFragment newFragment = new EdgeSettingDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(EdgeSettingDialogFragment.EDGE_NUMBER_KEY, 2);
                newFragment.setArguments(bundle);
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
                android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, newFragment)
                        .addToBackStack(null).commit();

            }
        });

        iconPackSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.FragmentManager fragmentManager = getFragmentManager();
                IconPackSettingDialogFragment newFragment = new IconPackSettingDialogFragment();
                newFragment.show(fragmentManager, "iconPackDialogFragment");
            }
        });


        setStepButtonAndDescription();


        step1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step1Text.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (isStep1Ok()){
                        step2GoToSettingButton.setVisibility(View.GONE);
                    }else step1GoToSettingButton.setVisibility(View.VISIBLE);

                }
                step2Text.setVisibility(View.GONE);
                step2GoToSettingButton.setVisibility(View.GONE);
                stepTextFrame.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.text_board_1));
            }
        });
        step2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step1Text.setVisibility(View.GONE);
                step1GoToSettingButton.setVisibility(View.GONE);
                step2Text.setVisibility(View.VISIBLE);
                if (Utility.isAccessibilityEnable(getApplicationContext())) {
                    step2GoToSettingButton.setVisibility(View.GONE);
                } else step2GoToSettingButton.setVisibility(View.VISIBLE);

                stepTextFrame.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.text_board_2_));
            }
        });

        step1GoToSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
        });
        step2GoToSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });
        if  (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1){
            startService(new Intent(this, EdgeGestureService.class));

        }else {
            checkDrawOverlayPermission();

            if (Settings.canDrawOverlays(this)){
                startService(new Intent(this, EdgeGestureService.class));

            }
        }


        ImageButton addFavoriteButton = (ImageButton) findViewById(R.id.main_favorite_add_app_image_button);
        addFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SetFavoriteShortcutActivity.class));
            }
        });

        ImageButton editExcludeButton = (ImageButton) findViewById(R.id.main_exclude_edit_image_button);
        editExcludeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentManager fragmentManager = getSupportFragmentManager();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                FavoriteOrExcludeDialogFragment newFragment = new FavoriteOrExcludeDialogFragment();
                    newFragment.show(fragmentManager, "excludeDialogFragment");
            }
        });
        step2GoToSettingButton.setVisibility(View.GONE);


        favoriteInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
//                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle(getString(R.string.main_favorite_info_title))
                        .setMessage(R.string.main_favorite_app_info_text)
                        .setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        });
                builder.show();
            }
        });
        excludeInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.main_exclude_info_title))
                        .setMessage(R.string.main_exclude_app_info)
                        .setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        });
                builder.show();
            }
        });

    }




    @Override
    protected void onResume() {
        super.onResume();
        setStepButtonAndDescription();
        step2GoToSettingButton.setVisibility(View.GONE);
    }

    public void showDialog() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
        EdgeSettingDialogFragment newFragment = new EdgeSettingDialogFragment();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        transaction.add(android.R.id.content, newFragment)
//                .addToBackStack(null).commit();

        android.app.FragmentManager fragmentManager1 = getFragmentManager();
        android.app.FragmentTransaction transaction1 = fragmentManager1.beginTransaction();
        transaction1.setTransition(android.app.FragmentTransaction.TRANSIT_ENTER_MASK);
        transaction1.add(android.R.id.content,newFragment).addToBackStack(null).commit();
    }



    public void setStepButtonAndDescription(){
        boolean isStep1Ok;
        boolean isStep2Ok = Utility.isAccessibilityEnable(this);
        Log.e(LOG_TAG, "isStep2OK = " + isStep2Ok);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            isStep1Ok = true;
            step1Text.setText(R.string.main_step1_text_for_kitkat_and_below);
            step1Text.setPadding(0, 0, 0, 0);
            step1GoToSettingButton.setVisibility(View.GONE);

        } else {
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), getPackageName());
            isStep1Ok = mode == AppOpsManager.MODE_ALLOWED;
        }
        if (isStep1Ok) {
            step1Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_green));
            step1GoToSettingButton.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                step1Text.setText(R.string.main_step1_text_success);
            }

        } else {
            step1Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_red));
            step1Text.setText(R.string.main_step1_information);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                step1GoToSettingButton.setVisibility(View.VISIBLE);
            }
        }

        if (isStep2Ok) {
            step2Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_green));
            step2Text.setText(R.string.main_step2_text_success);
            step2GoToSettingButton.setVisibility(View.GONE);

        } else {
            step2Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_red));
            step2Text.setText(R.string.main_step2_information);
            step2GoToSettingButton.setVisibility(View.VISIBLE);
        }

        if (isStep1Ok & isStep2Ok){
            descriptionText.setText("");
            descriptionText.setVisibility(View.GONE);
        }
    }



    private boolean isStep1Ok(){
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), getPackageName());
        return  mode == AppOpsManager.MODE_ALLOWED;

    }

    public void checkDrawOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                // continue here - permission was granted
                startService(new Intent(this, EdgeGestureService.class));

            }
        }
    }

}
