package org.de_studio.recentappswitcher.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
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

import java.util.List;
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
    public int icon_width = 48;
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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        icon_distance_pxl = (float) Utility.dpiToPixels(icon_distance,windowManager);
        icon_24dp_in_pxls = (float) Utility.dpiToPixels(24,windowManager);
        edgeImage.setLayoutParams(lp);
        itemView =(LinearLayout)layoutInflater.inflate(R.layout.item, null);
        icon0 = (ImageView) itemView.findViewById(R.id.item_0);
        icon0.setX(0);
        icon0.setY(300);
//        icon0.setY((float) (edge_centre_y - 0.97 * icon_distance_pxl));
        icon1 = (ImageView) itemView.findViewById(R.id.item_1);
//        icon1.setX((float)0.71*icon_distance_pxl);
//        icon1.setY((float) (edge_centre_y - 0.71 * icon_distance_pxl));
        icon2 = (ImageView) itemView.findViewById(R.id.item_2);
//        icon2.setX((float)0.97*icon_distance_pxl);
//        icon2.setY((float) (edge_centre_y - 0.26 * icon_distance_pxl));
        icon3 = (ImageView) itemView.findViewById(R.id.item_3);
//        icon3.setX((float)0.97*icon_distance_pxl);
//        icon3.setY((float) (edge_centre_y + 0.26 * icon_distance_pxl));
        icon4 = (ImageView) itemView.findViewById(R.id.item_4);
//        icon4.setX((float)0.71*icon_distance_pxl);
//        icon4.setY((float) (edge_centre_y + 0.71 * icon_distance_pxl));
        icon5 = (ImageView) itemView.findViewById(R.id.item_5);
//        icon5.setX((float)0.26*icon_distance_pxl);
//        icon5.setY((float) (edge_centre_y + 0.97 * icon_distance_pxl));
        WindowManager.LayoutParams paramEdge = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramEdge.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        windowManager.addView(edgeView,paramEdge);
        edgeImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x_cord =(int) event.getRawX();
                int y_cord = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x_init_cord = x_cord;
                        y_init_cord = y_cord;
                        icon0.setX((float)(x_init_cord + 0.26*icon_distance_pxl)-icon_24dp_in_pxls);
                        icon0.setY(y_init_cord- (float)0.96*icon_distance_pxl - icon_24dp_in_pxls);
                        icon1.setX((float)(x_init_cord + 0.71*icon_distance_pxl) - icon_24dp_in_pxls);
                        icon1.setY(y_init_cord- (float)0.71*icon_distance_pxl - icon_24dp_in_pxls);
                        icon2.setX((float)(x_init_cord + 0.97*icon_distance_pxl) - icon_24dp_in_pxls);
                        icon2.setY(y_init_cord- (float)0.26*icon_distance_pxl - icon_24dp_in_pxls);
                        icon3.setX((float)(x_init_cord + 0.97*icon_distance_pxl) - icon_24dp_in_pxls);
                        icon3.setY(y_init_cord + (float)0.26*icon_distance_pxl - icon_24dp_in_pxls);
                        icon4.setX((float)(x_init_cord + 0.71*icon_distance_pxl) - icon_24dp_in_pxls);
                        icon4.setY(y_init_cord + (float)0.71*icon_distance_pxl - icon_24dp_in_pxls);
                        icon5.setX((float)(x_init_cord + 0.26*icon_distance_pxl) - icon_24dp_in_pxls);
                        icon5.setY(y_init_cord + (float)0.97*icon_distance_pxl - icon_24dp_in_pxls);
                        WindowManager.LayoutParams paraItem = new WindowManager.LayoutParams(
                                WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.TYPE_PHONE,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                                PixelFormat.TRANSLUCENT);
                        paraItem.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                        windowManager.addView(itemView,paraItem);
                        String topPackageName;
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                            int numOfTask = 6;
                            List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(numOfTask);
                            if (list != null) {
                                ActivityManager.RunningTaskInfo taskInfo = list.get(1);
                                ComponentName componentName = taskInfo.baseActivity;
                                String packageName = componentName.getPackageName();
                                Intent extApp= getPackageManager().getLaunchIntentForPackage(packageName);
                                String className = componentName.getClassName();
                                Toast.makeText(getApplicationContext(),"The no2 task is " + packageName, Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent().setClassName(packageName, className).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                                startActivity(extApp);

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
                }

                return true;
            }
        });
        return START_STICKY;
    }
}
