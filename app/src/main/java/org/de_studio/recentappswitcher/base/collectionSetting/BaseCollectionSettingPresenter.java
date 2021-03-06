package org.de_studio.recentappswitcher.base.collectionSetting;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.DragAndDropCallback;
import org.de_studio.recentappswitcher.base.PresenterView;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;
import org.de_studio.recentappswitcher.utils.GridSpacingItemDecoration;

import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/26/16.
 */

public abstract class BaseCollectionSettingPresenter<V extends BaseCollectionSettingPresenter.View, M extends BaseCollectionSettingModel> extends BasePresenter<V, M> {
    private static final String TAG = BaseCollectionSettingPresenter.class.getSimpleName();
    public static final int CANNOT_DELETE_REASON_BEING_USED = 1;
    public static final int CANNOT_DELETE_REASON_THIS_IS_ONLY_ONE = 2;

    PublishSubject<Integer> setCircleSizeSJ = PublishSubject.create();

    boolean onDragDrop = false;
    PublishSubject<String> changeCurrentSetSJ = PublishSubject.create();

    public BaseCollectionSettingPresenter(M model) {
        super(model);
    }

    @Override
    public void onViewAttach(final V view) {
        super.onViewAttach(view);


        addSubscription(
                view.onMoveItem().distinctUntilChanged().subscribe(new Action1<DragAndDropCallback.MoveData>() {
                    @Override
                    public void call(DragAndDropCallback.MoveData moveData) {
                        onMoveItem(moveData);
//                        Log.e(TAG, "call: move from " + moveData.from + "\nto " + moveData.to);

                    }
                })
        );

//        addSubscription(
//                view.onDropItem().subscribe(new Action1<DragAndDropCallback.DropData>() {
//                    @Override
//                    public void call(DragAndDropCallback.DropData dropData) {
//                        onDropItem(dropData);
//                    }
//                })
//        );
//
        addSubscription(
                view.onDropItem().withLatestFrom(view.onDragItem(), new Func2<DragAndDropCallback.DropData, DragAndDropCallback.Coord, DragAndDropCallback.DropData>() {
                    @Override
                    public DragAndDropCallback.DropData call(DragAndDropCallback.DropData dropData, DragAndDropCallback.Coord coord) {
                        dropData.dropX = coord.xy[0];
                        dropData.dropY = coord.xy[1];
                        return dropData;
                    }
                }).subscribe(new Action1<DragAndDropCallback.DropData>() {
                    @Override
                    public void call(DragAndDropCallback.DropData dropData) {
                        if (dropData != null) {
                            onDropItem(dropData);
                        }
                    }
                })
        );

        addSubscription(
                view.onDragItem().subscribe(new Action1<DragAndDropCallback.Coord>() {
                    @Override
                    public void call(DragAndDropCallback.Coord coord) {
                        onDragDrop = true;
                        view.setDeleteButtonVisibility(true);
                        if (view.isHoverOnDeleteButton(coord.xy[0], coord.xy[1])) {
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
                        Log.e(TAG, "call: onDragDrop " + onDragDrop);
                        if (!onDragDrop) {
                            view.notifyAdapter();
                            view.updateCollectionInfo(model.getCurrentCollection());
                            setRecyclerView();
                        }
                    }
                })
        );

        addSubscription(
                view.onChooseCurrentSet().subscribe(new Action1<String>() {
                    @Override
                    public void call(String collectionId) {
                        changeCurrentSetSJ.onNext(collectionId);
                    }
                })
        );

        addSubscription(
                changeCurrentSetSJ.subscribe(new Action1<String>() {
                    @Override
                    public void call(String collectionId) {
                        if (collectionId != null) {
                            model.setCurrentCollection(collectionId);
                        } else {
                            model.setCurrentCollectionToAnotherOne();
                        }
                        view.updateRecyclerView(model.getSlots());
                    }
                })
        );



        addSubscription(
                setCircleSizeSJ.subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        model.setCurrentCollectionCircleSize(integer);
                        view.restartService();
                    }
                })
        );




        model.setup();
    }

    protected void onDropItem(DragAndDropCallback.DropData dropData) {
        if (view.isHoverOnDeleteButton(dropData.dropX, dropData.dropY)) {
            model.removeItem(dropData.position);
            view.notifyItemRemove(dropData.position);
        }
        view.setDeleteButtonVisibility(false);
        onDragDrop = false;
        if (model.collection.type.equals(Collection.TYPE_QUICK_ACTION)) {
            view.restartService();
        }
    }

    protected void onMoveItem(DragAndDropCallback.MoveData moveData) {
        if (moveData != null && moveData.from >=0 && moveData.to >=0) {
            Log.e(TAG, "onMoveItem: onDragDrop" + onDragDrop);
            model.moveItem(moveData.from, moveData.to);
//            view.notifyItemMove(moveData.from, moveData.to);
        }
    }

    public abstract void setRecyclerView();


    public void onCurrentSetClick() {
        view.chooseCurrentCollection(model.getCollectionList(), model.getCurrentCollection());
    }

    public void onAddNewCollection() {
        String newId = model.createNewCollection();
        model.setCurrentCollection(newId);
        view.updateRecyclerView(model.getSlots());
    }

    public void onSlotClick(int slotIndex) {
        Slot slot = model.getCurrentCollection().slots.get(slotIndex);
        view.chooseActionOnSlot(slotIndex,
                slot.type.equals(Slot.TYPE_ITEM),
                slot.type.equals(Slot.TYPE_FOLDER),
                model.getCurrentCollection().type.equals(Collection.TYPE_GRID_FAVORITE));

    }

    public void editItem(int slotIndex) {
        Slot slot = model.getCurrentCollection().slots.get(slotIndex);
        if (slot.type.equals(Slot.TYPE_FOLDER)) {
            view.editFolderLabelAndIcon(slot);
        } else if (slot.type.equals(Slot.TYPE_ITEM)) {
            Item item = model.getCurrentCollection().slots.get(slotIndex).stage1Item;
            if (item != null) {
                view.editItemLabelAndIcon(item);
            } else {
                Log.e(TAG, "editItem: item null");
            }

        }
    }

    public void setItemLabel(Item item, String label) {
        model.setItemLabel(item, label);
    }

    public void setItemIcon(Item item, int itemState) {
        if (!view.isFree()) {
            view.showChooseIconSourceDialog(item,null, itemState);
        } else {
            view.proOnlyDialog();
        }
    }

    public void setFolderLabel(Slot folder, String label) {
        model.setSlotLabel(folder, label);
    }

    public void setFolderIcon(Slot folder) {
        if (!view.isFree()) {
            view.showChooseIconSourceDialog(null, folder, -1);
        } else {
            view.proOnlyDialog();
        }
    }

    public void setFolderIconWithSource(IconPackManager.IconPack iconPack, Slot folder) {
        view.openFolderIconSetting(iconPack, folder);
    }

    public void resetFolderIcon(Slot folder) {
        view.resetFolderIcon(folder, model.getRealm());
    }

    public void resetItemIcon(Item item) {
        model.resetItemIcon(view.getActivity(), item);
    }

    public void setItemIconWithSource(SetItemIconInfo info) {
        view.openItemIconSetting(info);
    }

    public void editFolderContent(int slotIndex) {
        view.openSetFolder(model.getCurrentCollection().slots.get(slotIndex).slotId);
    }






    public void setSlotAsFolder(int slotIndex) {
        model.setSlotAsFolder(slotIndex);
    }

    public void setSlots(int slotIndex) {
        view.openSetItems(slotIndex, model.getCollectionId());
    }

    public void onChooseCollectionSize(int size) {
        model.setCurrentCollectionSize(size);
        view.restartService();
    }

    public void onSetStayOnScreen() {
        model.setStayOnScreen();
    }


    public void onSizeClick() {
        view.showChooseSizeDialog();
    }

    public void onCircleSize() {
        view.setCircleSizeDialog(setCircleSizeSJ, model.getCurrentCollection().radius);
    }

    public void onDeleteCollection() {
        final String currentCollectionId = model.getCollectionId();
        if (model.getCollectionList().size() < 2) {
            view.notifyCannotDelete(CANNOT_DELETE_REASON_THIS_IS_ONLY_ONE,null);
        } else {
            String placeUseThis = model.getPlaceUsingThis(currentCollectionId);
            if (placeUseThis != null) {
                Log.e(TAG, "onDeleteCollection: plase use this = " + placeUseThis);
                view.notifyCannotDelete(CANNOT_DELETE_REASON_BEING_USED, placeUseThis);
            } else {
                model.deleteCollection(currentCollectionId);
                changeCurrentSetSJ.onNext(null);
            }
        }
    }
    public void onSetCollectionLabel() {
        view.showSetLabelDialog(model.getCurrentCollection().label);
    }

    public void setCollectionLabel(String label) {
        model.setCollectionLabel(label);
    }


    public interface View extends PresenterView {
        PublishSubject<DragAndDropCallback.MoveData> onMoveItem();

        PublishSubject<DragAndDropCallback.DropData> onDropItem();

        PublishSubject<DragAndDropCallback.Coord> onDragItem();

        PublishSubject<String> onChooseCurrentSet();

        void highlightItem(int position);

        void notifyItemMove(int from, int to);

        void notifyItemRemove(int position);

        void notifyAdapter();

        void setDeleteButtonVisibility(boolean visible);

        void setDeleteButtonColor(boolean red);

        boolean isHoverOnDeleteButton(float x, float y);

        void updateRecyclerView(OrderedRealmCollection<Slot> slots);

        void openSetItems(int slotIndex, String collectionId);

        void openSetFolder(String folderId);

        void showChooseSizeDialog();

        void showSetLabelDialog(String currentLabel);

        void showChooseIconSourceDialog(Item item, Slot folder, int itemState);

        void setRecyclerView(OrderedRealmCollection<Slot> slots, RecyclerView.LayoutManager layoutManager, GridSpacingItemDecoration decoration);

        RecyclerView.LayoutManager getLayoutManager(int layoutType, int column);

        int dpToPixel(int dp);

        void updateCollectionInfo(Collection collection);

        void chooseCurrentCollection(List<Collection> collections, Collection currentCollection);

        void chooseActionOnSlot(final int slotIndex,boolean isItem, boolean isFolder, boolean folderAvailable);

        void setCircleSizeDialog(PublishSubject<Integer> subject, int currentValue);

        void restartService();

        void notifyCannotDelete(int reason,String id);

        void editItemLabelAndIcon(Item item);

        void editFolderLabelAndIcon(Slot folder);

        void openItemIconSetting(SetItemIconInfo info);

        void openFolderIconSetting(IconPackManager.IconPack iconPack, Slot folder);

        void resetFolderIcon(Slot folder, Realm realm);

        Context getActivity();

        boolean isFree();
        void proOnlyDialog();
    }

    public static class SetItemIconInfo {
        IconPackManager.IconPack iconPack;
        String itemId;
        int itemState;

        public SetItemIconInfo(IconPackManager.IconPack iconPack, String itemId, int itemState) {
            this.itemState = itemState;
            this.iconPack = iconPack;
            this.itemId = itemId;
        }
    }


}
