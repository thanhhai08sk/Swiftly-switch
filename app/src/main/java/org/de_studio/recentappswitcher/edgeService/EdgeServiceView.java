package org.de_studio.recentappswitcher.edgeService;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.favoriteShortcut.CircleFavoriteAdapter;
import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;
import org.de_studio.recentappswitcher.service.EdgeSetting;
import org.de_studio.recentappswitcher.service.FavoriteShortcutAdapter;
import org.de_studio.recentappswitcher.service.MyImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import static org.de_studio.recentappswitcher.Cons.BACKGROUND_COLOR_NAME;
import static org.de_studio.recentappswitcher.Cons.BACKGROUND_FRAME_NAME;
import static org.de_studio.recentappswitcher.Cons.BACKGROUND_FRAME_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.DEFAULT_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_OFFSET_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_SENSITIVE_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_OFFSET_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_SENSITIVE_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.FOLDER_GRID_VIEW_NAME;

/**
 * Created by HaiNguyen on 8/19/16.
 */
public class EdgeServiceView extends Service implements View.OnTouchListener {

    private static final String TAG = EdgeServiceView.class.getSimpleName();
    @Inject
    WindowManager windowManager;
    @Inject
    LayoutInflater layoutInflater;
    @Inject
    @Named(EDGE_1_SENSITIVE_NAME)
    int edge1Sensitive;
    @Inject
    @Named(EDGE_2_SENSITIVE_NAME)
    int edge2Sensitive;
    @Inject
    @Named(EDGE_1_OFFSET_NAME)
    int edge1Offset;
    @Inject
    @Named(EDGE_2_OFFSET_NAME)
    int edge2Offset;
    Vibrator vibrator;
    @Inject
    @Named(EDGE_1_SHARED_NAME)
    SharedPreferences edge1Shared;
    @Inject
    @Named(EDGE_2_SHARED_NAME)
    SharedPreferences edge2Shared;
    @Inject
    @Named(DEFAULT_SHARED_NAME)
    SharedPreferences defaultShared;
    @Inject
    @Named(FAVORITE_GRID_VIEW_NAME)
    GridView favoriteGridView;
    @Inject
    @Named(FOLDER_GRID_VIEW_NAME)
    GridView folderGridView;
    @Inject
    @Named(EDGE_1_PARA_NAME)
    WindowManager.LayoutParams edge1Para;
    @Inject
    @Named(EDGE_2_PARA_NAME)
    WindowManager.LayoutParams edge2Para;
    @Inject
    @Named(Cons.CIRCLE_SHORTCUT_VIEW_PARA_NAME)
    WindowManager.LayoutParams circleShortcutsViewPara;
    @Inject
    @Named(EDGE_1_VIEW_NAME)
    View edge1View;
    @Inject
    @Named(EDGE_2_VIEW_NAME)
    View edge2View;
    @Inject
    @Named(Cons.GRID_PARENT_VIEW_PARA_NAME)
    WindowManager.LayoutParams gridShortcutParentViewPara;
    @Inject
    FavoriteShortcutAdapter gridShortcutsAdapter;
    @Inject
    IconPackManager.IconPack iconPack;
    @Inject
    @Named(Cons.GUIDE_COLOR_NAME)
    int guideColor;
    @Inject
    @Named(Cons.CIRCLE_PARENTS_VIEW_NAME)
    FrameLayout circleParentsView;
    @Inject
    @Named(Cons.GRID_PARENTS_VIEW_NAME)
    FrameLayout gridParentsView;
    @Inject
    MyImageView[] circleIcons;
    @Inject
    @Named(Cons.EDGE_1_POSITION_NAME)
    int edge1Position;
    @Inject
    @Named(Cons.EDGE_2_POSITION_NAME)
    int edge2Position;
    @Inject
    @Named(BACKGROUND_COLOR_NAME)
    int backgroundColor;
    @Inject
    @Named(BACKGROUND_FRAME_PARA_NAME)
    WindowManager.LayoutParams backgroundPara;
    @Inject
    @Named(BACKGROUND_FRAME_NAME)
    FrameLayout backgroundFrame;

    @Inject
    FavoriteShortcutAdapter gridFavoriteAdapter;
    @Inject
    CircleFavoriteAdapter circleFavoriteAdapter;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

    public Point getWindowSize() {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point;
    }


    public boolean isEdge1On() {
        return edge1Shared.getBoolean(Cons.EDGE_ON_KEY, true);
    }

    public boolean isEdge2On() {
        return edge2Shared.getBoolean(Cons.EDGE_ON_KEY, false);
    }


    public void setVibrator() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }


    public void addEdgeToWindowManager(String edgeTag) {
        if (!(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && defaultShared.getBoolean(EdgeSetting.IS_DISABLE_IN_LANSCAPE,false)) ) {
            switch (edgeTag) {
                case Cons.TAG_EDGE_1:
                    windowManager.addView(edge1View, edge1Para);
                    break;
                case Cons.TAG_EDGE_2:
                    windowManager.addView(edge2View, edge2Para);
                    break;
            }
        }
    }




    public void setOnTouchListener(boolean edge1On, boolean edge2On) {
        if (edge1On) {
            edge1View.setOnTouchListener(this);
        }

        if (edge2On) {
            edge2View.setOnTouchListener(this);
        }
    }




    private int getEdgeSensitive(String edgeTag) {
        switch (edgeTag) {
            case Cons.TAG_EDGE_1:
                return edge2Sensitive;
            case Cons.TAG_EDGE_2:
                return edge2Sensitive;
        }
        return Cons.EDGE_SENSITIVE_DEFAULT;
    }

    public int getEdgeOffset(String edgeTag) {
        switch (edgeTag) {
            case Cons.TAG_EDGE_1:
                return edge1Offset;
            case Cons.TAG_EDGE_2:
                return edge2Offset;
            default:
                return Cons.EDGE_OFFSET_DEFAULT;
        }
    }

    public ArrayList<String> getRecentApps(String launcherPackagename, Set<String> excludeSet) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            int numOfTask;
            if (launcherPackagename != null) {
                numOfTask = 8;
            } else numOfTask = 7;
            List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(numOfTask);
            ArrayList<String> tempPackageNameKK = new ArrayList<String>();
            for (int i = 0; i < list.size(); i++) {
                ActivityManager.RunningTaskInfo taskInfo = list.get(i);
                ComponentName componentName = taskInfo.baseActivity;
                String packName = componentName.getPackageName();
                if (i != 0 && !packName.equals(launcherPackagename) && !excludeSet.contains(packName) && !packName.contains("launcher")) {
                    tempPackageNameKK.add(packName);
                }
            }
            return tempPackageNameKK;
        } else {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
            long currentTimeMillis = System.currentTimeMillis() + 5000;
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, currentTimeMillis - 1000 * 1000, currentTimeMillis);
            ArrayList<String> tempPackageName = new ArrayList<String>();
            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>(Cons.DATE_DECENDING_COMPARATOR);
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                Set<Long> setKey = mySortedMap.keySet();
                Log.e(TAG, "mySortedMap size   = " + mySortedMap.size());
                UsageStats usageStats;
                String packa;
                boolean isSystem = false;
                PackageManager packageManager = getPackageManager();
                boolean hasKeyInFuture = false;
                for (Long key : setKey) {
                    if (key >= currentTimeMillis) {
                        hasKeyInFuture = true;
                        Log.e(TAG, "key is in future");
                    } else {
                        usageStats = mySortedMap.get(key);
                        if (usageStats == null) {
                            Log.e(TAG, " usageStats is null");
                        } else {
                            packa = usageStats.getPackageName();
                            try {
                                try {
                                    isSystem = packageManager.getApplicationInfo(packa, 0).dataDir.startsWith("/system/app/");
                                } catch (NullPointerException e) {
                                    Log.e(TAG, "isSystem = null");
                                }
                                if (isSystem) {
                                    //do nothing
                                } else if (packageManager.getLaunchIntentForPackage(packa) == null ||
                                        packa.contains("systemui") ||
                                        packa.contains("googlequicksearchbox") ||
                                        excludeSet.contains(packa) ||
                                        tempPackageName.contains(packa)
                                        ) {
                                    // do nothing
                                } else tempPackageName.add(packa);
                                if (tempPackageName.size() >= 8) {
                                    Log.e(TAG, "tempackage >= 8");
                                    break;
                                }
                            } catch (PackageManager.NameNotFoundException e) {
                                Log.e(TAG, "name not found" + e);
                            }
                        }

                    }

                }
            }
            return tempPackageName;
        }
    }


    public void showCircleShortcutsView(Shortcut[] shortcuts) {
        for (int i = 0; i < 6; i++) {
            if (i >= shortcuts.length) {
                circleIcons[i].setImageDrawable(null);
            } else {
                Utility.setImageForShortcut(shortcuts[i], getPackageManager(), circleIcons[i], getApplicationContext(), iconPack, null, true);
            }
        }
        try {
            windowManager.addView(circleParentsView, circleShortcutsViewPara);
        } catch (IllegalStateException e) {
            Log.e(TAG, " item_view has already been added to the window manager");
        }
    }

    public void showFavoriteGridShortcutsView() {

    }

    public synchronized void removeCircleShortcutsView() {
        try {
            windowManager.removeView(circleParentsView);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove View");
        }
    }

    public void showGridShortcutsView() {

    }

    public void setupShortcutsGridView() {

    }
    public void setupGridView() {
        gridParentsView = (FrameLayout) layoutInflater.inflate(R.layout.grid_shortcut, null);
        favoriteGridView = (GridView) gridParentsView.findViewById(R.id.edge_shortcut_grid_view);
        folderGridView = (GridView) gridParentsView.findViewById(R.id.folder_grid);
    }





}
