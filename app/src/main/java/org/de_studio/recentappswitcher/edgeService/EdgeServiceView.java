package org.de_studio.recentappswitcher.edgeService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.service.EdgeSetting;
import org.de_studio.recentappswitcher.service.MyImageView;

/**
 * Created by HaiNguyen on 8/19/16.
 */
public class EdgeServiceView extends Service implements View.OnTouchListener {
    WindowManager windowManager;
    Vibrator vibrator;
    SharedPreferences defaultShared, edge1Shared, edge2Shared;
    FrameLayout itemsView;
    MyImageView[] recentIcons;
    FrameLayout backgroundFrame;
    WindowManager.LayoutParams backgroundParams;
    View edge1View, edge2View;


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

    public void createRecentIconsList() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        itemsView = (FrameLayout) layoutInflater.inflate(R.layout.items, null);
        recentIcons = new MyImageView[6];
        recentIcons[0] = (MyImageView) itemsView.findViewById(R.id.item_0);
        recentIcons[1] = (MyImageView) itemsView.findViewById(R.id.item_1);
        recentIcons[2] = (MyImageView) itemsView.findViewById(R.id.item_2);
        recentIcons[3] = (MyImageView) itemsView.findViewById(R.id.item_3);
        recentIcons[4] = (MyImageView) itemsView.findViewById(R.id.item_4);
        recentIcons[5] = (MyImageView) itemsView.findViewById(R.id.item_5);
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
        }
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

    public void setEdge2View(int edge2Position, float mScale) {
        edge2View = new ImageView(getApplicationContext());
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
    }






}
