package org.de_studio.recentappswitcher.main.general;

import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;

import rx.Observable;
import rx.Single;
import rx.functions.Action1;
import rx.functions.Func1;
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
                view.viewCreatedEvent().flatMap(new Func1<Void, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Void aVoid) {
                        return view.shouldShowReviewRequest().toObservable();
                    }
                }).filter(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        return aBoolean;
                    }
                }).flatMap(new Func1<Object, Observable<Result>>() {
                    @Override
                    public Observable<Result> call(Object o) {
                        return view.showReviewRequestCard();
                    }
                }).subscribe(new Action1<Result>() {
                    @Override
                    public void call(Result result) {
                        switch (result) {
                            case REVIEW_REQUEST_LESS_THAN_5_STARS:
                                view.askForFeedback();
                                break;
                            case REVIEW_REQUEST_5_STARS:
                                view.askForPlayStoreReview();
                                break;
                            case REVIEW_REQUEST_CLOSE:
                                view.closeReviewRequest(false);
                                break;
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


        Single<Boolean> shouldShowReviewRequest();

        Observable<Result> showReviewRequestCard();

        void askForPlayStoreReview();

        void askForFeedback();

        void closeReviewRequest(boolean neverAskAgain);
    }

    enum Result {
        REVIEW_REQUEST_LESS_THAN_5_STARS,
        REVIEW_REQUEST_5_STARS,
        REVIEW_REQUEST_CLOSE,

    }

}
