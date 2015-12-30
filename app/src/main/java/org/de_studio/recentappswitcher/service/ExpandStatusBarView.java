package org.de_studio.recentappswitcher.service;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

/**
 * Created by hai on 12/29/2015.
 */
public class ExpandStatusBarView extends View {
    private String text = "____show notification____";
    private int position;
    private Color backgroundColor;
    private Color textColor;
    private Paint textPaint;
    private Paint backgroundPaint;
    private Path path;
    private int width,height,x_init,y_init;
    private RectF oval;
    private int radius;
    private boolean isNoti = true;

    public ExpandStatusBarView(Context context){
        super(context);
        init();
    }

    public void setRadius(int radius){
        this.radius = radius;
    }
    public String getText(){
        return text;
    }
    public void setText(String text){
        this.text = text;
        isNoti = false;
    }
    public void setPosition(int position){
        this.position = position;
    }


    public Color getTextColor(){
        return textColor;
    }
    public void setTextColor(Color color){
        textColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("expand", "onDraw");
        oval.set(0, 0, radius * 2, radius * 2);
        if (isNoti){
            switch (position){
                case 10: path.addArc(oval, -270, 90);
                    break;
                case 11: path.addArc(oval, -270, 90);
                    break;
                case 12: path.addArc(oval, -270, 90);
                    break;
                case 20: path.addArc(oval, 0, 90);
                    break;
                case 21: path.addArc(oval, 0, 90);
                    break;
                case 22: path.addArc(oval, 0, 90);
                    break;
            }
        }else {
            switch (position){
                case 10: path.addArc(oval, -180, 90);
                    break;
                case 11: path.addArc(oval, -180, 90);
                    break;
                case 12: path.addArc(oval, -180, 90);
                    break;
                case 20: path.addArc(oval, 90, 90);
                    break;
                case 21: path.addArc(oval, 90, 90);
                    break;
                case 22: path.addArc(oval, 90, 90);
                    break;
            }
        }

        canvas.drawTextOnPath(text, path, 0, 0, textPaint);
    }
    private void init(){
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setColor(Color.CYAN);
        textPaint.setTextSize(50f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.CYAN);
        backgroundPaint.setStyle(Paint.Style.STROKE);

        oval = new RectF();

        path = new Path();

    }

}
