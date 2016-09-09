package org.de_studio.recentappswitcher.edgeService;

import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 8/19/16.
 */
public class EdgesServiceModel {
    Set<String> blackListSet;
    Realm pinRealm;
    String launcherPackagename;
    Shortcut[] savedRecentShortcut, pinnedShortcut;
    Set<Shortcut> pinnedSet;
    String lastAppPackageName;
    boolean isFreeAndOutOfTrial;
    float mScale, iconScale;
    float haftIconWidthPxl;
    float circleSizePxl;
    int iconSizeInclude10PaddingInGridDp;
    float iconSizePlx;
    int gridGap;
    float[] circleIconXs;
    float[] circleIconYs;



    private static final String TAG = EdgesServiceModel.class.getSimpleName();

    public EdgesServiceModel(Set<String> blackListSet, Realm pinRealm, String launcherPackagename
            , boolean isFreeAndOutOfTrial, float mScale, float haftIconWidthPxl
            , float circleSizePxl, float iconScale, int gridGap) {
        this.gridGap = gridGap;
        this.iconScale = iconScale;
        this.circleSizePxl = circleSizePxl;
        this.blackListSet = blackListSet;
        this.pinRealm = pinRealm;
        this.launcherPackagename = launcherPackagename;
        this.isFreeAndOutOfTrial = isFreeAndOutOfTrial;
        this.mScale = mScale;
        this.haftIconWidthPxl = haftIconWidthPxl;
        setPinnedShortcut();

        iconSizeInclude10PaddingInGridDp = (int) (Cons.DEFAULT_ICON_SIZE * iconScale) + Cons.DEFAULT_ICON_GAP_IN_GRID;
        iconSizePlx = Cons.DEFAULT_ICON_SIZE * iconScale * mScale;
    }
    public int getGridActivatedId(int x_cord, int y_cord, int x_grid, int y_grid,int gird_row, int grid_column, boolean folderMode) {
        double item_x,item_y;
        double xCordDouble = (double) x_cord;
        double yCordDouble = (double) y_cord;
        double distance;
        double smallestDistance = 1000*mScale;
        for (int i = 0; i < grid_column; i++) {
            for (int j = 0; j < gird_row; j++) {
                item_x = (x_grid + (iconSizeInclude10PaddingInGridDp /2)*mScale +i*(iconSizeInclude10PaddingInGridDp + gridGap)*mScale);
                item_y = (y_grid + (iconSizeInclude10PaddingInGridDp /2) * mScale + j * (iconSizeInclude10PaddingInGridDp + gridGap) * mScale);
                distance = Math.sqrt(Math.pow(xCordDouble - item_x,2) + Math.pow(yCordDouble - item_y, 2));
                if (distance <= 35 * mScale) {
                    return j * grid_column + i;
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

    public int getSemiCircleModeActivatedId(int[] itemXs, int[] itemYs, int x_init, int y_init, int x, int y, int position) {
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
            for (int i = 0; i < itemXs.length; i++) {
                distance = Math.sqrt(Math.pow(xDouble - (double) (itemXs[i] + haftIconWidthPxl), 2) + Math.pow(yDouble - (double) (itemYs[i] + haftIconWidthPxl), 2));
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

    public Shortcut[] getRecentList(ArrayList<String> tempPackageName) {
        Shortcut[] recentShortcut;
        boolean inHome = false;
        if (tempPackageName.size() > 0) {
            if (tempPackageName.contains(launcherPackagename) && tempPackageName.get(0).equalsIgnoreCase(launcherPackagename)) {
                inHome = true;
            }
            tempPackageName.remove(0);
            if (!inHome && tempPackageName.contains(launcherPackagename)) {
                tempPackageName.remove(launcherPackagename);
            }
        }

        if (tempPackageName.size() < 6 && savedRecentShortcut != null) {
            for (int i = 0; i < savedRecentShortcut.length; i++) {
                if (!tempPackageName.contains(savedRecentShortcut[i].getPackageName()) && tempPackageName.size() < 6) {
                    tempPackageName.add(savedRecentShortcut[i].getPackageName());
                }
            }
        }

        if (tempPackageName.size() >= 1) {
            lastAppPackageName = tempPackageName.get(0);
        }
        for (Shortcut t : pinnedSet) {
            if (tempPackageName.contains(t.getPackageName())) {
                tempPackageName.remove(t.getPackageName());
            }
        }
        if (6 - tempPackageName.size() - pinnedShortcut.length > 0) {
            recentShortcut = new Shortcut[tempPackageName.size() + pinnedShortcut.length];
        } else {
            recentShortcut = new Shortcut[6];
        }
        int n = 0; //count for pin
        int m = 0; //count for temp
        Shortcut tempShortcut;
        for (int t = 0; t <recentShortcut.length; t++) {
            if (pinnedShortcut.length > n && pinnedShortcut[n].getId() == t) {
                recentShortcut[t] = pinnedShortcut[n];
                n++;
            } else if (m < tempPackageName.size()) {
                tempShortcut = new Shortcut();
                tempShortcut.setType(Shortcut.TYPE_APP);
                tempShortcut.setPackageName(tempPackageName.get(m));
                m++;
                recentShortcut[t] = tempShortcut;
            } else {
                recentShortcut[t] = pinnedShortcut[n];
                n++;
            }
        }
        return recentShortcut;
    }

    public void setPinnedShortcut() {
        RealmResults<Shortcut> results1 =
                pinRealm.where(Shortcut.class).findAll().sort("id");
        if (isFreeAndOutOfTrial) {
            results1 = null;
        }
        int i = 0;
        if (results1 == null) {
            pinnedShortcut = new Shortcut[0];
        } else {
            pinnedShortcut = new Shortcut[results1.size()];
            for (Shortcut shortcut : results1) {
                Log.e(TAG, "result = " + shortcut.getPackageName());
                pinnedShortcut[i] = shortcut;
                i++;
            }
        }
        pinnedSet = new HashSet<Shortcut>(Arrays.asList(pinnedShortcut));
    }

    private float getInitPointOffsetNeeded() {
        return iconSizePlx/2 + circleSizePxl;
    }

    public float getYOffset(float windowHeight, float y_init) {
        float distanceNeeded = getInitPointOffsetNeeded();
        float distanceWeHave = windowHeight - y_init;
        if (distanceWeHave < distanceNeeded) {
            return distanceNeeded - distanceWeHave;
        } else if (y_init < distanceNeeded) {
            return y_init - distanceNeeded;
        } else return 0;
    }

    public float getXOffset(float windowWidth, float x_init) {
        float distanceNeeded = getInitPointOffsetNeeded();
        float distanceWeHave = windowWidth - x_init;
        if (distanceWeHave < distanceNeeded) {
            return distanceNeeded - distanceWeHave;
        } else if (x_init < distanceNeeded) {
            return x_init - distanceNeeded;
        } else return 0;
    }

    public float getXInit(int position, float x, float windowWidth) {
        switch (Utility.rightLeftOrBottom(position)) {
            case Cons.POSITION_RIGHT:
                return x - 10 * mScale;
            case Cons.POSITION_LEFT:
                return  x + 10 * mScale;
            case Cons.POSITION_BOTTOM:
                return x - getXOffset(windowWidth, x);
        }
        return -1;
    }

    public float getYInit(int position, float y, float windowHeight) {
        switch (Utility.rightLeftOrBottom(position)) {
            case Cons.POSITION_RIGHT:
                return y - getYOffset(windowHeight, y);
            case Cons.POSITION_LEFT:
                return y - getYOffset(windowHeight, y);
            case Cons.POSITION_BOTTOM:
                return (int) (y - 10 * mScale);
        }
        return -1;
    }

    public void calculateCircleIconPositions(float circleSizePxl, float iconSizePxl, int edgePosition, float xInit, float yInit, int iconsNumber) {
        circleIconXs = new float[iconsNumber];
        circleIconYs = new float[iconsNumber];
        Log.e(TAG, "calculateCircleIconPositions: circleSize = " + circleSizePxl
                + "\niconsize = " + iconSizePxl
                + "\nxInit = " + xInit
                + "\nyInit = " + yInit);
        double alpha, beta;
        double[] alphaN = new double[iconsNumber];
        switch (iconsNumber) {
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
            default:
                alpha = 0.0556;
        }
        beta = Math.PI - 2 * alpha;
        for (int i = 0; i < iconsNumber; i++) {
            alphaN[i] = alpha + i * (beta / (iconsNumber - 1));
            switch (edgePosition / 10) {
                case 1:
                    circleIconXs[i] = xInit - circleSizePxl * (float) Math.sin(alphaN[i]) - iconSizePxl/2;
                    circleIconYs[i] = yInit - circleSizePxl * (float) Math.cos(alphaN[i]) - iconSizePxl/2;
                    Log.e(TAG, "calculateCircleIconPositions: x" + i + " = " + circleIconXs[i] + "\ny" + i + " = " + circleIconYs[i]);
                    break;
                case 2:
                    circleIconXs[i] = xInit + circleSizePxl * (float) Math.sin(alphaN[i]) - iconSizePxl/2;
                    circleIconYs[i] = yInit - circleSizePxl * (float) Math.cos(alphaN[i]) - iconSizePxl/2;
                    break;
                case 3:
                    circleIconXs[i] = xInit - circleSizePxl * (float) Math.cos(alphaN[i]) - iconSizePxl/2;
                    circleIconYs[i] = yInit - circleSizePxl * (float) Math.sin(alphaN[i]) - iconSizePxl/2;
                    break;
            }
        }

    }





}
