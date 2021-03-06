package org.de_studio.recentappswitcher.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by HaiNguyen on 3/1/17.
 */

public class Shortcut extends RealmObject {
    public static final int TYPE_APP = 0;
    public static final int TYPE_ACTION = 1;
    public static final int TYPE_CONTACT =2;
    public static final int TYPE_FOLDER = 3;
    public static final int TYPE_SHORTCUT = 4;
    public static final int ACTION_WIFI = 0;
    public static final int ACTION_BLUETOOTH = 1;
    public static final int ACTION_POWER_MENU = 2;
    public static final int ACTION_ROTATION = 3;
    public static final int ACTION_HOME = 4;
    public static final int ACTION_BACK = 5;
    public static final int ACTION_NOTI = 6;
    public static final int ACTION_LAST_APP = 7;
    public static final int ACTION_CALL_LOGS = 8;
    public static final int ACTION_DIAL = 9;
    public static final int ACTION_CONTACT = 10;
    public static final int ACTION_NONE = 11;
    public static final int ACTION_RECENT = 12;
    public static final int ACTION_VOLUME = 13;
    public static final int ACTION_BRIGHTNESS = 14;
    public static final int ACTION_RINGER_MODE = 15;
    public static final int ACTION_FLASH_LIGHT = 16;
    public static final int ACTION_SCREEN_LOCK = 17;

    @PrimaryKey
    private int id;
    private int type;
    private String packageName;
    private int action;
    private String label;
    private String thumbnaiUri;
    private String number;
    private String name;
    private byte[] bitmap;
    private long contactId;
    private int resId;
    private String intent;
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

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    public void setThumbnaiUri(String thumbnaiUri) {
        this.thumbnaiUri = thumbnaiUri;
    }

    public String getThumbnaiUri() {
        return thumbnaiUri;
    }
    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public long getContactId() {
        return contactId;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getIntent() {
        return intent;
    }


}
