package org.de_studio.recentappswitcher.edgeService;

import android.graphics.Point;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Edge;
import org.de_studio.recentappswitcher.model.Slot;

import java.util.ArrayList;

import io.realm.RealmList;

/**
 * Created by HaiNguyen on 12/23/16.
 */

public class NewServicePresenter extends BasePresenter<NewServicePresenter.View, NewServiceModel> {
    Edge edge1;
    Edge edge2;
    float xInit, yInit;
    Edge currentEdge;
    boolean onHolding;
    Showing currentShowing = new Showing();


    public NewServicePresenter(NewServiceModel model, Edge edge1, Edge edge2) {
        super(model);
        this.edge1 = edge1;
        this.edge2 = edge2;
    }

    @Override
    public void onViewAttach(View view) {
        super.onViewAttach(view);
        model.setup();
        view.addEdgesToWindowAndSetListener();
        view.setupNotification();
        view.setupReceiver();
    }


    public void onActionDown(float x, float y, int edgeId) {
        setCurrentEdge(edgeId);
        setTriggerPoint(x, y);

        ArrayList<String> tempPackages = view.getRecentApp();
        view.showBackground();
        switch (currentEdge.mode) {
            case Edge.MODE_GRID:
                view.showGrid(xInit, yInit, currentEdge.grid);
                currentShowing.showWhat = Showing.SHOWING_GRID;
                currentShowing.grid = currentEdge.grid;
                break;
            case Edge.MODE_RECENT_AND_QUICK_ACTION:
                view.showCircle(model.calculateCircleIconPositions(currentEdge.recent.radius, currentEdge.position
                        , xInit, yInit, currentEdge.recent.slots.size()), currentEdge.recent
                        , model.getRecent(tempPackages, currentEdge.recent.slots));
                currentShowing.showWhat = Showing.SHOWING_CIRCLE_AND_ACTION;
                currentShowing.circle = currentEdge.recent;
                break;
            case Edge.MODE_CIRCLE_FAV_AND_QUICK_ACTION:
                view.showCircle(model.calculateCircleIconPositions(currentEdge.circleFav.radius, currentEdge.position
                        , xInit, yInit, currentEdge.circleFav.slots.size()), currentEdge.circleFav, currentEdge.circleFav.slots);
                currentShowing.showWhat = Showing.SHOWING_CIRCLE_AND_ACTION;
                currentShowing.circle = currentEdge.circleFav;
                break;

        }
        view.actionDownVibrate();
        view.showClock();
    }

    private void setTriggerPoint(float x, float y) {
        if (currentEdge.mode== Edge.MODE_RECENT_AND_QUICK_ACTION) {
            xInit = model.getXInit(currentEdge.position, x, view.getWindowSize().x, currentEdge.recent.radius);
            yInit = model.getYInit(currentEdge.position, y, view.getWindowSize().y, currentEdge.recent.radius);
        } else if (currentEdge.mode == Edge.MODE_CIRCLE_FAV_AND_QUICK_ACTION) {
            xInit = model.getXInit(currentEdge.position, x, view.getWindowSize().x, currentEdge.circleFav.radius);
            yInit = model.getYInit(currentEdge.position, y, view.getWindowSize().y, currentEdge.circleFav.radius);
        } else {
            xInit = x;
            yInit = y;
        }
    }

    private void setCurrentEdge(int edgeId) {
        switch (edgeId) {
            case Cons.EDGE_1_ID:
                currentEdge = edge1;
                break;
            case Cons.EDGE_2_ID:
                currentEdge = edge2;
                break;
        }
    }


    public interface View extends PresenterView, android.view.View.OnTouchListener {
        void addEdgesToWindowAndSetListener();

        void setupNotification();

        void setupReceiver();

        Point getWindowSize();

        ArrayList<String> getRecentApp();

        void showBackground();

        void showGrid(float xInit, float yInit, Collection grid);

        void showCircle(NewServiceModel.IconsXY iconsXY, Collection circle, RealmList<Slot> slots);

        void actionDownVibrate();

        void showClock();

    }

    public class Showing {
        public static final int SHOWING_GRID = 0;
        public static final int SHOWING_CIRCLE_AND_ACTION = 1;
        public int showWhat;
        public Collection grid;
        public Collection circle;
        public Collection action;
        public Showing() {
        }
    }

}
