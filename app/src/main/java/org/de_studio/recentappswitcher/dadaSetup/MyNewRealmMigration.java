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
            schema.get("Collection")
                    .removeField("longClickCollection")
                    .addRealmObjectField("longPressCollection", schema.get("Collection"));

            oldVersion++;
        }

        if (oldVersion ==1) {
            schema.get("Collection")
                    .addField("stayOnScreen", Boolean.class);
            oldVersion++;
        }

        //// TODO: 2/20/17 don't forget to implement the old realmMigration
    }
}
