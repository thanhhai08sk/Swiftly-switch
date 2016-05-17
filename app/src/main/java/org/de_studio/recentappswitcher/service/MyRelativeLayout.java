package org.de_studio.recentappswitcher.service;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by HaiNguyen on 5/17/16.
 */
public class MyRelativeLayout extends RelativeLayout {
    public MyRelativeLayout (Context context){
        super(context);
    }
    public MyRelativeLayout(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
    }
    public MyRelativeLayout(Context context,AttributeSet attributeSet, int deftype){
        super(context,attributeSet,deftype);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }


}
