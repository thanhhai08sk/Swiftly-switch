package org.de_studio.recentappswitcher.model;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by HaiNguyen on 11/5/16.
 */

public class Item extends RealmObject {
    public static final String TYPE_APP = "app_";
    public static final String TYPE_ACTION = "action_";
    public static final String TYPE_CONTACT = "contact_";
    public static final String TYPE_DEVICE_SHORTCUT = "shortcut_";
    public static final String TYPE_SHORTCUTS_SET = "shortcuts_set_";
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
    public static final int ACTION_SCREENSHOT = 18;
    public static final int ACTION_SEARCH_SHORTCUTS = 19;

    @PrimaryKey
    public String itemId; // = type + identifier | app_packageName | action_action | contact_number | shortcut_intent | shortcuts_set_collection_id_
    public String type;
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
    public byte[] originalIconBitmap;
    public byte[] iconBitmap;
    public byte[] iconBitmap2;
    public byte[] iconBitmap3;
    public String iconUri;
    public String collectionId;

    public Item() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public byte[] getIconBitmap2() {
        return iconBitmap2;
    }

    public void setIconBitmap2(byte[] iconBitmap2) {
        this.iconBitmap2 = iconBitmap2;
    }

    public byte[] getIconBitmap3() {
        return iconBitmap3;
    }

    public void setIconBitmap3(byte[] iconBitmap3) {
        this.iconBitmap3 = iconBitmap3;
    }

    public byte[] getOriginalIconBitmap() {
        return originalIconBitmap;
    }

    public void setOriginalIconBitmap(byte[] originalIconBitmap) {
        this.originalIconBitmap = originalIconBitmap;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(itemId);
        return builder.toString();

    }
}
