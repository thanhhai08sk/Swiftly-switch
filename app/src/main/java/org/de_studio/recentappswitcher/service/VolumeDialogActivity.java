package org.de_studio.recentappswitcher.service;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
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


        final SeekBar ringSeekBar = (SeekBar) dialog.findViewById(R.id.ringtone_seek_bar);
        SeekBar notiSeekBar = (SeekBar) dialog.findViewById(R.id.notification_seek_bar);
        SeekBar systemSeekBar = (SeekBar) dialog.findViewById(R.id.system_seek_bar);
        SeekBar mediaSeekBar = (SeekBar) dialog.findViewById(R.id.media_seek_bar);

        final ImageView ringtoneImage = (ImageView) dialog.findViewById(R.id.ringtone_image);
        updateRingtoneImage(ringtoneImage, manager);
        if (ringtoneImage != null) {
            ringtoneImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (manager.getRingerMode()) {
                        case AudioManager.RINGER_MODE_NORMAL:
                            manager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            updateRingtoneImage(ringtoneImage, manager);
                            updateRingSeekBar(ringSeekBar, manager);
                            break;
                        case AudioManager.RINGER_MODE_VIBRATE:
                            manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            updateRingtoneImage(ringtoneImage,manager);
                            updateRingSeekBar(ringSeekBar, manager);
                            break;
                        case AudioManager.RINGER_MODE_SILENT:
                            manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            updateRingtoneImage(ringtoneImage, manager);
                            updateRingSeekBar(ringSeekBar, manager);
                    }
                }
            });
        }


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
                updateRingtoneImage(ringtoneImage, manager);
                updateRingSeekBar(ringSeekBar,manager);
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

    private void updateRingtoneImage(ImageView imageView, AudioManager manager) {
        switch (manager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                imageView.setImageResource(R.drawable.ic_rotation);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                imageView.setImageResource(R.drawable.ic_contact);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                imageView.setImageResource(R.drawable.ic_contact_default);
                break;
        }
    }

    private void updateRingSeekBar(SeekBar seekBar, AudioManager manager) {
        seekBar.setProgress(manager.getStreamVolume(AudioManager.STREAM_RING));
    }
}
