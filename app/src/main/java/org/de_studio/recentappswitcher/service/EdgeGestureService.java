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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.de_studio.recentappswitcher.R;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by hai on 12/15/2015.
 */
public class EdgeGestureService extends Service {
    static final int EDGE_GESTURE_NOTIFICAION_ID = 10;
    private WindowManager windowManager;
    private RelativeLayout edgeView;
    private ImageView edgeImage;
    private LinearLayout itemView;

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
        itemView =(LinearLayout) edgeView.findViewById(R.id.item_view);
        WindowManager.LayoutParams paramEdge = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramEdge.gravity = Gravity.TOP | Gravity.LEFT;
        windowManager.addView(edgeView,paramEdge);
        edgeImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        itemView.setVisibility(View.VISIBLE);
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
                                startActivity(extApp);

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
                        itemView.setVisibility(View.GONE);
                }

                return true;
            }
        });
        return START_STICKY;
    }
}
