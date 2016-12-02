package org.de_studio.recentappswitcher.folderSetting;

import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;

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
    public void onViewAttach(View view) {
        super.onViewAttach(view);

        addSubscription(
                view.onAddItem().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                    }
                })
        );
    }

    public interface View extends PresenterView {
        PublishSubject<Void> onAddItem();
        void openSetItems

    }
}
