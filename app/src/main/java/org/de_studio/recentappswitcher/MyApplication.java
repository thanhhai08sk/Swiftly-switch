package org.de_studio.recentappswitcher;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by hai on 3/24/2016.
 */
public class MyApplication extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .name(Cons.DEFAULT_REALM_NAME)
                .schemaVersion(Cons.REALM_SCHEMA_VERSION)
                .deleteRealmIfMigrationNeeded().build());
        this.mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
