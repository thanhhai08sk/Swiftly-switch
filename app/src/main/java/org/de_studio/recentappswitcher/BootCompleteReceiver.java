package org.de_studio.recentappswitcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.de_studio.recentappswitcher.service.EdgeGestureService;

/**
 * Created by HaiNguyen on 8/7/16.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
                context.startService(new Intent(context, EdgeGestureService.class));
                break;
            case "android.intent.action.QUICKBOOT_POWERON":
                context.startService(new Intent(context, EdgeGestureService.class));
                break;
        }

    }
}
