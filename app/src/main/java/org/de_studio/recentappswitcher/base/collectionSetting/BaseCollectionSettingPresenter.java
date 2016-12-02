package org.de_studio.recentappswitcher.base.collectionSetting;

import org.de_studio.recentappswitcher.base.BasePresenter;

/**
 * Created by HaiNguyen on 11/26/16.
 */

public abstract class BaseCollectionSettingPresenter extends BasePresenter< BaseCollectionSettingView, BaseCollectionSettingModel> {
    private static final String TAG = BaseCollectionSettingPresenter.class.getSimpleName();

    public BaseCollectionSettingPresenter(BaseCollectionSettingModel model) {
        this.model = model;
    }

    @Override
    public void onViewAttach(BaseCollectionSettingView view) {
        super.onViewAttach(view);
        model.setup();
        view.setSpinner(model.getCollectionList(), model.getCurrentCollection());
        setRecyclerView();
    }

    public abstract void setRecyclerView();

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

    public void setFolder(int slotIndex) {
        model.setSlotAsFolder(slotIndex);
    }

    public void setItems(int slotIndex) {
        view.openSetItems(slotIndex, model.getCollectionId());
    }

    public void onChooseCollectionSize(int size) {
        model.setCurrentCollectionSize(size);
    }


    public void onSizeClick() {
        view.showChooseSizeDialog();
    }

    public void onLongClickModeClick() {

    }


}
