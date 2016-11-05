package org.de_studio.recentappswitcher.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by HaiNguyen on 11/5/16.
 */

public class Slot extends RealmObject {
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_RECENT = 2;
    public static final int TYPE_FOLDER = 3;
    public static final int TYPE_EMPTY = 4;
    public static final int TYPE_NULL = 5; //plus
    public int type;
    public Item stage1Item;
    public Item stage2Item;
    public int longClickMode;
    public RealmList<Item> items;

    public Slot() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
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
