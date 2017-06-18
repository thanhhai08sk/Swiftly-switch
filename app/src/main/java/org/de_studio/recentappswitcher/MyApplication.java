package org.de_studio.recentappswitcher;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import org.de_studio.recentappswitcher.dadaSetup.MyNewRealmMigration;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by hai on 3/24/2016.
 */
public class MyApplication extends MultiDexApplication {
    private static final String TAG = MyApplication.class.getSimpleName();
    private static Context mContext;
    private boolean edgeIsOn;
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .name(Cons.DEFAULT_REALM_NAME)
                .schemaVersion(Cons.REALM_SCHEMA_VERSION)
                .migration(new MyNewRealmMigration())
                .build());
        this.mContext = this;
    }

    public boolean isEdgeIsOn() {
        return edgeIsOn;
    }

    public void setEdgeIsOn(boolean edgeIsOn) {
        this.edgeIsOn = edgeIsOn;
    }

    private boolean checkIfDataOk() {
        Realm realm = Realm.getDefaultInstance();
        boolean isOk = !realm.isEmpty();
        Log.e(TAG, "checkIfDataOk: " + isOk);
        realm.close();
        return isOk;
    }


    public static Context getContext() {
        return mContext;
    }
}
