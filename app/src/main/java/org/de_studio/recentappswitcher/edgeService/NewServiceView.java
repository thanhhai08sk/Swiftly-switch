package org.de_studio.recentappswitcher.edgeService;

import android.animation.Animator;
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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeBounds;
import android.support.transition.Fade;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.MyApplication;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.ServiceSlotAdapter;
import org.de_studio.recentappswitcher.base.adapter.ItemsAdapter;
import org.de_studio.recentappswitcher.base.adapter.ServiceItemsAdapter;
import org.de_studio.recentappswitcher.circleFavoriteSetting.CircleFavoriteSettingView;
import org.de_studio.recentappswitcher.dadaSetup.DataSetupService;
import org.de_studio.recentappswitcher.dadaSetup.EdgeSetting;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerNewServiceComponent;
import org.de_studio.recentappswitcher.dagger.NewServiceModule;
import org.de_studio.recentappswitcher.gridFavoriteSetting.GridFavoriteSettingView;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.DataInfo;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;
import org.de_studio.recentappswitcher.service.Circle;
import org.de_studio.recentappswitcher.service.CircleAngleAnimation;
import org.de_studio.recentappswitcher.service.NotiDialog;
import org.de_studio.recentappswitcher.ui.MyEditText;
import org.de_studio.recentappswitcher.ui.QuickActionsView;
import org.de_studio.recentappswitcher.utils.GridSpacingItemDecoration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import io.realm.Realm;
import io.realm.RealmList;
import rx.subjects.PublishSubject;

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
import static org.de_studio.recentappswitcher.Cons.OPEN_FOLDER_DELAY_NAME;
import static org.de_studio.recentappswitcher.Cons.SHARED_PREFERENCE_NAME;
import static org.de_studio.recentappswitcher.Cons.USE_ANIMATION_NAME;
import static org.de_studio.recentappswitcher.Cons.USE_TRANSITION_NAME;

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
    static final int WINDOW_FLAG_TOUCHABLE_FOCUSABLE =
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;


    WindowManager.LayoutParams WINDOW_PARAMS_NO_TOUCH = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WINDOW_FLAG_NO_TOUCH,
            PixelFormat.TRANSLUCENT);
    WindowManager.LayoutParams WINDOW_PARAMS_TOUCHABLE = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WINDOW_FLAG_TOUCHABLE,
            PixelFormat.TRANSLUCENT);
    WindowManager.LayoutParams WINDOW_SCREENSHOT_LAYOUT_PARAMS = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WINDOW_FLAG_TOUCHABLE,
            PixelFormat.TRANSLUCENT);
    WindowManager.LayoutParams WINDOW_SEARCH_LAYOUT_PARAMS = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WINDOW_FLAG_TOUCHABLE_FOCUSABLE,
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
    @Named(OPEN_FOLDER_DELAY_NAME)
    boolean openFolderDelay;
    @Inject
    @Named(ICON_SCALE_NAME)
    float iconScale;
    @Inject
    @Named(ANIMATION_TIME_NAME)
    int animationTime;
    @Inject
    @Named(USE_ANIMATION_NAME)
    boolean useAnimation;
    @Inject
    @Named(USE_TRANSITION_NAME)
    boolean useTransition;
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
    NewServiceView.PackageChangedReceiver receiver1;
    private NotificationCompat.Builder notificationBuilder;
    Realm realm = Realm.getDefaultInstance();
    GenerateDataOkReceiver generateDataOkReceiver;

    ObjectAnimator gridAlphaAnimator;

    private GestureDetectorCompat backgroundGestureDetector;
    PublishSubject<Boolean> enterOrExitFullScreenSJ = PublishSubject.create();
    PublishSubject<Uri> finishTakingScreenshotSJ = PublishSubject.create();
    PublishSubject<String> searchQuerySJ = PublishSubject.create();
    PublishSubject<Item> startItemFromSearchSJ = PublishSubject.create();
    PublishSubject<Slot> startSlotSJ = PublishSubject.create();
    PublishSubject<Item> startItemSJ = PublishSubject.create();
    PublishSubject<Void> startSearchItemSJ = PublishSubject.create();
    boolean isFree;
    boolean isRTL;
    boolean useIndicator;
    boolean onHomeScreen;
    boolean firstSection = true;
    private ImageView screenshot;
    private ViewGroup searchParent;
    private MyEditText searchField;
    private ImageButton clearAll;
    private RecyclerView searchResults;
    private LinearLayout searchView;
    private ItemsAdapter searchResultAdapter;
    private Transition searchTransition;
    private boolean searchKeyboardShow;
    private int count = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        isFree = Utility.isFree(this);
        Configuration config = getResources().getConfiguration();
        isRTL = config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;


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

    private void setupVariables() {
        useIndicator = !sharedPreferences.getBoolean(Cons.DISABLE_INDICATOR_KEY, false);
    }

    @Override
    public PublishSubject<Boolean> onEnterOrExitFullScreen() {
        return enterOrExitFullScreenSJ;
    }

    @Override
    public PublishSubject<Uri> onFinishTakingScreenshot() {
        return finishTakingScreenshotSJ;
    }

    @Override
    public PublishSubject<Item> onStartItemFromSearch() {
        return startItemFromSearchSJ;
    }

    @Override
    public PublishSubject<Void> onStartSearchItem() {
        return startSearchItemSJ;
    }

    @Override
    public PublishSubject<Slot> onStartSlot() {
        return startSlotSJ;
    }

    @Override
    public PublishSubject<Item> onStartItem() {
        return startItemSJ;
    }

    @Override
    public PublishSubject<String> onSearch() {
        return searchQuerySJ;
    }

    private void inject() {
        Log.e(TAG, "inject: ");
        DaggerNewServiceComponent.builder()
                .appModule(new AppModule(this))
                .newServiceModule(new NewServiceModule(this, this, realm))
                .build().inject(this);
        setupVariables();

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
        if (receiver1 != null) {
            this.unregisterReceiver(receiver1);
            receiver1 = null;
        }
        realm.close();
    }

    @Override
    public void finish() {
        stopSelf();
    }

    @Override
    public boolean hasAtLeast1EdgeEnabled() {
        return sharedPreferences.getBoolean(Cons.EDGE_1_ON_KEY, true) || (!isFree && sharedPreferences.getBoolean(Cons.EDGE_2_ON_KEY, false));
    }

    @Override
    public void addEdgesToWindowAndSetListener() {
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
            if (!isFree && sharedPreferences.getBoolean(Cons.EDGE_2_ON_KEY, false)) {
                try {
                    windowManager.addView(edge2View, edge2Para);
                    edge2View.setOnTouchListener(null);
                    edge2View.setOnTouchListener(this);
                } catch (Exception e) {
                    Utility.startNotiDialog(getApplicationContext(), NotiDialog.DRAW_OVER_OTHER_APP);
                }
            }
        }

        if (sharedPreferences.getBoolean(Cons.DISABLE_IN_FULLSCREEN_KEY, false)) {
            if (sharedPreferences.getBoolean(Cons.EDGE_1_ON_KEY, true)) {
                edge1View.setOnSystemUiVisibilityChangeListener(this);
            } else if (!isFree && sharedPreferences.getBoolean(Cons.EDGE_2_ON_KEY, false)) {
                edge2View.setOnSystemUiVisibilityChangeListener(this);
            }
        }
        notifyEdgeServiceStarted();
    }

    @Override
    public void setupNotification() {
        Intent notiClickIntent = new Intent();
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
//            notiClickIntent.setClassName("com.android.settings", "com.android.settings.Settings$AppNotificationSettingsActivity");
//            notiClickIntent.putExtra("app_package", getPackageName());
//            notiClickIntent.putExtra("app_uid", getApplicationInfo().uid);
//        } else {
//            notiClickIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            notiClickIntent.addCategory(Intent.CATEGORY_DEFAULT);
//            notiClickIntent.setData(Uri.parse("package:" + getPackageName()));
//        }
        notiClickIntent = NotiDialog.getIntent(this, NotiDialog.NOTIFICATION_INFO);

        Intent remoteIntent = new Intent();
        remoteIntent.setAction(Cons.ACTION_TOGGLE_EDGES);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, remoteIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Action remoteAction =
                new NotificationCompat.Action.Builder(
                        android.R.drawable.ic_media_pause,
                        getString(R.string.pause),
                        pendingIntent).build();

        PendingIntent notiPending = PendingIntent.getActivity(getApplicationContext(), 0, notiClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.ic_stat_ic_looks_white_48dp1)
                .setContentIntent(notiPending)
                .addAction(remoteAction)
                .setPriority(Notification.PRIORITY_MIN)
                .setContentText(getString(R.string.notification_short_description)).setContentTitle(getString(R.string.notification_title));
        Notification notificationCompat = notificationBuilder.build();
        startForeground(Cons.NOTIFICATION_ID, notificationCompat);
    }

    @Override
    public void setupReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Cons.ACTION_TOGGLE_EDGES);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Cons.ACTION_REFRESH_FAVORITE);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Cons.ACTION_SCREENSHOT_OK);
        receiver = new NewServiceView.EdgesToggleReceiver();
        this.registerReceiver(receiver, filter);


        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter1.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter1.addDataScheme("package");
        receiver1 = new NewServiceView.PackageChangedReceiver();
        this.registerReceiver(receiver1, filter1);
    }

    @Override
    public Point getWindowSize() {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point;
    }

    @Override
    public ArrayList<String> getRecentApp(long timeInterval) {
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
//            long timeStart = System.currentTimeMillis();
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
//                Log.e(TAG, "mySortedMap size   = " + mySortedMap.size());
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
//                                Log.e(TAG, "getRecentApp: removed package = " + packa + "\ni = " + i +
//                                        "\nfirst && = " + (usageStats.getTotalTimeInForeground() > 500) + "value = " + usageStats.getTotalTimeInForeground() +
//                                        "\nsecond && = " + (!packa.contains("systemui")) +
//                                        "\nthird && = " + ((i == 0 || (excludeSet == null || excludeSet.where().equalTo(Cons.PACKAGENAME, packa).findFirst() == null))) +
//                                        "\nfourth && = " + !tempPackageName.contains(packa));

                            }
                            if (tempPackageName.size() >= (timeInterval == Cons.TIME_INTERVAL_LONG ? 15 : 10)) {
//                                Log.e(TAG, "tempackage >= " + 10);
                                break;
                            }
                        }
                    }
                }
            }
            onHomeScreen = tempPackageName.size() > 0 && tempPackageName.get(0).equals(launcherPackageName) || tempPackageName.size() == 0;
//            Log.e(TAG, "getRecentApp: time to get recent  = " + (System.currentTimeMillis() - timeStart));
//            Log.e(TAG, "getRecentApp: tem size = " + tempPackageName.size());
            return tempPackageName;

        }

    }

    @Override
    public void showBackground(boolean backgroundTouchable) {
        if (!backgroundView.isAttachedToWindow()) {
            try {
                windowManager.addView(backgroundView, backgroundTouchable ? WINDOW_PARAMS_TOUCHABLE : WINDOW_PARAMS_NO_TOUCH);
            } catch (IllegalStateException e) {
                e.printStackTrace();
                Log.e(TAG, "showBackground: already add to window");
            }
            backgroundGestureDetector = new GestureDetectorCompat(this, this);
            backgroundView.setAlpha(1f);
            backgroundView.setVisibility(View.VISIBLE);

            backgroundView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    backgroundGestureDetector.onTouchEvent(event);
                    return true;
                }
            });
        } else {
            backgroundView.setVisibility(View.VISIBLE);
        }

        if (animationTime >= 50) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                backgroundView.setBackgroundColor(backgroundColor);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(backgroundView, "alpha", 0f, 1f);
                objectAnimator.setDuration(animationTime).start();

            } else {
                ObjectAnimator objectAnimator = ObjectAnimator.ofArgb(backgroundView, "backgroundColor", Color.argb(0, 0, 0, 0), backgroundColor);
                objectAnimator.setDuration(animationTime);
                objectAnimator.start();
            }
        } else {
            backgroundView.setBackgroundColor(backgroundColor);
        }
    }

    public ImageView getIconImageView() {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams((int) (Cons.ICON_SIZE_DEFAULT * mScale * iconScale), (int) (Cons.ICON_SIZE_DEFAULT * mScale * iconScale)));
        imageView.setId(R.id.item_icon);
        return imageView;
    }

    public boolean isRTL() {
        return isRTL;
    }

    @Override
    public boolean isOpenFolderDelay() {
        return openFolderDelay;
    }

    public void showQuickActions(int edgePosition, final int highlighPosition, final NewServicePresenter.Showing currentShowing, boolean delay, boolean animate, boolean quickActionOnly) {
        if (currentShowing.action != null) {
            if (collectionViewsMap.get(getQuickActionsKey(edgePosition, currentShowing.action)) == null) {
                int width = (int) ((quickActionOnly? 60 : currentShowing.circle.radius) * 2 * mScale
                        + 60 * 2 * mScale);
                QuickActionsView actionsView = new QuickActionsView(this, iconPack, currentShowing.action.slots, edgePosition, currentShowing.action.visibilityOption != Collection.VISIBILITY_OPTION_ONLY_TRIGGERED_ONE_VISIBLE, width);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, width);
                actionsView.setLayoutParams(layoutParams);
                actionsView.setId(getQuickActionsResId(currentShowing.action, edgePosition));
                collectionViewsMap.put(getQuickActionsKey(edgePosition, currentShowing.action), actionsView);
            }
            final QuickActionsView quickActionsView = (QuickActionsView) collectionViewsMap.get(getQuickActionsKey(edgePosition, currentShowing.action));
            if (backgroundView.findViewById(getQuickActionsResId(currentShowing.action, edgePosition)) == null) {
                backgroundView.addView(quickActionsView);
            }

            if (firstSection) {
                quickActionsView.setVisibility(View.INVISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setQuickActionsPositionAndCurrentTrigger(quickActionsView, highlighPosition, currentShowing);
                    }
                }, 20);
            } else {
                setQuickActionsPositionAndCurrentTrigger(quickActionsView, highlighPosition, currentShowing);
            }
        }
    }

    private void setQuickActionsPositionAndCurrentTrigger(QuickActionsView quickActionsView, int highlighPosition, NewServicePresenter.Showing currentShowing) {
        quickActionsView.setVisibility(View.VISIBLE);
        quickActionsView.show(highlighPosition);
        int radius;
        if (currentShowing.showWhat == NewServicePresenter.Showing.SHOWING_ACTION_ONLY || currentShowing.circle == null) {
            radius = currentShowing.action.radius;
        } else radius = currentShowing.circle.radius;
        quickActionsView.setX(currentShowing.xInit - ( radius  * 2 * mScale + 56 * 2 * mScale) / 2);
        quickActionsView.setY(currentShowing.yInit - (radius * 2 * mScale + 56 * 2 * mScale) / 2);
    }

    @NonNull
    private String getQuickActionsKey(int edgePosition, Collection quickAction) {
        return quickAction.collectionId + String.valueOf(edgePosition);
    }

    public void hideQuickActions(NewServicePresenter.Showing currentShowing) {
        if (collectionViewsMap.get(getQuickActionsKey(currentShowing.edgePosition, currentShowing.action)) != null) {
            collectionViewsMap.get(getQuickActionsKey(currentShowing.edgePosition, currentShowing.action)).setVisibility(View.GONE);
        }
    }

    public void updateSearchResult(List<Item> items) {
        if (searchResultAdapter.getItemCount() != items.size()) {
            if (searchTransition == null) {
                searchTransition = new TransitionSet().addTransition(new Fade().addTarget(searchResults).setDuration(200)).addTransition(new ChangeBounds().addTarget(searchParent.findViewById(R.id.search_linear)).setDuration(200));
            }
            TransitionManager.beginDelayedTransition(searchParent, searchTransition);
        }
        searchResultAdapter.updateData(items);
    }

    public void showSearchView(List<Item> lastSearchItems) {
        if (searchParent == null) {
            searchParent = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.search_shortcut_view, backgroundView, false);
            searchParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startItemFromSearchSJ.onNext(null);

                }
            });
            searchResults = (RecyclerView) searchParent.findViewById(R.id.search_result);
            searchView = (LinearLayout) searchParent.findViewById(R.id.search_linear);
            searchResultAdapter = new ItemsAdapter(this, lastSearchItems, getPackageManager(), iconPack, startItemFromSearchSJ);
            searchResults.setLayoutManager(new LinearLayoutManager(this));
            searchResults.setAdapter(searchResultAdapter);
            searchResults.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING && searchKeyboardShow) {
                        hideKeyboard();
                        searchKeyboardShow = false;
                    }
                }
            });
            searchField = ((MyEditText) searchParent.findViewById(R.id.search_field));
            searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    Log.e(TAG, "onEditorAction: action");
                    startItemFromSearchSJ.onNext(searchResultAdapter.getFirstResult());
                    return true;
                }
            });
            clearAll = (ImageButton) searchParent.findViewById(R.id.clear_all);
            clearAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchField.getText().toString().isEmpty()) {
                        presenter.clearSearchHistory();
                    }
                    searchField.setText("");
                }
            });
            searchField.setBackButtonListener(new MyEditText.BackOnEditTextListener() {
                @Override
                public void onBackButton() {
                    if (searchKeyboardShow) {
                        hideKeyboard();
                        searchKeyboardShow = false;
                    } else {
                        presenter.onClickBackground(0, 0);
                    }
                }
            });
            searchField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchQuerySJ.onNext(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
//        if (backgroundView.findViewById(R.id.search_view) == null) {
//            backgroundView.addView(searchParent);
//        }

        if (!searchParent.isAttachedToWindow()) {
            windowManager.addView(searchParent, WINDOW_SEARCH_LAYOUT_PARAMS);

        }
        if (!Utility.isKitkat() && searchParent.isAttachedToWindow()) {
            int centerX = searchView.getWidth() / 2;
            int centerY = 0;
            float radius = ((float) Math.hypot(centerX, searchView.getHeight()));
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(searchView, centerX, centerY, 0f, radius);
            circularReveal.start();
        }

        Log.e(TAG, "showSearchView: alpha = " + searchParent.getAlpha());
        searchParent.setVisibility(View.VISIBLE);
        searchKeyboardShow = true;

        searchField.requestFocus();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) NewServiceView.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchField, InputMethodManager.SHOW_FORCED);
            }
        }, 100);


    }

    @Override
    public void hideKeyboard() {
        if (searchField != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            IBinder iBinder = searchField.getWindowToken();
            if (iBinder != null) {
                imm.hideSoftInputFromWindow(iBinder, 0);
            }
            searchField.clearFocus();
        }
    }

    @Override
    public void showGrid(final Collection grid, final int position, final NewServicePresenter.Showing currentShowing) {
        if (grid != null) {
//            Log.e(TAG, "showGrid: label " + grid.label);
        } else {
            Log.e(TAG, "showGrid: grid null");
            return;
        }
        if (collectionViewsMap.get(grid.collectionId) == null) {
            RecyclerView gridView = new RecyclerView(this);
            gridView.setItemAnimator(null);
            gridView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ServiceSlotAdapter adapter = new ServiceSlotAdapter(this, grid.slots, true, iconPack, mScale, iconScale, startSlotSJ);
            gridView.setLayoutManager(new GridLayoutManager(this, grid.columnCount));
            gridView.setClickable(false);
            gridView.setFocusable(false);
            gridView.setAdapter(adapter);
            gridView.setId(getCollectionResId(grid));
            gridView.addItemDecoration(new GridSpacingItemDecoration((int) (grid.space * mScale)));
            collectionViewsMap.put(grid.collectionId, gridView);
        }
        final RecyclerView recyclerView = (RecyclerView) collectionViewsMap.get(grid.collectionId);
        boolean needDelay = false;
        if (backgroundView.findViewById(getCollectionResId(grid)) == null) {
//            recyclerView.setClickable(true);
//            recyclerView.setFocusable(true);
            backgroundView.addView(recyclerView);
            needDelay = true;
        }
//        hideAllCollections();

        if (needDelay) {
            recyclerView.setVisibility(View.INVISIBLE);
            Handler handlerClose = new Handler();
            handlerClose.postDelayed(new Runnable() {
                public void run() {
                    hideAllCollections();
                    recyclerView.setVisibility(View.VISIBLE);
                    Utility.setFavoriteGridViewPosition(recyclerView
                            , grid.position == Collection.POSITION_CENTER
                            , recyclerView.getHeight()
                            , recyclerView.getWidth()
                            , currentShowing.xInit, currentShowing.yInit
                            , mScale
                            , position
                            , windowManager
                            , grid.marginHorizontal
                            , grid.marginVertical
                            , getWindowSize());
                    currentShowing.gridXY.x = (int) recyclerView.getX();
                    currentShowing.gridXY.y = (int) recyclerView.getY();
                    recyclerView.setAlpha(1f);
                    ((ServiceSlotAdapter) recyclerView.getAdapter()).updateIconsState();
                }
            }, 20);
        } else {
            Utility.setFavoriteGridViewPosition(recyclerView
                    , grid.position == Collection.POSITION_CENTER
                    , recyclerView.getHeight()
                    , recyclerView.getWidth()
                    , currentShowing.xInit, currentShowing.yInit
                    , mScale
                    , position
                    , windowManager
                    , grid.marginHorizontal
                    , grid.marginVertical,
                    getWindowSize());
            recyclerView.setVisibility(View.VISIBLE);
            ((ServiceSlotAdapter) recyclerView.getAdapter()).updateIconsState();
            View child;
            float childCenterX;
            float childCenterY;
            float halfIcon = 24 * mScale;
            float centerX = recyclerView.getWidth() / 2;
            float centerY = recyclerView.getHeight() / 2;

            if (useAnimation) {
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    child = recyclerView.getChildAt(i);
                    childCenterX = child.getX() + halfIcon;
                    childCenterY = child.getY() + halfIcon;
                    child.setTranslationY((childCenterY - centerY) / 2);
                    child.setTranslationX((childCenterX - centerX) / 2);
                    child.setAlpha(0f);
                    child.animate().translationY(0)
                            .translationX(0)
                            .alpha(1f)
                            .setInterpolator(new DecelerateInterpolator(3f))
                            .setDuration(animationTime)
                            .setStartDelay(animationTime)
                            .start();
                }
            }

            currentShowing.gridXY.x = (int) recyclerView.getX();
            currentShowing.gridXY.y = (int) recyclerView.getY();
        }

    }

    private int getCollectionResId(Collection grid) {
        return Math.abs(grid.collectionId.hashCode());
    }

    @Override
    public void openFile(Uri uri) {
        File file = new File(uri.getPath());
        Uri uri1 = FileProvider.getUriForFile(this, Cons.AUTHORITY, file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri1, "image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);

    }


    private int getFolderResId(Slot folder) {
        return Math.abs(folder.slotId.hashCode());
    }

    private int getQuickActionsResId(Collection quickActions, int position) {
        return Math.abs(quickActions.collectionId.hashCode()) + position;
    }

    @Override
    public void setFirstSectionFalse() {
        firstSection = false;
    }

    public void showFolder(int triggerPosition, Slot folder, final String gridId, int space, final int edgePosition, final NewServicePresenter.Showing currentShowing) {
        if (folder.items.size() == 0) {
            return;
        }
        createFolderViewIfNeeded(folder, space);

        final RecyclerView folderView = (RecyclerView) collectionViewsMap.get(folder.slotId);
        final RecyclerView triggerGridView = (RecyclerView) collectionViewsMap.get(gridId);
        boolean firstTime = addFolderToBackgroundIfNeeded(folder, folderView);

        displayFolderAndSetPosition(triggerPosition, space, edgePosition, currentShowing, folderView, triggerGridView, folder.items.size(), firstTime);
    }

    private void displayFolderAndSetPosition(int triggerPosition, final int space, final int edgePosition,
                                             final NewServicePresenter.Showing currentShowing,
                                             final RecyclerView folderView,
                                             final RecyclerView triggerGridView,
                                             final int size,
                                             boolean firstTime) {

        final float triggerX = triggerGridView.getChildAt(triggerPosition).getX() + triggerGridView.getX() + (iconScale * Cons.ICON_SIZE_DEFAULT + space) / 2 * mScale;
        final float triggerY = triggerGridView.getChildAt(triggerPosition).getY() + triggerGridView.getY() + (iconScale * Cons.ICON_SIZE_DEFAULT + space) / 2 * mScale;
        if (firstTime) {
            folderView.setVisibility(View.INVISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setFolderPosition(triggerX, triggerY, folderView, edgePosition, currentShowing, size, space);
                }
            }, 20);
        } else {
            setFolderPosition(triggerX, triggerY, folderView, edgePosition, currentShowing, size, space);
        }
        triggerGridView.setVisibility(View.GONE);
    }

    private void setFolderPosition(final float triggerX, final float triggerY, final RecyclerView folderView, final int edgePosition, final NewServicePresenter.Showing currentShowing, final int size, final int iconSpace) {
        Utility.setFolderPosition(triggerX, triggerY, folderView, edgePosition, mScale, iconScale, size, iconSpace,
                getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().heightPixels
        );
        folderView.setVisibility(View.VISIBLE);
        currentShowing.folderXY.x = (int) folderView.getX();
        currentShowing.folderXY.y = (int) folderView.getY();
    }

    private boolean addFolderToBackgroundIfNeeded(Slot folder, RecyclerView folderView) {
        if (backgroundView.findViewById(getFolderResId(folder)) == null) {
            backgroundView.addView(folderView);
            return true;
        } else return false;
    }


    private void createFolderViewIfNeeded(Slot folder, int space) {
        if (collectionViewsMap.get(folder.slotId) == null) {
            RecyclerView folderView = new RecyclerView(this);
            folderView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ServiceItemsAdapter adapter = new ServiceItemsAdapter(this, folder.items, true, getPackageManager(), iconPack, mScale, iconScale);
            adapter.setItemClickSJ(startItemSJ);
            int columnCount = 0;
            if (folder.items.size() > 4) {
                columnCount = 4;
            } else {
                columnCount = folder.items.size();
            }
            folderView.setLayoutManager(new GridLayoutManager(this, columnCount));
            folderView.setAdapter(adapter);
            folderView.setId(getFolderResId(folder));
            folderView.addItemDecoration(new GridSpacingItemDecoration((int) (space * mScale)));
//            folderView.setBackgroundResource(R.color.background_lightish);
            folderView.setBackgroundColor(sharedPreferences.getInt(Cons.FOLDER_BACKGROUND_COLOR_KEY, Cons.FOLDER_BACKGROUND_COLOR_DEFAULT));
            collectionViewsMap.put(folder.slotId, folderView);
        }
    }

    @Override
    public void showCircle(NewServiceModel.IconsXY iconsXY, Collection circle, RealmList<Slot> slots, float xInit, float yInit) {
        if (collectionViewsMap.get(circle.collectionId) == null) {
            FrameLayout circleView = new FrameLayout(this);
            circleView.setId(getCollectionResId(circle));
            addIconsToCircleView(circle.slots, circleView);
            collectionViewsMap.put(circle.collectionId, circleView);
        }
        FrameLayout frameLayout = (FrameLayout) collectionViewsMap.get(circle.collectionId);

        float previousX = 0;
        float previousY = 0;
        for (int i = 0; i < Math.min(frameLayout.getChildCount(),iconsXY.xs.length); i++) {
            if (i < slots.size()) {
                View icon = frameLayout.getChildAt(i);
                icon.setVisibility(View.VISIBLE);
                icon.setAlpha(0f);
                Utility.setSlotIcon(slots.get(i), this, (ImageView) icon, getPackageManager(), iconPack, false, true);


                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || !useAnimation) {
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
                    path.moveTo(icon.getX(), icon.getY());
                    path.lineTo(iconsXY.xs[i], iconsXY.ys[i]);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(icon, "x", "y", path);
                    animator.setStartDelay(animationTime / (frameLayout.getChildCount() - i) / 2);
                    animator.setDuration(animationTime);
                    animator.setInterpolator(new DecelerateInterpolator());
                    animator.start();


                }
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(icon, "alpha", 0f, 1f);
                alphaAnimator.setStartDelay(animationTime / (frameLayout.getChildCount() - i) / 2);
                alphaAnimator.setDuration(animationTime);
                alphaAnimator.start();

            } else {
                frameLayout.getChildAt(i).setVisibility(View.GONE);
            }
        }

        if (backgroundView.findViewById(getCollectionResId(circle)) == null) {
            backgroundView.addView(frameLayout);
        }

//        if (!frameLayout.isAttachedToWindow()) {
//            try {
//                windowManager.addView(frameLayout, WINDOW_PARAMS_NO_TOUCH);
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//                Log.e(TAG, "showCircle: already add to window");
//            }
//        }
        frameLayout.setVisibility(View.VISIBLE);
    }


    private void addIconsToCircleView(RealmList<Slot> slots, FrameLayout circleView) {
        circleView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        for (int i = 0; i < slots.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams((int) (Cons.ICON_SIZE_DEFAULT * mScale * iconScale), (int) (Cons.ICON_SIZE_DEFAULT * mScale * iconScale)));
            circleView.addView(imageView);
        }
    }

    public void showScreenshotReadyButton(final Uri uri) {
        removeScreenshotReadyButton();
        screenshot = new ImageView(this);
        int size = (int) (160 * mScale);
        int padding = (int) (4 * mScale);
        screenshot.setId(R.id.image_preview);
        screenshot.setPadding(padding, padding, padding, padding);
//        backgroundView.addView(imageView);
        Bitmap bitmap = Utility.decodeSampledBitmapFromUri(uri, size, size);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        WINDOW_SCREENSHOT_LAYOUT_PARAMS.width = width;
        WINDOW_SCREENSHOT_LAYOUT_PARAMS.height = height;
        WINDOW_SCREENSHOT_LAYOUT_PARAMS.gravity = Gravity.TOP | Gravity.END;


        windowManager.addView(screenshot, WINDOW_SCREENSHOT_LAYOUT_PARAMS);
        screenshot.setImageBitmap(bitmap);
        screenshot.setBackgroundColor(Color.WHITE);
        screenshot.postDelayed(new Runnable() {
            @Override
            public void run() {
                removeScreenshotReadyButton();
            }
        }, 5 * 1000);

        final GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                removeScreenshotReadyButton();
                openFile(uri);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (screenshot != null) {
                    if (screenshot.getTranslationX() > 72 * mScale || screenshot.getTranslationY() > 70 * mScale) {
                        removeScreenshotReadyButton();
                    } else {
                        screenshot.setTranslationX(screenshot.getTranslationX() - distanceX);
                        screenshot.setTranslationY(screenshot.getTranslationY() - distanceY);
                    }

                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                removeScreenshotReadyButton();
                return true;
            }
        });

        screenshot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetectorCompat.onTouchEvent(event);
                return true;
            }
        });
    }

    public synchronized void removeScreenshotReadyButton() {
        if (screenshot != null && screenshot.isAttachedToWindow()) {
            screenshot.setOnTouchListener(null);
            windowManager.removeView(screenshot);
            screenshot = null;
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
            TextView hourTextView = backgroundView.findViewById(R.id.clock_time_in_hour);
            TextView dateTextView = backgroundView.findViewById(R.id.clock_time_in_date);
            TextView batteryLifeTextView = backgroundView.findViewById(R.id.clock_battery_life);
            String batteryString = getApplicationContext().getString(R.string.batterylife) + " " + Utility.getBatteryLevel(getApplicationContext()) + "%";
            if (batteryLifeTextView != null) {
                batteryLifeTextView.setText(batteryString);
            }
            if (dateTextView != null) {
                dateTextView.setText(dateFormat.format(c.getTime()));
            }
            if (!DateFormat.is24HourFormat(getApplicationContext())) {
                SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                if (hourTextView != null) {
                    hourTextView.setText(hourFormat.format(c.getTime()));
                }
            } else {
                SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                if (hourTextView != null) {
                    hourTextView.setText(hourFormat.format(c.getTime()));
                }
            }
        }
    }

    public void hideClock() {
        backgroundView.findViewById(R.id.indicator_frame_layout).setVisibility(View.GONE);
        backgroundView.findViewById(R.id.clock_linear_layout).setVisibility(View.GONE);
    }

    @Override
    public void indicateCurrentShowing(NewServicePresenter.Showing currentShowing, int id) {
        if (!useIndicator) {
            return;
        }
        if (id >= 0) {
            switch (currentShowing.showWhat) {
                case NewServicePresenter.Showing.SHOWING_CIRCLE_AND_ACTION:
                    if (id < currentShowing.circleSlots.size()) {
                        indicateSlot(currentShowing.circleSlots.get(id));
                        if (currentShowing.action.visibilityOption != Collection.VISIBILITY_OPTION_ALWAYS_VISIBLE) {
                            hideQuickActions(currentShowing);
                        }

                    } else if (id >= 10 && id - 10 < currentShowing.action.slots.size()) {
                        Slot slot = currentShowing.action.slots.get(id - 10);
                        if (slot.type.equals(Slot.TYPE_NULL) || slot.type.equals(Slot.TYPE_EMPTY)) {
                            indicateSlot(null);
                        } else {
                            indicateSlot(slot);
                        }
                    }
                    break;
                case NewServicePresenter.Showing.SHOWING_CIRCLE_ONLY:
                    if (id < currentShowing.circleSlots.size()) {
                        indicateSlot(currentShowing.circleSlots.get(id));
                    }
                    break;
                case NewServicePresenter.Showing.SHOWING_ACTION_ONLY:
                    if (id >= 10 && id - 10 < currentShowing.action.slots.size()) {
                        Slot slot = currentShowing.action.slots.get(id - 10);
                        if (slot.type.equals(Slot.TYPE_NULL) || slot.type.equals(Slot.TYPE_EMPTY)) {
                            indicateSlot(null);
                        } else {
                            indicateSlot(slot);
                        }
                    }
                    break;
                case NewServicePresenter.Showing.SHOWING_GRID:
                    Slot slot = currentShowing.grid.slots.get(id);
                    indicateSlot(slot);
                    RecyclerView grid = (RecyclerView) collectionViewsMap.get(currentShowing.grid.collectionId);
                    if (slot.type.equals(Slot.TYPE_FOLDER)) {
                        if (openFolderDelay) {
                            gridAlphaAnimator = ObjectAnimator.ofFloat(grid, "alpha", 1f, 0f);
                            gridAlphaAnimator.setDuration(holdTime);
                            gridAlphaAnimator.start();
                        }
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
                    RecyclerView grid = (RecyclerView) collectionViewsMap.get(currentShowing.grid.collectionId);
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
            if (slot.type.equals(Slot.TYPE_FOLDER) && openFolderDelay) {
                circle.setVisibility(View.VISIBLE);
                circle.setAlpha(1f);
                circle.setAngle(0);
                CircleAngleAnimation angleAnimation = new CircleAngleAnimation(circle, 270);
                angleAnimation.setDuration(holdTime);
                circle.startAnimation(angleAnimation);
            } else if (circle.isShown()) {
                circle.setAlpha(0f);
            }


            backgroundView.findViewById(R.id.clock_linear_layout).setVisibility(View.GONE);
            backgroundView.findViewById(R.id.indicator_frame_layout).setVisibility(View.VISIBLE);

            ImageView icon = (ImageView) backgroundView.findViewById(R.id.indicator_icon);
            TextView label = (TextView) backgroundView.findViewById(R.id.indicator_label);
            Utility.setSlotIcon(slot, this, icon, getPackageManager(), iconPack, false, true);
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
        Utility.setItemIcon(item, this, icon, getPackageManager(), iconPack, true);
        label.setText(item.label);
    }

    @Override
    public void highlightSlot(NewServicePresenter.Showing currentShowing, int id) {
        if (id >= 0) {
            switch (currentShowing.showWhat) {
                case NewServicePresenter.Showing.SHOWING_GRID:
                    RecyclerView grid = (RecyclerView) collectionViewsMap.get(currentShowing.grid.collectionId);
                    if (grid.getChildAt(id) != null) {
                        Slot slot = ((ServiceSlotAdapter) grid.getAdapter()).getItem(id);
                        if (slot != null && !slot.type.equals(Slot.TYPE_EMPTY)) {
                            grid.getChildAt(id).setBackgroundColor(Color.argb(214, 255, 255, 255));
                        }
                    }
                    break;
                case NewServicePresenter.Showing.SHOWING_CIRCLE_AND_ACTION:
                    if (id < 10) {
                        FrameLayout recent = (FrameLayout) collectionViewsMap.get(currentShowing.circle.collectionId);
                        highlightCircleIcon(recent.getChildAt(id), currentShowing.circleIconsXY.xs[id], currentShowing.circleIconsXY.ys[id]);
                    } else if (id - 10 < currentShowing.action.slots.size()) {
                        showQuickActions(currentShowing.edgePosition, id - 10, currentShowing, false, false, false);
                    }
                    break;
                case NewServicePresenter.Showing.SHOWING_CIRCLE_ONLY:
                    ViewGroup circleView = (ViewGroup) collectionViewsMap.get(currentShowing.circle.collectionId);
                    highlightCircleIcon(circleView.getChildAt(id), currentShowing.circleIconsXY.xs[id], currentShowing.circleIconsXY.ys[id]);
                    break;
                case NewServicePresenter.Showing.SHOWING_ACTION_ONLY:
                    if (id >= 10 && id - 10 < currentShowing.action.slots.size()) {
                        showQuickActions(currentShowing.edgePosition, id - 10, currentShowing, false, false, true);
                    }
                    break;
                case NewServicePresenter.Showing.SHOWING_FOLDER:
                    if (id < currentShowing.folderItems.size()) {
                        RecyclerView folder = (RecyclerView) collectionViewsMap.get(currentShowing.folderSlotId);
                        if (folder.getChildAt(id) != null) {
                            folder.getChildAt(id).setBackgroundColor(Color.argb(255, 201, 201, 201));
                        }
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
                    if (grid.getChildAt(id) != null) {
                        grid.getChildAt(id).setBackgroundColor(Color.argb(0, 42, 96, 70));
                    }
                    break;
                case NewServicePresenter.Showing.SHOWING_CIRCLE_AND_ACTION:
                    if (id < 10) {
                        FrameLayout recent = (FrameLayout) collectionViewsMap.get(currentShowing.circle.collectionId);
                        unhighlightCircleIcon(recent.getChildAt(id), currentShowing.circleIconsXY.xs[id], currentShowing.circleIconsXY.ys[id]);
                    } else {
                        try {
                            ((QuickActionsView) collectionViewsMap.get(getQuickActionsKey(currentShowing.edgePosition, currentShowing.action))).show(-1);
                        } catch (Exception e) {
                            Log.e(TAG, "unhighlightSlot: " + e);
                        }
                    }
                    break;
                case NewServicePresenter.Showing.SHOWING_CIRCLE_ONLY:
                    if (id < currentShowing.circleSlots.size()) {
                        ViewGroup circleView = (ViewGroup) collectionViewsMap.get(currentShowing.circle.collectionId);
                        unhighlightCircleIcon(circleView.getChildAt(id), currentShowing.circleIconsXY.xs[id], currentShowing.circleIconsXY.ys[id]);
                    }
                    break;
                case NewServicePresenter.Showing.SHOWING_ACTION_ONLY:
                    if (id >= 10){
                        ((QuickActionsView) collectionViewsMap.get(getQuickActionsKey(currentShowing.edgePosition, currentShowing.action))).show(-1);
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
            icon.setScaleX(isRTL ? 1.5f : 1.2f);
            icon.setScaleY(isRTL ? 1.5f : 1.2f);

            int height = (int) ((16 + 48 * iconScale) * mScale);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(height, height);
            layoutParams.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon.setBackground(getDrawable(R.drawable.icon_background));
            } else {
                icon.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_background));
            }

            if (!isRTL) {
                icon.setLayoutParams(layoutParams);
                icon.setX(iconX - 8 * mScale);
                icon.setY(iconY - 8 * mScale);
            }
            int padding = isRTL ? ((int) (5 * mScale)) : ((int) (8 * mScale));
            icon.setPadding(padding, padding, padding, padding);
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


            icon.setLayoutParams(layoutParams1);
            icon.setPadding(0, 0, 0, 0);
            icon.setX(iconX);
            icon.setY(iconY);
        }
    }


    @Override
    public void startItem(Item item, String lastApp) {
        Log.e(TAG, "startItem: lastApp = " + lastApp);
        if (item.type.equals(Item.TYPE_ACTION) && item.action == Item.ACTION_SEARCH_SHORTCUTS) {
            startSearchItemSJ.onNext(null);
        }
        Utility.startItem(
                item,
                lastApp,
                this,
                sharedPreferences.getInt(Cons.CONTACT_ACTION_KEY, Cons.DEFAULT_CONTACT_ACTION),
                sharedPreferences.getInt(Cons.RINGER_MODE_ACTION_KEY, Cons.RINGER_MODE_ACTION_DEFAULT),
                onHomeScreen, useTransition);
    }

    @Override
    public void requestUsagePermissionForMarshmallowAndAbove() {
        if (!Utility.checkUsageAccess(this)) {
            Utility.needUsageAccessDialog(this);
        }
    }

    @Override
    public void setNullSlot(int showing, String currentCollectionId) {
        if (currentCollectionId != null) {
            Intent intent;
            switch (showing) {
                case NewServicePresenter.Showing.SHOWING_GRID:
                    intent = GridFavoriteSettingView.getIntent(this, currentCollectionId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.startActivity(intent);
                    break;
                case NewServicePresenter.Showing.SHOWING_CIRCLE_AND_ACTION:
                    intent = CircleFavoriteSettingView.getIntent(this, currentCollectionId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.startActivity(intent);
                    break;
                case NewServicePresenter.Showing.SHOWING_CIRCLE_ONLY:
                    intent = CircleFavoriteSettingView.getIntent(this, currentCollectionId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.startActivity(intent);
                    break;
            }
        }
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
        if (collectionId != null && collectionViewsMap.get(collectionId) != null) {
            collectionViewsMap.get(collectionId).setVisibility(View.VISIBLE);
            collectionViewsMap.get(collectionId).setAlpha(1f);

        }
    }

    @Override
    public Point getGridXy(String collectionId) {
        RecyclerView grid = (RecyclerView) collectionViewsMap.get(collectionId);
        return new Point((int) grid.getX(), (int) grid.getY());
    }

    @Override
    public void hideAllCollections() {
        if (collectionViewsMap != null) {
            Set<String> collectionIds = collectionViewsMap.keySet();
            for (String collectionId : collectionIds) {
//            Log.e(TAG, "hideAllCollections: hide " + collectionId);
                collectionViewsMap.get(collectionId).setVisibility(View.GONE);
//                View view = collectionViewsMap.get(collectionId);
//                collectionViewsMap.remove(collectionId);
//                backgroundView.removeView(view);
            }
            if (searchParent != null) {
                searchParent.setVisibility(View.GONE);

                if (searchField != null) {
                    searchField.setText("");
                    hideKeyboard();
                }
            }
        }
    }


    @Override
    public void hideAllExceptEdges() {
        hideAllCollections();
        if (backgroundView != null) {
//            Log.e(TAG, "hideAllExceptEdges: ");

            if (!Utility.isKitkat()) {
                ObjectAnimator objectAnimator = ObjectAnimator.ofArgb(backgroundView, "backgroundColor", backgroundColor, Color.argb(0, 0, 0, 0));
                objectAnimator.setDuration(100);
                objectAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (backgroundView != null) {
                            backgroundView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                objectAnimator.start();
            }else backgroundView.setVisibility(View.GONE);


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
        if (searchParent != null && searchParent.isAttachedToWindow()) {
            windowManager.removeView(searchParent);
        }
        removeScreenshotReadyButton();
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
                    presenter.onActionDown(getXCord(event), getYCord(event), view.getId());
                    break;
                case MotionEvent.ACTION_MOVE:
                    presenter.onActionMove(getXCord(event), getYCord(event));
                    break;
                case MotionEvent.ACTION_UP:
                    presenter.onActionUp(getXCord(event), getYCord(event));
                    break;
                case MotionEvent.ACTION_OUTSIDE:
                    presenter.onActionOutSide();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    presenter.onActionCancel();
                    break;
            }
            return true;
        } else if (view.getId() == Cons.BACKGROUND_ID_INT) {

        }
        return true;
    }

    @Override
    public void showToast(int message) {
        Toast.makeText(NewServiceView.this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        presenter.onClickBackground(getXCord(e), getYCord(e));
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    private float getXCord(MotionEvent motionEvent) {
        return motionEvent.getRawX();
    }

    private float getYCord(MotionEvent motionEvent) {
        return motionEvent.getRawY();
    }

    public final synchronized void addEdgeViews() {
        if (sharedPreferences.getBoolean(Cons.EDGE_1_ON_KEY, true) && edge1View != null && !edge1View.isAttachedToWindow()) {
            try {
                windowManager.addView(edge1View, edge1Para);
            } catch (IllegalStateException e) {
                Log.e(TAG, "addEdgeViews: fail when add edge1Image");
            } catch (SecurityException e) {
                if (!Utility.checkDrawPermission(this)) {
                    Utility.startNotiDialog(getApplicationContext(), NotiDialog.DRAW_OVER_OTHER_APP);
                } else throw new IllegalArgumentException("crash when addEdgeViews");
            }

        }
        if (!isFree && sharedPreferences.getBoolean(Cons.EDGE_2_ON_KEY, false) && edge2View != null && !edge2View.isAttachedToWindow()) {
            try {
                windowManager.addView(edge2View, edge2Para);
            } catch (IllegalStateException e) {
                Log.e(TAG, "addEdgeViews: fail when add edge2Image");
            }

        }
        notifyEdgeServiceStarted();
    }

    private void notifyEdgeServiceStarted() {
        ((MyApplication) getApplicationContext()).setEdgeIsOn(true);
        sendBroadcast(new Intent(Cons.ACTION_UPDATE_TOGGLE_WIDGET));
    }

    public final synchronized void removeEdgeViews() {
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
        notifyEdgeServicePaused();
    }

    private void notifyEdgeServicePaused() {
        ((MyApplication) getApplicationContext()).setEdgeIsOn(false);
        sendBroadcast(new Intent(Cons.ACTION_UPDATE_TOGGLE_WIDGET));
    }

    @Override
    public void disableEdgeViews(boolean disable) {
        if (disable) {
            if (edge1View != null && edge1View.isAttachedToWindow()) {
                Log.e(TAG, "disableEdgeViews: invisible");
                edge1View.setVisibility(View.INVISIBLE);
            }
            if (edge2View != null && edge2View.isAttachedToWindow()) {
                edge2View.setVisibility(View.INVISIBLE);
            }
        } else {
            if (edge1View != null && edge1View.isAttachedToWindow()) {
                Log.e(TAG, "disableEdgeViews: visible");
                edge1View.setVisibility(View.VISIBLE);
            }
            if (edge2View != null && edge2View.isAttachedToWindow()) {
                edge2View.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (sharedPreferences != null &&
                sharedPreferences.getBoolean(Cons.IS_DISABLE_IN_LANDSCAPE_KEY, false)) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                removeEdgeViews();
            } else {
                addEdgeViews();
            }
        }
        hideAllExceptEdges();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSystemUiVisibilityChange(int i) {
        int flag = i & View.SYSTEM_UI_FLAG_FULLSCREEN;
        enterOrExitFullScreenSJ.onNext(flag != 0);
    }

    public class EdgesToggleReceiver extends BroadcastReceiver {
        public EdgesToggleReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Cons.ACTION_TOGGLE_EDGES)) {
                Log.e(TAG, "onReceive: receive broadcast success");
                Intent remoteIntent = new Intent();
                remoteIntent.setAction(Cons.ACTION_TOGGLE_EDGES);
                NotificationCompat.Action remoteAction;

                PendingIntent pendingIntent = PendingIntent.getBroadcast(NewServiceView.this, 0, remoteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (((MyApplication) context.getApplicationContext()).isEdgeIsOn()) {
                    removeEdgeViews();
                    showToast(R.string.edge_service_paused_toast);
                    remoteAction =
                            new NotificationCompat.Action.Builder(
                                    android.R.drawable.ic_media_play,
                                    getString(R.string.resume),
                                    pendingIntent).build();
                } else {
                    addEdgeViews();
                    showToast(R.string.edge_running_toast);
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
            } else if (intent.getAction().equals(Cons.ACTION_SCREENSHOT_OK)) {
                Log.e(TAG, "onReceive: screenshot ok, uri = " + intent.getParcelableExtra("uri").toString());
                finishTakingScreenshotSJ.onNext(((Uri) intent.getParcelableExtra("uri")));
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

    public class PackageChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                Log.e(TAG, "onReceive: action package removed");
                if (!intent.getExtras().getBoolean(Intent.EXTRA_REPLACING)) {
                    String dataString = intent.getDataString();
                    String packageN = dataString.substring(dataString.indexOf(":") + 1);
                    presenter.onUninstallPackage(packageN);
                }
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                Log.e(TAG, "onReceive: action package added");
                if (!intent.getExtras().getBoolean(Intent.EXTRA_REPLACING)) {
                    int uid = intent.getExtras().getInt(Intent.EXTRA_UID);
                    String[] packageName = getPackageManager().getPackagesForUid(uid);
                    if (packageName != null) {
                        for (String s : packageName) {
                            Log.e(TAG, "onReceive: new app " + s);
                            presenter.newPackageInstalled(s, Utility.getLabelFromPackageName(s, getPackageManager()));
                        }
                    }
                }
            }
        }
    }


}
