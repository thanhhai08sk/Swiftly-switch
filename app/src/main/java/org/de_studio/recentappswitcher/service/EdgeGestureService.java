package org.de_studio.recentappswitcher.service;

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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.MyRealmMigration;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.favoriteShortcut.CircleFavoriteAdapter;
import org.de_studio.recentappswitcher.favoriteShortcut.FavoriteSettingActivity;
import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

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
    private static final int NOTIFICATION_ID = 111;
    public static final int CURRENT_SCHEMA_VERSION = 1;
    float mScale;
    static final String TAG = EdgeGestureService.class.getSimpleName();
    static final int EDGE_GESTURE_NOTIFICAION_ID = 10;
    private WindowManager windowManager;
    private ImageView edge1Image;
    private ImageView edge2Image;
    private FrameLayout item1View, item2View, shortcutView, backgroundFrame;
    public int icon_height = 48, serviceId;
    public int icon_width = 48, icon_rad = 24;
    public int icon_distance = 110, distance_to_arc = 35, distance_to_arc_pxl;
    public float icon_distance_pxl, icon_24dp_in_pxls, mIconScale = 1f;
    public int edge1Length, edge2Length, edge1offset, edge2offset;
    public int edge1HeightPxl, edge2HeightPxl;
    public int edge1WidthPxl, edge2WidthPxl;
    public int edge1Sensivite, edge2Sensitive;
    private List<MyImageView> iconImageArrayList1, iconImageArrayList2;
    private String[] packagename, pinnedPackageName;
    private String launcherPackagename, lastAppPackageName;
    private int[] x, y, folderCoor;
    private int numOfIcon, gridRow, gridColumn, gridGap, gridX, gridY, numOfRecent;
    public static final int GRID_ICON_SIZE = 48, GRID_2_PADDING = 10;
    private boolean hasOneActive = false;
    private boolean hasHomwBackNotiVisible = false;
    private boolean isEdge1On, isEdge2On;
    public int edge1Position, edge2Position, iconPaddingLeft, iconPaddingTop;
    private SharedPreferences defaultShared, sharedPreferences1, sharedPreferences2, sharedPreferences_favorite, sharedPreferences_exclude;
    private MyImageView[] iconImageList1, iconImageList2;
    private ExpandStatusBarView action4View, action1View, action2View, action3View;
    private Vibrator vibrator;
    private int ovalOffSet, ovalRadiusPlus = 17, ovalRadiusPlusPxl, ovalOffSetInDp = 70;
    private long holdTime = 450, vibrationDuration;
    private boolean touched = false, switched = false, isOutOfTrial = false, isFreeVersion = false;
    private String[] spinnerEntries;
    private GridView shortcutGridView, shortcutFolderGrid;
    private Circle circle;
    private FavoriteShortcutAdapter shortcutAdapter;
    private FolderAdapter folderAdapter;
    private CircleFavoriteAdapter circltShortcutAdapter;
    private IconPackManager.IconPack iconPack;
    private boolean isClockShown = false;
    private View clockView;
    private Realm pinAppRealm, favoriteRealm,circleFavoRealm;
    private Set<String> pinnedSet;
    private WindowManager.LayoutParams backgroundParams;
    private int backgroundColor, guideColor, animationTime, edge1mode, edge2mode;
    private Set<String> excludeSet;
    private long startDown;
    private String[] savedPackage;
    private int[] instantFavoAction;
    private boolean useInstantFavo, onInstantFavo;
    private WindowManager.LayoutParams paramsEdge1, paramsEdge2;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");

        if (getPackageName().equals(MainActivity.FREE_VERSION_PACKAGE_NAME)) isFreeVersion = true;
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo res = getPackageManager().resolveActivity(launcherIntent, 0);
        if (res.activityInfo != null) {
            launcherPackagename = res.activityInfo.packageName;
        } else launcherPackagename = "";
        Log.e(TAG, "Launcher packagename = " + launcherPackagename);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        icon_distance_pxl = icon_distance * mScale;
        icon_24dp_in_pxls = 24 * mScale;
        distance_to_arc_pxl = (int) (distance_to_arc * mScale);
        backgroundFrame = (FrameLayout) layoutInflater.inflate(R.layout.background, null);
        backgroundFrame.setBackgroundColor(backgroundColor);
        backgroundParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                PixelFormat.TRANSLUCENT);


        if (isEdge1On) {
            edge1Image = new ImageView(getApplicationContext());
            if (sharedPreferences1.getBoolean(EdgeSetting.USE_GUIDE_KEY, true)) {
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadius(0);
                shape.setStroke((int) (2 * mScale), guideColor);
                LayerDrawable drawable = new LayerDrawable(new Drawable[]{shape});
                switch (edge1Position / 10) {
                    case 1:
                        drawable.setLayerInset(0, (int) (-5 * mScale), (int) (-5 * mScale), 0, (int) (-5 * mScale));
                        break;
                    case 2:
                        drawable.setLayerInset(0, 0, (int) (-5 * mScale), (int) (-5 * mScale), (int) (-5 * mScale));
                        break;
                    case 3:
                        drawable.setLayerInset(0, (int) (-5 * mScale), (int) (-5 * mScale), (int) (-5 * mScale), 0);
                        break;
                }

                edge1Image.setBackground(drawable);
            }

            if (edge1Image != null) {

                if (edge1Position >= 30) {
                    edge1HeightPxl = (int) (edge1Sensivite * mScale);
                    edge1WidthPxl = (int) (edge1Length * mScale);
                } else {
                    edge1HeightPxl = (int) (edge1Length * mScale);
                    edge1WidthPxl = (int) (edge1Sensivite * mScale);
                }
                RelativeLayout.LayoutParams edge1ImageLayoutParams = new RelativeLayout.LayoutParams(edge1WidthPxl,edge1HeightPxl);
                edge1ImageLayoutParams.height = edge1HeightPxl;
                edge1ImageLayoutParams.width = edge1WidthPxl;
                edge1Image.setLayoutParams(edge1ImageLayoutParams);
            }
            item1View = (FrameLayout) layoutInflater.inflate(R.layout.items, null);
            iconImageList1 = new MyImageView[6];
            iconImageList1[0] = (MyImageView) item1View.findViewById(R.id.item_0);
            iconImageList1[1] = (MyImageView) item1View.findViewById(R.id.item_1);
            iconImageList1[2] = (MyImageView) item1View.findViewById(R.id.item_2);
            iconImageList1[3] = (MyImageView) item1View.findViewById(R.id.item_3);
            iconImageList1[4] = (MyImageView) item1View.findViewById(R.id.item_4);
            iconImageList1[5] = (MyImageView) item1View.findViewById(R.id.item_5);
            iconImageArrayList1 = new ArrayList<MyImageView>();
            iconImageArrayList1.add(iconImageList1[0]);
            iconImageArrayList1.add(iconImageList1[1]);
            iconImageArrayList1.add(iconImageList1[2]);
            iconImageArrayList1.add(iconImageList1[3]);
            iconImageArrayList1.add(iconImageList1[4]);
            iconImageArrayList1.add(iconImageList1[5]);
            FrameLayout.LayoutParams sampleParas1 = new FrameLayout.LayoutParams(iconImageArrayList1.get(0).getLayoutParams());
            for (MyImageView image : iconImageArrayList1) {
                sampleParas1.height = (int) (48 * mIconScale * mScale);
                sampleParas1.width = (int) (48 * mIconScale * mScale);
                image.setLayoutParams(sampleParas1);
            }
            paramsEdge1 = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    PixelFormat.TRANSLUCENT);
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
                case 31:
                    paramsEdge1.gravity = Gravity.BOTTOM | Gravity.CENTER;
                    break;
            }
            paramsEdge1.height = edge1HeightPxl;
            paramsEdge1.width = edge1WidthPxl;
            if (edge1Position == 12 | edge1Position == 22) {
                paramsEdge1.y = (int) (edge1offset * mScale);
            } else if (edge1Position == 31) {
                paramsEdge1.x = -(int) (edge1offset * mScale);
            } else {
                paramsEdge1.y = -(int) (edge1offset * mScale);
            }

//            paramsEdge1.verticalMargin = 500;
//            paramsEdge1.horizontalMargin= 500;
//            tempImageView = new ImageView(getApplicationContext());
//            tempImageView.setBackgroundResource(R.color.colorAccent);

            if (isEdge1On && !(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && defaultShared.getBoolean(EdgeSetting.IS_DISABLE_IN_LANSCAPE,false)) ) {
                windowManager.addView(edge1Image, paramsEdge1);
            } else {
                removeView(edge1Image);
            }
            boolean isOnlyFavorite1 = sharedPreferences1.getBoolean(EdgeSetting.IS_ONLY_FAVORITE_KEY, false);
            OnTouchListener onTouchListener1 = new OnTouchListener(edge1Position, iconImageList1, item1View, iconImageArrayList1, isOnlyFavorite1, edge1mode);
            edge1Image.setOnTouchListener(onTouchListener1);
        }


        if (isEdge2On) {
            edge2Image = new ImageView(getApplicationContext());
            if (sharedPreferences2.getBoolean(EdgeSetting.USE_GUIDE_KEY, true)) {
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadius(0);
                shape.setStroke((int) (2 * mScale), guideColor);
                LayerDrawable drawable = new LayerDrawable(new Drawable[]{shape});
                switch (edge2Position / 10) {
                    case 1:
                        drawable.setLayerInset(0, (int) (-5 * mScale), (int) (-5 * mScale), 0, (int) (-5 * mScale));
                        break;
                    case 2:
                        drawable.setLayerInset(0, 0, (int) (-5 * mScale), (int) (-5 * mScale), (int) (-5 * mScale));
                        break;
                    case 3:
                        drawable.setLayerInset(0, (int) (-5 * mScale), (int) (-5 * mScale), (int) (-5 * mScale), 0);
                        break;
                }

                edge2Image.setBackground(drawable);
            }
            if (Utility.getPositionIntFromString(sharedPreferences2.getString(EdgeSetting.EDGE_POSITION_KEY, spinnerEntries[5]), getApplicationContext()) >= 30) {
                edge2HeightPxl = (int) (edge2Sensitive * mScale);
                edge2WidthPxl = (int) (edge2Length * mScale);
            } else {

                edge2HeightPxl = (int) (edge2Length * mScale);
                edge2WidthPxl = (int) (edge2Sensitive * mScale);
            }
            RelativeLayout.LayoutParams edge2ImageLayoutParams = new RelativeLayout.LayoutParams(edge2WidthPxl,edge2HeightPxl);
            edge2ImageLayoutParams.height = edge2HeightPxl;
            edge2ImageLayoutParams.width = edge2WidthPxl;
            edge2Image.setLayoutParams(edge2ImageLayoutParams);
            paramsEdge2 = new WindowManager.LayoutParams(
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
                    break;
                case 31:
                    paramsEdge2.gravity = Gravity.BOTTOM | Gravity.CENTER;
                    break;
            }

            paramsEdge2.height = edge2HeightPxl;
            paramsEdge2.width = edge2WidthPxl;
            if (edge2Position == 12 | edge2Position == 22) {
                paramsEdge2.y = (int) (edge2offset * mScale);
            } else if (edge2Position == 31) {
                paramsEdge2.x = -(int) (edge2offset * mScale);
            } else {
                paramsEdge2.y = -(int) (edge2offset * mScale);
            }
            if (isEdge2On  && !(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && defaultShared.getBoolean(EdgeSetting.IS_DISABLE_IN_LANSCAPE,false))) {
                windowManager.addView(edge2Image, paramsEdge2);
            } else {
                    removeView(edge2Image);
            }
            item2View = (FrameLayout) layoutInflater.inflate(R.layout.items, null);
            iconImageList2 = new MyImageView[6];
            iconImageList2[0] = (MyImageView) item2View.findViewById(R.id.item_0);
            iconImageList2[1] = (MyImageView) item2View.findViewById(R.id.item_1);
            iconImageList2[2] = (MyImageView) item2View.findViewById(R.id.item_2);
            iconImageList2[3] = (MyImageView) item2View.findViewById(R.id.item_3);
            iconImageList2[4] = (MyImageView) item2View.findViewById(R.id.item_4);
            iconImageList2[5] = (MyImageView) item2View.findViewById(R.id.item_5);
            iconImageArrayList2 = new ArrayList<MyImageView>();
            iconImageArrayList2.add(iconImageList2[0]);
            iconImageArrayList2.add(iconImageList2[1]);
            iconImageArrayList2.add(iconImageList2[2]);
            iconImageArrayList2.add(iconImageList2[3]);
            iconImageArrayList2.add(iconImageList2[4]);
            iconImageArrayList2.add(iconImageList2[5]);
            FrameLayout.LayoutParams sampleParas2 = new FrameLayout.LayoutParams(iconImageArrayList2.get(0).getLayoutParams());
            for (MyImageView image : iconImageArrayList2) {
                sampleParas2.height = (int) (48 * mIconScale * mScale);
                sampleParas2.width = (int) (48 * mIconScale * mScale);
                image.setLayoutParams(sampleParas2);
            }

            boolean isOnlyFavorite2 = sharedPreferences2.getBoolean(EdgeSetting.IS_ONLY_FAVORITE_KEY, false);
            OnTouchListener onTouchListener2 = new OnTouchListener(edge2Position, iconImageList2, item2View, iconImageArrayList2, isOnlyFavorite2, edge2mode);
            edge2Image.setOnTouchListener(onTouchListener2);
        }

        String iconPackPacka = defaultShared.getString(EdgeSetting.ICON_PACK_PACKAGE_NAME_KEY, "none");
        if (!iconPackPacka.equals("none")) {
            IconPackManager iconPackManager = new IconPackManager();
            iconPackManager.setContext(getApplicationContext());
            iconPack = iconPackManager.getInstance(iconPackPacka);
            if (iconPack != null) {
                iconPack.load();
            }
        }
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

        PendingIntent notiPending = PendingIntent.getActivity(getApplicationContext(), 0, hideNotiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_ic_looks_white_48dp1)
                .setContentIntent(notiPending)
                .setPriority(Notification.PRIORITY_MIN)
                .setContentText(getString(R.string.notification_text)).setContentTitle(getString(R.string.notification_title));
        Notification notificationCompat = builder.build();
        startForeground(NOTIFICATION_ID, notificationCompat);


        shortcutView = (FrameLayout) layoutInflater.inflate(R.layout.grid_shortcut, null);
        shortcutGridView = (GridView) shortcutView.findViewById(R.id.edge_shortcut_grid_view);
        shortcutFolderGrid = (GridView) shortcutView.findViewById(R.id.folder_grid);

        return START_STICKY;
    }

    public class OnTouchListener implements View.OnTouchListener {
        private int x_init_cord, y_init_cord, mode, positionOfFolder;
        private int position, iconIdBackgrounded = -2, preShortcutToSwitch = -1,preShortcutInFolderToSwitch = -1 , activateId = 0, activatedId = 0;
        private FrameLayout itemView;
        private MyImageView[] iconImageList;
        private List<MyImageView> iconImageArrayList;
        private DelayToSwitchTask delayToSwitchTask;
        private boolean isOnlyFavorite, isStayPermanent, isShortcutBackgroundNull = true, isShortcutBackgroundInFolderNull = true, isCircleFavorite, folderShown;
        private ViewPropertyAnimator folderAnimator;
        private boolean onFolderAnimator;

        public OnTouchListener(int position, MyImageView[] iconImageList, FrameLayout itemView, List<MyImageView> iconImageArrayList, boolean isOnlyFavorite, int mode) {
            this.position = position;
            this.iconImageList = iconImageList;
            this.itemView = itemView;
            this.iconImageArrayList = iconImageArrayList;
            this.isOnlyFavorite = isOnlyFavorite;
            this.isCircleFavorite = true;
            this.mode = mode;
        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int x_cord = (int) event.getRawX();
            int y_cord = (int) event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    folderShown = false;
                    removeAllExceptEdgeView();
                    onInstantFavo = false;
//                    startDown = System.currentTimeMillis();
//                    Log.e(TAG, "Start action down at " + startDown);
                    switch (position / 10) {
                        case 1:
                            x_init_cord = (int) (x_cord - 10 * mScale);
                            y_init_cord = y_cord - getYOffset(y_cord);
                            break;
                        case 2:
                            x_init_cord = (int) (x_cord + 10 * mScale);
                            y_init_cord = y_cord - getYOffset(y_cord);
                            break;
                        case 3:
                            x_init_cord = x_cord - getXOffset(x_cord);
                            y_init_cord = (int) (y_cord - 10 * mScale);
                            break;
                    }
                    clearIconBackground();
                    Utility.setIconPositionNew(iconImageList, icon_distance_pxl, icon_24dp_in_pxls * mIconScale, position, x_init_cord, y_init_cord, 6, defaultShared.getBoolean(EdgeSetting.ANIMATION_KEY, false), animationTime);
                    excludeSet = sharedPreferences_exclude.getStringSet(EdgeSetting.EXCLUDE_KEY, new HashSet<String>());

//                    Log.e(TAG, "foreGroundApp is " + Utility.getForegroundApp(getApplicationContext()));
//                    if (!backgroundFrame.isAttachedToWindow() && (position == edge1Position || position == edge2Position)) {
//                    if (!backgroundFrame.isAttachedToWindow() && (defaultShared.getInt(EdgeSetting.SERVICE_ID,10) == serviceId )) {
                    if (defaultShared.getBoolean(EdgeSetting.ANIMATION_KEY, false)) {
                        backgroundFrame.setAlpha(0f);
                        windowManager.addView(backgroundFrame, backgroundParams);
                        backgroundFrame.animate().alpha(1f).setDuration(defaultShared.getInt(EdgeSetting.ANI_TIME_KEY, 100)).setInterpolator(new FastOutSlowInInterpolator());
                    } else {
                        windowManager.addView(backgroundFrame, backgroundParams);
                        backgroundFrame.setAlpha(1f);
                    }
                    isShortcutBackgroundNull = true;
                    preShortcutToSwitch = -1;
                    preShortcutInFolderToSwitch = -1;
                    isShortcutBackgroundInFolderNull = true;
                    if (!defaultShared.getBoolean(EdgeSetting.DISABLE_HAPTIC_FEEDBACK_KEY, true)) {
                        vibrator.vibrate(vibrationDuration);
                    }
                    isClockShown = false;
                    if (isFreeVersion) {
                        isOutOfTrial = System.currentTimeMillis() - defaultShared.getLong(EdgeSetting.BEGIN_DAY_KEY, System.currentTimeMillis())
                                > MainActivity.trialTime;
                    } else isOutOfTrial = false;
                    switched = isOnlyFavorite;
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
                            if (i != 0 && !packName.equals(launcherPackagename) && !excludeSet.contains(packName) && !packName.contains("launcher") && !pinnedSet.contains(packName)) {
                                tempPackageName.add(packName);
                            }
                        }

                        if (tempPackageName.size() >= 1) {
                            lastAppPackageName = tempPackageName.get(0);
                        }
                        if (6 - tempPackageName.size() - pinnedPackageName.length > 0) {
                            packagename = new String[tempPackageName.size() + pinnedPackageName.length];
                        } else {
                            packagename = new String[6];
                        }
                        int n = 0;
                        if (defaultShared.getBoolean(EdgeSetting.IS_PIN_TO_TOP_KEY, false)) {
                            for (int t = 0; t < packagename.length; t++) {
                                if (t < pinnedPackageName.length) {
                                    packagename[t] = pinnedPackageName[t];
                                } else {
                                    packagename[t] = tempPackageName.get(t - pinnedPackageName.length);
                                }

                            }

                        } else {
                            for (int t = 0; t < packagename.length; t++) {
                                if (t + pinnedPackageName.length < packagename.length) {
                                    packagename[t] = tempPackageName.get(t);
                                } else {
                                    packagename[t] = pinnedPackageName[n];
                                    n++;
                                }

                            }
                        }

                        for (int i = 0; i < 6; i++) {
                            if (i >= packagename.length) {
                                iconImageArrayList.get(i).setImageDrawable(null);
                            } else {
                                try {
                                    Drawable defaultDrawable = getPackageManager().getApplicationIcon(packagename[i]);
                                    if (iconPack != null) {
                                        iconImageArrayList.get(i).setImageDrawable(iconPack.getDrawableIconForPackage(packagename[i], defaultDrawable));
                                    } else {
                                        iconImageArrayList.get(i).setImageDrawable(defaultDrawable);

                                    }
                                } catch (PackageManager.NameNotFoundException e) {
                                    Log.e(TAG, "NameNotFound" + e);
                                }
                            }
                        }
                    }
//                    long start = System.currentTimeMillis();
//                    long timestart = start - startDown;
//                    Log.e(TAG, "time from start to get recent = " + timestart);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
                        long currentTimeMillis = System.currentTimeMillis() + 5000;
//                        android.os.SystemClock.uptimeMillis()
                        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, currentTimeMillis - 1000 * 1000, currentTimeMillis);
                        ArrayList<String> tempPackageName = new ArrayList<String>();
                        if (stats != null) {
                            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>(DATE_DECENDING_COMPARATOR);
                            for (UsageStats usageStats : stats) {
                                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                            }
                            Set<Long> setKey = mySortedMap.keySet();
                            Log.e(TAG, "mySortedMap size = " + mySortedMap.size());
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

                            boolean inHome = false;
                            if (tempPackageName.size() > 0) {
                                if (tempPackageName.contains(launcherPackagename) && tempPackageName.get(0).equalsIgnoreCase(launcherPackagename)) {
                                    inHome = true;
                                }
                                tempPackageName.remove(0);
                                if (!inHome && tempPackageName.contains(launcherPackagename)) {
                                    tempPackageName.remove(launcherPackagename);
                                }
                            }

                            if (tempPackageName.size() < 6 && savedPackage !=null) {
                                for (int i = 0; i < savedPackage.length; i++) {
                                    if (!tempPackageName.contains(savedPackage[i]) && tempPackageName.size() < 6) {
                                        tempPackageName.add(savedPackage[i]);
                                    }
                                }

                            }



//                            if (hasKeyInFuture) {
//                                if (tempPackageName.size() >= 2) {
//                                    lastAppPackageName = tempPackageName.get(1);
//                                }
//                            } else {
//                                if (tempPackageName.size() >= 1) {
//                                    lastAppPackageName = tempPackageName.get(0);
//                                }
//                            }
                            if (tempPackageName.size() >= 1) {
                                lastAppPackageName = tempPackageName.get(0);
                            }
                            for (String t : pinnedSet) {
                                if (tempPackageName.contains(t)) {
                                    tempPackageName.remove(t);
                                }
                            }
                            if (6 - tempPackageName.size() - pinnedPackageName.length > 0) {
                                packagename = new String[tempPackageName.size() + pinnedPackageName.length];
                            } else {
                                packagename = new String[6];
                            }
                            int n = 0;
                            if (defaultShared.getBoolean(EdgeSetting.IS_PIN_TO_TOP_KEY, false)) {
                                for (int t = 0; t < packagename.length; t++) {
                                    if (t < pinnedPackageName.length) {
                                        packagename[t] = pinnedPackageName[t];
                                    } else {
                                        packagename[t] = tempPackageName.get(t - pinnedPackageName.length);
                                    }

                                }
                            } else {
                                for (int t = 0; t < packagename.length; t++) {
                                    if (t + pinnedPackageName.length < packagename.length) {
                                        packagename[t] = tempPackageName.get(t);
                                    } else {
                                        packagename[t] = pinnedPackageName[n];
                                        n++;
                                    }

                                }
                            }


                            savedPackage = packagename;


//                            packagename = new String[tempPackageName.size()];
//                            tempPackageName.toArray(packagename);
                        } else Log.e(TAG, "erros in mySortedMap");
                        for (int i = 0; i < 6; i++) {
                            if (i >= packagename.length) {
                                iconImageArrayList.get(i).setImageDrawable(null);
                            } else {
                                try {
                                    Drawable defaultDrawable = getPackageManager().getApplicationIcon(packagename[i]);
                                    if (iconPack != null) {
                                        iconImageArrayList.get(i).setImageDrawable(iconPack.getDrawableIconForPackage(packagename[i], defaultDrawable));
                                    } else {
                                        iconImageArrayList.get(i).setImageDrawable(defaultDrawable);

                                    }
                                } catch (PackageManager.NameNotFoundException e) {
                                    Log.e(TAG, "NameNotFound" + e);
                                }
                            }
                        }

                    }
//                    long spendTime = System.currentTimeMillis() - start;
//                    Log.e(TAG, "time to get recent = " + spendTime);
                    if (isOnlyFavorite) {
                        if (delayToSwitchTask == null) {
                            delayToSwitchTask = new DelayToSwitchTask();
                            delayToSwitchTask.switchToShortcut();
                        } else if (delayToSwitchTask.isCancelled()) {
                            delayToSwitchTask = new DelayToSwitchTask();
                            delayToSwitchTask.switchToShortcut();
                        }
                        break;
                    }

                    float xForHomeBackNotiView = x_init_cord - icon_distance_pxl - distance_to_arc_pxl - ovalOffSet - ovalRadiusPlusPxl;
                    float yForHomeBackNotiView = y_init_cord - icon_distance_pxl - distance_to_arc_pxl - ovalOffSet - ovalRadiusPlusPxl;
                    int radiusForHomeBackNotiView = (int) icon_distance_pxl + distance_to_arc_pxl + ovalRadiusPlusPxl;
//                    isStayPermanent = defaultShared.getBoolean(EdgeSetting.IS_ACTIONS_STAY_PERMANENT, false);
                    isStayPermanent = false;
                    if (!defaultShared.getString(EdgeSetting.ACTION_4_KEY, MainActivity.ACTION_NOTI).equals(MainActivity.ACTION_NONE)) {
                        action4View = new ExpandStatusBarView(getApplicationContext(), radiusForHomeBackNotiView, ovalOffSet, position, 4);
                        action4View.setX(xForHomeBackNotiView);
                        action4View.setY(yForHomeBackNotiView);
                        if (isStayPermanent) {
                            action4View.drawBackground(false);
                        } else {
                            action4View.setVisibility(View.INVISIBLE);
                        }
                        itemView.addView(action4View);
                    }

                    if (!defaultShared.getString(EdgeSetting.ACTION_1_KEY, MainActivity.ACTION_INSTANT_FAVO).equals(MainActivity.ACTION_NONE)) {
                        action1View = new ExpandStatusBarView(getApplicationContext(), radiusForHomeBackNotiView, ovalOffSet, position, 1);
                        action1View.setX(xForHomeBackNotiView);
                        action1View.setY(yForHomeBackNotiView);
                        if (isStayPermanent) {
                            action1View.drawBackground(false);
                        } else {
                            action1View.setVisibility(View.INVISIBLE);
                        }
                        itemView.addView(action1View);
                    }


                    if (!defaultShared.getString(EdgeSetting.ACTION_2_KEY, MainActivity.ACTION_HOME).equals(MainActivity.ACTION_NONE)) {
                        action2View = new ExpandStatusBarView(getApplicationContext(), radiusForHomeBackNotiView, ovalOffSet, position, 2);
                        action2View.setX(xForHomeBackNotiView);
                        action2View.setY(yForHomeBackNotiView);
                        if (isStayPermanent) {
                            action2View.drawBackground(false);
                        } else {
                            action2View.setVisibility(View.INVISIBLE);
                        }
                        itemView.addView(action2View);
                    }


                    if (!defaultShared.getString(EdgeSetting.ACTION_3_KEY, MainActivity.ACTION_BACK).equals(MainActivity.ACTION_NONE)) {
                        action3View = new ExpandStatusBarView(getApplicationContext(), radiusForHomeBackNotiView, ovalOffSet, position, 3);
                        action3View.setX(xForHomeBackNotiView);
                        action3View.setY(yForHomeBackNotiView);
                        if (isStayPermanent) {
                            action3View.drawBackground(false);
                        } else {
                            action3View.setVisibility(View.INVISIBLE);
                        }
                        itemView.addView(action3View);
                    }


                    WindowManager.LayoutParams itemViewParameter = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.TYPE_PHONE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                            PixelFormat.TRANSLUCENT);
                    itemViewParameter.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
//                    try {
//                        windowManager.addView(itemView, itemViewParameter);
//
//                    }catch (IllegalStateException e){
//                        Log.e(TAG," item_view has already been added to the window manager");
//                    }
//                    Utility.setIconsPosition(iconImageList, x_init_cord, y_init_cord, icon_distance_pxl, icon_24dp_in_pxls, position);


                    if (packagename!= null && packagename.length == 0) {
                            delayToSwitchTask = new DelayToSwitchTask();
                            if (mode == 3) {
                                numOfIcon = iconImageArrayList.size();
                                x = new int[numOfIcon];
                                y = new int[numOfIcon];
                                try {
                                    windowManager.addView(itemView, itemViewParameter);
                                } catch (IllegalStateException e) {
                                    Log.e(TAG, " item_view has already been added to the window manager");
                                }
                                delayToSwitchTask.switchToCircleShortcut();
                                Log.e(TAG,"Switch to circle favorite");
                            } else {
                                delayToSwitchTask.switchToShortcut();
                            }
//                        } else if (delayToSwitchTask.isCancelled()) {
//                            delayToSwitchTask = new DelayToSwitchTask();
//                            delayToSwitchTask.switchToShortcut();
//                        }
                    } else {
                        numOfIcon = iconImageArrayList.size();
                        x = new int[numOfIcon];
                        y = new int[numOfIcon];
//                        for (int i = 0; i < numOfIcon; i++) {
//                            x[i] = (int) iconImageArrayList.get(i).getX();
//                            y[i] = (int) iconImageArrayList.get(i).getY();
//                        }
                        try {
                            windowManager.addView(itemView, itemViewParameter);
//                            for (AppCompatImageView icon : iconImageList) {
//                                icon.setAlpha(0f);
//                                icon.animate().alpha(1f).setDuration(150);
//                            }
                        } catch (IllegalStateException e) {
                            Log.e(TAG, " item_view has already been added to the window manager");
                        }
                    }
                    iconIdBackgrounded = -2;
//                    long drawTime = System.currentTimeMillis() - start - spendTime;
//                    long totalTime = System.currentTimeMillis() - startDown;
//                    Log.e(TAG, " time to draw = " + drawTime);
//                    Log.e(TAG, "finish action down at " + System.currentTimeMillis());
//                    Log.e(TAG, "total time in action down = " + totalTime);

                    break;


                case MotionEvent.ACTION_UP:


                    if (switched) {
                        int shortcutToSwitch;
                        Shortcut shortcut;
                        if (mode == 3 && !onInstantFavo ) {
                            shortcutToSwitch = Utility.findIconToSwitchNew(x, y, x_cord, y_cord, icon_24dp_in_pxls * mIconScale, mScale);
                            shortcut = circleFavoRealm.where(Shortcut.class).equalTo("id", shortcutToSwitch).findFirst();
                            int homeBackNoti = Utility.isHomeOrBackOrNoti(x_init_cord, y_init_cord, x_cord, y_cord, icon_distance, mScale, position);
                            Log.e(TAG, "homeBackNoti = " + homeBackNoti);
                            String action = MainActivity.ACTION_NONE;
                            switch (homeBackNoti) {
                                case 1:
                                    action = defaultShared.getString(EdgeSetting.ACTION_1_KEY, MainActivity.ACTION_INSTANT_FAVO);
                                    break;
                                case 2:
                                    action = defaultShared.getString(EdgeSetting.ACTION_2_KEY, MainActivity.ACTION_HOME);
                                    break;
                                case 3:
                                    action = defaultShared.getString(EdgeSetting.ACTION_3_KEY, MainActivity.ACTION_BACK);
                                    break;
                                case 4:
                                    action = defaultShared.getString(EdgeSetting.ACTION_4_KEY, MainActivity.ACTION_NOTI);
                            }
                            if (action.equals(MainActivity.ACTION_NOTI) & isFreeVersion & isOutOfTrial) {
                                Toast.makeText(getApplicationContext(), getString(R.string.edge_service_out_of_trial_text_when_homebacknoti), Toast.LENGTH_LONG).show();
                            } else {
                                Log.e(TAG, "Action = " + action);
                                Utility.executeAction(getApplicationContext(), action, v, getClass().getName(), getPackageName(), lastAppPackageName);
                            }
                        } else {
                            if (!folderShown) {
                                shortcutToSwitch = Utility.findShortcutToSwitch(x_cord, y_cord, (int) shortcutGridView.getX(), (int) shortcutGridView.getY(), (int) (GRID_ICON_SIZE * mIconScale) + GRID_2_PADDING, mScale, gridRow, gridColumn, gridGap, false);
                                shortcut = favoriteRealm.where(Shortcut.class).equalTo("id", shortcutToSwitch).findFirst();
                            } else {
                                shortcutToSwitch = Utility.findShortcutToSwitch(x_cord, y_cord,folderCoor[0] , folderCoor[1], (int) (GRID_ICON_SIZE * mIconScale) + GRID_2_PADDING, mScale, folderCoor[2], folderCoor[3], 5, true);
                                shortcut = favoriteRealm.where(Shortcut.class).equalTo("id", (folderCoor[4]+1)*1000 + shortcutToSwitch ).findFirst();
                            }

                        }
                        if (shortcut != null) {
                            Utility.startShortcut(getApplicationContext(),shortcut,v,getClass().getName(),getPackageName(),lastAppPackageName, defaultShared.getInt(EdgeSetting.CONTACT_ACTION, 0));
                        } else if (shortcutToSwitch >=0 && !folderShown) {
                            Toast.makeText(getApplicationContext(), getString(R.string.please_add_favorite_item), Toast.LENGTH_LONG).show();
                            if (onInstantFavo) {
                                showAddFavoriteDialog(1);
                            } else {
                                showAddFavoriteDialog(mode);
                            }

                        }
                    } else {
//                        int packageToSwitch = Utility.findIconToSwitch(x, y, x_cord, y_cord, numOfIcon, icon_rad, mScale);
                        int packageToSwitch = Utility.findIconToSwitchNew(x, y, x_cord, y_cord, icon_24dp_in_pxls * mIconScale, mScale);
                        if (packageToSwitch != -1) {
                            Intent extApp = null;
                            if (packageToSwitch < packagename.length) {
                                extApp = getPackageManager().getLaunchIntentForPackage(packagename[packageToSwitch]);
                            }

                            if (extApp != null) {
                                if (packagename[packageToSwitch].equals("com.devhomc.search")) {
                                    extApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(extApp);
                                } else {
                                    ComponentName componentName = extApp.getComponent();
                                    Intent startAppIntent = new Intent(Intent.ACTION_MAIN);
                                    startAppIntent.setComponent(componentName);
                                    startAppIntent.addFlags(1064960);
                                    startAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startAppIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startAppIntent.setFlags(270532608 | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startAppIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                                    startActivity(startAppIntent);
//                                startActivity(startApp);
                                }

                                Log.e(TAG, "packageToSwitch = " + packageToSwitch);
                            } else Log.e(TAG, "extApp = null ");

                        }
                        packagename = null;
                        int homeBackNoti = Utility.isHomeOrBackOrNoti(x_init_cord, y_init_cord, x_cord, y_cord, icon_distance, mScale, position);
                        Log.e(TAG, "homeBackNoti = " + homeBackNoti);
                        String action = MainActivity.ACTION_NONE;
                        switch (homeBackNoti) {
                            case 1:
                                action = defaultShared.getString(EdgeSetting.ACTION_1_KEY, MainActivity.ACTION_INSTANT_FAVO);
                                break;
                            case 2:
                                action = defaultShared.getString(EdgeSetting.ACTION_2_KEY, MainActivity.ACTION_HOME);
                                break;
                            case 3:
                                action = defaultShared.getString(EdgeSetting.ACTION_3_KEY, MainActivity.ACTION_BACK);
                                break;
                            case 4:
                                action = defaultShared.getString(EdgeSetting.ACTION_4_KEY, MainActivity.ACTION_NOTI);
                        }
                        if (action.equals(MainActivity.ACTION_NOTI) & isFreeVersion & isOutOfTrial) {
                            Toast.makeText(getApplicationContext(), getString(R.string.edge_service_out_of_trial_text_when_homebacknoti), Toast.LENGTH_LONG).show();
                        } else {
                            Log.e(TAG, "Action = " + action);
                            Utility.executeAction(getApplicationContext(), action, v, getClass().getName(), getPackageName(), lastAppPackageName);
                        }


                    }
                    removeAllExceptEdgeView();
                    clearActionView();
                    switched = false;
                    touched = false;
                    if (delayToSwitchTask != null) {
                        delayToSwitchTask.cancel(true);
                    }
                    isClockShown = false;
                    break;


                case MotionEvent.ACTION_MOVE:

                    if (!isClockShown) {
                        Log.e(TAG, "Show clock");
                        clockView = Utility.disPlayClock(getApplicationContext(), windowManager, defaultShared.getBoolean(EdgeSetting.ANIMATION_KEY, false), defaultShared.getInt(EdgeSetting.ANI_TIME_KEY, 100), defaultShared.getBoolean(EdgeSetting.DISABLE_CLOCK_KEY, false));
                        LinearLayout clock = (LinearLayout) clockView.findViewById(R.id.clock_linear_layout);
                        FrameLayout indicator = (FrameLayout) clockView.findViewById(R.id.indicator_frame_layout);
                        indicator.setVisibility(View.GONE);
                        circle = (Circle) clockView.findViewById(R.id.circle);
                        if (circle == null) {
                            Log.e(TAG, "circle = null");
                        }
                        isClockShown = true;
                    }
                    if (switched) {
                        final int shortcutToSwitch;
                        if (mode == 3 && ! onInstantFavo) {
                                if (iconIdBackgrounded == -2) {
                                    for (int i = 0; i < numOfIcon; i++) {
                                        if (x == null) {
                                            x = new int[numOfIcon];
                                        }
                                        if (y == null) {
                                            y = new int[numOfIcon];
                                        }
                                        x[i] = (int) iconImageArrayList.get(i).getX();
                                        y[i] = (int) iconImageArrayList.get(i).getY();
                                    }
                                }

                            shortcutToSwitch = Utility.findIconToSwitchNew(x, y, x_cord, y_cord, icon_24dp_in_pxls * mIconScale, mScale);
                            int moveToHomeBackNoti = Utility.isHomeOrBackOrNoti(x_init_cord, y_init_cord, x_cord, y_cord, icon_distance, mScale, position);
                            if (moveToHomeBackNoti != -1 | shortcutToSwitch != -1) {
                                if (shortcutToSwitch != -1) {
                                    activateId = shortcutToSwitch + 1000;
                                } else {
                                    activateId = moveToHomeBackNoti + 2000;
                                }
                            } else {
                                activateId = 0;
                                clearIndicator(activatedId);
                                activatedId = 0;
                            }


                            if (shortcutToSwitch == -1) {
                                clearIconBackground();
                            }
                            if (shortcutToSwitch != -1 && shortcutToSwitch < iconImageArrayList.size() && iconIdBackgrounded != shortcutToSwitch) {
                                clearIconBackground();
                                MyImageView iconHighlight = iconImageArrayList.get(shortcutToSwitch);
                                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(iconHighlight.getLayoutParams());
                                float x = iconHighlight.getX();
                                float y = iconHighlight.getY();

                                layoutParams.height = (int) ((16 + 48 * mIconScale) * mScale);
                                layoutParams.width = (int) ((28 + 48 * mIconScale) * mScale);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    iconHighlight.setBackground(getDrawable(R.drawable.icon_background));
                                } else {
                                    iconHighlight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_background));
                                }
                                iconHighlight.setX(x - 14 * mScale);
                                iconHighlight.setY(y - 8 * mScale);
                                iconHighlight.setLayoutParams(layoutParams);
                                iconHighlight.setPadding(iconPaddingLeft, iconPaddingTop, iconPaddingLeft, iconPaddingTop);
                                iconIdBackgrounded = shortcutToSwitch;

                            }
                            if (moveToHomeBackNoti != -1) {
                                setQuicActionView(moveToHomeBackNoti);
                                if (useInstantFavo && moveToHomeBackNoti > 0 && instantFavoAction[moveToHomeBackNoti -1] == 1 ) {
                                    delayToSwitchTask = new DelayToSwitchTask();
                                    onInstantFavo = true;
                                    delayToSwitchTask.switchToShortcut();
                                }
                            }


                        } else {
                            if (!folderShown) {
                                shortcutToSwitch = Utility.findShortcutToSwitch(x_cord, y_cord, gridX, gridY, (int) (GRID_ICON_SIZE * mIconScale) + GRID_2_PADDING, mScale, gridRow, gridColumn, gridGap,false);
                                if (shortcutToSwitch != -1) {
                                    Shortcut shortcut = favoriteRealm.where(Shortcut.class).equalTo("id", shortcutToSwitch).findFirst();
                                    if (shortcut != null && shortcut.getType() == Shortcut.TYPE_FOLDER && !folderShown && !onFolderAnimator) {
                                        positionOfFolder = shortcutToSwitch;
                                        folderAdapter = new FolderAdapter(getApplicationContext(),shortcutToSwitch);
                                        folderAnimator = shortcutGridView.animate().setDuration(holdTime + 200).alpha(0f).setListener(new Animator.AnimatorListener() {
                                            boolean isCancel = false;
                                            CircleAngleAnimation angleAnimation;
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                onFolderAnimator = true;
                                                circle.setVisibility(View.VISIBLE);
                                                circle.setAngle(0);
                                                angleAnimation = new CircleAngleAnimation(circle, 270);
                                                angleAnimation.setDuration(holdTime + 200);
                                                circle.startAnimation(angleAnimation);

                                            }

                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                if (!isCancel) {
                                                    folderCoor = Utility.showFolder(getApplicationContext(), shortcutGridView, windowManager, favoriteRealm, defaultShared, shortcutToSwitch, mScale, mIconScale,folderAdapter);
                                                    onFolderAnimator = false;
                                                    Log.e(TAG, "onAnimation end");
                                                    folderShown = true;
                                                    circle.setVisibility(View.GONE);
                                                    circle.setAngle(0);
                                                }

                                            }

                                            @Override
                                            public void onAnimationCancel(Animator animation) {
                                                shortcutGridView.setVisibility(View.VISIBLE);
                                                shortcutGridView.setAlpha(1f);
                                                shortcutFolderGrid.setVisibility(View.GONE);
                                                folderShown = false;
                                                onFolderAnimator = false;
                                                isCancel = true;
                                                angleAnimation.cancel();
                                                circle.setAngle(0);
                                                circle.setVisibility(View.GONE);
                                                Log.e(TAG, "onAnimation cancel");

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animator animation) {

                                            }
                                        });


                                    }
                                    activateId = shortcutToSwitch + 100;
                                } else {
                                    activateId = 0;
                                    clearIndicator(activatedId);
                                    activatedId = 0;
                                }

                                if (onFolderAnimator && positionOfFolder != shortcutToSwitch) {
                                    folderAnimator.cancel();
                                    Log.e(TAG, "Cancel opening folder");
                                }

                                if (shortcutAdapter != null) {
                                    if (shortcutToSwitch != -1 && shortcutToSwitch != preShortcutToSwitch) {
                                        shortcutAdapter.setBackground(shortcutToSwitch);
                                        isShortcutBackgroundNull = true;
                                        preShortcutToSwitch = shortcutToSwitch;
                                    } else if (isShortcutBackgroundNull && shortcutToSwitch == -1) {
                                        shortcutAdapter.setBackground(shortcutToSwitch);
                                        isShortcutBackgroundNull = false;
                                        preShortcutToSwitch = -1;
                                    }
                                }
                            } else {

                                if (!onFolderAnimator && folderCoor !=null) {
                                    shortcutToSwitch = Utility.findShortcutToSwitch(x_cord, y_cord, folderCoor[0], folderCoor[1], (int) (GRID_ICON_SIZE * mIconScale) + GRID_2_PADDING, mScale, folderCoor[2], folderCoor[3], 5, true);
                                    if (shortcutToSwitch != -1) {
                                        activateId = shortcutToSwitch + 3000;
                                    } else {
                                        activateId = 0;
                                        clearIndicator(activatedId);
                                        activatedId = 0;
                                    }

                                    if (folderAdapter != null) {
                                        if (shortcutToSwitch != -1 && shortcutToSwitch != preShortcutInFolderToSwitch) {
                                            folderAdapter.setBackground(shortcutToSwitch);
                                            isShortcutBackgroundInFolderNull = true;
                                            preShortcutInFolderToSwitch = shortcutToSwitch;
                                        } else if (isShortcutBackgroundInFolderNull && shortcutToSwitch == -1) {
                                            folderAdapter.setBackground(shortcutToSwitch);
                                            isShortcutBackgroundInFolderNull = false;
                                            preShortcutInFolderToSwitch = -1;
                                        }

                                    }
                                    if (shortcutToSwitch == -2) {
                                        Log.e(TAG, "outside folder zone");
                                        shortcutGridView.setVisibility(View.VISIBLE);
                                        shortcutGridView.setAlpha(1f);
                                        shortcutView.findViewById(R.id.folder_grid).setVisibility(View.GONE);
                                        folderShown = false;
                                    }
                                }


                            }
                        }

                    } else {
//                        int iconToSwitch = Utility.findIconToSwitch(x, y, x_cord, y_cord, numOfIcon, icon_rad, mScale);
                        int iconToSwitch = -1;
                        if (!iconImageArrayList.get(0).isOnAnimation()) {
                            if (iconIdBackgrounded == -2) {
                                for (int i = 0; i < numOfIcon; i++) {
                                    x[i] = (int) iconImageArrayList.get(i).getX();
                                    y[i] = (int) iconImageArrayList.get(i).getY();
                                }

                            }

                            iconToSwitch = Utility.findIconToSwitchNew(x, y, x_cord, y_cord, icon_24dp_in_pxls * mIconScale, mScale);
                        }

                        int moveToHomeBackNoti = Utility.isHomeOrBackOrNoti(x_init_cord, y_init_cord, x_cord, y_cord, icon_distance, mScale, position);
                        if (moveToHomeBackNoti > 0) {
                            activateId = moveToHomeBackNoti + 2000;
                        }
                        if (iconToSwitch != -1 && !iconImageArrayList.get(0).isOnAnimation()) {
                            if (iconToSwitch < iconImageArrayList.size() && iconIdBackgrounded != iconToSwitch) {
                                clearIconBackground();
                                MyImageView iconHighlight = iconImageArrayList.get(iconToSwitch);
                                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(iconHighlight.getLayoutParams());
                                float x = iconHighlight.getX();
                                float y = iconHighlight.getY();

                                layoutParams.height = (int) ((16 + 48 * mIconScale) * mScale);
                                layoutParams.width = (int) ((28 + 48 * mIconScale) * mScale);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    iconHighlight.setBackground(getDrawable(R.drawable.icon_background));
                                } else {
                                    iconHighlight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_background));
                                }
                                iconHighlight.setX(x - 14 * mScale);
                                iconHighlight.setY(y - 8 * mScale);
                                iconHighlight.setLayoutParams(layoutParams);
                                iconHighlight.setPadding(iconPaddingLeft, iconPaddingTop, iconPaddingLeft, iconPaddingTop);
                                iconIdBackgrounded = iconToSwitch;

                            }
                            Log.e(TAG, "set activateId = icontoswitch + 20 \nonAnimation = " + iconImageArrayList.get(0).isOnAnimation());

                            activateId = iconToSwitch + 20;

                            if (defaultShared.getBoolean(EdgeSetting.HOLD_TIME_ENABLE_KEY, true) && delayToSwitchTask == null) {
                                delayToSwitchTask = new DelayToSwitchTask();
                                delayToSwitchTask.execute(iconToSwitch);
                            } else if (defaultShared.getBoolean(EdgeSetting.HOLD_TIME_ENABLE_KEY, true) && delayToSwitchTask.isCancelled()) {
                                delayToSwitchTask = new DelayToSwitchTask();
                                delayToSwitchTask.execute(iconToSwitch);
                            }
                            if (activatedId != activateId) {
                                if (delayToSwitchTask != null) {
                                    delayToSwitchTask.cancel(true);
                                }
                            }

                            touched = true;

                        } else {
                            if (moveToHomeBackNoti == 0) {
                                activateId = 0;
                                clearIndicator(activatedId);
                                activatedId = 0;
                            }
                            clearIconBackground();

                            if (delayToSwitchTask != null) {
                                delayToSwitchTask.cancel(true);
                                delayToSwitchTask = null;
                            }
                            touched = false;
                        }
//                        if (iconToSwitch == -1 & moveToHomeBackNoti == 0) {
//                            if (hasOneActive) {
//                                for (ImageView imageView : iconImageArrayList) {
//                                    if (imageView.getColorFilter() != null) {
//                                        imageView.setColorFilter(null);
//                                    }
//                                }
//                            }
//                            hasOneActive = false;
//                        }
                        setQuicActionView(moveToHomeBackNoti);
                        if (useInstantFavo && moveToHomeBackNoti > 0 && instantFavoAction[moveToHomeBackNoti -1] == 1 ) {
                            delayToSwitchTask = new DelayToSwitchTask();
                            onInstantFavo = true;
                            delayToSwitchTask.switchToShortcut();
                        }
                    }
                    if (activateId != 0 && activatedId != activateId) {
                        if (defaultShared.getBoolean(EdgeSetting.HAPTIC_ON_ICON_KEY, false)) {
                            vibrator.vibrate(vibrationDuration);
                        }
                        setIndicator(activateId);
                        activatedId = activateId;
                        activateId = 0;
                    }

                    break;

                case MotionEvent.ACTION_OUTSIDE:
                    removeAllExceptEdgeView();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    removeAllExceptEdgeView();
                    break;
            }
            return true;
        }



        private class DelayToSwitchTask extends AsyncTask<Integer, Void, Void> {
            private boolean isSleepEnough = false;
            private int iconToSwitch = -1;

            @Override
            protected Void doInBackground(Integer... params) {
                isSleepEnough = false;
                iconToSwitch = params[0];
                try {
                    Thread.sleep(holdTime);
                    isSleepEnough = true;
                } catch (InterruptedException e) {
                    Log.e(TAG, "interrupt sleeping");
                }

                return null;
            }

            @Override
            protected void onCancelled(Void aVoid) {
                super.onCancelled(aVoid);
                isSleepEnough = false;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (isSleepEnough & touched) {
                    if (mode == 3) {
                        switchToCircleShortcut();
                    } else {
                        switchToShortcut();
                    }


                }

                super.onPostExecute(aVoid);
            }

            protected void switchToShortcut() {
                clearIconBackground();
                ViewGroup.LayoutParams gridParams = shortcutGridView.getLayoutParams();
                int gridRow = defaultShared.getInt(EdgeSetting.NUM_OF_GRID_ROW_KEY, 5);
                int gridColumn = defaultShared.getInt(EdgeSetting.NUM_OF_GRID_COLUMN_KEY, 4);
                int gridGap = defaultShared.getInt(EdgeSetting.GAP_OF_SHORTCUT_KEY, 5);
                shortcutGridView.setVerticalSpacing((int) (gridGap * mScale));
                shortcutGridView.setNumColumns(gridColumn);
                shortcutGridView.setGravity(Gravity.CENTER);
                int gridDistanceFromEdge = defaultShared.getInt(EdgeSetting.GRID_DISTANCE_FROM_EDGE_KEY, 20);
                int gridDistanceVertical = defaultShared.getInt(EdgeSetting.GRID_DISTANCE_VERTICAL_FROM_EDGE_KEY, 20);
                float gridWide = (int) (mScale * (float) (((GRID_ICON_SIZE * mIconScale) + GRID_2_PADDING) * gridColumn + gridGap * (gridColumn - 1)));
                float gridTall = (int) (mScale * (float) (((GRID_ICON_SIZE * mIconScale) + GRID_2_PADDING) * gridRow + gridGap * (gridRow - 1)));
                gridParams.height = (int) gridTall;
                gridParams.width = (int) gridWide;
                shortcutGridView.setLayoutParams(gridParams);
                shortcutGridView.setAdapter(shortcutAdapter);
                WindowManager.LayoutParams shortcutViewParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        PixelFormat.TRANSLUCENT);
//                shortcutViewParams.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;

                Utility.setFavoriteShortcutGridViewPosition(shortcutGridView, gridTall, gridWide, x_init_cord, y_init_cord, mScale, position, windowManager, defaultShared, gridDistanceFromEdge, gridDistanceVertical, iconToSwitch);
                gridX = (int) shortcutGridView.getX();
                gridY = (int) shortcutGridView.getY();
                shortcutGridView.setVisibility(View.VISIBLE);
                shortcutGridView.setAlpha(1f);
                GridView folderGrid = (GridView) shortcutView.findViewById(R.id.folder_grid);
                folderGrid.setVisibility(View.GONE);
                if (shortcutView != null && !shortcutView.isAttachedToWindow()) {
                    windowManager.addView(shortcutView, shortcutViewParams);
                }
                removeView(itemView);
                switched = true;

//                removeView(clockView);
//                if (clockView != null) {
//                    clockView.findViewById(R.id.clock_linear_layout).setVisibility(View.GONE);
//                }
            }

            protected void switchToCircleShortcut() {
                clearIconBackground();
                Shortcut shortcut;
                for (int i = 0; i < 6; i++) {
                    shortcut = circleFavoRealm.where(Shortcut.class).equalTo("id", i).findFirst();
                    Utility.setShortcutDrawable(shortcut,getApplicationContext(),iconImageArrayList.get(i),iconPack, false);

                }
                switched = true;



            }
        }

        private void clearIconBackground() {
            if (iconIdBackgrounded != -2) {
                if (iconIdBackgrounded < iconImageArrayList.size()) {
                    Log.e(TAG, "Clear Icon Background test test2 test3 test4 test5");
                    ImageView iconResetBackground = iconImageArrayList.get(iconIdBackgrounded);
                    FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(iconResetBackground.getLayoutParams());
                    layoutParams1.width = (int) (48 * mScale * mIconScale);
                    layoutParams1.height = (int) (48 * mScale * mIconScale);
                    float x = iconResetBackground.getX();
                    float y = iconResetBackground.getY();
                    iconResetBackground.setBackground(null);
                    iconResetBackground.setX(x + 14 * mScale);
                    iconResetBackground.setY(y + 8 * mScale);
                    iconResetBackground.setLayoutParams(layoutParams1);
                    iconResetBackground.setPadding(0, 0, 0, 0);
                    iconIdBackgrounded = -2;
                } else iconIdBackgrounded = -2;
            }


        }

        private void setIndicator(int activateId) {
            if (clockView == null) {
                return;
            }
            if (activateId != -1) {
                LinearLayout clock = (LinearLayout) clockView.findViewById(R.id.clock_linear_layout);
                FrameLayout indicator = (FrameLayout) clockView.findViewById(R.id.indicator_frame_layout);
                clock.setVisibility(View.GONE);
                indicator.setVisibility(View.VISIBLE);
                ImageView icon = (ImageView) indicator.findViewById(R.id.indicator_icon);
                TextView label = (TextView) indicator.findViewById(R.id.indicator_label);
                if (activateId - 20 >= 0 && activateId - 20 < 6) {
                    if (activateId - 20 < packagename.length) {
                        icon.setImageDrawable(iconImageArrayList.get(activateId - 20).getDrawable());
                        try {
                            label.setText(getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(packagename[activateId - 20], 0)));
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.e(TAG, "Namenotfound when setIndicator");
                        }
                    } else {
                        icon.setImageDrawable(null);
                        label.setText(null);
                    }
                } else if (activateId - 100 >= 0 && activateId - 100 <= 200) {
                    Shortcut shortcut = favoriteRealm.where(Shortcut.class).equalTo("id",activateId - 100).findFirst();
                    Utility.setShortcutDrawable(shortcut,getApplicationContext(),icon,iconPack, false);
                    if (shortcut != null) {
                        if (shortcut.getType() == Shortcut.TYPE_CONTACT) {
                            label.setText(shortcut.getName());
                        }else if (shortcut.getAction() != Shortcut.ACTION_NONE ) {
                            label.setText(shortcut.getLabel());
                        }else label.setText(null);

                    }else label.setText("");
                } else if (activateId - 1000 >= 0 && activateId - 1000 < 10) {
                    Shortcut shortcut = circleFavoRealm.where(Shortcut.class).equalTo("id", activateId - 1000).findFirst();
                    Utility.setShortcutDrawable(shortcut,getApplicationContext(),icon,iconPack, false);
                    if (shortcut != null) {
                        label.setText(shortcut.getLabel());
                    }else label.setText("");
                } else if (activateId - 2000 >= 0 && activateId - 2000 < 10) {
                    Utility.setIndicatorForQuickAction(defaultShared,getApplicationContext(),activateId-2000,icon, label);
                } else if (activateId - 3000 >= 0 && activateId - 3000 < 20) {
                    Shortcut shortcut = favoriteRealm.where(Shortcut.class).equalTo("id", (positionOfFolder +1)* 1000 + (activateId - 3000)).findFirst();
                    Utility.setShortcutDrawable(shortcut,getApplicationContext(),icon,iconPack, true);
                    if (shortcut == null) {
                        clearIndicator(1);
                        label.setText("");
                    } else {
                            if (shortcut.getType() == Shortcut.TYPE_CONTACT) {
                                label.setText(shortcut.getName());
                            }else if (shortcut.getAction() != Shortcut.ACTION_NONE || shortcut.getType() == Shortcut.TYPE_APP ||
                                    shortcut.getType() == Shortcut.TYPE_ACTION) {
                                label.setText(shortcut.getLabel());
                            } else label.setText(null);


                    }
                }
            }

        }

        private void clearIndicator(int activatedId) {
            if (activatedId != 0 && clockView!=null) {
                LinearLayout clock = (LinearLayout) clockView.findViewById(R.id.clock_linear_layout);
                FrameLayout indicator = (FrameLayout) clockView.findViewById(R.id.indicator_frame_layout);
                if (switched) {
                    clock.setVisibility(View.GONE);
                    indicator.setVisibility(View.INVISIBLE);
                } else {
                    indicator.setVisibility(View.INVISIBLE);
                }

            }
        }

        private void setQuicActionView(int moveToHomeBackNoti) {
            if (!isStayPermanent) {
                switch (moveToHomeBackNoti) {
                    case 0:
                        if (action1View != null) {
                            action1View.setVisibility(View.INVISIBLE);
                        }
                        if (action2View != null) {
                            action2View.setVisibility(View.INVISIBLE);
                        }
                        if (action3View != null) {
                            action3View.setVisibility(View.INVISIBLE);
                        }
                        if (action4View != null) {
                            action4View.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case 1:
                        if (action1View != null) {
                            action1View.setVisibility(View.VISIBLE);
                        }
                        if (action2View != null) {
                            action2View.setVisibility(View.INVISIBLE);
                        }
                        if (action3View != null) {
                            action3View.setVisibility(View.INVISIBLE);
                        }
                        if (action4View != null) {
                            action4View.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case 2:
                        if (action1View != null) {
                            action1View.setVisibility(View.INVISIBLE);
                        }
                        if (action2View != null) {
                            action2View.setVisibility(View.VISIBLE);
                        }
                        if (action3View != null) {
                            action3View.setVisibility(View.INVISIBLE);
                        }
                        if (action4View != null) {
                            action4View.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case 3:
                        if (action1View != null) {
                            action1View.setVisibility(View.INVISIBLE);
                        }
                        if (action2View != null) {
                            action2View.setVisibility(View.INVISIBLE);
                        }
                        if (action3View != null) {
                            action3View.setVisibility(View.VISIBLE);
                        }
                        if (action4View != null) {
                            action4View.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case 4:
                        if (action1View != null) {
                            action1View.setVisibility(View.INVISIBLE);
                        }
                        if (action2View != null) {
                            action2View.setVisibility(View.INVISIBLE);
                        }
                        if (action3View != null) {
                            action3View.setVisibility(View.INVISIBLE);
                        }
                        if (action4View != null) {
                            action4View.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            } else {
                switch (moveToHomeBackNoti) {
                    case 0:
                        if (action1View != null) {
                            action1View.drawBackground(false);
                        }
                        if (action2View != null) {
                            action2View.drawBackground(false);
                        }
                        if (action3View != null) {
                            action3View.drawBackground(false);
                        }
                        if (action4View != null) {
                            action4View.drawBackground(false);
                        }
                        break;
                    case 1:
                        if (action1View != null) {
                            action1View.drawBackground(true);
                        }
                        if (action2View != null) {
                            action2View.drawBackground(false);
                        }
                        if (action3View != null) {
                            action3View.drawBackground(false);
                        }
                        if (action4View != null) {
                            action4View.drawBackground(false);
                        }
                        break;
                    case 2:
                        if (action1View != null) {
                            action1View.drawBackground(false);
                        }
                        if (action2View != null) {
                            action2View.drawBackground(true);
                        }
                        if (action3View != null) {
                            action3View.drawBackground(false);
                        }
                        if (action4View != null) {
                            action4View.drawBackground(false);
                        }
                        break;
                    case 3:
                        if (action1View != null) {
                            action1View.drawBackground(false);
                        }
                        if (action2View != null) {
                            action2View.drawBackground(false);
                        }
                        if (action3View != null) {
                            action3View.drawBackground(true);
                        }
                        if (action4View != null) {
                            action4View.drawBackground(false);
                        }
                        break;
                    case 4:
                        if (action1View != null) {
                            action1View.drawBackground(false);
                        }
                        if (action2View != null) {
                            action2View.drawBackground(false);
                        }
                        if (action3View != null) {
                            action3View.drawBackground(false);
                        }
                        if (action4View != null) {
                            action4View.drawBackground(true);
                        }
                        break;
                }
            }
        }

        private synchronized void clearActionView() {
            if (action1View != null) {
                itemView.removeView(action1View);
            }
            if (action2View != null) {
                itemView.removeView(action2View);
            }
            if (action3View != null) {
                itemView.removeView(action3View);
            }
            if (action4View != null) {
                itemView.removeView(action4View);
            }
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (defaultShared.getBoolean(EdgeSetting.IS_DISABLE_IN_LANSCAPE, false) && newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            removeEdgeImage();
        } else {
            if (isEdge1On && edge1Image !=null && !edge1Image.isAttachedToWindow()) {
                windowManager.addView(edge1Image,paramsEdge1);
            }
            if (isEdge2On && edge2Image !=null && !edge2Image.isAttachedToWindow()) {
                windowManager.addView(edge2Image,paramsEdge2);
            }
        }
        Log.e(TAG, "onConfigurationChanged: ");
        super.onConfigurationChanged(newConfig);
        if (item1View != null && item1View.isAttachedToWindow()) {
            if (action1View != null) {
                item1View.removeView(action2View);
            }
            if (action2View != null) {
                item1View.removeView(action1View);
            }
            if (action3View != null) {
                item1View.removeView(action4View);
            }
            if (action4View != null) {
                item1View.removeView(action3View);
            }
        }
        if (item2View != null && item2View.isAttachedToWindow()) {
            if (action1View != null) {
                item2View.removeView(action2View);
            }
            if (action2View != null) {
                item2View.removeView(action1View);
            }
            if (action3View != null) {
                item2View.removeView(action4View);
            }
            if (action4View != null) {
                item2View.removeView(action3View);
            }
        }
        removeAllExceptEdgeView();

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
        edge1Length = sharedPreferences1.getInt(EdgeSetting.EDGE_LENGTH_KEY, 150);
        edge2Length = sharedPreferences2.getInt(EdgeSetting.EDGE_LENGTH_KEY, 150);
        edge1offset = sharedPreferences1.getInt(EdgeSetting.EDGE_OFFSET_KEY, 0);
        edge2offset = sharedPreferences2.getInt(EdgeSetting.EDGE_OFFSET_KEY, 0);
        edge1Sensivite = sharedPreferences1.getInt(EdgeSetting.EDGE_SENSIIVE_KEY, 12);
        edge2Sensitive = sharedPreferences2.getInt(EdgeSetting.EDGE_SENSIIVE_KEY, 12);
        isEdge1On = sharedPreferences1.getBoolean(EdgeSetting.EDGE_ON_KEY, true);
        isEdge2On = sharedPreferences2.getBoolean(EdgeSetting.EDGE_ON_KEY, false);
        spinnerEntries = getResources().getStringArray(R.array.edge_dialog_spinner_array);
        icon_distance = defaultShared.getInt(EdgeSetting.ICON_DISTANCE_KEY, 105);
        ovalOffSet = (int) (ovalOffSetInDp * mScale);
        ovalRadiusPlusPxl = (int) (ovalRadiusPlus * mIconScale * mScale);
        numOfRecent = defaultShared.getInt(EdgeSetting.NUM_OF_RECENT_KEY, 6);
        gridRow = defaultShared.getInt(EdgeSetting.NUM_OF_GRID_ROW_KEY, 5);
        gridColumn = defaultShared.getInt(EdgeSetting.NUM_OF_GRID_COLUMN_KEY, 4);
        gridGap = defaultShared.getInt(EdgeSetting.GAP_OF_SHORTCUT_KEY, 5);
        holdTime = defaultShared.getInt(EdgeSetting.HOLD_TIME_KEY, 600);
        vibrationDuration = defaultShared.getInt(EdgeSetting.VIBRATION_DURATION_KEY, 15);
        iconPaddingLeft = (int) (14 * mScale);
        iconPaddingTop = (int) (8 * mScale);
        edge1Position = Utility.getPositionIntFromString(sharedPreferences1.getString(EdgeSetting.EDGE_POSITION_KEY, spinnerEntries[1]), getApplicationContext()); // default =1
        edge2Position = Utility.getPositionIntFromString(sharedPreferences2.getString(EdgeSetting.EDGE_POSITION_KEY, spinnerEntries[5]), getApplicationContext());
        pinAppRealm = Realm.getInstance(new RealmConfiguration.Builder(getApplicationContext())
                .name("pinApp.realm")
                .schemaVersion(CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        circleFavoRealm = Realm.getInstance(new RealmConfiguration.Builder(getApplicationContext())
                .name("circleFavo.realm")
                .schemaVersion(CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        favoriteRealm = Realm.getInstance(new RealmConfiguration.Builder(getApplicationContext())
                .name("default.realm")
                .schemaVersion(CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        backgroundColor = defaultShared.getInt(EdgeSetting.BACKGROUND_COLOR_KEY, 1879048192);
//        guideColor = defaultShared.getInt(EdgeSetting.GUIDE_COLOR_KEY,16728193);
        guideColor = defaultShared.getInt(EdgeSetting.GUIDE_COLOR_KEY, Color.argb(255, 255, 64, 129));
//        guideColor = defaultShared.getInt(EdgeSetting.GUIDE_COLOR_KEY, Color.argb(255, 40, 92, 161));
        shortcutAdapter = new FavoriteShortcutAdapter(getApplicationContext());
        circltShortcutAdapter = new CircleFavoriteAdapter(getApplicationContext());
//        Random r = new Random();
//        serviceId = r.nextInt(1000);
        mIconScale = defaultShared.getFloat(EdgeSetting.ICON_SCALE, 1f);
//        defaultShared.edit().putInt(EdgeSetting.SERVICE_ID, serviceId).commit();
        RealmResults<Shortcut> results1 =
                pinAppRealm.where(Shortcut.class).findAll();
        if (isFreeVersion && isOutOfTrial) {
            results1 = null;
        }
        int i = 0;
        if (results1 == null) {
            pinnedPackageName = new String[0];
        } else {
            pinnedPackageName = new String[results1.size()];
            for (Shortcut shortcut : results1) {
                Log.e(TAG, "result = " + shortcut.getPackageName());
                pinnedPackageName[i] = shortcut.getPackageName();
                Log.e(TAG, "pinnedPack = " + pinnedPackageName[0]);
                i++;
            }
        }
        pinnedSet = new HashSet<String>(Arrays.asList(pinnedPackageName));
        animationTime = defaultShared.getInt(EdgeSetting.ANI_TIME_KEY, 100);
        edge1mode = sharedPreferences1.getInt(EdgeSetting.CIRCLE_FAVORITE_MODE, 0);
        if (edge1mode == 0) {
            if (sharedPreferences1.getBoolean(EdgeSetting.IS_ONLY_FAVORITE_KEY, false)) {
                edge1mode = 2;
            } else {
                edge1mode = 1;
            }
        }

        edge2mode = sharedPreferences2.getInt(EdgeSetting.CIRCLE_FAVORITE_MODE, 0);
        if (edge2mode == 0) {
            if (sharedPreferences2.getBoolean(EdgeSetting.IS_ONLY_FAVORITE_KEY, false)) {
                edge2mode = 2;
            } else {
                edge2mode = 1;
            }
        }
        Log.e(TAG, "onCreate service" + "\nEdge1 on = " + isEdge1On + "\nEdge2 on = " + isEdge2On +
                "\nEdge1 position = " + edge1Position + "\nEdge2 positon = " + edge2Position
                + "\nMode = " + edge1mode);
        useInstantFavo = false;
        instantFavoAction = new int[4];
        if (defaultShared.getString(EdgeSetting.ACTION_1_KEY, MainActivity.ACTION_INSTANT_FAVO).equalsIgnoreCase(MainActivity.ACTION_INSTANT_FAVO)) {
            instantFavoAction[0] = 1;
            useInstantFavo = true;
        }else instantFavoAction[0] = -1;
        if (defaultShared.getString(EdgeSetting.ACTION_2_KEY, MainActivity.ACTION_HOME).equalsIgnoreCase(MainActivity.ACTION_INSTANT_FAVO)) {
            instantFavoAction[1] = 1;
            useInstantFavo = true;
        }else instantFavoAction[1] = -1;
        if (defaultShared.getString(EdgeSetting.ACTION_3_KEY, MainActivity.ACTION_BACK).equalsIgnoreCase(MainActivity.ACTION_INSTANT_FAVO)) {
            instantFavoAction[2] = 1;
            useInstantFavo = true;
        }else instantFavoAction[2] = -1;
        if (defaultShared.getString(EdgeSetting.ACTION_4_KEY, MainActivity.ACTION_NOTI).equalsIgnoreCase(MainActivity.ACTION_INSTANT_FAVO)) {
            instantFavoAction[3] = 1;
            useInstantFavo = true;
        }else instantFavoAction[3] = -1;


    }

    @Override
    public void onDestroy() {
        removeAll();
        super.onDestroy();
    }

    public final synchronized void removeView(View view) {
        try {
            windowManager.removeView(view);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove View");
        }
    }

    public final synchronized void removeAll() {
        Log.e(TAG, "remove all view");
        try {
            windowManager.removeView(edge1Image);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove edge1Image");
        }
        try {
            windowManager.removeView(edge2Image);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove edge2Image");
        }
        try {
            windowManager.removeView(backgroundFrame);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove backgroundFrame");
        }
        try {
            windowManager.removeView(clockView);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove clockView");
        }
        try {
            windowManager.removeView(shortcutView);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove shortcutView");
        }
        try {
            windowManager.removeView(item1View);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove item1View");
        }
        try {
            windowManager.removeView(item2View);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove item2View");
        }

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
            windowManager.removeView(clockView);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove clockView");
        }
        try {
            windowManager.removeView(shortcutView);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove shortcutView");
        }
        try {
            windowManager.removeView(item1View);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove item1View");
        }
        try {
            windowManager.removeView(item2View);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove item2View");
        }
    }

    public final synchronized void removeEdgeImage() {
        Log.e(TAG, "removeEdgeImage: ");
        try {
            windowManager.removeView(edge1Image);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove edge1Image");
        }
        try {
            windowManager.removeView(edge2Image);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Null when remove edge2Image");
        }
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

    private int getXOffset(int x_init) {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        int distanceNeeded = (int) (mScale * (icon_distance + icon_rad));
        int distanceWeHave = point.x - x_init;
        if (distanceWeHave < distanceNeeded) {
            return distanceNeeded - distanceWeHave;
        } else if (x_init < distanceNeeded) {
            return x_init - distanceNeeded;
        } else return 0;
    }


    public void showAddFavoriteDialog(int mode) {
        Intent intent = new Intent(getApplicationContext(),FavoriteSettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("mode", mode);
        startActivity(intent);
    }


//    public static class BootCompleteReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            context.startService(new Intent(context, EdgeGestureService.class));
//        }
//    }

    public static class BootCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.startService(new Intent(context, EdgeGestureService.class));
        }
    }


}
