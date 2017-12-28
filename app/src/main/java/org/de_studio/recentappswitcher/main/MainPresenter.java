package org.de_studio.recentappswitcher.main;

import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;

import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/5/16.
 */

public class MainPresenter extends BasePresenter<MainPresenter.View,MainModel> {


    public MainPresenter(MainModel model) {
        super(model);
    }

    @Override
    public void onViewAttach(final View view) {
        super.onViewAttach(view);
        addSubscription(
                view.onDataSetupOk().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        view.showInitializingDialog(false);
                    }
                })
        );
        view.setupViewPager();
        if (!model.checkIfDataSetupOk()) {
            view.registerForDataSetupOk();
            view.showInitializingDialog(true);
        }
        if (view.isFirstStart()) {
            view.clearFirstStartAndStartIntroScreen();
        } else {
            view.restartServiceIfPossible();
            view.showWhatNewIfNeeded();
        }
    }

    public void resume() {
        view.displayPermissionNeeded(!view.checkIf2FirstPermissionsOk());
    }



    public interface View extends PresenterView {
        PublishSubject<Void> onDataSetupOk();

        void setupViewPager();

        boolean isFirstStart();

        void clearFirstStartAndStartIntroScreen();

        void restartServiceIfPossible();

        boolean checkIf2FirstPermissionsOk();

        void displayPermissionNeeded(boolean show);

        void showWhatNewIfNeeded();

        void showWhatNew();

        void showInitializingDialog(boolean visible);

        void registerForDataSetupOk();


    }
}
