package org.de_studio.recentappswitcher.setItems.chooseShortcutsSet;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseChooseItemDialogView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.ChooseShortcutsSetDialogModule;
import org.de_studio.recentappswitcher.dagger.DaggerChooseShortcutsSetDialogComponent;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Item;

import java.lang.ref.WeakReference;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class ChooseShortcutsSetDialogView extends BaseChooseItemDialogView {
    String collectionType;


    public static ChooseShortcutsSetDialogView newInstance(String collectionTYpe) {

        Bundle args = new Bundle();
        args.putString(Cons.TYPE, collectionTYpe);
        ChooseShortcutsSetDialogView fragment = new ChooseShortcutsSetDialogView();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        collectionType = getArguments().getString(Cons.TYPE);
    }

    @Override
    public void loadItems() {
        LoadShortcutsSetsTask loadShortcutsSetsTask = new LoadShortcutsSetsTask(new WeakReference<Context>(getActivity()), null);
        loadShortcutsSetsTask.execute();
    }

    @Override
    protected void inject() {
        DaggerChooseShortcutsSetDialogComponent.builder()
                .appModule(new AppModule(getActivity()))
                .chooseShortcutsSetDialogModule(new ChooseShortcutsSetDialogModule(this, collectionType))
                .build().inject(this);
    }

    public static class LoadShortcutsSetsTask extends AsyncTask<Void, Void, Void> {
        WeakReference<Context> contextWeakReference;
        PublishSubject<Void> loadItemsOkSubject;

        public LoadShortcutsSetsTask(WeakReference<Context> contextWeakReference, PublishSubject<Void> loadItemsOkSubject) {
            this.contextWeakReference = contextWeakReference;
            this.loadItemsOkSubject = loadItemsOkSubject;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Collection> collections = realm.where(Collection.class)
                            .equalTo(Cons.TYPE, Collection.TYPE_GRID_FAVORITE)
                            .or()
                            .equalTo(Cons.TYPE, Collection.TYPE_CIRCLE_FAVORITE)
                            .or()
                            .equalTo(Cons.TYPE, Collection.TYPE_RECENT)
                            .findAllSorted(Cons.LABEL);
                    for (Collection collection : collections) {
                        Item item = realm.where(Item.class).equalTo(Cons.ITEM_ID, Item.TYPE_SHORTCUTS_SET + collection.collectionId).findFirst();
                        if (item == null) {
                            item = new Item();
                            item.type = Item.TYPE_SHORTCUTS_SET;
                            item.itemId = Item.TYPE_SHORTCUTS_SET + collection.collectionId;
                            item.collectionId = collection.collectionId;
                            item.label = collection.label;
                            Utility.setItemBitmapForShortcutsSet(contextWeakReference.get(), item);
                            realm.copyToRealm(item);
                        } else {
                            Utility.setItemBitmapForShortcutsSet(contextWeakReference.get(), item);
                        }
                    }
                }
            });

            realm.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (loadItemsOkSubject != null) {
                loadItemsOkSubject.onNext(null);
            }
            super.onPostExecute(aVoid);
        }
    }

}
