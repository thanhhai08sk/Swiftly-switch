package org.de_studio.recentappswitcher.main.edgeSetting;

import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Edge;

import io.realm.RealmResults;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/5/16.
 */

public class EdgeSettingPresenter extends BasePresenter<EdgeSettingPresenter.View, EdgeSettingModel>{
    private static final String TAG = EdgeSettingPresenter.class.getSimpleName();
    PublishSubject<Integer> setModeSubject = PublishSubject.create();
    PublishSubject<String> setRecentSetSubject = PublishSubject.create();
    PublishSubject<String> setCircleSetSubject = PublishSubject.create();
    PublishSubject<String> setQuickActionsSetSubject = PublishSubject.create();
    PublishSubject<String> setGridSetSubject = PublishSubject.create();
    PublishSubject<Integer> setGuideColorSubject = PublishSubject.create();

    public EdgeSettingPresenter(EdgeSettingModel model) {
        super(model);
    }

    @Override
    public void onViewAttach(final View view) {
        super.onViewAttach(view);

        addSubscription(
                model.onGotEdge().subscribe(new Action1<Edge>() {
                    @Override
                    public void call(Edge edge) {
                        if (edge != null) {
                            showEdge(edge);
                        } else {
                            view.registerSetDataCompleteEven();
                        }
                    }
                })
        );

        addSubscription(
                view.onSetDataComplete().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showEdge(model.getEdge());
                    }
                })
        );

        addSubscription(
                setModeSubject.subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        model.setMode(integer);
                    }
                })
        );

        addSubscription(
                setRecentSetSubject.subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        model.setRecentSet(s);
                    }
                })
        );

        addSubscription(
                setCircleSetSubject.subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        model.setCircleFavoriteSet(s);
                    }
                })
        );

        addSubscription(
                setQuickActionsSetSubject.subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        model.setQuickActionsSet(s);
                    }
                })
        );

        addSubscription(
                setGridSetSubject.subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        model.setGridFavoriteSet(s);
                    }
                })
        );

        addSubscription(
                setGuideColorSubject.subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        model.setGuideColor(integer);
                        view.restartService();
                    }
                })
        );




        model.setup();
    }

    public void onSetMode() {
        view.chooseMode(model.getEdge().mode, setModeSubject);
    }

    public void onSetRecent() {
        view.chooseCollection(model.getRecentSetsList(), model.getEdge().recent, setRecentSetSubject);
    }

    public void onSetQuickActions() {
        view.chooseCollection(model.getQuickActionsSetsList(), model.getEdge().quickAction, setQuickActionsSetSubject);
    }

    public void onSetCircle() {
        view.chooseCollection(model.getCircleFavoriteSetsList(), model.getEdge().circleFav, setCircleSetSubject);
    }

    public void onSetGrid() {
        view.chooseCollection(model.getGridFavoriteSetsList(), model.getEdge().grid, setGridSetSubject);
    }

    public void onSetPosition() {
        view.showPositionSetting(model.getEdge());
    }

    public void onEnable() {
        model.setEnable(!model.isEdgeEnabled());
        view.setEnable(model.isEdgeEnabled());
        view.restartService();
    }

    public void onSetShowGuide() {
        model.setShowGuide(!model.getEdge().useGuide);
        view.setShowGuideEnable(model.getEdge().useGuide);
        view.restartService();
    }

    public void onSetGuideColor() {
        view.chooseGuideColor(model.getGuideColor(), setGuideColorSubject);
    }

    private void showEdge(Edge edge) {
        view.setEnable(model.isEdgeEnabled());
        view.setShowGuideEnable(edge.useGuide);
        view.setCurrentMode(edge.mode);
        if (edge.recent != null) {
            view.setCurrentRecent(edge.recent.label);
        }
        if (edge.quickAction != null) {
            view.setCurrentQuickActions(edge.quickAction.label);
        }
        if (edge.circleFav != null) {
            view.setCurrentCircle(edge.circleFav.label);
        }
        if (edge.grid != null) {
            view.setCurrentGrid(edge.grid.label);
        }
    }

    public interface View extends PresenterView {

        PublishSubject<Void> onSetDataComplete();

        void chooseMode(int currentMode, PublishSubject<Integer> setModeSubject);

        void chooseCollection(RealmResults<Collection> recents, Collection currentRecent, PublishSubject<String> setRecentSetSj);


        void chooseGuideColor(int currentColor, PublishSubject<Integer> setGuideColorSj);

        void showPositionSetting(Edge edge);

        void setEnable(boolean enable);

        void setCurrentMode(int mode);

        void setCurrentRecent(String label);

        void setCurrentQuickActions(String label);

        void setCurrentCircle(String label);

        void setCurrentGrid(String label);

        void setShowGuideEnable(boolean enable);

        void registerSetDataCompleteEven();

        void restartService();
    }

}
