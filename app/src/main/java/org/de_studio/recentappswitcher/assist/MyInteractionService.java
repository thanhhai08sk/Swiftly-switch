package org.de_studio.recentappswitcher.assist;

import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.service.voice.VoiceInteractionSessionService;
import android.util.Log;

/**
 * Created by HaiNguyen on 8/5/16.
 */
public class MyInteractionService extends VoiceInteractionSessionService {
    private static final String TAG = MyInteractionService.class.getSimpleName();
    @Override
    public VoiceInteractionSession onNewSession(Bundle args) {
        Log.e(TAG, "onNewSession: ");
        return new MyVoiceInteractionSecssion(this);
    }
}
