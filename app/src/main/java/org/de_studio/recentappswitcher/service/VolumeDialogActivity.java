package org.de_studio.recentappswitcher.service;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

import org.de_studio.recentappswitcher.R;

public class VolumeDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AudioManager manager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.volume_dialog);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


        int maxRing = manager.getStreamMaxVolume(AudioManager.STREAM_RING);
        int maxNoti = manager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
        int maxSystem = manager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        int maxMedia = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentRing = manager.getStreamVolume(AudioManager.STREAM_RING);
        int currentNoti = manager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        int currentSystem = manager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        int currentMedia = manager.getStreamVolume(AudioManager.STREAM_MUSIC);

        SeekBar ringSeekBar = (SeekBar) dialog.findViewById(R.id.ringtone_seek_bar);
        SeekBar notiSeekBar = (SeekBar) dialog.findViewById(R.id.notification_seek_bar);
        SeekBar systemSeekBar = (SeekBar) dialog.findViewById(R.id.system_seek_bar);
        SeekBar mediaSeekBar = (SeekBar) dialog.findViewById(R.id.media_seek_bar);

        ringSeekBar.setMax(maxRing);
        notiSeekBar.setMax(maxNoti);
        systemSeekBar.setMax(maxSystem);
        mediaSeekBar.setMax(maxMedia);

        ringSeekBar.setProgress(currentRing);
        notiSeekBar.setProgress(currentNoti);
        systemSeekBar.setProgress(currentSystem);
        mediaSeekBar.setProgress(currentMedia);

        ringSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                manager.setStreamVolume(AudioManager.STREAM_RING,seekBar.getProgress(),0);
            }
        });

        notiSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                manager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,seekBar.getProgress(),0);
            }
        });

        systemSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                manager.setStreamVolume(AudioManager.STREAM_SYSTEM,seekBar.getProgress(),0);
            }
        });

        mediaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                manager.setStreamVolume(AudioManager.STREAM_MUSIC,seekBar.getProgress(),0);
            }
        });
    }
}
