package org.de_studio.recentappswitcher.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by HaiNguyen on 11/5/16.
 */

public class Slot extends RealmObject {
    public static final String TYPE_ITEM = "item_";
    public static final String TYPE_RECENT = "recent_";
    public static final String TYPE_FOLDER = "folder_";
    public static final String TYPE_EMPTY = "empty_";
    public static final String TYPE_NULL = "null_"; //plus
    public String type;
    public Item stage1Item;
    public Item stage2Item;
    public int longClickMode;
    public RealmList<Item> items;
    public byte[] iconBitmap;

    public Slot() {
    }

    public byte[] getIconBitmap() {
        return iconBitmap;
    }

    public void setIconBitmap(byte[] iconBitmap) {
        this.iconBitmap = iconBitmap;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Item getStage1Item() {
        return stage1Item;
    }

    public void setStage1Item(Item stage1Item) {
        this.stage1Item = stage1Item;
    }

    public Item getStage2Item() {
        return stage2Item;
    }

    public void setStage2Item(Item stage2Item) {
        this.stage2Item = stage2Item;
    }

    public int getLongClickMode() {
        return longClickMode;
    }

    public void setLongClickMode(int longClickMode) {
        this.longClickMode = longClickMode;
    }

    public RealmList<Item> getItems() {
        return items;
    }

    public void setItems(RealmList<Item> items) {
        this.items = items;
    }
}
