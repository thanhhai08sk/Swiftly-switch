package org.de_studio.recentappswitcher.main;

import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;

/**
 * Created by HaiNguyen on 11/5/16.
 */

public class MainPresenter extends BasePresenter<MainPresenter.View,MainModel> {


    public MainPresenter(MainModel model) {
        super(model);
    }

    @Override
    public void onViewAttach(View view) {
        super.onViewAttach(view);
        view.startIntroAndDataSetupIfNeeded();
        view.setupViewPager();
        view.restartServiceIfPossible();
    }

    public void resume() {
        view.displayPermissionNeeded(!view.checkIfAllPermissionOk());
    }



    public interface View extends PresenterView {
        void setupViewPager();

        void startIntroAndDataSetupIfNeeded();

        void restartServiceIfPossible();

        boolean checkIfAllPermissionOk();

        void displayPermissionNeeded(boolean show);

    }
}
