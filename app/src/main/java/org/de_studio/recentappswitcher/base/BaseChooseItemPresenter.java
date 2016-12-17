package org.de_studio.recentappswitcher.base;

import org.de_studio.recentappswitcher.model.Item;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/25/16.
 */

public abstract class BaseChooseItemPresenter extends BasePresenter<BaseChooseItemPresenter.View,BaseModel> {
    protected RealmResults<Item> results;
    protected Realm realm = Realm.getDefaultInstance();

    public BaseChooseItemPresenter(BaseModel model) {
        super(model);
    }

    @Override
    public void onViewAttach(final View view) {
        super.onViewAttach(view);
        view.loadItems();
        view.setProgressBar(true);
        results = getItemRealmResult();
        if (results != null) {
            view.setAdapter(results);
            results.addChangeListener(new RealmChangeListener<RealmResults<Item>>() {
                @Override
                public void onChange(RealmResults<Item> element) {
                    if (element.size()>0) {
                        view.setProgressBar(false);
                    }
                }
            });
        }

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
                view.onItemClick().subscribe(new Action1<Item>() {
                    @Override
                    public void call(Item item) {
                        view.onSetItemToSlot().onNext(item);
                        view.dismissIfDialog();
                    }
                })
        );

    }


    @Override
    public void onViewDetach() {
        super.onViewDetach();
        if (results != null) {
            results.removeChangeListeners();
        }
        realm.close();
    }

    protected abstract RealmResults<Item> getItemRealmResult();

    public interface View extends PresenterView {
        PublishSubject<Item> onItemClick();

        PublishSubject<Item> onSetItemToSlot();

        BehaviorSubject<Item> onCurrentItemChange();

        void loadItems();

        void setAdapter(RealmResults<Item> items);

        void setCurrentItem(Item item);

        void setProgressBar(boolean visible);

        void dismissIfDialog();
    }

}
