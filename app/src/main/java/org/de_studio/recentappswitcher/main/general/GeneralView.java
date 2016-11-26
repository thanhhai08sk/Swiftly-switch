package org.de_studio.recentappswitcher.main.general;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.circleFavoriteSetting.CircleFavoriteSettingView;
import org.de_studio.recentappswitcher.gridFavoriteSetting.GridFavoriteSettingView;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class GeneralView extends Fragment {
    private static final String TAG = GeneralView.class.getSimpleName();

    GeneralPresenter presenter;

    Unbinder unbinder;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new GeneralPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.general_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        presenter.onViewAttach();
        return view;
    }

    @Override
    public void onDestroy() {
        presenter.onViewDetach();
        super.onDestroy();
    }

    public void setRecent() {

    }

    public void setQuickAction() {

    }

    public void setGridFavorite() {
        startActivity(GridFavoriteSettingView.getIntent(getActivity(), null));
    }

    public void setCircleFavorite() {
        startActivity(CircleFavoriteSettingView.getIntent(getActivity(), null));
    }

    public void setBlackList() {

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

    public void clear() {
        unbinder.unbind();
    }
}
