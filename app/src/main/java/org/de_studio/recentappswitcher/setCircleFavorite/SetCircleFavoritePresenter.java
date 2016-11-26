package org.de_studio.recentappswitcher.setCircleFavorite;

import org.de_studio.recentappswitcher.Cons;
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
        setRecyclerView();
    }

    public void setRecyclerView() {
        view.setRecyclerView(model.getSlots(), view.getLayoutManager(Cons.LAYOUT_TYPE_LINEAR, -1));
    }

    public void onSpinnerItemSelect(String itemLabel) {

        model.setCurrentCollection(itemLabel);
        view.updateRecyclerView(model.getSlots());
    }

    public void onAddNewCollection() {
        String newLabel =  model.createNewCollection();
        view.addCollectionToSpinner(newLabel);
        onSpinnerItemSelect(newLabel);
    }

    public void onSlotClick(int slotIndex) {
        view.openSetItems(slotIndex,model.getCollectionId());
    }

    public void onChooseCollectionSize(int size) {
        model.setCurrentCollectionSize(size);
    }


    @Override
    public void onViewDetach() {
        model.clear();
        view.clear();
        super.onViewDetach();
    }

    public void onSizeClick() {
        view.showChooseSizeDialog();
    }

    public void onLongClickModeClick() {

    }
}
