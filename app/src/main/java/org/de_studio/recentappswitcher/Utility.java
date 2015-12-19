package org.de_studio.recentappswitcher;

import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by hai on 12/19/2015.
 */
public  class Utility {
    public static int dpiToPixels (int dp, WindowManager windowManager){
        DisplayMetrics metrics =new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;
        return (int) Math.ceil(dp*logicalDensity);
    }
}
