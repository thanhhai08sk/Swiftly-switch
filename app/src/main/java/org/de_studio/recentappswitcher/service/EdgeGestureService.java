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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
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

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
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
    public int icon_distance = 115, distance_to_arc = 35, distance_to_arc_pxl;
    public float icon_distance_pxl, icon_24dp_in_pxls;
    public int edge_height = 150;
    public int edge_height_pxl;
    public int edge_width_pxl;
    public int edge_width = 18;
    public int edge_y;
    public int edge_centre_y;
    private List<ImageView> list_icon_1, list_icon_2;
    private String[] packagename;
    private String launcherPackagename;
    private int[] x, y;
    private int numOfIcon;
    private boolean hasOneActive = false;
    private boolean hasVibrate = false,hasHomwBackNotiVisible=false;
    public int numOfEdge, edge1Position, edge2Position;
    private SharedPreferences defaultShared;
    private ImageView[] icons1Image, icons2Image;
    private ExpandStatusBarView expandView, homeView, backView;
    private Vibrator vibrator;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        defaultShared = getApplicationContext().getSharedPreferences(getPackageName() + "_preferences", 0);
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo res = getPackageManager().resolveActivity(launcherIntent, 0);
        if (res.activityInfo != null) {
            launcherPackagename = res.activityInfo.packageName;
        }
        numOfEdge = defaultShared.getInt("numOfEdge", 1);
        edge1Position = defaultShared.getInt("edge1Position", 11);
        edge2Position = defaultShared.getInt("edge2Position", 11);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        edge1View = (RelativeLayout) layoutInflater.inflate(R.layout.edge_view, null);
        edge1Image = (MyImageView) edge1View.findViewById(R.id.edge_image);
        ViewGroup.LayoutParams lp = edge1Image.getLayoutParams();
        edge_height_pxl =(int) (edge_height *mScale);
        edge_width_pxl = (int)(edge_width * mScale);
        lp.height = edge_height_pxl;
        lp.width = edge_width_pxl;
        edge_y = (int) edge1Image.getY();
        edge_centre_y = edge_y + edge_height_pxl / 2;
        icon_distance_pxl = icon_distance * mScale;
        icon_24dp_in_pxls = 24 * mScale;
        distance_to_arc_pxl = (int)(distance_to_arc * mScale);
        edge1Image.setLayoutParams(lp);
        item1View = (FrameLayout) layoutInflater.inflate(R.layout.item, null);
        icons1Image = new ImageView[6];
        icons1Image[0] = (ImageView) item1View.findViewById(R.id.item_0);
        icons1Image[1] = (ImageView) item1View.findViewById(R.id.item_1);
        icons1Image[2] = (ImageView) item1View.findViewById(R.id.item_2);
        icons1Image[3] = (ImageView) item1View.findViewById(R.id.item_3);
        icons1Image[4] = (ImageView) item1View.findViewById(R.id.item_4);
        icons1Image[5] = (ImageView) item1View.findViewById(R.id.item_5);
        list_icon_1 = new ArrayList<ImageView>();
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

        windowManager.addView(edge1View, paramsEdge1);
        if (numOfEdge == 2) {
            edge2View = (RelativeLayout) layoutInflater.inflate(R.layout.edge_view, null);
            edge2Image = (MyImageView) edge2View.findViewById(R.id.edge_image);
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
            windowManager.addView(edge2View, paramsEdge2);

            item2View = (FrameLayout) layoutInflater.inflate(R.layout.item, null);
            icons2Image = new ImageView[6];
            icons2Image[0] = (ImageView) item2View.findViewById(R.id.item_0);
            icons2Image[1] = (ImageView) item2View.findViewById(R.id.item_1);
            icons2Image[2] = (ImageView) item2View.findViewById(R.id.item_2);
            icons2Image[3] = (ImageView) item2View.findViewById(R.id.item_3);
            icons2Image[4] = (ImageView) item2View.findViewById(R.id.item_4);
            icons2Image[5] = (ImageView) item2View.findViewById(R.id.item_5);
            list_icon_2 = new ArrayList<ImageView>();
            list_icon_2.add(icons2Image[0]);
            list_icon_2.add(icons2Image[1]);
            list_icon_2.add(icons2Image[2]);
            list_icon_2.add(icons2Image[3]);
            list_icon_2.add(icons2Image[4]);
            list_icon_2.add(icons2Image[5]);
        }
        OnTouchListener onTouchListener1 = new OnTouchListener(edge1Position, icons1Image, item1View, list_icon_1);
        edge1Image.setOnTouchListener(onTouchListener1);
        if (numOfEdge == 2) {
            OnTouchListener onTouchListener2 = new OnTouchListener(edge2Position, icons2Image, item2View, list_icon_2);
            edge2Image.setOnTouchListener(onTouchListener2);
        }
        return START_STICKY;
    }

    public class OnTouchListener implements View.OnTouchListener {
        private int x_init_cord, y_init_cord;
        private int position;
        private FrameLayout itemView;
        private ImageView[] iconImage;
        private List<ImageView> listIcon;

        public OnTouchListener(int position, ImageView[] iconImage, FrameLayout itemView, List<ImageView> listIcon) {
            this.position = position;
            this.iconImage = iconImage;
            this.itemView = itemView;
            this.listIcon = listIcon;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int x_cord = (int) event.getRawX();
            int y_cord = (int) event.getRawY();
            int x1,y1,top,left,bottom,right;
            int[] expandSpec;// left, top, right, bottom
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x_init_cord = x_cord;
                    y_init_cord = y_cord;
                    expandSpec = Utility.getExpandSpec(x_init_cord,y_init_cord,icon_distance,35,windowManager);
                    int expandWidth = expandSpec[2] - expandSpec[0];
                    int expandHeight = expandSpec[3] - expandSpec[1];
                    expandView = new ExpandStatusBarView(getApplicationContext());
                    expandView.setX(x_init_cord - icon_distance_pxl - distance_to_arc_pxl);
                    expandView.setY(y_init_cord - icon_distance_pxl - distance_to_arc_pxl);
                    expandView.setRadius((int) icon_distance_pxl + distance_to_arc_pxl);
                    expandView.setPosition(position);
                    expandView.setVisibility(View.INVISIBLE);
                    itemView.addView(expandView);

                    homeView = new ExpandStatusBarView(getApplicationContext());
                    homeView.setX(x_init_cord - icon_distance_pxl - distance_to_arc_pxl);
                    homeView.setY(y_init_cord - icon_distance_pxl - distance_to_arc_pxl);
                    homeView.setRadius((int) icon_distance_pxl + distance_to_arc_pxl);
                    homeView.setPosition(position);
                    homeView.setText("__home__");
                    homeView.setVisibility(View.INVISIBLE);
                    itemView.addView(homeView);

                    backView = new ExpandStatusBarView(getApplicationContext());
                    backView.setX(x_init_cord - icon_distance_pxl - distance_to_arc_pxl );
                    backView.setY(y_init_cord - icon_distance_pxl - distance_to_arc_pxl);
                    backView.setRadius((int) icon_distance_pxl + distance_to_arc_pxl);
                    backView.setPosition(position);
                    backView.setText("____back____");
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
                        String[] tempPackagename = new String[list.size() - 1];
                        int numOfException = 0;
                        if (list != null) {
                            for (int i = 1; i < list.size(); i++) {
                                ActivityManager.RunningTaskInfo taskInfo = list.get(i);
                                ComponentName componentName = taskInfo.baseActivity;
                                String packName = componentName.getPackageName();
                                if (launcherPackagename != null & packName.equals(launcherPackagename)) {
                                    Log.e(LOG_TAG, "category = home");
                                    tempPackagename[i - 1] = null;
                                    numOfException++;
                                } else tempPackagename[i - 1] = componentName.getPackageName();
                            }
                            packagename = new String[tempPackagename.length - numOfException];
                            int j = 0;
                            for (int i = 0; i < packagename.length; i++) {
                                while (packagename[i] == null) {
                                    if (tempPackagename[i + j] != null) {
                                        packagename[i] = tempPackagename[i + j];

                                    } else j++;
                                }
                            }
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
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
                        long time = System.currentTimeMillis();
                        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
                        int numOfTask = 6;
                        packagename = new String[6];
                        String[] tempPackagename = new String[15];
                        int j = 0;
                        int numbOfException = 0;
                        if (stats != null) {
                            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>(DATE_DECENDING_COMPARATOR);
                            for (UsageStats usageStats : stats) {
                                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                            }
                            if (!mySortedMap.isEmpty()) {
                                Set<Long> setKey = mySortedMap.keySet();
                                for (Long key : setKey) {
                                    if (j < tempPackagename.length) {
                                        UsageStats usageStats = mySortedMap.get(key);
                                        String packa = usageStats.getPackageName();
                                        try {
                                            if (getPackageManager().getApplicationInfo(packa, 0).dataDir.startsWith("/system/app/")) {
                                                tempPackagename[j] = null;
                                                numbOfException++;

                                            } else if (packa.contains("systemui")
                                                    | packa.contains("googlequicksearchbox")
                                                    | key == mySortedMap.firstKey()) {
                                                tempPackagename[j] = null;
                                                numbOfException++;
                                            } else {
                                                tempPackagename[j] = packa;
                                            }
                                        } catch (PackageManager.NameNotFoundException e) {
                                            Log.e(LOG_TAG, "packageName not found" + e);
                                        }
                                        j++;
                                    }

                                    Log.e(LOG_TAG, mySortedMap.get(key).getPackageName());
                                }
                                int k = 0;
                                int l = 0;
                                while (packagename[5] == null & l < tempPackagename.length) {
                                    if (tempPackagename[l] != null) {
                                        packagename[k] = tempPackagename[l];
                                        k++;
                                        l++;
                                    } else {
                                        l++;
                                    }
                                }
                            } else Log.e(LOG_TAG, " error in mySortedMap");
                        }
                        for (int i = 0; i < 6; i++) {
                            if (i >= packagename.length | packagename[i] == null) {
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
                        Intent extApp = getPackageManager().getLaunchIntentForPackage(packagename[packageToSwitch]);
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
                    if (homeBackNoti ==1){
                        AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
                        event1.setClassName(getClass().getName());
                        event1.getText().add("home");
                        event1.setAction(1);
                        event1.setPackageName(getPackageName());
                        event1.setEnabled(true);
                        AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
                        AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
                        recordCompat.setSource(v);
                        if (manager.isEnabled()){
                            manager.sendAccessibilityEvent(event1);
                        }
                    }else if (homeBackNoti ==2){
                        AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
                        event1.setClassName(getClass().getName());
                        event1.getText().add("back");
                        event1.setAction(2);
                        event1.setPackageName(getPackageName());
                        event1.setEnabled(true);
                        AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
                        AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
                        recordCompat.setSource(v);
                        if (manager.isEnabled()){
                            manager.sendAccessibilityEvent(event1);
                        }
                    }else if (homeBackNoti ==3){
                        expandStatusBar();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int iconToSwitch = Utility.findIconToSwitch(x, y, x_cord, y_cord, numOfIcon, icon_rad, windowManager);
                    int moveToHomeBackNoti = Utility.isHomeOrBackOrNoti(x_init_cord, y_init_cord, x_cord, y_cord, icon_distance, windowManager);

                    if (iconToSwitch != -1 |moveToHomeBackNoti>0 ) {
                        if (!hasVibrate) {
                            vibrator.vibrate(15);
                            hasVibrate = true;
                        }
                        for (int i = 0; i < listIcon.size(); i++) {
                            hasOneActive = true;
                            if (i == iconToSwitch) {
                                listIcon.get(i).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_background));
                            } else listIcon.get(i).setBackground(null);

                        }

                    }
                    if (iconToSwitch == -1 & moveToHomeBackNoti ==0) {
                        hasVibrate = false;
                        if (hasOneActive) {
                            for (ImageView imageView : listIcon) {
                                imageView.setBackground(null);
                            }
                        }
                        hasOneActive = false;
                    }
                    Log.e(LOG_TAG,"movetoHomeBackNoti = " + moveToHomeBackNoti);
                    switch (moveToHomeBackNoti){
                        case 0: homeView.setVisibility(View.INVISIBLE);
                            backView.setVisibility(View.INVISIBLE);
                            expandView.setVisibility(View.INVISIBLE);
                            break;
                        case 1: homeView.setVisibility(View.VISIBLE);
                            backView.setVisibility(View.INVISIBLE);
                            expandView.setVisibility(View.INVISIBLE);
                            break;
                        case 2: homeView.setVisibility(View.INVISIBLE);
                            backView.setVisibility(View.VISIBLE);
                            expandView.setVisibility(View.INVISIBLE);
                            break;
                        case 3: homeView.setVisibility(View.INVISIBLE);
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
        if (newConfig.orientation != configuration){
            try {
                windowManager.removeView(item1View);
                windowManager.removeView(item2View);
            }catch (NullPointerException e){
                Log.e("onConfiguration","Null");
            }catch (IllegalArgumentException e){
                Log.e("onConfiguration","Illegal");
            }
            configuration = newConfig.orientation;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mScale = getResources().getDisplayMetrics().density;
        Log.e(LOG_TAG,"onCreate service");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(LOG_TAG,"onDestroy service");
    }
}
