package org.de_studio.recentappswitcher.setItems.chooseApp;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseChooseItemFragmentView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.ChooseAppFragmentModule;
import org.de_studio.recentappswitcher.dagger.DaggerChooseAppFragmentComponent;
import org.de_studio.recentappswitcher.model.Item;

import java.lang.ref.WeakReference;
import java.util.Set;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 11/18/16.
 */

public class ChooseAppFragmentView extends BaseChooseItemFragmentView<ChooseAppPresenter> {



    @Override
    protected void inject() {
        DaggerChooseAppFragmentComponent.builder()
                .appModule(new AppModule(getActivity()))
                .chooseAppFragmentModule(new ChooseAppFragmentModule(this))
                .build().inject(this);
    }

    @Override
    public void loadItems() {
        LoadAppsTask task = new LoadAppsTask(new WeakReference<PackageManager>(getActivity().getPackageManager()));
        task.execute();
    }

    public static class LoadAppsTask extends AsyncTask<Void, Void, Void> {
        WeakReference<PackageManager> packageManagerWeakReference;

        public LoadAppsTask(WeakReference<PackageManager> packageManagerWeakReference) {
            this.packageManagerWeakReference = packageManagerWeakReference;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Realm realm = Realm.getDefaultInstance();
            Set<PackageInfo> packageInfos = Utility.getInstalledApps(packageManagerWeakReference.get());
            Item tempItem;
            for (final PackageInfo info : packageInfos) {
                final String itemId = Utility.createAppItemId(info.packageName);
                tempItem = realm.where(Item.class).equalTo(Cons.ITEM_ID, itemId).findFirst();
                if (tempItem == null) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Item item = new Item();
                            item.type = Item.TYPE_APP;
                            item.itemId = itemId;
                            item.packageName = info.packageName;
                            item.label = info.applicationInfo.loadLabel(packageManagerWeakReference.get()).toString();
                            try {
                                realm.copyToRealm(item);
                            } catch (Exception e) {
                                Log.e("LoadAppsTask", "execute: error when insert app shortcut");
                            }
                        }
                    });
                }
            }
            realm.close();
            return null;
        }
    }
}
