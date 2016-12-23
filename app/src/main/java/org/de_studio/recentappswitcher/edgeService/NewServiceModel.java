package org.de_studio.recentappswitcher.edgeService;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseModel;

/**
 * Created by HaiNguyen on 12/23/16.
 */

public class NewServiceModel extends BaseModel {

    float mScale, iconScale;

    float iconWidth;


    void setup() {
        iconWidth = Cons.DEFAULT_ICON_WIDTH * mScale * iconScale;
    }

    float convertDpToPixel(int dp) {
        return dp * mScale;
    }


    public IconsXY calculateCircleIconPositions(int radius, int edgePosition, float xInit, float yInit, int iconCount) {
        float circleSizePxl = radius * mScale;

        float[] xs = new float[iconCount];
        float[] ys = new float[iconCount];
        double alpha, beta;
        double[] alphaN = new double[iconCount];
        switch (iconCount) {
            case 4:
//                alpha = 0.1389*Math.PI; // 25 degree
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
//                    Log.e(TAG, "calculateCircleIconPositions: x" + i + " = " + xs[i] + "\ny" + i + " = " + ys[i]);
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
