package org.de_studio.recentappswitcher;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by HaiNguyen on 6/9/16.
 */
public class MyRealmMigration implements RealmMigration {
    private static final String TAG = MyRealmMigration.class.getSimpleName();
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        Log.e(TAG, "migrate: old = " + oldVersion + "\nnew = " + newVersion);
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 0) {
            RealmObjectSchema shortcutSchema =  schema.get("Shortcut");
            try {
                shortcutSchema.addField("thumbnaiUri", String.class)
                        .addField("number", String.class)
                        .addField("name", String.class)
                        .addField("contactId", long.class);
            } catch (IllegalArgumentException e) {
                Log.e("MyRealmMigration", "migrate: " + e);
                e.printStackTrace();
            }

            oldVersion++;
        }
        if (oldVersion == 1) {
            RealmObjectSchema shortcutSchema =  schema.get("Shortcut");
            try {
                shortcutSchema.addField("bitmap", byte[].class)
                        .addField("resId", int.class)
                        .addField("intent", String.class);
            } catch (IllegalArgumentException e) {
                Log.e("MyRealmMigration", "migrate: oldVersion = 1 " + e);
                e.printStackTrace();
            }
            oldVersion++;
        }

        if (oldVersion == 2) {
            schema.create("Item")
                    .addField("itemId", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("type", String.class)
                    .addField("label", String.class)
                    .addField("action", int.class)
                    .addField("packageName", String.class,FieldAttribute.INDEXED)
                    .addField("number", String.class)
                    .addField("contactId", long.class)
                    .addField("intent", String.class)
                    .addField("appForegroundTime", long.class)
                    .addField("iconResourceId", int.class)
                    .addField("iconResourceId2", int.class)
                    .addField("iconResourceId3", int.class)
                    .addField("iconBitmap", byte[].class)
                    .addField("iconUri", String.class)
                    .addField("collectionId", String.class);

            schema.create("Slot")
                    .addField("type", String.class)
                    .addField("slotId", String.class, FieldAttribute.PRIMARY_KEY)
                    .addRealmObjectField("stage1Item", schema.get("Item"))
                    .addRealmObjectField("stage2Item", schema.get("Item"))
                    .addField("longClickMode", int.class)
                    .addRealmListField("items", schema.get("Item"))
                    .addField("iconBitmap", byte[].class);

            schema.create("Collection")
                    .addField("type", String.class)
                    .addField("collectionId", String.class)
                    .addField("label", String.class)
                    .addRealmListField("slots", schema.get("Slot"))
                    .addRealmListField("items", schema.get("Item"))
                    .addField("longClickMode", int.class)
                    .addField("longClickCollection", String.class)
                    .addField("rowsCount", int.class)
                    .addField("columnCount", int.class)
                    .addField("marginHorizontal", int.class)
                    .addField("marginVertical", int.class)
                    .addField("offsetHorizontal", int.class)
                    .addField("offsetVertical", int.class)
                    .addField("space", int.class)
                    .addField("radius", int.class)
                    .addField("position", int.class);


            schema.create("Edge")
                    .addField("mode", int.class)
                    .addField("position", int.class)
                    .addField("edgeId", String.class, FieldAttribute.PRIMARY_KEY)
                    .addRealmObjectField("recent", schema.get("Collection"))
                    .addRealmObjectField("circleFav", schema.get("Collection"))
                    .addRealmObjectField("grid", schema.get("Collection"))
                    .addField("sensitive", int.class)
                    .addField("length", int.class)
                    .addField("offset", int.class)
                    .addField("useGuide", boolean.class)
                    .addField("guideColor", int.class)
                    .addField("keyboardOption", int.class);

            schema.create("DataInfo")
                    .addField("recentOk", boolean.class)
                    .addField("circleFavoriteOk", boolean.class)
                    .addField("gridOk", boolean.class)
                    .addField("blackListOk", boolean.class)
                    .addField("quickActionOk", boolean.class)
                    .addField("edge1Ok", boolean.class)
                    .addField("edge2Ok", boolean.class)
                    .addField("id", String.class, FieldAttribute.PRIMARY_KEY);





            oldVersion++;

        }

    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MyRealmMigration;
    }
}
