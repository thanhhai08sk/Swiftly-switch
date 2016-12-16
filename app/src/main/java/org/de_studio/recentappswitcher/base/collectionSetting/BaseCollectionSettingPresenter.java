package org.de_studio.recentappswitcher.base.collectionSetting;

import android.support.v7.widget.RecyclerView;

import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.DragAndDropCallback;
import org.de_studio.recentappswitcher.base.PresenterView;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Slot;
import org.de_studio.recentappswitcher.utils.GridSpacingItemDecoration;

import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/26/16.
 */

public abstract class BaseCollectionSettingPresenter<V extends BaseCollectionSettingPresenter.View, M extends BaseCollectionSettingModel> extends BasePresenter<V, M> {
    private static final String TAG = BaseCollectionSettingPresenter.class.getSimpleName();

    boolean onDragDrop = false;

    public BaseCollectionSettingPresenter(M model) {
        super(model);
    }

    @Override
    public void onViewAttach(final V view) {
        super.onViewAttach(view);
        model.setup();
        view.setSpinner(model.getCollectionList(), model.getCurrentCollection());
        setRecyclerView();

        addSubscription(
                view.onMoveItem().subscribe(new Action1<DragAndDropCallback.MoveData>() {
                    @Override
                    public void call(DragAndDropCallback.MoveData moveData) {
                        model.moveItem(moveData.from, moveData.to);
                        view.notifyItemMove(moveData.from, moveData.to);

                    }
                })
        );

        addSubscription(
                view.onDropItem().subscribe(new Action1<DragAndDropCallback.DropData>() {
                    @Override
                    public void call(DragAndDropCallback.DropData dropData) {
                        if (view.isHoverOnDeleteButton(dropData.dropX, dropData.dropY)) {
                            model.removeItem(dropData.position);
                            view.notifyItemRemove(dropData.position);
                        }
                        view.setDeleteButtonVisibility(false);
                        onDragDrop = false;
                    }
                })
        );

        addSubscription(
                view.onDragItem().subscribe(new Action1<DragAndDropCallback.Coord>() {
                    @Override
                    public void call(DragAndDropCallback.Coord coord) {
                        onDragDrop = true;
                        view.setDeleteButtonVisibility(true);
                        if (view.isHoverOnDeleteButton(coord.x, coord.y)) {
                            view.setDeleteButtonColor(true);
                        } else {
                            view.setDeleteButtonColor(false);
                        }
                    }
                })
        );

        addSubscription(
                model.onCollectionChanged().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (!onDragDrop) {
                            view.notifyAdapter();
                        }
                    }
                })
        );

    }

    public abstract void setRecyclerView();

    public void onSpinnerItemSelect(String itemLabel) {

        model.setCurrentCollection(itemLabel);
        view.updateRecyclerView(model.getSlots());
    }

    public void onAddNewCollection() {
        String newLabel = model.createNewCollection();
        view.addCollectionToSpinner(newLabel);
        onSpinnerItemSelect(newLabel);
    }

    public void onSlotClick(int slotIndex) {
        view.openSetItems(slotIndex, model.getCollectionId());
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

    public interface View extends PresenterView {
        PublishSubject<DragAndDropCallback.MoveData> onMoveItem();

        PublishSubject<DragAndDropCallback.DropData> onDropItem();

        PublishSubject<DragAndDropCallback.Coord> onDragItem();

        void notifyItemMove(int from, int to);

        void notifyItemRemove(int position);

        void notifyAdapter();

        void setDeleteButtonVisibility(boolean visible);

        void setDeleteButtonColor(boolean red);

        boolean isHoverOnDeleteButton(float x, float y);

        void updateRecyclerView(OrderedRealmCollection<Slot> slots);

        void setSpinner(RealmResults<Collection> collections, Collection currentCollection);

        void addCollectionToSpinner(String collectionLabel);

        void openSetItems(int slotIndex, String collectionId);

        void openSetFolder(String folderId);

        void showChooseSizeDialog();

        void showChooseBetweenSetFolderAndSetItems(final int slotIndex);
        void setRecyclerView(OrderedRealmCollection<Slot> slots, RecyclerView.LayoutManager layoutManager, GridSpacingItemDecoration decoration);

        RecyclerView.LayoutManager getLayoutManager(int layoutType, int column);

        int dpToPixel(int dp);


    }


}
