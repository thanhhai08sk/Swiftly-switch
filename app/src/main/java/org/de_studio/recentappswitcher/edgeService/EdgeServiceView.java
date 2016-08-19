package org.de_studio.recentappswitcher.edgeService;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

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
    SharedPreferences defaultShared, edge1Shared, edge2Shared;
    FrameLayout itemsView;
    MyImageView[] recentIcons;
    FrameLayout backgroundFrame;
    WindowManager.LayoutParams backgroundParams;


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




}
