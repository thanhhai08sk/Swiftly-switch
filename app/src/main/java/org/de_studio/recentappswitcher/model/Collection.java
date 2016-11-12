package org.de_studio.recentappswitcher.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by HaiNguyen on 11/5/16.
 */

public class Collection extends RealmObject {
    public static final String TYPE_RECENT = "recent_";
    public static final String TYPE_CIRCLE_FAVORITE = "circleFavorite_";
    public static final String TYPE_QUICK_ACTION = "quickAction_";
    public static final String TYPE_GRID_FAVORITE = "gridFavorite_";
    public static final String TYPE_BLACK_LIST = "blackList_";
    public static final int LONG_CLICK_MODE_NONE = 1;
    public static final int LONG_CLICK_MODE_OPEN_COLLECTION = 2;
    public String type;
    @PrimaryKey
    public String collectionId;
    public String label;
    public RealmList<Slot> slots;
    public int longClickMode;
    public String longClickCollection;
    public int rowsCount;
    public int columnCount;

    public Collection() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public RealmList<Slot> getSlots() {
        return slots;
    }

    public void setSlots(RealmList<Slot> slots) {
        this.slots = slots;
    }

    public int getLongClickMode() {
        return longClickMode;
    }

    public void setLongClickMode(int longClickMode) {
        this.longClickMode = longClickMode;
    }

    public int getRowsCount() {
        return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
        this.rowsCount = rowsCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }
}
