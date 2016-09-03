package org.de_studio.recentappswitcher.edgeService;

import android.util.Log;

import org.de_studio.recentappswitcher.Cons;

/**
 * Created by HaiNguyen on 8/19/16.
 */
public class EdgeServicePresenter {
    private static final String TAG = EdgeServicePresenter.class.getSimpleName();
    EdgeServiceModel model;
    EdgeServiceView view;
    float xInit, yInit;
    String laucherPackageName;

    public EdgeServicePresenter(EdgeServiceModel model, EdgeServiceView view) {
        this.model = model;
        this.view = view;
    }

    void onCreate() {
        if (view.isEdge1On) {
            view.addEdgeToWindowManager(Cons.EDGE_1_ID);
        }
        if (view.isEdge2On) {
            view.addEdgeToWindowManager(Cons.EDGE_2_ID);
        }
        view.setOnTouchListener(view.isEdge1On, view.isEdge2On);
    }

    public void onActionDown(float x, float y, int edgeId) {
        int position = 0;
        switch (edgeId) {
            case Cons.EDGE_1_ID:
                Log.e(TAG, "onActionDown: edge1");
                position = view.edge1Position;
                break;
            case Cons.EDGE_2_ID:
                Log.e(TAG, "onActionDown: edge2");
                position = view.edge2Position;
                break;
        }
        xInit = model.getXInit(position, x, view.getWindowSize().x);
        yInit = model.getYInit(position, y, view.getWindowSize().y);

        view.removeAllExceptEdgeView();
        model.calculateCircleIconPositions(view.circleSizePxl, view.iconSizePxl, position, xInit, yInit, 6);
        view.setCircleIconsPosition(model.circleIconXs, model.circleIconYs);
        view.setCircleIconsView(model.getRecentList(view.getRecentApps()));




    }

    public void onActionUp(float x, float y, int edgeId) {
        int position = 0;
        switch (edgeId) {
            case Cons.EDGE_1_ID:
                position = view.edge1Position;
                break;
            case Cons.EDGE_2_ID:
                position = view.edge2Position;
                break;
        }
        view.removeAllExceptEdgeView();

    }
}
