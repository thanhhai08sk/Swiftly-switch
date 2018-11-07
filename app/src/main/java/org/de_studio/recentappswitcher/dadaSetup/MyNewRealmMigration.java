package org.de_studio.recentappswitcher.dadaSetup;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by HaiNguyen on 2/11/17.
 */

public class MyNewRealmMigration implements RealmMigration {
    private static final String TAG = MyNewRealmMigration.class.getSimpleName();
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        Log.e(TAG, "migrate: old = " + oldVersion + "\nnew = " + newVersion);
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 0) {
            Log.e(TAG, "migrate: oldVersion = " + oldVersion);
            schema.get("Collection")
                    .removeField("longClickCollection")
                    .addRealmObjectField("longPressCollection", schema.get("Collection"));

            oldVersion++;
        }

        if (oldVersion ==1) {
            Log.e(TAG, "migrate: oldVersion = " + oldVersion);
            schema.get("Collection")
                    .addField("stayOnScreen", Boolean.class);
            oldVersion++;
        }

        if (oldVersion == 2) {
            Log.e(TAG, "migrate: oldVersion = " + oldVersion);
            schema.get("Collection")
                    .addField("visibilityOption", int.class);
            oldVersion++;
        }

        if (oldVersion == 3) {
            Log.e(TAG, "migrate: oldVersion = " + oldVersion);
            schema.get("Item")
                    .addField("iconBitmap2", byte[].class)
                    .addField("iconBitmap3", byte[].class);
            oldVersion++;
        }
        if (oldVersion == 4) {
            Log.e(TAG, "migrate: oldVersion = " + oldVersion);
            schema.get("Slot")
                    .addField("instant", boolean.class);
            oldVersion++;
        }

        if (oldVersion == 5) {
            Log.e(TAG, "migrate: oldVersion = " + oldVersion);
            schema.get("DataInfo")
                    .addField("initGridItemOk", boolean.class);
            oldVersion++;
        }

        if (oldVersion == 6) {
            Log.e(TAG, "migrate: oldVersion = " + oldVersion);
            schema.get("Slot")
                    .addField("label", String.class);
            oldVersion++;
        }
        if (oldVersion == 7) {
            Log.e(TAG, "migrate: oldVersion = " + oldVersion);
            schema.get("Slot")
                    .addField("useIconSetByUser", boolean.class);
            oldVersion++;
        }
        if (oldVersion == 8) {
            Log.e(TAG, "migrate: oldVersion = " + oldVersion);
            schema.get("Item")
                    .addField("originalIconBitmap", byte[].class);
            oldVersion++;
        }

        if (oldVersion == 9) {
            Log.e(TAG, "migrate: oldVersion = " + oldVersion);
            schema.get("DataInfo")
                    .addField("initNavigationQuickActionOk", boolean.class);
            oldVersion++;
        }

   //// TODO: 2/20/17 don't forget to implement the old realmMigration
    }
}
