package org.de_studio.recentappswitcher.main.general;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.setCircleFavorite.SetCircleFavoriteView;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class GeneralView extends Fragment {

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

    }

    public void setCircleFavorite() {
        startActivity(SetCircleFavoriteView.getIntent(getActivity(), null));
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
