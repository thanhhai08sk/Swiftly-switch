package org.de_studio.recentappswitcher.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by hai on 12/15/2015.
 */
public class EdgeGestureService extends Service {
    static final String LOG_TAG = EdgeGestureService.class.getSimpleName();
    static final int EDGE_GESTURE_NOTIFICAION_ID = 10;
    private WindowManager windowManager;
    private RelativeLayout edgeView;
    private ImageView edgeImage;
    private LinearLayout itemView;
    public int icon_height = 48;
    public int icon_width = 48, icon_rad = 24;
    public int icon_distance = 150;
    public float icon_distance_pxl,icon_24dp_in_pxls;
    public int edge_height = 150;
    public int edge_height_pxl;
    public int edge_width_pxl;
    public int edge_width = 18;
    public int edge_y;
    public int edge_centre_y;
    public int x_init_cord, y_init_cord;
    private ImageView icon0, icon1, icon2,icon3,icon4,icon5;
    private List<ImageView> list_icon;
    private String[] packagename;
    private String launcherPackagename;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo res = getPackageManager().resolveActivity(launcherIntent,0);
        if (res.activityInfo != null){
            launcherPackagename = res.activityInfo.packageName;
        }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        edgeView =(RelativeLayout) layoutInflater.inflate(R.layout.edge_view, null);
        edgeImage = (ImageView) edgeView.findViewById(R.id.edge_image);
        ViewGroup.LayoutParams lp = edgeImage.getLayoutParams();
        edge_height_pxl = Utility.dpiToPixels(edge_height,windowManager);
        edge_width_pxl = Utility.dpiToPixels(edge_width,windowManager);
        lp.height = edge_height_pxl;
        lp.width =  edge_width_pxl;
        edge_y = (int)edgeImage.getY();
        edge_centre_y = edge_y + edge_height_pxl/2;
        icon_distance_pxl = (float) Utility.dpiToPixels(icon_distance, windowManager);
        icon_24dp_in_pxls = (float) Utility.dpiToPixels(24,windowManager);
        edgeImage.setLayoutParams(lp);
        itemView =(LinearLayout)layoutInflater.inflate(R.layout.item, null);
        icon0 = (ImageView) itemView.findViewById(R.id.item_0);
        icon0.setX(0);
        icon0.setY(300);
        icon1 = (ImageView) itemView.findViewById(R.id.item_1);
        icon2 = (ImageView) itemView.findViewById(R.id.item_2);
        icon3 = (ImageView) itemView.findViewById(R.id.item_3);
        icon4 = (ImageView) itemView.findViewById(R.id.item_4);
        icon5 = (ImageView) itemView.findViewById(R.id.item_5);
        list_icon = new ArrayList<ImageView>();
        list_icon.add(icon0);
        list_icon.add(icon1);
        list_icon.add(icon2);
        list_icon.add(icon3);
        list_icon.add(icon4);
        list_icon.add(icon5);
        WindowManager.LayoutParams paramEdge = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramEdge.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        windowManager.addView(edgeView, paramEdge);
        edgeImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        WindowManager.LayoutParams paraItem = new WindowManager.LayoutParams(
                                WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.TYPE_PHONE,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                                PixelFormat.TRANSLUCENT);
                        paraItem.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                        windowManager.addView(itemView, paraItem);
                        icon0.setX((float) (x_init_cord + 0.26 * icon_distance_pxl) - icon_24dp_in_pxls);
                        icon0.setY(y_init_cord - (float) 0.96 * icon_distance_pxl - icon_24dp_in_pxls);
                        icon1.setX((float) (x_init_cord + 0.71 * icon_distance_pxl) - icon_24dp_in_pxls);
                        icon1.setY(y_init_cord - (float) 0.71 * icon_distance_pxl - icon_24dp_in_pxls);
                        icon2.setX((float) (x_init_cord + 0.97 * icon_distance_pxl) - icon_24dp_in_pxls);
                        icon2.setY(y_init_cord - (float) 0.26 * icon_distance_pxl - icon_24dp_in_pxls);
                        icon3.setX((float) (x_init_cord + 0.97 * icon_distance_pxl) - icon_24dp_in_pxls);
                        icon3.setY(y_init_cord + (float) 0.26 * icon_distance_pxl - icon_24dp_in_pxls);
                        icon4.setX((float) (x_init_cord + 0.71 * icon_distance_pxl) - icon_24dp_in_pxls);
                        icon4.setY(y_init_cord + (float) 0.71 * icon_distance_pxl - icon_24dp_in_pxls);
                        icon5.setX((float) (x_init_cord + 0.26 * icon_distance_pxl) - icon_24dp_in_pxls);
                        icon5.setY(y_init_cord + (float) 0.97 * icon_distance_pxl - icon_24dp_in_pxls);
                        String topPackageName;
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
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
                                    Set<String> setCategoty = getPackageManager().getLaunchIntentForPackage(packName).getCategories();
                                    if (launcherPackagename != null & packName.equals(launcherPackagename)) {
                                        Log.e(LOG_TAG, "category = home");
                                        tempPackagename[i - 1] = null;
                                        numOfException++;
                                    } else if (packName.equals("org.de_studio.phonetools")) {
                                        Log.e(LOG_TAG, "package mobai");
                                        tempPackagename[i - 1] = null;
                                        numOfException++;
                                    } else tempPackagename[i - 1] = componentName.getPackageName();
//                                    final String packageName = componentName.getPackageName();
//                                    try {
//                                        Drawable icon = getPackageManager().getApplicationIcon(packageName);
//                                        ImageView iconi = list_icon.get(i - 1);
//                                        iconi.setImageDrawable(icon);
//                                    } catch (PackageManager.NameNotFoundException e) {
//                                        Log.e(LOG_TAG, "NameNotFound" + e);
//                                    }
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
                                        list_icon.get(i).setImageDrawable(null);
                                    } else {
                                        try {
                                            Drawable icon = getPackageManager().getApplicationIcon(packagename[i]);
                                            ImageView iconi = list_icon.get(i);
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
                            // We get usage stats for the last 10 seconds
                            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
                            // Sort the stats by the last time used
                            if (stats != null) {
                                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                                for (UsageStats usageStats : stats) {
                                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                                }
                                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                                    topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                                    Toast.makeText(getApplicationContext(), topPackageName, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        windowManager.removeView(itemView);
//                        int numOfIcon = packagename.length;
                        int numOfIcon = list_icon.size();
                        int[] x = new int[numOfIcon];
                        int[] y = new int[numOfIcon];
                        for (int i = 0; i < numOfIcon; i++) {
                            x[i] = (int) list_icon.get(i).getX();
                            y[i] = (int) list_icon.get(i).getY();
                        }
                        int packageToSwitch = Utility.findIconToSwitch(x, y, x_cord, y_cord, numOfIcon, icon_rad, windowManager);
                        if (packageToSwitch != -1) {
                            Intent extApp = getPackageManager().getLaunchIntentForPackage(packagename[packageToSwitch]);
                            extApp.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(extApp);
                        }
                        packagename = null;
                }

                return true;
            }
        });
        return START_STICKY;
    }
}
