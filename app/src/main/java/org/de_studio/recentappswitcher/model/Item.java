package org.de_studio.recentappswitcher.model;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by HaiNguyen on 11/5/16.
 */

public class Item extends RealmObject {
    public static final int TYPE_APP = 0;
    public static final int TYPE_ACTION = 1;
    public static final int TYPE_CONTACT =2;
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
    public static final int ACTION_RECENT = 12;
    public static final int ACTION_VOLUME = 13;
    public static final int ACTION_BRIGHTNESS = 14;
    public static final int ACTION_RINGER_MODE = 15;
    public static final int ACTION_FLASH_LIGHT = 16;
    public static final int ACTION_SCREEN_LOCK = 17;
    @PrimaryKey
    public String itemId;
    public int type;
    public String label;
    public int action;
    @Index
    public String packageName;
    public String number;
    public long contactId;
    public String intent;
    public long appForegroundTime;
    public int iconResourceId;
    public int iconResourceId2;
    public int iconResourceId3;
    public byte[] iconBitmap;
    public String iconUri;

    public Item() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public long getAppForegroundTime() {
        return appForegroundTime;
    }

    public void setAppForegroundTime(long appForegroundTime) {
        this.appForegroundTime = appForegroundTime;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }

    public int getIconResourceId2() {
        return iconResourceId2;
    }

    public void setIconResourceId2(int iconResourceId2) {
        this.iconResourceId2 = iconResourceId2;
    }

    public int getIconResourceId3() {
        return iconResourceId3;
    }

    public void setIconResourceId3(int iconResourceId3) {
        this.iconResourceId3 = iconResourceId3;
    }

    public byte[] getIconBitmap() {
        return iconBitmap;
    }

    public void setIconBitmap(byte[] iconBitmap) {
        this.iconBitmap = iconBitmap;
    }

    public String getIconUri() {
        return iconUri;
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }
}
