package org.de_studio.recentappswitcher.setItems;

import org.de_studio.recentappswitcher.base.BasePresenter;

import rx.functions.Action1;

/**
 * Created by HaiNguyen on 11/18/16.
 */

public class SetItemsPresenter extends BasePresenter {
    SetItemsView view;
    SetItemsModel model;

    public SetItemsPresenter(SetItemsView view, SetItemsModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onViewAttach() {


        addSubscription(
                view.onNextButton().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                    }
                })
        );

    }
}
