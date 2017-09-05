package org.de_studio.recentappswitcher.main.general;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseActivity;
import org.de_studio.recentappswitcher.base.BaseFragment;
import org.de_studio.recentappswitcher.blackListSetting.BlackListSettingView;
import org.de_studio.recentappswitcher.circleFavoriteSetting.CircleFavoriteSettingView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerGeneralComponent;
import org.de_studio.recentappswitcher.dagger.GeneralModule;
import org.de_studio.recentappswitcher.gridFavoriteSetting.GridFavoriteSettingView;
import org.de_studio.recentappswitcher.quickActionSetting.QuickActionSettingView;
import org.de_studio.recentappswitcher.recentSetting.RecentSettingView;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class GeneralView extends BaseFragment<GeneralPresenter> implements GeneralPresenter.View{
    private static final String TAG = GeneralView.class.getSimpleName();

    PublishSubject<Void> viewCreatedSJ = PublishSubject.create();

    @BindView(R.id.parent)
    ViewGroup parentView;

    @Inject
    @Named(Cons.SHARED_PREFERENCE_NAME)
    SharedPreferences shared;


    @Override
    protected int getLayoutRes() {
        return R.layout.general_view;
    }

    @Override
    protected void inject() {
        DaggerGeneralComponent.builder()
                .appModule(new AppModule(getActivity().getApplicationContext()))
                .generalModule(new GeneralModule(this))
                .build().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewCreatedSJ.onNext(null);
    }


    @Override
    public PublishSubject<Void> viewCreatedEvent() {
        return viewCreatedSJ;
    }


    @Override
    public void askForPlayStoreReview() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.thank_you)
                .content(R.string.play_store_review_request)
                .positiveText(R.string.review_on_play_store)
                .neutralText(R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Utility.openPlayStorePage(getActivity(),getActivity().getPackageName());
                        closeReviewRequest(true);
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        closeReviewRequest(false);
                    }
                })
                .show();
    }

    @Override
    public void askForFeedback() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.send_feedback)
                .content(R.string.quick_feedback_review_request_text)
                .positiveText(R.string.send_email)
                .negativeText(R.string.no_thanks)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        closeReviewRequest(true);
                        Utility.sendFeedback(getActivity(), true);
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                closeReviewRequest(false);
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void closeReviewRequest(boolean neverAskAgain) {
        View request = parentView.findViewById(R.id.review_request);
        if (request != null) {
            TransitionManager.beginDelayedTransition(parentView);
            request.setVisibility(View.GONE);
        }
        SharedPreferences.Editor editor = shared.edit().putLong(Cons.LAST_REVIEW_REQUEST, System.currentTimeMillis());
        if (neverAskAgain) {
            editor.putBoolean(Cons.DONE_WITH_REVIEW_REQUEST, true);
        }
        editor.apply();

    }

    @Override
    public Single<Boolean> shouldShowReviewRequest() {
        return Single.create(new Single.OnSubscribe<Boolean>() {
            @Override
            public void call(SingleSubscriber<? super Boolean> singleSubscriber) {
                boolean doneWithReviewRequest = shared.getBoolean(Cons.DONE_WITH_REVIEW_REQUEST, false);
                long lastReviewRequest = shared.getLong(Cons.LAST_REVIEW_REQUEST, 0);
                singleSubscriber.onSuccess(!doneWithReviewRequest
                        && (System.currentTimeMillis() - lastReviewRequest > Cons.REVIEW_REQUEST_INTEVAL_TIME));
            }
        });
    }

    public boolean shouldDisplaySale() {
        return Utility.isFree(getActivity()) && System.currentTimeMillis() < 1505176586000L;
    }

    public void showSaleCard() {
        final LinearLayout card = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.header_sale_card, parentView, false);
        TransitionManager.beginDelayedTransition(parentView);
        parentView.addView(card, 0);
        Button close = (Button) card.findViewById(R.id.close);
        Button upgrade = (Button) card.findViewById(R.id.upgrade);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(parentView);
                card.setVisibility(View.GONE);
            }
        });

        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) getActivity()).buyPro();
                TransitionManager.beginDelayedTransition(parentView);
                card.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public Observable<GeneralPresenter.Result> showReviewRequestCard() {
        return Observable.create(new Observable.OnSubscribe<GeneralPresenter.Result>() {
            @Override
            public void call(final Subscriber<? super GeneralPresenter.Result> subscriber) {
                Log.e(TAG, "showReviewRequestCard: ");
                LinearLayout cardView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.header_review_request, parentView, false);
                parentView.addView(cardView, 0);
                final ImageView star1 = ((ImageView) cardView.findViewById(R.id.star_1));
                ImageView star2 = ((ImageView) cardView.findViewById(R.id.star_2));
                ImageView star3 = ((ImageView) cardView.findViewById(R.id.star_3));
                ImageView star4 = ((ImageView) cardView.findViewById(R.id.star_4));
                ImageView star5 = ((ImageView) cardView.findViewById(R.id.star_5));
                ImageButton close = (ImageButton) cardView.findViewById(R.id.close);
                final Drawable fullStar = ContextCompat.getDrawable(getActivity(), R.drawable.star_full);
                final ViewGroup stars = (ViewGroup) cardView.findViewById(R.id.stars);
                star1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        star1.setImageDrawable(fullStar);
                        subscriber.onNext(GeneralPresenter.Result.REVIEW_REQUEST_LESS_THAN_5_STARS);
                    }
                });
                star2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < 2; i++) {
                            ((ImageView) stars.getChildAt(i)).setImageDrawable(fullStar);
                        }
                        subscriber.onNext(GeneralPresenter.Result.REVIEW_REQUEST_LESS_THAN_5_STARS);

                    }
                });
                star3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < 3; i++) {
                            ((ImageView) stars.getChildAt(i)).setImageDrawable(fullStar);
                        }
                        subscriber.onNext(GeneralPresenter.Result.REVIEW_REQUEST_LESS_THAN_5_STARS);

                    }
                });
                star4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < 4; i++) {
                            ((ImageView) stars.getChildAt(i)).setImageDrawable(fullStar);
                        }
                        subscriber.onNext(GeneralPresenter.Result.REVIEW_REQUEST_LESS_THAN_5_STARS);

                    }
                });
                star5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < 5; i++) {
                            ((ImageView) stars.getChildAt(i)).setImageDrawable(fullStar);
                        }
                        subscriber.onNext(GeneralPresenter.Result.REVIEW_REQUEST_5_STARS);

                    }
                });
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subscriber.onNext(GeneralPresenter.Result.REVIEW_REQUEST_CLOSE);
                    }
                });

            }
        });
    }


    public void setRecent() {
        startActivity(RecentSettingView.getIntent(getActivity(), null));
    }

    public void setQuickAction() {
        startActivity(QuickActionSettingView.getIntent(getActivity(), null));
    }

    public void setGridFavorite() {
        startActivity(GridFavoriteSettingView.getIntent(getActivity(), null));
    }

    public void setCircleFavorite() {
        startActivity(CircleFavoriteSettingView.getIntent(getActivity(), null));
    }

    public void setBlackList() {
        BlackListSettingView view = BlackListSettingView.newInstance();
        view.show(getFragmentManager(), "blackList");
    }

    @OnClick(R.id.recent)
    void onRecentClick(){
        presenter.onRecentClick();
    }

    @OnClick(R.id.quick_actions)
    void onQuickActionClick(){
        presenter.onQuickActionClick();
    }

    @OnClick(R.id.grid_favorite)
    void onGridFavoriteClick(){
        presenter.onGridFavoriteClick();
    }

    @OnClick(R.id.circle_favorite)
    void onCircleFavoriteClick(){
        presenter.onCircleFavoriteClick();
    }

    @OnClick(R.id.black_list)
    void onBlackListClick(){
        presenter.onBlackListClick();
    }

}
