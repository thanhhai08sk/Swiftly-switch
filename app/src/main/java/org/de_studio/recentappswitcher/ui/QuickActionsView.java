package org.de_studio.recentappswitcher.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.model.Slot;

import io.realm.RealmList;

/**
 * Created by HaiNguyen on 2/9/17.
 */

public class QuickActionsView extends View {
    private static final String TAG = QuickActionsView.class.getSimpleName();
    public static final float ARC_SIZE_DP = 56;
    private Paint backgroundPaint;
    float arcSize;
    float mScale;
    int visibleItem = 0;
    RealmList<Slot> actions;
    Bitmap[] bitmaps;
    RectF[] rectFs;
    Path path;
    float iconSize;
    int firstItemAcr = 29;
    int middleItemArc = 39;
    int lastItemArc = 69;
    IconPackManager.IconPack iconPack;

    public QuickActionsView(Context context, IconPackManager.IconPack iconPack, RealmList<Slot> actions) {
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
        arcSize = ARC_SIZE_DP * mScale;
        path = new Path();
        rectFs = new RectF[actions.size()];
        bitmaps = new Bitmap[actions.size()];
        for (int i = 0; i < actions.size(); i++) {
            bitmaps[i] = Utility.getItemBitmap(actions.get(i).stage1Item, getContext(), iconPack);
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
        float radius = centerX;
        double angular ;
        float left, top;
        path.reset();
        switch (visibleItem) {
            case 0:
                path.addArc(0, 0, canvasWidth, canvasHeight, -120, firstItemAcr);
                angular = Math.toRadians(-120 + firstItemAcr * 0.5);
                left = (float) (centerX + radius * Math.cos(angular) - iconSize/2);
                top = (float) (centerY + radius * Math.sin(angular) - iconSize/2);
                rectFs[visibleItem].set(left
                        , top
                        , left + iconSize
                        , top + iconSize);
                break;
            case 1:
                path.addArc(0, 0, canvasWidth, canvasHeight, -160, middleItemArc);
                angular = Math.toRadians(-160 + middleItemArc * 0.5);

                left = (float) (centerX + radius * Math.cos(angular) - iconSize/2);
                top = (float) (centerY + radius * Math.sin(angular) - iconSize/2);
                rectFs[visibleItem].set(left
                        , top
                        , left + iconSize
                        , top + iconSize);
                break;
            case 2:
                path.addArc(0, 0, canvasWidth, canvasHeight, -200, middleItemArc);
                angular = Math.toRadians(-200 + middleItemArc * 0.5);
                left = (float) (centerX + radius * Math.cos(angular) - iconSize/2);
                top = (float) (centerY + radius * Math.sin(angular) - iconSize/2);
                Log.e(TAG, "onDraw: left " + left + "\ntop " + top
                        + "\ncenterX " + centerX
                        + "\ncenterY" + centerY + "\nangular " + (-200 + middleItemArc * 0.5));
                rectFs[visibleItem].set(left
                        , top
                        , left + iconSize
                        , top + iconSize);
                break;
            case 3:
                path.addArc(0, 0, canvasWidth, canvasHeight, -270, lastItemArc);
                angular = Math.toRadians(-270 + lastItemArc * 0.5);
                left = (float) (centerX + radius * Math.cos(angular) - iconSize/2);
                top = (float) (centerY + radius * Math.sin(angular) - iconSize/2);
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
