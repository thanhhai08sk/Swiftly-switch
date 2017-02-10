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

import org.de_studio.recentappswitcher.Cons;
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
    public static final int ICON_SIZE_DP = 36;
    private Paint backgroundPaint;
    float arcSize;
    float mScale;
    int visibleItem = 0;
    RealmList<Slot> actions;
    Bitmap[] bitmaps;
    RectF[] rectFs;
    Path path;
    float iconSize;
    int[] sweepAngles;
    IconPackManager.IconPack iconPack;
    int edgePosition;

    public QuickActionsView(Context context, IconPackManager.IconPack iconPack, RealmList<Slot> actions, int edgePosition) {
        super(context);
        this.iconPack = iconPack;
        this.actions = actions;
        this.edgePosition = edgePosition;
        init();

    }


    private void init() {
        mScale = getResources().getDisplayMetrics().density;
        iconSize = ICON_SIZE_DP * mScale;
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.quick_actions_background));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(ARC_SIZE_DP * mScale);

        arcSize = ARC_SIZE_DP * mScale;
        path = new Path();
        rectFs = new RectF[actions.size()];
        bitmaps = new Bitmap[actions.size()];
        sweepAngles = new int[actions.size()];
        for (int i = 0; i < actions.size(); i++) {
            bitmaps[i] = Utility.getItemBitmap(actions.get(i).stage1Item, getContext(), iconPack);
            rectFs[i] = new RectF();
        }
        sweepAngles[0] = 30;
        sweepAngles[1] = sweepAngles[2] = 40;
        sweepAngles[3] = 70;
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
        float startAngle = 0;
        switch (Utility.rightLeftOrBottom(edgePosition)) {
            case Cons.POSITION_RIGHT:
                startAngle = -270;
                for (int i = visibleItem; i < actions.size(); i++) {
                    if (i + 1 < actions.size()) {
                        startAngle = startAngle + sweepAngles[i + 1];
                    }
                }
                break;
            case Cons.POSITION_LEFT:
                startAngle = -90;
                for (int i = 0; i <= visibleItem; i++) {
                    if (i - 1 > 0) {
                        startAngle = startAngle + sweepAngles[i - 1];
                    }
                }
                break;
            case Cons.POSITION_BOTTOM:
                startAngle = -180;
                for (int i = 0; i <= visibleItem; i++) {
                    if (i - 1 > 0) {
                        startAngle = startAngle + sweepAngles[i - 1];
                    }
                }
                break;
        }
        Log.e(TAG, "onDraw: startAngle = " + startAngle);

        double iconAngle;
        path.reset();
        path.addArc(0, 0, canvasWidth, canvasHeight, startAngle, sweepAngles[visibleItem]);
        iconAngle = Math.toRadians(startAngle + sweepAngles[visibleItem] * 0.5);
        setIconRectF(centerX, centerY, centerX, iconAngle);
        canvas.drawPath(path, backgroundPaint);
        canvas.drawBitmap(bitmaps[visibleItem], null, rectFs[visibleItem], backgroundPaint);



    }

    private void setIconRectF(int centerX, int centerY, float radius, double iconAngular) {
        float left;
        float top;
        left = (float) (centerX + radius * Math.cos(iconAngular) - iconSize/2);
        top = (float) (centerY + radius * Math.sin(iconAngular) - iconSize/2);
        rectFs[visibleItem].set(left
                , top
                , left + iconSize
                , top + iconSize);
    }
}
