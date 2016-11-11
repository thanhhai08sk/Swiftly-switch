package org.de_studio.recentappswitcher.main.general;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class GeneralPresenter {
    GeneralView view;
    GeneralModel model;

    public GeneralPresenter(GeneralView view, GeneralModel model) {
        this.view = view;
        this.model = model;
    }

    public void onViewAttach() {

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
