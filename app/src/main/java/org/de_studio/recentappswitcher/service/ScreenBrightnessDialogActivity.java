package org.de_studio.recentappswitcher.service;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.de_studio.recentappswitcher.R;

public class ScreenBrightnessDialogActivity extends AppCompatActivity {
    private int brightness;
    private ContentResolver cResolver;
    private Window window;
    private static final String TAG = ScreenBrightnessDialogActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setView(R.layout.screen_brightness_dialog);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        final ImageView autobutton = (ImageView) dialog.findViewById(R.id.screen_brightness_auto_image_view);

        final TextView textView = (TextView) dialog.findViewById(R.id.percent_text);
        final SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.screen_brightness_seek_bar);
        if (seekBar != null) {
            seekBar.setMax(255);
        }

        cResolver = getContentResolver();
        window = getWindow();
        int mode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;


        updateView(seekBar,textView);
        try {
            mode = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (autobutton != null) {
                if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                    autobutton.setImageResource(R.drawable.ic_brightness_auto_32dp);
                }else autobutton.setImageResource(R.drawable.ic_brightness_auto_off);
            }
//            Settings.System.putInt(cResolver,
//                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        } catch (Settings.SettingNotFoundException e)
        {
            Log.e("Error", "Cannot access system brightness");
            e.printStackTrace();
        }

        Log.e(TAG, "onCreate: brightness = "+ brightness + "\nmode = " + mode    );

        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Settings.System.putInt(cResolver,Settings.System.SCREEN_BRIGHTNESS, progress);
                    textView.setText(String.format("%d%%",(int) (progress*100 /255) ) );
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        if (autobutton != null) {
            autobutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int mode = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
                        if ( mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                            Settings.System.putInt(cResolver,
                                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                            autobutton.setImageResource(R.drawable.ic_brightness_auto_off);
                        } else {
                            Settings.System.putInt(cResolver,
                                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                            autobutton.setImageResource(R.drawable.ic_brightness_auto_32dp);
                        }
                        updateView(seekBar, textView);
                    } catch (Settings.SettingNotFoundException e) {
                        Log.e(TAG, "onClick: Setting not found when click auto brightness button");
                    }

                }
            });
        }


    }

    private void updateView(SeekBar seekBar, TextView textView) {
        try {
            int mode = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
            seekBar.setEnabled(mode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            int progress = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
            if (seekBar != null) {
                seekBar.setProgress(progress);
            }
            Log.e(TAG, "updateView: progress = "+ progress);
            textView.setText(String.format("%d%%",(int) (progress*100 /255) ) );
        } catch (Settings.SettingNotFoundException e)
        {
            Log.e("Error", "Cannot access system brightness");
            e.printStackTrace();
        }
    }
}
