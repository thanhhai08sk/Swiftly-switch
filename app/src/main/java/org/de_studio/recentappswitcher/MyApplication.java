package org.de_studio.recentappswitcher;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.de_studio.recentappswitcher.dadaSetup.DataSetupService;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by hai on 3/24/2016.
 */
public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
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

        if (!checkIfDataOk()) {
            Log.e(TAG, "onCreate: start dataSetupService");
            startService(new Intent(this, DataSetupService.class));
        }
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
