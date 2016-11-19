package org.de_studio.recentappswitcher.setItems;

import android.util.Log;

import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.model.Item;

import rx.functions.Action1;

/**
 * Created by HaiNguyen on 11/18/16.
 */

public class SetItemsPresenter extends BasePresenter {
    private static final String TAG = SetItemsPresenter.class.getSimpleName();
    SetItemsView view;
    SetItemsModel model;
    int currentIndex;


    public SetItemsPresenter(SetItemsView view, SetItemsModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onViewAttach() {
        currentIndex = view.itemIndex;

        addSubscription(
                view.onNextButton().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (currentIndex < model.getMaxIndex()) {
                            Item item = model.getNextItem(currentIndex);
                            currentIndex++;
                            view.onCurrentItemChange().onNext(item);
                        }

                    }
                })
        );

        addSubscription(
                view.onPreviousButton().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (currentIndex >0) {
                            Item item = model.getPreviousItem(currentIndex);
                            currentIndex--;
                            view.onCurrentItemChange().onNext(item);
                        }
                    }
                })
        );

        addSubscription(
                view.onCurrentItemChange()
                        .subscribe(new Action1<Item>() {
                    @Override
                    public void call(Item item) {
                        Log.e(TAG, "call: on current item change");
                        view.showCurrentIconAndIndex(item, currentIndex);
                    }
                })
        );

        addSubscription(
                view.onSetItem().subscribe(new Action1<Item>() {
                    @Override
                    public void call(Item item) {
                        Log.e(TAG, "call SetItemsSubject " + item.label);
                        model.setCurrentItem(item, currentIndex);
                        view.onCurrentItemChange().onNext(item);

                    }
                })
        );

        addSubscription(
                view.onOkButton().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        view.finishAfterTransition();
                    }
                })
        );

        view.onCurrentItemChange().onNext(model.getCurrentItem(currentIndex));
    }


    @Override
    public void onViewDetach() {
        super.onViewDetach();
        model.clear();
        view.clear();
    }
}
