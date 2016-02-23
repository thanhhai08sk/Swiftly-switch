package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Created by hai on 2/23/2016.
 */
public class MyCheckableLinearLayout extends LinearLayout implements Checkable {

    public MyCheckableLinearLayout(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }
    private boolean isChecked = false;
    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public void toggle() {

        isChecked = !isChecked;
    }
}
