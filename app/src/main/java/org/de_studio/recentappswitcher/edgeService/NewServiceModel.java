package org.de_studio.recentappswitcher.edgeService;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Edge;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;
import org.de_studio.recentappswitcher.ui.QuickActionsView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

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
    Edge edge1, edge2;
    ArrayList<String> savedRecentShortcut;
    Boolean backgroundTouchable = null;

    public NewServiceModel(float mScale, float iconScale, String launcherPackageName, Realm realm, Edge edge1, Edge edge2) {
        this.mScale = mScale;
        this.realm = realm;
        this.iconScale = iconScale;
        this.launcherPackageName = launcherPackageName;
        this.edge1 = edge1;
        this.edge2 = edge2;
    }

    void setup() {
        iconWidth = Cons.ICON_SIZE_DEFAULT * mScale * iconScale;
        haftIconWidth = iconWidth / 2;
        RealmResults<Item> screenLocks = realm.where(Item.class).equalTo(Cons.ACTION, Item.ACTION_SCREEN_LOCK).findAll();
        if (screenLocks.size() > 0) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Slot> screenLockSlot = realm.where(Slot.class).equalTo(Cons.TYPE, Slot.TYPE_ITEM).equalTo("stage1Item.action", Item.ACTION_SCREEN_LOCK).findAll();
                    for (Slot slot : screenLockSlot) {
                        slot.type = Slot.TYPE_NULL;
                    }
                    RealmResults<Item> screenLocks = realm.where(Item.class).equalTo(Cons.ACTION, Item.ACTION_SCREEN_LOCK).findAll();
                    for (Item screenLock : screenLocks) {
                        RealmObject.deleteFromRealm(screenLock);
                    }
                }
            });
        }
    }

    public Edge getEdge(String edgeId) {
        switch (edgeId) {
            case Edge.EDGE_1_ID:
                return edge1;
            case Edge.EDGE_2_ID:
                return edge2;
        }
        return null;
    }



    public void setSavedRecentShortcuts(ArrayList<String> recents) {
        if (recents != null) {
            recents.remove(launcherPackageName);
        }
        this.savedRecentShortcut = recents;
    }


    public void clearSectionData() {
        lastAppPackageName = null;
    }
    public float convertDpToPixel(int dp) {
        return dp * mScale;
    }


    public RealmList<Slot> getRecent(ArrayList<String> packageNames, RealmList<Slot> slots, NewServicePresenter.Showing currentShowing) {
        Item item = null;
        RealmList<Slot> returnSlots = new RealmList<>();

        long recentSlotsCount = slots.where().equalTo(Cons.TYPE, Slot.TYPE_RECENT).count();

        String removedPackage = removeLauncherAndCurrentAppAndSetLastApp(packageNames);
        currentShowing.lastApp = lastAppPackageName;


        for (Slot slot : slots) {
            if (slot.type.equals(Slot.TYPE_ITEM) && slot.stage1Item.type.equals(Item.TYPE_APP) && packageNames.contains(slot.stage1Item.packageName)) {
                packageNames.remove(slot.stage1Item.packageName);
            }
            if (savedRecentShortcut !=null &&
                    slot.type.equals(Slot.TYPE_ITEM) && slot.stage1Item.type.equals(Item.TYPE_APP) &&
                    savedRecentShortcut.contains(slot.stage1Item.packageName)) {
                savedRecentShortcut.remove(slot.stage1Item.packageName);
            }
        }


        for (int i = packageNames.size() - 1; i >= 0; i--) {
            item = realm.where(Item.class).equalTo(Cons.ITEM_ID, Utility.createAppItemId(packageNames.get(i))).findFirst();
            if (item == null) {
                packageNames.remove(packageNames.get(i));
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int i = 0;
            if (savedRecentShortcut != null) {
                while (packageNames.size() < recentSlotsCount && i < savedRecentShortcut.size()) {
                    if (!savedRecentShortcut.get(i).equals(removedPackage)
                            && !packageNames.contains(savedRecentShortcut.get(i)) &&
                            realm.where(Item.class).equalTo(Cons.ITEM_ID, Utility.createAppItemId(savedRecentShortcut.get(i))).findFirst() != null
                            ) {
                        packageNames.add(savedRecentShortcut.get(i));
                    }
                    i++;
                }
            }
        }


        savedRecentShortcut = packageNames;
        int i = 0;
        for (Slot slot : slots) {
            switch (slot.type) {
                case Slot.TYPE_RECENT:
                    if (packageNames.size() > i) {
                        Item item1 = null;
                        while (item1 == null && i < packageNames.size()) {
                            item1 = realm.where(Item.class).equalTo(Cons.ITEM_ID, Utility.createAppItemId(packageNames.get(i))).findFirst();
                            if (item1 != null) {
                                Slot slot1 = new Slot();
                                slot1.type = Slot.TYPE_ITEM;
                                slot1.stage1Item = item1;
                                returnSlots.add(slot1);
                            }
                            i++;
                        }
                    }
                    break;
                default:
                    returnSlots.add(slot);
                    break;
            }
        }
        return returnSlots;
    }

    private String removeLauncherAndCurrentAppAndSetLastApp(ArrayList<String> packageNames) {
        if (lastAppPackageName == null) {
            String removedPackage = null;
            if (packageNames.size() > 0) {
                removedPackage = packageNames.get(0);
                packageNames.remove(0);
                packageNames.remove(launcherPackageName);
                if (packageNames.size() > 0) {
                    lastAppPackageName = packageNames.get(0);
                }
            }
            return removedPackage;

        } else return null;
    }

    public String getLastApp(ArrayList<String> packageNames) {
        if (packageNames != null) {
            removeLauncherAndCurrentAppAndSetLastApp(packageNames);
        }
//        for (String packageName : packageNames) {
//            Log.e(TAG, "getLastApp: " + packageName);
//        }
        return lastAppPackageName;
    }

    public Collection getCollection(String id) {
        return realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, id).findFirst();
    }

    public int getCircleAndQuickActionTriggerId(IconsXY iconsXY, int radius, float x_init, float y_init, float x, float y, int position, int iconsCount, boolean hasQuickActions, int quickActionsCount, boolean quickActionsStayOnScreen, boolean useAngleForCircle) {
        float circleSizePxl = radius * mScale;
        double xInitDouble = (double) x_init;
        double yInitDouble = (double) y_init;
        double xDouble = (double) x;
        double yDouble = (double) y;
        double distanceFromInitPxl = Math.sqrt(Math.pow(xDouble - xInitDouble, 2) + Math.pow(yDouble - yInitDouble, 2));
        double startQuickActionZonePxl = (double) (35 * mScale + circleSizePxl);

        if (hasQuickActions) {
            double endQuickActionZonePxl = (double) (QuickActionsView.ARC_SIZE_DP * mScale + startQuickActionZonePxl);

            double ang30 = 0.1666 * Math.PI;
            double ang70 = 0.3889 * Math.PI;
            double ang110 = 0.6111 * Math.PI;
            double angle;
            if (distanceFromInitPxl < startQuickActionZonePxl) {
                double distance;
                double distanceNeeded = 35 * mScale;
                if (iconsXY != null) {
                    for (int i = 0; i < Math.min(iconsCount, iconsXY.xs.length); i++) {
                        distance = Math.sqrt(Math.pow(xDouble - (double) (iconsXY.xs[i] + haftIconWidth), 2) + Math.pow(yDouble - (double) (iconsXY.ys[i] + haftIconWidth), 2));
                        if (distance <= distanceNeeded) {
                            return i;
                        }
                    }
                }
            } else {
                if (quickActionsStayOnScreen && distanceFromInitPxl > endQuickActionZonePxl) {
                    return -1;
                }
                if (Utility.rightLeftOrBottom(position) == Cons.POSITION_BOTTOM) {
                    angle = Math.acos((x_init - x) / distanceFromInitPxl);
                } else {
                    angle = Math.acos((y_init - y) / distanceFromInitPxl);
                }

                if (quickActionsCount == 4) {
                    if (angle < ang30) {
                        return 10;
                    } else if (angle < ang70) {
                        return 11;
                    } else if (angle < ang110) {
                        return 12;
                    } else return 13;
                } else {
                    return 10 + (int) (angle / (Math.PI / quickActionsCount));
                }
            }
            return -1;
        } else {
            double alpha;
            double halfIconAngle;
            double angleToMidOfFirstIcon;
            double minDistance = (double) (circleSizePxl - mScale * 35);
            if (distanceFromInitPxl < startQuickActionZonePxl || useAngleForCircle) {
                if (distanceFromInitPxl > minDistance) {
                    if (Utility.rightLeftOrBottom(position) == Cons.POSITION_BOTTOM) {
                        alpha = Math.acos((x_init - x) / distanceFromInitPxl);
                    } else {
                        alpha = Math.acos((y_init - y) / distanceFromInitPxl);
                    }
                    if (iconsCount < 6) {
                        angleToMidOfFirstIcon = Cons.CIRCLE_INIT_ANGLE_LESS_THAN_6_ITEMS;
                    } else {
                        angleToMidOfFirstIcon = Cons.CIRCLE_INIT_ANGLE_GREATER_OR_EQUAL_6_ITEMS;
                    }
                    halfIconAngle = (Math.PI - 2 * angleToMidOfFirstIcon) / ((iconsCount - 1)*2);

                    for (int i = 1; i < iconsCount * 2; i = i + 2) {
                        if (alpha < angleToMidOfFirstIcon + i * halfIconAngle) {
                            return (i - 1) / 2;
                        }
                    }
                }

            }
        }
        return -1;
    }

    public int getGridActivatedId(float x, float y, float gridX, float gridY, int rowsCount, int columnCount, int space, boolean folderMode, boolean isRTL) {

//        Log.e(TAG, "getGridActivatedId: x " + x
//                + "\ny " + y
//                + "\ngridX " + gridX
//                + "\ngridY " + gridY
//                + "\nspace " + space
//                + "\niconWidth " + iconWidth
//                +"\nmScale " + mScale
//        );
        if (gridX == 0 && gridY == 0) {
            return -1;
        }
        double centerIconX, centerIconY;
        double xDouble = (double) x;
        double yDouble = (double) y;
        float totalIconWidth = space * mScale + iconWidth;
//        Log.e(TAG, "getGridActivatedId: total iconWidth = " + totalIconWidth + "\nspace = " + space
//                + "\nmScale = " + mScale + "\niconWidth = " + iconWidth);
        double distance;
        double smallestDistance = 1000 * mScale;
        for (int i = 0; i < columnCount; i ++) {
            for (int j = 0; j < rowsCount; j++) {
                centerIconX = (gridX + totalIconWidth / 2 + i * totalIconWidth);
                centerIconY = (gridY + totalIconWidth / 2 + j * totalIconWidth);
                distance = Math.sqrt(Math.pow(xDouble - centerIconX, 2) + Math.pow(yDouble - centerIconY, 2));
                if (distance <= 35 * mScale) {
                    return isRTL ? j*columnCount + (columnCount - 1 -i) : j * columnCount + i;
                } else {
                    if (smallestDistance > distance) {
                        smallestDistance = distance;
                    }
                }
            }
        }
        if (folderMode) {
            if (smallestDistance > 90 * mScale) {
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
            case 3:
                alpha = Cons.CIRCLE_INIT_ANGLE_FOR_3;
                break;
            case 4:
                alpha = Cons.CIRCLE_INIT_ANGLE_LESS_THAN_6_ITEMS; // 20 degree
                break;
            case 5:
                alpha = Cons.CIRCLE_INIT_ANGLE_LESS_THAN_6_ITEMS; // 20 degree
                break;
            case 6:
                alpha = Cons.CIRCLE_INIT_ANGLE_GREATER_OR_EQUAL_6_ITEMS; // 10 degree
                break;
            case 7:
                alpha = Cons.CIRCLE_INIT_ANGLE_GREATER_OR_EQUAL_6_ITEMS;
                break;
            default:
                alpha = Cons.CIRCLE_INIT_ANGLE_GREATER_OR_EQUAL_6_ITEMS;
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
//        return haftIconWidth + radius*mScale;
        return iconWidth + radius*mScale;
    }

    public void addPackageToData(final String packageName, final String label) {
        final String itemId = Utility.createAppItemId(packageName);
        Item item = realm.where(Item.class).equalTo(Cons.ITEM_ID, itemId).findFirst();
        if (item == null) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Item item = new Item();
                    item.type = Item.TYPE_APP;
                    item.itemId = itemId;
                    item.packageName = packageName;
                    item.label = label;
                    realm.copyToRealm(item);
                    Log.e(TAG, "generate app item: " + label);

                }
            });
        }

    }

    public void setDefaultRadiusForQuickActions(Collection quickActions) {
        if (quickActions != null) {
            realm.beginTransaction();
            quickActions.radius = Cons.QUICK_ACTION_RADIUS_DEFAULT;
            realm.commitTransaction();
        }
    }

    public void findAndSetRecentToEdge(Edge edge) {
        if (edge != null) {
            final String edgeId = edge.edgeId;
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Edge edgeToFix = realm.where(Edge.class).equalTo(Cons.EDGE_ID, edgeId).findFirst();
                    Collection recent = realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_RECENT).findFirst();
                    if (edgeToFix != null && recent != null) {
                        edgeToFix.recent = recent;
                    }
                }
            });
        }
    }

    public void removeAppItemFromData(final String packageName) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
//                Item item = realm.where(Item.class).equalTo(Cons.ITEM_ID, Utility.createAppItemId(packageName)).findFirst();
                Item item = realm.where(Item.class).equalTo(Cons.PACKAGENAME, packageName).findFirst();
                if (item != null) {
                    RealmResults<Collection> recents = realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_RECENT)
                            .equalTo("slots.stage1Item.itemId", item.itemId).findAll();
                    for (Collection recent : recents) {
                        RealmResults<Slot> slots = recent.slots.where().equalTo(Cons.TYPE, Slot.TYPE_ITEM)
                                .equalTo("stage1Item.itemId", item.itemId).findAll();
                        for (Slot slot : slots) {
                            slot.type = Slot.TYPE_RECENT;
                        }
                    }

                    RealmResults<Collection> otherCollections = realm.where(Collection.class).notEqualTo(Cons.TYPE, Collection.TYPE_RECENT)
                            .equalTo("slots.stage1Item.itemId", item.itemId).findAll();

                    for (Collection collection : otherCollections) {
                        RealmResults<Slot> slots = collection.slots.where().equalTo(Cons.TYPE, Slot.TYPE_ITEM)
                                .equalTo("stage1Item.itemId", item.itemId).findAll();
                        for (Slot slot : slots) {
                            slot.type = Slot.TYPE_NULL;
                        }
                    }


                    Log.e(TAG, "execute: delete app item from realm: " + packageName);
                    item.deleteFromRealm();
                }else Log.e(TAG, "execute: can not delete app, null item, package = " + packageName);
            }
        });
    }

    public float getIconCenterX(float iconX) {
        return iconX + haftIconWidth;
    }

    public float getIconCenterY(float iconY) {
        return iconY + haftIconWidth;
    }


    @Override
    public void clear() {
    }

    public Boolean shouldBackgroundTouchable() {
        if (backgroundTouchable != null) {
            return backgroundTouchable;
        }
        if (edge1 != null) {
            if (edge1.grid != null) {
                backgroundTouchable = true;
            }
            if (edge1.quickAction != null) {
                backgroundTouchable = true;
            }
        }
        return backgroundTouchable;
    }

    public List<Item> searchForItemsWithTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return getLastSearch();
        }
        return realm.where(Item.class)
                .contains(Cons.LABEL,title, Case.INSENSITIVE)
                .notEqualTo(Cons.TYPE, Item.TYPE_SHORTCUTS_SET)
                .findAll();
    }

    public void addToLastSearch(final Item item) {
        final String itemId = item.itemId;
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Collection lastSearch = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, Collection.TYPE_LAST_SEARCH).findFirst();
                Item item1 = realm.where(Item.class).equalTo(Cons.ITEM_ID, itemId).findFirst();
                if (lastSearch != null && item1 != null) {
                    if (lastSearch.items.contains(item1 )) {
                        lastSearch.items.move(lastSearch.items.indexOf(item1), 0);
                    } else lastSearch.items.add(0, item1);
                }
            }
        });
    }

    private Collection getLastSearchCollection() {
        return realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, Collection.TYPE_LAST_SEARCH).findFirst();
    }

    public List<Item> getLastSearch() {
        Collection lastSearch = getLastSearchCollection();
        if (lastSearch != null) {
            return lastSearch.items;
        } else {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Collection collection = new Collection();
                    collection.type = Collection.TYPE_LAST_SEARCH;
                    collection.collectionId = Collection.TYPE_LAST_SEARCH;
                    collection.label = "LastSearch";
                    realm.insertOrUpdate(collection);
                }
            });
            return new ArrayList<>();
        }
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
