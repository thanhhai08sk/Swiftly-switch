package org.de_studio.recentappswitcher;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by HaiNguyen on 7/22/16.
 */
public class GlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
    View contentView;
    private static final String TAG = GlobalLayoutListener.class.getSimpleName();

    public GlobalLayoutListener(View contentView) {
        this.contentView = contentView;
    }
    @Override
    public void onGlobalLayout() {
        Rect r = new Rect();
        contentView.getWindowVisibleDisplayFrame(r);
        int screenHeight = contentView.getRootView().getHeight();

        // r.bottom is the position above soft keypad or device button.
        // if keypad is shown, the r.bottom is smaller than that before.
        int keypadHeight = screenHeight - r.bottom;

        Log.d(TAG, "keypadHeight = " + keypadHeight);

        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
            // keyboard is opened
            Log.e(TAG, "onGlobalLayout: keyboard is opend");
        }
        else {
            Log.e(TAG, "onGlobalLayout: keyboard is closed");
            // keyboard is closed
        }
    }
}
