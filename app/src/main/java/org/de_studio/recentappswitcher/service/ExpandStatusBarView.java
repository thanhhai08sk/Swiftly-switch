package org.de_studio.recentappswitcher.service;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import org.de_studio.recentappswitcher.R;

/**
 * Created by hai on 12/29/2015.
 */
public class ExpandStatusBarView extends View {
    private String text = "show notification";
    private int position;
    private Color backgroundColor;
    private Color textColor;
    private Paint textPaint;
    private Paint backgroundPaint;
    private Path path;
    private int width,height,x_init,y_init;
    private RectF oval;
    private int radius;
    private int homwBackNoti = 3;
    private int ovalOffset =0;
    private float mScale;
    private int textSize = 16, strokeWidth = 30; // in dp

    public ExpandStatusBarView(Context context, int radius,int ovalOffset, String text, int positionOfEdge, int positionOfArc){
        super(context);
        this.radius = radius;
        this.ovalOffset = ovalOffset;
        this.text = text;
        this.position = positionOfEdge;
        homwBackNoti = positionOfArc;
        init();
        Log.e("ExpandStatusBarView ", "position%10 = " + position % 10);
    }

    public String getText(){
        return text;
    }


    public Color getTextColor(){
        return textColor;
    }
    public void setTextColor(Color color){
        textColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        oval.set(ovalOffset, ovalOffset,ovalOffset + radius * 2,ovalOffset+ radius * 2);
        if (homwBackNoti ==3){
            switch (position/10){
                case 1: path.addArc(oval, -270, 70);
                    break;
                case 2: path.addArc(oval, 20, 70);
                    break;
                case 3: path.addArc(oval, -70,70);
                    break;
            }
        }else if (homwBackNoti==1){
            switch (position/10){
                case 1: path.addArc(oval, -115, 25);
                    break;
                case 2: path.addArc(oval, -90, 25);
                    break;
                case 3: path.addArc(oval,-180,25);
                    break;
            }
        }else if (homwBackNoti ==2){
            switch (position/10){
                case 1: path.addArc(oval, -160, 40);
                    break;
                case 2: path.addArc(oval, -60, 40);
                    break;
                case 3: path.addArc(oval,-150,40);
                    break;
            }
        }
        canvas.drawPath(path,backgroundPaint);
        canvas.drawTextOnPath(text.toUpperCase(), path, 0, ((float)(textSize/2.5))*mScale, textPaint);

    }
    private void init(){
        mScale = getResources().getDisplayMetrics().density;
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(textSize * mScale);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(ContextCompat. getColor(getContext(), R.color.colorPrimary));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth * mScale);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        oval = new RectF();

        path = new Path();

    }

}
