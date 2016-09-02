package org.de_studio.recentappswitcher.dagger;

import android.content.Context;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.MyRealmMigration;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static org.de_studio.recentappswitcher.Cons.CURRENT_SCHEMA_VERSION;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_CIRCLE_REALM_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_REALM_NAME;
import static org.de_studio.recentappswitcher.Cons.PIN_REALM_NAME;

/**
 * Created by HaiNguyen on 8/27/16.
 */
@Module
public class RealmModule {
    Context context;

    public RealmModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    @Named(PIN_REALM_NAME)
    Realm pinRealm() {
        return Realm.getInstance(new RealmConfiguration.Builder(context)
                .name(Cons.PIN_REALM_NAME)
                .schemaVersion(Cons.CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
    }

    @Provides
    @Singleton
    @Named(FAVORITE_GRID_REALM_NAME)
    Realm favoriteGridRealm() {
        return Realm.getInstance(new RealmConfiguration.Builder(context)
                .name("default.realm")
                .schemaVersion(Cons.CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
    }

    @Provides
    @Singleton
    @Named(FAVORITE_CIRCLE_REALM_NAME)
    Realm favoriteCircleRealm() {
        return Realm.getInstance(new RealmConfiguration.Builder(context)
                .name("circleFavo.realm")
                .schemaVersion(CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
    }
}
