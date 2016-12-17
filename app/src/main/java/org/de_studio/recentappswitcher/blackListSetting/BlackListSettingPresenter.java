package org.de_studio.recentappswitcher.blackListSetting;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingPresenter;

import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class BlackListSettingPresenter extends BaseCollectionSettingPresenter<BlackListSettingPresenter.View, BlackListSettingModel> {
    public BlackListSettingPresenter(BlackListSettingModel model) {
        super(model);
    }


    @Override
    public void onViewAttach(final View view) {
        super.onViewAttach(view);
        addSubscription(
                view.onAddApps().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        view.addApps();
                    }
                })
        );
    }

    @Override
    public void setRecyclerView() {
        view.setRecyclerView(model.getSlots(), view.getLayoutManager(Cons.LAYOUT_TYPE_LINEAR, -1),null);
    }

    public interface View extends BaseCollectionSettingPresenter.View {
        PublishSubject<Void> onAddApps();

        void addApps();
    }
}
