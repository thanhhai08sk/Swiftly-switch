package org.de_studio.recentappswitcher.setCircleFavorite;

import org.de_studio.recentappswitcher.base.BasePresenter;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class SetCircleFavoritePresenter extends BasePresenter {
    SetCircleFavoriteView view;
    SetCircleFavoriteModel model;

    public SetCircleFavoritePresenter(SetCircleFavoriteView view, SetCircleFavoriteModel model) {
        this.view = view;
        this.model = model;
    }


    public void onSpinnerItemSelect(String itemId) {

    }

    @Override
    public void onViewAttach() {

    }

    @Override
    public void onViewDetach() {
        super.onViewDetach();
    }

    public void onSizeClick() {

    }

    public void onLongClickModeClick() {

    }
}
