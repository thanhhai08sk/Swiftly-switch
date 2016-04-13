package org.de_studio.recentappswitcher;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.CallLog;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.de_studio.recentappswitcher.dialogActivity.AudioDialogActivity;
import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;
import org.de_studio.recentappswitcher.service.EdgeSettingDialogFragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by hai on 12/19/2015.
 */
public  class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();
//    public static int dpiToPixels (int dp, WindowManager windowManager){
//        DisplayMetrics metrics =new DisplayMetrics();
//        windowManager.getDefaultDisplay().getMetrics(metrics);
//        float logicalDensity = metrics.density;
//        return (int) Math.ceil(dp*logicalDensity);
//    }

    public static int findIconToSwitch (int[] x, int[] y,int x_cord, int y_cord, int numOfIcon, int radOfIcon, float mScale) {
        int radOfIconPxl = (int) (radOfIcon * mScale);
        if (numOfIcon >= 1) {
            if (x_cord >= x[0] & x_cord <= (x[0] + radOfIconPxl * 2) & y_cord >= y[0] & y_cord <= (y[0] + radOfIconPxl * 2)) {
                return 0;
            }
        }
        if (numOfIcon >= 2) {
            if (x_cord >= x[1] & x_cord <= (x[1] + radOfIconPxl * 2) & y_cord >= y[1] & y_cord <= (y[1] + radOfIconPxl * 2)) {
                return 1;
            }
        }
        if (numOfIcon >= 3) {
            if (x_cord >= x[2] & x_cord <= (x[2] + radOfIconPxl * 2) & y_cord >= y[2] & y_cord <= (y[2] + radOfIconPxl * 2)) {
                return 2;
            }
        }
        if (numOfIcon >= 4) {
            if (x_cord >= x[3] & x_cord <= (x[3] + radOfIconPxl * 2) & y_cord >= y[3] & y_cord <= (y[3] + radOfIconPxl * 2)) {
                return 3;
            }
        }
        if (numOfIcon >= 5) {
            if (x_cord >= x[4] & x_cord <= (x[4] + radOfIconPxl * 2) & y_cord >= y[4] & y_cord <= (y[4] + radOfIconPxl * 2)) {
                return 4;
            }
        }
        if (numOfIcon >= 6) {
            if (x_cord >= x[5] & x_cord <= (x[5] + radOfIconPxl * 2) & y_cord >= y[5] & y_cord <= (y[5] + radOfIconPxl * 2)) {
                return 5;
            }
        }
        return -1;
    }

    public static int findIconToSwitchNew(int[] x, int[] y, int x_cord, int y_cord, float radOfIconPxl, float mScale) {
        double distance;
        double distanceNeeded = 35*mScale;
        for (int i = 0; i < x.length; i++) {
            distance = Math.sqrt(Math.pow((double)x_cord - (double)(x[i] + radOfIconPxl),2) + Math.pow((double)y_cord - (double)(y[i]+radOfIconPxl), 2));
            if (distance <= distanceNeeded) {
                return i;
            }
        }
        return -1;
    }


    public static int findShortcutToSwitch(int x_cord, int y_cord, int x_grid, int y_grid, int sizeOfIcon, float mScale,int gird_row, int grid_column, int grid_gap) {
        int item_x,item_y;
        double distance;
        for (int i = 0; i < grid_column; i++) {
            for (int j = 0; j < gird_row; j++) {
                item_x = (int)(x_grid + (sizeOfIcon/2)*mScale +i*(sizeOfIcon + grid_gap)*mScale);
                item_y = (int) (y_grid + (sizeOfIcon/2) * mScale + j * (sizeOfIcon + grid_gap) * mScale);
                distance = Math.sqrt(Math.pow((double)x_cord - (double)item_x,2) + Math.pow((double)y_cord - (double) item_y, 2));
                if (distance <= 35 * mScale) {
                    return j * grid_column + i;
                }
            }
        }
        return -1;
    }

    public static void setIconPositionNew(AppCompatImageView[] icon, float r, float icon_24_dp_pxl, int position, int x_i, int y_i, int n) {
        Log.e(LOG_TAG, "setIconPositionNew");
        double alpha, beta;
        double[] alphaN = new double[n];
        switch (n) {
            case 4:
//                alpha = 0.1389*Math.PI; // 25 degree
                alpha = 0.111 * Math.PI; // 20 degree
                break;
            case 5:
                alpha = 0.111 * Math.PI; // 20 degree
                break;
            case 6:
                alpha = 0.0556 * Math.PI; // 10 degree
                break;
            default:
                alpha = 0.0556;
        }
        beta = Math.PI - 2 * alpha;
        for (int i = 0; i < n; i++) {
            alphaN[i] = alpha + i * (beta / (n - 1));
            switch (position / 10) {
                case 1:
                    icon[i].setX(x_i - r * (float) Math.sin(alphaN[i]) - icon_24_dp_pxl);
                    icon[i].setY(y_i - r * (float) Math.cos(alphaN[i]) - icon_24_dp_pxl);
                    break;
                case 2:
                    icon[i].setX(x_i + r * (float) Math.sin(alphaN[i]) - icon_24_dp_pxl);
                    icon[i].setY(y_i - r * (float) Math.cos(alphaN[i]) - icon_24_dp_pxl);
                    break;
                case 3:
                    icon[i].setX(x_i - r * (float) Math.cos(alphaN[i]) - icon_24_dp_pxl);
                    icon[i].setY(y_i - r * (float) Math.sin(alphaN[i]) - icon_24_dp_pxl);
                    break;
            }
        }
        if (n < icon.length) {
            for (int j = n; j < icon.length; j++) {
                icon[j].setVisibility(View.GONE);
            }
        }

    }

    public static void setIconsPosition(AppCompatImageView[] icon, int x_init_cord, int y_init_cord, float icon_distance_pxl, float icon_24_dp_pxl, int edgePosition){
        double sin10 = 0.1736, sin42 = 0.6691, sin74 = 0.9613, cos10 = 0.9848, cos42 = 0.7431, cos74 = 0.2756;
        switch (edgePosition){
            case 10:
                icon[0].setX((float) (x_init_cord - sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord - sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord - sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord - sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord - sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord - sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                break;
            case 11:
                icon[0].setX((float) (x_init_cord - sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord - sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord - sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord - sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord - sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord - sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                int distance = x_init_cord - (int)icon[0].getX();
                Log.e(LOG_TAG,"x_init - x0 = "+distance + "\nx_init = "+ x_init_cord );
                break;
            case 12:
                icon[0].setX((float) (x_init_cord - sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord - sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord - sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord - sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord - sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord - sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                break;
            case 20:
                icon[0].setX((float) (x_init_cord + sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord + sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord + sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord + sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord + sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord + sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                break;
            case 21:
                icon[0].setX((float) (x_init_cord + sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord + sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord + sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord + sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord + sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord + sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                int distance2 = x_init_cord - (int)icon[0].getX();
                Log.e(LOG_TAG, "x_init - x0 = " + distance2 + "\nx_init = "+ x_init_cord);
                break;
            case 22:
                icon[0].setX((float) (x_init_cord + sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord + sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord + sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord + sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord + sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord + sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                break;

            case 31:
                icon[0].setX((float) (x_init_cord - cos10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) sin10 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord - cos42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) sin42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord - cos74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) sin74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord + cos74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord - (float) sin74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord + cos42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord - (float) sin42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord + cos10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord - (float) sin10 * icon_distance_pxl - icon_24_dp_pxl);
                break;
        }

    }

    public static void setFavoriteShortcutGridViewPosition(GridView gridView,float gridTall, float gridWide, int x_init_cord, int y_init_cord, float mScale, int edgePosition, WindowManager windowManager, SharedPreferences sharedPreferences, int distanceFromEdgeDp, int gap) {
        float distanceFromEdge = ((float)distanceFromEdgeDp) *mScale;
        boolean isCenter = sharedPreferences.getBoolean(EdgeSettingDialogFragment.IS_CENTRE_FAVORITE, false);
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        float x = point.x;
        float y = point.y;
        if (!isCenter) {
            switch (edgePosition) {
                case 10:
                    gridView.setX(((float) x_init_cord) - distanceFromEdge - gridWide);
                    gridView.setY(0);
                    break;
                case 11:
                    gridView.setX(((float) x_init_cord) - distanceFromEdge - gridWide);
//                    if (((float) y_init_cord) - gridTall / (float)2 < 0) {
//                        gridView.setY(0);
//                    } else if (((float) y_init_cord) - gridTall /(float) 2 + gridTall > y) {
//                        gridView.setY(y - gridTall);
//                    } else {
                        gridView.setY(((float) y_init_cord) - gridTall /(float) 2);
//                    }
                    break;
                case 12:
                    gridView.setX(((float) x_init_cord) - distanceFromEdge - gridWide);
                    gridView.setY(y - gridTall);
                    break;
                case 20:
                    gridView.setX(((float) x_init_cord) + distanceFromEdge);
                    gridView.setY(0);
                    break;
                case 21:
                    gridView.setX(((float) x_init_cord) + distanceFromEdge);
//                    if (((float) y_init_cord) - gridTall /(float) 2 < 0) {
//                        gridView.setY(0);
//                    } else if (((float) y_init_cord) - gridTall /(float) 2 + gridTall > y) {
//                        gridView.setY(y - gridTall);
//                    } else {
                        gridView.setY(((float) y_init_cord) - gridTall /(float) 2);
//                    }
                    break;
                case 22:
                    gridView.setX(((float) x_init_cord) + distanceFromEdge);
                    gridView.setY(y - gridTall);
                    break;
                case 31:
                    gridView.setX(((float) x_init_cord) - gridWide /(float) 2);
                    gridView.setY(((float) y_init_cord) - distanceFromEdge - gridTall);
                    break;
            }
        } else {
            gridView.setX((x-gridWide)/2);
            gridView.setY((y-gridTall)/2);
        }

    }

    public static int isHomeOrBackOrNoti(int x_init_int, int y_init_int, int x_int, int y_int, int radius_int, float mScale, int position){
        double x_init = (double)x_init_int;
        double y_init = (double)y_init_int;
        double x = (double) x_int;
        double y = (double) y_int;
        double radius = (double) radius_int;
        double distance = Math.sqrt(Math.pow((double)x - (double)x_init,2) + Math.pow((double)y - (double) y_init, 2));
        double distanceNeeded_pxl = (double) ((35+ radius)* mScale);
        double maxAng = 0.4166*Math.PI;  // 75 degree
        double midAng = 0.3333* Math.PI; //60 degree
        double minAng = 0.0833*Math.PI; //15 degree
        double ang30 = 0.1666*Math.PI;
        double ang70 = 0.3889*Math.PI;
        double ang110 = 0.6111*Math.PI;
        double alpha;
        if (distance < distanceNeeded_pxl) {
            return 0;
        }

        if (position >= 30) {
            alpha = Math.acos((x_init - x) / distance);
        }else {
            alpha = Math.acos((y_init-y)/distance);
        }
        if (alpha < ang30) {
            return 1;
        }else if (alpha < ang70) {
            return 2;
        }else if (alpha < ang110) {
            return 3;
        }else return 4;

    }
//    public static int[] getExpandSpec(int x_init,int y_init,int rad, int distanceFromIcon,WindowManager win){
//        int[] result = new int[4];
//        int rad_pxl = dpiToPixels(rad,win);
//        int distance_pxl = dpiToPixels(distanceFromIcon,win);
//        double radian30 = 0.16667* Math.PI;
//        double sin30 = Math.sin(radian30);
//        double cos30 = Math.cos(radian30);
//        int a = 2* (int)((rad_pxl+ distance_pxl)*sin30);
//        int b = rad_pxl + distance_pxl - (int)((rad_pxl+distance_pxl)*cos30);
//        result[0] = x_init -(int)( (rad_pxl + distance_pxl)*sin30);
//        result[1] = y_init - (int)(rad_pxl*cos30) -(int)( distance_pxl*cos30) - b;
//        result[2] = result[0] + a;
//        result[3] = result[1] + b;
//        Log.e("expand", "left = " + result[0]+ "\ntop =" + result[1] + "\nright = "+ result[2] + "\nbottom = "+ result[3] +"\na = "+ a + "\nb = "+ b +"\nsin30= "
//        + sin30+ "\ncos30 = "+ cos30 + "\ndistance = " +distance_pxl + "\nradpx; = "+rad_pxl);
//        return result;
//    }

    public static int getPositionIntFromString(String position, Context context){
        String[] array = context.getResources().getStringArray(R.array.edge_dialog_spinner_array);
        if (position.equals(array[0])){
            return 10;
        }else if (position.equals(array[1])){
            return 11;
        }else if (position.equals(array[2])){
            return 12;
        }else if (position.equals(array[3])){
            return 20;
        }else if (position.equals(array[4])){
            return 21;
        }else if (position.equals(array[5])){
            return 22;
        }else if (position.equals(array[6])){
            return 31;
        }
        return 11;
    }



    // get installed apps but skip the system apps
    public static Set<PackageInfo> getInstalledApps(Context ctx) {
        final PackageManager packageManager = ctx.getPackageManager();

        final List<PackageInfo> allInstalledPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
        final Set<PackageInfo> filteredPackages = new HashSet();

        Drawable defaultActivityIcon = packageManager.getDefaultActivityIcon();

        for(PackageInfo each : allInstalledPackages) {
//            if(ctx.getPackageName().equals(each.packageName)) {
//                continue;  // skip own app
//            }

            try {
                // add only apps with application icon
                Intent intentOfStartActivity = packageManager.getLaunchIntentForPackage(each.packageName);
                if(intentOfStartActivity == null)
                    continue;

                Drawable applicationIcon = packageManager.getActivityIcon(intentOfStartActivity);
                if(applicationIcon != null && !defaultActivityIcon.equals(applicationIcon)) {
                    filteredPackages.add(each);
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.i("MyTag", "Unknown package name " + each.packageName);
            }
        }

        return filteredPackages;
    }

    public static boolean isAccessibilityEnable(Context context){
        AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List <AccessibilityServiceInfo> info = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for (AccessibilityServiceInfo info1 : info){
            String description = info1.loadDescription(context.getPackageManager());
            if (description!= null){
                if (description.equals(context.getString(R.string.accessibility_service_description))){
                    return true;
                }
            }
        }
        return false;
    }


    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static void toggleWifi(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean currentState = wifiManager.isWifiEnabled();
        wifiManager.setWifiEnabled(!currentState);
    }
    public static boolean getWifiState(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }
    public static void toggleBluetooth (Context context){
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (bluetoothAdapter == null) {
            return;
        }
        boolean bluetoothState = bluetoothAdapter.isEnabled();
        if (bluetoothState){
            bluetoothAdapter.disable();
        }else {
            bluetoothAdapter.enable();
        }

    }
    public static boolean getBluetoothState (Context context){
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (bluetoothAdapter == null) {
            return false;
        }
        return bluetoothAdapter.isEnabled();
    }
    public static boolean checkIsFlashLightAvailable (Context context){
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public static boolean getIsRotationAuto(Context context){
        int current = android.provider.Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION,0);
        return current ==1;
    }

    public static void setAutorotation(Context context){
        if(android.provider.Settings.System.getInt(context.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 0) == 1)
        {
            android.provider.Settings.System.putInt(context.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 0);
        }
        else{
            android.provider.Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
        }
    }

    public static void showAudioDialog(Context context){
        Intent intent = new Intent(context,AudioDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static int getActionFromLabel(Context context, String label) {
        if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_wifi))) {
            return Shortcut.ACTION_WIFI;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_bluetooth))) {
            return Shortcut.ACTION_BLUETOOTH;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_rotation))) {
            return Shortcut.ACTION_ROTATION;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_power_menu))) {
            return Shortcut.ACTION_POWER_MENU;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_home))) {
            return Shortcut.ACTION_HOME;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_back))) {
            return Shortcut.ACTION_BACK;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_noti))) {
            return Shortcut.ACTION_NOTI;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_last_app))) {
            return Shortcut.ACTION_LAST_APP;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_call_log))) {
            return Shortcut.ACTION_CALL_LOGS;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_contact))) {
            return Shortcut.ACTION_CONTACT;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_dial))) {
            return Shortcut.ACTION_DIAL;
        }else return -1;
    }

    public static int getSizeOfFavoriteGrid(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        return sharedPreferences.getInt(EdgeSettingDialogFragment.NUM_OF_GRID_ROW_KEY, 5) * sharedPreferences.getInt(EdgeSettingDialogFragment.NUM_OF_GRID_COLUMN_KEY, 4);
    }

    public static Bitmap getBitmapFromAction(Context context,SharedPreferences sharedPreferences, int actionButton) {
        String action = MainActivity.ACTION_NONE;
        switch (actionButton) {
            case 1:
                action = sharedPreferences.getString(EdgeSettingDialogFragment.ACTION_1_KEY, MainActivity.ACTION_HOME);
                break;
            case 2:
                action = sharedPreferences.getString(EdgeSettingDialogFragment.ACTION_2_KEY, MainActivity.ACTION_BACK);
                break;
            case 3:
                action = sharedPreferences.getString(EdgeSettingDialogFragment.ACTION_3_KEY, MainActivity.ACTION_NONE);
                break;
            case 4:
                action = sharedPreferences.getString(EdgeSettingDialogFragment.ACTION_4_KEY, MainActivity.ACTION_NOTI);
                break;
        }
        switch (action) {
            case MainActivity.ACTION_HOME:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_home);
            case MainActivity.ACTION_BACK:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_back);
            case MainActivity.ACTION_WIFI:
                if (getWifiState(context)) {
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_wifi);
                }else
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_wifi_no_bound_off);

            case MainActivity.ACTION_NOTI:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_expand);
            case MainActivity.ACTION_BLUETOOTH:
                if (getBluetoothState(context)) {
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_bluetooth);
                } else {
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_bluetooth_no_bound_off);
                }
            case MainActivity.ACTION_ROTATE:
                if (getIsRotationAuto(context)) {
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_rotate_no_bound_auto);
                } else {
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_rotate_no_bound_lock);
                }
            case MainActivity.ACTION_POWER_MENU:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_power_menu_no_bound);
            case MainActivity.ACTION_LAST_APP:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_last_app);
            case MainActivity.ACTION_CALL_LOGS:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_call_log);
            case MainActivity.ACTION_CONTACT:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_contact);
            case MainActivity.ACTION_DIAL:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_dial);
            case MainActivity.ACTION_NONE:
                return null;

        }
        return null;
    }

    public static Bitmap getBitmapForOuterSetting(Context context,String action) {
        switch (action) {
            case MainActivity.ACTION_HOME:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_home);
            case MainActivity.ACTION_BACK:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_back);
            case MainActivity.ACTION_WIFI:
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_wifi);
            case MainActivity.ACTION_NOTI:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_expand);
            case MainActivity.ACTION_BLUETOOTH:
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_bluetooth);
            case MainActivity.ACTION_ROTATE:
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_rotate_no_bound_auto);
            case MainActivity.ACTION_POWER_MENU:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_power_menu_no_bound);
            case MainActivity.ACTION_LAST_APP:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_last_app);
            case MainActivity.ACTION_CALL_LOGS:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_call_log);
            case MainActivity.ACTION_CONTACT:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_contact);
            case MainActivity.ACTION_DIAL:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_dial);
            case MainActivity.ACTION_NONE:
                return null;
        }
        return null;
    }

    public static String getLabelForOuterSetting(Context context, String action) {
        switch (action) {
            case MainActivity.ACTION_HOME:
                return context.getString(R.string.setting_shortcut_home);
            case MainActivity.ACTION_BACK:
                return context.getString(R.string.setting_shortcut_back);
            case MainActivity.ACTION_WIFI:
                return context.getString(R.string.setting_shortcut_wifi);
            case MainActivity.ACTION_NOTI:
                return context.getString(R.string.setting_shortcut_noti);
            case MainActivity.ACTION_BLUETOOTH:
                return context.getString(R.string.setting_shortcut_bluetooth);
            case MainActivity.ACTION_ROTATE:
                return context.getString(R.string.setting_shortcut_rotation);
            case MainActivity.ACTION_POWER_MENU:
                return context.getString(R.string.setting_shortcut_power_menu);
            case MainActivity.ACTION_LAST_APP:
                return context.getString(R.string.last_app);
            case MainActivity.ACTION_CALL_LOGS:
                return context.getString(R.string.setting_shortcut_call_log);
            case MainActivity.ACTION_CONTACT:
                return context.getString(R.string.setting_shortcut_contact);
            case MainActivity.ACTION_DIAL:
                return context.getString(R.string.setting_shortcut_dial);
            case MainActivity.ACTION_NONE:
                return context.getString(R.string.setting_shortcut_none);
        }
        return context.getString(R.string.setting_shortcut_none);
    }

    public static void homeAction(Context context, View v,String className, String packageName) {
        AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
        event1.setClassName(className);
        event1.getText().add("home");
        event1.setAction(1);
        event1.setPackageName(packageName);
        event1.setEnabled(true);
        AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
        recordCompat.setSource(v);
        if (Utility.isAccessibilityEnable(context)) {
            manager.sendAccessibilityEvent(event1);
        }else Toast.makeText(context, R.string.ask_user_to_turn_on_accessibility_toast, Toast.LENGTH_LONG).show();
    }



    public static void backAction(Context context, View v, String className, String packageName) {
        AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
        event1.setClassName(className);
        event1.getText().add("back");
        event1.setAction(2);
        event1.setPackageName(packageName);
        event1.setEnabled(true);
        AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
        recordCompat.setSource(v);
        if (Utility.isAccessibilityEnable(context)) {
            manager.sendAccessibilityEvent(event1);
        }else Toast.makeText(context,R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
    }

    public static void powerAction(Context context, View v, String className, String packageName) {
        AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
        event1.setClassName(className);
        event1.getText().add("power");
        event1.setAction(3);
        event1.setPackageName(packageName);
        event1.setEnabled(true);
        AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
        recordCompat.setSource(v);
        if (Utility.isAccessibilityEnable(context)) {
            manager.sendAccessibilityEvent(event1);
        }else Toast.makeText(context,R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
    }

    public static void notiAction(Context context, View v, String className, String packageName) {
        Object sbservice =context.getSystemService("statusbar");
        try {
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method showsb;
            if (Build.VERSION.SDK_INT >= 17) {
                showsb = statusbarManager.getMethod("expandNotificationsPanel");
            } else {
                showsb = statusbarManager.getMethod("expand");
            }
            showsb.invoke(sbservice);
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, "ClassNotFound " + e);
        } catch (NoSuchMethodException e) {
            AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
            event1.setClassName(className);
            event1.getText().add("noti");
            event1.setAction(4);
            event1.setPackageName(packageName);
            event1.setEnabled(true);
            AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
            AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
            recordCompat.setSource(v);
            if (Utility.isAccessibilityEnable(context)) {
                manager.sendAccessibilityEvent(event1);
            }else Toast.makeText(context,R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
        } catch (IllegalAccessException e) {
            AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
            event1.setClassName(className);
            event1.getText().add("noti");
            event1.setAction(4);
            event1.setPackageName(packageName);
            event1.setEnabled(true);
            AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
            AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
            recordCompat.setSource(v);
            if (Utility.isAccessibilityEnable(context)) {
                manager.sendAccessibilityEvent(event1);
            }else Toast.makeText(context,R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "IllegalAccessException " + e);
        } catch (InvocationTargetException e) {
            AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
            event1.setClassName(className);
            event1.getText().add("noti");
            event1.setAction(4);
            event1.setPackageName(packageName);
            event1.setEnabled(true);
            AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
            AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
            recordCompat.setSource(v);
            if (Utility.isAccessibilityEnable(context)) {
                manager.sendAccessibilityEvent(event1);
            }else Toast.makeText(context,R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "InvocationTargetException " + e);
        }
    }

    public static void lastAppAction(Context context, String packageName) {
        Intent extApp = null;

        extApp = context.getPackageManager().getLaunchIntentForPackage(packageName);


        if (extApp != null) {
            ComponentName componentName = extApp.getComponent();
            Intent startApp = new Intent(Intent.ACTION_MAIN, null);
            startApp.addCategory(Intent.CATEGORY_LAUNCHER);
            startApp.setComponent(componentName);
            startApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(startApp);
            Log.e(LOG_TAG, "packageToSwitch = " + packageName);
        } else Log.e(LOG_TAG, "extApp = null ");
    }

    public static void callLogsAction(Context context) {
        Intent launchCallLog = new Intent(Intent.ACTION_VIEW);
        launchCallLog.setData(CallLog.Calls.CONTENT_URI);
        launchCallLog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(launchCallLog);
    }

    public static void contactAction(Context context) {
        Intent launchContact = new Intent(Intent.ACTION_VIEW);
        launchContact.setData(Uri.parse("content://contacts/people/"));
        launchContact.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(launchContact);
    }

    public static void dialAction(Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }


    public static void executeAction(Context context, String action, View v, String className, String packageName, String lastAppPackageName) {
        switch (action) {
            case MainActivity.ACTION_HOME:
//                homeAction(context,v,className,packageName);
                homeAction(context, v, className, packageName);
                break;
            case MainActivity.ACTION_BACK:
                backAction(context,v,className,packageName);
                break;
            case MainActivity.ACTION_NOTI:
                notiAction(context,v,className,packageName);
                break;
            case MainActivity.ACTION_WIFI:
                toggleWifi(context);
                break;
            case MainActivity.ACTION_BLUETOOTH:
                toggleBluetooth(context);
                break;
            case MainActivity.ACTION_ROTATE:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Utility.setAutorotation(context);
                } else {
                    if (Settings.System.canWrite(context)) {
                        Utility.setAutorotation(context);
                    } else {
                        Intent notiIntent = new Intent();
                        notiIntent.setAction(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        PendingIntent notiPending = PendingIntent.getActivity(context, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        builder.setContentTitle(context.getString(R.string.ask_for_write_setting_notification_title)).setContentText(context.getString(R.string.ask_for_write_setting_notification_text)).setSmallIcon(R.drawable.ic_settings_white_36px)
                                .setContentIntent(notiPending)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setDefaults(NotificationCompat.DEFAULT_SOUND);
                        Notification notification = builder.build();
                        NotificationManager notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
                        notificationManager.notify(22, notification);
                    }
                }
                break;
            case MainActivity.ACTION_NONE:
                //nothing
                break;
            case MainActivity.ACTION_POWER_MENU:
                powerAction(context,v,className,packageName);
                break;
            case MainActivity.ACTION_LAST_APP:
                lastAppAction(context, lastAppPackageName);
                break;
            case MainActivity.ACTION_CONTACT:
                contactAction(context);
                break;
            case MainActivity.ACTION_CALL_LOGS:
                callLogsAction(context);
                break;
            case MainActivity.ACTION_DIAL:
                dialAction(context);
                break;
        }
    }

    public static View disPlayClock(Context context, WindowManager windowManager, boolean isDisableAnimation) {
        Calendar c = Calendar.getInstance();
        int mHour;
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMMM");
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.clock, null);
        TextView hourTextView = (TextView) view.findViewById(R.id.clock_time_in_hour);
        TextView dateTextView = (TextView) view.findViewById(R.id.clock_time_in_date);
        TextView batteryLifeTextView = (TextView) view.findViewById(R.id.clock_battery_life);
        String batteryString = context.getString(R.string.batterylife)+ " "+ getBatteryLevel(context) + "%";
//        String batteryString =getBatteryLevel(context) + "%";
        batteryLifeTextView.setText(batteryString);
        dateTextView.setText(dateFormat.format(c.getTime()));
        if (!DateFormat.is24HourFormat(context))
        {
            SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm");

            hourTextView.setText(hourFormat.format(c.getTime()));

        }
        else {
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
            hourTextView.setText(hourFormat.format(c.getTime()));
        }
        WindowManager.LayoutParams paramsEdge1 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                PixelFormat.TRANSLUCENT);
        paramsEdge1.gravity = Gravity.TOP|Gravity.CENTER_HORIZONTAL;
        windowManager.addView(view, paramsEdge1);
//        hourTextView.startAnimation(animationSet);
//        dateTextView.startAnimation(animationSet);
//        batteryLifeTextView.startAnimation(animationSet);
        if (!isDisableAnimation) {
            view.setAlpha(0f);
            view.animate().alpha(1f).setDuration(120);
        }
        return view;

    }
    public static int getBatteryLevel(Context context) {
        Intent batteryIntent =context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level,scale;
        try {
            level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "Null when get battery life");
            return 50;
        }
        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50;
        }

        return((int) (((float)level / (float)scale) * 100.0f));
    }

    public static String getForegroundApp(Context context) {
        ActivityManager.RunningAppProcessInfo resultInfo=null, info=null;
        ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List <ActivityManager.RunningAppProcessInfo> l = mActivityManager.getRunningAppProcesses();
        Iterator<ActivityManager.RunningAppProcessInfo> i = l.iterator();
        Log.e(LOG_TAG, "list size = " + l.size());
        while(i.hasNext()){
            info = i.next();
            if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    ) {
                if (!isRunningService(context, info.processName, mActivityManager)) {
                    resultInfo = info;
                    break;
                } else {
                    Log.e(LOG_TAG, "info = " + info.processName);
                }
            }
        }
        if (resultInfo != null && resultInfo.importanceReasonComponent!= null) {
            String packageReturn = resultInfo.importanceReasonComponent.getPackageName();
            if (!packageReturn.isEmpty()) {
                return packageReturn;
            }else return null;
        }else return null;

    }

    public static boolean isRunningService(Context mContext, String processname, ActivityManager mActivityManager){
        if(processname==null || processname.isEmpty())
            return false;

        ActivityManager.RunningServiceInfo service;

        if(mActivityManager==null)
            mActivityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List <ActivityManager.RunningServiceInfo> l = mActivityManager.getRunningServices(9999);
        Iterator <ActivityManager.RunningServiceInfo> i = l.iterator();
        while(i.hasNext()){
            service = i.next();
            if(service.process.equals(processname))
                return true;
        }

        return false;
    }



}
