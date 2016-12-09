package org.de_studio.recentappswitcher.setItems.chooseAction;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseChooseItemView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.ChooseActionModule;
import org.de_studio.recentappswitcher.dagger.DaggerChooseActionComponent;
import org.de_studio.recentappswitcher.model.Item;

import java.lang.ref.WeakReference;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 11/19/16.
 */

public class ChooseActionView extends BaseChooseItemView{
    private static final String TAG = ChooseActionView.class.getSimpleName();

    @Override
    protected void inject() {
        DaggerChooseActionComponent.builder()
                .appModule(new AppModule(getActivity()))
                .chooseActionModule(new ChooseActionModule(this))
                .build().inject(this);
    }

    @Override
    public void loadItems() {
        LoadActionsTask task = new LoadActionsTask(new WeakReference<Context>(getActivity()));
        task.execute();
    }

    public static class LoadActionsTask extends AsyncTask<Void, Void, Void> {
        WeakReference<Context> contextWeakReference;

        public LoadActionsTask(WeakReference<Context> contextWeakReference) {
            this.contextWeakReference = contextWeakReference;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Realm realm = Realm.getDefaultInstance();
            final String[] actionStrings = contextWeakReference.get().getResources().getStringArray(R.array.setting_shortcut_array_no_folder);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (String string : actionStrings) {
                        int action = Utility.getActionFromLabel(contextWeakReference.get(), string);
                        String itemId = Item.TYPE_ACTION + action;
                        Item item = realm.where(Item.class).equalTo(Cons.ITEM_ID, itemId).findFirst();
                        if (item == null) {
                            Log.e(TAG, "LoadActions - add action " + string);
                            Item newItem = new Item();
                            newItem.type = Item.TYPE_ACTION;
                            newItem.itemId = itemId;
                            newItem.label = string;
                            newItem.action = action;
                            Utility.setIconResourceIdsForAction(newItem);
                            realm.copyToRealm(newItem);
                        }
                    }

                }
            });
            realm.close();
            return null;
        }
    }

}
