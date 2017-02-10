package org.de_studio.recentappswitcher;

import android.util.Log;

import io.realm.DynamicRealm;
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
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MyRealmMigration;
    }
}
