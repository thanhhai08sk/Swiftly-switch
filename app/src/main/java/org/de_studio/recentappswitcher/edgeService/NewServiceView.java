package org.de_studio.recentappswitcher.edgeService;

import android.animation.ObjectAnimator;
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
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.ServiceSlotAdapter;
import org.de_studio.recentappswitcher.base.adapter.ServiceItemsAdapter;
import org.de_studio.recentappswitcher.dadaSetup.DataSetupService;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerNewServiceComponent;
import org.de_studio.recentappswitcher.dagger.NewServiceModule;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.DataInfo;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;
import org.de_studio.recentappswitcher.service.Circle;
import org.de_studio.recentappswitcher.service.CircleAngleAnimation;
import org.de_studio.recentappswitcher.service.EdgeSetting;
import org.de_studio.recentappswitcher.service.NotiDialog;
import org.de_studio.recentappswitcher.ui.QuickActionsView;
import org.de_studio.recentappswitcher.utils.GridSpacingItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import io.realm.Realm;
import io.realm.RealmList;

import static org.de_studio.recentappswitcher.Cons.ANIMATION_TIME_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.EXCLUDE_SET_NAME;
import static org.de_studio.recentappswitcher.Cons.HOLD_TIME_NAME;
import static org.de_studio.recentappswitcher.Cons.ICON_SCALE_NAME;
import static org.de_studio.recentappswitcher.Cons.LAUNCHER_PACKAGENAME_NAME;
import static org.de_studio.recentappswitcher.Cons.M_SCALE_NAME;
import static org.de_studio.recentappswitcher.Cons.SHARED_PREFERENCE_NAME;

/**
 * Created by HaiNguyen on 12/23/16.
 */

public class NewServiceView extends Service implements NewServicePresenter.View {
    private static final String TAG = NewServiceView.class.getSimpleName();
    public static boolean FLASH_LIGHT_ON = false;
    static final int WINDOW_FLAG_NO_TOUCH = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
    static final int WINDOW_FLAG_TOUCHABLE =
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;

    WindowManager.LayoutParams WINDOW_PARAMS_NO_TOUCH =new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WINDOW_FLAG_NO_TOUCH,
            PixelFormat.TRANSLUCENT);
    WindowManager.LayoutParams WINDOW_PARAMS_TOUCHABLE =new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WINDOW_FLAG_TOUCHABLE,
            PixelFormat.TRANSLUCENT);



    @Nullable
    @Inject
    IconPackManager.IconPack iconPack;
    @Inject
    @Named(Cons.GRID_WINDOW_PARAMS_NAME)
    WindowManager.LayoutParams gridParams;
    @Inject
    @Named(Cons.CLOCK_PARENTS_VIEW_NAME)
    FrameLayout backgroundView;
    @Inject
    @Named(EDGE_1_VIEW_NAME)
    View edge1View;
    @Inject
    @Named(EDGE_2_VIEW_NAME)
    View edge2View;
    @Inject
    @Named(LAUNCHER_PACKAGENAME_NAME)
    String launcherPackageName;
    @Nullable
    @Inject
    @Named(EXCLUDE_SET_NAME)
    RealmList<Item> excludeSet;
    @Inject
    @Named(EDGE_1_PARA_NAME)
    WindowManager.LayoutParams edge1Para;
    @Inject
    @Named(EDGE_2_PARA_NAME)
    WindowManager.LayoutParams edge2Para;
    @Inject
    WindowManager windowManager;
    @Inject
    @Named(M_SCALE_NAME)
    float mScale;
    @Inject
    @Named(HOLD_TIME_NAME)
    int holdTime;
    @Inject
    @Named(ICON_SCALE_NAME)
    float iconScale;
    @Inject
    @Named(ANIMATION_TIME_NAME)
    int animationTime;
    @Inject
    @Named(Cons.BACKGROUND_COLOR_NAME)
    int backgroundColor;
    @Named(SHARED_PREFERENCE_NAME)
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    NewServicePresenter presenter;
    HashMap<String, View> collectionViewsMap = new HashMap<>();
    UsageStatsManager usageStatsManager;
    NewServiceView.EdgesToggleReceiver receiver;
    boolean working = true;
    private NotificationCompat.Builder notificationBuilder;
    Realm realm = Realm.getDefaultInstance();
    GenerateDataOkReceiver generateDataOkReceiver;

    ObjectAnimator gridAlphaAnimator;

    private GestureDetectorCompat mDetector;

    @Override
    public void onCreate() {
        super.onCreate();
        DataInfo dataInfo = realm.where(DataInfo.class).findFirst();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        }
        if (dataInfo == null || !dataInfo.everyThingsOk()) {
            Log.e(TAG, "onCreate: start DataSetupService");
            IntentFilter filter = new IntentFilter();
            filter.addAction(DataSetupService.BROADCAST_GENERATE_DATA_OK);
            generateDataOkReceiver = new GenerateDataOkReceiver();
//            this.registerReceiver(receiver, filter);
            this.registerReceiver(generateDataOkReceiver, filter);
            Intent intent = new Intent(this, DataSetupService.class);
            intent.setAction(DataSetupService.ACTION_GENERATE_DATA);
            startService(intent);
        } else {
            inject();
            presenter.onViewAttach(this);
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        if (presenter != null) {
            presenter.onViewDetach();
        }
        super.onDestroy();
    }

    private void inject() {
        Log.e(TAG, "inject: ");
        DaggerNewServiceComponent.builder()
                .appModule(new AppModule(this))
                .newServiceModule(new NewServiceModule(this, this, realm))
                .build().inject(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void clear() {
        collectionViewsMap = null;
        usageStatsManager = null;
        notificationBuilder = null;
        windowManager = null;
        sharedPreferences = null;
        iconPack = null;
        if (generateDataOkReceiver != null) {
            this.unregisterReceiver(generateDataOkReceiver);
            generateDataOkReceiver = null;
        }

        if (receiver != null) {
            this.unregisterReceiver(receiver);
            receiver = null;
        }
        realm.close();

    }

    @Override
    public void addEdgesToWindowAndSetListener() {
        Log.e(TAG, "addEdgesToWindowAndSetListener: ");
        if (!(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && sharedPreferences.getBoolean(EdgeSetting.IS_DISABLE_IN_LANSCAPE, false))) {
            if (sharedPreferences.getBoolean(Cons.EDGE_1_ON_KEY, true)) {
                try {
                    windowManager.addView(edge1View, edge1Para);
                    edge1View.setOnTouchListener(null);
                    edge1View.setOnTouchListener(this);
                } catch (Exception e) {
                    Utility.startNotiDialog(getApplicationContext(), NotiDialog.DRAW_OVER_OTHER_APP);
                }
            }
            if (sharedPreferences.getBoolean(Cons.EDGE_2_ON_KEY, false)) {
                try {
                    windowManager.addView(edge2View, edge2Para);
                    edge2View.setOnTouchListener(null);
                    edge2View.setOnTouchListener(this);
                } catch (Exception e) {
                    Utility.startNotiDialog(getApplicationContext(), NotiDialog.DRAW_OVER_OTHER_APP);
                }
            }
        }
    }

    @Override
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


        NotificationCompat.Action remoteAction =
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

    @Override
    public void setupReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Cons.ACTION_TOGGLE_EDGES);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Cons.ACTION_REFRESH_FAVORITE);
        receiver = new NewServiceView.EdgesToggleReceiver();
        this.registerReceiver(receiver, filter);
    }

    @Override
    public Point getWindowSize() {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point;
    }

    @Override
    public ArrayList<String> getRecentApp(long timeInterval) {
        Log.e(TAG, "getRecentApp: launcher = " + launcherPackageName);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            int numOfTask = 13;

            List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(numOfTask);
            ArrayList<String> tempPackageNameKK = new ArrayList<String>();
            for (int i = 0; i < list.size(); i++) {
                ActivityManager.RunningTaskInfo taskInfo = list.get(i);
                ComponentName componentName = taskInfo.baseActivity;
                String packName = componentName.getPackageName();
                if ((excludeSet == null || excludeSet.where().equalTo(Cons.PACKAGENAME, packName).findFirst() == null) && !packName.contains("systemui")) {
                    tempPackageNameKK.add(packName);
                }
            }
            return tempPackageNameKK;
        } else {
            long timeStart = System.currentTimeMillis();
            long currentTimeMillis = System.currentTimeMillis() + 2000;

            List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, currentTimeMillis - timeInterval, currentTimeMillis);
            ArrayList<String> tempPackageName = new ArrayList<String>();
            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>(Cons.DATE_DECENDING_COMPARATOR);
                for (UsageStats usageStats : stats) {
                    long lastTimeUsed = usageStats.getLastTimeUsed();
                    if (mySortedMap.containsKey(lastTimeUsed)) {
                        lastTimeUsed = lastTimeUsed - 1;
                    }
                    mySortedMap.put(lastTimeUsed, usageStats);
                }
                Set<Long> setKey = mySortedMap.keySet();
                Log.e(TAG, "mySortedMap size   = " + mySortedMap.size());
                UsageStats usageStats;
                String packa;
                int i = 0;
                for (Long key : setKey) {
                    if (key <= currentTimeMillis) {
                        usageStats = mySortedMap.get(key);
                        if (usageStats != null) {
                            packa = usageStats.getPackageName();
                            if (packa != null &&
                                    (!packa.contains("systemui")
                                            && (i == 0 || (excludeSet == null || excludeSet.where().equalTo(Cons.PACKAGENAME, packa).findFirst() == null))
                                            && !tempPackageName.contains(packa))
                                    ) {

                                tempPackageName.add(packa);
                                i++;
                            } else {
                                Log.e(TAG, "getRecentApp: removed package = " + packa + "\ni = " + i +
                                        "\nfirst && = " + (usageStats.getTotalTimeInForeground() > 500) + "value = " + usageStats.getTotalTimeInForeground() +
                                        "\nsecond && = " + (!packa.contains("systemui")) +
                                        "\nthird && = " + ((i == 0 || (excludeSet == null || excludeSet.where().equalTo(Cons.PACKAGENAME, packa).findFirst() == null))) +
                                        "\nfourth && = " + !tempPackageName.contains(packa));

                            }
                            if (tempPackageName.size() >= 10) {
                                Log.e(TAG, "tempackage >= " + 10);
                                break;
                            }
                        }
                    }
                }
            }
            Log.e(TAG, "getRecentApp: time to get recent  = " + (System.currentTimeMillis() - timeStart));
            Log.e(TAG, "getRecentApp: tem size = " + tempPackageName.size());
            return tempPackageName;

        }

    }

    @Override
    public void showBackground(boolean backgroundTouchable) {
        if (!backgroundView.isAttachedToWindow()) {
            windowManager.addView(backgroundView, backgroundTouchable ? WINDOW_PARAMS_TOUCHABLE : WINDOW_PARAMS_NO_TOUCH);
            mDetector = new GestureDetectorCompat(this,this);
            backgroundView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mDetector.onTouchEvent(event);
                    return true;
                }
            });
        } else {
            backgroundView.setVisibility(View.VISIBLE);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            backgroundView.setBackgroundColor(backgroundColor);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(backgroundView, "alpha", 0f, 1f);
            objectAnimator.setDuration(animationTime).start();

        } else {
            ObjectAnimator objectAnimator = ObjectAnimator.ofArgb(backgroundView, "backgroundColor", Color.argb(0, 0, 0, 0), backgroundColor);
            objectAnimator.setDuration(animationTime);
            objectAnimator.start();
        }
    }

    public ImageView getIconImageView() {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams((int) (Cons.DEFAULT_ICON_WIDTH * mScale * iconScale), (int) (Cons.DEFAULT_ICON_WIDTH * mScale * iconScale)));
        imageView.setId(R.id.item_icon);
        return imageView;
    }

    public void showQuickActions(int edgePosition, int actionPosition, NewServicePresenter.Showing currentShowing, boolean delay, boolean animate) {
        Log.e(TAG, "showQuickActions: ");
        if (currentShowing.action != null) {
            if (collectionViewsMap.get(getQuickActionsKey(edgePosition, currentShowing.action)) == null) {
                QuickActionsView actionsView = new QuickActionsView(this, iconPack, currentShowing.action.slots, edgePosition, currentShowing.action.visibilityOption != Collection.VISIBILITY_OPTION_ONLY_TRIGGERED_ONE_VISIBLE);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (currentShowing.circle.radius * 2 * mScale + 60 * 2 * mScale), (int) (currentShowing.circle.radius * 2 * mScale + 60 * 2 * mScale));
                actionsView.setLayoutParams(layoutParams);
                actionsView.setId(getQuickActionsResId(currentShowing.action, edgePosition));
                collectionViewsMap.put(getQuickActionsKey(edgePosition, currentShowing.action), actionsView);
            }
            QuickActionsView quickActionsView = (QuickActionsView) collectionViewsMap.get(getQuickActionsKey(edgePosition, currentShowing.action));
            if (backgroundView.findViewById(getQuickActionsResId(currentShowing.action, edgePosition)) == null) {
                backgroundView.addView(quickActionsView);
            }


            quickActionsView.setVisibility(View.VISIBLE);
            quickActionsView.show(actionPosition);
            quickActionsView.setX(currentShowing.xInit - (currentShowing.circle.radius * 2 * mScale + 56 * 2 * mScale) / 2);
            quickActionsView.setY(currentShowing.yInit - (currentShowing.circle.radius * 2 * mScale + 56 * 2 * mScale) / 2);
//            if (animate) {
//                quickActionsView.setAlpha(0f);
//                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(quickActionsView, "alpha", 0f, 1f);
//                objectAnimator.setDuration(animationTime);
//                objectAnimator.setInterpolator(new DecelerateInterpolator());
//                if (delay) {
//                    objectAnimator.setStartDelay(animationTime);
//                    objectAnimator.start();
//                } else {
//                    objectAnimator.start();
//                }
//
//            } else {
//                quickActionsView.setAlpha(1f);
//            }

        }
    }

    @NonNull
    private String getQuickActionsKey(int edgePosition, Collection quickAction) {
        return quickAction.collectionId + String.valueOf(edgePosition);
    }

    public void hideQuickActions(NewServicePresenter.Showing currentShowing) {
        Log.e(TAG, "hideQuickActions: ");
        if (collectionViewsMap.get(getQuickActionsKey(currentShowing.edgePosition, currentShowing.action)) != null) {
            collectionViewsMap.get(getQuickActionsKey(currentShowing.edgePosition, currentShowing.action)).setVisibility(View.GONE);
        }
    }

    @Override
    public void showGrid(final float xInit, final float yInit, final Collection grid, final int position, final NewServicePresenter.Showing currentShowing) {
        if (grid != null) {
            Log.e(TAG, "showGrid: label " + grid.label);
        } else {
            Log.e(TAG, "showGrid: grid null");
            return;
        }
        if (collectionViewsMap.get(grid.collectionId) == null) {
            RecyclerView gridView = new RecyclerView(this);
            gridView.setItemAnimator(null);
            gridView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ServiceSlotAdapter adapter = new ServiceSlotAdapter(this, grid.slots, true, iconPack, mScale, iconScale);
            gridView.setLayoutManager(new GridLayoutManager(this, grid.columnCount));
            gridView.setAdapter(adapter);
            gridView.setId(getCollectionResId(grid));
            gridView.addItemDecoration(new GridSpacingItemDecoration((int) (grid.space * mScale)));
            collectionViewsMap.put(grid.collectionId, gridView);
        }
        final RecyclerView recyclerView = (RecyclerView) collectionViewsMap.get(grid.collectionId);
        boolean needDelay = false;
        if (backgroundView.findViewById(getCollectionResId(grid)) == null) {
            recyclerView.setClickable(false);
            recyclerView.setFocusable(false);
            backgroundView.addView(recyclerView);
            needDelay = true;
        }
        hideAllCollections();

        if (needDelay) {
            Log.e(TAG, "showGrid: with delay");
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAlpha(0f);
            Handler handlerClose = new Handler();
            handlerClose.postDelayed(new Runnable() {
                public void run() {
                    Utility.setFavoriteGridViewPosition(recyclerView
                            , grid.position == Collection.POSITION_CENTER
                            , recyclerView.getHeight()
                            , recyclerView.getWidth()
                            , xInit, yInit
                            , mScale
                            , position
                            , windowManager
                            , grid.marginHorizontal
                            , grid.marginVertical);
                    currentShowing.gridXY.x = (int) recyclerView.getX();
                    currentShowing.gridXY.y = (int) recyclerView.getY();
                    recyclerView.setAlpha(1f);
                    ((ServiceSlotAdapter) recyclerView.getAdapter()).updateIconsState();
                }
            }, 20);
        } else {
            Log.e(TAG, "showGrid: without delay");
            Utility.setFavoriteGridViewPosition(recyclerView
                    , grid.position == Collection.POSITION_CENTER
                    , recyclerView.getHeight()
                    , recyclerView.getWidth()
                    , xInit, yInit
                    , mScale
                    , position
                    , windowManager
                    , grid.marginHorizontal
                    , grid.marginVertical);
            recyclerView.setVisibility(View.VISIBLE);
            ((ServiceSlotAdapter) recyclerView.getAdapter()).updateIconsState();
            View child;
            float childCenterX;
            float childCenterY;
            float halfIcon = 24 * mScale;
            float centerX = recyclerView.getWidth()/2;
            float centerY = recyclerView.getHeight()/2;
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                child = recyclerView.getChildAt(i);
                childCenterX = child.getX() + halfIcon;
                childCenterY = child.getY() + halfIcon;
                child.setTranslationY((childCenterY - centerY)/2);
                child.setTranslationX((childCenterX - centerX)/2);
                child.setAlpha(0f);
                child.animate().translationY(0)
                        .translationX(0)
                        .alpha(1f)
                        .setInterpolator(new DecelerateInterpolator(3f))
                        .setDuration(animationTime)
                        .setStartDelay(animationTime)
                        .start();
            }
            currentShowing.gridXY.x = (int) recyclerView.getX();
            currentShowing.gridXY.y = (int) recyclerView.getY();
        }

    }

    private int getCollectionResId(Collection grid) {
        return Math.abs(grid.collectionId.hashCode());
    }

    private int getFolderResId(Slot folder) {
        return Math.abs(folder.slotId.hashCode());
    }

    private int getQuickActionsResId(Collection quickActions, int position) {
        return Math.abs(quickActions.collectionId.hashCode()) + position;
    }


    public void showFolder(int triggerPosition, Slot folder, final String gridId, int space, final int edgePosition, final NewServicePresenter.Showing currentShowing) {
        if (collectionViewsMap.get(folder.slotId) == null) {
            RecyclerView folderView = new RecyclerView(this);
            folderView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ServiceItemsAdapter adapter = new ServiceItemsAdapter(this, folder.items, true, getPackageManager(), iconPack, mScale, iconScale);
            int columnCount = 0;
            if (folder.items.size() > 4) {
                columnCount = 4;
            } else {
                columnCount = folder.items.size();
            }
            folderView.setLayoutManager(new GridLayoutManager(this, columnCount));
            folderView.setAdapter(adapter);
            folderView.setId(getFolderResId(folder));
            folderView.addItemDecoration(new GridSpacingItemDecoration((int)(space * mScale)));
            folderView.setBackgroundResource(R.color.background_lightish);
            collectionViewsMap.put(folder.slotId, folderView);
        }
        final RecyclerView folderView = (RecyclerView) collectionViewsMap.get(folder.slotId);
        final RecyclerView triggerGridView = (RecyclerView) collectionViewsMap.get(gridId);
        final float triggerX = triggerGridView.getChildAt(triggerPosition).getX() + triggerGridView.getX() + (iconScale * Cons.DEFAULT_ICON_WIDTH + space)/2 * mScale;
        final float triggerY = triggerGridView.getChildAt(triggerPosition).getY() + triggerGridView.getY() + (iconScale * Cons.DEFAULT_ICON_WIDTH + space) / 2 * mScale;
        boolean needDelay = false;
        if (backgroundView.findViewById(getFolderResId(folder)) == null) {
            backgroundView.addView(folderView);
            needDelay = true;
        }

        if (needDelay) {
            folderView.setVisibility(View.VISIBLE);
            folderView.setAlpha(0f);
            Handler handlerClose = new Handler();
            handlerClose.postDelayed(new Runnable() {
                public void run() {
                    Utility.setFolderPosition(triggerX, triggerY, folderView, triggerGridView,edgePosition, mScale);
                    folderView.setVisibility(View.VISIBLE);
                    currentShowing.gridXY.x = (int) folderView.getX();
                    currentShowing.gridXY.y = (int) folderView.getY();
                    folderView.setAlpha(1f);
                }
            }, 20);
        } else {
            Utility.setFolderPosition(triggerX, triggerY, folderView, triggerGridView, edgePosition, mScale);
            folderView.setVisibility(View.VISIBLE);
            currentShowing.gridXY.x = (int) folderView.getX();
            currentShowing.gridXY.y = (int) folderView.getY();
        }
        triggerGridView.setVisibility(View.GONE);
    }

    @Override
    public void showCircle(NewServiceModel.IconsXY iconsXY, Collection circle, RealmList<Slot> slots, float xInit, float yInit) {
        if (collectionViewsMap.get(circle.collectionId) == null) {
            FrameLayout circleView = new FrameLayout(this);
            addIconsToCircleView(circle.slots, circleView);
            collectionViewsMap.put(circle.collectionId, circleView);
        }
        FrameLayout frameLayout = (FrameLayout) collectionViewsMap.get(circle.collectionId);
        float previousX = 0;
        float previousY = 0;
        for (int i = 0; i < frameLayout.getChildCount(); i++) {
            if (i < slots.size()) {
                View icon = frameLayout.getChildAt(i);
                icon.setVisibility(View.VISIBLE);
//                icon.setAlpha(0f);
                Utility.setSlotIcon(slots.get(i), this, (ImageView) icon, getPackageManager(), iconPack, false, true);


                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    icon.setX(iconsXY.xs[i]);
                    icon.setY(iconsXY.ys[i]);

                } else {
                    icon.setX(iconsXY.xs[i]);
                    icon.setY(iconsXY.ys[i]);

                    if (i == 0) {
                        if (iconsXY.xs.length >= 2) {
                            icon.setX(iconsXY.xs[0] - (iconsXY.xs[1] - iconsXY.xs[0]));
                            icon.setY(iconsXY.ys[0] - (iconsXY.ys[1] - iconsXY.ys[0]));
                        } else {
                            icon.setX(iconsXY.xs[0]);
                            icon.setY(iconsXY.ys[0]);
                        }

                    } else {

                        icon.setX(previousX);
                        icon.setY(previousY);
                    }
                    icon.setAlpha(0f);
                    previousX = iconsXY.xs[i];
                    previousY = iconsXY.ys[i];

                    Path path = new Path();
                    path.moveTo(icon.getX(),icon.getY());
                    path.lineTo(iconsXY.xs[i], iconsXY.ys[i]);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(icon, "x", "y", path);
                    animator.setStartDelay(animationTime / (frameLayout.getChildCount() - i)/2);
                    animator.setDuration(animationTime);
                    animator.setInterpolator(new DecelerateInterpolator());
                    animator.start();
                }
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(icon, "alpha", 0f, 1f);
                alphaAnimator.setStartDelay(animationTime / (frameLayout.getChildCount() - i)/2);
                alphaAnimator.setDuration(animationTime);
                alphaAnimator.start();

            } else {
                frameLayout.getChildAt(i).setVisibility(View.GONE);
            }
        }
        if (!frameLayout.isAttachedToWindow()) {
            windowManager.addView(frameLayout, WINDOW_PARAMS_NO_TOUCH);
        }
        frameLayout.setVisibility(View.VISIBLE);
    }

    private void addIconsToCircleView(RealmList<Slot> slots, FrameLayout circleView) {
        circleView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        for (int i = 0; i < slots.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams((int) (Cons.DEFAULT_ICON_WIDTH * mScale * iconScale), (int) (Cons.DEFAULT_ICON_WIDTH * mScale * iconScale)));
            circleView.addView(imageView);
        }
    }

    @Override
    public void actionDownVibrate() {
        if (!sharedPreferences.getBoolean(Cons.DISABLE_HAPTIC_FEEDBACK_KEY, true)) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(sharedPreferences.getInt(Cons.VIBRATION_DURATION_KEY, Cons.DEFAULT_VIBRATE_DURATION));
        }
    }

    @Override
    public void actionMoveVibrate() {
        if (sharedPreferences.getBoolean(Cons.HAPTIC_ON_ICON_KEY, false)) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(sharedPreferences.getInt(Cons.VIBRATION_DURATION_KEY, Cons.DEFAULT_VIBRATE_DURATION));
        }
    }

    @Override
    public void showClock() {
        backgroundView.findViewById(R.id.indicator_frame_layout).setVisibility(View.GONE);
        if (!sharedPreferences.getBoolean(Cons.DISABLE_CLOCK_KEY, false)) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMMM");
            backgroundView.findViewById(R.id.clock_linear_layout).setVisibility(View.VISIBLE);
            TextView hourTextView = (TextView) backgroundView.findViewById(R.id.clock_time_in_hour);
            TextView dateTextView = (TextView) backgroundView.findViewById(R.id.clock_time_in_date);
            TextView batteryLifeTextView = (TextView) backgroundView.findViewById(R.id.clock_battery_life);
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
    }

    @Override
    public void indicateCurrentShowing(NewServicePresenter.Showing currentShowing, int id) {
        if (id >= 0) {
            switch (currentShowing.showWhat) {
                case NewServicePresenter.Showing.SHOWING_CIRCLE_AND_ACTION:
                    if (id < currentShowing.circleSlots.size()) {
                        indicateSlot(currentShowing.circleSlots.get(id));
                        if (currentShowing.action.visibilityOption != Collection.VISIBILITY_OPTION_ALWAYS_VISIBLE) {
                            hideQuickActions(currentShowing);
                        }

                    } else if (id >= 10) {
                        Slot slot = currentShowing.action.slots.get(id - 10);
                        if (slot.type.equals(Slot.TYPE_NULL) || slot.type.equals(Slot.TYPE_EMPTY)) {
                            indicateSlot(null);
                        } else {
                            indicateSlot(slot);
                        }
                        showQuickActions(currentShowing.edgePosition, id - 10, currentShowing, false, false);
                    }
                    break;
                case NewServicePresenter.Showing.SHOWING_CIRCLE_ONLY:
                    if (id < currentShowing.circleSlots.size()) {
                        indicateSlot(currentShowing.circleSlots.get(id));
                    }
                    break;
                case NewServicePresenter.Showing.SHOWING_GRID:
                    Slot slot = currentShowing.grid.slots.get(id);
                    indicateSlot(slot);
                    RecyclerView grid =(RecyclerView) collectionViewsMap.get(currentShowing.grid.collectionId);
                    if (slot.type.equals(Slot.TYPE_FOLDER)) {
                        gridAlphaAnimator = ObjectAnimator.ofFloat(grid, "alpha", 1f, 0f);
                        gridAlphaAnimator.setDuration(holdTime);
                        gridAlphaAnimator.start();
                    } else {
                        if (gridAlphaAnimator != null) {
                            gridAlphaAnimator.cancel();
                            grid.setAlpha(1f);
                            gridAlphaAnimator = null;
                        }
                    }
                    break;
                case NewServicePresenter.Showing.SHOWING_FOLDER:
                    if (id < currentShowing.folderItems.size()) {
                        indicateItem(currentShowing.folderItems.get(id));
                    }
                    break;
                case NewServicePresenter.Showing.SHOWING_NONE:
                    break;
            }
        } else {
            indicateSlot(null);
            if (currentShowing.showWhat == NewServicePresenter.Showing.SHOWING_GRID) {
                if (gridAlphaAnimator != null) {
                    RecyclerView grid =(RecyclerView) collectionViewsMap.get(currentShowing.grid.collectionId);
                    gridAlphaAnimator.cancel();
                    grid.setAlpha(1f);
                    gridAlphaAnimator = null;
                }
            }
        }
    }

    @Override
    public void indicateSlot(Slot slot) {
        if (slot != null) {
            Circle circle = (Circle) backgroundView.findViewById(R.id.circle);
            if (slot.type.equals(Slot.TYPE_FOLDER)) {
                circle.setVisibility(View.VISIBLE);
                circle.setAlpha(1f);
                circle.setAngle(0);
                CircleAngleAnimation angleAnimation = new CircleAngleAnimation(circle, 270);
                angleAnimation.setDuration(holdTime);
                circle.startAnimation(angleAnimation);
            } else if (circle.isShown()){
                circle.setAlpha(0f);
            }


            backgroundView.findViewById(R.id.clock_linear_layout).setVisibility(View.GONE);
            backgroundView.findViewById(R.id.indicator_frame_layout).setVisibility(View.VISIBLE);

            ImageView icon = (ImageView) backgroundView.findViewById(R.id.indicator_icon);
            TextView label = (TextView) backgroundView.findViewById(R.id.indicator_label);
            Utility.setSlotIcon(slot, this, icon, getPackageManager(), iconPack,false,true);
            Utility.setSlotLabel(slot, this, label);
        } else {
            backgroundView.findViewById(R.id.indicator_frame_layout).setVisibility(View.GONE);

        }
    }

    @Override
    public void indicateItem(Item item) {
        backgroundView.findViewById(R.id.circle).setVisibility(View.GONE);
        backgroundView.findViewById(R.id.clock_linear_layout).setVisibility(View.GONE);
        backgroundView.findViewById(R.id.indicator_frame_layout).setVisibility(View.VISIBLE);
        ImageView icon = (ImageView) backgroundView.findViewById(R.id.indicator_icon);
        TextView label = (TextView) backgroundView.findViewById(R.id.indicator_label);
        Utility.setItemIcon(item, this, icon, getPackageManager(), iconPack,true);
        label.setText(item.label);
    }

    @Override
    public void highlightSlot(NewServicePresenter.Showing currentShowing, int id) {
        if (id >= 0) {
            switch (currentShowing.showWhat) {
                case NewServicePresenter.Showing.SHOWING_GRID:
                    RecyclerView grid = (RecyclerView) collectionViewsMap.get(currentShowing.grid.collectionId);
                    grid.getChildAt(id).setBackgroundColor(Color.argb(214, 255, 255, 255));
                    break;
                case NewServicePresenter.Showing.SHOWING_CIRCLE_AND_ACTION:
                    if (id < 10) {
                        FrameLayout recent = (FrameLayout) collectionViewsMap.get(currentShowing.circle.collectionId);
                        highlightCircleIcon(recent.getChildAt(id), currentShowing.circleIconsXY.xs[id], currentShowing.circleIconsXY.ys[id]);
                    }
                    break;
                case NewServicePresenter.Showing.SHOWING_CIRCLE_ONLY:
                    ViewGroup circleView = (ViewGroup) collectionViewsMap.get(currentShowing.circle.collectionId);
                    highlightCircleIcon(circleView.getChildAt(id), currentShowing.circleIconsXY.xs[id], currentShowing.circleIconsXY.ys[id]);
                    break;
                case NewServicePresenter.Showing.SHOWING_FOLDER:
                    if (id < currentShowing.folderItems.size()) {
                        RecyclerView folder = (RecyclerView) collectionViewsMap.get(currentShowing.folderSlotId);
                        folder.getChildAt(id).setBackgroundColor(Color.argb(255, 42, 96, 70));
                    }
                    break;


            }

        }
    }

    @Override
    public void unhighlightSlot(NewServicePresenter.Showing currentShowing, int id) {
        if (id >= 0) {
            switch (currentShowing.showWhat) {
                case NewServicePresenter.Showing.SHOWING_GRID:
                    RecyclerView grid = (RecyclerView) collectionViewsMap.get(currentShowing.grid.collectionId);
                    grid.getChildAt(id).setBackgroundColor(Color.argb(0, 42, 96, 70));
                    break;
                case NewServicePresenter.Showing.SHOWING_CIRCLE_AND_ACTION:
                    if (id < 10) {
                        FrameLayout recent = (FrameLayout) collectionViewsMap.get(currentShowing.circle.collectionId);
                        unhighlightCircleIcon(recent.getChildAt(id), currentShowing.circleIconsXY.xs[id], currentShowing.circleIconsXY.ys[id]);
                    } else {
                        ((QuickActionsView) collectionViewsMap.get(getQuickActionsKey(currentShowing.edgePosition, currentShowing.action))).show(-1);
                    }
                    break;
                case NewServicePresenter.Showing.SHOWING_CIRCLE_ONLY:
                    if (id < currentShowing.circleSlots.size()) {
                        ViewGroup circleView = (ViewGroup) collectionViewsMap.get(currentShowing.circle.collectionId);
                        unhighlightCircleIcon(circleView.getChildAt(id), currentShowing.circleIconsXY.xs[id], currentShowing.circleIconsXY.ys[id]);
                    }
                    break;
                case NewServicePresenter.Showing.SHOWING_FOLDER:
                    if (id < currentShowing.folderItems.size()) {
                        RecyclerView folder = (RecyclerView) collectionViewsMap.get(currentShowing.folderSlotId);
                        folder.getChildAt(id).setBackgroundColor(Color.argb(0, 42, 96, 70));
                    }
                    break;
            }

        }
    }

    private void highlightCircleIcon(View icon, float iconX, float iconY) {
        if (icon != null) {

            icon.setScaleX(1.2f);
            icon.setScaleY(1.2f);

            int height = (int) ((16 + 48 * iconScale) * mScale);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(height, height);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon.setBackground(getDrawable(R.drawable.icon_background));
            } else {
                icon.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_background));
            }

            icon.setX(iconX - 8 * mScale);
            icon.setY(iconY - 8 * mScale);

            icon.setLayoutParams(layoutParams);
            icon.setPadding((int) (8 * mScale), (int) (8 * mScale), (int) (8 * mScale), (int) (8 * mScale));
        }
    }

    private void unhighlightCircleIcon(View icon, float iconX, float iconY) {
        if (icon != null) {
            icon.setScaleX(1f);
            icon.setScaleY(1f);


            FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(icon.getLayoutParams());
            layoutParams1.width = (int) (48 * mScale * iconScale);
            layoutParams1.height = (int) (48 * mScale * iconScale);

            icon.setBackground(null);

            icon.setX(iconX);
            icon.setY(iconY);

            icon.setLayoutParams(layoutParams1);
            icon.setPadding(0, 0, 0, 0);
        }
    }

    @Override
    public void startSlot(Slot slot, String lastApp, int showing, String collectionId) {
        Log.e(TAG, "startSlot: " + slot.toString());

        Utility.startSlot(slot, lastApp, this, sharedPreferences.getInt(Cons.CONTACT_ACTION_KEY, Cons.DEFAULT_CONTACT_ACTION), showing, collectionId, presenter);
    }

    @Override
    public void startItem(Item item, String lastApp) {
        Utility.startItem(item, lastApp, this, sharedPreferences.getInt(Cons.CONTACT_ACTION_KEY, Cons.DEFAULT_CONTACT_ACTION));
    }

    @Override
    public void hideCollection(String collectionId) {
        if (collectionId != null) {
            if (collectionViewsMap.get(collectionId) != null) {
                collectionViewsMap.get(collectionId).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showCollection(String collectionId) {
        if (collectionId != null && collectionViewsMap.get(collectionId)!=null) {
            collectionViewsMap.get(collectionId).setVisibility(View.VISIBLE);
            collectionViewsMap.get(collectionId).setAlpha(1f);

        }
    }

    @Override
    public Point getGridXy(String collectionId) {
        RecyclerView grid = (RecyclerView) collectionViewsMap.get(collectionId);
        return new Point((int) grid.getX(),(int)grid.getY());
    }

    @Override
    public void hideAllCollections() {
        Log.e(TAG, "hideAllCollections: ");
        Set<String> collectionIds = collectionViewsMap.keySet();
        for (String collectionId : collectionIds) {
            collectionViewsMap.get(collectionId).setVisibility(View.GONE);
        }
    }


    @Override
    public void hideAllExceptEdges() {
        hideAllCollections();
        if (backgroundView != null) {
            Log.e(TAG, "hideAllExceptEdges: ");
            backgroundView.setVisibility(View.GONE);
        }
    }



    @Override
    public synchronized void removeAllExceptEdges() {
        Set<String> collectionIds = collectionViewsMap.keySet();
        for (String collectionId : collectionIds) {
            try {
                windowManager.removeView(collectionViewsMap.get(collectionId));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "removeAllExceptEdges: can not remove " + collectionId);
            }
        }

        if (backgroundView != null && backgroundView.isAttachedToWindow()) {
            windowManager.removeView(backgroundView);
        }
    }

    @Override
    public synchronized void removeAll() {
        if (edge1View != null) {
            edge1View.setOnTouchListener(null);
            if (edge1View.isAttachedToWindow()) {
                windowManager.removeView(edge1View);
            }
        }
        if (edge2View != null) {
            edge2View.setOnClickListener(null);
            if (edge2View.isAttachedToWindow()) {
                windowManager.removeView(edge2View);
            }
        }
        removeAllExceptEdges();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        if (view.getId() == Cons.EDGE_1_ID_INT || view.getId() == Cons.EDGE_2_ID_INT) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    Log.e(TAG, "onTouch: action down");
                    presenter.onActionDown(getXCord(event), getYCord(event), view.getId());
                    break;
                case MotionEvent.ACTION_MOVE:
                    presenter.onActionMove(getXCord(event), getYCord(event));
                    break;
                case MotionEvent.ACTION_UP:
                    Log.e(TAG, "onTouch: action up");
                    presenter.onActionUp(getXCord(event), getYCord(event));
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
        } else if (view.getId() == Cons.BACKGROUND_ID_INT) {

        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.e(TAG, "onDown: ");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.e(TAG, "onShowPress: ");

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.e(TAG, "onSingleTapUp: ");
        presenter.onClickBackground(getXCord(e), getYCord(e));
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.e(TAG, "onLongPress: ");

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.e(TAG, "onFling: ");
        return false;
    }

    private float getXCord(MotionEvent motionEvent) {
        return motionEvent.getRawX();
    }

    private float getYCord(MotionEvent motionEvent) {
        return motionEvent.getRawY();
    }

    public final synchronized void addEdgeImage() {
        if (sharedPreferences.getBoolean(Cons.EDGE_1_ON_KEY, true) && edge1View != null && !edge1View.isAttachedToWindow()) {
            try {
                windowManager.addView(edge1View, edge1Para);
            } catch (IllegalStateException e) {
                Log.e(TAG, "addEdgeImage: fail when add edge1Image");
            }

        }
        if (sharedPreferences.getBoolean(Cons.EDGE_2_ON_KEY, false) && edge2View != null && !edge2View.isAttachedToWindow()) {
            try {
                windowManager.addView(edge2View, edge2Para);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (sharedPreferences.getBoolean(Cons.IS_DISABLE_IN_LANDSCAPE_KEY, false) && newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            removeEdgeImage();
        } else {
            addEdgeImage();
        }
        hideAllExceptEdges();
        Log.e(TAG, "onConfigurationChanged: ");
        super.onConfigurationChanged(newConfig);
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

                PendingIntent pendingIntent = PendingIntent.getBroadcast(NewServiceView.this, 0, remoteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (working) {
                    removeEdgeImage();
                    working = !working;


                    remoteAction =
                            new NotificationCompat.Action.Builder(
                                    android.R.drawable.ic_media_play,
                                    getString(R.string.resume),
                                    pendingIntent).build();
                } else {
                    addEdgeImage();
                    working = !working;


                    remoteAction =
                            new NotificationCompat.Action.Builder(
                                    android.R.drawable.ic_media_pause,
                                    getString(R.string.pause),
                                    pendingIntent).build();
                }

                notificationBuilder.mActions = new ArrayList<>();
                notificationBuilder.addAction(remoteAction);
                startForeground(Cons.NOTIFICATION_ID, notificationBuilder.build());

            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                Log.e(TAG, "onReceive: userPresent");
                hideAllExceptEdges();
            }
        }
    }

    public class GenerateDataOkReceiver extends BroadcastReceiver {
        public GenerateDataOkReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DataSetupService.BROADCAST_GENERATE_DATA_OK)) {
                Log.e(TAG, "onReceive: generate data ok");
                if (presenter == null) {
                    inject();
                    presenter.onViewAttach(NewServiceView.this);
                }
            }
        }
    }


}
