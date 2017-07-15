package org.de_studio.recentappswitcher.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by HaiNguyen on 7/15/17.
 */

public class MyEditText extends EditText {
    private BackOnEditTextListener listener;
    public interface BackOnEditTextListener {
        void onBackButton();
    }
    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP) {
            if (listener != null) {
                listener.onBackButton();
            }
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    public void setBackButtonListener(BackOnEditTextListener listener) {
        this.listener = listener;
    }
}
