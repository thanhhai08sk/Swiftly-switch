package org.de_studio.recentappswitcher.assist;

import android.content.Intent;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.service.voice.VoiceInteractionSessionService;
import android.util.Log;

import org.de_studio.recentappswitcher.Cons;

/**
 * Created by HaiNguyen on 8/5/16.
 */
public class MyInteractionService extends VoiceInteractionSessionService {
    private static final String TAG = MyInteractionService.class.getSimpleName();
    @Override
    public VoiceInteractionSession onNewSession(Bundle args) {
        Log.e(TAG, "onNewSession: ");
        Intent intent = new Intent();
        intent.setAction(Cons.ACTION_TOGGLE_EDGES);
        sendBroadcast(intent);
        return new MyVoiceInteractionSecssion(this);
    }
}
