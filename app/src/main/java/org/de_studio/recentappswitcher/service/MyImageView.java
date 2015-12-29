package org.de_studio.recentappswitcher.service;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;

/**
 * Created by hai on 12/29/2015.
 */
public class MyImageView extends ImageView {
    public MyImageView (Context context){
        super(context);
    }
    public MyImageView(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
    }
    public MyImageView(Context context,AttributeSet attributeSet, int deftype){
        super(context,attributeSet,deftype);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        if (action == MotionEvent.ACTION_UP){
//            sendAccessibilityEvent(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
            Log.e("myimage","action up");
            AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT);
            event1.setClassName(getClass().getName());
            event1.getText().add("this is text");
            event1.setPackageName("org.de_studio.recentappswitcher");
            event1.setEnabled(true);
            AccessibilityManager manager = (AccessibilityManager)this.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
            AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
            recordCompat.setSource(this);
            manager.sendAccessibilityEvent(event1);
        }
        return super.onTouchEvent(event);
    }
}
