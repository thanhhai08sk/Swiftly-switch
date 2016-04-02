package org.de_studio.recentappswitcher.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
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
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.favoriteShortcut.SetFavoriteShortcutActivity;
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
    float mScale;
    static final String LOG_TAG = EdgeGestureService.class.getSimpleName();
    static final int EDGE_GESTURE_NOTIFICAION_ID = 10;
    private WindowManager windowManager;
    private RelativeLayout edge1View;
    private RelativeLayout edge2View;
    private ImageView edge1Image;
    private ImageView edge2Image;
    private FrameLayout item1View, item2View, shortcutView,backgroundFrame;
    public int icon_height = 48;
    public int icon_width = 48, icon_rad = 24;
    public int icon_distance = 110, distance_to_arc = 35, distance_to_arc_pxl;
    public float icon_distance_pxl, icon_24dp_in_pxls;
    public int edge1Length, edge2Length, edge1offset, edge2offset;
    public int edge1HeightPxl, edge2HeightPxl;
    public int edge1WidthPxl, edge2WidthPxl;
    public int edge1Sensivite, edge2Sensitive;
    private List<AppCompatImageView> iconImageArrayList1, iconImageArrayList2;
    private String[] packagename, pinnedPackageName;
    private String launcherPackagename;
    private int[] x, y;
    private int numOfIcon, gridRow, gridColumn, gridGap, gridX, gridY, numOfRecent;
    public static final int GRID_ICON_SIZE = 58;
    private boolean hasOneActive = false;
    private boolean hasHomwBackNotiVisible = false;
    private boolean isEdge1On, isEdge2On;
    public int edge1Position, edge2Position, iconPaddingLeft, iconPaddingTop;
    private SharedPreferences defaultShared, sharedPreferences1, sharedPreferences2, sharedPreferences_favorite, sharedPreferences_exclude;
    private AppCompatImageView[] iconImageList1, iconImageList2;
    private ExpandStatusBarView action4View, action1View, action2View, action3View;
    private Vibrator vibrator;
    private int ovalOffSet, ovalRadiusPlus = 15, ovalRadiusPlusPxl, ovalOffSetInDp = 70;
    private long holdTime = 450, vibrationDuration;
    private boolean touched = false, switched = false, isOutOfTrial = false, isFreeVersion = false;
    private String[] spinnerEntries;
    private GridView shortcutGridView;
    private FavoriteShortcutAdapter shortcutAdapter;
    private IconPackManager.IconPack iconPack;
    private boolean isClockShown = false;
    private View clockView;
    private Realm pinAppRealm;
    private Set<String> pinnedSet;
    private WindowManager.LayoutParams backgroundParams;
    private int backgroundColor;
    private AnimationSet clockAnimation;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (getPackageName().equals(MainActivity.FREE_VERSION_PACKAGE_NAME)) isFreeVersion = true;
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo res = getPackageManager().resolveActivity(launcherIntent, 0);
        if (res.activityInfo != null) {
            launcherPackagename = res.activityInfo.packageName;
        } else launcherPackagename = "";
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (edge1View != null && edge1View.isAttachedToWindow()) {
            Log.e(LOG_TAG, "edge1View still attached to window");
            windowManager.removeView(edge1View);
        }
        if (edge2View != null && edge2View.isAttachedToWindow()) {
            Log.e(LOG_TAG, "edge1View still attached to window");
            windowManager.removeView(edge2View);
        }
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
            edge1View = (RelativeLayout) layoutInflater.inflate(R.layout.edge_view, null);
            edge1Image = (ImageView) edge1View.findViewById(R.id.edge_image);

//        ViewGroup.LayoutParams edge1ImageLayoutParams = edge1Image.getLayoutParams();
            if (edge1Image != null) {
                RelativeLayout.LayoutParams edge1ImageLayoutParams = new RelativeLayout.LayoutParams(edge1Image.getLayoutParams());
                if (Utility.getPositionIntFromString(sharedPreferences1.getString(EdgeSettingDialogFragment.EDGE_POSITION_KEY, spinnerEntries[1]), getApplicationContext()) >= 30) {
                    edge1HeightPxl = (int) (edge1Sensivite * mScale);
                    edge1WidthPxl = (int) (edge1Length * mScale);
                    if (edge1offset > 0) {
                        edge1ImageLayoutParams.rightMargin = (int) (edge1offset * mScale);
                    } else {
                        edge1ImageLayoutParams.leftMargin = (int) (-edge1offset * mScale);
                    }
                } else {
                    edge1HeightPxl = (int) (edge1Length * mScale);
                    edge1WidthPxl = (int) (edge1Sensivite * mScale);
                    if (edge1offset > 0) {
                        edge1ImageLayoutParams.bottomMargin = (int) (edge1offset * mScale);
                    } else {
                        edge1ImageLayoutParams.topMargin = (int) (-edge1offset * mScale);
                    }
                }
                edge1ImageLayoutParams.height = edge1HeightPxl;
                edge1ImageLayoutParams.width = edge1WidthPxl;
                edge1Image.setLayoutParams(edge1ImageLayoutParams);
            }
            item1View = (FrameLayout) layoutInflater.inflate(R.layout.item, null);
            iconImageList1 = new AppCompatImageView[6];
            iconImageList1[0] = (AppCompatImageView) item1View.findViewById(R.id.item_0);
            iconImageList1[1] = (AppCompatImageView) item1View.findViewById(R.id.item_1);
            iconImageList1[2] = (AppCompatImageView) item1View.findViewById(R.id.item_2);
            iconImageList1[3] = (AppCompatImageView) item1View.findViewById(R.id.item_3);
            iconImageList1[4] = (AppCompatImageView) item1View.findViewById(R.id.item_4);
            iconImageList1[5] = (AppCompatImageView) item1View.findViewById(R.id.item_5);
            iconImageArrayList1 = new ArrayList<AppCompatImageView>();
            iconImageArrayList1.add(iconImageList1[0]);
            iconImageArrayList1.add(iconImageList1[1]);
            iconImageArrayList1.add(iconImageList1[2]);
            iconImageArrayList1.add(iconImageList1[3]);
            iconImageArrayList1.add(iconImageList1[4]);
            iconImageArrayList1.add(iconImageList1[5]);
            WindowManager.LayoutParams paramsEdge1 = new WindowManager.LayoutParams(
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
            if (isEdge1On) {
                if (edge1View != null && edge1View.isAttachedToWindow()) {
                    windowManager.removeView(edge1View);
                }
                windowManager.addView(edge1View, paramsEdge1);
            } else {
                if (edge1View != null && edge1View.isAttachedToWindow()) {
                    windowManager.removeView(edge1View);
                }
            }

            boolean isOnlyFavorite1 = sharedPreferences1.getBoolean(EdgeSettingDialogFragment.IS_ONLY_FAVORITE_KEY, false);

            OnTouchListener onTouchListener1 = new OnTouchListener(edge1Position, iconImageList1, item1View, iconImageArrayList1, isOnlyFavorite1);
            edge1Image.setOnTouchListener(onTouchListener1);
        }


        if (isEdge2On) {
            edge2View = (RelativeLayout) layoutInflater.inflate(R.layout.edge_view, null);
            edge2Image = (ImageView) edge2View.findViewById(R.id.edge_image);
            RelativeLayout.LayoutParams edge2ImageLayoutParams = new RelativeLayout.LayoutParams(edge2Image.getLayoutParams());
            if (Utility.getPositionIntFromString(sharedPreferences2.getString(EdgeSettingDialogFragment.EDGE_POSITION_KEY, spinnerEntries[5]), getApplicationContext()) >= 30) {
                edge2HeightPxl = (int) (edge2Sensitive * mScale);
                edge2WidthPxl = (int) (edge2Length * mScale);
                if (edge2offset > 0) {
                    edge2ImageLayoutParams.rightMargin = (int) (edge2offset * mScale);
                } else {
                    edge2ImageLayoutParams.leftMargin = (int) (-edge2offset * mScale);
                }
            } else {

                edge2HeightPxl = (int) (edge2Length * mScale);
                edge2WidthPxl = (int) (edge2Sensitive * mScale);
                if (edge2offset > 0) {
                    edge2ImageLayoutParams.bottomMargin = (int) (edge2offset * mScale);
                } else {
                    edge2ImageLayoutParams.topMargin = (int) (-edge2offset * mScale);
                }
            }
            edge2ImageLayoutParams.height = edge2HeightPxl;
            edge2ImageLayoutParams.width = edge2WidthPxl;
            edge2Image.setLayoutParams(edge2ImageLayoutParams);
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
                    break;
                case 31:
                    paramsEdge2.gravity = Gravity.BOTTOM | Gravity.CENTER;
                    break;
            }
            if (isEdge2On) {
                if (edge2View != null && edge2View.isAttachedToWindow()) {
                    windowManager.removeView(edge2View);
                }
                windowManager.addView(edge2View, paramsEdge2);
            } else {
                if (edge2View != null && edge2View.isAttachedToWindow()) {
                    windowManager.removeView(edge2View);
                }
            }

            item2View = (FrameLayout) layoutInflater.inflate(R.layout.item, null);
            iconImageList2 = new AppCompatImageView[6];
            iconImageList2[0] = (AppCompatImageView) item2View.findViewById(R.id.item_0);
            iconImageList2[1] = (AppCompatImageView) item2View.findViewById(R.id.item_1);
            iconImageList2[2] = (AppCompatImageView) item2View.findViewById(R.id.item_2);
            iconImageList2[3] = (AppCompatImageView) item2View.findViewById(R.id.item_3);
            iconImageList2[4] = (AppCompatImageView) item2View.findViewById(R.id.item_4);
            iconImageList2[5] = (AppCompatImageView) item2View.findViewById(R.id.item_5);
            iconImageArrayList2 = new ArrayList<AppCompatImageView>();
            iconImageArrayList2.add(iconImageList2[0]);
            iconImageArrayList2.add(iconImageList2[1]);
            iconImageArrayList2.add(iconImageList2[2]);
            iconImageArrayList2.add(iconImageList2[3]);
            iconImageArrayList2.add(iconImageList2[4]);
            iconImageArrayList2.add(iconImageList2[5]);


            boolean isOnlyFavorite2 = sharedPreferences2.getBoolean(EdgeSettingDialogFragment.IS_ONLY_FAVORITE_KEY, false);
            OnTouchListener onTouchListener2 = new OnTouchListener(edge2Position, iconImageList2, item2View, iconImageArrayList2, isOnlyFavorite2);
            edge2Image.setOnTouchListener(onTouchListener2);
        }

        String iconPackPacka = defaultShared.getString(EdgeSettingDialogFragment.ICON_PACK_PACKAGE_NAME_KEY, "none");
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
                .setContentText(getString(R.string.notification_text)).setContentTitle(getString(R.string.notification_title));
        Notification notificationCompat = builder.build();
        startForeground(NOTIFICATION_ID, notificationCompat);


        shortcutView = (FrameLayout) layoutInflater.inflate(R.layout.grid_shortcut, null);
        shortcutGridView = (GridView) shortcutView.findViewById(R.id.edge_shortcut_grid_view);

        return START_NOT_STICKY;
    }

    public class OnTouchListener implements View.OnTouchListener {
        private int x_init_cord, y_init_cord;
        private int position, iconIdBackgrounded = -1, preShortcutToSwitch = -1, activateId = 0, activatedId = 0;
        private FrameLayout itemView;
        private AppCompatImageView[] iconImageList;
        private List<AppCompatImageView> iconImageArrayList;
        private DelayToSwitchTask delayToSwitchTask;
        private boolean isOnlyFavorite, isStayPermanent, isShortcutBackgroundNull = true;

        public OnTouchListener(int position, AppCompatImageView[] iconImageList, FrameLayout itemView, List<AppCompatImageView> iconImageArrayList, boolean isOnlyFavorite) {
            this.position = position;
            this.iconImageList = iconImageList;
            this.itemView = itemView;
            this.iconImageArrayList = iconImageArrayList;
            this.isOnlyFavorite = isOnlyFavorite;
        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int x_cord = (int) event.getRawX();
            int y_cord = (int) event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!backgroundFrame.isAttachedToWindow()) {
                        backgroundFrame.setAlpha(0f);
                        windowManager.addView(backgroundFrame, backgroundParams);
                        backgroundFrame.animate().alpha(1f).setDuration(300);
                    }
                    edge1Position = Utility.getPositionIntFromString(sharedPreferences1.getString(EdgeSettingDialogFragment.EDGE_POSITION_KEY, spinnerEntries[1]), getApplicationContext()); // default =1
                    edge2Position = Utility.getPositionIntFromString(sharedPreferences2.getString(EdgeSettingDialogFragment.EDGE_POSITION_KEY, spinnerEntries[5]), getApplicationContext());
                    if (position != edge1Position && position != edge2Position) {
                        Log.e(LOG_TAG, "postion != edge1position and edge2 position");
                        if (edge1View != null && edge1View.isAttachedToWindow()) {
                            windowManager.removeView(edge1View);
                        }
                        if (edge2View != null && edge2View.isAttachedToWindow()) {
                            windowManager.removeView(edge2View);
                        }
                        onDestroy();
                        return false;
                    }
                    Log.e(LOG_TAG, "position = " + position + "\nEdge1position = " + edge1Position + "\nEdge2Position = " + edge2Position);
                    isShortcutBackgroundNull = true;
                    preShortcutToSwitch = -1;
                    clearIconBackground();
                    if (!defaultShared.getBoolean(EdgeSettingDialogFragment.DISABLE_HAPTIC_FEEDBACK_KEY, true)) {
                        vibrator.vibrate(15);
                    }
                    try {
                        windowManager.removeView(clockView);
                        isClockShown = false;
                    } catch (IllegalArgumentException e) {
                        Log.e(LOG_TAG, "clockView is not attacked to the windowManager");
                    }
                    itemView.removeView(action2View);
                    itemView.removeView(action1View);
                    itemView.removeView(action4View);
                    if (item1View != null && itemView.isAttachedToWindow()) {
                        windowManager.removeView(itemView);
                    }
                    if (shortcutView != null && shortcutView.isAttachedToWindow()) {
                        windowManager.removeView(shortcutView);
                    }
                    if (isFreeVersion) {
                        isOutOfTrial = System.currentTimeMillis() - defaultShared.getLong(EdgeSettingDialogFragment.BEGIN_DAY_KEY, System.currentTimeMillis())
                                > MainActivity.trialTime;
                    } else isOutOfTrial = false;

                    Set<String> excludeSet = sharedPreferences_exclude.getStringSet(EdgeSettingDialogFragment.EXCLUDE_KEY, new HashSet<String>());

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
                    switched = isOnlyFavorite;
                    if (isOnlyFavorite) {
                        if (delayToSwitchTask == null) {
                            delayToSwitchTask = new DelayToSwitchTask();
                            delayToSwitchTask.switchShortcut();
                        } else if (delayToSwitchTask.isCancelled()) {
                            delayToSwitchTask = new DelayToSwitchTask();
                            delayToSwitchTask.switchShortcut();
                        }
                        break;
                    }

                    float xForHomeBackNotiView = x_init_cord - icon_distance_pxl - distance_to_arc_pxl - ovalOffSet - ovalRadiusPlusPxl;
                    float yForHomeBackNotiView = y_init_cord - icon_distance_pxl - distance_to_arc_pxl - ovalOffSet - ovalRadiusPlusPxl;
                    int radiusForHomeBackNotiView = (int) icon_distance_pxl + distance_to_arc_pxl + ovalRadiusPlusPxl;
                    isStayPermanent = defaultShared.getBoolean(EdgeSettingDialogFragment.IS_ACTIONS_STAY_PERMANENT, false);
                    if (!defaultShared.getString(EdgeSettingDialogFragment.ACTION_4_KEY, MainActivity.ACTION_NOTI).equals(MainActivity.ACTION_NONE)) {
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

                    if (!defaultShared.getString(EdgeSettingDialogFragment.ACTION_1_KEY, MainActivity.ACTION_HOME).equals(MainActivity.ACTION_NONE)) {
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


                    if (!defaultShared.getString(EdgeSettingDialogFragment.ACTION_2_KEY, MainActivity.ACTION_BACK).equals(MainActivity.ACTION_NONE)) {
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


                    if (!defaultShared.getString(EdgeSettingDialogFragment.ACTION_3_KEY, MainActivity.ACTION_NONE).equals(MainActivity.ACTION_NONE)) {
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
//                        Log.e(LOG_TAG," item_view has already been added to the window manager");
//                    }
//                    Utility.setIconsPosition(iconImageList, x_init_cord, y_init_cord, icon_distance_pxl, icon_24dp_in_pxls, position);
                    Utility.setIconPositionNew(iconImageList, icon_distance_pxl, icon_24dp_in_pxls, position, x_init_cord, y_init_cord, 6);

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


                        if (6 - tempPackageName.size() - pinnedPackageName.length > 0) {
                            packagename = new String[tempPackageName.size() + pinnedPackageName.length];
                        } else {
                            packagename = new String[6];
                        }
                        int n = 0;
                        if (defaultShared.getBoolean(EdgeSettingDialogFragment.IS_PIN_TO_TOP_KEY, false)) {
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



//                        packagename = new String[tempPackageName.size()];
//                        tempPackageName.toArray(packagename);
                        for (int i = 0; i < 6; i++) {
                            if (i >= packagename.length) {
                                iconImageArrayList.get(i).setImageDrawable(null);
                            } else {
                                try {
//                                    Drawable icon = getPackageManager().getApplicationIcon(packagename[i]);
//                                    ImageView iconi = iconImageArrayList.get(i);
                                    Drawable defaultDrawable = getPackageManager().getApplicationIcon(packagename[i]);
                                    if (iconPack != null) {
                                        iconImageArrayList.get(i).setImageDrawable(iconPack.getDrawableIconForPackage(packagename[i], defaultDrawable));
                                    } else {
                                        iconImageArrayList.get(i).setImageDrawable(defaultDrawable);

                                    }
                                } catch (PackageManager.NameNotFoundException e) {
                                    Log.e(LOG_TAG, "NameNotFound" + e);
                                }
                            }
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
                        long currentTimeMillis = System.currentTimeMillis()+5000;
                        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTimeMillis - 1000 * 1000, currentTimeMillis);
                        ArrayList<String> tempPackageName = new ArrayList<String>();
                        if (stats != null) {
                            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>(DATE_DECENDING_COMPARATOR);
                            for (UsageStats usageStats : stats) {
                                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                            }
                            Set<Long> setKey = mySortedMap.keySet();
                            Log.e(LOG_TAG, "mySortedMap size = " + mySortedMap.size());
                            UsageStats usageStats;
                            String packa;
                            PackageManager packageManager = getPackageManager();
                            for (Long key : setKey) {
                                if (key >= currentTimeMillis) {
                                    Log.e(LOG_TAG, "key is in future");
                                } else {
                                    usageStats = mySortedMap.get(key);
                                    if (usageStats == null) {
                                        Log.e(LOG_TAG, " usageStats is null");
                                    } else {
                                        packa = usageStats.getPackageName();
                                        try {
                                            if (packageManager.getApplicationInfo(packa, 0).dataDir.startsWith("/system/app/")) {
                                                //do nothing
                                            } else if (     packageManager.getLaunchIntentForPackage(packa) == null ||
                                                            packa.contains("systemui") ||
                                                            packa.contains("googlequicksearchbox") ||
                                                            key == mySortedMap.firstKey() ||
                                                            excludeSet.contains(packa) ||
                                                            pinnedSet.contains(packa) ||
                                                            packa.contains("launcher") )
                                                             {
                                                // do nothing
                                            } else tempPackageName.add(packa);
                                            if (tempPackageName.size() >= 8) {
                                                Log.e(LOG_TAG, "tempackage >= 8");
                                                break;
                                            }
                                        } catch (PackageManager.NameNotFoundException e) {
                                            Log.e(LOG_TAG, "name not found" + e);
                                        }
                                    }

                                }

                            }
                            if (6 - tempPackageName.size() - pinnedPackageName.length > 0) {
                                packagename = new String[tempPackageName.size() + pinnedPackageName.length];
                            } else {
                                packagename = new String[6];
                            }
                            int n = 0;
                            if (defaultShared.getBoolean(EdgeSettingDialogFragment.IS_PIN_TO_TOP_KEY, false)) {
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


//                            packagename = new String[tempPackageName.size()];
//                            tempPackageName.toArray(packagename);
                        } else Log.e(LOG_TAG, "erros in mySortedMap");
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
                                    Log.e(LOG_TAG, "NameNotFound" + e);
                                }
                            }
                        }

                    }

                    if (packagename.length == 0) {
                        if (delayToSwitchTask == null) {
                            delayToSwitchTask = new DelayToSwitchTask();
                            delayToSwitchTask.switchShortcut();
                        } else if (delayToSwitchTask.isCancelled()) {
                            delayToSwitchTask = new DelayToSwitchTask();
                            delayToSwitchTask.switchShortcut();
                        }
                    } else {
                        numOfIcon = iconImageArrayList.size();
                        x = new int[numOfIcon];
                        y = new int[numOfIcon];
                        for (int i = 0; i < numOfIcon; i++) {
                            x[i] = (int) iconImageArrayList.get(i).getX();
                            y[i] = (int) iconImageArrayList.get(i).getY();
                        }
                        try {
                            windowManager.addView(itemView, itemViewParameter);

                        } catch (IllegalStateException e) {
                            Log.e(LOG_TAG, " item_view has already been added to the window manager");
                        }
                    }
                    iconIdBackgrounded = -1;

                    break;



                case MotionEvent.ACTION_UP:
                    try {
                        windowManager.removeView(itemView);
                    } catch (IllegalArgumentException e) {
                        Log.e(LOG_TAG, "itemView is not attacked to the windowManager");
                    }
                    try {
                        windowManager.removeView(shortcutView);
                    } catch (IllegalArgumentException e) {
                        Log.e(LOG_TAG, "shortcutView is not attacked to the windowManager");
                    }
                    try {
                        windowManager.removeView(clockView);
                        isClockShown = false;
                    } catch (IllegalArgumentException e) {
                        Log.e(LOG_TAG, "clockView is not attacked to the windowManager");
                    }
                    try {
                        windowManager.removeView(backgroundFrame);
                    } catch (IllegalArgumentException e) {
                        Log.e(LOG_TAG, "background is not attacted to window");
                    }
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

                    if (switched) {
                        int shortcutToSwitch = Utility.findShortcutToSwitch(x_cord, y_cord, (int) shortcutGridView.getX(), (int) shortcutGridView.getY(), GRID_ICON_SIZE, mScale, gridRow, gridColumn, gridGap);
                        Log.e(LOG_TAG, "shortcutToSwitch = " + shortcutToSwitch + "\ngrid_x =" + shortcutGridView.getX() + "\ngrid_y = " + shortcutGridView.getY() +
                                "\nx_cord = " + x_cord + "\ny_cord = " + y_cord);
                        Realm myRealm = Realm.getInstance(getApplicationContext());
                        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id", shortcutToSwitch).findFirst();
                        if (shortcut != null) {
                            if (shortcut.getType() == Shortcut.TYPE_APP) {
                                Intent extApp;
                                extApp = getPackageManager().getLaunchIntentForPackage(shortcut.getPackageName());
                                if (extApp != null) {
                                    ComponentName componentName = extApp.getComponent();
                                    Intent startApp = new Intent(Intent.ACTION_MAIN, null);
                                    startApp.addCategory(Intent.CATEGORY_LAUNCHER);
                                    startApp.setComponent(componentName);
                                    startApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(startApp);
                                } else {
                                    Log.e(LOG_TAG, "extApp of shortcut = null ");
                                }
                            } else if (shortcut.getType() == Shortcut.TYPE_SETTING) {
                                switch (shortcut.getAction()) {
                                    case Shortcut.ACTION_WIFI:
                                        Utility.toggleWifi(getApplicationContext());
                                        break;
                                    case Shortcut.ACTION_BLUETOOTH:
                                        Utility.toggleBluetooth(getApplicationContext());
                                        break;
                                    case Shortcut.ACTION_ROTATION:
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                            Utility.setAutorotation(getApplicationContext());
                                        } else {
                                            if (Settings.System.canWrite(getApplicationContext())) {
                                                Utility.setAutorotation(getApplicationContext());
                                            } else {
                                                Intent notiIntent = new Intent();
                                                notiIntent.setAction(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                                PendingIntent notiPending = PendingIntent.getActivity(getApplicationContext(), 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                                                builder.setContentTitle(getString(R.string.ask_for_write_setting_notification_title)).setContentText(getString(R.string.ask_for_write_setting_notification_text)).setSmallIcon(R.drawable.ic_settings_white_36px)
                                                        .setContentIntent(notiPending)
                                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                        .setDefaults(NotificationCompat.DEFAULT_SOUND);
                                                Notification notification = builder.build();
                                                NotificationManager notificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
                                                notificationManager.notify(22, notification);
                                            }
                                        }

                                        break;
                                    case Shortcut.ACTION_POWER_MENU:
                                        AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
                                        event1.setClassName(getClass().getName());
                                        event1.getText().add("power");
                                        event1.setAction(3);
                                        event1.setPackageName(getPackageName());
                                        event1.setEnabled(true);
                                        AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
                                        AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
                                        recordCompat.setSource(v);
                                        if (Utility.isAccessibilityEnable(getApplicationContext())) {
                                            manager.sendAccessibilityEvent(event1);
                                        } else
                                            Toast.makeText(getApplicationContext(), R.string.ask_user_to_turn_on_accessibility_toast, Toast.LENGTH_LONG).show();
                                }
                            }
                        } else if (shortcutToSwitch != -1) {
                            Toast.makeText(getApplicationContext(), getString(R.string.please_add_favorite_item), Toast.LENGTH_LONG).show();
                            showAddFavoriteDialog();
                        }
                    } else {
//                        int packageToSwitch = Utility.findIconToSwitch(x, y, x_cord, y_cord, numOfIcon, icon_rad, mScale);
                        int packageToSwitch = Utility.findIconToSwitchNew(x, y, x_cord, y_cord, icon_24dp_in_pxls, mScale);
                        if (packageToSwitch != -1) {
                            Intent extApp = null;
                            if (packageToSwitch < packagename.length) {
                                extApp = getPackageManager().getLaunchIntentForPackage(packagename[packageToSwitch]);
                            }

                            if (extApp != null) {
                                ComponentName componentName = extApp.getComponent();
                                Intent startApp = new Intent(Intent.ACTION_MAIN, null);
                                startApp.addCategory(Intent.CATEGORY_LAUNCHER);
                                startApp.setComponent(componentName);
                                startApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(startApp);
                                Log.e(LOG_TAG, "packageToSwitch = " + packageToSwitch);
                            } else Log.e(LOG_TAG, "extApp = null ");

                        }
                        packagename = null;
                        int homeBackNoti = Utility.isHomeOrBackOrNoti(x_init_cord, y_init_cord, x_cord, y_cord, icon_distance, mScale, position);
                        Log.e(LOG_TAG, "homeBackNoti = " + homeBackNoti);
                        String action = MainActivity.ACTION_NONE;
                        switch (homeBackNoti) {
                            case 1:
                                action = defaultShared.getString(EdgeSettingDialogFragment.ACTION_1_KEY, MainActivity.ACTION_HOME);
                                break;
                            case 2:
                                action = defaultShared.getString(EdgeSettingDialogFragment.ACTION_2_KEY, MainActivity.ACTION_BACK);
                                break;
                            case 3:
                                action = defaultShared.getString(EdgeSettingDialogFragment.ACTION_3_KEY, MainActivity.ACTION_NONE);
                                break;
                            case 4:
                                action = defaultShared.getString(EdgeSettingDialogFragment.ACTION_4_KEY, MainActivity.ACTION_NOTI);
                        }
                        if (action.equals(MainActivity.ACTION_NOTI) & isFreeVersion & isOutOfTrial) {
                            Toast.makeText(getApplicationContext(), getString(R.string.edge_service_out_of_trial_text_when_homebacknoti), Toast.LENGTH_LONG).show();
                        } else {
                            Utility.executeAction(getApplicationContext(), action, v, getClass().getName(), getPackageName());
                        }


                    }


                    switched = false;
                    touched = false;
                    if (delayToSwitchTask != null) {
                        delayToSwitchTask.cancel(true);
                    }
                    if (itemView != null && itemView.isAttachedToWindow()) {
                        windowManager.removeView(itemView);
                    }
                    if (shortcutView != null && shortcutView.isAttachedToWindow()) {
                        windowManager.removeView(shortcutView);
                    }
                    try {
                        windowManager.removeView(backgroundFrame);
                    } catch (IllegalArgumentException e) {
                        Log.e(LOG_TAG, "background is not attacted to window");
                    }
                    break;


                case MotionEvent.ACTION_MOVE:
                    if (!isClockShown && !switched && !defaultShared.getBoolean(EdgeSettingDialogFragment.DISABLE_CLOCK_KEY, false)) {
                        Log.e(LOG_TAG, "Show clock");
                        clockView = Utility.disPlayClock(getApplicationContext(), windowManager);
                        isClockShown = true;
                    }
                    if (switched) {
                        int shortcutToSwitch = Utility.findShortcutToSwitch(x_cord, y_cord, gridX, gridY, GRID_ICON_SIZE, mScale, gridRow, gridColumn, gridGap);
                        if (shortcutToSwitch != -1) {
                            activateId = shortcutToSwitch + 1;
                        } else {
                            activatedId = 0;
                            activateId = 0;
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
//                        int iconToSwitch = Utility.findIconToSwitch(x, y, x_cord, y_cord, numOfIcon, icon_rad, mScale);
                        int iconToSwitch = Utility.findIconToSwitchNew(x, y, x_cord, y_cord, icon_24dp_in_pxls, mScale);
                        int moveToHomeBackNoti = Utility.isHomeOrBackOrNoti(x_init_cord, y_init_cord, x_cord, y_cord, icon_distance, mScale, position);
                        if (moveToHomeBackNoti > 0) {
                            activateId = moveToHomeBackNoti + 30;
                        }
                        if (iconToSwitch != -1) {
                            if (iconToSwitch < iconImageArrayList.size() && iconIdBackgrounded != iconToSwitch) {
                                clearIconBackground();
                                ImageView iconHighlight = iconImageArrayList.get(iconToSwitch);
                                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(iconHighlight.getLayoutParams());
                                float x = iconHighlight.getX();
                                float y = iconHighlight.getY();

                                layoutParams.height = (int) (64 * mScale);
                                layoutParams.width = (int) (76 * mScale);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    iconHighlight.setBackground(getDrawable(R.drawable.icon_background));
                                } else {
                                    iconHighlight.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_background));
                                }

                                iconHighlight.setX(x - 14 * mScale);
                                iconHighlight.setY(y - 8 * mScale);
//                                iconHighlight.setTranslationX(-14 * mScale);
//                                iconHighlight.setTranslationY(-8 * mScale);
                                iconHighlight.setLayoutParams(layoutParams);
                                iconHighlight.setPadding(iconPaddingLeft, iconPaddingTop, iconPaddingLeft, iconPaddingTop);
                                iconIdBackgrounded = iconToSwitch;
                                Log.e(LOG_TAG, "setBackground");

                            }


                            activateId = iconToSwitch + 20;

                            if (defaultShared.getBoolean(EdgeSettingDialogFragment.HOLD_TIME_ENABLE_KEY,true) && delayToSwitchTask == null) {
                                delayToSwitchTask = new DelayToSwitchTask();
                                delayToSwitchTask.execute();
                            } else if (defaultShared.getBoolean(EdgeSettingDialogFragment.HOLD_TIME_ENABLE_KEY, true) && delayToSwitchTask.isCancelled()) {
                                delayToSwitchTask = new DelayToSwitchTask();
                                delayToSwitchTask.execute();
                            }
                            if (activatedId != activateId) {
                                if (delayToSwitchTask != null) {
                                    delayToSwitchTask.cancel(true);
                                }
                            }

                            if (!touched) {
                                touched = true;
                            }
                        } else {
                            if (moveToHomeBackNoti == 0) {
                                activatedId = 0;
                                activateId = 0;
                            }
                            clearIconBackground();

                            if (delayToSwitchTask != null) {
                                delayToSwitchTask.cancel(true);
                                delayToSwitchTask = null;
                            }
                            touched = false;
                        }
                        if (iconToSwitch == -1 & moveToHomeBackNoti == 0) {
                            if (hasOneActive) {
                                for (ImageView imageView : iconImageArrayList) {
                                    if (imageView.getColorFilter() != null) {
                                        imageView.setColorFilter(null);
                                    }
                                }
                            }
                            hasOneActive = false;
                        }
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
                    if (activateId != 0 && activatedId != activateId) {
                        if (defaultShared.getBoolean(EdgeSettingDialogFragment.HAPTIC_ON_ICON_KEY, false)) {
                            vibrator.vibrate(vibrationDuration);
                        }
                        activatedId = activateId;
                        activateId = 0;
                    }

                    break;

                case MotionEvent.ACTION_OUTSIDE:
                    if (item1View != null && item1View.isAttachedToWindow()) {
                        windowManager.removeView(item1View);
                    }
                    if (item2View != null && item2View.isAttachedToWindow()) {
                        windowManager.removeView(item2View);
                    }
                    if (backgroundFrame != null && backgroundFrame.isAttachedToWindow()) {
                        windowManager.removeView(backgroundFrame);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (item1View != null && item1View.isAttachedToWindow()) {
                        windowManager.removeView(item1View);
                    }
                    if (item2View != null && item2View.isAttachedToWindow()) {
                        windowManager.removeView(item2View);
                    }
                    if (backgroundFrame != null && backgroundFrame.isAttachedToWindow()) {
                        windowManager.removeView(backgroundFrame);
                    }
                    break;
            }
            return true;
        }

        private class DelayToSwitchTask extends AsyncTask<Void, Void, Void> {
            private boolean isSleepEnough = false;

            @Override
            protected Void doInBackground(Void... params) {
                isSleepEnough = false;
                try {
                    Thread.sleep(holdTime);
                    isSleepEnough = true;
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, "interrupt sleeping");
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
                    switchShortcut();

                }

                super.onPostExecute(aVoid);
            }

            protected void switchShortcut() {
                clearIconBackground();
                shortcutAdapter = new FavoriteShortcutAdapter(getApplicationContext());
                ViewGroup.LayoutParams gridParams = shortcutGridView.getLayoutParams();
                int gridRow = defaultShared.getInt(EdgeSettingDialogFragment.NUM_OF_GRID_ROW_KEY, 5);
                int gridColumn = defaultShared.getInt(EdgeSettingDialogFragment.NUM_OF_GRID_COLUMN_KEY, 4);
                int gridGap = defaultShared.getInt(EdgeSettingDialogFragment.GAP_OF_SHORTCUT_KEY, 12);
                shortcutGridView.setVerticalSpacing((int) (gridGap * mScale));
                shortcutGridView.setNumColumns(gridColumn);
                int gridDistanceFromEdge = defaultShared.getInt(EdgeSettingDialogFragment.GRID_DISTANCE_FROM_EDGE_KEY, 20);
                float gridWide = (int) (mScale * (float) (GRID_ICON_SIZE * gridColumn + gridGap * (gridColumn - 1)));
                float gridTall = (int) (mScale * (float) (GRID_ICON_SIZE * gridRow + gridGap * (gridRow - 1)));
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

                Utility.setFavoriteShortcutGridViewPosition(shortcutGridView,gridTall,gridWide, x_init_cord, y_init_cord, mScale, position, windowManager, defaultShared, gridDistanceFromEdge, gridGap);
                gridX = (int) shortcutGridView.getX();
                gridY = (int) shortcutGridView.getY();
                if (shortcutView != null && !shortcutView.isAttachedToWindow()) {
                    windowManager.addView(shortcutView, shortcutViewParams);
                }
                if (itemView != null && itemView.isAttachedToWindow()) {
                    windowManager.removeView(itemView);
                }
                switched = true;
                if (clockView != null && clockView.isAttachedToWindow()) {
                    windowManager.removeView(clockView);
                    isClockShown = false;
                }
            }
        }

        private void clearIconBackground() {
            if (iconIdBackgrounded != -1) {
                if (iconIdBackgrounded < iconImageArrayList.size()) {
                    ImageView iconResetBackground = iconImageArrayList.get(iconIdBackgrounded);
                    FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(iconResetBackground.getLayoutParams());
                    layoutParams1.width = (int) (48 * mScale);
                    layoutParams1.height = (int) (48 * mScale);
                    float x = iconResetBackground.getX();
                    float y = iconResetBackground.getY();
                    iconResetBackground.setBackground(null);
                    iconResetBackground.setX(x + 14 * mScale);
                    iconResetBackground.setY(y + 8 * mScale);
                    iconResetBackground.setLayoutParams(layoutParams1);
                    iconResetBackground.setPadding(0, 0, 0, 0);
                    iconIdBackgrounded = -1;
                } else iconIdBackgrounded = -1;
            }
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
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
            windowManager.removeView(item1View);
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
            windowManager.removeView(item2View);
        }
        if (backgroundFrame != null && backgroundFrame.isAttachedToWindow()) {
            windowManager.removeView(backgroundFrame);
        }

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
        edge1Length = sharedPreferences1.getInt(EdgeSettingDialogFragment.EDGE_LENGTH_KEY, 150);
        edge2Length = sharedPreferences2.getInt(EdgeSettingDialogFragment.EDGE_LENGTH_KEY, 150);
        edge1offset = sharedPreferences1.getInt(EdgeSettingDialogFragment.EDGE_OFFSET_KEY, 0);
        edge2offset = sharedPreferences2.getInt(EdgeSettingDialogFragment.EDGE_OFFSET_KEY, 0);
        edge1Sensivite = sharedPreferences1.getInt(EdgeSettingDialogFragment.EDGE_SENSIIVE_KEY, 12);
        edge2Sensitive = sharedPreferences2.getInt(EdgeSettingDialogFragment.EDGE_SENSIIVE_KEY, 12);
        isEdge1On = sharedPreferences1.getBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, true);
        isEdge2On = sharedPreferences2.getBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, false);
        spinnerEntries = getResources().getStringArray(R.array.edge_dialog_spinner_array);
        icon_distance = defaultShared.getInt(EdgeSettingDialogFragment.ICON_DISTANCE_KEY, 110);
        ovalOffSet = (int) (ovalOffSetInDp * mScale);
        ovalRadiusPlusPxl = (int) (ovalRadiusPlus * mScale);
        numOfRecent = defaultShared.getInt(EdgeSettingDialogFragment.NUM_OF_RECENT_KEY, 6);
        gridRow = defaultShared.getInt(EdgeSettingDialogFragment.NUM_OF_GRID_ROW_KEY, 5);
        gridColumn = defaultShared.getInt(EdgeSettingDialogFragment.NUM_OF_GRID_COLUMN_KEY, 4);
        gridGap = defaultShared.getInt(EdgeSettingDialogFragment.GAP_OF_SHORTCUT_KEY, 12);
        holdTime = defaultShared.getInt(EdgeSettingDialogFragment.HOLD_TIME_KEY, 450);
        vibrationDuration = defaultShared.getInt(EdgeSettingDialogFragment.VIBRATION_DURATION_KEY, 15);
        iconPaddingLeft = (int) (14 * mScale);
        iconPaddingTop = (int) (8 * mScale);
        edge1Position = Utility.getPositionIntFromString(sharedPreferences1.getString(EdgeSettingDialogFragment.EDGE_POSITION_KEY, spinnerEntries[1]), getApplicationContext()); // default =1
        edge2Position = Utility.getPositionIntFromString(sharedPreferences2.getString(EdgeSettingDialogFragment.EDGE_POSITION_KEY, spinnerEntries[5]), getApplicationContext());
        pinAppRealm = Realm.getInstance(new RealmConfiguration.Builder(getApplicationContext()).name("pinApp.realm").build());
        backgroundColor  = defaultShared.getInt(EdgeSettingDialogFragment.BACKGROUND_COLOR_KEY, 1879048192);
        clockAnimation = new AnimationSet(getBaseContext(), null);
        clockAnimation.addAnimation(new AlphaAnimation(0f,1f));
//        clockAnimation.addAnimation(new TranslateAnimation(0,0,0,1000));
        clockAnimation.setDuration(1000);

//        pinAppRealm.beginTransaction();
//        Shortcut country1 = pinAppRealm.createObject(Shortcut.class);
//
//        // Set its fields
//        country1.setPackageName(getPackageName());
//        country1.setId(0);
//
//        pinAppRealm.commitTransaction();
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
                Log.e(LOG_TAG,"result = " + shortcut.getPackageName());
                pinnedPackageName[i] = shortcut.getPackageName();
                Log.e(LOG_TAG,"pinnedPack = " + pinnedPackageName[0]);
                i++;
            }
        }
        pinnedSet = new HashSet<String>(Arrays.asList(pinnedPackageName));

        Log.e(LOG_TAG, "onCreate service" + "\nEdge1 on = " + isEdge1On + "\nEdge2 on = " + isEdge2On +
                "\nEdge1 position = " + edge1Position + "\nEdge2 positon = " + edge2Position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (edge1View != null && edge1View.isAttachedToWindow()) {
            Log.e(LOG_TAG, "remove edge1");
            edge1View.setVisibility(View.GONE);
            windowManager.removeView(edge1View);
        }
        if (edge2View != null && edge2View.isAttachedToWindow()) {
            Log.e(LOG_TAG, "remove edge2");
            edge2View.setVisibility(View.GONE);
            windowManager.removeView(edge2View);
        }
        if (backgroundFrame != null && backgroundFrame.isAttachedToWindow()) {
            windowManager.removeView(backgroundFrame);
        }
        Log.e(LOG_TAG, "onDestroy service");
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


    public void showAddFavoriteDialog() {
        startActivity(new Intent(getApplicationContext(), SetFavoriteShortcutActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }


    public static class BootCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.startService(new Intent(context, EdgeGestureService.class));
        }
    }


}
