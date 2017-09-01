package org.de_studio.recentappswitcher;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.de_studio.recentappswitcher.base.BaseActivity;
import org.de_studio.recentappswitcher.dialogActivity.AudioDialogActivity;
import org.de_studio.recentappswitcher.edgeService.NewServiceView;
import org.de_studio.recentappswitcher.main.MainView;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Edge;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;
import org.de_studio.recentappswitcher.screenshot.ScreenshotView;
import org.de_studio.recentappswitcher.service.ChooseActionDialogActivity;
import org.de_studio.recentappswitcher.service.NotiDialog;
import org.de_studio.recentappswitcher.service.ScreenBrightnessDialogActivity;
import org.de_studio.recentappswitcher.service.VolumeDialogActivity;
import org.de_studio.recentappswitcher.shortcut.FlashService;
import org.de_studio.recentappswitcher.shortcut.FlashServiceM;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import io.realm.Realm;
import io.realm.RealmList;
import rx.subjects.PublishSubject;

import static org.de_studio.recentappswitcher.Cons.RINGER_MODE_NORMAL;
import static org.de_studio.recentappswitcher.Cons.RINGER_MODE_SILENT;
import static org.de_studio.recentappswitcher.Cons.RINGER_MODE_VIBRATE;

/**
 * Created by hai on 12/19/2015.
 */
public  class Utility {
    private static final String TAG = Utility.class.getSimpleName();

    public static int dpToPixel(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,(float) (dp), metrics);
    }




    public static void setFavoriteGridViewPosition(View gridView,boolean isCenter ,  float gridTall, float gridWide, float xInit, float yInit, float mScale, int edgePosition, WindowManager windowManager,
                                                   int distanceFromEdgeDp, int distanceVertical,
                                                   Point windowSize) {
//        Log.e(TAG, "setFavoriteGridViewPosition: width " + gridWide + "\ntall " + gridTall + "\nxInit " + xInit + "\nyInit " + yInit +
//                "\noffsetHorizontal " + distanceFromEdgeDp + "\noffsetVertical " + distanceVertical);
//        long time = System.currentTimeMillis();
        float distanceFromEdge = ((float)distanceFromEdgeDp) *mScale;
        float distanceVerticalFromEdge = ((float)distanceVertical)* mScale;
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        float screenWidth = point.x;
        float screenHeight = point.y;
        if (!isCenter) {
            switch (edgePosition) {
                case 10:
                    gridView.setX( xInit - distanceFromEdge - gridWide);
                    if (yInit - gridTall / 2 < distanceVerticalFromEdge) {
                        gridView.setY(distanceVerticalFromEdge);
                    } else {
                        gridView.setY(yInit - gridTall/2);
                    }
                    break;
                case 11:
                    gridView.setX( xInit - distanceFromEdge - gridWide);
                    if (yInit - gridTall / 2 < distanceVerticalFromEdge) {
                        gridView.setY(distanceVerticalFromEdge);
                    } else if (screenHeight - yInit - gridTall / 2 < distanceVerticalFromEdge) {
                        gridView.setY(screenHeight - gridTall - distanceVerticalFromEdge);
                    }else gridView.setY(yInit - gridTall/2);
                    break;
                case 12:
                    gridView.setX(( xInit) - distanceFromEdge - gridWide);
                    if (screenHeight - yInit - gridTall / 2 < distanceVerticalFromEdge) {
                        gridView.setY(screenHeight - gridTall - distanceVerticalFromEdge);
                    } else {
                        gridView.setY(yInit - gridTall/2);
                    }
                    break;
                case 20:
                    gridView.setX(( xInit) + distanceFromEdge);
                    if (yInit - gridTall / 2 < distanceVerticalFromEdge) {
                        gridView.setY(distanceVerticalFromEdge);
                    } else if (screenHeight - yInit - gridTall / 2 < distanceVerticalFromEdge) {
                        gridView.setY(screenHeight - gridTall - distanceVerticalFromEdge);
                    }else gridView.setY(yInit - gridTall/2);
                    break;
                case 21:
                    gridView.setX(( xInit) + distanceFromEdge);
//                    gridView.setY(( yInit) - gridTall /(float) 2);
                    if (yInit - gridTall / 2 < distanceVerticalFromEdge) {
                        gridView.setY(distanceVerticalFromEdge);
                    } else if (screenHeight - yInit - gridTall / 2 < distanceVerticalFromEdge) {
                        gridView.setY(screenHeight - gridTall - distanceVerticalFromEdge);
                    }else gridView.setY(yInit - gridTall/2);
                    break;
                case 22:
                    gridView.setX(( xInit) + distanceFromEdge);
//                    if (screenHeight - yInit - gridTall / 2 < distanceVerticalFromEdge) {
//                        gridView.setY(screenHeight - gridTall - distanceVerticalFromEdge);
//                    } else {
//                        gridView.setY(yInit - gridTall/2);
//                    }
                    if (yInit - gridTall / 2 < distanceVerticalFromEdge) {
                        gridView.setY(distanceVerticalFromEdge);
                    } else if (screenHeight - yInit - gridTall / 2 < distanceVerticalFromEdge) {
                        gridView.setY(screenHeight - gridTall - distanceVerticalFromEdge);
                    }else gridView.setY(yInit - gridTall/2);
                    break;
                case 31:
                    if (xInit - gridWide / (float) 2 > distanceFromEdge &&
                            xInit + gridWide / (float) 2 < (windowSize.x - distanceFromEdge)) {
                        gridView.setX((xInit) - gridWide / (float) 2);
                    } else {
                        if (xInit - gridWide / (float) 2 < distanceFromEdge) {
                            gridView.setX(distanceFromEdge);
                        } else {
                            gridView.setX(windowSize.x - distanceFromEdge - gridWide);
                        }
                    }
                    gridView.setY(( yInit) - distanceVerticalFromEdge - gridTall);
                    break;
            }
        } else {
            gridView.setX((screenWidth-gridWide)/2);
            gridView.setY((screenHeight-gridTall)/2);
        }

//        Log.e(TAG, "setFavoriteGridViewPosition: time spent = " + (System.currentTimeMillis() - time));
    }




    public static int getPositionIntFromString(String position, Context context){
        String[] array = context.getResources().getStringArray(R.array.edge_positions_array);
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
    public static Set<PackageInfo> getInstalledApps(PackageManager packageManager) {

        final List<PackageInfo> allInstalledPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
        final Set<PackageInfo> filteredPackages = new HashSet();

        Drawable defaultActivityIcon = packageManager.getDefaultActivityIcon();

        for(PackageInfo each : allInstalledPackages) {
            try {
                // add only apps with application icon
                Intent intentOfStartActivity = packageManager.getLaunchIntentForPackage(each.packageName);
                if(intentOfStartActivity == null)
                    continue;
                try {
                    Drawable applicationIcon = packageManager.getActivityIcon(intentOfStartActivity);
                    if (applicationIcon != null && !defaultActivityIcon.equals(applicationIcon)) {
                        filteredPackages.add(each);
                    }
                } catch (OutOfMemoryError error) {
                    error.printStackTrace();
                    Log.e(TAG, "getInstalledApps: " + error);
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
        if (info == null) {
            return false;
        }
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
            return Item.ACTION_WIFI;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_bluetooth))) {
            return Item.ACTION_BLUETOOTH;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_rotation))) {
            return Item.ACTION_ROTATION;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_power_menu))) {
            return Item.ACTION_POWER_MENU;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_home))) {
            return Item.ACTION_HOME;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_back))) {
            return Item.ACTION_BACK;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_noti))) {
            return Item.ACTION_NOTI;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_last_app))) {
            return Item.ACTION_LAST_APP;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_call_log))) {
            return Item.ACTION_CALL_LOGS;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_contact))) {
            return Item.ACTION_CONTACT;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_recent))) {
            return Item.ACTION_RECENT;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_volume))) {
            return Item.ACTION_VOLUME;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_brightness))) {
            return Item.ACTION_BRIGHTNESS;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_ringer_mode))) {
            return Item.ACTION_RINGER_MODE;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_dial))) {
            return Item.ACTION_DIAL;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_flash_light))) {
            return Item.ACTION_FLASH_LIGHT;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.screen_shot))) {
            return Item.ACTION_SCREENSHOT;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_search_shortcuts))) {
            return Item.ACTION_SEARCH_SHORTCUTS;
        }else {
            throw new IllegalArgumentException("do not support this shortcut " + label);
        }
    }

    public static void startHomeAction(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startBackAction(Context context) {
        Log.e(TAG, "startBackAction: ");
        context.sendBroadcast(new Intent(Cons.ACTION_BACK));
        if (!Utility.isAccessibilityEnable(context)) {
            startNotiDialog(context,NotiDialog.ACCESSIBILITY_PERMISSION);
        }
    }


    public static void startRecentAction(Context context) {
        context.sendBroadcast(new Intent(Cons.ACTION_RECENT));
        if (!Utility.isAccessibilityEnable(context)) {
            startNotiDialog(context,NotiDialog.ACCESSIBILITY_PERMISSION);
        }
    }

    public static void volumeAction(Context context) {
        Intent intent = new Intent(context, VolumeDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(intent);

    }

    public static void startNotiDialog(Context context, int type) {
        context.startActivity(NotiDialog.getIntent(context, type));
    }



    public static void flashLightAction3(Context context) {
        Intent i = new Intent(context, Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? FlashServiceM.class : FlashService.class);
        if (!NewServiceView.FLASH_LIGHT_ON) {
            context.startService(i);
            NewServiceView.FLASH_LIGHT_ON = true;
        } else {
            context.stopService(i);
            NewServiceView.FLASH_LIGHT_ON = false;
        }
    }


    public static void brightnessAction(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(context)) {
//            Intent intent = new Intent(context, ScreenBrightnessDialogActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//            context.startActivity(intent);

            Intent intent = new Intent(context, ScreenBrightnessDialogActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(context, 0, intent, 0);
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }

        } else {
            startNotiDialog(context, NotiDialog.WRITE_SETTING_PERMISSION);
        }

    }



    public static void startPowerAction(Context context) {

        context.sendBroadcast(new Intent(Cons.ACTION_POWER_MENU));
        if (!Utility.isAccessibilityEnable(context)) {
            startNotiDialog(context,NotiDialog.ACCESSIBILITY_PERMISSION);
        }
    }

    public static void screenshotAction(Context context) {
        Intent intent = new Intent(context, ScreenshotView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(intent);
    }

    public static void startNotiAction(Context context) {
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
            Log.e(TAG, "ClassNotFound " + e);
        } catch (NoSuchMethodException e) {
            notiActionByAccessibility(context);
        } catch (IllegalAccessException e) {
            notiActionByAccessibility(context);
            Log.e(TAG, "IllegalAccessException " + e);
        } catch (InvocationTargetException e) {
            notiActionByAccessibility(context);
            Log.e(TAG, "InvocationTargetException " + e);
        }
    }


    private static void notiActionByAccessibility(Context context) {
        context.sendBroadcast(new Intent(Cons.ACTION_NOTI));
        if (!Utility.isAccessibilityEnable(context)) {
            startNotiDialog(context,NotiDialog.ACCESSIBILITY_PERMISSION);
        }
    }

    public static void lastAppAction(Context context, String packageName, boolean useTransition) {
        startApp(packageName, context, false, useTransition);
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

    public static int getRingerMode(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (manager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                return 0;
            case AudioManager.RINGER_MODE_VIBRATE:
                return 1;
            case AudioManager.RINGER_MODE_SILENT:
                return 2;
            default:
                return 0;
        }
    }

    public static void setRinggerMode(Context context, int ringerModeAction) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (getRingerMode(context)) {
            case 0:
                manager.setRingerMode(ringerModeAction == Cons.RINGER_MODE_ACTION_SOUND_AND_VIBRATE?
                        AudioManager.RINGER_MODE_VIBRATE : AudioManager.RINGER_MODE_SILENT);
                break;
            case 1:
                manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
            case 2:
                manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
        }
    }

    public static void dialAction(Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }


    public static int getBatteryLevel(Context context) {
        Intent batteryIntent =context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level,scale;
        try {
            level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        } catch (NullPointerException e) {
            Log.e(TAG, "Null when get battery life");
            return 50;
        }
        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50;
        }

        return((int) (((float)level / (float)scale) * 100.0f));
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }




    public static Drawable getDrawableForAction(Context context, int action) {
        switch (action) {
            case Item.ACTION_WIFI:
                return ContextCompat.getDrawable(context, R.drawable.ic_wifi);
            case Item.ACTION_BLUETOOTH:
                return ContextCompat.getDrawable(context, R.drawable.ic_bluetooth);
            case Item.ACTION_ROTATION:
                return ContextCompat.getDrawable(context, R.drawable.ic_rotation);
            case Item.ACTION_POWER_MENU:
                return ContextCompat.getDrawable(context, R.drawable.ic_power_menu);
            case Item.ACTION_HOME:
                return ContextCompat.getDrawable(context, R.drawable.ic_home);
            case Item.ACTION_BACK:
                return ContextCompat.getDrawable(context, R.drawable.ic_back);
            case Item.ACTION_NOTI:
                return ContextCompat.getDrawable(context, R.drawable.ic_notification);
            case Item.ACTION_LAST_APP:
                return ContextCompat.getDrawable(context, R.drawable.ic_last_app);
            case Item.ACTION_CALL_LOGS:
                return ContextCompat.getDrawable(context, R.drawable.ic_call_log);
            case Item.ACTION_DIAL:
                return ContextCompat.getDrawable(context, R.drawable.ic_dial);
            case Item.ACTION_CONTACT:
                return ContextCompat.getDrawable(context, R.drawable.ic_contact);
            case Item.ACTION_RECENT:
                return ContextCompat.getDrawable(context, R.drawable.ic_recent);
            case Item.ACTION_VOLUME:
                return ContextCompat.getDrawable(context, R.drawable.ic_volume);
            case Item.ACTION_BRIGHTNESS:
                return ContextCompat.getDrawable(context, R.drawable.ic_screen_brightness);
            case Item.ACTION_RINGER_MODE:
                return ContextCompat.getDrawable(context, R.drawable.ic_sound_normal);
            case Item.ACTION_FLASH_LIGHT:
                return ContextCompat.getDrawable(context, R.drawable.ic_flash_light);
            case Item.ACTION_SCREENSHOT:
                return ContextCompat.getDrawable(context, R.drawable.ic_screenshot2);
            case Item.ACTION_SEARCH_SHORTCUTS:
                return ContextCompat.getDrawable(context, R.drawable.ic_search_shortcuts);
            default:
                throw new IllegalArgumentException("do not support this action: " + action);
        }
    }



    public static Bitmap createAndSaveFolderThumbnail(final Slot folder, Realm realm, Context context, IconPackManager.IconPack iconPack) {
        float mScale = context. getResources().getDisplayMetrics().density;
        int width =(int)( 48*mScale);
        int height = (int) (48 * mScale);
        int smallWidth, smallHeight;
        smallWidth = width/2;
        smallHeight = height/2;
        PackageManager packageManager = context.getPackageManager();
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        final Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        Drawable drawable;
        Item item = null;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.WHITE);
        int gap1dp = (int) (mScale);
        boolean isFolderEmpty = true;
        Log.e(TAG, "createAndSaveFolderThumbnail: folderId " + folder.slotId + "\nsize " + folder.items.size());
        int noOfSmallIcon = folder.items.size() >= 4 ? 4 : folder.items.size();
        for (int i = 0; i < noOfSmallIcon; i++) {
            drawable = null;
            if (i < folder.items.size()) {
                item = folder.items.get(i);
            }
            if (item != null) {
                Log.e(TAG, "createAndSaveFolderThumbnail: item type = " + item.type +"\nid = " + item.itemId);
                isFolderEmpty = false;
                switch (item.type) {
                    case Item.TYPE_APP:
                        try {
                            Drawable defaultDrawable = packageManager.getApplicationIcon(item.getPackageName());

//                            Bitmap defaultBm = ((BitmapDrawable) defaultDrawable).getBitmap();
                            Bitmap defaultBm = null;
                            if (defaultDrawable instanceof BitmapDrawable) {
                                defaultBm = ((BitmapDrawable) defaultDrawable).getBitmap();
                            } else {
                                defaultBm = Bitmap.createBitmap(defaultDrawable.getIntrinsicWidth(), defaultDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                                Canvas canvasForDefaultBitmap = new Canvas(defaultBm);
                                defaultDrawable.setBounds(0, 0, canvasForDefaultBitmap.getWidth(), canvasForDefaultBitmap.getHeight());
                                defaultDrawable.draw(canvasForDefaultBitmap);
                            }

                            if (iconPack!=null) {
                                drawable = new BitmapDrawable(context.getResources(), iconPack.getIconForPackage(item.packageName, defaultBm));

                            } else {
                                drawable = defaultDrawable;
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.e(TAG, "NameNotFound " + e);
                        }
                        drawIconToFolderCanvas(width, height, smallWidth, smallHeight, canvas, drawable, gap1dp, i);


//                        try {
//                            drawable = packageManager.getApplicationIcon(item.getPackageName());
//                        } catch (PackageManager.NameNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                        drawIconToFolderCanvas(width, height, smallWidth, smallHeight, canvas, drawable, gap1dp, i);

                        break;
                    case Item.TYPE_ACTION:
                        drawable = getDrawableForAction(context, item.getAction());
                        drawIconToFolderCanvas(width, height, smallWidth, smallHeight, canvas, drawable, gap1dp, i);

                        break;
                    case Item.TYPE_CONTACT:
                        String uri = item.iconUri;
                        if (uri != null) {
                            try {
                                Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(uri));
                                drawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap1);
                                ((RoundedBitmapDrawable) drawable).setCircular(true);
                            } catch (IOException e) {
                                e.printStackTrace();
                                drawable = ContextCompat.getDrawable(context, R.drawable.ic_contact_default);
                            }
                        } else {
                            drawable = ContextCompat.getDrawable(context, R.drawable.ic_contact_default);
                        }
                        drawIconToFolderCanvas(width, height, smallWidth, smallHeight, canvas, drawable, gap1dp, i);

                        break;
                    case Item.TYPE_DEVICE_SHORTCUT:
                        byte[] byteArray = item.iconBitmap;
                        try {
                            Bitmap bmp;
                            Resources resources = packageManager.getResourcesForApplication(item.getPackageName());
                            if (byteArray != null) {

                                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                                drawable = new BitmapDrawable(resources, bmp);

                            }
                        } catch (Exception e) {
                            Log.e(TAG, "getView: can not set imageview for item item");
                        }
                        drawIconToFolderCanvas(width, height, smallWidth, smallHeight, canvas, drawable, gap1dp, i);

                        break;

                }

            }
        }
        if (isFolderEmpty) {
            drawable = ContextCompat.getDrawable(context, R.drawable.ic_folder);
            if (drawable != null) {
                drawable.setBounds(0, 0, width, height);
                drawable.draw(canvas);
            }

        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.e(TAG, "execute: in transaction");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                folder.iconBitmap = stream.toByteArray();
                folder.useIconSetByUser = false;
            }
        });
        if (folder.iconBitmap != null) {
            Log.e(TAG, "createAndSaveFolderThumbnail: ok ");
        } else {
            Log.e(TAG, "createAndSaveFolderThumbnail: array null");
        }
        return bitmap;
    }

    private static void drawIconToFolderCanvas(int width, int height, int smallWidth, int smallHeight, Canvas canvas, Drawable drawable, int gap1dp, int i) {
        if (drawable != null) {
            switch (i) {
                case 0:
                    drawable.setBounds(0, 0, smallWidth - gap1dp, smallHeight - gap1dp);
                    drawable.draw(canvas);
                    break;
                case 1:
                    drawable.setBounds(smallWidth + gap1dp, 0, width, smallHeight - gap1dp);
                    drawable.draw(canvas);
                    break;
                case 2:
                    drawable.setBounds(0, smallHeight + gap1dp, smallWidth - gap1dp, height);
                    drawable.draw(canvas);
                    break;
                case 3:
                    drawable.setBounds(smallWidth + gap1dp, smallHeight + gap1dp, width, height);
                    drawable.draw(canvas);
                    break;
            }
        }
    }


    public static void saveShortcutBitmap(Bitmap bmp, int mPosition, Context context) {
        Log.e(TAG, "getShortcutBitmap: ");
        File myDir = context.getFilesDir();
        String fname = "shortcut-"+ mPosition +".png";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean checkDrawPermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }


    public static boolean checkContactPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }


    public static int rightLeftOrBottom(int position) {
        switch (position / 10) {
            case 1:
                return Cons.POSITION_RIGHT;
            case 2:
                return Cons.POSITION_LEFT;
            case 3:
                return Cons.POSITION_BOTTOM;
        }
        return -1;
    }

    public static WindowManager.LayoutParams getEdgeLayoutPara(int avoidKeyboardOption, float mScale, int edgePosition, int edgeWidth, int edgeHeight, int edgeOffset) {
        WindowManager.LayoutParams edgePara;
        boolean fullEdge = false;
        switch (rightLeftOrBottom(edgePosition)) {
            case Cons.POSITION_BOTTOM:
                if (edgeWidth == Cons.EDGE_LENGTH_MAX) {
                    fullEdge = true;
                }
                break;
            default:
                if (edgeHeight == Cons.EDGE_LENGTH_MAX) {
                    fullEdge = true;
                }
                break;
        }
        switch (avoidKeyboardOption) {
            case Edge.KEYBOARD_OPTION_PLACE_UNDER:
                edgePara = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        PixelFormat.TRANSLUCENT);
                break;
            case Edge.KEYBOARD_OPTION_STEP_ASIDE:
                edgePara = new WindowManager.LayoutParams();
                edgePara.type = 2002;
                edgePara.gravity = 53;
                edgePara.flags = 40;
                edgePara.width = 1;
                edgePara.height = -1;
                edgePara.format = -2;
                break;
            default:
                edgePara = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        PixelFormat.TRANSLUCENT);
                break;
        }

        if (avoidKeyboardOption != Edge.KEYBOARD_OPTION_NONE) {
            edgePara.flags |= 131072;
        }
        switch (edgePosition) {
            case 10:
                edgePara.gravity = Gravity.TOP | Gravity.RIGHT;
                break;
            case 11:
                edgePara.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                break;
            case 12:
                edgePara.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                break;
            case 20:
                edgePara.gravity = Gravity.TOP | Gravity.LEFT;
                break;
            case 21:
                edgePara.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                break;
            case 22:
                edgePara.gravity = Gravity.BOTTOM | Gravity.LEFT;
                break;
            case 31:
                edgePara.gravity = Gravity.BOTTOM | Gravity.CENTER;
                break;
        }


        if (edgePosition == 12 | edgePosition == 22) {
            edgePara.y = (int) (edgeOffset * mScale);
        } else if (edgePosition == 31) {
            edgePara.x = -(int) (edgeOffset * mScale);
        } else {
            edgePara.y = -(int) (edgeOffset * mScale);
        }

        switch (rightLeftOrBottom(edgePosition)) {
            case Cons.POSITION_BOTTOM:
                if (edgeWidth != Cons.EDGE_LENGTH_MAX) {
                    edgePara.width = (int) (edgeWidth * mScale);
                }
                edgePara.height = (int) (edgeHeight *mScale);
                break;
            default:
                if (edgeHeight != Cons.EDGE_LENGTH_MAX) {
                    edgePara.height = (int) (edgeHeight *mScale);
                }
                edgePara.width = (int) (edgeWidth * mScale);
                break;
        }

        return edgePara;

    }


    public static void startService(Context context) {
        Log.e(TAG, "startService: ");
        context.startService(new Intent(context, NewServiceView.class));
    }

    public static void stopService(Context context) {
        Log.e(TAG, "stopService: ");
        context.stopService(new Intent(context, NewServiceView.class));
    }

    public static void restartService(Context context) {
        Log.e(TAG, "restartService: ");
        stopService(context);
        startService(context);
    }

    public static void showSimpleDialog(Context context, int contentId) {
        new MaterialDialog.Builder(context)
                .content(contentId)
                .positiveText(R.string.app_tab_fragment_ok_button)
                .show();
    }

    public static MaterialDialog showProgressDialog(Context context, int titleRes, int contentRes) {
        return new MaterialDialog.Builder(context)
                .title(titleRes)
                .content(contentRes)
                .progress(true, 0)
                .show();
    }

    public static void setSlotIcon(Slot slot, Context context, ImageView icon, PackageManager packageManager, IconPackManager.IconPack iconPack, boolean isDark, boolean showIconState) {
        switch (slot.type) {
            case Slot.TYPE_ITEM:
                setItemIcon(slot.stage1Item, context, icon, packageManager, iconPack, showIconState);
                break;
            case Slot.TYPE_FOLDER:
                byte[] byteArray = slot.iconBitmap;
                if (byteArray != null) {
                    icon.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
                } else {
                    icon.setImageResource(R.drawable.ic_folder);
                }

                break;
            case Slot.TYPE_RECENT:
                icon.setImageResource(R.drawable.ic_recent_app_slot);
                if (isDark) {
                    icon.setColorFilter(R.color.button_54_black);
                }
                break;
            case Slot.TYPE_EMPTY:
                icon.setImageDrawable(null);
                break;
            case Slot.TYPE_NULL:
                icon.setImageResource(R.drawable.ic_add_circle_outline_white_48dp);
                if (isDark) {
                    icon.setColorFilter(R.color.button_54_black);
                }
                break;
        }
    }


    public static void setItemIcon(Item item, Context context, ImageView icon, PackageManager packageManager, IconPackManager.IconPack iconPack, boolean showIconState) {
        if (item == null) {
            return;
        }
        if (!setItemIconFromBitmap(item, icon, showIconState,context)) {
            switch (item.type) {
                case Item.TYPE_APP:
                        try {
                            Drawable defaultDrawable = packageManager.getApplicationIcon(item.getPackageName());
                            try {
                                Bitmap defaultBm = ((BitmapDrawable) defaultDrawable).getBitmap();
                                if (iconPack!=null) {
                                    Bitmap iconBitmap = iconPack.getIconForPackage(item.packageName, defaultBm);
                                    icon.setImageBitmap(iconBitmap);
                                } else {
                                    icon.setImageDrawable(defaultDrawable);
                                }
                            } catch (ClassCastException e) {
                                e.printStackTrace();
//                                Log.e(TAG, "setItemIcon: not a BitmapDrawable");
                                icon.setImageDrawable(defaultDrawable);
                            }

                        } catch (PackageManager.NameNotFoundException e) {
                            Log.e(TAG, "NameNotFound " + e);
                        }
                    break;
                case Item.TYPE_ACTION:
                    if (item.iconBitmap == null) {
                        break;
                    }
                    if (showIconState) {
                        setActionIconWithState(item, icon, context);
                    } else {
                        icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap, 0, item.iconBitmap.length));
                    }
                    break;
                case Item.TYPE_DEVICE_SHORTCUT:
                    setItemIconFromBitmap(item, icon, showIconState, context);
                    break;
                case Item.TYPE_CONTACT:
                    Uri person = ContentUris.withAppendedId(
                            ContactsContract.Contacts.CONTENT_URI, item.contactId);
                    Uri photo = Uri.withAppendedPath(person,
                            ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                    if (photo != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photo);
                            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
                            drawable.setCircular(true);
                            icon.setImageDrawable(drawable);
                            icon.setColorFilter(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                            icon.setImageResource(R.drawable.ic_contact_default);
                        } catch (SecurityException e) {
                            Toast.makeText(context, context.getString(R.string.missing_contact_permission), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        icon.setImageResource(R.drawable.ic_contact_default);
                    }
                    break;
                case Item.TYPE_SHORTCUTS_SET:
                    if (item.iconBitmap != null) {
                        icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap, 0, item.iconBitmap.length));
                    }
                    break;
            }

        }
    }

    private static boolean setItemIconFromBitmap(Item item, ImageView icon, boolean showState, Context context) {


        if (showState && item.type.equals(Item.TYPE_ACTION)) {
            setActionIconWithState(item, icon, context);
            return true;
        } else {
            byte[] byteArray = item.iconBitmap;
            if (byteArray != null) {
                icon.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
                return true;
            }
        }
        return false;
    }

    public static Bitmap convertDrawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getItemBitmap(Item item, Context context, IconPackManager.IconPack iconPack) {
        switch (item.type) {
            case Item.TYPE_APP:
                try {
                    Drawable defaultDrawable = context.getPackageManager().getApplicationIcon(item.getPackageName());
                    Drawable iconPackDrawable;
                    if (iconPack!=null) {
                        iconPackDrawable = iconPack.getDrawableIconForPackage(item.getPackageName(), defaultDrawable);
                        if (iconPackDrawable == null) {
                            iconPackDrawable = defaultDrawable;
                        }
                        return ((BitmapDrawable) iconPackDrawable).getBitmap();
                    } else {
                        try {
                            return ((BitmapDrawable) defaultDrawable).getBitmap();
                        } catch (ClassCastException e) {
                            return convertDrawableToBitmap(defaultDrawable);
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "NameNotFound " + e);
                }
                break;
            case Item.TYPE_ACTION:
                return BitmapFactory.decodeByteArray(item.iconBitmap, 0, item.iconBitmap.length);
            case Item.TYPE_DEVICE_SHORTCUT:
                byte[] byteArray = item.iconBitmap;
                try {
                    if (byteArray != null) {
                        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "getView: can not set imageview for shortcut shortcut");
                }
                break;
            case Item.TYPE_CONTACT:
                String thumbnaiUri = item.iconUri;
                if (thumbnaiUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(thumbnaiUri));
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
                        drawable.setCircular(true);
                        return drawable.getBitmap();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.ic_contact_default)).getBitmap();
                    } catch (SecurityException e) {
                        Toast.makeText(context, context.getString(R.string.missing_contact_permission), Toast.LENGTH_LONG).show();
                    }
                } else {
                    return ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.ic_contact_default)).getBitmap();
                }
                break;
            case Item.TYPE_SHORTCUTS_SET:
                if (item.iconBitmap == null) {
                    return null;
                }
                return BitmapFactory.decodeByteArray(item.iconBitmap, 0, item.iconBitmap.length);
        }
        return null;
    }

    public static void setSlotLabel(Slot slot, Context context, TextView label) {
        switch (slot.type) {
            case Slot.TYPE_ITEM:
                Item item = slot.stage1Item;
                if (item != null) {
                    label.setText(item.label);
                }
                break;
            case Slot.TYPE_FOLDER:
                label.setText(context.getString(R.string.setting_shortcut_folder));
                break;
            case Slot.TYPE_RECENT:
                label.setText(context.getString(R.string.recent_app));
                break;
            case Slot.TYPE_EMPTY:
                label.setText("");
                break;
            case Slot.TYPE_NULL:
                label.setText(context.getString(R.string.empty));
                break;
        }
    }


    public static void setActionIconWithState(Item item, ImageView icon, Context context) {
        setActionIconWithState(item, icon, context, -1);
    }

    public static void setActionIconWithState(Item item, ImageView icon, Context context, int state) {
        if (item.iconBitmap == null) {
            return;
        }
        boolean enable;
        switch (item.action) {
            case Item.ACTION_WIFI:
                enable = state != -1 ? state == 1 : getWifiState(context);
                if (enable) {
                    icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap, 0, item.iconBitmap.length));
                } else {
                    icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap2, 0, item.iconBitmap2.length));
                }
                break;
            case Item.ACTION_BLUETOOTH:
                enable = state != -1 ? state == 1 : getBluetoothState(context);
                if (enable) {
                    icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap, 0, item.iconBitmap.length));
                } else {
                    icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap2, 0, item.iconBitmap2.length));
                }
                break;
            case Item.ACTION_ROTATION:
                enable = state != -1 ? state == 1 : getIsRotationAuto(context);
                if (enable) {
                    icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap, 0, item.iconBitmap.length));
                } else {
                    icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap2, 0, item.iconBitmap2.length));
                }
                break;
            case Item.ACTION_RINGER_MODE:
                int currentState = state != -1 ? state - 1 : getRingerMode(context);
                switch (currentState) {
                    case RINGER_MODE_NORMAL:
                        icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap, 0, item.iconBitmap.length));
                        break;
                    case RINGER_MODE_VIBRATE:
                        icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap2, 0, item.iconBitmap2.length));
                        break;
                    case RINGER_MODE_SILENT:
                        icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap3, 0, item.iconBitmap3.length));
                        break;
                }
                break;
            case Item.ACTION_FLASH_LIGHT:
                enable = state != -1 ? state == 1 : NewServiceView.FLASH_LIGHT_ON;
                if (enable) {
                    icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap, 0, item.iconBitmap.length));
                } else {
                    icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap2, 0, item.iconBitmap2.length));
                }
                break;
            default:
                icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap, 0, item.iconBitmap.length));
                break;
        }
    }


    public static void setIconBitmapsForActionItem(Context context, Item item) {
        switch (item.action) {
            case Item.ACTION_WIFI:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_wifi, context);
                setBitMapForActionItemFromResId(item, 2, R.drawable.ic_wifi_off, context);
                break;
            case Item.ACTION_BLUETOOTH:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_bluetooth, context);
                setBitMapForActionItemFromResId(item, 2, R.drawable.ic_bluetooth_off, context);
                break;
            case Item.ACTION_ROTATION:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_rotation, context);
                setBitMapForActionItemFromResId(item, 2, R.drawable.ic_rotation_lock, context);
                break;
            case Item.ACTION_POWER_MENU:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_power_menu, context);
                break;
            case Item.ACTION_HOME:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_home, context);
                break;
            case Item.ACTION_BACK:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_back, context);
                break;
            case Item.ACTION_NOTI:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_notification, context);
                break;
            case Item.ACTION_LAST_APP:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_last_app, context);
                break;
            case Item.ACTION_CALL_LOGS:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_call_log, context);
                break;
            case Item.ACTION_DIAL:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_dial, context);
                break;
            case Item.ACTION_CONTACT:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_contact, context);
                break;
            case Item.ACTION_RECENT:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_recent, context);
                break;
            case Item.ACTION_VOLUME:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_volume, context);
                break;
            case Item.ACTION_BRIGHTNESS:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_screen_brightness, context);
                break;
            case Item.ACTION_RINGER_MODE:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_sound_normal, context);
                setBitMapForActionItemFromResId(item, 2, R.drawable.ic_sound_vibrate, context);
                setBitMapForActionItemFromResId(item, 3, R.drawable.ic_sound_silent, context);
                break;
            case Item.ACTION_FLASH_LIGHT:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_flash_light, context);
                setBitMapForActionItemFromResId(item, 2, R.drawable.ic_flash_light_off, context);
                break;
            case Item.ACTION_SCREENSHOT:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_screenshot2, context);
                break;
            case Item.ACTION_SEARCH_SHORTCUTS:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_search_shortcuts, context);
                break;
            default:
                throw new IllegalArgumentException("do not support this " + item.action);
        }

    }

    public static Item getActionItemFromResult(ResolveInfo mResolveInfo, final PackageManager packageManager, Realm realm, Intent data) {
        if (mResolveInfo == null) {
            return null;
        }
        String label = (String) data.getExtras().get(Intent.EXTRA_SHORTCUT_NAME);
        String stringIntent = ((Intent) data.getExtras().get(Intent.EXTRA_SHORTCUT_INTENT)).toUri(0);
        String packageName = mResolveInfo.activityInfo.packageName;
        final String itemId = Item.TYPE_DEVICE_SHORTCUT + stringIntent;
        Item realmItem = realm.where(Item.class).equalTo(Cons.ITEM_ID, itemId).findFirst();
        if (realmItem == null) {

            int iconResId = 0;

            Bitmap bmp = null;
            Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
            if (extra != null && extra instanceof Bitmap)
                bmp = (Bitmap) extra;
            if (bmp == null) {
                extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
                if (extra != null && extra instanceof Intent.ShortcutIconResource) {
                    try {
                        Intent.ShortcutIconResource iconResource = (Intent.ShortcutIconResource) extra;
                        packageName = iconResource.packageName;
                        Resources resources = packageManager.getResourcesForApplication(iconResource.packageName);
                        iconResId = resources.getIdentifier(iconResource.resourceName, null, null);
                    } catch (Exception e) {
                        Log.e(TAG, "onActivityResult: Could not load shortcut icon:");
                    }
                }
            }
            realm.beginTransaction();
            Item item = new Item();
            item.type = Item.TYPE_DEVICE_SHORTCUT;
            item.itemId = itemId;
            item.label = label;
            item.packageName = packageName;
            item.intent = stringIntent;
            if (bmp != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                item.iconBitmap = stream.toByteArray();
            } else {
                try {
                    Resources resources = packageManager.getResourcesForApplication(item.getPackageName());
                    Bitmap bmp2 = BitmapFactory.decodeResource(resources, iconResId);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp2.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    item.iconBitmap = stream.toByteArray();
                    bmp2.recycle();
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "onActivityResult: exception when setting item bitmap");
                }


            }
            realmItem = realm.copyToRealm(item);
            realm.commitTransaction();
        } else {
            if (realmItem.iconBitmap == null && realmItem.iconResourceId != 0) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Item realmItem = realm.where(Item.class).equalTo(Cons.ITEM_ID, itemId).findFirst();

                        try {
                            Resources resources = packageManager.getResourcesForApplication(realmItem.getPackageName());
                            Bitmap bmp2 = BitmapFactory.decodeResource(resources, realmItem.iconResourceId);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bmp2.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            realmItem.iconBitmap = stream.toByteArray();
                            bmp2.recycle();
                            stream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "onActivityResult: exception when setting item bitmap");
                        }
                    }
                });
            }
        }
        return realmItem;
    }

    private static void setBitMapForActionItemFromResId(Item item, int position, int resourceId, Context context) {
        Bitmap bmp = ((BitmapDrawable) ContextCompat.getDrawable(context, resourceId)).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

        switch (position) {
            case 1:
                item.iconBitmap = stream.toByteArray();
                break;
            case 2:
                item.iconBitmap2 = stream.toByteArray();
                break;
            case 3:
                item.iconBitmap3 = stream.toByteArray();
                break;
        }
        try {
            bmp.recycle();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "setBitMapForActionItemFromResId: IOException");
        }
    }



    public static void setItemBitmapForShortcutsSet(Context context, Item item) {
        Log.e(TAG, "setItemBitmapForShortcutsSet: " + item.toString());
        if (context != null) {
            String itemId = item.itemId;
            if (itemId.contains(Collection.TYPE_GRID_FAVORITE)) {
                item.iconBitmap = getBitmapByteArrayFromResId(context, R.drawable.ic_grid_favorite_set);
            } else if (itemId.contains(Collection.TYPE_CIRCLE_FAVORITE)) {
                item.iconBitmap = getBitmapByteArrayFromResId(context, R.drawable.ic_circle_favorite_set);
            } else if (itemId.contains(Collection.TYPE_RECENT)) {
                item.iconBitmap = getBitmapByteArrayFromResId(context, R.drawable.ic_recent_set);
            }
        }
    }

    public static byte[] getBitmapByteArrayFromResId(Context context, int resId) {
        Bitmap bmp = ((BitmapDrawable) ContextCompat.getDrawable(context, resId)).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {

            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.e(TAG, "getBitmapByteArrayFromResId: ");
        }
        byte[] byteArray = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            Log.e(TAG, "getBitmapByteArrayFromResId: IOException");
            e.printStackTrace();
        }
//        bmp.recycle();
        return byteArray;
    }

    public static String getContactItemLabel(int type, String name, Context context) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return name;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return String.format("%s(%s)", name, context.getString(R.string.contact_type_work));
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return String.format("%s(%s)", name, context.getString(R.string.contact_type_home));
            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                return String.format("%s(%s)", name, context.getString(R.string.contact_type_main));
            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                return String.format("%s(%s)", name, context.getString(R.string.contact_type_work_fax));
            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                return String.format("%s(%s)", name, context.getString(R.string.contact_type_pager));
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                return String.format("%s(%s)", name, context.getString(R.string.contact_type_other));
            case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
                return String.format("%s(%s)", name, context.getString(R.string.contact_type_custom));
            default:
                return name;
        }

    }

    public static String createCollectionId(String collectionType, long number) {
        return collectionType + number;
    }

    public static String createSlotId() {
        return String.valueOf(System.currentTimeMillis() + new Random().nextLong());
    }

    public static String createCollectionLabel(String defaultLabel, long number) {
        return defaultLabel + " " + number;
    }

    public static Slot createSlotAndAddToRealm(Realm realm, String slotType) {
        Slot newSlot = new Slot();
        newSlot.slotId = Utility.createSlotId();
        newSlot.type = slotType;
        return realm.copyToRealm(newSlot);
    }

    public static void showDialogWithSeekBar(final int min, int max, int current, final String unit
            , String title
            , final PublishSubject<Integer> subject, Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_with_seek_bar, null);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        final TextView value = (TextView) view.findViewById(R.id.value);
        value.setText(current  + unit);
        seekBar.setProgress(current - min);
        seekBar.setMax(max - min);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress + min;
                value.setText(progressChanged + unit);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                subject.onNext(progressChanged);
            }
        });

        builder.setView(view).
                setTitle(title).
                setPositiveButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });
        builder.show();

    }

    public static void showDialogWithOptionToChoose(Context context, int titleId, CharSequence[] options, final PublishSubject<Void>[] subjects) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (titleId>0) {
            builder.setTitle(titleId);
        }
        builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        subjects[which].onNext(null);
                    }
                });
        builder.create().show();
    }

    public static void showDialogWithOptionToChoose(Context context, int titleId, int[] optionsResId, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (titleId > 0) {
            builder.setTitle(titleId);
        }
        CharSequence[] options = new CharSequence[optionsResId.length];
        for (int i = 0; i < options.length; i++) {
            options[i] = context.getString(optionsResId[i]);
        }
        builder.setItems(options, onClickListener);
        builder.create().show();
    }

    public static String createAppItemId(String packageName) {
        return Item.TYPE_APP + packageName;
    }

    public static String createShortcutSetItemId(String collectionId) {
        return Item.TYPE_SHORTCUTS_SET + collectionId;
    }

    public static String createActionItemId(int action) {
        return Item.TYPE_ACTION + action;
    }


    public static void startItem(Item item, String lastAppPackageName, Context context,int contactAction, int ringerModeAction, boolean onHomeScreen, boolean useTransition) {
        switch (item.type) {
            case Item.TYPE_APP:
                startApp(item.getPackageName(), context, onHomeScreen, useTransition);
                break;
            case Item.TYPE_ACTION:
                startAction(item.action, context, lastAppPackageName, ringerModeAction, useTransition);
                break;
            case Item.TYPE_CONTACT:
                startContact(item, context, contactAction);
                break;
            case Item.TYPE_DEVICE_SHORTCUT:
                startDeviceShortcut(item, context);
                break;
        }
    }

    private static void startDeviceShortcut(Item item, Context context) {
        try {
            Intent intent = Intent.parseUri(item.getIntent(), 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "startShortcut: exception when start Shortcut shortcut " + e);
        }
    }

    private static void startApp(String packageName, Context context, boolean onHomeScreen, boolean useTransition) {
        Intent extApp = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (packageName != null && extApp != null) {
            if (packageName.equals("com.devhomc.search")) {
                extApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(extApp);
            } else {
                ComponentName componentName = extApp.getComponent();
                Intent startAppIntent = new Intent(Intent.ACTION_MAIN);
                startAppIntent.setComponent(componentName);
                startAppIntent.addFlags(1064960);
                startAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startAppIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if (useTransition) {
                    startAppIntent.setFlags(270532608);
                } else {
                startAppIntent.setFlags(270532608 | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                }
                startAppIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                if (onHomeScreen) {
                    startIntentUsingPendingIntent(startAppIntent, context);
                } else {
//                    ContextCompat.startActivity(context, startAppIntent, null);
                    ContextCompat.startActivities(context, new Intent[]{startAppIntent});
                }
            }
        } else {
            Log.e(TAG, "extApp of shortcut = null " + packageName);
        }
    }


    private static void startIntentUsingPendingIntent(Intent intent, Context context) {
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent, 0);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
            context.startActivity(intent);
            ContextCompat.startActivities(context, new Intent[]{intent});
//            ContextCompat.startActivity(context, intent, null);

        }
    }

    private static void startContact(Item item, Context context, int contactAction) {
        switch (contactAction) {
            case Cons.CONTACT_ACTION_CHOOSE:
                Intent intent = new Intent(context, ChooseActionDialogActivity.class);
                intent.putExtra("number", item.getNumber());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                context.startActivity(intent);
                break;
            case Cons.CONTACT_ACTION_CALL:
                String url = "tel:"+ item.getNumber();
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(callIntent);
                } else {
                    Toast.makeText(context, context.getString(R.string.missing_call_phone_permission), Toast.LENGTH_LONG).show();
                }
                break;
            case Cons.CONTACT_ACTION_SMS:
                Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                        + item.getNumber()));
                smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(smsIntent);
                break;
        }
    }

    public static void startAction(int action, Context context, String lastAppPackageName, int ringerModeAction, boolean useTransition) {
        switch (action) {
            case Item.ACTION_WIFI:
                Utility.toggleWifi(context);
                break;
            case Item.ACTION_BLUETOOTH:
                Utility.toggleBluetooth(context);
                break;
            case Item.ACTION_ROTATION:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Utility.setAutorotation(context);
                } else {
                    if (Settings.System.canWrite(context)) {
                        Utility.setAutorotation(context);
                    } else {
                        Utility.startNotiDialog(context, NotiDialog.WRITE_SETTING_PERMISSION);
                    }
                }

                break;
            case Item.ACTION_POWER_MENU:
                Utility.startPowerAction(context);
                break;
            case Item.ACTION_HOME:
                Utility.startHomeAction(context);
                break;
            case Item.ACTION_BACK:
                Utility.startBackAction(context);
                break;
            case Item.ACTION_NOTI:
                Utility.startNotiAction(context);
                break;
            case Item.ACTION_LAST_APP:
                Utility.lastAppAction(context, lastAppPackageName, useTransition);
                break;
            case Item.ACTION_CALL_LOGS:
                Utility.callLogsAction(context);
                break;
            case Item.ACTION_DIAL:
                Log.e(TAG, "startShortcut: Start dial");
                Utility.dialAction(context);
                break;
            case Item.ACTION_CONTACT:
                Utility.contactAction(context);
                break;
            case Item.ACTION_RECENT:
                Utility.startRecentAction(context);
                break;
            case Item.ACTION_VOLUME:
                Utility.volumeAction(context);
                break;
            case Item.ACTION_BRIGHTNESS:
                Utility.brightnessAction(context);
                break;
            case Item.ACTION_RINGER_MODE:
                Utility.setRinggerMode(context, ringerModeAction);
                break;
            case Item.ACTION_FLASH_LIGHT:
                Utility.flashLightAction3(context);
                break;
            case Item.ACTION_SCREENSHOT:
                Utility.screenshotAction(context);
                break;
        }
    }

    public static void setFolderPosition(float triggerX, float triggerY, final RecyclerView folderView, int edgePosition, float mScale, float iconScale, int size, int iconSpace,
                                         int screenWidth, int screenHeight) {
        int columnCount = size <= 4 ? size : 4;
        int rowCount = size % 4 > 0 ? size / 4 + 1 : size / 4;
        int folderWide = calculateGridWide(columnCount, iconSpace, mScale, iconScale);
        int folderTall = calculateGridHeight(rowCount, iconSpace, mScale, iconScale);
        float x;
        float y;
//        Log.e(TAG, "setFolderPosition: folderWide = " + folderWide + "\nfolderTall = " + folderTall + "\ntriggerX = " + triggerX + "\ntriggerY = " + triggerY);

        if (triggerX + folderWide / 2 < screenWidth && triggerX - folderWide / 2 > 0) {
            x = triggerX - folderWide / 2;
        } else if (triggerX + folderWide / 2 >= screenWidth){
            x = screenWidth - folderWide;
        } else {
            x = 0;
        }

        if (triggerY + folderTall / 2 < screenHeight && triggerY - folderTall/2 >0) {
            y = triggerY - folderTall / 2;
        } else if (triggerY + folderTall / 2 >= screenHeight) {
            y = screenHeight - folderTall;
        } else {
            y = 0;
        }
//        Log.e(TAG, "setFolderPosition: x = " + x + "\ny = " + y + "\nposition = " + rightLeftOrBottom(edgePosition));
        folderView.setX(x);
        folderView.setY(y);
    }

    public static int calculateGridWide(int columnCount, int iconsSpace, float mScale, float iconScale) {
        int for1Icon = (int) (iconsSpace * mScale + Cons.ICON_SIZE_DEFAULT * mScale * iconScale);
        return for1Icon * columnCount + (int) (iconsSpace * mScale);
    }
    public static int calculateGridHeight(int rowCount, int iconsSpace, float mScale, float iconScale) {
        int for1Icon = (int) (iconsSpace * mScale + Cons.ICON_SIZE_DEFAULT * mScale * iconScale);
        return for1Icon * rowCount + (int) (iconsSpace * mScale);
    }



    public static int getPositionOfIntArray(int[] array, int item) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public static int getPositionOfStringArray(String[] array, String item) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }


    public static String getLabelFromPackageName(String packageName, PackageManager packageManager) {
        String label = null;
        try {
            label = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return label;
    }

    public static boolean isTrial(Context context) {
        return context.getPackageName().equals(Cons.FREE_VERSION_PACKAGE_NAME);
    }

    public static boolean isTrialAndOutOfTrialTime(Context context, SharedPreferences sharedPreferences) {
        return context.getPackageName().equals(Cons.FREE_VERSION_PACKAGE_NAME)
                && System.currentTimeMillis() - sharedPreferences.getLong(Cons.TRIAL_START_TIME_KEY, System.currentTimeMillis()) > Cons.TRIAL_TIME;
    }

    public static String getDeviceShortcutItemId(String intent) {
        return Item.TYPE_DEVICE_SHORTCUT + intent;
    }

    public static String getAppItemId(String packageName) {
        return Item.TYPE_APP + packageName;
    }

    public static int getActionFromStringAction(String action) {
        switch (action) {
            case MainActivity.ACTION_HOME:
                return Item.ACTION_HOME;
            case MainActivity.ACTION_BACK:
                return Item.ACTION_BACK;
            case MainActivity.ACTION_WIFI:
                return Item.ACTION_WIFI;
            case MainActivity.ACTION_NOTI:
                return Item.ACTION_NOTI;
            case MainActivity.ACTION_BLUETOOTH:
                return Item.ACTION_BLUETOOTH;
            case MainActivity.ACTION_ROTATE:
                return Item.ACTION_ROTATION;
            case MainActivity.ACTION_POWER_MENU:
                return Item.ACTION_POWER_MENU;
            case MainActivity.ACTION_LAST_APP:
                return Item.ACTION_LAST_APP;
            case MainActivity.ACTION_CALL_LOGS:
                return Item.ACTION_CALL_LOGS;
            case MainActivity.ACTION_CONTACT:
                return Item.ACTION_CONTACT;
            case MainActivity.ACTION_DIAL:
                return Item.ACTION_DIAL;
            case MainActivity.ACTION_RECENT:
                return Item.ACTION_RECENT;
            case MainActivity.ACTION_VOLUME:
                return Item.ACTION_VOLUME;
            case MainActivity.ACTION_BRIGHTNESS:
                return Item.ACTION_BRIGHTNESS;
            case MainActivity.ACTION_RINGER_MODE:
                return Item.ACTION_RINGER_MODE;
            case MainActivity.ACTION_FLASH_LIGHT:
                return Item.ACTION_FLASH_LIGHT;
            default:
                throw new IllegalArgumentException("do not convert this action " + action);
        }
    }

    public static void setCollectionSlotsSize(Realm inTransitionRealm, Collection collection, int size) {
        final RealmList<Slot> slots = collection.slots;
        while (slots.size() > size) {
            slots.remove(slots.size() - 1);
        }
        while (slots.size() < size) {
            slots.add(createSlotAndAddToRealm(inTransitionRealm, Slot.TYPE_ITEM));
        }
    }

    public static void generateActionItems(Realm realm, final WeakReference<Context> contextWeakReference) {
        final String[] actionStrings = contextWeakReference.get().getResources().getStringArray(R.array.setting_shortcut_array_no_folder);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (String string : actionStrings) {
                    int action = Utility.getActionFromLabel(contextWeakReference.get(), string);
                    if (action != Item.ACTION_SCREEN_LOCK ||
                            !(
                                    (android.os.Build.MANUFACTURER.toLowerCase().contains("sam") || android.os.Build.MANUFACTURER.toLowerCase().contains("zte")) &&
                                            Build.VERSION.SDK_INT == Build.VERSION_CODES.M
                            )) {

                        String itemId = Item.TYPE_ACTION + action;
                        Item item = realm.where(Item.class).equalTo(Cons.ITEM_ID, itemId).findFirst();
                        if (item == null) {
                            Log.e(TAG, "LoadActions - add action " + string);
                            Item newItem = new Item();
                            newItem.type = Item.TYPE_ACTION;
                            newItem.itemId = itemId;
                            newItem.label = string;
                            newItem.action = action;
                            Utility.setIconBitmapsForActionItem(contextWeakReference.get(), newItem);
                            realm.copyToRealm(newItem);
                        } else if (item.iconBitmap == null) {
                            Log.e(TAG, "execute: need to update action icon " + item.toString());
                            setIconBitmapsForActionItem(contextWeakReference.get(), item);
                        }
                    }

                }

            }
        });
    }

    public static void zip(String[] files, File zipFile) throws IOException {
        BufferedInputStream origin = null;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            byte data[] = new byte[1024];

            for (int i = 0; i < files.length; i++) {
                Log.e(TAG, "zip: file = " + files[i]);
                FileInputStream fi = new FileInputStream(files[i]);
                origin = new BufferedInputStream(fi, 1024);
                try {
                    ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, 1024)) != -1) {
                        out.write(data, 0, count);
                    }
                }
                finally {
                    origin.close();
                }
            }
        }
        finally {
            out.close();
        }
    }

    public static void unzip(String zipFile, String realmLocation,String sharedPreferenceLocation) throws IOException {
        int size;
        byte[] buffer = new byte[1024];

        try {
            if ( !realmLocation.endsWith("/") ) {
                realmLocation += "/";
            }
            File realmFile = new File(realmLocation);
            if(!realmFile.isDirectory()) {
                realmFile.mkdirs();
            }

            if ( !sharedPreferenceLocation.endsWith("/") ) {
                sharedPreferenceLocation += "/";
            }
            File sharedFile = new File(sharedPreferenceLocation);
            if(!sharedFile.isDirectory()) {
                sharedFile.mkdirs();
            }

            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), 1024));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = null;
                    if (ze.getName().equals(Cons.DEFAULT_REALM_NAME)) {
                        path = realmLocation + ze.getName();
                    } else if (ze.getName().equals(Cons.SHARED_PREFERENCE_NAME +".xml")) {
                        path = sharedPreferenceLocation + ze.getName();
                    } else {
                        throw new IllegalArgumentException("imcompatable file");
                    }
                    File unzipFile = new File(path);


                    if (ze.isDirectory()) {
                        if(!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        // check for and create parent directories if they don't exist
                        File parentDir = unzipFile.getParentFile();
                        if ( null != parentDir ) {
                            if ( !parentDir.isDirectory() ) {
                                parentDir.mkdirs();
                            }
                        }

                        // unzip the file
                        FileOutputStream out = new FileOutputStream(unzipFile, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, 1024);
                        try {
                            while ( (size = zin.read(buffer, 0, 1024)) != -1 ) {
                                fout.write(buffer, 0, size);
                            }

                            zin.closeEntry();
                        }
                        finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            }
            finally {
                zin.close();
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Unzip exception", e);
        }
    }

    public static void hideNotification(Context context) {
        Intent hideNotiIntent = new Intent();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            hideNotiIntent.setClassName("com.android.settings", "com.android.settings.Settings$AppNotificationSettingsActivity");
            hideNotiIntent.putExtra("app_package", context.getPackageName());
            hideNotiIntent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else {
            hideNotiIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            hideNotiIntent.addCategory(Intent.CATEGORY_DEFAULT);
            hideNotiIntent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        ContextCompat.startActivity(context, hideNotiIntent, null);
    }

    public static void pauseEdgeService(Context context) {
        Intent intent = new Intent();
        intent.setAction(Cons.ACTION_TOGGLE_EDGES);
        context.sendBroadcast(intent);
    }

    public static boolean isMashmallow() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.M;
    }

    public static boolean isKitkat() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT;
    }

    public static boolean isFree(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Cons.SHARED_PREFERENCE_NAME, 0);
        return context.getPackageName().equals(Cons.FREE_VERSION_PACKAGE_NAME) && !sharedPreferences.getBoolean(Cons.PRO_PURCHASED_KEY, false);
    }

    public static boolean isFreeAndOutOfTrial(Context context, SharedPreferences sharedPreferences) {
        return isFree(context) && System.currentTimeMillis() - sharedPreferences.getLong(Cons.BEGIN_DAY_KEY, System.currentTimeMillis()) > Cons.TRIAL_TIME;
    }

    public static void sendFeedback(Context context, boolean fromReviewRequest) {
        String[] TO = {"thanhhai08sk@gmail.com"};
        String content = new StringBuilder().append("Manufacture: ").append(Build.MANUFACTURER)
                .append("\nDevice: ")
                .append(Build.MODEL)
                .append(" - ")
                .append(Build.DEVICE)
                .append("\nAndroid: ")
                .append(Build.VERSION.RELEASE)
                .append("\n\n")
                .append(context.getString(R.string.email_prompt))
                .toString();



        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        String subject = context.getString(R.string.app_name) + (fromReviewRequest ? " feedback" : "");

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, content);


        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void getProVersion(Context context) {
        openPlayStorePage(context, Cons.PRO_VERSION_PACKAGE_NAME);
    }


    public static void openPlayStorePage(Context context, String packageName) {
        Uri uri = Uri.parse("mbarket://details?id=" + packageName);
        Intent gotoMarket = new Intent(Intent.ACTION_VIEW, uri);
        gotoMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(gotoMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    public static void openJournalItPlayStorePage(Context context) {
        openPlayStorePage(context, Cons.JOURNAL_IT_PACKAGE_NAME);
    }

    public static void showProOnlyDialog(final Activity context) {
        new MaterialDialog.Builder(context)
                .title(R.string.pro_only)
                .content(R.string.pro_only_content)
                .positiveText(R.string.main_edge_switch_2_trial_buy_pro_button)
                .negativeText(R.string.edge_dialog_cancel_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        Utility.getProVersion(context);
                        if (context instanceof BaseActivity) {
                            ((BaseActivity) context).buyPro();
                        } else {
                            Utility.getProVersion(context);
                        }
                    }
                })
                .show();
    }

    public static void noticeUserAboutScreenLock(final Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.admin_permission)
                .content(R.string.admin_permission_notice_2)
                .positiveText(R.string.button_close)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        ComponentName cm = new ComponentName(context, LockAdmin.class);
//                        Intent buttonIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//                        buttonIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cm);
//                        buttonIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
//                                context.getString(R.string.admin_permission_notice));
//                        context.startActivity(buttonIntent);
                        dialog.dismiss();
                    }
                })
                .show();
    }


    public static int calculateNoOfColumns(Context context, int iconSize) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / iconSize);
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        if (height / inSampleSize > reqHeight) {
            inSampleSize *= 2;
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromUri(Uri uri,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri.getPath(),options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(uri.getPath(),options);
    }

    public static void rebootApp(Context context) {
        Intent mStartActivity = new Intent(context.getApplicationContext(), MainView.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    public static String getSharedPreferenceFile(Context context) throws IOException {
        String sharedFile = Environment.getDataDirectory().getAbsolutePath() + "/data/" + context.getPackageName() + "/" + Cons.SHARED_PREFERENCE_FOLDER_NAME + "/" + Cons.SHARED_PREFERENCE_NAME+".xml";

        File file = new File(sharedFile);
        if (!file.exists()) {
            Log.e(TAG, "onResult: file not exist " + sharedFile);
            File file1 = new File("/data/data/org.de_studio.recentappswitcher.fastbuild/shared_prefs/");
            for (File file2 : file1.listFiles()) {
                Log.e(TAG, "onResult: file = " + file2.getAbsolutePath());

            }
            throw new IOException("Can not find shared file");
        }
        return sharedFile;
    }

    public static File createTempBackupZipFile(Context context) {
        return new File(context.getApplicationInfo().dataDir + "/" + Cons.BACKUP_FILE_NAME);
    }

    public static File createDownloadBackupZipFile() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + Cons.BACKUP_FILE_NAME);
    }

    public static void writeToStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buf = new byte[1024];
        int bytesRead;
        if (inputStream != null) {
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
        }
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service already","running");
                return true;
            }
        }
        Log.i("Service not","running");
        return false;
    }
}

