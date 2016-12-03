package org.de_studio.recentappswitcher.folderSetting;

import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.OrderedRealmCollection;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/2/16.
 */

public class FolderSettingPresenter extends BasePresenter<FolderSettingPresenter.View, FolderSettingModel> {
    public FolderSettingPresenter(FolderSettingModel model) {
        this.model = model;
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


    }

    public interface View extends PresenterView {
        PublishSubject<Void> onAddItemToFolder();

        PublishSubject<Void> onAddApps();

        PublishSubject<Void> onAddActions();

        PublishSubject<Void> onAddContacts();

        PublishSubject<Void> onAddShortcuts();


        void chooseTypeOfItemsToAdd();

        void addApps();

        void addActions();

        void addContacts();

        void addShortcuts();


        void setAdapter(OrderedRealmCollection<Item> folderItems);

    }
}
