package org.de_studio.recentappswitcher.quickActionSetting;

import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingPresenter;

import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/10/16.
 */

public class QuickActionSettingPresenter extends BaseCollectionSettingPresenter<QuickActionSettingPresenter.View, QuickActionSettingModel> {

    public QuickActionSettingPresenter(QuickActionSettingModel model) {
        super(model);
    }

    @Override
    public void onViewAttach(View view) {
        super.onViewAttach(view);
        addSubscription(
                view.onLoadItemsOk().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                    }
                })
        );


        view.loadItems();

    }

    @Override
    public void setRecyclerView() {

    }

    public interface View extends BaseCollectionSettingPresenter.View {

        PublishSubject<Void> onLoadItemsOk();
        void loadItems();

    }

}
