package org.de_studio.recentappswitcher.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by HaiNguyen on 11/5/16.
 */

public class Collection extends RealmObject {
    public static final int TYPE_RECENT = 1;
    public static final int TYPE_CIRCLE_FAVORITE = 2;
    public static final int TYPE_QUICK_ACTION = 3;
    public static final int TYPE_GRID_FAVORITE = 4;
    public static final int TYPE_BLACK_LIST = 5;
    public int type;
    public String collectionId;
    public String label;
    public RealmList<Slot> slots;
    public int longClickMode;
    public int rowsCount;
    public int columnCount;

    public Collection() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
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
