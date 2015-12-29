package org.de_studio.recentappswitcher;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

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

    public static void setIconsPosition(ImageView[] icon, int x_init_cord, int y_init_cord, float icon_distance_pxl, float icon_24_dp_pxl, int edgePosition){

        switch (edgePosition){
            case 10:
                icon[0].setX((float) (x_init_cord - 0.26 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) 0.96 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord - 0.71 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) 0.71 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord - 0.97 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) 0.26 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord - 0.97 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) 0.26 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord - 0.71 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) 0.71 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord - 0.26 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) 0.97 * icon_distance_pxl - icon_24_dp_pxl);
                break;
            case 11:
                icon[0].setX((float) (x_init_cord - 0.26 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) 0.96 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord - 0.71 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) 0.71 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord - 0.97 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) 0.26 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord - 0.97 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) 0.26 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord - 0.71 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) 0.71 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord - 0.26 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) 0.97 * icon_distance_pxl - icon_24_dp_pxl);
                break;
            case 12:
                icon[0].setX((float) (x_init_cord - 0.26 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) 0.96 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord - 0.71 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) 0.71 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord - 0.97 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) 0.26 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord - 0.97 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) 0.26 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord - 0.71 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) 0.71 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord - 0.26 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) 0.97 * icon_distance_pxl - icon_24_dp_pxl);
                break;
            case 20:
                icon[0].setX((float) (x_init_cord + 0.26 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) 0.96 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord + 0.71 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) 0.71 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord + 0.97 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) 0.26 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord + 0.97 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) 0.26 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord + 0.71 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) 0.71 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord + 0.26 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) 0.97 * icon_distance_pxl - icon_24_dp_pxl);
                break;
            case 21:
                icon[0].setX((float) (x_init_cord + 0.26 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) 0.96 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord + 0.71 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) 0.71 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord + 0.97 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) 0.26 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord + 0.97 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) 0.26 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord + 0.71 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) 0.71 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord + 0.26 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) 0.97 * icon_distance_pxl - icon_24_dp_pxl);
                break;
            case 22:
                icon[0].setX((float) (x_init_cord + 0.26 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) 0.96 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord + 0.71 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) 0.71 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord + 0.97 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) 0.26 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord + 0.97 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) 0.26 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord + 0.71 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) 0.71 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord + 0.26 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) 0.97 * icon_distance_pxl - icon_24_dp_pxl);
                break;
        }

    }

    public static boolean isExpandStatusBar(int x_init, int y_init, int x, int y, int radius, WindowManager win){
        double distance = Math.sqrt(Math.pow((double)x - (double)x_init,2) + Math.pow((double)y - (double) y_init,2));
        double distanceNeeded_pxl = (double) dpiToPixels(35+ radius,win);
        boolean isPositiveAng = y < y_init;
        double oriAng = Math.acos((double)(Math.abs(x - x_init))/distance);
        double maxAng = 0.4166*Math.PI;
        double minAng = 0.0833*Math.PI;
        Log.e("isExpandStatusBar","oriAng = " + oriAng + " max = "+ maxAng + " min = "+ minAng+ "distance = " + distance + "need = " + distanceNeeded_pxl );
        return oriAng>minAng & oriAng < maxAng & distance >= distanceNeeded_pxl;
    }

}
