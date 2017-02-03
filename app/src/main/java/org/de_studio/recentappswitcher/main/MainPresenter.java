package org.de_studio.recentappswitcher.main;

/**
 * Created by HaiNguyen on 11/5/16.
 */

public class MainPresenter {
    MainView view;
    MainModel model;

    public MainPresenter(MainView view, MainModel model) {
        this.view = view;
        this.model = model;
    }

    public void onViewAttach() {
        view.setupViewPager();
    }


    public void onDestroy() {
        view.clear();
        model.clear();
    }
}
