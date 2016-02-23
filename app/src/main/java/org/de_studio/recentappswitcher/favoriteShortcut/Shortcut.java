package org.de_studio.recentappswitcher.favoriteShortcut;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hai on 2/14/2016.
 */
public class Shortcut extends RealmObject {
    public static final int TYPE_APP = 0;
    public static final int TYPE_SETTING = 1;
    public static final int ACTION_WIFI = 0;
    public static final int ACTION_BLUETOOTH = 1;
    public static final int ACTION_POWER_MENU = 2;
    public static final int ACTION_ROTATION = 3;
    @PrimaryKey
    private int id;
    private int type;
    private String packageName;
    private int action;

    public Shortcut() {}

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
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