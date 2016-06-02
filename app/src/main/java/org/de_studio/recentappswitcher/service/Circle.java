package org.de_studio.recentappswitcher.service;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by HaiNguyen on 6/2/16.
 */
public class Circle extends View {
    private static final int START_ANGLE_POINT = 135;

    private final Paint paint;
    private final RectF rect;
    private float mScale;

    private float angle;

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScale = getResources().getDisplayMetrics().density;
        final int strokeWidth =(int) (4*mScale);
        int circleSize;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        //Circle color
        paint.setColor(Color.CYAN);
        circleSize =(int) (116* mScale);

        rect = new RectF(strokeWidth/2, strokeWidth/2, circleSize + strokeWidth/2, circleSize + strokeWidth/2);
        //Initial Angle (optional, it can be zero)
        angle = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rect, START_ANGLE_POINT, angle, false, paint);
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
        requestLayout();
    }
}
