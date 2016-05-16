package org.de_studio.recentappswitcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.de_studio.recentappswitcher.favoriteShortcut.SetFavoriteShortcutActivity;
import org.de_studio.recentappswitcher.intro.IntroActivity;
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
    public static final String ACTION_LAST_APP = "last_app";
    public static final String ACTION_ROTATE = "rotate";
    public static final String ACTION_POWER_MENU = "power_menu";
    public static final String ACTION_CALL_LOGS = "call_logs";
    public static final String ACTION_CONTACT = "contacts";
    public static final String ACTION_DIAL = "dial";
    public static final int REQUEST_CODE = 3243;
    public static final int REQUEST_INVITE = 232;
    public static final long trialTime = 1000 * 60 * 60 * 24 * 7;
    private SharedPreferences sharedPreferences1, sharedPreferences2, sharedPreferencesDefautl, sharedPreferences_favorite, sharedPreferences_exclude;
    private ArrayList<AppInfors> mAppInforsArrayList;
//    private Button step1Button;
//    private TextView step1Text;
//    private Button step2Button;
    private boolean isTrial = false, isOutOfTrial = false;
    private long trialTimePass, beginTime;
    private int step = 1;
    private LinearLayout permissionMissing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                SharedPreferences getPrefs = PreferenceManager
//                        .getDefaultSharedPreferences(getBaseContext());
//                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
//                if (isFirstStart) {
//                    Intent i = new Intent(MainActivity.this, IntroActivity.class);
//                    startActivity(i);
//                    SharedPreferences.Editor e = getPrefs.edit();
//                    e.putBoolean("firstStart", true);
//                    e.apply();
//                }
//            }
//        });
//        t.start();
//        Intent i = new Intent(MainActivity.this, IntroActivity.class);
//        startActivity(i);
        if (getPackageName().equals(FREE_VERSION_PACKAGE_NAME)) isTrial = true;
        setContentView(R.layout.activity_main);
        permissionMissing = (LinearLayout) findViewById(R.id.permission_missing);
        sharedPreferences1 = getSharedPreferences(EDGE_1_SHAREDPREFERENCE, 0);
        sharedPreferences2 = getSharedPreferences(EDGE_2_SHAREDPREFERENCE, 0);
        sharedPreferencesDefautl = getSharedPreferences(DEFAULT_SHAREDPREFERENCE, 0);
        sharedPreferences_favorite = getSharedPreferences(FAVORITE_SHAREDPREFERENCE, 0);
        sharedPreferences_exclude = getSharedPreferences(EXCLUDE_SHAREDPREFERENCE, 0);
        beginTime = sharedPreferencesDefautl.getLong(EdgeSettingDialogFragment.BEGIN_DAY_KEY, 0);
        if (beginTime == 0) {
            sharedPreferencesDefautl.edit().putLong(EdgeSettingDialogFragment.BEGIN_DAY_KEY, System.currentTimeMillis()).commit();
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
        if (!sharedPreferencesDefautl.getBoolean(EdgeSettingDialogFragment.HAS_REACT_FOR_VOTE_KEY, false)) {
            int timeOpen = sharedPreferencesDefautl.getInt(EdgeSettingDialogFragment.APP_OPEN_TIME_KEY, 0);
            sharedPreferencesDefautl.edit().putInt(EdgeSettingDialogFragment.APP_OPEN_TIME_KEY, timeOpen + 1).commit();
            if (timeOpen >= 4) {

                final LinearLayout doYouLoveLinearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.do_you_love_this_app, null);
                appBarLayout.addView(doYouLoveLinearLayout);
                Button yesButton = (Button) doYouLoveLinearLayout.findViewById(R.id.main_do_you_love_yes_button);
                Button noButton = (Button) doYouLoveLinearLayout.findViewById(R.id.main_do_you_love_no_button);
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.HAS_REACT_FOR_VOTE_KEY, true).commit();
                        appBarLayout.removeView(doYouLoveLinearLayout);
                    }
                });
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.HAS_REACT_FOR_VOTE_KEY, true).commit();
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
//                                        sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.HAS_REACT_FOR_VOTE_KEY, true).commit();
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
//                                        sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.HAS_REACT_FOR_VOTE_KEY, true).commit();
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
        final Switch edge1Switch = (Switch) findViewById(R.id.edge_1_switch);
        final Switch edge2Switch = (Switch) findViewById(R.id.edge_2_switch);
//        Switch hapticFeedbackOnTriggerSwitch = (Switch) findViewById(R.id.main_disable_haptic_feedback_switch);
//        Switch hapticFeedbackOnItemSwitch = (Switch) findViewById(R.id.main_haptic_feedback_on_item_switch);
//        Switch disableClockSwitch = (Switch) findViewById(R.id.main_disable_clock_switch);
//        Switch disableAnimationSwitch = (Switch) findViewById(R.id.main_disable_animation_switch);
//        Switch holdTimeSwitch = (Switch) findViewById(R.id.main_hold_time_switch);
//        final LinearLayout shareFriendLinearLayout = (LinearLayout) findViewById(R.id.main_share_linear_layout);
//        LinearLayout reviewLinearLayout = (LinearLayout) findViewById(R.id.main_review_linear_layout);
//        LinearLayout emailLinearLayout = (LinearLayout) findViewById(R.id.main_email_linear_layout);
//        hapticFeedbackOnTriggerSwitch.setChecked(!sharedPreferencesDefautl.getBoolean(EdgeSettingDialogFragment.DISABLE_HAPTIC_FEEDBACK_KEY, true));
//        hapticFeedbackOnItemSwitch.setChecked(sharedPreferencesDefautl.getBoolean(EdgeSettingDialogFragment.HAPTIC_ON_ICON_KEY,false));
//        disableClockSwitch.setChecked(sharedPreferencesDefautl.getBoolean(EdgeSettingDialogFragment.DISABLE_CLOCK_KEY,false));
//        disableAnimationSwitch.setChecked(sharedPreferencesDefautl.getBoolean(EdgeSettingDialogFragment.ANIMATION_KEY,true));
//        holdTimeSwitch.setChecked(sharedPreferencesDefautl.getBoolean(EdgeSettingDialogFragment.HOLD_TIME_ENABLE_KEY,true));
        edge1Switch.setChecked(sharedPreferences1.getBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, true));
        edge2Switch.setChecked(sharedPreferences2.getBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, false));
        if (isTrial) {
            edge2Switch.setChecked(false);
        }
        edge1Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                sharedPreferences1.edit().putBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, isChecked).commit();
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
                    sharedPreferences2.edit().putBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, isChecked).commit();

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
//                sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.DISABLE_HAPTIC_FEEDBACK_KEY, !isChecked).commit();
//            }
//        });
//        hapticFeedbackOnItemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.HAPTIC_ON_ICON_KEY,isChecked).commit();
//            }
//        });
//        disableClockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.DISABLE_CLOCK_KEY,isChecked).commit();
//            }
//        });
//        disableAnimationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.ANIMATION_KEY,isChecked).commit();
//            }
//        });
//        holdTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.HOLD_TIME_ENABLE_KEY, isChecked).commit();
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
                if (isTrial && isOutOfTrial) {
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
                EdgeSettingDialogFragment newFragment = new EdgeSettingDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(EdgeSettingDialogFragment.EDGE_NUMBER_KEY, 1);
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
                Intent i = new Intent(MainActivity.this, IntroActivity.class);
                startActivity(i);

//                android.app.FragmentManager fragmentManager = getFragmentManager();
//                EdgeSettingDialogFragment newFragment = new EdgeSettingDialogFragment();
//                Bundle bundle = new Bundle();
//                bundle.putInt(EdgeSettingDialogFragment.EDGE_NUMBER_KEY, 2);
//                newFragment.setArguments(bundle);
//                android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                transaction.add(android.R.id.content, newFragment)
//                        .addToBackStack(null).commit();

            }
        });

//        iconPackSettingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isTrial) {
//                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
//                    builder.setMessage(R.string.main_icon_pack_trial_dialog_message)
//                            .setPositiveButton(R.string.main_edge_switch_2_trial_buy_pro_button, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Uri uri = Uri.parse("mbarket://details?id=" + PRO_VERSION_PACKAGE_NAME);
//                                    Intent gotoMarket = new Intent(Intent.ACTION_VIEW, uri);
//                                    gotoMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
//                                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                                    try {
//                                        startActivity(gotoMarket);
//                                    } catch (ActivityNotFoundException e) {
//                                        startActivity(new Intent(Intent.ACTION_VIEW,
//                                                Uri.parse("http://play.google.com/store/apps/details?id=" + PRO_VERSION_PACKAGE_NAME)));
//                                    }
//                                }
//                            })
//                            .setNegativeButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    // do nothing
//                                }
//                            });
//                    builder.show();
//
//
//                } else {
//                    android.app.FragmentManager fragmentManager = getFragmentManager();
//                    IconPackSettingDialogFragment newFragment = new IconPackSettingDialogFragment();
//                    newFragment.show(fragmentManager, "iconPackDialogFragment");
//                }
//
//            }
//        });
        outerRingSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OuterRingSettingActivity.class));
            }
        });

//        vibrationDurationSettingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
//                View view = View.inflate(MainActivity.this, R.layout.dialog_vibration_duration_setting, null);
//                SeekBar seekBar = (SeekBar) view.findViewById(R.id.dialog_vibration_duration_seek_bar);
//                final TextView textView = (TextView) view.findViewById(R.id.dialog_vibration_duration_value);
//                int currentValue = sharedPreferencesDefautl.getInt(EdgeSettingDialogFragment.VIBRATION_DURATION_KEY, 15);
//                textView.setText(currentValue +" ms");
//                seekBar.setProgress(currentValue - 5);
//                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    int progressChanged;
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        progressChanged = progress +5;
//                        textView.setText(progressChanged + " ms");
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//                        sharedPreferencesDefautl.edit().putInt(EdgeSettingDialogFragment.VIBRATION_DURATION_KEY,progressChanged).commit();
//                        stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
//                        startService(new Intent(getApplicationContext(), EdgeGestureService.class));
//                    }
//                });
//                builder.setView(view).
//                        setTitle(R.string.main_vibration_duration).
//                        setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //do nothing
//                            }
//                        });
//                builder.show();
//            }
//        });

//        iconSizeSettingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
//                View view = View.inflate(MainActivity.this, R.layout.dialog_icon_size_setting, null);
//                SeekBar seekBar = (SeekBar) view.findViewById(R.id.dialog_icon_size_seek_bar);
//                final TextView textView = (TextView) view.findViewById(R.id.dialog_icon_size_value);
//                int currentValue =(int) (sharedPreferencesDefautl.getFloat(EdgeSettingDialogFragment.ICON_SCALE, 1f) * 100);
//                textView.setText(currentValue +" %");
//                seekBar.setProgress(currentValue - 70);
//                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    int progressChanged;
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        progressChanged = progress +70;
//                        textView.setText(progressChanged + " %");
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//                        sharedPreferencesDefautl.edit().putFloat(EdgeSettingDialogFragment.ICON_SCALE, ((float) progressChanged) / 100).commit();
//                        stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
//                        startService(new Intent(getApplicationContext(), EdgeGestureService.class));
//                    }
//                });
//                builder.setView(view).
//                        setTitle(R.string.main_icon_size).
//                        setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //do nothing
//                            }
//                        });
//                builder.show();
//            }
//        });

//        backgroundColorSettingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int currentColor = sharedPreferencesDefautl.getInt(EdgeSettingDialogFragment.BACKGROUND_COLOR_KEY,1879048192);
//                ColorPickerDialogBuilder
//                        .with(MainActivity.this)
//                        .setTitle(getApplicationContext().getString(R.string.main_set_background_color))
//                        .initialColor(currentColor)
//                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
//                        .density(12)
//                        .setOnColorSelectedListener(new OnColorSelectedListener() {
//                            @Override
//                            public void onColorSelected(int selectedColor) {
//                            }
//                        })
//                        .setPositiveButton("ok", new ColorPickerClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
//                                sharedPreferencesDefautl.edit().putInt(EdgeSettingDialogFragment.BACKGROUND_COLOR_KEY,selectedColor).commit();
//                                stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
//                                startService(new Intent(getApplicationContext(), EdgeGestureService.class));
//                            }
//                        })
//                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        })
//                        .build()
//                        .show();
//            }
//        });

//        guideColorSettingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int currentColor = sharedPreferencesDefautl.getInt(EdgeSettingDialogFragment.GUIDE_COLOR_KEY, Color.argb(255, 255, 64, 129));
//                ColorPickerDialogBuilder
//                        .with(MainActivity.this)
//                        .setTitle(getApplicationContext().getString(R.string.main_set_guide_color))
//                        .initialColor(currentColor)
//                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
//                        .density(12)
//                        .setOnColorSelectedListener(new OnColorSelectedListener() {
//                            @Override
//                            public void onColorSelected(int selectedColor) {
//                            }
//                        })
//                        .setPositiveButton("ok", new ColorPickerClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
//                                sharedPreferencesDefautl.edit().putInt(EdgeSettingDialogFragment.GUIDE_COLOR_KEY,selectedColor).commit();
//                                stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
//                                startService(new Intent(getApplicationContext(), EdgeGestureService.class));
//                            }
//                        })
//                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        })
//                        .build()
//                        .show();
//            }
//        });

//        holdTimeSettingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
//                View view = View.inflate(MainActivity.this, R.layout.dialog_hold_time_setting, null);
//                SeekBar seekBar = (SeekBar) view.findViewById(R.id.dialog_hold_time_seek_bar);
//                final TextView textView = (TextView) view.findViewById(R.id.dialog_hold_time_value);
//                int currentValue = sharedPreferencesDefautl.getInt(EdgeSettingDialogFragment.HOLD_TIME_KEY, 600);
//                textView.setText(currentValue +" ms");
//                seekBar.setProgress(currentValue - 300);
//                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    int progressChanged;
//
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        progressChanged = progress + 300;
//                        textView.setText(progressChanged + " ms");
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//                        sharedPreferencesDefautl.edit().putInt(EdgeSettingDialogFragment.HOLD_TIME_KEY, progressChanged).commit();
//                        stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
//                        startService(new Intent(getApplicationContext(), EdgeGestureService.class));
//                    }
//                });
//
//                builder.setView(view).
//                        setTitle(R.string.main_hold_time).
//                        setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //do nothing
//                            }
//                        });
//                builder.show();
//            }
//        });

//        animationTimeSettingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
//                View view = View.inflate(MainActivity.this, R.layout.dialog_ani_time_setting, null);
//                SeekBar seekBar = (SeekBar) view.findViewById(R.id.dialog_ani_time_seek_bar);
//                final TextView textView = (TextView) view.findViewById(R.id.dialog_ani_time_value);
//                int currentValue = sharedPreferencesDefautl.getInt(EdgeSettingDialogFragment.ANI_TIME_KEY, 100);
//                textView.setText(currentValue +" ms");
//                seekBar.setProgress(currentValue);
//                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    int progressChanged;
//
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        progressChanged = progress;
//                        textView.setText(progressChanged + " ms");
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//                        sharedPreferencesDefautl.edit().putInt(EdgeSettingDialogFragment.ANI_TIME_KEY, progressChanged).commit();
//                        stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
//                        startService(new Intent(getApplicationContext(), EdgeGestureService.class));
//                    }
//                });
//
//                builder.setView(view).
//                        setTitle(R.string.main_ani_time).
//                        setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //do nothing
//                            }
//                        });
//                builder.show();
//            }
//        });

//        holdTimeInfoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle(R.string.main_hold_time)
//                        .setMessage(R.string.main_hold_time_info)
//                        .setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //do nothing
//                            }
//                        });
//                builder.show();
//            }
//        });

//        numberOfRecentButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int current = sharedPreferencesDefautl.getInt(EdgeSettingDialogFragment.NUM_OF_RECENT_KEY, 6);
//                int checked = 2;
//                switch (current) {
//                    case 4:
//                        checked = 0;
//                        break;
//                    case 5:
//                        checked = 1;
//                        break;
//                    case 6:
//                        checked = 2;
//                        break;
//                }
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle(R.string.main_number_of_recent)
//                        .setSingleChoiceItems(new CharSequence[]{"4", "5", "6"}, checked, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                switch (which) {
//                                    case 0:
//                                        sharedPreferencesDefautl.edit().putInt(EdgeSettingDialogFragment.NUM_OF_RECENT_KEY, 4).commit();
//                                        break;
//                                    case 1:
//                                        sharedPreferencesDefautl.edit().putInt(EdgeSettingDialogFragment.NUM_OF_RECENT_KEY, 5).commit();
//                                        break;
//                                    case 2:
//                                        sharedPreferencesDefautl.edit().putInt(EdgeSettingDialogFragment.NUM_OF_RECENT_KEY, 6).commit();
//                                        break;
//                                }
//                            }
//                        })
//                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
//                            @Override
//                            public void onDismiss(DialogInterface dialog) {
//                                stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
//                                startService(new Intent(getApplicationContext(), EdgeGestureService.class));
//                            }
//                        })
//                .
//                setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //do nothing
//                    }
//                });
//                builder.show();
//            }
//        });


//        setStepButtonAndDescription();


//        step1Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                step =1;
//                if (isStep1Ok()) {
//                    step1Text.setText(R.string.main_step1_text_success);
//                } else {
//                    step1Text.setText(R.string.main_step1_information);
//                }
//                stepTextFrame.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.text_board_1));
//            }
//        });
//        step2Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                step = 2;
//                if (Utility.isAccessibilityEnable(getApplicationContext())) {
//                    step1Text.setText(R.string.main_step2_text_success);
//                } else step1Text.setText(R.string.main_step2_information);
//
//                stepTextFrame.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.text_board_2_));
//            }
//        });

//        step1GoToSettingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                    if (step == 1) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            try {
//                                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
//                            } catch (ActivityNotFoundException e) {
//                                Log.e(LOG_TAG, "Can not found usage access setting");
//                                Toast.makeText(MainActivity.this,R.string.main_usage_access_can_not_found,Toast.LENGTH_LONG).show();
//                            }
//
//                        }
//                    } else {
//                        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
//                    }
//
//
//            }
//        });
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            stopService(new Intent(this, EdgeGestureService.class));
            startService(new Intent(this, EdgeGestureService.class));

        } else {
            checkDrawOverlayPermission();

            if (Settings.canDrawOverlays(this)) {
                stopService(new Intent(this, EdgeGestureService.class));
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

        GridLayout editExcludeButton = (GridLayout) findViewById(R.id.main_exclude_edit_image_button);
        editExcludeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentManager fragmentManager = getSupportFragmentManager();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                FavoriteOrExcludeDialogFragment newFragment = new FavoriteOrExcludeDialogFragment();
                newFragment.show(fragmentManager, "excludeDialogFragment");
            }
        });


//        favoriteInfoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
////                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                builder.setTitle(getString(R.string.main_favorite_info_title))
//                        .setMessage(R.string.main_favorite_app_info_text)
//                        .setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // do nothing
//                            }
//                        });
//                builder.show();
//            }
//        });
//        excludeInfoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle(getString(R.string.main_exclude_info_title))
//                        .setMessage(R.string.main_exclude_app_info)
//                        .setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //do nothing
//                            }
//                        });
//                builder.show();
//            }
//        });
//        pinAppInfoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle(getString(R.string.main_pin_app))
//                        .setMessage(R.string.pin_app_info)
//                        .setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //do nothing
//                            }
//                        });
//                builder.show();
//            }
//        });
//        disableAccessibilityInfoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle(R.string.main_consider_disable_accessibility_dialog_title)
//                        .setMessage(R.string.main_disable_accessibility_detail_text)
//                        .setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //do nothing
//                            }
//                        });
//                builder.show();
//            }
//        });
//        ImageButton disableAccessibilityButton = (ImageButton) findViewById(R.id.main_disable_accessiblity_image_button);
//        disableAccessibilityButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
//            }
//        });

//        shareFriendLinearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shareFriend();
//            }
//        });
//        emailLinearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendEmail();
//            }
//        });
//
//        reviewLinearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri uri = Uri.parse("mbarket://details?id=" + getPackageName());
//                Intent gotoMarket = new Intent(Intent.ACTION_VIEW, uri);
//                gotoMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
//                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                try {
//                    startActivity(gotoMarket);
//                } catch (ActivityNotFoundException e) {
//                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
//                }
//            }
//        });

        checkPermissionOk();


    }


    @Override
    protected void onResume() {
        super.onResume();
//        setStepButtonAndDescription();
        checkPermissionOk();
        if (!(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1)) {
            checkDrawOverlayPermission();
        }

    }

//    public void showDialog() {
////        FragmentManager fragmentManager = getSupportFragmentManager();
//        EdgeSettingDialogFragment newFragment = new EdgeSettingDialogFragment();
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
//        Log.e(LOG_TAG, "isStep2OK = " + isStep2Ok);
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

    public void checkDrawOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

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
            Log.e(LOG_TAG, "Activity not found when share app");
        }

    }

    private boolean checkPermissionOk() {
        boolean isOk = isStep1Ok() && Settings.canDrawOverlays(this) && Utility.isAccessibilityEnable(this);
        if (isOk) {
            permissionMissing.setVisibility(View.GONE);
        } else {
            permissionMissing.setVisibility(View.VISIBLE);
        }
        return isOk;
    }


}
