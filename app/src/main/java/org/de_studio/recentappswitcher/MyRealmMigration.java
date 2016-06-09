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
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 0) {
            RealmObjectSchema shortcutSchema =  schema.get("Shortcut");
            try {
                shortcutSchema.addField("thumbnailUri", String.class)
                        .addField("number", String.class)
                        .addField("name", String.class)
                        .addField("contactId", Long.class);
            } catch (IllegalArgumentException e) {
                Log.e("MyRealmMigration", "migrate: " + e);
                e.printStackTrace();
            }

            oldVersion++;
        }
    }
}
