package org.de_studio.recentappswitcher.dialogActivity;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;

import org.de_studio.recentappswitcher.R;

public class AudioDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_dialog);
        ImageButton soundProfileImageButton = (ImageButton) findViewById(R.id.audio_dialog_sound_profile);
        SeekBar volumnSeekBar = (SeekBar) findViewById(R.id.audio_dialog_volume_seekbar);
        AudioManager myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int currentRingerMode = myAudioManager.getRingerMode();
        boolean isMusicActive = myAudioManager.isMusicActive();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    }
}
