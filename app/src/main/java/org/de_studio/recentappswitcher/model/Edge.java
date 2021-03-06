package org.de_studio.recentappswitcher.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by HaiNguyen on 12/23/16.
 */

public class Edge extends RealmObject {
    public static final int MODE_RECENT_AND_QUICK_ACTION = 0;
    public static final int MODE_CIRCLE_FAV_AND_QUICK_ACTION = 1;
    public static final int MODE_GRID = 2;
    public static final int MODE_RECENT_ONLY = 3;
    public static final int MODE_CIRCLE_FAVORITE_ONLY = 4;
    public static final int MODE_QUICK_ACTION_ONLY = 5;
    public static final int KEYBOARD_OPTION_STEP_ASIDE = 1;
    public static final int KEYBOARD_OPTION_PLACE_UNDER = 0;
    public static final int KEYBOARD_OPTION_NONE = 3;
    public static final String EDGE_1_ID = "edge1";
    public static final String EDGE_2_ID = "edge2";
    public int mode;
    public int position;
    @PrimaryKey
    public String edgeId; //edge1 or edge2
    public Collection recent;
    public Collection circleFav;
    public Collection quickAction;
    public Collection grid;
    public int sensitive;
    public int length;
    public int offset;
    public boolean useGuide;
    public int guideColor;
    public int keyboardOption;

    public Edge() {
    }

    public int getKeyboardOption() {
        return keyboardOption;
    }

    public void setKeyboardOption(int keyboardOption) {
        this.keyboardOption = keyboardOption;
    }

    public Collection getCircleFav() {
        return circleFav;
    }

    public void setCircleFav(Collection circleFav) {
        this.circleFav = circleFav;
    }

    public boolean isUseGuide() {
        return useGuide;
    }

    public void setUseGuide(boolean useGuide) {
        this.useGuide = useGuide;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(String edgeId) {
        this.edgeId = edgeId;
    }

    public Collection getRecent() {
        return recent;
    }

    public void setRecent(Collection recent) {
        this.recent = recent;
    }

    public Collection getQuickAction() {
        return quickAction;
    }

    public void setQuickAction(Collection quickAction) {
        this.quickAction = quickAction;
    }

    public Collection getGrid() {
        return grid;
    }

    public void setGrid(Collection grid) {
        this.grid = grid;
    }

    public int getSensitive() {
        return sensitive;
    }

    public void setSensitive(int sensitive) {
        this.sensitive = sensitive;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getGuideColor() {
        return guideColor;
    }

    public void setGuideColor(int guideColor) {
        this.guideColor = guideColor;
    }
}
