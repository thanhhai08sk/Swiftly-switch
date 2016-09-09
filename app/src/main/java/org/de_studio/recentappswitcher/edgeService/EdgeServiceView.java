package org.de_studio.recentappswitcher.edgeService;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerEdgeServiceComponent;
import org.de_studio.recentappswitcher.dagger.EdgeServiceModule;
import org.de_studio.recentappswitcher.dagger.RealmModule;
import org.de_studio.recentappswitcher.favoriteShortcut.CircleFavoriteAdapter;
import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;
import org.de_studio.recentappswitcher.service.EdgeSetting;
import org.de_studio.recentappswitcher.service.ExpandStatusBarView;
import org.de_studio.recentappswitcher.service.FavoriteShortcutAdapter;
import org.de_studio.recentappswitcher.service.FolderAdapter;
import org.de_studio.recentappswitcher.service.MyImageView;

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
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_PADDING_HORIZONTAL_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_PADDING_VERTICAL_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_REALM_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.FOLDER_GRID_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_HEIGHT_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_WIDTH_NAME;
import static org.de_studio.recentappswitcher.Cons.HOLD_TIME_ENABLE_NAME;
import static org.de_studio.recentappswitcher.Cons.HOLD_TIME_NAME;
import static org.de_studio.recentappswitcher.Cons.ICON_SCALE_NAME;
import static org.de_studio.recentappswitcher.Cons.ICON_SIZE_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.IS_EDGE_1_ON_NAME;
import static org.de_studio.recentappswitcher.Cons.IS_EDGE_2_ON_NAME;
import static org.de_studio.recentappswitcher.Cons.LAUNCHER_PACKAGENAME_NAME;
import static org.de_studio.recentappswitcher.Cons.M_SCALE_NAME;
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
    FavoriteShortcutAdapter gridShortcutsAdapter;
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
    boolean holdTimeEnable;
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




    @Override
    public void onCreate() {
        super.onCreate();
        inject();
        presenter.onCreate();
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
                Log.e(TAG, "onTouch: action move");
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "onTouch: action up");
                presenter.onActionUp(getXCord(event), getYCord(event), view.getId());
                break;
            case MotionEvent.ACTION_OUTSIDE:
                Log.e(TAG, "onTouch: action outside");
                break;
            case MotionEvent.ACTION_CANCEL:
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
                lastAppPackageName = tempPackageNameKK.get(0);
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
            if (tempPackageName.size()>=1) {
                lastAppPackageName = tempPackageName.get(0);
            }
            return tempPackageName;
        }
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

    public void highlightCircleIcon(int iconId) {


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
        circleIcons[iconId].setPadding((int) (14 * mScale),(int)(8 * mScale) ,(int) (14 * mScale) ,(int)(8 * mScale) );
    }

    public void unhighlightCircleIcon(int iconId) {
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
            Log.e(TAG, " Null when remove backgroundFrame");
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

    public void showFolderGridView(int folderPosition) {
        FolderAdapter folderAdapter = new FolderAdapter(getApplicationContext(), folderPosition);
        Utility.showFolder(favoriteGridView, favoriteRealm, folderPosition, mScale, iconScale, folderAdapter);
    }

    public void showQuickAction(int edgeId, int id, int xInit, int yInit) {
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

    public void setCircleIconsPosition(float[] circleIconXs, float[] circleIconYs) {
        for (int i = 0; i < circleIcons.length; i++) {
            circleIcons[i].setX(circleIconXs[i]);
            circleIcons[i].setY(circleIconYs[i]);
        }
    }

    public void showClock() {
        LinearLayout clock = (LinearLayout) clockParentsView.findViewById(R.id.clock_linear_layout);
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
        windowManager.addView(clockParentsView, clockParentsPara);
    }

    public void showBackground() {
        if (useAnimation) {
            backgroundFrame.setAlpha(0f);
            windowManager.addView(backgroundFrame, backgroundPara);
            backgroundFrame.animate().alpha(1f).setDuration(defaultShared.getInt(EdgeSetting.ANI_TIME_KEY, 100)).setInterpolator(new FastOutSlowInInterpolator());
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
        presenter.onDestroy();
        super.onDestroy();
    }
}
