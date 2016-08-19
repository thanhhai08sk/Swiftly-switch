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
public class EdgeServiceModel {
    Set<String> blackListSet;
    Realm pinRealm;
    String launcherPackagename;
    Shortcut[] savedRecentShortcut, pinnedShortcut;
    Set<Shortcut> pinnedSet;
    String lastAppPackageName;
    boolean isFreeAndOutOfTrial;
    float mScale, iconScale;
    float haftIconWidthPxl;
    int circleSizeDp;
    int iconSizeInclude10PaddingInGridDp;
    int iconSize;
    int gridGap;



    private static final String TAG = EdgeServiceModel.class.getSimpleName();

    public EdgeServiceModel(Set<String> blackListSet, Realm pinRealm, String launcherPackagename
            , boolean isFreeAndOutOfTrial, float mScale, float haftIconWidthPxl
            , int circleSizeDp, float iconScale, int gridGap) {
        this.gridGap = gridGap;
        this.iconScale = iconScale;
        this.circleSizeDp = circleSizeDp;
        this.blackListSet = blackListSet;
        this.pinRealm = pinRealm;
        this.launcherPackagename = launcherPackagename;
        this.isFreeAndOutOfTrial = isFreeAndOutOfTrial;
        this.mScale = mScale;
        this.haftIconWidthPxl = haftIconWidthPxl;
        setPinnedShortcut();

        iconSizeInclude10PaddingInGridDp = (int) (Cons.DEFAULT_ICON_SIZE * iconScale) + Cons.DEFAULT_ICON_GAP_IN_GRID;
        iconSize = (int) (Cons.DEFAULT_ICON_SIZE * iconScale);
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
        double startQuickActionZonePxl = (double) ((35+ circleSizeDp)* mScale);

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
            Log.e(TAG, "onTouch:  n = " + n + "\nm = " + m + "\nt = "+ t);
//                                Log.e(TAG, "onTouch: pin.n.id = " + pinnedShortcut[n].getId());
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

    private int getInitPointOffsetNeeded() {
        return (int) (mScale * (circleSizeDp + iconSize));
    }

    public int getYOffset(int windowHeight, int y_init) {
        int distanceNeeded = getInitPointOffsetNeeded();
        int distanceWeHave = windowHeight - y_init;
        if (distanceWeHave < distanceNeeded) {
            return distanceNeeded - distanceWeHave;
        } else if (y_init < distanceNeeded) {
            return y_init - distanceNeeded;
        } else return 0;
    }

    public int getXOffset(int windowWidth, int x_init) {
        int distanceNeeded = getInitPointOffsetNeeded();
        int distanceWeHave = windowWidth - x_init;
        if (distanceWeHave < distanceNeeded) {
            return distanceNeeded - distanceWeHave;
        } else if (x_init < distanceNeeded) {
            return x_init - distanceNeeded;
        } else return 0;
    }

    public int getXInit(int position, int x, int windowWidth) {
        switch (position) {
            case Cons.POSITION_RIGHT:
                return (int) (x - 10 * mScale);
            case Cons.POSITION_LEFT:
                return  (int) (x + 10 * mScale);
            case Cons.POSITION_BOTTOM:
                return x - getXOffset(windowWidth, x);
        }
        return -1;
    }

    public int getYInit(int position, int y, int windowHeight) {
        switch (position) {
            case Cons.POSITION_RIGHT:
                return y - getYOffset(windowHeight,y);
            case Cons.POSITION_LEFT:
                return y - getYOffset(windowHeight, y);
            case Cons.POSITION_BOTTOM:
                return (int) (y - 10 * mScale);
        }
        return -1;
    }



}