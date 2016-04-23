package org.de_studio.recentappswitcher.service;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by hai on 12/29/2015.
 */
public class MyImageView extends ImageView {
    private boolean onAnimation = false;
    public MyImageView (Context context){
        super(context);
    }
    public MyImageView(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
    }
    public MyImageView(Context context,AttributeSet attributeSet, int deftype){
        super(context,attributeSet,deftype);
    }

    public boolean isOnAnimation() {
        return onAnimation;
    }

    public void setOnAnimation(boolean onAnimation) {
        this.onAnimation = onAnimation;
    }
}
