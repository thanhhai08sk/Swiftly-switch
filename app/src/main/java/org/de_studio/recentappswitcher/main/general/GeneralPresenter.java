package org.de_studio.recentappswitcher.main.general;

import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;

import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class GeneralPresenter extends BasePresenter<GeneralPresenter.View,BaseModel> {


    public GeneralPresenter(BaseModel model) {
        super(model);
    }

    @Override
    public void onViewAttach(final View view) {
        super.onViewAttach(view);
        addSubscription(
                view.viewCreatedEvent().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (view.shouldShowJournalItHeader()) {
                            view.addJournalItHeader();
                        }
                    }
                })
        );
    }

    public void onRecentClick() {
        view.setRecent();
    }

    public void onQuickActionClick() {
        view.setQuickAction();
    }

    public void onGridFavoriteClick() {
        view.setGridFavorite();
    }

    public void onCircleFavoriteClick() {
        view.setCircleFavorite();
    }

    public void onBlackListClick() {
        view.setBlackList();
    }

    public interface View extends PresenterView {

        PublishSubject<Void> viewCreatedEvent();

        void setRecent();

        void setQuickAction();

        void setGridFavorite();

        void setCircleFavorite();

        void setBlackList();

        boolean shouldShowJournalItHeader();

        void addJournalItHeader();
    }
}
