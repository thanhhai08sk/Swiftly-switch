package org.de_studio.recentappswitcher.base;

import android.util.Log;

import org.de_studio.recentappswitcher.model.Item;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/25/16.
 */

public abstract class BaseChooseItemPresenter extends BasePresenter<BaseChooseItemPresenter.View, BaseModel> {
    private static final String TAG = BaseChooseItemPresenter.class.getSimpleName();
    protected RealmResults<Item> results;
    protected Realm realm = Realm.getDefaultInstance();

    public BaseChooseItemPresenter(BaseModel model) {
        super(model);
    }

    @Override
    public void onViewAttach(final View view) {
        super.onViewAttach(view);

        addSubscription(
                view.onViewCreated().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        results = getItemRealmResult();
                        if (results != null) {
                            view.setAdapter(results);
                            results.addChangeListener(new RealmChangeListener<RealmResults<Item>>() {
                                @Override
                                public void onChange(RealmResults<Item> element) {
                                    if (element.size() > 0) {
                                        view.setProgressBar(false);
                                    }
                                }
                            });
                        }
                    }
                })
        );

        if (view.onCurrentItemChange() != null) {
            addSubscription(
                    view.onCurrentItemChange().subscribe(new Action1<Item>() {
                        @Override
                        public void call(Item item) {
                            view.setCurrentItem(item);
                        }
                    })
            );
        }

        addSubscription(
                view.onItemClick()
                        .flatMap(new Func1<Item, Observable<Item>>() {
                            @Override
                            public Observable<Item> call(Item item) {
                                Log.e(TAG, "call: on item, type = " + item.type + "\naction = " + item.action);
                                if (item.type.equals(Item.TYPE_ACTION) && item.action == Item.ACTION_SCREEN_LOCK) {
                                    if (view.checkHasAdminPermission()) {
                                        return Observable.just(item);
                                    }else {
                                        view.noticeUserAboutScreenLock();
                                        return Observable.empty();
                                    }
                                } else return Observable.just(item);
                            }
                        })
                        .subscribe(new Action1<Item>() {
                    @Override
                    public void call(Item item) {
                        view.onSetItemToSlot().onNext(item);
                        view.dismissIfDialog();
                    }
                })
        );



        if (view.onContactPermissionGranted() != null) {

            addSubscription(
                    view.onContactPermissionGranted().subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            view.showNeedContactButton(false);
                            view.loadItems();
                        }
                    })
            );

            addSubscription(
                    view.onViewCreated().withLatestFrom(view.onNeedContactPermission().first(), new Func2<Void, Void, Boolean>() {
                        @Override
                        public Boolean call(Void aVoid, Void aVoid2) {
                            return true;
                        }
                    }).subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            view.showNeedContactButton(true);
                        }
                    })
            );
        }

        view.loadItems();


    }


    @Override
    public void onViewDetach() {
        super.onViewDetach();
        if (results != null) {
            results.removeChangeListeners();
        }
        realm.removeAllChangeListeners();
        realm.close();
    }


    protected abstract RealmResults<Item> getItemRealmResult();

    public interface View extends PresenterView {
        PublishSubject<Void> onViewCreated();

        PublishSubject<Item> onItemClick();

        PublishSubject<Item> onSetItemToSlot();

        BehaviorSubject<Item> onCurrentItemChange();

        PublishSubject<Void> onNeedContactPermission();

        PublishSubject<Void> onContactPermissionGranted();

        void showNeedContactButton(boolean visible);

        void loadItems();

        void setAdapter(RealmResults<Item> items);

        void setCurrentItem(Item item);

        void setProgressBar(boolean visible);

        void dismissIfDialog();

        void noticeUserAboutScreenLock();

        boolean checkHasAdminPermission();
    }

}
