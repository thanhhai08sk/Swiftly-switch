package org.de_studio.recentappswitcher.dadaSetup;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.model.Item;

import java.util.Set;

import io.realm.Realm;


public class DataSetupService extends IntentService {
    PackageManager packageManager = this.getPackageManager();
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_CREATE_ITEMS = "org.de_studio.recentappswitcher.dadaSetup.action.CREATE_ITEM";
    public static final String ACTION_CONVERT = "org.de_studio.recentappswitcher.dadaSetup.action.CONVERT";


    public DataSetupService() {
        super("DataSetupService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CREATE_ITEMS.equals(action)) {
                handleActionCreateItems();
            } else if (ACTION_CONVERT.equals(action)) {
                handleActionConvert();
            }
        }
    }

    private void handleActionCreateItems() {
        Realm realm = Realm.getDefaultInstance();
        final Set<PackageInfo> packageInfos = getAppsList();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (PackageInfo packageInfo : packageInfos) {
                    Item item = new Item();
                    item.itemId = Cons.ITEM_ID_APP + packageInfo.packageName;
                    item.type = Item.TYPE_APP;
                    item.label = (String) packageManager.getApplicationLabel(packageInfo.applicationInfo);
                    item.packageName = packageInfo.packageName;
                    realm.copyToRealm(item);
                }
            }
        });


        realm.close();

    }


    private void handleActionConvert() {

    }


    private Set<PackageInfo> getAppsList() {
        return Utility.getInstalledApps(this);
    }
}
