package org.de_studio.recentappswitcher.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import org.de_studio.recentappswitcher.R;
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
    int itemCount;
    int visibleItem = 0;
    ArrayList<Item> actions;
    Bitmap[] bitmaps;
    Path path;
    int firstItemAcr = 29;
    int middleItemArc = 39;
    int lastItemArc = 69;

    public QuickActionsView(Context context) {
        super(context);
        init();
    }

    public QuickActionsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mScale = getResources().getDisplayMetrics().density;
        backgroundPaint = new Paint();
        backgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(ARC_SIZE_DP * mScale);
        radius = ARC_SIZE_DP * mScale;
        path = new Path();

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
        path.reset();
        switch (visibleItem) {
            case 0:
                path.addArc(0, 0, canvasWidth, canvasHeight, -270, firstItemAcr);
                break;
            case 1:
                path.addArc(0, 0, canvasWidth, canvasHeight, -120, middleItemArc);
                break;
            case 2:
                path.addArc(0, 0, canvasWidth, canvasHeight, -160, middleItemArc);
                break;
            case 3:
                path.addArc(0, 0, canvasWidth, canvasHeight, -200, lastItemArc);
                break;
        }

        canvas.drawPath(path, backgroundPaint);



    }
}
