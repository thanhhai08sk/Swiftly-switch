package org.de_studio.recentappswitcher.base.addItemsToFolder;

import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/3/16.
 */

public abstract class BaseAddItemsToFolderPresenter extends BasePresenter<BaseAddItemsToFolderPresenter.View, BaseModel> {
    private static final String TAG = BaseAddItemsToFolderPresenter.class.getSimpleName();

    protected RealmResults<Item> results;
    protected Realm realm = Realm.getDefaultInstance();
    protected RealmList<Item> folderItems;
    protected String folderId;

    public BaseAddItemsToFolderPresenter(BaseModel model, String folderId) {
        super(model);
        this.folderId = folderId;
    }

    @Override
    public void onViewAttach(final View view) {
        super.onViewAttach(view);
        view.loadItems();
        loadFolderItems();

//        view.setProgressBar(true);
        results = getItemRealmResult();
        if (results != null) {
            view.setAdapter(results, folderItems);
            results.addChangeListener(new RealmChangeListener<RealmResults<Item>>() {
                @Override
                public void onChange(RealmResults<Item> element) {
                    if (element.size()>0) {
                        view.setProgressBar(false);
                        view.setAdapter(element, folderItems);
                    }
                }
            });
        }

        addSubscription(
                view.onSetItem().subscribe(new Action1<Item>() {
                    @Override
                    public void call(final Item item) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Log.e(TAG, "execute: set item " + item.itemId);
                                if (!folderItems.contains(item)) {
                                    folderItems.add(item);
                                } else {
                                    folderItems.remove(item);
                                }
                            }
                        });
                    }
                })
        );

    }

    private void loadFolderItems() {
        Slot slot = realm.where(Slot.class).equalTo(Cons.SLOT_ID, folderId).findFirst();
        if (slot!= null && slot.type.equals(Slot.TYPE_FOLDER)) {
            folderItems = slot.items;
            folderItems.addChangeListener(new RealmChangeListener<RealmList<Item>>() {
                @Override
                public void onChange(RealmList<Item> items) {
                    view.notifyAdapter();
                }
            });
        }
    }

    protected abstract RealmResults<Item> getItemRealmResult();


    @Override
    public void onViewDetach() {
        super.onViewDetach();
        if (results != null) {
            results.removeAllChangeListeners();
        }
        if (folderItems != null) {
            folderItems.removeAllChangeListeners();
        }
        realm.close();
    }


    public interface View extends PresenterView {
        void loadItems();

        void setProgressBar(boolean visible);

        void setAdapter(OrderedRealmCollection<Item> result, RealmList<Item> folderItems);

        PublishSubject<Item> onSetItem();

        PublishSubject<Void> onLayouted();

        void notifyAdapter();
    }
}
