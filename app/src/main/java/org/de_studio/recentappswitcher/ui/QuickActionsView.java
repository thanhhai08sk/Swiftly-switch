package org.de_studio.recentappswitcher.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.model.Item;

import java.util.ArrayList;

/**
 * Created by HaiNguyen on 2/9/17.
 */

public class QuickActionsView extends View {
    public static final float ARC_SIZE_DP = 56;
    private Paint backgroundPaint;
    float radius;
    float mScale;
    int visibleItem = 0;
    ArrayList<Item> actions;
    Bitmap[] bitmaps;
    RectF[] rectFs;
    Path path;
    float iconSize;
    int firstItemAcr = 29;
    int middleItemArc = 39;
    int lastItemArc = 69;
    IconPackManager.IconPack iconPack;

    public QuickActionsView(Context context, IconPackManager.IconPack iconPack, ArrayList<Item> actions) {
        super(context);
        this.iconPack = iconPack;
        this.actions = actions;
        init();

    }


    private void init() {
        mScale = getResources().getDisplayMetrics().density;
        iconSize = 36 * mScale;
        backgroundPaint = new Paint();
        backgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(ARC_SIZE_DP * mScale);
        radius = ARC_SIZE_DP * mScale;
        path = new Path();
        for (int i = 0; i < actions.size(); i++) {
            bitmaps[i] = Utility.getItemBitmap(actions.get(i), getContext(), iconPack);
            rectFs[i] = new RectF();
        }

    }

    public void show(int itemPosition) {
        if (itemPosition != -1) {
            visibleItem = itemPosition;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int centerX = Math.round(canvasWidth * 0.5f);
        int centerY = Math.round(canvasHeight * 0.5f);
        double angular ;
        float left, top;
        path.reset();
        switch (visibleItem) {
            case 0:
                path.addArc(0, 0, canvasWidth, canvasHeight, -120, firstItemAcr);
                angular = Math.toDegrees(-120 + firstItemAcr * 0.5);
                left = (float) (centerX - radius * Math.cos(angular));
                top = (float) (centerY - radius * Math.sin(angular));
                rectFs[visibleItem].set(left
                        , top
                        , left + iconSize
                        , top + iconSize);
                break;
            case 1:
                path.addArc(0, 0, canvasWidth, canvasHeight, -160, middleItemArc);
                angular = Math.toDegrees(-160 + middleItemArc * 0.5);
                left = (float) (centerX - radius * Math.cos(angular));
                top = (float) (centerY - radius * Math.sin(angular));
                rectFs[visibleItem].set(left
                        , top
                        , left + iconSize
                        , top + iconSize);
                break;
            case 2:
                path.addArc(0, 0, canvasWidth, canvasHeight, -200, middleItemArc);
                angular = Math.toDegrees(-200 + middleItemArc * 0.5);
                left = (float) (centerX - radius * Math.cos(angular));
                top = (float) (centerY - radius * Math.sin(angular));
                rectFs[visibleItem].set(left
                        , top
                        , left + iconSize
                        , top + iconSize);
                break;
            case 3:
                path.addArc(0, 0, canvasWidth, canvasHeight, -270, lastItemArc);
                angular = Math.toDegrees(-270 + lastItemArc * 0.5);
                left = (float) (centerX - radius * Math.cos(angular));
                top = (float) (centerY - radius * Math.sin(angular));
                rectFs[visibleItem].set(left
                        , top
                        , left + iconSize
                        , top + iconSize);
                break;
        }

        canvas.drawPath(path, backgroundPaint);
        canvas.drawBitmap(bitmaps[visibleItem], null, rectFs[visibleItem], backgroundPaint);



    }
}
