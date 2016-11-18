package org.de_studio.recentappswitcher.utils;

/**
 * Created by HaiNguyen on 11/18/16.
 */

public class Action {
    public static final int TYPE_BOTTOM_BAR_ACTION = 1;
    public static final int ITEM_OK_BUTTON = 1;
    public static final int ITEM_NEXT_BUTTON = 2;
    public static final int ITEM_PREVIOUS_BUTTON = 3;
    public int type;
    public int item;

    public Action(int type) {
        this.type = type;
    }
}
