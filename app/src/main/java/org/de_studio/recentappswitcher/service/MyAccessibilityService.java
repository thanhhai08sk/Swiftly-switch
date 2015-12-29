package org.de_studio.recentappswitcher.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by hai on 12/28/2015.
 */
public class MyAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.e("MyAccessibilityService", "get event");
        if (event.getEventType() == AccessibilityEvent.TYPE_TOUCH_INTERACTION_END){
            Log.e("MyAccessibilityService", "home");
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected boolean onGesture(int gestureId) {
        return super.onGesture(gestureId);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.e("myaccess", "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType= AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout= 100;
        info.packageNames = new  String[]{"org.de_studio.recentappswitcher"};
        this.setServiceInfo(info);

    }
}
