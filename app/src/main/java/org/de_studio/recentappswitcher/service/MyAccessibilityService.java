package org.de_studio.recentappswitcher.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;

/**
 * Created by hai on 12/28/2015.
 */
public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = MyAccessibilityService.class.getSimpleName();
    BroadcastReceiver receiver;

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: ");
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Cons.ACTION_BACK);
        filter.addAction(Cons.ACTION_RECENT);
        filter.addAction(Cons.ACTION_HOME);
        filter.addAction(Cons.ACTION_POWER_MENU);
        filter.addAction(Cons.ACTION_NOTI);
        receiver = new EventReceiver();
        this.registerReceiver(receiver, filter);
        if (!Utility.isEdgesOn(this)) {
            Utility.startService(this);
        }
    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.e("MyAccessibilityService", "get event");
        if (event.getEventType() == AccessibilityEvent.TYPE_TOUCH_INTERACTION_END) {
            switch (event.getAction()) {
                case 1:
                    Log.e("MyAccessibilityService ", "home");
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                    break;
                case 2:
                    Log.e("MyAccessibilityService ", "back " + System.currentTimeMillis());
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    Log.e("MyAccessibilityService ", "back " + System.currentTimeMillis());

                    break;
                case 3:
                    Log.e("MyAccessibilityService ", "power");
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG);
                    break;
                case 4:
                    Log.e("MyAccessibilityService ", "noti");
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
                    break;
                case 5:
                    Log.e("MyAccessibilityService ", "recent");
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                    break;
            }


        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected boolean onGesture(int gestureId) {
        return super.onGesture(gestureId);
    }


    public class EventReceiver extends BroadcastReceiver {

        public EventReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Cons.ACTION_BACK:
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    break;
                case Cons.ACTION_HOME:
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                    break;
                case Cons.ACTION_RECENT:
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                    break;
                case Cons.ACTION_NOTI:
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
                    break;
                case Cons.ACTION_POWER_MENU:
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG);
                    break;

            }
        }
    }


//    @Override
//    protected void onServiceConnected() {
//        super.onServiceConnected();
//        Log.e("myaccess", "onServiceConnected");
//        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
//        info.eventTypes = AccessibilityEvent.TYPE_TOUCH_INTERACTION_END;
//        info.feedbackType= AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
//        info.notificationTimeout= 100;
//        info.packageNames = new  String[]{getPackageName()};
//        this.setServiceInfo(info);
//
//    }
}
