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

    public int getGridActivatedId(float x, float y, float gridX, float gridY,int rowsCount, int columnCount, int space, boolean folderMode) {
        double item_x,item_y;
        double xDouble = (double) x;
        double yDouble = (double) y;
        float iconSpace = space * mScale * 2 + iconWidth;
        double distance;
        double smallestDistance = 1000*mScale;
        for (int i = 0; i < columnCount; i++) {
            for (int j = 0; j < rowsCount; j++) {
                item_x = (gridX + iconSpace/2 +i*iconSpace);
                item_y = (gridY + iconSpace/2 * mScale + j * iconSpace);
                distance = Math.sqrt(Math.pow(xDouble - item_x,2) + Math.pow(yDouble - item_y, 2));
                if (distance <= 35 * mScale) {
                    return j * columnCount + i;
                } else {
                    if (smallestDistance > distance) {
                        smallestDistance = distance;
                    }
                }
            }
        }
        if (folderMode) {
            if (smallestDistance > 105 * mScale) {
                return -2;
            }
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

    public float getXInit(int position, float x, float windowWidth, int radius) {
        switch (Utility.rightLeftOrBottom(position)) {
            case Cons.POSITION_RIGHT:
                return x - 10 * mScale;
            case Cons.POSITION_LEFT:
                return  x + 10 * mScale;
            case Cons.POSITION_BOTTOM:
                return x - getXOffset(windowWidth, x,radius);
        }
        return -1;
    }

    public float getYInit(int position, float y, float windowHeight, int radius) {
        switch (Utility.rightLeftOrBottom(position)) {
            case Cons.POSITION_RIGHT:
                return y - getYOffset(windowHeight, y, radius);
            case Cons.POSITION_LEFT:
                return y - getYOffset(windowHeight, y,radius);
            case Cons.POSITION_BOTTOM:
                return (int) (y - 10 * mScale);
        }
        return -1;
    }

    private float getYOffset(float windowHeight, float y_init, int radius) {
        float distanceNeeded = getInitPointOffsetNeeded(radius);
        float distanceWeHave = windowHeight - y_init;
        if (distanceWeHave < distanceNeeded) {
            return distanceNeeded - distanceWeHave;
        } else if (y_init < distanceNeeded) {
            return y_init - distanceNeeded;
        } else return 0;
    }

    private float getXOffset(float windowWidth, float x_init, int radius) {
        float distanceNeeded = getInitPointOffsetNeeded(radius);
        float distanceWeHave = windowWidth - x_init;
        if (distanceWeHave < distanceNeeded) {
            return distanceNeeded - distanceWeHave;
        } else if (x_init < distanceNeeded) {
            return x_init - distanceNeeded;
        } else return 0;
    }
    private float getInitPointOffsetNeeded(int radius) {
        return haftIconWidth + radius*mScale;
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
