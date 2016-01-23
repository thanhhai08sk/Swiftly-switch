package org.de_studio.recentappswitcher.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by hai on 12/15/2015.
 */
public class EdgeGestureService extends Service {

    public static final Comparator<Long> DATE_DECENDING_COMPARATOR = new Comparator<Long>() {
        @Override
        public int compare(Long lhs, Long rhs) {
            if (rhs > lhs) {
                return 1;
            } else if (rhs == lhs) {
                return 0;
            } else return -1;
        }
    };
    private static final int NOTIFICATION_ID = 111;
    float mScale;
    static final String LOG_TAG = EdgeGestureService.class.getSimpleName();
    static final int EDGE_GESTURE_NOTIFICAION_ID = 10;
    private WindowManager windowManager;
    private RelativeLayout edge1View;
    private RelativeLayout edge2View;
    private MyImageView edge1Image;
    private MyImageView edge2Image;
    private FrameLayout item1View, item2View;
    public int icon_height = 48;
    public int icon_width = 48, icon_rad = 24;
    public int icon_distance = 110, distance_to_arc = 35, distance_to_arc_pxl;
    public float icon_distance_pxl, icon_24dp_in_pxls;
    public int edge1Length, edge2Length;
    public int edge1HeightPxl, edge2HeightPxl;
    public int edge1WidthPxl, edge2WidthPxl;
    public int edge1Sensivite, edge2Sensitive;
    private List<AppCompatImageView> iconImageArrayList1, iconImageArrayList2;
    private String[] packagename, favoritePackageName;
    private String launcherPackagename;
    private int[] x, y;
    private int numOfIcon;
    private boolean hasOneActive = false;
    private boolean hasVibrate = false, hasHomwBackNotiVisible = false;
    private boolean isEdge1On, isEdge2On;
    public int edge1Position, edge2Position;
    private SharedPreferences defaultShared, sharedPreferences1, sharedPreferences2, sharedPreferences_favorite, sharedPreferences_exclude;
    private AppCompatImageView[] iconImageList1, iconImageList2;
    private ExpandStatusBarView expandView, homeView, backView;
    private Vibrator vibrator;
    private int ovalOffSet, ovalRadiusPlus = 15, ovalRadiusPlusPxl;
    private long holdTime = 650, firstTouchTime;
    private boolean touched = false, switched = false, itemSwitched = false, isOutOfTrial = false, isFreeVersion = false;
    private String[] spinnerEntries;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (getPackageName().equals(MainActivity.FREE_VERSION_PACKAGE_NAME) ) isFreeVersion = true;
        Set<String> favoriteSet = sharedPreferences_favorite.getStringSet(EdgeSettingDialogFragment.FAVORITE_KEY, new HashSet<String>());
        favoritePackageName = new String[favoriteSet.size()];
        favoriteSet.toArray(favoritePackageName);
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo res = getPackageManager().resolveActivity(launcherIntent, 0);
        if (res.activityInfo != null) {
            launcherPackagename = res.activityInfo.packageName;
        } else launcherPackagename = "";
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        edge1View = (RelativeLayout) layoutInflater.inflate(R.layout.edge_view, null);
        edge1Image = (MyImageView) edge1View.findViewById(R.id.edge_image);
        ViewGroup.LayoutParams edge1ImageLayoutParams = edge1Image.getLayoutParams();
        if (Utility.getPositionIntFromString(sharedPreferences1.getString(EdgeSettingDialogFragment.EDGE_POSITION_KEY, spinnerEntries[1]), getApplicationContext()) >= 30){
            edge1HeightPxl = (int) (edge1Sensivite * mScale);
            edge1WidthPxl = (int) (edge1Length * mScale);
        }else {
            edge1HeightPxl = (int) (edge1Length * mScale);
            edge1WidthPxl = (int) (edge1Sensivite * mScale);
        }
        edge1ImageLayoutParams.height = edge1HeightPxl;
        edge1ImageLayoutParams.width = edge1WidthPxl;
        edge1Image.setLayoutParams(edge1ImageLayoutParams);
        icon_distance_pxl = icon_distance * mScale;
        icon_24dp_in_pxls = 24 * mScale;
        distance_to_arc_pxl = (int) (distance_to_arc * mScale);
        item1View = (FrameLayout) layoutInflater.inflate(R.layout.item, null);
        iconImageList1 = new AppCompatImageView[6];
        iconImageList1[0] = (AppCompatImageView) item1View.findViewById(R.id.item_0);
        iconImageList1[1] = (AppCompatImageView) item1View.findViewById(R.id.item_1);
        iconImageList1[2] = (AppCompatImageView) item1View.findViewById(R.id.item_2);
        iconImageList1[3] = (AppCompatImageView) item1View.findViewById(R.id.item_3);
        iconImageList1[4] = (AppCompatImageView) item1View.findViewById(R.id.item_4);
        iconImageList1[5] = (AppCompatImageView) item1View.findViewById(R.id.item_5);
        iconImageArrayList1 = new ArrayList<AppCompatImageView>();
        iconImageArrayList1.add(iconImageList1[0]);
        iconImageArrayList1.add(iconImageList1[1]);
        iconImageArrayList1.add(iconImageList1[2]);
        iconImageArrayList1.add(iconImageList1[3]);
        iconImageArrayList1.add(iconImageList1[4]);
        iconImageArrayList1.add(iconImageList1[5]);
        WindowManager.LayoutParams paramsEdge1 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                PixelFormat.TRANSLUCENT);
        switch (edge1Position) {
            case 10:
                paramsEdge1.gravity = Gravity.TOP | Gravity.RIGHT;
                break;
            case 11:
                paramsEdge1.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                break;
            case 12:
                paramsEdge1.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                break;
            case 20:
                paramsEdge1.gravity = Gravity.TOP | Gravity.LEFT;
                break;
            case 21:
                paramsEdge1.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                break;
            case 22:
                paramsEdge1.gravity = Gravity.BOTTOM | Gravity.LEFT;
                break;
            case 31:
                paramsEdge1.gravity = Gravity.BOTTOM | Gravity.CENTER;
                break;
        }
        if (isEdge1On) {
            windowManager.addView(edge1View, paramsEdge1);
        }
        edge2View = (RelativeLayout) layoutInflater.inflate(R.layout.edge_view, null);
        edge2Image = (MyImageView) edge2View.findViewById(R.id.edge_image);
        ViewGroup.LayoutParams edge2ImageLayoutParams = edge2Image.getLayoutParams();
        if (Utility.getPositionIntFromString(sharedPreferences2.getString(EdgeSettingDialogFragment.EDGE_POSITION_KEY, spinnerEntries[5]), getApplicationContext()) >= 30){
            edge2HeightPxl = (int) (edge2Sensitive * mScale);
            edge2WidthPxl = (int) (edge2Length * mScale);
        }
        edge2HeightPxl = (int) (edge2Length * mScale);
        edge2WidthPxl = (int) (edge2Sensitive * mScale);
        edge2ImageLayoutParams.height = edge2HeightPxl;
        edge2ImageLayoutParams.width = edge2WidthPxl;
        edge2Image.setLayoutParams(edge2ImageLayoutParams);
        WindowManager.LayoutParams paramsEdge2 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                PixelFormat.TRANSLUCENT);
        switch (edge2Position) {
            case 10:
                paramsEdge2.gravity = Gravity.TOP | Gravity.RIGHT;
                break;
            case 11:
                paramsEdge2.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                break;
            case 12:
                paramsEdge2.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                break;
            case 20:
                paramsEdge2.gravity = Gravity.TOP | Gravity.LEFT;
                break;
            case 21:
                paramsEdge2.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                break;
            case 22:
                paramsEdge2.gravity = Gravity.BOTTOM | Gravity.LEFT;
                break;
            case 31:
                paramsEdge2.gravity = Gravity.BOTTOM | Gravity.CENTER;
                break;
        }
        if (isEdge2On) {
            windowManager.addView(edge2View, paramsEdge2);

        }

        item2View = (FrameLayout) layoutInflater.inflate(R.layout.item, null);
        iconImageList2 = new AppCompatImageView[6];
        iconImageList2[0] = (AppCompatImageView) item2View.findViewById(R.id.item_0);
        iconImageList2[1] = (AppCompatImageView) item2View.findViewById(R.id.item_1);
        iconImageList2[2] = (AppCompatImageView) item2View.findViewById(R.id.item_2);
        iconImageList2[3] = (AppCompatImageView) item2View.findViewById(R.id.item_3);
        iconImageList2[4] = (AppCompatImageView) item2View.findViewById(R.id.item_4);
        iconImageList2[5] = (AppCompatImageView) item2View.findViewById(R.id.item_5);
        iconImageArrayList2 = new ArrayList<AppCompatImageView>();
        iconImageArrayList2.add(iconImageList2[0]);
        iconImageArrayList2.add(iconImageList2[1]);
        iconImageArrayList2.add(iconImageList2[2]);
        iconImageArrayList2.add(iconImageList2[3]);
        iconImageArrayList2.add(iconImageList2[4]);
        iconImageArrayList2.add(iconImageList2[5]);
        OnTouchListener onTouchListener1 = new OnTouchListener(edge1Position, iconImageList1, item1View, iconImageArrayList1);
        edge1Image.setOnTouchListener(onTouchListener1);

        OnTouchListener onTouchListener2 = new OnTouchListener(edge2Position, iconImageList2, item2View, iconImageArrayList2);
        edge2Image.setOnTouchListener(onTouchListener2);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_ic_looks_white_48dp1)
                .setContentText(getString(R.string.notification_text)).setContentTitle(getString(R.string.notification_title));
        Notification notificationCompat = builder.build();
        startForeground(NOTIFICATION_ID,notificationCompat);

        return START_NOT_STICKY;
    }

    public class OnTouchListener implements View.OnTouchListener {
        private int x_init_cord, y_init_cord;
        private int position;
        private FrameLayout itemView;
        private AppCompatImageView[] iconImageList;
        private List<AppCompatImageView> iconImageArrayList;

        public OnTouchListener(int position, AppCompatImageView[] iconImageList, FrameLayout itemView, List<AppCompatImageView> iconImageArrayList) {
            this.position = position;
            this.iconImageList = iconImageList;
            this.itemView = itemView;
            this.iconImageArrayList = iconImageArrayList;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int x_cord = (int) event.getRawX();
            int y_cord = (int) event.getRawY();
            if (touched){
                if (!switched) {
                    long currentTime = System.currentTimeMillis();
                    long eslapeTime = currentTime - firstTouchTime;
                    if (eslapeTime > holdTime) {
                        switched = true;
                    }
                }
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
//                    try {
                        itemView.removeView(backView);
                        itemView.removeView(homeView);
                        itemView.removeView(expandView);
//                    }catch (NullPointerException e ){
//                        Log.e(LOG_TAG,"NullpointException " + e);
//                    }
                    if (isFreeVersion){
                        isOutOfTrial = System.currentTimeMillis() - defaultShared.getLong(EdgeSettingDialogFragment.BEGIN_DAY_KEY,System.currentTimeMillis())
                                > MainActivity.trialTime;
                    }else isOutOfTrial = false;

                    Set<String> excludeSet = sharedPreferences_exclude.getStringSet(EdgeSettingDialogFragment.EXCLUDE_KEY, new HashSet<String>());
                    switched = false;
                    itemSwitched = false;
                    if (position < 30){
                        x_init_cord = x_cord;
                    }else {
                        x_init_cord = x_cord - getXOffset(x_cord);
                    }
                    if (position >= 30){
                        y_init_cord = y_cord;
                    }else y_init_cord = y_cord - getYOffset(y_cord);
                    float xForHomeBackNotiView = x_init_cord - icon_distance_pxl - distance_to_arc_pxl - ovalOffSet - ovalRadiusPlusPxl;
                    float yForHomeBackNotiView = y_init_cord - icon_distance_pxl - distance_to_arc_pxl - ovalOffSet - ovalRadiusPlusPxl;
                    int radiusForHomeBackNotiView = (int) icon_distance_pxl + distance_to_arc_pxl + ovalRadiusPlusPxl;
                    expandView = new ExpandStatusBarView(getApplicationContext());
                    expandView.setX(xForHomeBackNotiView);
                    expandView.setY(yForHomeBackNotiView);
                    expandView.setRadius(radiusForHomeBackNotiView);
                    expandView.setOvalOffset(ovalOffSet);
                    expandView.setPosition(position);
                    expandView.setVisibility(View.INVISIBLE);
                    itemView.addView(expandView);

                    homeView = new ExpandStatusBarView(getApplicationContext());
                    homeView.setX(xForHomeBackNotiView);
                    homeView.setY(yForHomeBackNotiView);
                    homeView.setRadius(radiusForHomeBackNotiView);
                    homeView.setOvalOffset(ovalOffSet);
                    homeView.setPosition(position);
                    homeView.setText("home");
                    homeView.setVisibility(View.INVISIBLE);
                    itemView.addView(homeView);

                    backView = new ExpandStatusBarView(getApplicationContext());
                    backView.setX(xForHomeBackNotiView);
                    backView.setY(yForHomeBackNotiView);
                    backView.setRadius(radiusForHomeBackNotiView);
                    backView.setOvalOffset(ovalOffSet);
                    backView.setPosition(position);
                    backView.setText("back");
                    backView.setVisibility(View.INVISIBLE);
                    itemView.addView(backView);

                    WindowManager.LayoutParams itemViewParameter = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.TYPE_PHONE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                            PixelFormat.TRANSLUCENT);
                    itemViewParameter.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
//                    try {
//                        windowManager.addView(itemView, itemViewParameter);
//
//                    }catch (IllegalStateException e){
//                        Log.e(LOG_TAG," item_view has already been added to the window manager");
//                    }
                    Utility.setIconsPosition(iconImageList, x_init_cord, y_init_cord, icon_distance_pxl, icon_24dp_in_pxls, position);

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        int numOfTask;
                        if (launcherPackagename != null) {
                            numOfTask = 8;
                        } else numOfTask = 7;
                        List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(numOfTask);
                        ArrayList<String> tempPackageName = new ArrayList<String>();
                        for (int i = 0; i < list.size(); i++) {
                            ActivityManager.RunningTaskInfo taskInfo = list.get(i);
                            ComponentName componentName = taskInfo.baseActivity;
                            String packName = componentName.getPackageName();
                            if (i != 0 & !packName.equals(launcherPackagename) & !excludeSet.contains(packName) & !packName.contains("launcher")) {
                                tempPackageName.add(packName);
                            }
                        }
                        packagename = new String[tempPackageName.size()];
                        tempPackageName.toArray(packagename);
                        for (int i = 0; i < 6; i++) {
                            if (i >= packagename.length) {
                                iconImageArrayList.get(i).setImageDrawable(null);
                            } else {
                                try {
//                                    Drawable icon = getPackageManager().getApplicationIcon(packagename[i]);
//                                    ImageView iconi = iconImageArrayList.get(i);
                                    iconImageArrayList.get(i).setImageDrawable(getPackageManager().getApplicationIcon(packagename[i]));
                                } catch (PackageManager.NameNotFoundException e) {
                                    Log.e(LOG_TAG, "NameNotFound" + e);
                                }
                            }
                        }
                        if (packagename.length ==0) switched = true;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
                        long currentTimeMillis = System.currentTimeMillis();
                        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTimeMillis - 1000 * 1000, currentTimeMillis);
                        ArrayList<String> tempPackageName = new ArrayList<String>();
                        if (stats != null) {
                            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>(DATE_DECENDING_COMPARATOR);
                            for (UsageStats usageStats : stats) {
                                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                            }
                            Set<Long> setKey = mySortedMap.keySet();
                            Log.e(LOG_TAG,"mySortedMap size = " + mySortedMap.size());
                            for (Long key : setKey) {
                                UsageStats usageStats = mySortedMap.get(key);
                                if (usageStats == null){
                                    Log.e(LOG_TAG," usageStats is null");
                                }
                                if (usageStats != null){
                                    String packa = usageStats.getPackageName();
                                    try {
                                        if (getPackageManager().getApplicationInfo(packa, 0).dataDir.startsWith("/system/app/")) {
                                            //do nothing
                                        } else if (packa.contains("systemui") |
                                                packa.contains("googlequicksearchbox") |
                                                key == mySortedMap.firstKey() |
                                                excludeSet.contains(packa)|
                                                packa.contains("launcher") |
                                                packa.contains("android.provider")) {
                                            // do nothing
                                        } else tempPackageName.add(packa);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        Log.e(LOG_TAG, "name not found" + e);
                                    }
                                }

                            }
                            packagename = new String[tempPackageName.size()];
                            tempPackageName.toArray(packagename);
                        } else Log.e(LOG_TAG, "erros in mySortedMap");
                        for (int i = 0; i < 6; i++) {
                            if (i >= packagename.length) {
                                iconImageArrayList.get(i).setImageDrawable(null);
                            } else {
                                try {
//                                    Drawable icon = getPackageManager().getApplicationIcon(packagename[i]);
                                    iconImageArrayList.get(i).setImageDrawable(getPackageManager().getApplicationIcon(packagename[i]));
//                                    iconi.setImageDrawable(icon);
                                } catch (PackageManager.NameNotFoundException e) {
                                    Log.e(LOG_TAG, "NameNotFound" + e);
                                }
                            }
                        }
                        if (packagename.length ==0) switched = true;
                    }
                    numOfIcon = iconImageArrayList.size();
                    x = new int[numOfIcon];
                    y = new int[numOfIcon];
                    for (int i = 0; i < numOfIcon; i++) {
                        x[i] = (int) iconImageArrayList.get(i).getX();
                        y[i] = (int) iconImageArrayList.get(i).getY();
                    }
                    try {
                        windowManager.addView(itemView, itemViewParameter);

                    }catch (IllegalStateException e){
                        Log.e(LOG_TAG," item_view has already been added to the window manager");
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    try {
                        windowManager.removeView(itemView);
                    }catch (IllegalArgumentException e){
                        Log.e(LOG_TAG,"itemView not attacked to the windowManager");
                    }

                    itemView.removeView(expandView);
                    itemView.removeView(homeView);
                    itemView.removeView(backView);
                    int packageToSwitch = Utility.findIconToSwitch(x, y, x_cord, y_cord, numOfIcon, icon_rad, mScale);
                    if (packageToSwitch != -1) {
                        Intent extApp = null;
                        if (!switched) {
                            if (packageToSwitch < packagename.length){
                                extApp = getPackageManager().getLaunchIntentForPackage(packagename[packageToSwitch]);
                            }

                        } else {
                            if (packageToSwitch < favoritePackageName.length){
                                extApp = getPackageManager().getLaunchIntentForPackage(favoritePackageName[packageToSwitch]);
                            }else {
                                Toast.makeText(getApplicationContext(),getString(R.string.please_add_favorite_item),Toast.LENGTH_LONG).show();
                                showAddFavoriteDialog();
                            }
                        }

                        if (extApp != null) {
//                            extApp.addCategory(Intent.CATEGORY_LAUNCHER);
//                            extApp.setAction(Intent.ACTION_MAIN);
                            ComponentName componentName = extApp.getComponent();
                            Intent startApp = new Intent(Intent.ACTION_MAIN,null);
                            startApp.addCategory(Intent.CATEGORY_LAUNCHER);
                            startApp.setComponent(componentName);
                            startApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_NO_ANIMATION );
                            startActivity(startApp);
                            Log.e(LOG_TAG, "packageToSwitch = " + packageToSwitch);
                        }else Log.e(LOG_TAG, "extApp = null " );

                    }
                    packagename = null;
                    int homeBackNoti = Utility.isHomeOrBackOrNoti(x_init_cord, y_init_cord, x_cord, y_cord, icon_distance, mScale, position);
                    Log.e(LOG_TAG, "homeBackNoti = " + homeBackNoti);
                    if (!isFreeVersion){
                        if (homeBackNoti == 1) {
                            AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
                            event1.setClassName(getClass().getName());
                            event1.getText().add("home");
                            event1.setAction(1);
                            event1.setPackageName(getPackageName());
                            event1.setEnabled(true);
                            AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
                            AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
                            recordCompat.setSource(v);
                            if (Utility.isAccessibilityEnable(getApplicationContext())) {
                                manager.sendAccessibilityEvent(event1);
                            }else Toast.makeText(getApplicationContext(),R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
                        } else if (homeBackNoti == 2) {
                            AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
                            event1.setClassName(getClass().getName());
                            event1.getText().add("back");
                            event1.setAction(2);
                            event1.setPackageName(getPackageName());
                            event1.setEnabled(true);
                            AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
                            AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
                            recordCompat.setSource(v);
                            if (Utility.isAccessibilityEnable(getApplicationContext())) {
                                manager.sendAccessibilityEvent(event1);
                            }else Toast.makeText(getApplicationContext(),R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
                        } else if (homeBackNoti == 3) {
                            expandStatusBar();
                        }
                    } else if(!isOutOfTrial & homeBackNoti >0){
                        if (homeBackNoti == 1) {
                            AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
                            event1.setClassName(getClass().getName());
                            event1.getText().add("home");
                            event1.setAction(1);
                            event1.setPackageName(getPackageName());
                            event1.setEnabled(true);
                            AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
                            AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
                            recordCompat.setSource(v);
                            if (Utility.isAccessibilityEnable(getApplicationContext())) {
                                manager.sendAccessibilityEvent(event1);
                            }else Toast.makeText(getApplicationContext(),R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
                        } else if (homeBackNoti == 2) {
                            AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
                            event1.setClassName(getClass().getName());
                            event1.getText().add("back");
                            event1.setAction(2);
                            event1.setPackageName(getPackageName());
                            event1.setEnabled(true);
                            AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
                            AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
                            recordCompat.setSource(v);
                            if (Utility.isAccessibilityEnable(getApplicationContext())) {
                                manager.sendAccessibilityEvent(event1);
                            }else Toast.makeText(getApplicationContext(),R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
                        } else if (homeBackNoti == 3) {
                            expandStatusBar();
                        }
                    }else if (isOutOfTrial & homeBackNoti >0){
                        if (homeBackNoti == 1) {
                            AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
                            event1.setClassName(getClass().getName());
                            event1.getText().add("home");
                            event1.setAction(1);
                            event1.setPackageName(getPackageName());
                            event1.setEnabled(true);
                            AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
                            AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
                            recordCompat.setSource(v);
                            if (Utility.isAccessibilityEnable(getApplicationContext())) {
                                manager.sendAccessibilityEvent(event1);
                            }else Toast.makeText(getApplicationContext(),R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
                        } else if (homeBackNoti == 2) {
                            AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
                            event1.setClassName(getClass().getName());
                            event1.getText().add("back");
                            event1.setAction(2);
                            event1.setPackageName(getPackageName());
                            event1.setEnabled(true);
                            AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
                            AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
                            recordCompat.setSource(v);
                            if (Utility.isAccessibilityEnable(getApplicationContext())) {
                                manager.sendAccessibilityEvent(event1);
                            }else Toast.makeText(getApplicationContext(),R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
                        } else if (homeBackNoti == 3) {
                            Toast.makeText(getApplicationContext(),getString(R.string.edge_service_out_of_trial_text_when_homebacknoti),Toast.LENGTH_LONG).show();
                        }
                    }
                    touched = false;

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (switched & !itemSwitched) {
                        for (int i = 0; i < 6; i++) {
                            if (i >= favoritePackageName.length) {
                                iconImageArrayList.get(i).setImageDrawable(ContextCompat. getDrawable(getApplicationContext() , R.drawable.ic_add_circle_outline_white_48dp));
                            } else {
                                try {
//                                    Drawable icon = getPackageManager().getApplicationIcon(favoritePackageName[i]);
//                                    ImageView iconi = iconImageArrayList.get(i);
                                    iconImageArrayList.get(i).setImageDrawable(getPackageManager().getApplicationIcon(favoritePackageName[i]));
                                } catch (PackageManager.NameNotFoundException e) {
                                    Log.e(LOG_TAG, "NameNotFound" + e);
                                }
                            }
                        }
                        itemSwitched = false;
                    }
                    int iconToSwitch = Utility.findIconToSwitch(x, y, x_cord, y_cord, numOfIcon, icon_rad, mScale);
                    int moveToHomeBackNoti = Utility.isHomeOrBackOrNoti(x_init_cord, y_init_cord, x_cord, y_cord, icon_distance, mScale, position);
                    if (iconToSwitch != -1) {
                        if (!touched) {
                            firstTouchTime = System.currentTimeMillis();
                            touched = true;
                        }
                    } else {
                        touched = false;
                    }
                    if (iconToSwitch != -1 | moveToHomeBackNoti > 0) {
                        if (!hasVibrate) {
                            if (!defaultShared.getBoolean(EdgeSettingDialogFragment.DISABLE_HAPTIC_FEEDBACK_KEY,false)){
                                vibrator.vibrate(15);
                            }

                            hasVibrate = true;
                        }
                        for (int i = 0; i < iconImageArrayList.size(); i++) {
                            hasOneActive = true;
                            if (i == iconToSwitch) {
                                iconImageArrayList.get(i).setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.icon_tint));
                            } else iconImageArrayList.get(i).setColorFilter(null);
                        }
                    }
                    if (iconToSwitch == -1 & moveToHomeBackNoti == 0) {
                        hasVibrate = false;
                        if (hasOneActive) {
                            for (ImageView imageView : iconImageArrayList) {
                                if (imageView.getColorFilter() != null) {
                                    imageView.setColorFilter(null);
                                }
                            }
                        }
                        hasOneActive = false;
                    }
                    switch (moveToHomeBackNoti) {
                        case 0:
                            homeView.setVisibility(View.INVISIBLE);
                            backView.setVisibility(View.INVISIBLE);
                            expandView.setVisibility(View.INVISIBLE);
                            break;
                        case 1:
                            homeView.setVisibility(View.VISIBLE);
                            backView.setVisibility(View.INVISIBLE);
                            expandView.setVisibility(View.INVISIBLE);
                            break;
                        case 2:
                            homeView.setVisibility(View.INVISIBLE);
                            backView.setVisibility(View.VISIBLE);
                            expandView.setVisibility(View.INVISIBLE);
                            break;
                        case 3:
                            homeView.setVisibility(View.INVISIBLE);
                            backView.setVisibility(View.INVISIBLE);
                            expandView.setVisibility(View.VISIBLE);
                            break;
                    }
                    break;

                case MotionEvent.ACTION_OUTSIDE:
                    if (item1View.isAttachedToWindow()) {
                        windowManager.removeView(item1View);
                    }
                    if (item2View.isAttachedToWindow()) {
                        windowManager.removeView(item2View);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (item1View.isAttachedToWindow()) {
                        windowManager.removeView(item1View);
                    }
                    if (item2View.isAttachedToWindow()) {
                        windowManager.removeView(item2View);
                    }
                    break;
            }
            return true;
        }
    }
    public void expandStatusBar() {
        Object sbservice = getSystemService("statusbar");
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
            Log.e(LOG_TAG, "NosuchMethod " + e);
            try {
                Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                Method showsb =statusbarManager.getMethod("expandSettingsPanel");
                showsb.invoke(sbservice);

            }catch (ClassNotFoundException e1) {
                Log.e(LOG_TAG, "ClassNotFound 2 " + e1);
            }catch (NoSuchMethodException e1){
                Log.e(LOG_TAG, "NosuchMethod 2 " + e1);
            }catch (InvocationTargetException e1) {
            Log.e(LOG_TAG, "InvocationTargetException 2" + e1);
            }catch (IllegalAccessException e1) {
                Log.e(LOG_TAG, "IllegalAccessException 2 " + e1);
            }
        } catch (IllegalAccessException e) {
            Log.e(LOG_TAG, "IllegalAccessException " + e);
        } catch (InvocationTargetException e) {
            Log.e(LOG_TAG, "InvocationTargetException " + e);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (item1View.isAttachedToWindow()) {
            item1View.removeView(backView);
            item1View.removeView(homeView);
            item1View.removeView(expandView);
            windowManager.removeView(item1View);
        }
        if (item2View.isAttachedToWindow()) {
            item2View.removeView(backView);
            item2View.removeView(homeView);
            item2View.removeView(expandView);
            windowManager.removeView(item2View);
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mScale = getResources().getDisplayMetrics().density;
        defaultShared = getApplicationContext().getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        sharedPreferences_favorite = getApplicationContext().getSharedPreferences(MainActivity.FAVORITE_SHAREDPREFERENCE, 0);
        sharedPreferences_exclude = getApplicationContext().getSharedPreferences(MainActivity.EXCLUDE_SHAREDPREFERENCE, 0);
        sharedPreferences1 = getSharedPreferences(MainActivity.EDGE_1_SHAREDPREFERENCE, 0);
        sharedPreferences2 = getSharedPreferences(MainActivity.EDGE_2_SHAREDPREFERENCE, 0);
        edge1Length = sharedPreferences1.getInt(EdgeSettingDialogFragment.EDGE_LENGTH_KEY, 150);
        edge2Length = sharedPreferences2.getInt(EdgeSettingDialogFragment.EDGE_LENGTH_KEY, 150);
        edge1Sensivite = sharedPreferences1.getInt(EdgeSettingDialogFragment.EDGE_SENSIIVE_KEY, 12);
        edge2Sensitive = sharedPreferences2.getInt(EdgeSettingDialogFragment.EDGE_SENSIIVE_KEY, 12);
        isEdge1On = sharedPreferences1.getBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, true);
        isEdge2On = sharedPreferences2.getBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, false);
        spinnerEntries = getResources().getStringArray(R.array.edge_dialog_spinner_array);
        edge1Position = Utility.getPositionIntFromString(sharedPreferences1.getString(EdgeSettingDialogFragment.EDGE_POSITION_KEY, spinnerEntries[1]), getApplicationContext()); // default =1
        edge2Position = Utility.getPositionIntFromString(sharedPreferences2.getString(EdgeSettingDialogFragment.EDGE_POSITION_KEY, spinnerEntries[5]), getApplicationContext());
        icon_distance = defaultShared.getInt(EdgeSettingDialogFragment.ICON_DISTANCE_KEY,110);
        ovalOffSet = (int) (25 * mScale);
        ovalRadiusPlusPxl = (int) (ovalRadiusPlus * mScale);
        Set<String> set = sharedPreferences_favorite.getStringSet(EdgeSettingDialogFragment.FAVORITE_KEY, new HashSet<String>());
        favoritePackageName = new String[set.size()];
        set.toArray(favoritePackageName);
        Log.e(LOG_TAG, "onCreate service"+ "\nEdge1 on = " + isEdge1On + "\nEdge2 on = " + isEdge2On +
        "\nEdge1 position = " + edge1Position + "\nEdge2 positon = "+ edge2Position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (edge1View.isAttachedToWindow()) {
            windowManager.removeView(edge1View);
        }
        if (edge2View.isAttachedToWindow()) {
            windowManager.removeView(edge2View);
        }
        Log.e(LOG_TAG, "onDestroy service");
    }

    private int getYOffset(int y_init) {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        int distanceNeeded = (int) (mScale * (icon_distance + icon_rad));
        int distanceWeHave = point.y - y_init;
        if (distanceWeHave < distanceNeeded) {
            return distanceNeeded - distanceWeHave;
        } else if (y_init < distanceNeeded) {
            return y_init - distanceNeeded;
        } else return 0;
    }

    private int getXOffset(int x_init){
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        int distanceNeeded = (int) (mScale * (icon_distance + icon_rad));
        int distanceWeHave = point.x - x_init;
        if (distanceWeHave < distanceNeeded){
            return distanceNeeded - distanceWeHave;
        }else if (x_init < distanceNeeded){
            return x_init - distanceNeeded;
        }else return 0;
    }

    public void showAddFavoriteDialog(){
        startActivity(new Intent(getApplicationContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static class BootCompleteReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            context.startService(new Intent(context,EdgeGestureService.class));
        }
    }


}
