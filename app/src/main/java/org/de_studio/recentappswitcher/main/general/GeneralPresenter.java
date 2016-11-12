package org.de_studio.recentappswitcher.main.general;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class GeneralPresenter {
    GeneralView view;

    public GeneralPresenter(GeneralView view) {
        this.view = view;
    }

    public void onViewAttach() {

    }

    public void onViewDetach() {
        view.clear();
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
}
