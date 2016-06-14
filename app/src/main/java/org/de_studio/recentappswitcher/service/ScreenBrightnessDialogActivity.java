package org.de_studio.recentappswitcher.service;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;

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
        ImageView autobutton = (ImageView) dialog.findViewById(R.id.screen_brightness_auto_image_view);
        SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.screen_brightness_seek_bar);
        if (seekBar != null) {
            seekBar.setMax(255);
        }
        cResolver = getContentResolver();
        window = getWindow();
        int mode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;



        try {
            mode = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
            Settings.System.putInt(cResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
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
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }




    }
}
