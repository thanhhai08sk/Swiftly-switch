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
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.SwitchCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import org.de_studio.recentappswitcher.favoriteShortcut.FavoriteSettingActivity;
import org.de_studio.recentappswitcher.intro.IntroActivity;
import org.de_studio.recentappswitcher.service.EdgeGestureService;
import org.de_studio.recentappswitcher.service.EdgeSetting;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final int VERSION_NUMBER = 61;
    private static final String TAG = MainActivity.class.getSimpleName();
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
    public static final String ACTION_LAST_APP = "last_app";
    public static final String ACTION_ROTATE = "rotate";
    public static final String ACTION_POWER_MENU = "power_menu";
    public static final String ACTION_CALL_LOGS = "call_logs";
    public static final String ACTION_CONTACT = "contacts";
    public static final String ACTION_DIAL = "dial";
    public static final String ACTION_RECENT = "recent";
    public static final String ACTION_INSTANT_FAVO = "instant_favo";
    public static final String ACTION_VOLUME = "volume";
    public static final String ACTION_BRIGHTNESS = "brightness";
    public static final String ACTION_RINGER_MODE = "ringer_mode";
    public static final int REQUEST_CODE = 3243;
    public static final int REQUEST_INVITE = 232;
    public static final long trialTime = 1000 * 60 * 60 * 24 * 14;
    private SharedPreferences sharedPreferences1, sharedPreferences2, sharedPreferencesDefautl, sharedPreferences_favorite, sharedPreferences_exclude;
    private ArrayList<AppInfors> mAppInforsArrayList;
//    private Button step1Button;
//    private TextView step1Text;
//    private Button step2Button;
    private boolean isTrial = false, isOutOfTrial = false;
    private long trialTimePass, beginTime;
    private int step = 1;
    private LinearLayout permissionMissing;
    private SwitchCompat edge1Switch;
    private SwitchCompat edge2Switch;
    private boolean isFirstStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run Intro");
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                isFirstStart = getPrefs.getBoolean("firstStart", true);
                if (isFirstStart) {
                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean("firstStart", false);
                    e.commit();
                    Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(i);

                }
            }
        });
        t.start();
        Log.e(TAG, "after Intro");
//        Intent i = new Intent(MainActivity.this, IntroActivity.class);
//        startActivity(i);
        if (getPackageName().equals(FREE_VERSION_PACKAGE_NAME)) isTrial = true;
        setContentView(R.layout.activity_main);
        permissionMissing = (LinearLayout) findViewById(R.id.permission_missing);
        Button getProButton = (Button) findViewById(R.id.get_pro);
        Switch globalSwitch = (Switch) findViewById(R.id.global_switch);
        sharedPreferences1 = getSharedPreferences(EDGE_1_SHAREDPREFERENCE, 0);
        sharedPreferences2 = getSharedPreferences(EDGE_2_SHAREDPREFERENCE, 0);
        sharedPreferencesDefautl = getSharedPreferences(DEFAULT_SHAREDPREFERENCE, 0);
        sharedPreferences_favorite = getSharedPreferences(FAVORITE_SHAREDPREFERENCE, 0);
        sharedPreferences_exclude = getSharedPreferences(EXCLUDE_SHAREDPREFERENCE, 0);
        beginTime = sharedPreferencesDefautl.getLong(EdgeSetting.BEGIN_DAY_KEY, 0);
        if (beginTime == 0) {
            sharedPreferencesDefautl.edit().putLong(EdgeSetting.BEGIN_DAY_KEY, System.currentTimeMillis()).commit();
            beginTime = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - beginTime > trialTime) isOutOfTrial = true;
//        Button buyProButton = (Button) findViewById(R.id.main_buy_pro_button);
//        if (isTrial) buyProButton.setVisibility(View.VISIBLE);
//        buyProButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri uri = Uri.parse("mbarket://details?id=" + PRO_VERSION_PACKAGE_NAME);
//                Intent gotoMarket = new Intent(Intent.ACTION_VIEW, uri);
//                gotoMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
//                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                try {
//                    startActivity(gotoMarket);
//                } catch (ActivityNotFoundException e) {
//                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("http://play.google.com/store/apps/details?id=" + PRO_VERSION_PACKAGE_NAME)));
//                }
//            }
//        });
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.main_app_bar_layout);
        if (isTrial) {
            getProButton.setVisibility(View.VISIBLE);
        }else getProButton.setVisibility(View.GONE);
        getProButton.setOnClickListener(new View.OnClickListener() {
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

        globalSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(new Intent(getApplicationContext(), EdgeGestureService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                }
            }
        });

        if (!sharedPreferencesDefautl.getBoolean(EdgeSetting.HAS_REACT_FOR_VOTE_KEY, false)) {
            int timeOpen = sharedPreferencesDefautl.getInt(EdgeSetting.APP_OPEN_TIME_KEY, 0);
            sharedPreferencesDefautl.edit().putInt(EdgeSetting.APP_OPEN_TIME_KEY, timeOpen + 1).commit();
            if (timeOpen >= 4) {

                final LinearLayout doYouLoveLinearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.do_you_love_this_app, null);
                appBarLayout.addView(doYouLoveLinearLayout);
                Button yesButton = (Button) doYouLoveLinearLayout.findViewById(R.id.main_do_you_love_yes_button);
                Button noButton = (Button) doYouLoveLinearLayout.findViewById(R.id.main_do_you_love_no_button);
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPreferencesDefautl.edit().putBoolean(EdgeSetting.HAS_REACT_FOR_VOTE_KEY, true).commit();
                        appBarLayout.removeView(doYouLoveLinearLayout);
                    }
                });
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPreferencesDefautl.edit().putBoolean(EdgeSetting.HAS_REACT_FOR_VOTE_KEY, true).commit();
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
                });


//                yesButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
//                        builder.setMessage(R.string.please_vote_for_this_app)
//                                .setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        sharedPreferencesDefautl.edit().putBoolean(EdgeSetting.HAS_REACT_FOR_VOTE_KEY, true).commit();
//                                        appBarLayout.removeView(doYouLoveLinearLayout);
//                                        Uri uri = Uri.parse("mbarket://details?id=" + getPackageName());
//                                        Intent gotoMarket = new Intent(Intent.ACTION_VIEW, uri);
//                                        gotoMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
//                                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                                        try {
//                                            startActivity(gotoMarket);
//                                        } catch (ActivityNotFoundException e) {
//                                            startActivity(new Intent(Intent.ACTION_VIEW,
//                                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
//                                        }
//                                    }
//                                })
//                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        sharedPreferencesDefautl.edit().putBoolean(EdgeSetting.HAS_REACT_FOR_VOTE_KEY, true).commit();
//                                        appBarLayout.removeView(doYouLoveLinearLayout);
//                                        // d
//                                    }
//                                })
//                                .setNeutralButton(R.string.share, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        shareFriend();
//                                    }
//                                });
//                        builder.show();
//                    }
//                });
            }
        }

//        step1Button = (Button) findViewById(R.id.step1_button);
        edge1Switch = (SwitchCompat) findViewById(R.id.edge_1_switch);
        edge2Switch = (SwitchCompat) findViewById(R.id.edge_2_switch);
//        Switch hapticFeedbackOnTriggerSwitch = (Switch) findViewById(R.id.main_disable_haptic_feedback_switch);
//        Switch hapticFeedbackOnItemSwitch = (Switch) findViewById(R.id.main_haptic_feedback_on_item_switch);
//        Switch disableClockSwitch = (Switch) findViewById(R.id.main_disable_clock_switch);
//        Switch disableAnimationSwitch = (Switch) findViewById(R.id.main_disable_animation_switch);
//        Switch holdTimeSwitch = (Switch) findViewById(R.id.main_hold_time_switch);
        final LinearLayout shareFriendLinearLayout = (LinearLayout) findViewById(R.id.main_share_linear_layout);
        LinearLayout reviewLinearLayout = (LinearLayout) findViewById(R.id.main_review_linear_layout);
        LinearLayout emailLinearLayout = (LinearLayout) findViewById(R.id.main_email_linear_layout);
//        hapticFeedbackOnTriggerSwitch.setChecked(!sharedPreferencesDefautl.getBoolean(EdgeSetting.DISABLE_HAPTIC_FEEDBACK_KEY, true));
//        hapticFeedbackOnItemSwitch.setChecked(sharedPreferencesDefautl.getBoolean(EdgeSetting.HAPTIC_ON_ICON_KEY,false));
//        disableClockSwitch.setChecked(sharedPreferencesDefautl.getBoolean(EdgeSetting.DISABLE_CLOCK_KEY,false));
//        disableAnimationSwitch.setChecked(sharedPreferencesDefautl.getBoolean(EdgeSetting.ANIMATION_KEY,true));
//        holdTimeSwitch.setChecked(sharedPreferencesDefautl.getBoolean(EdgeSetting.HOLD_TIME_ENABLE_KEY,true));
        edge1Switch.setChecked(sharedPreferences1.getBoolean(EdgeSetting.EDGE_ON_KEY, true));
        edge2Switch.setChecked(sharedPreferences2.getBoolean(EdgeSetting.EDGE_ON_KEY, false));
        if (isTrial) {
            edge2Switch.setChecked(false);
        }
        edge1Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                sharedPreferences1.edit().putBoolean(EdgeSetting.EDGE_ON_KEY, isChecked).commit();
                if (!isChecked && !edge2Switch.isChecked()) {
                    stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                    startService(new Intent(getApplicationContext(), EdgeGestureService.class));
                }


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
                    if (!isChecked && !edge1Switch.isChecked()) {
                        stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                    } else {
                        stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                        startService(new Intent(getApplicationContext(), EdgeGestureService.class));
                    }
                    sharedPreferences2.edit().putBoolean(EdgeSetting.EDGE_ON_KEY, isChecked).commit();

                }


            }
        });

        permissionMissing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IntroActivity.class);
                intent.putExtra("page",4);
                startActivity(intent);
            }
        });
//        hapticFeedbackOnTriggerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferencesDefautl.edit().putBoolean(EdgeSetting.DISABLE_HAPTIC_FEEDBACK_KEY, !isChecked).commit();
//            }
//        });
//        hapticFeedbackOnItemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferencesDefautl.edit().putBoolean(EdgeSetting.HAPTIC_ON_ICON_KEY,isChecked).commit();
//            }
//        });
//        disableClockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferencesDefautl.edit().putBoolean(EdgeSetting.DISABLE_CLOCK_KEY,isChecked).commit();
//            }
//        });
//        disableAnimationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferencesDefautl.edit().putBoolean(EdgeSetting.ANIMATION_KEY,isChecked).commit();
//            }
//        });
//        holdTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferencesDefautl.edit().putBoolean(EdgeSetting.HOLD_TIME_ENABLE_KEY, isChecked).commit();
//            }
//        });
//        step2Button = (Button) findViewById(R.id.step2_button);
//        step1Text = (TextView) findViewById(R.id.step_1_text);
//        step1GoToSettingButton = (Button) findViewById(R.id.step1_go_to_setting_button);
//        ImageButton favoriteInfoButton = (ImageButton) findViewById(R.id.main_favorite_info_image_button);
//        ImageButton excludeInfoButton = (ImageButton) findViewById(R.id.main_exclude_info_image_button);
//        ImageButton pinAppInfoButton = (ImageButton)findViewById(R.id.main_pin_app_info_image_button);
//        ImageButton disableAccessibilityInfoButton = (ImageButton) findViewById(R.id.main_disable_accessibility_info_image_button);
//        final FrameLayout stepTextFrame = (FrameLayout) findViewById(R.id.step_text_frame_layout);
        ImageButton edge1SettingButton = (ImageButton) findViewById(R.id.edge_1_setting_image_button);
        ImageButton edge2SettingButton = (ImageButton) findViewById(R.id.edge_2_setting_image_button);
//        ImageButton iconPackSettingButton = (ImageButton) findViewById(R.id.main_icon_pack_support_setting_button);
        GridLayout pinAppImageButton = (GridLayout) findViewById(R.id.main_pin_app_setting_button);
        pinAppImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTrial && !sharedPreferencesDefautl.getBoolean(EdgeSetting.HAS_TELL_ABOUT_TRIAL_LIMIT, false) && !isOutOfTrial) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.limit_of_free_version).
                            setPositiveButton(R.string.app_tab_fragment_ok_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getApplicationContext(), PinAppActivity.class));
                                }
                            });
                    sharedPreferencesDefautl.edit().putBoolean(EdgeSetting.HAS_TELL_ABOUT_TRIAL_LIMIT,true).apply();
                    builder.create().show();
                }else if (isTrial && isOutOfTrial) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.edge_service_out_of_trial_text_when_homebacknoti)
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
                    startActivity(new Intent(getApplicationContext(), PinAppActivity.class));
                }

            }
        });
        ImageButton outerRingSettingButton = (ImageButton) findViewById(R.id.main_outter_ring_setting_button);
//        final ImageButton vibrationDurationSettingButton = (ImageButton) findViewById(R.id.main_vibration_duration_setting_image_button);
//        ImageButton iconSizeSettingButton = (ImageButton) findViewById(R.id.main_icon_size_setting_image_button);
//        ImageButton backgroundColorSettingButton = (ImageButton) findViewById(R.id.main_background_color_setting_image_button);
//        ImageButton guideColorSettingButton = (ImageButton) findViewById(R.id.main_guide_color_setting_image_button);
//        ImageButton holdTimeSettingButton = (ImageButton) findViewById(R.id.main_hold_time_setting_image_button);
//        ImageButton animationTimeSettingButton = (ImageButton) findViewById(R.id.main_animation_time_setting_image_button);
//        ImageButton holdTimeInfoButton = (ImageButton) findViewById(R.id.main_hold_time_info_image_button);
        edge1SettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentManager fragmentManager = getSupportFragmentManager();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                EdgeSetting newFragment = new EdgeSetting();
                Bundle bundle = new Bundle();
                bundle.putInt(EdgeSetting.EDGE_NUMBER_KEY, 1);
                newFragment.setArguments(bundle);
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
                android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, newFragment)
                        .addToBackStack(null).commit();

            }
        });
        edge2SettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.FragmentManager fragmentManager = getFragmentManager();
                EdgeSetting newFragment = new EdgeSetting();
                Bundle bundle = new Bundle();
                bundle.putInt(EdgeSetting.EDGE_NUMBER_KEY, 2);
                newFragment.setArguments(bundle);
                android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, newFragment)
                        .addToBackStack(null).commit();
            }
        });
        outerRingSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(new Intent(getApplicationContext(), OuterRingSettingActivity.class));
            }
        });

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            stopService(new Intent(this, EdgeGestureService.class));
            startService(new Intent(this, EdgeGestureService.class));

        } else {
            if (Settings.canDrawOverlays(this)) {
                stopService(new Intent(this, EdgeGestureService.class));
                startService(new Intent(this, EdgeGestureService.class));

            }
        }


        ImageButton addFavoriteButton = (ImageButton) findViewById(R.id.main_favorite_add_app_image_button);
        addFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FavoriteSettingActivity.class));
            }
        });

        GridLayout editExcludeButton = (GridLayout) findViewById(R.id.main_exclude_edit_image_button);
        GridLayout moreSetting = (GridLayout) findViewById(R.id.main_more_setting);
        editExcludeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentManager fragmentManager = getSupportFragmentManager();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                BlackListDialogFragment newFragment = new BlackListDialogFragment();
                newFragment.show(fragmentManager, "excludeDialogFragment");
            }
        });

        moreSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MoreSettingActivity.class));
            }
        });
        shareFriendLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFriend();
            }
        });
        emailLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        reviewLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        checkPermissionOk();
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        int versionSaved = getPrefs.getInt("version_saved", 0);
        if (versionSaved != 0 && versionSaved < VERSION_NUMBER) {
            showWhatNew();
        }
        if (versionSaved == 0 && !isFirstStart) {
            showWhatNew();
        }


        if (versionSaved < VERSION_NUMBER) {
            getPrefs.edit().putInt("version_saved", VERSION_NUMBER).commit();
        }


    }

    private void showWhatNew() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        int titleSize = getResources().getDimensionPixelSize(R.dimen.what_new_title_size);
        String title2_2_13 = "Version 2.2.13";
        String text2_2_13 = " - App on sale 50%" +
                "\n - Fix minor bugs";
        String title2_2_12 = "Version 2.2.12";
        String text2_2_12 = " - Fix bugs" +
                "\n - Support Portuguese" +
                "\n - Now you can pin actions, shortcut, contacts to recent list";
        String title2_2_11 = "Version 2.2.11";
        String text2_2_11 = " - Fix bugs" +
                "\n - Add Hebrew language (Thanks Elior!)";
        String title2_2_10 = "Version 2.2.10";
        String text2_2_10 = " - Support Shortcut (include Tasker action)" +
                "\n - Add Sound/Vibrate shortcut" +
                "\n - Fix bugs" ;
        String title2_2_9 = "Version 2.2.9";
        String text2_2_9 = " - Add 2 more shortcuts: Screen brightness, Volume control" +
                "\n - Add option to disable the trigger zone in Landscape" +
                "\n - Fix bugs" ;
        String title2_2_8 = "Version 2.2.8";
        String text2_2_8 = " - Support contact shortcut" +
                "\n - Support folder" +
                "\n - Indicator" +
                "\n - Change the default setting for Quick Action";

        SpannableString span2_2_13 = new SpannableString(title2_2_13);
        span2_2_13.setSpan(new AbsoluteSizeSpan(titleSize),0,title2_2_13.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);


        SpannableString span2_2_11_1 = new SpannableString(title2_2_12);
        span2_2_11_1.setSpan(new AbsoluteSizeSpan(titleSize),0,title2_2_12.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString span2_2_11 = new SpannableString(title2_2_11);
        span2_2_11.setSpan(new AbsoluteSizeSpan(titleSize),0,title2_2_11.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString span2_2_10 = new SpannableString(title2_2_10);
        span2_2_10.setSpan(new AbsoluteSizeSpan(titleSize),0,title2_2_10.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString span2_2_9 = new SpannableString(title2_2_9);
        span2_2_9.setSpan(new AbsoluteSizeSpan(titleSize),0,title2_2_9.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        SpannableString span2_2_8 = new SpannableString(title2_2_8);
        span2_2_8.setSpan(new AbsoluteSizeSpan(titleSize),0,title2_2_8.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        CharSequence finalText = TextUtils.concat(span2_2_13,"\n\n",text2_2_13, "\n\n",span2_2_11_1,"\n\n",text2_2_12, "\n\n",span2_2_11,"\n\n",text2_2_11, "\n\n",span2_2_10,"\n\n",text2_2_10, "\n\n",span2_2_9,"\n\n",text2_2_9,"\n\n",span2_2_8,"\n\n",text2_2_8
                );
        builder.setTitle(R.string.what_new)
                .setMessage(finalText)
                .setPositiveButton("Ok", null)
                .setNegativeButton(R.string.vote_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                });
        builder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
//        setStepButtonAndDescription();
        checkPermissionOk();
        stopService(new Intent(this, EdgeGestureService.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this) && (edge1Switch.isChecked() || edge1Switch.isChecked())) {
                Log.e(TAG, "onResume: startService");
                startService(new Intent(this, EdgeGestureService.class));
            }
        } else {
            if (edge1Switch.isChecked() || edge1Switch.isChecked()) {
                startService(new Intent(this, EdgeGestureService.class));
            }
        }

    }

//    public void showDialog() {
////        FragmentManager fragmentManager = getSupportFragmentManager();
//        EdgeSetting newFragment = new EdgeSetting();
////        FragmentTransaction transaction = fragmentManager.beginTransaction();
////        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
////        transaction.add(android.R.id.content, newFragment)
////                .addToBackStack(null).commit();
//
//        android.app.FragmentManager fragmentManager1 = getFragmentManager();
//        android.app.FragmentTransaction transaction1 = fragmentManager1.beginTransaction();
//        transaction1.setTransition(android.app.FragmentTransaction.TRANSIT_ENTER_MASK);
//        transaction1.add(android.R.id.content, newFragment).addToBackStack(null).commit();
//    }


//    public void setStepButtonAndDescription() {
//        boolean isStep1ok = isStep1Ok();
//        if (isStep1ok) {
////            step1Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_green));
////            step1Text.setText(R.string.main_step1_text_success);
//
//        } else {
////            step1Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_red));
////            step1Text.setText(R.string.main_step1_information);
//        }
//        boolean isStep2Ok = Utility.isAccessibilityEnable(this);
//        Log.e(TAG, "isStep2OK = " + isStep2Ok);
//
//        if (isStep2Ok) {
//            step2Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_green));
//
//        } else {
//            step2Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_red));
//        }
//
//        if (isStep1ok & isStep2Ok) {
////            descriptionText.setText("");
////            descriptionText.setVisibility(View.GONE);
//        }
//    }


    private boolean isStep1Ok() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        } else return true;


    }

//    public void checkDrawOverlayPermission() {
//        if (!Settings.canDrawOverlays(this)) {
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + getPackageName()));
//            startActivityForResult(intent, REQUEST_CODE);
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                // continue here - permission was granted
                stopService(new Intent(this, EdgeGestureService.class));
                startService(new Intent(this, EdgeGestureService.class));

            }
        }else if (requestCode == REQUEST_INVITE) {
            Toast.makeText(this, "Thanks!", Toast.LENGTH_SHORT);
        }
    }
    protected void sendEmail() {
        String[] TO = {"thanhhai08sk@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void shareFriend() {
        String url = "http://play.google.com/store/apps/details?id="  + getPackageName();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Activity not found when share app");
        }

    }

    private boolean checkPermissionOk() {
        boolean isOk;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isOk = isStep1Ok() && Settings.canDrawOverlays(this) && Utility.isAccessibilityEnable(this);

        } else {
            isOk = isStep1Ok() && Utility.isAccessibilityEnable(this);
        }
        if (isOk) {
            permissionMissing.setVisibility(View.GONE);
        } else {
            permissionMissing.setVisibility(View.VISIBLE);
        }

        return isOk;
    }

}
