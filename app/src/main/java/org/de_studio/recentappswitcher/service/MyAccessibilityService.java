package org.de_studio.recentappswitcher.service;

import android.accessibilityservice.AccessibilityService;
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
            if (event.getAction()==1){
                Log.e("MyAccessibilityService ", "home");
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
            }else if (event.getAction()==2){
                Log.e("MyAccessibilityService ", "back");
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }else if (event.getAction() ==3){
                Log.e("MyAccessibilityService ", "power");
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG);
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
