package org.de_studio.recentappswitcher.assist;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;

/**
 * Created by HaiNguyen on 8/5/16.
 */
public class MyVoiceInteractionSecssion extends VoiceInteractionSession {
    public MyVoiceInteractionSecssion(Context context) {
        super(context);
    }
    @Override
    public void onHandleAssist(Bundle data,
                               AssistStructure structure,
                               AssistContent content) {
        finish();
    }


}
