package org.de_studio.recentappswitcher;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import java.util.HashSet;
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

    public static int isHomeOrBackOrNoti(int x_init, int y_init, int x, int y, int radius, float mScale, int position){

        double distance = Math.sqrt(Math.pow((double)x - (double)x_init,2) + Math.pow((double)y - (double) y_init, 2));
        double distanceNeeded_pxl = (double) ((35+ radius)* mScale);
        double maxAng = 0.4166*Math.PI;  // 75 degree
        double midAng = 0.3333* Math.PI; //60 degree
        double minAng = 0.0833*Math.PI; //15 degree
        if (position >= 30){
            double oriAng = Math.acos((double)(Math.abs(y - y_init))/distance);
            boolean isLeft = x < x_init;
            if (isLeft){
                if (oriAng > minAng & oriAng < midAng & distance > distanceNeeded_pxl){
                    return 2;
                }else if (oriAng > midAng & oriAng < 0.5*Math.PI & distance > distanceNeeded_pxl){
                    return 1;
                }else return 0;

            }else {
                if (oriAng > minAng & oriAng < 0.5*Math.PI & distance > distanceNeeded_pxl){
                    return 3;
                }else return 0;
            }
        }else {
            boolean isTop = y < y_init;
            double oriAng = Math.acos((double)(Math.abs(x - x_init))/distance);

            if (isTop){
                if (oriAng>minAng & oriAng < midAng & distance > distanceNeeded_pxl){
                    return 2;
                }else if (oriAng>midAng & oriAng < 0.5*Math.PI & distance >= distanceNeeded_pxl){
                    return 1;
                }else return 0;
            }else {
                if (oriAng>minAng & oriAng < 0.5*Math.PI & distance >= distanceNeeded_pxl){
                    return 3;
                }else return 0;
            }
        }

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
            if(ctx.getPackageName().equals(each.packageName)) {
                continue;  // skip own app
            }

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
        BluetoothAdapter bluetoothAdapter = (BluetoothAdapter) context.getSystemService(Context.BLUETOOTH_SERVICE);
        boolean bluetoothState = bluetoothAdapter.isEnabled();
        if (bluetoothState){
            bluetoothAdapter.disable();
        }else {
            bluetoothAdapter.enable();
        }
    }
    public static boolean getBluetoothState (Context context){
        BluetoothAdapter bluetoothAdapter = (BluetoothAdapter) context.getSystemService(Context.BLUETOOTH_SERVICE);
        return bluetoothAdapter.isEnabled();
    }
    public static boolean checkIsFlashLightAvailable (Context context){
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

}
