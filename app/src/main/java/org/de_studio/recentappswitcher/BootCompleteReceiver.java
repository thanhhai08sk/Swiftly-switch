package org.de_studio.recentappswitcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by HaiNguyen on 8/7/16.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
                Utility.startService(context);
                break;
            case "android.intent.action.QUICKBOOT_POWERON":
                Utility.startService(context);
                break;
            case Intent.ACTION_USER_PRESENT:
                if (!Utility.isEdgesOn(context)) {
                    Utility.startService(context);
                }
                break;
        }

    }
}
