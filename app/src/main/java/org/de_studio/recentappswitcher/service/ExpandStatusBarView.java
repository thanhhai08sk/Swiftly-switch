package org.de_studio.recentappswitcher.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;

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
    private int homwBackNoti = 4;
    private int ovalOffset =0;
    private float mScale,o1x,o1y   ;
    private int textSize = 16, strokeWidth = 35; // in dp
    private Bitmap actionBitmap;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private boolean isBackground = true;

    public ExpandStatusBarView(Context context, int radius,int ovalOffset, int positionOfEdge, int positionOfArc){
        super(context);
        this.radius = radius;
        this.ovalOffset = ovalOffset;
        this.text = text;
        mContext = context;
        this.position = positionOfEdge;
        homwBackNoti = positionOfArc;
        sharedPreferences = context.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
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
        oval.set(ovalOffset, ovalOffset, ovalOffset + radius * 2, ovalOffset + radius * 2);
        int action1Acr = 29;
        int action23Arc = 39;
        int action4Arc = 69;
        if (homwBackNoti ==4){
            switch (position/10){
                case 1: path.addArc(oval, -270, action4Arc);
                    break;
                case 2: path.addArc(oval, 20, action4Arc);
                    break;
                case 3: path.addArc(oval, -70,action4Arc);
                    break;
            }
        }else if (homwBackNoti==1){
            switch (position/10){
                case 1: path.addArc(oval, -120, action1Acr);
                    break;
                case 2: path.addArc(oval, -90, action1Acr);
                    break;
                case 3: path.addArc(oval,-180,action1Acr);
                    break;
            }
        }else if (homwBackNoti ==2){
            switch (position/10){
                case 1: path.addArc(oval, -160, action23Arc);
                    break;
                case 2: path.addArc(oval, -60, action23Arc);
                    break;
                case 3: path.addArc(oval,-150,action23Arc);
                    break;
            }
        }else if (homwBackNoti == 3) {
            switch (position / 10) {
                case 1:
                    path.addArc(oval, -200,action23Arc);
                    break;
                case 2:
                    path.addArc(oval,-20,action23Arc);
                    break;
                case 3:
                    path.addArc(oval,-110,action23Arc);
            }
        }
        if (isBackground) {
            canvas.drawPath(path, backgroundPaint);
        }

        canvas.drawBitmap(actionBitmap, o1x, o1y, textPaint);
//        canvas.drawTextOnPath(text.toUpperCase(), path, 0, ((float) (textSize / 2.5)) * mScale, textPaint);

    }

    public void drawBackground(boolean isBackground) {
        this.isBackground = isBackground;
        invalidate();
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
//        backgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.outRing));
        int  color1 = ContextCompat.getColor(getContext(), R.color.outRing);
        int color2 = ContextCompat.getColor(getContext(), R.color.outOutRing);
        RadialGradient gradient = new RadialGradient(ovalOffset + radius, ovalOffset + radius, radius +20*mScale,
                new int[]{color1, color2}, new float[]{0.85f, 1.0f}, Shader.TileMode.CLAMP);
        backgroundPaint.setShader(gradient);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth * mScale);
//        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        oval = new RectF();

        path = new Path();
        float sin;
        float cos;
        actionBitmap = Utility.getBitmapFromAction(mContext, sharedPreferences, homwBackNoti);
//        switch (homwBackNoti) {
//            case 1:
//                actionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_home);
//                break;
//            case 2:
//                actionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_back);
//                break;
//            case 3:
//                actionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_recent);
//                break;
//            case 4:
//                actionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_expand);
//                break;
//        }

        switch (position / 10) {
            case 1:
                switch (homwBackNoti) {
                    case 1:
                        sin = (float)(Math.sin(0.0833 * Math.PI)); //15
                        cos = (float) (Math.cos(0.0833 * Math.PI));  //15
                        o1x = ovalOffset + (float)radius -(float)(radius) * sin - 16*mScale;
                        o1y = ovalOffset +(float)radius - (float)(radius)*cos - 16*mScale;
                        break;
                    case 2:
                        sin = (float)(Math.sin(0.2778 * Math.PI)); //50
                        cos = (float) (Math.cos(0.2778 * Math.PI));  //50
                        o1x = ovalOffset + (float)radius -(float)(radius) * sin - 16*mScale;
                        o1y = ovalOffset +(float)radius - (float)(radius)*cos - 16*mScale;
                        break;
                    case 3:
                        o1x = ovalOffset - 16*mScale;
                        o1y = ovalOffset +(float)radius - 16*mScale;
                        break;
                    case 4:
                        sin = (float)(Math.sin(0.1944 * Math.PI)); //35
                        cos = (float) (Math.cos(0.1944 * Math.PI));  //35
                        o1x = ovalOffset + (float)radius -(float)(radius) * sin - 16*mScale;
                        o1y = ovalOffset +(float)radius + (float)(radius)*cos - 16*mScale;
                        break;
                }
                break;
            case 2:
                switch (homwBackNoti) {
                    case 1:
                        sin = (float)(Math.sin(0.0833 * Math.PI)); //15
                        cos = (float) (Math.cos(0.0833 * Math.PI));  //15
                        o1x = ovalOffset + (float)radius +(float)(radius) * sin - 16*mScale;
                        o1y = ovalOffset +(float)radius - (float)(radius)*cos - 16*mScale;
                        break;
                    case 2:
                        sin = (float)(Math.sin(0.2778 * Math.PI)); //50
                        cos = (float) (Math.cos(0.2778 * Math.PI));  //50
                        o1x = ovalOffset + (float)radius +(float)(radius) * sin - 16*mScale;
                        o1y = ovalOffset +(float)radius - (float)(radius)*cos - 16*mScale;
                        break;
                    case 3:
                        o1x = ovalOffset - 16 * mScale + 2 * ((float) radius);
                        o1y = ovalOffset +(float)radius - 16*mScale;
                        break;
                    case 4:
                        sin = (float)(Math.sin(0.1944 * Math.PI)); //35
                        cos = (float) (Math.cos(0.1944 * Math.PI));  //35
                        o1x = ovalOffset + (float)radius +(float)(radius) * sin - 16*mScale;
                        o1y = ovalOffset +(float)radius + (float)(radius)*cos - 16*mScale;
                        break;
                }
                break;
            case 3:
                switch (homwBackNoti) {
                    case 1:
                        sin = (float)(Math.sin(0.0833 * Math.PI)); //15
                        cos = (float) (Math.cos(0.0833 * Math.PI));  //15
                        o1x = ovalOffset + (float)radius -(float)(radius) * cos - 16*mScale;
                        o1y = ovalOffset +(float)radius - (float)(radius)*sin - 16*mScale;
                        break;
                    case 2:
                        sin = (float)(Math.sin(0.2778 * Math.PI)); //50
                        cos = (float) (Math.cos(0.2778 * Math.PI));  //50
                        o1x = ovalOffset + (float)radius -(float)(radius) * cos - 16*mScale;
                        o1y = ovalOffset +(float)radius - (float)(radius)*sin - 16*mScale;
                        break;
                    case 3:
                        o1x = ovalOffset + (float)(radius) - 16*mScale;
                        o1y = ovalOffset - 16*mScale;
                        break;
                    case 4:
                        sin = (float)(Math.sin(0.1944 * Math.PI)); //35
                        cos = (float) (Math.cos(0.1944 * Math.PI));  //35
                        o1x = ovalOffset + (float)radius +(float)(radius) * cos - 16*mScale;
                        o1y = ovalOffset +(float)radius - (float)(radius)*sin - 16*mScale;
                        break;
                }
                break;
        }



    }

}
