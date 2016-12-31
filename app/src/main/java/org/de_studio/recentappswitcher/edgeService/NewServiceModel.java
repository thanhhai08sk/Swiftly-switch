package org.de_studio.recentappswitcher.edgeService;

import android.os.Build;
import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by HaiNguyen on 12/23/16.
 */

public class NewServiceModel extends BaseModel {
    private static final String TAG = NewServiceModel.class.getSimpleName();
    float mScale, iconScale;

    float iconWidth, haftIconWidth;
    String launcherPackageName;
    String lastAppPackageName;
    Realm realm;
    ArrayList<String> savedRecentShortcut;

    public NewServiceModel(float mScale, float iconScale, String launcherPackageName, Realm realm) {
        this.mScale = mScale;
        this.iconScale = iconScale;
        this.launcherPackageName = launcherPackageName;
        this.realm = realm;
    }

    void setup() {
        iconWidth = Cons.DEFAULT_ICON_WIDTH * mScale * iconScale;
        haftIconWidth = iconWidth / 2;
    }

    public float convertDpToPixel(int dp) {
        return dp * mScale;
    }


    public RealmList<Slot> getRecent(ArrayList<String> packageNames, RealmList<Slot> slots) {
        RealmList<Slot> returnSlots = new RealmList<>();
        for (String packageName : packageNames) {
            Log.e(TAG, "getRecent: temp package " + packageName);
        }
        long recentSlotsCount = slots.where().equalTo(Cons.TYPE, Slot.TYPE_RECENT).count();
        String removedPackage = null;
        if (packageNames.size() > 0) {
            removedPackage = packageNames.get(0);
            packageNames.remove(0);
            packageNames.remove(launcherPackageName);
            if (packageNames.size()>0) {
                lastAppPackageName = packageNames.get(0);
            }
        }


        for (Slot slot : slots) {
            if (slot.type.equals(Slot.TYPE_ITEM) && slot.stage1Item.type.equals(Item.TYPE_APP) && packageNames.contains(slot.stage1Item.packageName)) {
                packageNames.remove(slot.stage1Item.packageName);
            }
            if (slot.type.equals(Slot.TYPE_ITEM) && slot.stage1Item.type.equals(Item.TYPE_APP) && savedRecentShortcut.contains(slot.stage1Item.packageName)) {
                savedRecentShortcut.remove(slot.stage1Item.packageName);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int i = 0;
            if (savedRecentShortcut != null) {
                while (packageNames.size() < recentSlotsCount && i < savedRecentShortcut.size()) {
                    if (!savedRecentShortcut.get(i).equals(removedPackage)
                            && !packageNames.contains(savedRecentShortcut.get(i))) {
                        packageNames.add(savedRecentShortcut.get(i));
                    }
                    i++;
                }
            }
        }

        savedRecentShortcut = packageNames;

        for (Slot slot : slots) {
            switch (slot.type) {
                case Slot.TYPE_RECENT:
                    if (packageNames.size()>0) {
                        Item item = realm.where(Item.class).equalTo(Cons.ITEM_ID, Utility.createAppItemId(packageNames.get(0))).findFirst();
                        if (item != null) {
                            Slot slot1 = new Slot();
                            slot1.type = Slot.TYPE_ITEM;
                            slot1.stage1Item = item;
                            returnSlots.add(slot1);
                        }
                        packageNames.remove(0);
                    }
                    break;
                default:
                    returnSlots.add(slot);
                    break;
            }
        }

        for (Slot returnSlot : returnSlots) {
            Log.e(TAG, "return slot " + returnSlot.toString());
        }


        return returnSlots;
    }

    public String getLastApp() {
        return lastAppPackageName;
    }

    public Collection getCollection(String id) {
        return realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, id).findFirst();
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
//            Log.e(TAG, "calculateCircleIconPositions: " + i + " = " + xs[i] + "\n" + ys[i]);
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
        realm.close();
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
