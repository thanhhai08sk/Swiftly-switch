package org.de_studio.recentappswitcher.blackListSetting;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class BlackListSettingPresenter extends BasePresenter<BlackListSettingPresenter.View, BlackListSettingModel> {
    RealmResults<Item> appsList;
    RealmList<Item> blackListItems;
    Realm realm = Realm.getDefaultInstance();
    public BlackListSettingPresenter(BlackListSettingModel model) {
        super(model);
    }


    @Override
    public void onViewAttach(final View view) {
        super.onViewAttach(view);
        view.loadApps();
        getBlackListItems();

        if (blackListItems.isValid()) {
            blackListItems.addChangeListener(new RealmChangeListener<RealmList<Item>>() {
                @Override
                public void onChange(RealmList<Item> items) {
                    view.updateAdapter();
                }
            });
        }
//        view.setProgressBar(true);

        appsList = realm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_APP).findAllSortedAsync(Cons.LABEL);
        view.setAdapter(appsList, blackListItems);
        appsList.addChangeListener(new RealmChangeListener<RealmResults<Item>>() {
            @Override
            public void onChange(RealmResults<Item> element) {
                if (element.size()>0) {
                    view.setProgressBar(false);

                }
            }
        });

        addSubscription(
                view.onSetItem().subscribe(new Action1<Item>() {
                    @Override
                    public void call(final Item item) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if (blackListItems.contains(item)) {
                                    blackListItems.remove(item);
                                } else {
                                    blackListItems.add(item);
                                }
                            }
                        });
                    }
                })
        );

    }

    @Override
    public void onViewDetach() {
        appsList.removeAllChangeListeners();
        blackListItems.removeAllChangeListeners();
        realm.close();
        super.onViewDetach();
    }

    private void getBlackListItems() {
        Collection blackList = realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_BLACK_LIST).findFirst();
        if (blackList != null) {
            blackListItems = blackList.items;
        }
    }

    public interface View extends PresenterView {
        void loadApps();

        void setProgressBar(boolean show);

        void setAdapter(OrderedRealmCollection<Item> appList, RealmList<Item> blackListItems);

        PublishSubject<Item> onSetItem();

        void updateAdapter();
    }
}
