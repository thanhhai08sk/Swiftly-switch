package org.de_studio.recentappswitcher.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
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
    float mScale;
    private int configuration = 0;
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
    public int edge_height = 150;
    public int edge_1_height, edge_2_height;
    public int edge1HeightPxl, edge2HeightPxl;
    public int edge1WidthPxl, edge2WidthPxl;
    public int edge1Width, edge2Width;
    public int edge_y;
    public int edge_centre_y;
    private List<AppCompatImageView> list_icon_1, list_icon_2;
    private String[] packagename, favoritePackageName;
    private String launcherPackagename;
    private int[] x, y;
    private int numOfIcon;
    private boolean hasOneActive = false;
    private boolean hasVibrate = false, hasHomwBackNotiVisible = false;
    private boolean isEdge1On, isEdge2On;
    public int numOfEdge, edge1Position, edge2Position;
    private SharedPreferences defaultShared, sharedPreferences1, sharedPreferences2, sharedPreferences_favorite, sharedPreferences_exclude;
    private AppCompatImageView[] icons1Image, icons2Image;
    private ExpandStatusBarView expandView, homeView, backView;
    private Vibrator vibrator;
    private int ovalOffSet, ovalRadiusPlus = 15, ovalRadiusPlusPxl;
    private long holdTime = 1000, firstTouchTime;
    private boolean touched = false, switched = false, itemSwitched = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Set<String> set = sharedPreferences_favorite.getStringSet(EdgeSettingDialogFragment.FAVORITE_KEY, new HashSet<String>());
        favoritePackageName = new String[set.size()];
        set.toArray(favoritePackageName);
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo res = getPackageManager().resolveActivity(launcherIntent, 0);
        if (res.activityInfo != null) {
            launcherPackagename = res.activityInfo.packageName;
        } else launcherPackagename = "";
        numOfEdge = defaultShared.getInt("numOfEdge", 1);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        edge1View = (RelativeLayout) layoutInflater.inflate(R.layout.edge_view, null);
        edge1Image = (MyImageView) edge1View.findViewById(R.id.edge_image);
        ViewGroup.LayoutParams lp = edge1Image.getLayoutParams();
        edge1HeightPxl = (int) (edge_1_height * mScale);
        edge1WidthPxl = (int) (edge1Width * mScale);
        lp.height = edge1HeightPxl;
        lp.width = edge1WidthPxl;
        edge1Image.setLayoutParams(lp);
        edge_y = (int) edge1Image.getY();
        edge_centre_y = edge_y + edge1HeightPxl / 2;
        icon_distance_pxl = icon_distance * mScale;
        icon_24dp_in_pxls = 24 * mScale;
        distance_to_arc_pxl = (int) (distance_to_arc * mScale);
        item1View = (FrameLayout) layoutInflater.inflate(R.layout.item, null);
        icons1Image = new AppCompatImageView[6];
        icons1Image[0] = (AppCompatImageView) item1View.findViewById(R.id.item_0);
        icons1Image[1] = (AppCompatImageView) item1View.findViewById(R.id.item_1);
        icons1Image[2] = (AppCompatImageView) item1View.findViewById(R.id.item_2);
        icons1Image[3] = (AppCompatImageView) item1View.findViewById(R.id.item_3);
        icons1Image[4] = (AppCompatImageView) item1View.findViewById(R.id.item_4);
        icons1Image[5] = (AppCompatImageView) item1View.findViewById(R.id.item_5);
        list_icon_1 = new ArrayList<AppCompatImageView>();
        list_icon_1.add(icons1Image[0]);
        list_icon_1.add(icons1Image[1]);
        list_icon_1.add(icons1Image[2]);
        list_icon_1.add(icons1Image[3]);
        list_icon_1.add(icons1Image[4]);
        list_icon_1.add(icons1Image[5]);
        WindowManager.LayoutParams paramsEdge1 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                PixelFormat.TRANSLUCENT);

        // | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
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
        }
        if (isEdge1On) {
            windowManager.addView(edge1View, paramsEdge1);
        }
        edge2View = (RelativeLayout) layoutInflater.inflate(R.layout.edge_view, null);
        edge2Image = (MyImageView) edge2View.findViewById(R.id.edge_image);
        ViewGroup.LayoutParams lp2 = edge2Image.getLayoutParams();
        edge2HeightPxl = (int) (edge_2_height * mScale);
        edge2WidthPxl = (int) (edge2Width * mScale);
        lp2.height = edge2HeightPxl;
        lp2.width = edge2WidthPxl;
        edge2Image.setLayoutParams(lp2);
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
        }
        if (isEdge2On) {
            windowManager.addView(edge2View, paramsEdge2);

        }

        item2View = (FrameLayout) layoutInflater.inflate(R.layout.item, null);
        icons2Image = new AppCompatImageView[6];
        icons2Image[0] = (AppCompatImageView) item2View.findViewById(R.id.item_0);
        icons2Image[1] = (AppCompatImageView) item2View.findViewById(R.id.item_1);
        icons2Image[2] = (AppCompatImageView) item2View.findViewById(R.id.item_2);
        icons2Image[3] = (AppCompatImageView) item2View.findViewById(R.id.item_3);
        icons2Image[4] = (AppCompatImageView) item2View.findViewById(R.id.item_4);
        icons2Image[5] = (AppCompatImageView) item2View.findViewById(R.id.item_5);
        list_icon_2 = new ArrayList<AppCompatImageView>();
        list_icon_2.add(icons2Image[0]);
        list_icon_2.add(icons2Image[1]);
        list_icon_2.add(icons2Image[2]);
        list_icon_2.add(icons2Image[3]);
        list_icon_2.add(icons2Image[4]);
        list_icon_2.add(icons2Image[5]);
        OnTouchListener onTouchListener1 = new OnTouchListener(edge1Position, icons1Image, item1View, list_icon_1);
        edge1Image.setOnTouchListener(onTouchListener1);

        OnTouchListener onTouchListener2 = new OnTouchListener(edge2Position, icons2Image, item2View, list_icon_2);
        edge2Image.setOnTouchListener(onTouchListener2);

        return START_NOT_STICKY;
    }

    public class OnTouchListener implements View.OnTouchListener {
        private int x_init_cord, y_init_cord;
        private int position;
        private FrameLayout itemView;
        private AppCompatImageView[] iconImage;
        private List<AppCompatImageView> listIcon;

        public OnTouchListener(int position, AppCompatImageView[] iconImage, FrameLayout itemView, List<AppCompatImageView> listIcon) {
            this.position = position;
            this.iconImage = iconImage;
            this.itemView = itemView;
            this.listIcon = listIcon;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int x_cord = (int) event.getRawX();
            int y_cord = (int) event.getRawY();
            int x1, y1, top, left, bottom, right;
            int[] expandSpec;// left, top, right, bottom
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Set<String> excludeSet = sharedPreferences_exclude.getStringSet(EdgeSettingDialogFragment.EXCLUDE_KEY, new HashSet<String>());
                    switched = false;
                    itemSwitched = false;
                    x_init_cord = x_cord;
                    y_init_cord = y_cord - getYOffset(y_cord);
                    expandSpec = Utility.getExpandSpec(x_init_cord, y_init_cord, icon_distance, 35, windowManager);
                    int expandWidth = expandSpec[2] - expandSpec[0];
                    int expandHeight = expandSpec[3] - expandSpec[1];
                    expandView = new ExpandStatusBarView(getApplicationContext());
                    expandView.setX(x_init_cord - icon_distance_pxl - distance_to_arc_pxl - ovalOffSet - ovalRadiusPlusPxl);
                    expandView.setY(y_init_cord - icon_distance_pxl - distance_to_arc_pxl - ovalOffSet - ovalRadiusPlusPxl);
                    expandView.setRadius((int) icon_distance_pxl + distance_to_arc_pxl + ovalRadiusPlusPxl);
                    expandView.setOvalOffset(ovalOffSet);
                    expandView.setPosition(position);
                    expandView.setVisibility(View.INVISIBLE);
                    itemView.addView(expandView);

                    homeView = new ExpandStatusBarView(getApplicationContext());
                    homeView.setX(x_init_cord - icon_distance_pxl - distance_to_arc_pxl - ovalOffSet - ovalRadiusPlusPxl);
                    homeView.setY(y_init_cord - icon_distance_pxl - distance_to_arc_pxl - ovalOffSet - ovalRadiusPlusPxl);
                    homeView.setRadius((int) icon_distance_pxl + distance_to_arc_pxl + ovalRadiusPlusPxl);
                    homeView.setOvalOffset(ovalOffSet);
                    homeView.setPosition(position);
                    homeView.setText("home");
                    homeView.setVisibility(View.INVISIBLE);
                    itemView.addView(homeView);

                    backView = new ExpandStatusBarView(getApplicationContext());
                    backView.setX(x_init_cord - icon_distance_pxl - distance_to_arc_pxl - ovalOffSet - ovalRadiusPlusPxl);
                    backView.setY(y_init_cord - icon_distance_pxl - distance_to_arc_pxl - ovalOffSet - ovalRadiusPlusPxl);
                    backView.setRadius((int) icon_distance_pxl + distance_to_arc_pxl + ovalRadiusPlusPxl);
                    backView.setOvalOffset(ovalOffSet);
                    backView.setPosition(position);
                    backView.setText("back");
                    backView.setVisibility(View.INVISIBLE);
                    itemView.addView(backView);

                    WindowManager.LayoutParams paraItem = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.TYPE_PHONE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                            PixelFormat.TRANSLUCENT);
                    paraItem.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                    windowManager.addView(itemView, paraItem);
                    Utility.setIconsPosition(iconImage, x_init_cord, y_init_cord, icon_distance_pxl, icon_24dp_in_pxls, position);



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
                            if (i != 0 & !packName.equals(launcherPackagename) & !excludeSet.contains(packName)) {
                                tempPackageName.add(packName);
                            }
                        }
                        packagename = new String[tempPackageName.size()];
                        tempPackageName.toArray(packagename);
                        for (int i = 0; i < 6; i++) {
                            if (i >= packagename.length) {
                                listIcon.get(i).setImageDrawable(null);
                            } else {
                                try {
                                    Drawable icon = getPackageManager().getApplicationIcon(packagename[i]);
                                    ImageView iconi = listIcon.get(i);
                                    iconi.setImageDrawable(icon);
                                } catch (PackageManager.NameNotFoundException e) {
                                    Log.e(LOG_TAG, "NameNotFound" + e);
                                }
                            }
                        }
                    }



                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
                        long time = System.currentTimeMillis();
                        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
                        ArrayList<String> tempPackageName = new ArrayList<String>();
                        if (stats != null) {
                            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>(DATE_DECENDING_COMPARATOR);
                            for (UsageStats usageStats : stats) {
                                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                            }
                            Set<Long> setKey = mySortedMap.keySet();
                            for (Long key : setKey) {
                                UsageStats usageStats = mySortedMap.get(key);
                                String packa = usageStats.getPackageName();
                                try {
                                    if (getPackageManager().getApplicationInfo(packa, 0).dataDir.startsWith("/system/app/")) {
                                        //do nothing
                                    } else if (packa.contains("systemui") |
                                            packa.contains("googlequicksearchbox") |
                                            key == mySortedMap.firstKey() |
                                            excludeSet.contains(packa)) {
                                        // do nothing
                                    } else tempPackageName.add(packa);
                                } catch (PackageManager.NameNotFoundException e) {
                                    Log.e(LOG_TAG, "name not found" + e);
                                }
                            }
                            packagename = new String[tempPackageName.size()];
                            tempPackageName.toArray(packagename);
                        } else Log.e(LOG_TAG, "erros in mySortedMap");
                        for (int i = 0; i < 6; i++) {
                            if (i >= packagename.length) {
                                listIcon.get(i).setImageDrawable(null);
                            } else {
                                try {
                                    Drawable icon = getPackageManager().getApplicationIcon(packagename[i]);
                                    ImageView iconi = listIcon.get(i);
                                    iconi.setImageDrawable(icon);
                                } catch (PackageManager.NameNotFoundException e) {
                                    Log.e(LOG_TAG, "NameNotFound" + e);
                                }
                            }
                        }
                    }
                    numOfIcon = listIcon.size();
                    x = new int[numOfIcon];
                    y = new int[numOfIcon];
                    for (int i = 0; i < numOfIcon; i++) {
                        x[i] = (int) listIcon.get(i).getX();
                        y[i] = (int) listIcon.get(i).getY();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    windowManager.removeView(itemView);
                    itemView.removeView(expandView);
                    itemView.removeView(homeView);
                    itemView.removeView(backView);
                    int packageToSwitch = Utility.findIconToSwitch(x, y, x_cord, y_cord, numOfIcon, icon_rad, windowManager);
                    if (packageToSwitch != -1) {
                        int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
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
                            extApp.addCategory(Intent.CATEGORY_LAUNCHER);
                            extApp.setAction(Intent.ACTION_MAIN);
                            ComponentName componentName = extApp.getComponent();
                            Intent startApp = new Intent();
                            startApp.setComponent(componentName);
                            startApp.addCategory(Intent.CATEGORY_LAUNCHER);
                            startApp.addFlags(launchFlags);
                            startApp.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(startApp);
                            Log.e(LOG_TAG, "packageToSwitch = " + packageToSwitch);
                        }

                    }
                    packagename = null;
                    int homeBackNoti = Utility.isHomeOrBackOrNoti(x_init_cord, y_init_cord, x_cord, y_cord, icon_distance, windowManager);
                    Log.e(LOG_TAG, "homeBackNoti = " + homeBackNoti);
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
                        if (manager.isEnabled()) {
                            manager.sendAccessibilityEvent(event1);
                        }
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
                        if (manager.isEnabled()) {
                            manager.sendAccessibilityEvent(event1);
                        }
                    } else if (homeBackNoti == 3) {
                        expandStatusBar();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (switched & !itemSwitched) {
                        for (int i = 0; i < 6; i++) {
                            if (i >= favoritePackageName.length) {
                                listIcon.get(i).setImageDrawable(ContextCompat. getDrawable(getApplicationContext() , R.drawable.ic_add_circle_outline_white_48dp));
                            } else {
                                try {
                                    Drawable icon = getPackageManager().getApplicationIcon(favoritePackageName[i]);
                                    ImageView iconi = listIcon.get(i);
                                    iconi.setImageDrawable(icon);
                                } catch (PackageManager.NameNotFoundException e) {
                                    Log.e(LOG_TAG, "NameNotFound" + e);
                                }
                            }
                        }
                        itemSwitched = false;
                    }
                    int iconToSwitch = Utility.findIconToSwitch(x, y, x_cord, y_cord, numOfIcon, icon_rad, windowManager);
                    int moveToHomeBackNoti = Utility.isHomeOrBackOrNoti(x_init_cord, y_init_cord, x_cord, y_cord, icon_distance, windowManager);
                    if (iconToSwitch != -1) {
                        if (!touched) {
                            firstTouchTime = System.currentTimeMillis();
                            touched = true;
                        } else {
                            if (!switched) {
                                long currentTime = System.currentTimeMillis();
                                long eslapeTime = currentTime - firstTouchTime;
                                if (eslapeTime > holdTime) {
                                    switched = true;
//                                    Toast.makeText(getApplicationContext(), "switch to favorite apps", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    } else {
                        touched = false;
                    }


                    if (iconToSwitch != -1 | moveToHomeBackNoti > 0) {
                        if (!hasVibrate) {
                            vibrator.vibrate(15);
                            hasVibrate = true;
                        }
                        for (int i = 0; i < listIcon.size(); i++) {
                            hasOneActive = true;
                            if (i == iconToSwitch) {
//                                listIcon.get(i).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_background));
                                listIcon.get(i).setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.icon_tint));

                            } else listIcon.get(i).setColorFilter(null);
                            ;

                        }

                    }
                    if (iconToSwitch == -1 & moveToHomeBackNoti == 0) {
                        hasVibrate = false;
                        if (hasOneActive) {
                            for (ImageView imageView : listIcon) {
//                                imageView.setBackground(null);
                                if (imageView.getColorFilter() != null) {
                                    imageView.setColorFilter(null);
                                }

                            }
                        }
                        hasOneActive = false;
                    }
//                    Log.e(LOG_TAG, "movetoHomeBackNoti = " + moveToHomeBackNoti);
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
        } catch (IllegalAccessException e) {
            Log.e(LOG_TAG, "IllegalAccessException " + e);
        } catch (InvocationTargetException e) {
            Log.e(LOG_TAG, "InvocationTargetException " + e);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation != configuration) {
        if (item1View.isAttachedToWindow()) {
            windowManager.removeView(item1View);
        }
        if (item2View.isAttachedToWindow()) {
            windowManager.removeView(item2View);
        }
//            try {
//                windowManager.removeView(item1View);
//                windowManager.removeView(item2View);
//            } catch (NullPointerException e) {
//                Log.e("onConfiguration", "Null");
//            } catch (IllegalArgumentException e) {
//                Log.e("onConfiguration", "Illegal");
//            }
//            configuration = newConfig.orientation;
//        }
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
        edge_1_height = sharedPreferences1.getInt(EdgeSettingDialogFragment.EDGE_LENGTH_KEY, 150);
        edge_2_height = sharedPreferences2.getInt(EdgeSettingDialogFragment.EDGE_LENGTH_KEY, 150);
        edge1Width = sharedPreferences1.getInt(EdgeSettingDialogFragment.EDGE_SENSIIVE_KEY, 12);
        edge2Width = sharedPreferences2.getInt(EdgeSettingDialogFragment.EDGE_SENSIIVE_KEY, 12);
        isEdge1On = sharedPreferences1.getBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, true);
        isEdge2On = sharedPreferences2.getBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, false);
        edge1Position = Utility.getPositionIntFromString(sharedPreferences1.getString(EdgeSettingDialogFragment.EDGE_POSITION_KEY, "Right centre"), getApplicationContext());
        edge2Position = Utility.getPositionIntFromString(sharedPreferences2.getString(EdgeSettingDialogFragment.EDGE_POSITION_KEY, "Left centre"), getApplicationContext());
        ovalOffSet = (int) (25 * mScale);
        ovalRadiusPlusPxl = (int) (ovalRadiusPlus * mScale);

        Set<String> set = sharedPreferences_favorite.getStringSet(EdgeSettingDialogFragment.FAVORITE_KEY, new HashSet<String>());
        favoritePackageName = new String[set.size()];
        set.toArray(favoritePackageName);
        Log.e(LOG_TAG, "onCreate service");
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

    public void showAddFavoriteDialog(){
        startActivity(new Intent(getApplicationContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

}
