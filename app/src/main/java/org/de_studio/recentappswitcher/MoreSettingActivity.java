package org.de_studio.recentappswitcher;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.de_studio.recentappswitcher.service.EdgeGestureService;
import org.de_studio.recentappswitcher.service.EdgeSettingDialogFragment;

public class MoreSettingActivity extends AppCompatActivity {

    public static final String PRO_VERSION_PACKAGE_NAME = "org.de_studio.recentappswitcher.pro";
    public static final String FREE_VERSION_PACKAGE_NAME = "org.de_studio.recentappswitcher.trial";
    public static final String DEFAULT_SHAREDPREFERENCE = "org.de_studio.recentappswitcher_sharedpreferences";
    private boolean isTrial = false, isOutOfTrial = false;
    private SharedPreferences sharedPreferencesDefautl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getPackageName().equals(FREE_VERSION_PACKAGE_NAME)) isTrial = true;
        sharedPreferencesDefautl = getSharedPreferences(DEFAULT_SHAREDPREFERENCE, 0);

        final ImageButton vibrationDurationSettingButton = (ImageButton) findViewById(R.id.main_vibration_duration_setting_image_button);
        ImageButton iconSizeSettingButton = (ImageButton) findViewById(R.id.main_icon_size_setting_image_button);
        ImageButton backgroundColorSettingButton = (ImageButton) findViewById(R.id.main_background_color_setting_image_button);
        ImageButton guideColorSettingButton = (ImageButton) findViewById(R.id.main_guide_color_setting_image_button);
        ImageButton holdTimeSettingButton = (ImageButton) findViewById(R.id.main_hold_time_setting_image_button);
        ImageButton animationTimeSettingButton = (ImageButton) findViewById(R.id.main_animation_time_setting_image_button);
        ImageButton iconPackSettingButton = (ImageButton) findViewById(R.id.main_icon_pack_support_setting_button);
        SwitchCompat hapticFeedbackOnTriggerSwitch = (SwitchCompat) findViewById(R.id.main_disable_haptic_feedback_switch);
        SwitchCompat hapticFeedbackOnItemSwitch = (SwitchCompat) findViewById(R.id.main_haptic_feedback_on_item_switch);
        SwitchCompat disableClockSwitch = (SwitchCompat) findViewById(R.id.main_disable_clock_switch);
        SwitchCompat disableAnimationSwitch = (SwitchCompat) findViewById(R.id.main_disable_animation_switch);
        SwitchCompat holdTimeSwitch = (SwitchCompat) findViewById(R.id.main_hold_time_switch);


        disableAnimationSwitch.setChecked(sharedPreferencesDefautl.getBoolean(EdgeSettingDialogFragment.ANIMATION_KEY,false));
        iconPackSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTrial) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MoreSettingActivity.this);
                    builder.setMessage(R.string.main_icon_pack_trial_dialog_message)
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
                    android.app.FragmentManager fragmentManager = getFragmentManager();
                    IconPackSettingDialogFragment newFragment = new IconPackSettingDialogFragment();
                    newFragment.show(fragmentManager, "iconPackDialogFragment");
                }

            }
        });

                hapticFeedbackOnTriggerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.DISABLE_HAPTIC_FEEDBACK_KEY, !isChecked).commit();
            }
        });
        hapticFeedbackOnItemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.HAPTIC_ON_ICON_KEY,isChecked).commit();
            }
        });
        disableClockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.DISABLE_CLOCK_KEY,isChecked).commit();
            }
        });
        disableAnimationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.ANIMATION_KEY,isChecked).commit();
            }
        });
        holdTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferencesDefautl.edit().putBoolean(EdgeSettingDialogFragment.HOLD_TIME_ENABLE_KEY, isChecked).commit();
            }
        });


        vibrationDurationSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MoreSettingActivity.this);
                View view = View.inflate(MoreSettingActivity.this, R.layout.dialog_vibration_duration_setting, null);
                SeekBar seekBar = (SeekBar) view.findViewById(R.id.dialog_vibration_duration_seek_bar);
                final TextView textView = (TextView) view.findViewById(R.id.dialog_vibration_duration_value);
                int currentValue = sharedPreferencesDefautl.getInt(EdgeSettingDialogFragment.VIBRATION_DURATION_KEY, 15);
                textView.setText(currentValue +" ms");
                seekBar.setProgress(currentValue - 5);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int progressChanged;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progressChanged = progress +5;
                        textView.setText(progressChanged + " ms");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        sharedPreferencesDefautl.edit().putInt(EdgeSettingDialogFragment.VIBRATION_DURATION_KEY,progressChanged).commit();
                        stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                        startService(new Intent(getApplicationContext(), EdgeGestureService.class));
                    }
                });
                builder.setView(view).
                        setTitle(R.string.main_vibration_duration).
                        setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        });
                builder.show();
            }
        });

        iconSizeSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MoreSettingActivity.this);
                View view = View.inflate(MoreSettingActivity.this, R.layout.dialog_icon_size_setting, null);
                SeekBar seekBar = (SeekBar) view.findViewById(R.id.dialog_icon_size_seek_bar);
                final TextView textView = (TextView) view.findViewById(R.id.dialog_icon_size_value);
                int currentValue =(int) (sharedPreferencesDefautl.getFloat(EdgeSettingDialogFragment.ICON_SCALE, 1f) * 100);
                textView.setText(currentValue +" %");
                seekBar.setProgress(currentValue - 70);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int progressChanged;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progressChanged = progress +70;
                        textView.setText(progressChanged + " %");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        sharedPreferencesDefautl.edit().putFloat(EdgeSettingDialogFragment.ICON_SCALE, ((float) progressChanged) / 100).commit();
                        stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                        startService(new Intent(getApplicationContext(), EdgeGestureService.class));
                    }
                });
                builder.setView(view).
                        setTitle(R.string.main_icon_size).
                        setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        });
                builder.show();
            }
        });

        backgroundColorSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentColor = sharedPreferencesDefautl.getInt(EdgeSettingDialogFragment.BACKGROUND_COLOR_KEY,1879048192);
                ColorPickerDialogBuilder
                        .with(MoreSettingActivity.this)
                        .setTitle(getApplicationContext().getString(R.string.main_set_background_color))
                        .initialColor(currentColor)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                sharedPreferencesDefautl.edit().putInt(EdgeSettingDialogFragment.BACKGROUND_COLOR_KEY,selectedColor).commit();
                                stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                                startService(new Intent(getApplicationContext(), EdgeGestureService.class));
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

        guideColorSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentColor = sharedPreferencesDefautl.getInt(EdgeSettingDialogFragment.GUIDE_COLOR_KEY, Color.argb(255, 255, 64, 129));
                ColorPickerDialogBuilder
                        .with(MoreSettingActivity.this)
                        .setTitle(getApplicationContext().getString(R.string.main_set_guide_color))
                        .initialColor(currentColor)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                sharedPreferencesDefautl.edit().putInt(EdgeSettingDialogFragment.GUIDE_COLOR_KEY,selectedColor).commit();
                                stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                                startService(new Intent(getApplicationContext(), EdgeGestureService.class));
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

        holdTimeSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MoreSettingActivity.this);
                View view = View.inflate(MoreSettingActivity.this, R.layout.dialog_hold_time_setting, null);
                SeekBar seekBar = (SeekBar) view.findViewById(R.id.dialog_hold_time_seek_bar);
                final TextView textView = (TextView) view.findViewById(R.id.dialog_hold_time_value);
                int currentValue = sharedPreferencesDefautl.getInt(EdgeSettingDialogFragment.HOLD_TIME_KEY, 600);
                textView.setText(currentValue +" ms");
                seekBar.setProgress(currentValue - 300);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int progressChanged;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progressChanged = progress + 300;
                        textView.setText(progressChanged + " ms");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        sharedPreferencesDefautl.edit().putInt(EdgeSettingDialogFragment.HOLD_TIME_KEY, progressChanged).commit();
                        stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                        startService(new Intent(getApplicationContext(), EdgeGestureService.class));
                    }
                });

                builder.setView(view).
                        setTitle(R.string.main_hold_time).
                        setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        });
                builder.show();
            }
        });

        animationTimeSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MoreSettingActivity.this);
                View view = View.inflate(MoreSettingActivity.this, R.layout.dialog_ani_time_setting, null);
                SeekBar seekBar = (SeekBar) view.findViewById(R.id.dialog_ani_time_seek_bar);
                final TextView textView = (TextView) view.findViewById(R.id.dialog_ani_time_value);
                int currentValue = sharedPreferencesDefautl.getInt(EdgeSettingDialogFragment.ANI_TIME_KEY, 100);
                textView.setText(currentValue +" ms");
                seekBar.setProgress(currentValue);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int progressChanged;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progressChanged = progress;
                        textView.setText(progressChanged + " ms");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        sharedPreferencesDefautl.edit().putInt(EdgeSettingDialogFragment.ANI_TIME_KEY, progressChanged).commit();
                        stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                        startService(new Intent(getApplicationContext(), EdgeGestureService.class));
                    }
                });

                builder.setView(view).
                        setTitle(R.string.main_ani_time).
                        setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        });
                builder.show();
            }
        });

    }
}
