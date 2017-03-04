package org.de_studio.recentappswitcher.folderSetting;

import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.DragAndDropCallback;
import org.de_studio.recentappswitcher.base.PresenterView;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/2/16.
 */

public class FolderSettingPresenter extends BasePresenter<FolderSettingPresenter.View, FolderSettingModel> {
    private static final String TAG = FolderSettingPresenter.class.getSimpleName();

    public FolderSettingPresenter(FolderSettingModel model) {
        super(model);
    }

    @Override
    public void onViewAttach(final View view) {
        super.onViewAttach(view);
        model.setup();
        view.setAdapter(model.getFolderItems());
        addSubscription(
                view.onAddItemToFolder().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        view.chooseTypeOfItemsToAdd();
                    }
                })
        );

        addSubscription(
                view.onAddApps().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        view.addApps();
                    }
                })
        );

        addSubscription(
                view.onAddActions().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        view.addActions();
                    }
                })
        );

        addSubscription(
                view.onAddContacts().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        view.addContacts();
                    }
                })
        );

        addSubscription(
                view.onAddShortcuts().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        view.addShortcuts();
                    }
                })
        );

        addSubscription(
                view.onMoveItem().subscribe(new Action1<DragAndDropCallback.MoveData>() {
                    @Override
                    public void call(DragAndDropCallback.MoveData moveData) {
                        if (moveData != null) {
                            model.moveItem(moveData.from, moveData.to);
                            view.notifyItemMove(moveData.from,moveData.to);
                        }

                    }
                })
        );

        addSubscription(
                view.onDropItem().subscribe(new Action1<DragAndDropCallback.DropData>() {
                    @Override
                    public void call(DragAndDropCallback.DropData dropData) {
                        if (dropData.dropY > view.getDeleteButtonY()) {
                            model.removeItem(dropData.position);
                            view.notifyItemRemove(dropData.position);
                        }
                        view.setDeleteButtonVisibility(false);
                        view.updateFolderThumbnail(model.getRealm(), model.getFolder());

                    }
                })
        );

        addSubscription(
                view.onCurrentlyDrag().subscribe(new Action1<DragAndDropCallback.Coord>() {
                    @Override
                    public void call(DragAndDropCallback.Coord coord) {
                        view.setDeleteButtonVisibility(true);
                        if (coord.y > view.getDeleteButtonY()) {
                            view.setDeleteButtonColor(true);
                        } else {
                            view.setDeleteButtonColor(false);
                        }
                    }
                })
        );

        addSubscription(
                view.onDialogClosed().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        view.updateFolderThumbnail(model.getRealm(), model.getFolder());
                    }
                })
        );






    }

    public interface View extends PresenterView {
        PublishSubject<Void> onAddItemToFolder();

        PublishSubject<Void> onAddApps();

        PublishSubject<Void> onAddActions();

        PublishSubject<Void> onAddContacts();

        PublishSubject<Void> onAddShortcuts();

        PublishSubject<Void> onDialogClosed();

        PublishSubject<DragAndDropCallback.MoveData> onMoveItem();

        PublishSubject<DragAndDropCallback.DropData> onDropItem();

        PublishSubject<DragAndDropCallback.Coord> onCurrentlyDrag();


        void chooseTypeOfItemsToAdd();

        void addApps();

        void addActions();

        void addContacts();

        void addShortcuts();


        void setAdapter(OrderedRealmCollection<Item> folderItems);

        void notifyItemMove(int from, int to);

        void notifyItemRemove(int position);

        void setDeleteButtonVisibility(boolean visible);

        void setDeleteButtonColor(boolean redColor);

        float getDeleteButtonY();

        void updateFolderThumbnail(Realm realm, Slot folder);

    }
}
