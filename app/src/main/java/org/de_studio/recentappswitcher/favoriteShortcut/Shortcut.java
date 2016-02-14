package org.de_studio.recentappswitcher.favoriteShortcut;

import android.graphics.drawable.Drawable;

import io.realm.annotations.PrimaryKey;

/**
 * Created by hai on 2/14/2016.
 */
public class Shortcut {
    public static final int TYPE_APP = 0;
    public static final int TYPE_SETTING = 1;
    public static final int ACTION_WIFI = 0;
    public static final int ACTION_BLUETOOTH = 1;
    public static final int ACTION_POWER_MENU = 2;
    public static final int ACTION_ROTATION = 3;
    @PrimaryKey
    private int id;
    private int type;
    private Drawable drawable;
    private String packageName;
    private int action;

    public Shortcut() {}

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }

}
