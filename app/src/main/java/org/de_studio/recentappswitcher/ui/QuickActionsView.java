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
    private Paint iconPaint;
    float arcSize;
    float mScale;
    int visibleItem = 0;
    RealmList<Slot> actions;
    Bitmap[] bitmaps;
    RectF[] rectFs;
    RectF rectF;
    Path path;
    int iconSize;
    int[] sweepAngles;
    float[] startAngle;
    IconPackManager.IconPack iconPack;
    int edgePosition;
    boolean showAll;

    public QuickActionsView(Context context, IconPackManager.IconPack iconPack, RealmList<Slot> actions, int edgePosition, boolean showAll) {
        super(context);
        this.iconPack = iconPack;
        this.actions = actions;
        this.edgePosition = edgePosition;
        this.showAll = showAll;
        init();

    }




    private void init() {
        mScale = getResources().getDisplayMetrics().density;
        iconSize = (int) (ICON_SIZE_DP * mScale);
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.quick_actions_background));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(ARC_SIZE_DP * mScale);
        iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        arcSize = ARC_SIZE_DP * mScale;
        path = new Path();
        rectFs = new RectF[getSize()];
        rectF = new RectF();
        bitmaps = new Bitmap[getSize()];

        sweepAngles = new int[getSize()];
        if (getSize() == 4) {
            sweepAngles[0] = 30;
            sweepAngles[1] = sweepAngles[2] = 40;
            sweepAngles[3] = 70;
        } else {
            for (int i = 0; i < sweepAngles.length; i++) {
                sweepAngles[i] = 180 / getSize();
            }
        }

        startAngle = new float[getSize()];
        for (int i = 0; i < getSize(); i++) {
            if (actions.get(i).stage1Item != null) {
//                bitmaps[i] = Utility.getItemBitmap(actions.get(i).stage1Item, getContext(), iconPack);
                try {
                    bitmaps[i] = Bitmap.createScaledBitmap(Utility.getItemBitmap(actions.get(i).stage1Item, getContext(), iconPack), iconSize, iconSize, true);

                } catch (NullPointerException e) {
                    Log.e(TAG, "init: Null");
                    e.printStackTrace();
                }
                rectFs[i] = new RectF();

                switch (Utility.rightLeftOrBottom(edgePosition)) {
                    case Cons.POSITION_RIGHT:
                        startAngle[i] = -270;
                        for (int j = i; j < getSize(); j++) {
                            if (j + 1 < getSize()) {
                                startAngle[i] = startAngle[i] + sweepAngles[j + 1];
                            }
                        }
                        break;
                    case Cons.POSITION_LEFT:
                        startAngle[i] = -90;
                        for (int j = 0; j <= i; j++) {
                            if (j - 1 >= 0) {
                                startAngle[i] = startAngle[i] + sweepAngles[j - 1];
                            }
                        }
                        break;
                    case Cons.POSITION_BOTTOM:
                        startAngle[i] = -180;
                        for (int j = 0; j <= i; j++) {
                            if (j - 1 >= 0) {
                                startAngle[i] = startAngle[i] + sweepAngles[j - 1];
                            }
                        }
                        break;
                }

            }
        }

    }

    public void show(int itemPosition) {
            visibleItem = itemPosition;
            invalidate();
    }

    private int getSize() {
        return actions.size();
    }
    @Override
    protected void onDraw(Canvas canvas) {

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int centerX = Math.round(canvasWidth * 0.5f);
        int centerY = Math.round(canvasHeight * 0.5f);
        Log.e(TAG, "onDraw: canvas width = " + canvasWidth +
                "\nheight = " + canvasHeight +
                "\ncenterX = " + centerX +
                "\ncenterY = " + centerY);


        if (visibleItem != -1 && bitmaps[visibleItem] != null) {
            path.reset();
//            path.addArc(0, 0, canvasWidth, canvasHeight, startAngle[visibleItem], sweepAngles[visibleItem]);
            rectF.set(0, 0, canvasWidth, canvasHeight);
            path.addArc(rectF, startAngle[visibleItem], sweepAngles[visibleItem]);
            canvas.drawPath(path, backgroundPaint);
        }

        if (showAll) {
            for (int i = 0; i < bitmaps.length; i++) {
                drawIconBitmap(canvas, centerX, centerY, i);
            }
        } else if (visibleItem != -1 && bitmaps[visibleItem] != null) {
            drawIconBitmap(canvas, centerX, centerY, visibleItem);
        }


    }

    private void drawIconBitmap(Canvas canvas, int centerX, int centerY, int i) {
        if (bitmaps[i] != null) {
            double iconAngle;
            iconAngle = Math.toRadians(startAngle[i] + sweepAngles[i] * 0.5);
            setIconRectF(centerX, centerY, centerX, iconAngle, i);
//        canvas.drawBitmap(bitmaps[i], null, rectFs[i], backgroundPaint);
            canvas.drawBitmap(bitmaps[i], rectFs[i].left,rectFs[i].top,iconPaint);
        }
    }

    private void setIconRectF(int centerX, int centerY, float radius, double iconAngular,int iconPosition) {
        float left;
        float top;
        left = (float) (centerX + radius * Math.cos(iconAngular) - iconSize/2);
        top = (float) (centerY + radius * Math.sin(iconAngular) - iconSize/2);
        if (rectFs[iconPosition] != null) {
            rectFs[iconPosition].set(left
                    , top
                    , left + iconSize
                    , top + iconSize);
        }
    }
}
