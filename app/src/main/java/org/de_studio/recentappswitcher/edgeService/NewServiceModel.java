package org.de_studio.recentappswitcher.edgeService;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseModel;

/**
 * Created by HaiNguyen on 12/23/16.
 */

public class NewServiceModel extends BaseModel {

    float mScale, iconScale;

    float iconWidth, haftIconWidth;


    void setup() {
        iconWidth = Cons.DEFAULT_ICON_WIDTH * mScale * iconScale;
        haftIconWidth = iconWidth / 2;
    }

    float convertDpToPixel(int dp) {
        return dp * mScale;
    }

    public int getCircleAndQuickActionTriggerId(IconsXY iconsXY, int radius, float x_init, float y_init, float x, float y, int position, int iconsCount) {
        float circleSizePxl = radius * mScale;
        double xInitDouble = (double) x_init;
        double yInitDouble = (double) y_init;
        double xDouble = (double) x;
        double yDouble = (double) y;

        double distanceFromInitPxl = Math.sqrt(Math.pow(xDouble - xInitDouble,2) + Math.pow(yDouble - yInitDouble, 2));
        double startQuickActionZonePxl = (double) (35 * mScale + circleSizePxl);

        double ang30 = 0.1666*Math.PI;
        double ang70 = 0.3889*Math.PI;
        double ang110 = 0.6111*Math.PI;
        double alpha;
        if (distanceFromInitPxl < startQuickActionZonePxl) {
            double distance;
            double distanceNeeded = 35 * mScale;
            for (int i = 0; i < iconsCount; i++) {
                distance = Math.sqrt(Math.pow(xDouble - (double) (iconsXY.xs[i] + haftIconWidth), 2) + Math.pow(yDouble - (double) (iconsXY.ys[i] + haftIconWidth), 2));
                if (distance <= distanceNeeded) {
                    return i;
                }
            }
        } else {
            if (Utility.rightLeftOrBottom(position) == Cons.POSITION_BOTTOM) {
                alpha = Math.acos((x_init - x) / distanceFromInitPxl);
            }else {
                alpha = Math.acos((y_init-y)/distanceFromInitPxl);
            }
            if (alpha < ang30) {
                return 10;
            }else if (alpha < ang70) {
                return 11;
            }else if (alpha < ang110) {
                return 12;
            }else return 13;
        }
        return -1;

    }

    public IconsXY calculateCircleIconPositions(int radius, int edgePosition, float xInit, float yInit, int iconCount) {
        float circleSizePxl = radius * mScale;
        float[] xs = new float[iconCount];
        float[] ys = new float[iconCount];
        double alpha, beta;
        double[] alphaN = new double[iconCount];
        switch (iconCount) {
            case 4:
                alpha = 0.111 * Math.PI; // 20 degree
                break;
            case 5:
                alpha = 0.111 * Math.PI; // 20 degree
                break;
            case 6:
                alpha = 0.0556 * Math.PI; // 10 degree
                break;
            case 7:
                alpha = 0.0566 * Math.PI;
                break;
            default:
                alpha = 0.0556 * Math.PI;
                break;
        }
        beta = Math.PI - 2 * alpha;
        for (int i = 0; i < iconCount; i++) {
            alphaN[i] = alpha + i * (beta / (iconCount - 1));
            switch (edgePosition / 10) {
                case Cons.POSITION_RIGHT:
                    xs[i] = xInit - circleSizePxl * (float) Math.sin(alphaN[i]) - iconWidth/2;
                    ys[i] = yInit - circleSizePxl * (float) Math.cos(alphaN[i]) - iconWidth/2;
                    break;
                case Cons.POSITION_LEFT:
                    xs[i] = xInit + circleSizePxl * (float) Math.sin(alphaN[i]) - iconWidth/2;
                    ys[i] = yInit - circleSizePxl * (float) Math.cos(alphaN[i]) - iconWidth/2;
                    break;
                case Cons.POSITION_BOTTOM:
                    xs[i] = xInit - circleSizePxl * (float) Math.cos(alphaN[i]) - iconWidth/2;
                    ys[i] = yInit - circleSizePxl * (float) Math.sin(alphaN[i]) - iconWidth/2;
                    break;
            }
        }
        return new IconsXY(xs, ys);
    }



    @Override
    public void clear() {

    }

    public class IconsXY {
        float[] xs;
        float[] ys;

        public IconsXY(float[] xs, float[] ys) {
            this.xs = xs;
            this.ys = ys;
        }
    }

}
