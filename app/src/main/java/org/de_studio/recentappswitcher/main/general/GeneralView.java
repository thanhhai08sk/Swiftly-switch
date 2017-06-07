package org.de_studio.recentappswitcher.main.general;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
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
    public boolean shouldShowJournalItHeader() {
        boolean firstStart = shared.getBoolean(Cons.FIRST_START_KEY, true);
        long dateStart = shared.getLong(Cons.DATE_START_KEY, 0);
        boolean saw = shared.getBoolean(Cons.SAW_JOURNAL_IT_KEY, false);
//        return true;
        return !firstStart && (System.currentTimeMillis() - dateStart > Cons.WAIT_FOR_SHOWING_JOURNAL_IT_TIME) && !saw;
    }

    @Override
    public void addJournalItHeader() {
        Log.e(TAG, "addJournalItHeader: show journal it header");
        final LinearLayout header = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.header_checkout_journal_it, parentView, false);
        TransitionManager.beginDelayedTransition(parentView);
        parentView.addView(header, 0);
        Button close = (Button) header.findViewById(R.id.dismiss);
        Button checkout = (Button) header.findViewById(R.id.checkout);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shared.edit().putBoolean(Cons.SAW_JOURNAL_IT_KEY, true).apply();
                TransitionManager.beginDelayedTransition(parentView);
                header.setVisibility(View.GONE);
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shared.edit().putBoolean(Cons.SAW_JOURNAL_IT_KEY, true).apply();
                Utility.openJournalItPlayStorePage(getActivity());
                TransitionManager.beginDelayedTransition(parentView);
                header.setVisibility(View.GONE);
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
