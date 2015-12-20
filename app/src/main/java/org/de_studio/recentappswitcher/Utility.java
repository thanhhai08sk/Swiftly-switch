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

    public static int findIconToSwitch (int[] x, int[] y,int x_cord, int y_cord, int numOfIcon, int radOfIcon, WindowManager win) {

        if (numOfIcon >= 1) {
            if (x_cord >= x[0] & x_cord <= (x[0] + dpiToPixels(radOfIcon, win) * 2) & y_cord >= y[0] & y_cord <= (y[0] + dpiToPixels(radOfIcon, win) * 2)) {
                return 0;
            }
        }
        if (numOfIcon >= 2) {
            if (x_cord >= x[1] & x_cord <= (x[1] + dpiToPixels(radOfIcon, win) * 2) & y_cord >= y[1] & y_cord <= (y[1] + dpiToPixels(radOfIcon, win) * 2)) {
                return 1;
            }
        }
        if (numOfIcon >= 3) {
            if (x_cord >= x[2] & x_cord <= (x[2] + dpiToPixels(radOfIcon, win) * 2) & y_cord >= y[2] & y_cord <= (y[2] + dpiToPixels(radOfIcon, win) * 2)) {
                return 2;
            }
        }
        if (numOfIcon >= 4) {
            if (x_cord >= x[3] & x_cord <= (x[3] + dpiToPixels(radOfIcon, win) * 2) & y_cord >= y[3] & y_cord <= (y[3] + dpiToPixels(radOfIcon, win) * 2)) {
                return 3;
            }
        }
        if (numOfIcon >= 5) {
            if (x_cord >= x[4] & x_cord <= (x[4] + dpiToPixels(radOfIcon, win) * 2) & y_cord >= y[4] & y_cord <= (y[4] + dpiToPixels(radOfIcon, win) * 2)) {
                return 4;
            }
        }
        if (numOfIcon >= 6) {
            if (x_cord >= x[5] & x_cord <= (x[5] + dpiToPixels(radOfIcon, win) * 2) & y_cord >= y[5] & y_cord <= (y[5] + dpiToPixels(radOfIcon, win) * 2)) {
                return 5;
            }
        }
        return -1;
    }

}
