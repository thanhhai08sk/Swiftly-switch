package org.de_studio.recentappswitcher;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
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
import android.os.Handler;
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

import org.de_studio.recentappswitcher.circleFavoriteSetting.CircleFavoriteSettingView;
import org.de_studio.recentappswitcher.dialogActivity.AudioDialogActivity;
import org.de_studio.recentappswitcher.edgeService.NewServicePresenter;
import org.de_studio.recentappswitcher.edgeService.NewServiceView;
import org.de_studio.recentappswitcher.gridFavoriteSetting.GridFavoriteSettingView;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Edge;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;
import org.de_studio.recentappswitcher.service.ChooseActionDialogActivity;
import org.de_studio.recentappswitcher.service.NotiDialog;
import org.de_studio.recentappswitcher.service.ScreenBrightnessDialogActivity;
import org.de_studio.recentappswitcher.service.VolumeDialogActivity;
import org.de_studio.recentappswitcher.shortcut.FlashService;
import org.de_studio.recentappswitcher.shortcut.FlashServiceM;
import org.de_studio.recentappswitcher.shortcut.LockAdmin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
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




    public static void setFavoriteGridViewPosition(View gridView,boolean isCenter ,  float gridTall, float gridWide, float xInit, float yInit, float mScale, int edgePosition, WindowManager windowManager, int distanceFromEdgeDp, int distanceVertical) {
//        Log.e(TAG, "setFavoriteGridViewPosition: width " + gridWide + "\ntall " + gridTall + "\nxInit " + xInit + "\nyInit " + yInit +
//                "\noffsetHorizontal " + distanceFromEdgeDp + "\noffsetVertical " + distanceVertical);
        float distanceFromEdge = ((float)distanceFromEdgeDp) *mScale;
        float distanceVerticalFromEdge = ((float)distanceVertical)* mScale;
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        float x = point.x;
        float y = point.y;
        if (!isCenter) {
            switch (edgePosition) {
                case 10:
                    gridView.setX(( xInit) - distanceFromEdge - gridWide);
                    gridView.setY(yInit - gridTall/2);
                    break;
                case 11:
                    float x1 = (xInit) - distanceFromEdge - gridWide;
                    float y1 = (yInit) - gridTall / (float) 2;
                    gridView.setX(x1);
                    gridView.setY(y1);
                    break;
                case 12:
                    gridView.setX(( xInit) - distanceFromEdge - gridWide);
                    if (y - yInit - gridTall / 2 < distanceVerticalFromEdge) {
                        gridView.setY(y - gridTall - distanceVerticalFromEdge);
                    } else {
                        gridView.setY(yInit - gridTall/2);
                    }
                    break;
                case 20:
                    gridView.setX(( xInit) + distanceFromEdge);
                    gridView.setY(yInit - gridTall/2);
                    break;
                case 21:
                    gridView.setX(( xInit) + distanceFromEdge);
                    gridView.setY(( yInit) - gridTall /(float) 2);
                    break;
                case 22:
                    gridView.setX(( xInit) + distanceFromEdge);
                    if (y - yInit - gridTall / 2 < distanceVerticalFromEdge) {
                        gridView.setY(y - gridTall - distanceVerticalFromEdge);
                    } else {
                        gridView.setY(yInit - gridTall/2);
                    }
                    break;
                case 31:
                    gridView.setX(( xInit) - gridWide /(float) 2);
                    gridView.setY(( yInit) - distanceVerticalFromEdge - gridTall);
                    break;
            }
        } else {
            gridView.setX((x-gridWide)/2);
            gridView.setY((y-gridTall)/2);
        }

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
        }  else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_dial))) {
            return Item.ACTION_DIAL;
        }  else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_flash_light))) {
            return Item.ACTION_FLASH_LIGHT;
        }  else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_screen_lock))) {
            return Item.ACTION_SCREEN_LOCK;
        }else return -1;
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

    public static void screenLockAction(Context context) {
        final DevicePolicyManager pm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName cm = new ComponentName(context, LockAdmin.class);
        Log.e(TAG, "screenLockAction: ");

        if (pm.isAdminActive(cm)) {
            Log.e(TAG, "screenLockAction: permission ok");
            Runnable lockRunnable = new Runnable() {

                @Override
                public void run() {
                    pm.lockNow();
                }
            };
            Handler handler = new Handler();
            handler.post(lockRunnable);

        } else {
            startNotiDialog(context,NotiDialog.PHONE_ADMIN_PERMISSION);
        }
    }

    public static void brightnessAction(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(context)) {
            Intent intent = new Intent(context, ScreenBrightnessDialogActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            context.startActivity(intent);
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

    public static void lastAppAction(Context context, String packageName) {
        startApp(packageName,context);
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

    public static void setRinggerMode(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (getRingerMode(context)) {
            case 0:
                manager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
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

    public static String getForegroundApp(Context context) {
        ActivityManager.RunningAppProcessInfo resultInfo=null, info=null;
        ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List <ActivityManager.RunningAppProcessInfo> l = mActivityManager.getRunningAppProcesses();
        Iterator<ActivityManager.RunningAppProcessInfo> i = l.iterator();
        Log.e(TAG, "list size = " + l.size());
        while(i.hasNext()){
            info = i.next();
            if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    ) {
                if (!isRunningService(context, info.processName, mActivityManager)) {
                    resultInfo = info;
                    break;
                } else {
                    Log.e(TAG, "info = " + info.processName);
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
            case Item.ACTION_SCREEN_LOCK:
                return ContextCompat.getDrawable(context, R.drawable.ic_screen_lock);
        }
        return null;
    }


    public static   Bitmap createAndSaveFolderThumbnail(Slot folder, Realm realm, Context context) {
        float mScale = context. getResources().getDisplayMetrics().density;
        int width =(int)( 48*mScale);
        int height = (int) (48 * mScale);
        int smallWidth, smallHeight;
        smallWidth = width/2;
        smallHeight = height/2;
        PackageManager packageManager = context.getPackageManager();
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        Drawable drawable;
        Item item = null;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.WHITE);
        int gap1dp = (int) (mScale);
        boolean isFolderEmpty = true;
        Log.e(TAG, "createAndSaveFolderThumbnail: folderId " + folder.slotId + "\nsize " + folder.items.size());

        for (int i = 0; i < 4; i++) {
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
                            drawable = packageManager.getApplicationIcon(item.getPackageName());
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        drawIconToFolderCanvas(width, height, smallWidth, smallHeight, canvas, drawable, gap1dp, i);

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

//        File myDir = context.getFilesDir();
//        String fname = "folder-"+ folder.slotId +".png";
//        File file = new File (myDir, fname);
//        if (file.exists ()) file.delete ();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
//            out.flush();
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        realm.beginTransaction();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        folder.iconBitmap = stream.toByteArray();
        realm.commitTransaction();
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

    public static void askForAdminPermission(final Context context) {
            final ComponentName cm = new ComponentName(context, LockAdmin.class);
            final DevicePolicyManager pm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (!pm.isAdminActive(cm)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.admin_permission)
                        .setMessage(R.string.admin_permission_explain)
                        .setPositiveButton(R.string.go_to_setting, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cm);
                                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                        context.getString(R.string.admin_desc));
                                context.startActivity(intent);

                            }
                        });
                builder.show();
            }

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
        edgePara.width = (int) (edgeWidth * mScale);
        edgePara.height = (int) (edgeHeight *mScale);
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

    public static void showTextDialog(Context context, int stringId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(stringId);
        builder.setPositiveButton(R.string.app_tab_fragment_ok_button, null);
        builder.create().show();
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
        switch (item.type) {
            case Item.TYPE_APP:
                try {
//                    Log.e(TAG, "setItemIcon: app: " + item.getPackageName() );
                    Drawable defaultDrawable = packageManager.getApplicationIcon(item.getPackageName());
                    Drawable iconPackDrawable;
                    if (iconPack!=null) {
                        iconPackDrawable = iconPack.getDrawableIconForPackage(item.getPackageName(), defaultDrawable);
                        if (iconPackDrawable == null) {
                            iconPackDrawable = defaultDrawable;
                        }
                        icon.setImageDrawable(iconPackDrawable);
                    } else {
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
                byte[] byteArray = item.iconBitmap;
                try {
                    if (byteArray != null) {
                        icon.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
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
                        return ((BitmapDrawable) defaultDrawable).getBitmap();
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
        if (item.iconBitmap == null) {
            return;
        }
        switch (item.action) {
            case Item.ACTION_WIFI:
                if (getWifiState(context)) {
                    icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap, 0, item.iconBitmap.length));
                } else {
                    icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap2, 0, item.iconBitmap2.length));
                }
                break;
            case Item.ACTION_BLUETOOTH:
                if (getBluetoothState(context)) {
                    icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap, 0, item.iconBitmap.length));
                } else {
                    icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap2, 0, item.iconBitmap2.length));
                }
                break;
            case Item.ACTION_ROTATION:
                if (getIsRotationAuto(context)) {
                    icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap, 0, item.iconBitmap.length));
                } else {
                    icon.setImageBitmap(BitmapFactory.decodeByteArray(item.iconBitmap2, 0, item.iconBitmap2.length));
                }
                break;
            case Item.ACTION_RINGER_MODE:
                switch (getRingerMode(context)) {
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
                if (NewServiceView.FLASH_LIGHT_ON) {
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
            case Item.ACTION_SCREEN_LOCK:
                setBitMapForActionItemFromResId(item, 1, R.drawable.ic_screen_lock, context);
                break;

        }

    }

    public static Item getActionItemFromResult(ResolveInfo mResolveInfo,final PackageManager packageManager, Realm realm, Intent data) {
        String label = (String) data.getExtras().get(Intent.EXTRA_SHORTCUT_NAME);
        String stringIntent = ((Intent) data.getExtras().get(Intent.EXTRA_SHORTCUT_INTENT)).toUri(0);
        String packageName =  mResolveInfo.activityInfo.packageName;
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
        bmp.recycle();
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

    public static void startSlot(Slot slot, String lastAppPackageName, Context context, int contactAction, int showing, String currentCollectionId, NewServicePresenter presenter) {
        switch (slot.type) {
            case Slot.TYPE_ITEM:
                startItem(slot.stage1Item, lastAppPackageName, context, contactAction);
                break;
            case Slot.TYPE_NULL:
                if (currentCollectionId != null) {
                    Intent intent;
                    switch (showing) {
                        case NewServicePresenter.Showing.SHOWING_GRID:
                            intent = GridFavoriteSettingView.getIntent(context, currentCollectionId);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        case NewServicePresenter.Showing.SHOWING_CIRCLE_AND_ACTION:
                            intent = CircleFavoriteSettingView.getIntent(context, currentCollectionId);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        case NewServicePresenter.Showing.SHOWING_CIRCLE_ONLY:
                            intent = CircleFavoriteSettingView.getIntent(context, currentCollectionId);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                    }
                }
                break;
            case Slot.TYPE_FOLDER:
                presenter.requestShowingFolder(slot);
                break;
        }
    }

    public static void startItem(Item item, String lastAppPackageName, Context context,int contactAction) {
        Log.e(TAG, "startItem: start action " + item.toString());
        switch (item.type) {
            case Item.TYPE_APP:
                startApp(item.getPackageName(), context);
                break;
            case Item.TYPE_ACTION:
                startAction(item.action, context, lastAppPackageName);
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
            Log.e(TAG, "startShortcut: exception when start Shortcut shortcut");
        }
    }

    private static void startApp(String packageName, Context context) {
        Intent extApp = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (packageName != null && extApp != null) {
            if (packageName.equals("com.devhomc.search")) {
                extApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(extApp);
            } else {
//                Log.e(TAG, "startApp: " + item.getPackageName() + "\nContext = " + context.toString());
//                extApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                ContextCompat.startActivities(context, new Intent[]{extApp});
//
                ComponentName componentName = extApp.getComponent();
                Intent startAppIntent = new Intent(Intent.ACTION_MAIN);
                startAppIntent.setComponent(componentName);
                startAppIntent.addFlags(1064960);
                startAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startAppIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startAppIntent.setFlags(270532608 | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startAppIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                context.startActivity(startAppIntent);
                ContextCompat.startActivities(context, new Intent[]{startAppIntent});
            }
        } else {
            Log.e(TAG, "extApp of shortcut = null ");
        }
    }

    private static void startContact(Item item, Context context, int contactAction) {
        switch (contactAction) {
            case Cons.ACTION_CHOOSE:
                Intent intent = new Intent(context, ChooseActionDialogActivity.class);
                intent.putExtra("number", item.getNumber());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                context.startActivity(intent);
                break;
            case Cons.ACTION_CALL:
                String url = "tel:"+ item.getNumber();
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(callIntent);
                } else {
                    Toast.makeText(context, context.getString(R.string.missing_call_phone_permission), Toast.LENGTH_LONG).show();
                }
                break;
            case Cons.ACTION_SMS:
                Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                        + item.getNumber()));
                smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(smsIntent);
                break;
        }
    }

    public static void startAction(int action, Context context, String lastAppPackageName) {
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
                Utility.lastAppAction(context, lastAppPackageName);
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
                Utility.setRinggerMode(context);
                break;
            case Item.ACTION_FLASH_LIGHT:
                Utility.flashLightAction3(context);
                break;
            case Item.ACTION_SCREEN_LOCK:
                Utility.screenLockAction(context);
                break;

        }
    }

    public static void setFolderPosition(float triggerX, float triggerY, RecyclerView folderView, RecyclerView gridView, int edgePosition, float mScale) {
        int folderWide = folderView.getWidth();
        int folderTall = folderView.getHeight();
        float x;
        float y;
        Log.e(TAG, "setFolderPosition: folderWide = " + folderWide + "\nfolderTall = " + folderTall + "\ntriggerX = " + triggerX + "\ntriggerY = " + triggerY);

        if (Utility.rightLeftOrBottom(edgePosition) == Cons.POSITION_RIGHT) {
            if (triggerX + folderWide / 2 < gridView.getX() + gridView.getWidth()) {
                x = triggerX - folderWide / 2;
            } else {
                x = gridView.getX() + gridView.getWidth() - folderWide;
            }
        } else {
            if (triggerX - folderWide / 2 > gridView.getX()) {
                x = triggerX - folderWide / 2;
            } else {
                x = gridView.getX();
            }
        }
        y = triggerY - folderTall / 2;
        Log.e(TAG, "setFolderPosition: x = " + x + "\ny = " + y + "\nposition = "+ rightLeftOrBottom(edgePosition));
        folderView.setX(x);
        folderView.setY(y);
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
            case MainActivity.ACTION_SCREEN_LOCK:
                return Item.ACTION_SCREEN_LOCK;
        }
        return -1;
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
                            !(android.os.Build.MANUFACTURER.toLowerCase().contains("sam") || android.os.Build.MANUFACTURER.toLowerCase().contains("zte"))) {

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
                        Log.e(TAG, "unzip: ZipEntry name = " + ze.getName());
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

    public static boolean isFree(Context context) {
        return context.getPackageName().equals(Cons.FREE_VERSION_PACKAGE_NAME);
    }

    public static boolean isFreeAndOutOfTrial(Context context, SharedPreferences sharedPreferences) {
        return isFree(context) && System.currentTimeMillis() - sharedPreferences.getLong(Cons.BEGIN_DAY_KEY, System.currentTimeMillis()) > Cons.TRIAL_TIME;
    }

    public static void getProVersion(Context context) {
        Uri uri = Uri.parse("mbarket://details?id=" + Cons.PRO_VERSION_PACKAGE_NAME);
        Intent gotoMarket = new Intent(Intent.ACTION_VIEW, uri);
        gotoMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(gotoMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + Cons.PRO_VERSION_PACKAGE_NAME)));
        }
    }

    public static void showProOnlyDialog(final Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.pro_only)
                .content(R.string.pro_only_content)
                .positiveText(R.string.main_edge_switch_2_trial_buy_pro_button)
                .negativeText(R.string.edge_dialog_cancel_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Utility.getProVersion(context);
                    }
                })
                .show();
    }

    public static void noticeUserAboutScreenLock(final Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.admin_permission)
                .content(R.string.admin_permission_notice)
                .positiveText(R.string.go_to_setting)
                .negativeText(R.string.edge_dialog_cancel_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ComponentName cm = new ComponentName(context, LockAdmin.class);
                        Intent buttonIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        buttonIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cm);
                        buttonIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                context.getString(R.string.admin_permission_notice));
                        context.startActivity(buttonIntent);
                    }
                })
                .show();
    }
}
