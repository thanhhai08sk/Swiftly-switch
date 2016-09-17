package org.de_studio.recentappswitcher.edgeService;

import android.Manifest;
import android.animation.Animator;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerEdgeServiceComponent;
import org.de_studio.recentappswitcher.dagger.EdgeServiceModule;
import org.de_studio.recentappswitcher.dagger.RealmModule;
import org.de_studio.recentappswitcher.favoriteShortcut.CircleFavoriteAdapter;
import org.de_studio.recentappswitcher.favoriteShortcut.FavoriteSettingActivity;
import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;
import org.de_studio.recentappswitcher.service.ChooseActionDialogActivity;
import org.de_studio.recentappswitcher.service.Circle;
import org.de_studio.recentappswitcher.service.CircleAngleAnimation;
import org.de_studio.recentappswitcher.service.EdgeGestureService;
import org.de_studio.recentappswitcher.service.EdgeSetting;
import org.de_studio.recentappswitcher.service.ExpandStatusBarView;
import org.de_studio.recentappswitcher.service.FavoriteShortcutAdapter;
import org.de_studio.recentappswitcher.service.FolderAdapter;
import org.de_studio.recentappswitcher.service.MyImageView;
import org.de_studio.recentappswitcher.service.NotiDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import io.realm.Realm;

import static org.de_studio.recentappswitcher.Cons.ANIMATION_TIME_NAME;
import static org.de_studio.recentappswitcher.Cons.BACKGROUND_COLOR_NAME;
import static org.de_studio.recentappswitcher.Cons.BACKGROUND_FRAME_NAME;
import static org.de_studio.recentappswitcher.Cons.BACKGROUND_FRAME_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_AND_QUICK_ACTION_GAP;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_SIZE_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.CLOCK_LINEAR_LAYOUT_NAME;
import static org.de_studio.recentappswitcher.Cons.CLOCK_PARENTS_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.CLOCK_PARENTS_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.DEFAULT_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_ID;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_MODE_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_OFFSET_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_QUICK_ACTION_VIEWS_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_SENSITIVE_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_ID;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_MODE_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_OFFSET_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_QUICK_ACTION_VIEWS_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_SENSITIVE_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.EXCLUDE_SET_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_ADAPTER_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_PADDING_HORIZONTAL_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_PADDING_VERTICAL_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_REALM_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.FOLDER_ADAPTER_NAME;
import static org.de_studio.recentappswitcher.Cons.FOLDER_CIRCLE_NAME;
import static org.de_studio.recentappswitcher.Cons.FOLDER_GRID_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_HEIGHT_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_NUMBER_COLUMNS_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_NUMBER_ROWS_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_WIDTH_NAME;
import static org.de_studio.recentappswitcher.Cons.HOLD_TIME_ENABLE_NAME;
import static org.de_studio.recentappswitcher.Cons.HOLD_TIME_NAME;
import static org.de_studio.recentappswitcher.Cons.ICON_SCALE_NAME;
import static org.de_studio.recentappswitcher.Cons.ICON_SIZE_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.INDICATOR_FRAME_LAYOUT_NAME;
import static org.de_studio.recentappswitcher.Cons.IS_EDGE_1_ON_NAME;
import static org.de_studio.recentappswitcher.Cons.IS_EDGE_2_ON_NAME;
import static org.de_studio.recentappswitcher.Cons.IS_FREE_AND_OUT_OF_TRIAL_NAME;
import static org.de_studio.recentappswitcher.Cons.LAUNCHER_PACKAGENAME_NAME;
import static org.de_studio.recentappswitcher.Cons.M_SCALE_NAME;
import static org.de_studio.recentappswitcher.Cons.NO_INTENT_PACKAGES_NAME;
import static org.de_studio.recentappswitcher.Cons.OVAL_OFFSET;
import static org.de_studio.recentappswitcher.Cons.OVAL_RADIUS_PLUS;
import static org.de_studio.recentappswitcher.Cons.QUICK_ACTION_WITH_INSTANT_FAVORITE_NAME;
import static org.de_studio.recentappswitcher.Cons.USE_ACTION_DOW_VIBRATE_NAME;
import static org.de_studio.recentappswitcher.Cons.USE_ACTION_MOVE_VIBRATE_NAME;
import static org.de_studio.recentappswitcher.Cons.USE_ANIMATION_NAME;
import static org.de_studio.recentappswitcher.Cons.USE_CLOCK_NAME;
import static org.de_studio.recentappswitcher.Cons.USE_INSTANT_FAVORITE_NAME;
import static org.de_studio.recentappswitcher.Cons.VIBRATE_DURATION_NAME;

/**
 * Created by HaiNguyen on 8/19/16.
 */
public class EdgeServiceView extends Service implements View.OnTouchListener {

    private static final String TAG = EdgeServiceView.class.getSimpleName();
    public static boolean FLASH_LIGHT_ON = false;
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
    @Inject
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
    WindowManager.LayoutParams gridParentViewPara;
    @Inject
    @Nullable
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
    @Named(M_SCALE_NAME)
    float mScale;
    @Inject
    @Named(ICON_SCALE_NAME)
    float iconScale;
    @Inject
    @Named(FAVORITE_GRID_ADAPTER_NAME)
    FavoriteShortcutAdapter gridFavoriteAdapter;
    @Inject
    CircleFavoriteAdapter circleFavoriteAdapter;
    @Inject
    @Named(FAVORITE_GRID_PADDING_HORIZONTAL_NAME)
    int favoriteGridPaddingHorizontal;
    @Inject
    @Named(FAVORITE_GRID_PADDING_VERTICAL_NAME)
    int favoriteGridPaddingVertical;
    @Inject
    @Named(FAVORITE_GRID_REALM_NAME)
    Realm favoriteRealm;
    @Inject
    @Named(Cons.FAVORITE_CIRCLE_REALM_NAME)
    Realm circleRealm;
    @Inject
    @Named(EDGE_1_QUICK_ACTION_VIEWS_NAME)
    ExpandStatusBarView[] edge1QuickActionViews;
    @Inject
    @Named(EDGE_2_QUICK_ACTION_VIEWS_NAME)
    ExpandStatusBarView[] edge2QuickActionViews;
    @Inject
    @Named(CIRCLE_SIZE_PXL_NAME)
    float circleSizePxl;
    @Inject
    @Named(IS_EDGE_1_ON_NAME)
    boolean isEdge1On;
    @Inject
    @Named(IS_EDGE_2_ON_NAME)
    boolean isEdge2On;
    @Inject
    @Named(HOLD_TIME_NAME)
    int holdTime;
    @Inject
    @Named(HOLD_TIME_ENABLE_NAME)
    boolean useHoldTime;
    @Inject
    @Named(VIBRATE_DURATION_NAME)
    int vibrationDuration;
    @Inject
    @Named(ANIMATION_TIME_NAME)
    int animationTime;
    @Inject
    @Named(EDGE_1_MODE_NAME)
    int edge1Mode;
    @Inject
    @Named(EDGE_2_MODE_NAME)
    int edge2Mode;
    @Inject
    @Named(QUICK_ACTION_WITH_INSTANT_FAVORITE_NAME)
    int[] quickActionWithInstantFavorite;
    @Inject
    @Named(USE_INSTANT_FAVORITE_NAME)
    boolean useInstantFavorite;
    @Inject
    EdgeServicePresenter presenter;
    @Inject
    EdgesServiceModel model;
    @Inject
    @Named(CLOCK_PARENTS_VIEW_NAME)
    View clockParentsView;
    @Inject
    @Named(CLOCK_PARENTS_PARA_NAME)
    WindowManager.LayoutParams clockParentsPara;
    @Inject
    @Named(USE_ANIMATION_NAME)
    boolean useAnimation;
    @Inject
    @Named(ICON_SIZE_PXL_NAME)
    float iconSizePxl;
    @Inject
    @Named(LAUNCHER_PACKAGENAME_NAME)
    String launcherPackageName;
    @Inject
    @Named(EXCLUDE_SET_NAME)
    Set<String> excludeSet;
    @Inject
    @Named(USE_ACTION_DOW_VIBRATE_NAME)
    boolean useActionDownVibrate;
    @Inject
    @Named(USE_ACTION_MOVE_VIBRATE_NAME)
    boolean useActionMoveVibrate;
    @Inject
    @Named(USE_CLOCK_NAME)
    boolean useClock;
    String lastAppPackageName;
    @Inject
    @Named(GRID_WIDTH_NAME)
    float gridWidth;
    @Inject
    @Named(GRID_HEIGHT_NAME)
    float gridHeight;
    @Inject
    @Named(GRID_NUMBER_COLUMNS_NAME)
    int gridColumns;
    @Inject
    @Named(GRID_NUMBER_ROWS_NAME)
    int gridRows;
    @Inject
    @Named(INDICATOR_FRAME_LAYOUT_NAME)
    FrameLayout indicator;
    @Inject
    @Named(CLOCK_LINEAR_LAYOUT_NAME)
    LinearLayout clock;
    @Inject
    @Named(FOLDER_CIRCLE_NAME)
    Circle folderCircle;
    @Inject
    @Named(FOLDER_ADAPTER_NAME)
    FolderAdapter folderAdapter;
    @Inject
    @Named(IS_FREE_AND_OUT_OF_TRIAL_NAME)
    boolean isFreeAndOutOfTrial;
    @Inject
    DelayToSwitchAsyncTask asyncTask;
    @Inject
    @Named(NO_INTENT_PACKAGES_NAME)
    Set<String> noIntentPackagesSet;
    @Inject
    PackageManager packageManager;
    @Inject
    UsageStatsManager usageStatsManager;

    ViewPropertyAnimator folderAnimator;
    float[] folderCoor;
    boolean working = true;
    EdgesToggleReceiver receiver;
    private NotificationCompat.Builder notificationBuilder;


    private static void startQuickAction(Context context, String action, View v, String className, String packageName, String lastAppPackageName) {
        switch (action) {
            case MainActivity.ACTION_HOME:
                Utility.homeAction(context, v, className, packageName);
                break;
            case MainActivity.ACTION_BACK:
                Utility.backAction(context,v,className,packageName);
                break;
            case MainActivity.ACTION_NOTI:
                Utility.notiAction(context,v,className,packageName);
                break;
            case MainActivity.ACTION_WIFI:
                Utility.toggleWifi(context);
                break;
            case MainActivity.ACTION_BLUETOOTH:
                Utility.toggleBluetooth(context);
                break;
            case MainActivity.ACTION_ROTATE:
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
            case MainActivity.ACTION_NONE:
                //nothing
                break;
            case MainActivity.ACTION_POWER_MENU:
                Utility.powerAction(context,v,className,packageName);
                break;
            case MainActivity.ACTION_LAST_APP:
                Utility.lastAppAction(context, lastAppPackageName);
                break;
            case MainActivity.ACTION_CONTACT:
                Utility.contactAction(context);
                break;
            case MainActivity.ACTION_CALL_LOGS:
                Utility.callLogsAction(context);
                break;
            case MainActivity.ACTION_DIAL:
                Utility.dialAction(context);
                break;
            case MainActivity.ACTION_RECENT:
                Utility.recentAction(context,v,className,packageName);
                break;
            case MainActivity.ACTION_VOLUME:
                Utility.volumeAction(context);
                break;
            case MainActivity.ACTION_BRIGHTNESS:
                Utility.brightnessAction(context);
                break;
            case MainActivity.ACTION_RINGER_MODE:
                Utility.setRinggerMode(context);
            case MainActivity.ACTION_FLASH_LIGHT:
                Utility.flashLightAction2(context,EdgeGestureService.FLASH_LIGHT_ON);
                break;
            case MainActivity.ACTION_SCREEN_LOCK:
                Utility.screenLockAction(context);
                break;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        inject();
        presenter.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        presenter.onStartCommand();
        return START_STICKY;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "onTouch: action down");
                presenter.onActionDown(getXCord(event), getYCord(event), view.getId());
                break;
            case MotionEvent.ACTION_MOVE:
                presenter.onActionMove(getXCord(event), getYCord(event), view.getId());
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "onTouch: action up");
                presenter.onActionUp(getXCord(event), getYCord(event), view.getId(), view);
                break;
            case MotionEvent.ACTION_OUTSIDE:
                presenter.onActionOutSide();
                Log.e(TAG, "onTouch: action outside");
                break;
            case MotionEvent.ACTION_CANCEL:
                presenter.onActionCancel();
                Log.e(TAG, "onTouch: action cancel");
                break;
        }

        return true;
    }

    public Point getWindowSize() {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point;
    }

    private float getXCord(MotionEvent motionEvent) {
        return  motionEvent.getRawX();
    }

    private float getYCord(MotionEvent motionEvent) {
        return  motionEvent.getRawY();
    }

    public void addEdgeToWindowManager(int edgeId) {
        if (!(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && defaultShared.getBoolean(EdgeSetting.IS_DISABLE_IN_LANSCAPE,false)) ) {
            switch (edgeId) {
                case Cons.EDGE_1_ID:
                    windowManager.addView(edge1View, edge1Para);
                    break;
                case Cons.EDGE_2_ID:
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

    private int getEdgeSensitive(int edgeId) {
        switch (edgeId) {
            case Cons.EDGE_1_ID:
                return edge2Sensitive;
            case Cons.EDGE_2_ID:
                return edge2Sensitive;
        }
        return Cons.EDGE_SENSITIVE_DEFAULT;
    }

    public int getEdgeOffset(int edgeId) {
        switch (edgeId) {
            case Cons.EDGE_1_ID:
                return edge1Offset;
            case Cons.EDGE_2_ID:
                return edge2Offset;
            default:
                return Cons.EDGE_OFFSET_DEFAULT;
        }
    }

    public ArrayList<String> getRecentApps() {
        long timeStart = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            int numOfTask;
            if (launcherPackageName != null) {
                numOfTask = 8;
            } else numOfTask = 7;
            List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(numOfTask);
            ArrayList<String> tempPackageNameKK = new ArrayList<String>();
            for (int i = 0; i < list.size(); i++) {
                ActivityManager.RunningTaskInfo taskInfo = list.get(i);
                ComponentName componentName = taskInfo.baseActivity;
                String packName = componentName.getPackageName();
                if (i != 0 && !packName.equals(launcherPackageName) && !excludeSet.contains(packName) && !packName.contains("launcher")) {
                    tempPackageNameKK.add(packName);
                }
            }
            if (tempPackageNameKK.size()>=1) {
                lastAppPackageName = tempPackageNameKK.get(1);
            }
            return tempPackageNameKK;
        } else {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
            long currentTimeMillis = System.currentTimeMillis() + 2000;
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
                for (Long key : setKey) {
                    if (key >= currentTimeMillis) {
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

//                                    Log.e(TAG, "app: " + packa +
//                                            "\nfirst time stamp = " + usageStats.getFirstTimeStamp()
//                                            + "\nlast time stamp = " + usageStats.getLastTimeStamp()
//                                            + "\nlast time used = " + usageStats.getLastTimeUsed()
//                                            + "\ntotal time foreground = " + usageStats.getTotalTimeInForeground()
//                                            + "\ndescribe = " + usageStats.describeContents()
//                                            + "\nstring = " + usageStats.toString());

                                } catch (NullPointerException e) {
                                    Log.e(TAG, "isSystem = null");
                                }
                                if (isSystem) {
                                    //do nothing
                                } else if (packageManager.getLaunchIntentForPackage(packa) == null ||
                                        packa.contains("systemui") ||
                                        packa.equals(launcherPackageName) ||
                                        packa.contains("googlequicksearchbox") ||
                                        excludeSet.contains(packa) ||
                                        tempPackageName.contains(packa)
                                        ) {
                                    // do nothing
                                } else {
                                    tempPackageName.add(packa);
                                }

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
            if (tempPackageName.size()>=1) {
                lastAppPackageName = tempPackageName.get(1);
            }
            Log.e(TAG, "getRecentApp: time to get recent  = " + (System.currentTimeMillis() - timeStart));
            return tempPackageName;
        }
    }

    public ArrayList<String> getRecentApp2() {
        long timeStart = System.currentTimeMillis();

//            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
            long currentTimeMillis = System.currentTimeMillis() + 2000;
        Log.e(TAG, "getRecentApp2: start get stats = " + System.currentTimeMillis());

        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, currentTimeMillis - 1000 * 1000, currentTimeMillis);
        Log.e(TAG, "getRecentApp2: end get stats = " + System.currentTimeMillis());
        ArrayList<String> tempPackageName = new ArrayList<String>();
            if (stats != null) {
                Log.e(TAG, "getRecentApp2: start sorting = " + System.currentTimeMillis());
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>(Cons.DATE_DECENDING_COMPARATOR);
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                Log.e(TAG, "getRecentApp2: stop sorting = " + System.currentTimeMillis());
                Set<Long> setKey = mySortedMap.keySet();
                Log.e(TAG, "mySortedMap size   = " + mySortedMap.size());
                UsageStats usageStats;
                String packa;
//                PackageManager packageManager = getPackageManager();
                Log.e(TAG, "getRecentApp2: start get temp = " + System.currentTimeMillis());
                for (Long key : setKey) {
                    if (key <= currentTimeMillis) {
                        usageStats = mySortedMap.get(key);
                        if (usageStats != null) {
                            packa = usageStats.getPackageName();
                            Log.e(TAG, "getRecentApp2: start if = " + System.currentTimeMillis());
                            if (usageStats.getTotalTimeInForeground() > 500 &&
                                    !packa.contains("systemui") &&
                                    !packa.equals(launcherPackageName) &&
                                    !excludeSet.contains(packa) &&
                                    !tempPackageName.contains(packa)
                            ) {
                                if (!noIntentPackagesSet.contains(packa)) {
                                    tempPackageName.add(packa);
                                }
//                                if (packageManager.getLaunchIntentForPackage(packa) != null) {
//                                    tempPackageName.add(packa);
//                                }
                            }
                            Log.e(TAG, "getRecentApp2: stop if = " + System.currentTimeMillis());
                            if (tempPackageName.size() >= 6) {
                                Log.e(TAG, "tempackage >= "  + 6);
                                break;
                            }
                        }
                    } else {
                        Log.e(TAG, "key is in future");
                    }

                }
                Log.e(TAG, "getRecentApp2: stop get temp = " + System.currentTimeMillis());

            }
            if (tempPackageName.size()>=1) {
                lastAppPackageName = tempPackageName.get(1);
            }
        Log.e(TAG, "getRecentApp2: time to get recent  = " + (System.currentTimeMillis() - timeStart));
            return tempPackageName;

    }

    public void showCircleIconsView(Shortcut[] shortcuts) {
        for (int i = 0; i < 6; i++) {
            if (i >= shortcuts.length) {
                circleIcons[i].setImageDrawable(null);
            } else {
                Utility.setImageForShortcut(shortcuts[i], getPackageManager(), circleIcons[i], getApplicationContext(), iconPack, null, true);
            }
        }
        try {
            Log.e(TAG, "showCircleIconsView: add circle item to windowmanager");
            windowManager.addView(circleParentsView, circleShortcutsViewPara);
            Log.e(TAG, "showCircleIconsView: item1 x = " + circleParentsView.findViewById(R.id.item_1).getX());
        } catch (IllegalStateException e) {
            Log.e(TAG, " item_view has already been added to the window manager");
        }
    }

    public void showCircleFavorite() {
        Shortcut shortcut;
        for (int i = 0; i < 6; i++) {
            shortcut = circleRealm.where(Shortcut.class).equalTo("id", i).findFirst();
            if (shortcut != null) {
                Utility.setImageForShortcut(shortcut, getPackageManager(),circleIcons[i], getApplicationContext(), iconPack, circleRealm, true);
            } else {
                circleIcons[i].setImageResource(R.drawable.ic_add_circle_outline_white_48dp);
            }

        }
    }

    public Shortcut getCircleFavoriteShorcut(int position) {
        return circleRealm.where(Shortcut.class).equalTo("id", position).findFirst();
    }

    public void highlightCircleIcon(int iconId, int edgeId, float xInit, float yInit, float[] circleIconXs, float[] circleIconYs) {

        if (iconId > -1 && iconId < 10) {
            if (circleIcons[iconId].getX() == circleIconXs[iconId] && circleIcons[iconId].getY() == circleIconYs[iconId]) {
                int height = (int) ((16 + 48 * iconScale) * mScale);
                int width = (int) ((28 + 48 * iconScale) * mScale);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    circleIcons[iconId].setBackground(getDrawable(R.drawable.icon_background));
                } else {
                    circleIcons[iconId].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_background));
                }
                circleIcons[iconId].setX(circleIcons[iconId].getX() - 14 * mScale);
                circleIcons[iconId].setY(circleIcons[iconId].getY() - 8 * mScale);
                circleIcons[iconId].setLayoutParams(layoutParams);
                circleIcons[iconId].setPadding((int) (14 * mScale), (int) (8 * mScale), (int) (14 * mScale), (int) (8 * mScale));
            }
        } else if (iconId >= 10) {
            showQuickAction(edgeId, iconId - 10, xInit, yInit);
        }
    }

    public void highlightGridFavoriteIcon(int iconId) {
        if (iconId >= 0 && iconId < gridColumns * gridRows) {
            favoriteGridView.getChildAt(iconId).setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.icon_background_square));
        }
    }

    public void unhighlightGridFavoriteIcon(int iconId) {
        if (iconId >= 0 && iconId < gridColumns * gridRows) {
            favoriteGridView.getChildAt(iconId).setBackground(null);
        }
    }

    public void highlightGridFolderIcon(int iconId) {
        if (iconId >= 0 && iconId < folderAdapter.getCount()) {
            folderGridView.getChildAt(iconId).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_background_square));
        }
    }

    public void unhighlightGridFolderIcon(int iconId) {
        if (iconId >= 0 && iconId < folderAdapter.getCount()) {
            folderGridView.getChildAt(iconId).setBackground(null);
        }
    }

    public void unhighlightCircleIcon(int iconId, int edgeId, float[] circleIconXs, float[] circleIconYs) {
        if (iconId > -1 && iconId < 10) {
            if ((circleIcons[iconId].getX() == circleIconXs[iconId] - 14 * mScale)
                    && (circleIcons[iconId].getY() == circleIconYs[iconId] - 8 * mScale)) {

                ImageView iconResetBackground = circleIcons[iconId];
                FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(iconResetBackground.getLayoutParams());
                layoutParams1.width = (int) (48 * mScale * iconScale);
                layoutParams1.height = (int) (48 * mScale * iconScale);
                float x = iconResetBackground.getX();
                float y = iconResetBackground.getY();
                iconResetBackground.setBackground(null);
                iconResetBackground.setX(x + 14 * mScale);
                iconResetBackground.setY(y + 8 * mScale);
                iconResetBackground.setLayoutParams(layoutParams1);
                iconResetBackground.setPadding(0, 0, 0, 0);
            }
        } else if (iconId >= 10) {
            switch (edgeId) {
                case Cons.EDGE_1_ID:
                    edge1QuickActionViews[iconId - 10].setVisibility(View.GONE);
                    break;
                case Cons.EDGE_2_ID:
                    edge2QuickActionViews[iconId - 10].setVisibility(View.GONE);
                    break;
            }
        }
    }

    public synchronized void removeCircleShortcutsView() {
        try {
            windowManager.removeView(circleParentsView);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " cannot remove circleParentsView");
        }
    }

    public synchronized void removeGridParentsView() {
        try {
            windowManager.removeView(gridParentsView);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "removeGridParentsView: cannot remove gridParentsView");
        }
    }

    public final synchronized void addEdgeImage() {
        if (isEdge1On && edge1View !=null && !edge1View.isAttachedToWindow()) {
            try {
                windowManager.addView(edge1View,edge1Para);
            } catch (IllegalStateException e) {
                Log.e(TAG, "addEdgeImage: fail when add edge1Image");
            }

        }
        if (isEdge2On && edge2View !=null && !edge2View.isAttachedToWindow()) {
            try {
                windowManager.addView(edge2View,edge2Para);
            } catch (IllegalStateException e) {
                Log.e(TAG, "addEdgeImage: fail when add edge2Image");
            }

        }
    }

    public final synchronized void removeEdgeImage() {
        Log.e(TAG, "removeEdgeImage: ");
        try {
            windowManager.removeView(edge1View);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove edge1Image");
        }
        try {
            windowManager.removeView(edge2View);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove edge2Image");
        }
    }

    public final synchronized void removeAll() {
        Log.e(TAG, "remove all view");
        try {
            edge1View.setOnTouchListener(null);
            windowManager.removeView(edge1View);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove edge1Image");
        }
        try {
            edge2View.setOnTouchListener(null);
            windowManager.removeView(edge2View);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove edge2Image");
        }
        removeAllExceptEdgeView();
    }

    public final synchronized void removeAllExceptEdgeView() {
        Log.e(TAG, "removeAllExceptEdgeView");

        try {
            windowManager.removeView(backgroundFrame);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove background");
        }

        try {
            windowManager.removeView(clockParentsView);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove clockView");
        }
        try {
            windowManager.removeView(gridParentsView);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove shortcutView");
        }
        try {
            windowManager.removeView(circleParentsView);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove item1View");
        }

    }

    public void showFavoriteGridView(float xInit, float yInit, int edgePosition, int iconToSwitch) {
        Log.e(TAG, "showFavoriteGridView: height = " + favoriteGridView.getHeight() + "\nwidth = " + favoriteGridView.getWidth());

        Utility.setFavoriteGridViewPosition(favoriteGridView
                , gridHeight
                , gridWidth
                , xInit
                , yInit
                , mScale
                , edgePosition
                , windowManager
                , defaultShared
                , favoriteGridPaddingHorizontal
                , favoriteGridPaddingVertical
                , iconToSwitch);
        favoriteGridView.setVisibility(View.VISIBLE);
        favoriteGridView.setAlpha(1f);
        folderGridView.setVisibility(View.GONE);
        if (!gridParentsView.isAttachedToWindow()) {
            windowManager.addView(gridParentsView, gridParentViewPara);
        }
    }

    public void startFolderCircleAnimation(final int folderPosition) {
        folderAdapter.setFolderId(folderPosition);
        folderAnimator = favoriteGridView.animate().setDuration(holdTime).alpha(0f).setListener(new Animator.AnimatorListener() {
            boolean isCancel = false;
            CircleAngleAnimation angleAnimation;

            @Override
            public void onAnimationStart(Animator animation) {
                folderCircle.setVisibility(View.VISIBLE);
                folderCircle.setAngle(0);
                angleAnimation = new CircleAngleAnimation(folderCircle, 270);
                angleAnimation.setDuration(holdTime + 200);
                folderCircle.startAnimation(angleAnimation);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isCancel) {
                    folderCoor = showFolder( folderPosition);
                    Log.e(TAG, "onAnimation end");
                    folderCircle.setVisibility(View.GONE);
                    folderCircle.setAngle(0);
                    presenter.currentShowing = Cons.SHOWING_FOLDER;
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                favoriteGridView.setVisibility(View.VISIBLE);
                favoriteGridView.setAlpha(1f);
                folderGridView.setVisibility(View.GONE);
                isCancel = true;
                angleAnimation.cancel();
                folderCircle.setAngle(0);
                folderCircle.setVisibility(View.GONE);
                Log.e(TAG, "onAnimation cancel");

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public float[] showFolder(int mPosition) {

        float gridX = favoriteGridView.getX();
        float gridY = favoriteGridView.getY();
        float x = favoriteGridView.getChildAt(mPosition).getX()+ gridX;
        float y = favoriteGridView.getChildAt(mPosition).getY() + gridY;
        int size = (int) favoriteRealm.where(Shortcut.class).greaterThan("id",(mPosition+1)*1000 -1).lessThan("id",(mPosition+2)*1000).count();
        if (size == 0) {
            return new float[5];
        }
        int gridColumn = size;
        if (gridColumn > 4) {
            gridColumn = 4;
        }
        int gridRow;
        if ( size % gridColumn == 0) {
            gridRow = size/gridColumn;
        }else gridRow = size/gridColumn +1;
        int gridGap = 5;


        favoriteGridView.setVisibility(View.GONE);
        ViewGroup.LayoutParams gridParams = folderGridView.getLayoutParams();
        folderGridView.setVerticalSpacing((int) (gridGap * mScale));
        folderGridView.setNumColumns(gridColumn);
        folderGridView.setGravity(Gravity.CENTER);
        float gridWide = (int) (mScale * (((EdgeGestureService.GRID_ICON_SIZE * iconScale) + EdgeGestureService.GRID_2_PADDING) * gridColumn + gridGap * (gridColumn - 1)));
        float gridTall = (int) (mScale * (((EdgeGestureService.GRID_ICON_SIZE * iconScale) + EdgeGestureService.GRID_2_PADDING) * gridRow + gridGap * (gridRow - 1)));
        gridParams.height = (int) gridTall;
        gridParams.width = (int) gridWide;
        folderGridView.setLayoutParams(gridParams);
        folderGridView.setAdapter(folderAdapter);
        if (x - gridWide / 2 + gridWide > gridX + favoriteGridView.getWidth()) {
            folderGridView.setX(gridX + favoriteGridView.getWidth() - gridWide);
        } else if (x - gridWide / 2 < 10 * mScale) {
            folderGridView.setX(10*mScale);
        } else {
            folderGridView.setX(x - gridWide / 2);
        }

        folderGridView.setY(y - gridTall + gridTall/gridRow);
        Log.e(TAG,"gridX = " + gridX + "\nGridY = " + gridY +  "\nfolder x = " + folderGridView.getX() + "\nfolder y= " + folderGridView.getY() );
        folderGridView.setVisibility(View.VISIBLE);
        return new float[]{ folderGridView.getX(),  folderGridView.getY(), gridRow, gridColumn, mPosition};



    }

    public void closeFolder() {
        favoriteGridView.setVisibility(View.VISIBLE);
        favoriteGridView.setAlpha(1f);
        folderGridView.setVisibility(View.GONE);
    }

    public void showQuickAction(int edgeId, int id, float xInit, float yInit) {
        float x = xInit - circleSizePxl - CIRCLE_AND_QUICK_ACTION_GAP * mScale - OVAL_OFFSET * mScale - OVAL_RADIUS_PLUS * mScale;
        float y = yInit - circleSizePxl - CIRCLE_AND_QUICK_ACTION_GAP * mScale - OVAL_OFFSET * mScale - OVAL_RADIUS_PLUS * mScale;
        switch (edgeId) {
            case EDGE_1_ID:

                for (int i = 0; i < edge1QuickActionViews.length; i++) {
                    if (i == id) {
                        edge1QuickActionViews[i].setVisibility(View.VISIBLE);
                        edge1QuickActionViews[i].setX(x);
                        edge1QuickActionViews[i].setY(y);
                    } else {
                        edge1QuickActionViews[i].setVisibility(View.GONE);
                    }
                }
                break;
            case EDGE_2_ID:
                for (int i = 0; i < edge2QuickActionViews.length; i++) {
                    if (i == id) {
                        edge2QuickActionViews[i].setVisibility(View.VISIBLE);
                        edge2QuickActionViews[i].setX(x);
                        edge2QuickActionViews[i].setY(y);
                    } else {
                        edge2QuickActionViews[i].setVisibility(View.GONE);
                    }
                }
                break;

        }
    }

    public void hideAllQuickAction(int edgeId) {
        switch (edgeId) {
            case EDGE_1_ID:
                for (ExpandStatusBarView edge1QuickActionView : edge1QuickActionViews) {
                    edge1QuickActionView.setVisibility(View.GONE);
                }
                break;
            case EDGE_2_ID:
                for (ExpandStatusBarView edge2QuickActionView : edge2QuickActionViews) {
                    edge2QuickActionView.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void inject() {
        DaggerEdgeServiceComponent.builder()
                .appModule(new AppModule(getApplicationContext()))
                .edgeServiceModule(new EdgeServiceModule(this))
                .realmModule(new RealmModule(getApplicationContext()))
                .build()
                .inject(this);

    }

    public void setCircleIconsPosition(float[] circleIconXs, float[] circleIconYs, float xInit, float yInit) {
        for (int i = 0; i < circleIcons.length; i++) {
            if (useAnimation) {
                if (i > 0) {
                    circleIcons[i].setX(circleIconXs[0]);
                    circleIcons[i].setY(circleIconYs[0]);
                    circleIcons[i].setAlpha(0f);
                    circleIcons[i].animate().x(circleIconXs[i]).y(circleIconYs[i]).setStartDelay(animationTime / (6 - i)).setDuration(animationTime).alpha(1f).setInterpolator(new FastOutSlowInInterpolator());
                } else if (i == 0) {
                    circleIcons[i].setX(xInit - iconSizePxl / 2);
                    circleIcons[i].setY(yInit - iconSizePxl / 2);
                    circleIcons[i].setAlpha(0f);
                    circleIcons[i].animate().x(circleIconXs[i]).y(circleIconYs[i]).setDuration(animationTime / 2).alpha(1f).setInterpolator(new FastOutSlowInInterpolator());
                }
            } else {
                circleIcons[i].setX(circleIconXs[i]);
                circleIcons[i].setY(circleIconYs[i]);
            }

        }
    }

    public void showClock() {
        indicator.setVisibility(View.GONE);
        if (useClock) {
            Calendar c = Calendar.getInstance();
            int mHour;
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMMM");
            clock.setVisibility(View.VISIBLE);
            TextView hourTextView = (TextView) clockParentsView.findViewById(R.id.clock_time_in_hour);
            TextView dateTextView = (TextView) clockParentsView.findViewById(R.id.clock_time_in_date);
            TextView batteryLifeTextView = (TextView) clockParentsView.findViewById(R.id.clock_battery_life);
            String batteryString = getApplicationContext().getString(R.string.batterylife) + " " + Utility.getBatteryLevel(getApplicationContext()) + "%";
            if (batteryLifeTextView != null) {
                batteryLifeTextView.setText(batteryString);
            }
            if (dateTextView != null) {
                dateTextView.setText(dateFormat.format(c.getTime()));
            }
            if (!DateFormat.is24HourFormat(getApplicationContext())) {
                SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm");
                if (hourTextView != null) {
                    hourTextView.setText(hourFormat.format(c.getTime()));
                }
            } else {
                SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
                if (hourTextView != null) {
                    hourTextView.setText(hourFormat.format(c.getTime()));
                }
            }
        }
        windowManager.addView(clockParentsView, clockParentsPara);
    }

    public void showBackground() {
        if (useAnimation) {
            backgroundFrame.setAlpha(0f);
            windowManager.addView(backgroundFrame, backgroundPara);
            backgroundFrame.animate().alpha(1f).setDuration(animationTime).setInterpolator(new FastOutSlowInInterpolator());
        } else {
            Log.e(TAG, "showBackground: ");
            backgroundFrame.setAlpha(1f);
            windowManager.addView(backgroundFrame, backgroundPara);
        }
    }

    public void vibrate() {
        vibrator.vibrate(vibrationDuration);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        this.unregisterReceiver(receiver);
        removeAll();
        presenter.onDestroy();
        super.onDestroy();
    }

    void clear() {
        if (favoriteRealm != null) {
            favoriteRealm.close();
        }
        if (circleRealm != null) {
            circleRealm.close();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (defaultShared.getBoolean(EdgeSetting.IS_DISABLE_IN_LANSCAPE, false) && newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            removeEdgeImage();
        } else {
            addEdgeImage();
        }
        Log.e(TAG, "onConfigurationChanged: ");
        super.onConfigurationChanged(newConfig);
        removeAllExceptEdgeView();

    }

    public void setupReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Cons.ACTION_TOGGLE_EDGES);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        receiver = new EdgesToggleReceiver();
        this.registerReceiver(receiver, filter);
    }

    public void setupNotification() {
        Intent hideNotiIntent = new Intent();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            hideNotiIntent.setClassName("com.android.settings", "com.android.settings.Settings$AppNotificationSettingsActivity");
            hideNotiIntent.putExtra("app_package", getPackageName());
            hideNotiIntent.putExtra("app_uid", getApplicationInfo().uid);
        } else {
            hideNotiIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            hideNotiIntent.addCategory(Intent.CATEGORY_DEFAULT);
            hideNotiIntent.setData(Uri.parse("package:" + getPackageName()));
        }

        Intent remoteIntent = new Intent();
        remoteIntent.setAction(Cons.ACTION_TOGGLE_EDGES);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, remoteIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Action remoteAction=
                new NotificationCompat.Action.Builder(
                        android.R.drawable.ic_media_pause,
                        getString(R.string.pause),
                        pendingIntent).build();

        PendingIntent notiPending = PendingIntent.getActivity(getApplicationContext(), 0, hideNotiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.ic_stat_ic_looks_white_48dp1)
                .setContentIntent(notiPending)
                .addAction(remoteAction)
                .setPriority(Notification.PRIORITY_MIN)
                .setContentText(getString(R.string.notification_text)).setContentTitle(getString(R.string.notification_title));
        Notification notificationCompat = notificationBuilder.build();
        startForeground(Cons.NOTIFICATION_ID, notificationCompat);
    }

    public void setIndicator(Shortcut shortcut, boolean forQuickAction, int quickActionId) {
        if (forQuickAction) {
            Utility.setIndicatorForQuickAction(defaultShared, getApplicationContext(), quickActionId + 1, ((ImageView) indicator.findViewById(R.id.indicator_icon))
                    , (TextView) indicator.findViewById(R.id.indicator_label));
        } else {
            clock.setVisibility(View.GONE);
            indicator.setVisibility(View.VISIBLE);
            if (shortcut != null) {
                Utility.setImageForShortcut(shortcut, getPackageManager()
                        , (ImageView) indicator.findViewById(R.id.indicator_icon)
                        , getApplicationContext(), iconPack, null, true);
                if (shortcut.getLabel() == null || shortcut.getLabel().equals("") && shortcut.getType() == Shortcut.TYPE_APP) {
                    ((TextView) indicator.findViewById(R.id.indicator_label)).setText(Utility.getLabelForShortcut(getApplicationContext(), shortcut));
                } else {
                    ((TextView) indicator.findViewById(R.id.indicator_label)).setText(shortcut.getLabel());
                }
            } else {
                ((ImageView) indicator.findViewById(R.id.indicator_icon)).setImageDrawable(null);
                ((TextView) indicator.findViewById(R.id.indicator_label)).setText("");
            }
        }
    }

    public void executeQuickAction(int actionId, View v) {
        String action = MainActivity.ACTION_NONE;
        switch (actionId) {
            case 0:
                action = defaultShared.getString(EdgeSetting.ACTION_1_KEY, MainActivity.ACTION_INSTANT_FAVO);
                break;
            case 1:
                action = defaultShared.getString(EdgeSetting.ACTION_2_KEY, MainActivity.ACTION_HOME);
                break;
            case 2:
                action = defaultShared.getString(EdgeSetting.ACTION_3_KEY, MainActivity.ACTION_BACK);
                break;
            case 3:
                action = defaultShared.getString(EdgeSetting.ACTION_4_KEY, MainActivity.ACTION_NOTI);
                break;
        }
        if (action.equals(MainActivity.ACTION_NOTI) & isFreeAndOutOfTrial) {
            Utility.startNotiDialog(getApplicationContext(), NotiDialog.OUT_OF_TRIAL);
        } else {
            Log.e(TAG, "executeQuickAction: lastapp = " + lastAppPackageName);

            startQuickAction(getApplicationContext(), action, v, getClass().getName(), getPackageName(), lastAppPackageName);
        }
    }

    public void executeShortcut(Shortcut shortcut, View v, int mode) {
        if (shortcut != null) {
            startShortcut(getApplicationContext(), shortcut, v, getClass().getName(), getPackageName(), lastAppPackageName, defaultShared.getInt(EdgeSetting.CONTACT_ACTION, 0), FLASH_LIGHT_ON);
        } else if (mode != -1) {
            showFavoriteSettingActivity(mode);
        }
    }

    public void showFavoriteSettingActivity(int mode) {
        Intent intent = new Intent(getApplicationContext(),FavoriteSettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("mode", mode);
        startActivity(intent);
    }

    private void startShortcut(Context context, Shortcut shortcut, View v, String className, String packageName, String lastAppPackageName, int contactAction, boolean flashLightOn) {
        {
            if (shortcut.getType() == Shortcut.TYPE_APP) {
                Intent extApp;
                extApp =context.getPackageManager().getLaunchIntentForPackage(shortcut.getPackageName());
                if (extApp != null) {
                    if (shortcut.getPackageName().equals("com.devhomc.search")) {
                        extApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(extApp);
                    } else {
                        ComponentName componentName = extApp.getComponent();
                        Intent startAppIntent = new Intent(Intent.ACTION_MAIN);
                        startAppIntent.setComponent(componentName);
                        startAppIntent.addFlags(1064960);
                        startAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startAppIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startAppIntent.setFlags(270532608 | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startAppIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        context.startActivity(startAppIntent);
//                                    startActivity(extApp);
                    }

                } else {
                    Log.e(TAG, "extApp of shortcut = null ");
                }
            } else if (shortcut.getType() == Shortcut.TYPE_ACTION) {
                switch (shortcut.getAction()) {
                    case Shortcut.ACTION_WIFI:
                        Utility.toggleWifi(context);
                        break;
                    case Shortcut.ACTION_BLUETOOTH:
                        Utility.toggleBluetooth(context);
                        break;
                    case Shortcut.ACTION_ROTATION:
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
                    case Shortcut.ACTION_POWER_MENU:
                        Utility.powerAction(context, v, className, packageName);
                        break;
                    case Shortcut.ACTION_HOME:
                        Utility.homeAction(context, v, className, packageName);
                        break;
                    case Shortcut.ACTION_BACK:
                        Utility.backAction(context, v, className, packageName);
                        break;
                    case Shortcut.ACTION_NOTI:
                        Utility.notiAction(context, v, className, packageName);
                        break;
                    case Shortcut.ACTION_LAST_APP:
                        Utility.lastAppAction(context, lastAppPackageName);
                        break;
                    case Shortcut.ACTION_CALL_LOGS:
                        Utility.callLogsAction(context);
                        break;
                    case Shortcut.ACTION_DIAL:
                        Log.e(TAG, "startShortcut: Start dial");
                        Utility.dialAction(context);
                        break;
                    case Shortcut.ACTION_CONTACT:
                        Utility.contactAction(context);
                        break;
                    case Shortcut.ACTION_RECENT:
                        Utility.recentAction(context, v, className, packageName);
                        break;
                    case Shortcut.ACTION_VOLUME:
                        Utility.volumeAction(context);
                        break;
                    case Shortcut.ACTION_BRIGHTNESS:
                        Utility.brightnessAction(context);
                        break;
                    case Shortcut.ACTION_RINGER_MODE:
                        Utility.setRinggerMode(context);
                        break;
                    case Shortcut.ACTION_FLASH_LIGHT:
                        Utility.flashLightAction2(context,!flashLightOn);
                        break;
                    case Shortcut.ACTION_SCREEN_LOCK:
                        Utility.screenLockAction(context);
                        break;
                    case Shortcut.ACTION_NONE:
                        break;

                }
            } else if (shortcut.getType() == Shortcut.TYPE_CONTACT) {
                switch (contactAction) {
                    case EdgeSetting.ACTION_CHOOSE:
                        Intent intent = new Intent(context, ChooseActionDialogActivity.class);
                        intent.putExtra("number", shortcut.getNumber());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        context.startActivity(intent);
                        break;
                    case EdgeSetting.ACTION_CALL:
                        String url = "tel:"+ shortcut.getNumber();
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(callIntent);
                        } else {
                            Toast.makeText(context, context.getString(R.string.missing_call_phone_permission), Toast.LENGTH_LONG).show();
                        }
                        break;
                    case EdgeSetting.ACTION_SMS:
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                                + shortcut.getNumber()));
                        smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(smsIntent);
                        break;
                }

            } else if (shortcut.getType() == Shortcut.TYPE_SHORTCUT) {
                try {
                    Intent intent = Intent.parseUri(shortcut.getIntent(), 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "startShortcut: exception when start Shortcut shortcut");
                }

            }
        }
    }

    public class EdgesToggleReceiver extends BroadcastReceiver {
        public EdgesToggleReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Cons.ACTION_TOGGLE_EDGES)) {
                Log.e(TAG, "onReceive: receive broadbast success");
                Intent remoteIntent = new Intent();
                remoteIntent.setAction(Cons.ACTION_TOGGLE_EDGES);
                NotificationCompat.Action remoteAction;

                PendingIntent pendingIntent = PendingIntent.getBroadcast(EdgeServiceView.this, 0, remoteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (working) {
                    removeEdgeImage();
                    working = !working;


                    remoteAction=
                            new NotificationCompat.Action.Builder(
                                    android.R.drawable.ic_media_play,
                                    getString(R.string.resume),
                                    pendingIntent).build();
                } else {
                    addEdgeImage();
                    working = !working;


                    remoteAction=
                            new NotificationCompat.Action.Builder(
                                    android.R.drawable.ic_media_pause,
                                    getString(R.string.pause),
                                    pendingIntent).build();
                }

                notificationBuilder.mActions = new ArrayList<>();
                notificationBuilder.addAction(remoteAction);
                startForeground(Cons.NOTIFICATION_ID,notificationBuilder.build());

            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                Log.e(TAG, "onReceive: userPresent");
                removeAllExceptEdgeView();
            }
        }
    }
}
