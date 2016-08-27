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
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;
import org.de_studio.recentappswitcher.service.EdgeSetting;
import org.de_studio.recentappswitcher.service.MyImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by HaiNguyen on 8/19/16.
 */
public class EdgeServiceView extends Service implements View.OnTouchListener {

    private static final String TAG = EdgeServiceView.class.getSimpleName();
    WindowManager windowManager;
    Vibrator vibrator;
    SharedPreferences defaultShared, edge1Shared, edge2Shared;
    FrameLayout circleShortcutsView;
    MyImageView[] recentIcons;
    FrameLayout backgroundFrame;
    WindowManager.LayoutParams backgroundParams, edge1Para, edge2Para, circleShortcutsViewPara;
    View edge1View, edge2View;
    private IconPackManager.IconPack iconPack;


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

    public int getEdge1Position() {
        String edge1Default = getResources().getStringArray(R.array.edge_dialog_spinner_array)[1];
        return Utility.getPositionIntFromString(edge1Shared.getString(EdgeSetting.EDGE_POSITION_KEY, edge1Default), getApplicationContext());
    }

    public int getEdge2Position() {
        String edge2Default = getResources().getStringArray(R.array.edge_dialog_spinner_array)[5];
        return Utility.getPositionIntFromString(edge2Shared.getString(EdgeSetting.EDGE_POSITION_KEY, edge2Default), getApplicationContext());

    }

    public void createRecentIconsList(float mScale) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        circleShortcutsView = (FrameLayout) layoutInflater.inflate(R.layout.items, null);
        recentIcons = new MyImageView[6];
        recentIcons[0] = (MyImageView) circleShortcutsView.findViewById(R.id.item_0);
        recentIcons[1] = (MyImageView) circleShortcutsView.findViewById(R.id.item_1);
        recentIcons[2] = (MyImageView) circleShortcutsView.findViewById(R.id.item_2);
        recentIcons[3] = (MyImageView) circleShortcutsView.findViewById(R.id.item_3);
        recentIcons[4] = (MyImageView) circleShortcutsView.findViewById(R.id.item_4);
        recentIcons[5] = (MyImageView) circleShortcutsView.findViewById(R.id.item_5);

        FrameLayout.LayoutParams sampleParas1 = new FrameLayout.LayoutParams(recentIcons[0].getLayoutParams());
        float mIconScale = defaultShared.getFloat(Cons.ICON_SCALE, Cons.ICON_SCALE_DEFAULT);
        for (MyImageView image : recentIcons) {
            sampleParas1.height = (int) (48 * mIconScale * mScale);
            sampleParas1.width = (int) (48 * mIconScale * mScale);
            image.setLayoutParams(sampleParas1);
        }
    }

    public boolean isEdge1On() {
        return edge1Shared.getBoolean(Cons.EDGE_ON_KEY, true);
    }

    public boolean isEdge2On() {
        return edge2Shared.getBoolean(Cons.EDGE_ON_KEY, false);
    }

    public void createBackgroundFrame() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        backgroundFrame = (FrameLayout) layoutInflater.inflate(R.layout.background, null);
        backgroundFrame.setBackgroundColor(getBackgroundColor());
        backgroundParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                PixelFormat.TRANSLUCENT);
    }

    public int getBackgroundColor() {
        return  defaultShared.getInt(EdgeSetting.BACKGROUND_COLOR_KEY, 1879048192);
    }

    public String getLauncherPackagename() {
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo res = getPackageManager().resolveActivity(launcherIntent, 0);
        if (res.activityInfo != null) {
            return res.activityInfo.packageName;
        } else return  "";
    }

    public void setWindowManager() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    public void setVibrator() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void setEdge1View(int edge1Position, float mScale) {
        edge1View = new View(getApplicationContext());
        edge1View.setTag(Cons.TAG_EDGE_1);
        if (edge1Shared.getBoolean(EdgeSetting.USE_GUIDE_KEY, true)) {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(0);
            shape.setStroke((int) (2 * mScale), getGuideColor());
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
            edge1View.setBackground(drawable);


            int edge1Sensivite = edge1Shared.getInt(Cons.EDGE_SENSIIVE_KEY,Cons.EDGE_SENSITIVE_DEFAULT);
            int edge1Length = edge1Shared.getInt(Cons.EDGE_LENGTH_KEY,Cons.EDGE_LENGTH_DEFAULT);
            int edge1HeightPxl;
            int edge1WidthPxl;


            if (Utility.rightLeftOrBottom(edge1Position) == Cons.POSITION_BOTTOM) {
                edge1HeightPxl = (int) (edge1Sensivite * mScale);
                edge1WidthPxl = (int) (edge1Length * mScale);
            } else {
                edge1HeightPxl = (int) (edge1Length * mScale);
                edge1WidthPxl = (int) (edge1Sensivite * mScale);
            }
            RelativeLayout.LayoutParams edge1ImageLayoutParams = new RelativeLayout.LayoutParams(edge1WidthPxl,edge1HeightPxl);
            edge1ImageLayoutParams.height = edge1HeightPxl;
            edge1ImageLayoutParams.width = edge1WidthPxl;
            edge1View.setLayoutParams(edge1ImageLayoutParams);
        }
    }

    public void setEdge2View(int edge2Position, float mScale) {
        edge2View = new View(getApplicationContext());
        edge2View.setTag(Cons.TAG_EDGE_2);
        if (edge2Shared.getBoolean(EdgeSetting.USE_GUIDE_KEY, true)) {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(0);
            shape.setStroke((int) (2 * mScale), getGuideColor());
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
            edge2View.setBackground(drawable);

            int edge2Sensivite = edge2Shared.getInt(Cons.EDGE_SENSIIVE_KEY,Cons.EDGE_SENSITIVE_DEFAULT);
            int edge2Length = edge2Shared.getInt(Cons.EDGE_LENGTH_KEY,Cons.EDGE_LENGTH_DEFAULT);
            int edge2HeightPxl;
            int edge2WidthPxl;


            if (Utility.rightLeftOrBottom(edge2Position) == Cons.POSITION_BOTTOM) {
                edge2HeightPxl = (int) (edge2Sensivite * mScale);
                edge2WidthPxl = (int) (edge2Length * mScale);
            } else {
                edge2HeightPxl = (int) (edge2Length * mScale);
                edge2WidthPxl = (int) (edge2Sensivite * mScale);
            }
            RelativeLayout.LayoutParams edge2ImageLayoutParams = new RelativeLayout.LayoutParams(edge2WidthPxl,edge2HeightPxl);
            edge2ImageLayoutParams.height = edge2HeightPxl;
            edge2ImageLayoutParams.width = edge2WidthPxl;
            edge2View.setLayoutParams(edge2ImageLayoutParams);
        }
    }


    public void addEdgeToWindowManager(String edgeTag, WindowManager.LayoutParams edgePara) {
        if (!(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && defaultShared.getBoolean(EdgeSetting.IS_DISABLE_IN_LANSCAPE,false)) ) {
            switch (edgeTag) {
                case Cons.TAG_EDGE_1:
                    windowManager.addView(edge1View, edgePara);
                    break;
                case Cons.TAG_EDGE_2:
                    windowManager.addView(edge2View, edgePara);
            }
        }
    }

    public WindowManager.LayoutParams getEdgePara(int edgePosition,String edgeTag, float mScale) {
        if (edgeTag.equals(Cons.TAG_EDGE_1) && edge1Para != null) {
            return edge1Para;
        }

        if (edgeTag.equals(Cons.TAG_EDGE_2) && edge2Para != null) {
            return edge2Para;
        }

        WindowManager.LayoutParams edgePara;
        switch (defaultShared.getInt(EdgeSetting.AVOID_KEYBOARD_OPTION_KEY, EdgeSetting.OPTION_PLACE_UNDER)) {
            case EdgeSetting.OPTION_PLACE_UNDER:
                edgePara = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        PixelFormat.TRANSLUCENT);
                break;
            case EdgeSetting.OPTION_STEP_ASIDE:
                edgePara = new WindowManager.LayoutParams();
                edgePara.type = 2002;
                edgePara.gravity = 53;
                edgePara.flags = 40;
                edgePara.width = 1;
                edgePara.height = -1;
                edgePara.format = -2;
                break;
            default:
                edgePara = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        PixelFormat.TRANSLUCENT);
                break;
        }

        if (defaultShared.getBoolean(EdgeSetting.AVOID_KEYBOARD_KEY, true)) {
            edgePara.flags |= 131072;
        }
        switch (edgePosition) {
            case 10:
                edgePara.gravity = Gravity.TOP | Gravity.RIGHT;
                break;
            case 11:
                edgePara.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                break;
            case 12:
                edgePara.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                break;
            case 20:
                edgePara.gravity = Gravity.TOP | Gravity.LEFT;
                break;
            case 21:
                edgePara.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                break;
            case 22:
                edgePara.gravity = Gravity.BOTTOM | Gravity.LEFT;
                break;
            case 31:
                edgePara.gravity = Gravity.BOTTOM | Gravity.CENTER;
                break;
        }

        int edge1offset = edge1Shared.getInt(Cons.EDGE_OFFSET_KEY, Cons.EDGE_OFFSET_DEFAULT);

        if (edgePosition == 12 | edgePosition == 22) {
            edgePara.y = (int) (edge1offset * mScale);
        } else if (edgePosition == 31) {
            edgePara.x = -(int) (edge1offset * mScale);
        } else {
            edgePara.y = -(int) (edge1offset * mScale);
        }
        switch (edgeTag) {
            case Cons.TAG_EDGE_1:
                edge1Para = edgePara;
                break;
            case Cons.TAG_EDGE_2:
                edge2Para = edgePara;
        }
        return edgePara;

    }


    public void setOnTouchListener(boolean edge1On, boolean edge2On) {
        if (edge1On) {
            edge1View.setOnTouchListener(this);
        }

        if (edge2On) {
            edge2View.setOnTouchListener(this);
        }
    }



    private int getGuideColor() {
        return defaultShared.getInt(EdgeSetting.GUIDE_COLOR_KEY, Color.argb(255, 255, 64, 129));
    }

    private int getEdgeSensitive(String edgeTag) {
        switch (edgeTag) {
            case Cons.TAG_EDGE_1:
                return edge1Shared.getInt(Cons.EDGE_SENSIIVE_KEY, Cons.EDGE_SENSITIVE_DEFAULT);
        }
        return 0;
    }

    public int getEdgeOffset(String edgeTag) {
        switch (edgeTag) {
            case Cons.TAG_EDGE_1:
                return edge1Shared.getInt(Cons.EDGE_OFFSET_KEY, Cons.EDGE_OFFSET_DEFAULT);
            case Cons.TAG_EDGE_2:
                return edge2Shared.getInt(Cons.EDGE_OFFSET_KEY, Cons.EDGE_OFFSET_DEFAULT);
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
                recentIcons[i].setImageDrawable(null);
            } else {
                Utility.setImageForShortcut(shortcuts[i], getPackageManager(), recentIcons[i], getApplicationContext(), getIconPack(), null, true);

            }
        }
        try {
            windowManager.addView(circleShortcutsView, getCircleShortcutsViewPara());
        } catch (IllegalStateException e) {
            Log.e(TAG, " item_view has already been added to the window manager");
        }
    }

    public void setCircleShortcutsViewPara() {
        circleShortcutsViewPara = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                PixelFormat.TRANSLUCENT);
        circleShortcutsViewPara.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
    }



    private WindowManager.LayoutParams getCircleShortcutsViewPara() {
        if (circleShortcutsViewPara != null) {
            return circleShortcutsViewPara;
        }

        setCircleShortcutsViewPara();
        return circleShortcutsViewPara;
    }

    private IconPackManager.IconPack getIconPack() {
        if (iconPack != null) {
            return iconPack;
        }

        setIconPack();
        return iconPack;

    }

    private void setIconPack() {
        String iconPackPacka = defaultShared.getString(EdgeSetting.ICON_PACK_PACKAGE_NAME_KEY, "none");
        if (!iconPackPacka.equals("none")) {
            IconPackManager iconPackManager = new IconPackManager();
            iconPackManager.setContext(getApplicationContext());
            iconPack = iconPackManager.getInstance(iconPackPacka);
            if (iconPack != null) {
                iconPack.load();
            }
        }
    }





}
