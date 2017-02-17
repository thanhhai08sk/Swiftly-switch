package org.de_studio.recentappswitcher.base.collectionSetting;

import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Item;

import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 2/17/17.
 */

public abstract class BaseCircleCollectionSettingPresenter<V extends BaseCircleCollectionSettingPresenter.View, M extends BaseCircleCollectionSettingModel> extends BaseCollectionSettingPresenter<V,M> {


    PublishSubject<Item> chooseLongPressCollectionSJ = PublishSubject.create();
    @Override
    public void onViewAttach(final V view) {
        super.onViewAttach(view);
        addSubscription(
                view.onChooseLongPressMode().subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        switch (integer) {
                            case Collection.LONG_CLICK_MODE_OPEN_COLLECTION:
                                view.chooseLongPressCollection(chooseLongPressCollectionSJ);
                                break;
                            case Collection.LONG_CLICK_MODE_NONE:
                                model.setLongPress(Collection.LONG_CLICK_MODE_NONE, null);
                                break;
                        }
                    }
                })
        );

        addSubscription(
                chooseLongPressCollectionSJ.subscribe(new Action1<Item>() {
                    @Override
                    public void call(Item item) {
                        model.setLongPress(Collection.LONG_CLICK_MODE_OPEN_COLLECTION, item);
                    }
                })
        );
    }

    public BaseCircleCollectionSettingPresenter(M model) {
        super(model);
    }

    public void onLongPressAction() {
        view.chooseLongPressMode();
    }

    public interface View extends BaseCollectionSettingPresenter.View {

        PublishSubject<Integer> onChooseLongPressMode();

        void chooseLongPressMode();

        void chooseLongPressCollection(PublishSubject<Item> chooseLongPressCollectionSJ);


    }
}
