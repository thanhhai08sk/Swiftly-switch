package org.de_studio.recentappswitcher.setCircleFavorite;

import org.de_studio.recentappswitcher.base.BasePresenter;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class SetCircleFavoritePresenter extends BasePresenter {
    private static final String TAG = SetCircleFavoritePresenter.class.getSimpleName();
    SetCircleFavoriteView view;
    SetCircleFavoriteModel model;

    public SetCircleFavoritePresenter(SetCircleFavoriteView view, SetCircleFavoriteModel model) {
        this.view = view;
        this.model = model;
    }


    @Override
    public void onViewAttach() {
        view.setSpinner(model.getCollectionList(), model.getCurrentCollection());
        view.setRecyclerView(model.getSlots());

    }

    public void onSpinnerItemSelect(String itemLabel) {
        model.setCollection(itemLabel);
        view.updateRecyclerView(model.getSlots());
    }

    public void onSlotClick(int position) {

    }


    @Override
    public void onViewDetach() {
        model.clear();
        view.clear();
        super.onViewDetach();
    }

    public void onSizeClick() {

    }

    public void onLongClickModeClick() {

    }
}
