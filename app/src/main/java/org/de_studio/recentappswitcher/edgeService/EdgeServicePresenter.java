package org.de_studio.recentappswitcher.edgeService;

import org.de_studio.recentappswitcher.Cons;

/**
 * Created by HaiNguyen on 8/19/16.
 */
public class EdgeServicePresenter {
    EdgeServiceModel model;
    EdgeServiceView view;
    int edge1Position, edge2Position;
    int xInit, yInit;
    String laucherPackageName;

    public EdgeServicePresenter(EdgeServiceModel model, EdgeServiceView view) {
        this.model = model;
        this.view = view;
    }

    void onCreate() {
        view.setOnTouchListener(view.isEdge1On, view.isEdge2On);
    }

    public void onActionDown(int x, int y, int edgeId) {
        int position = 0;
        switch (edgeId) {
            case Cons.EDGE_1_ID:
                position = edge1Position;
                break;
            case Cons.EDGE_2_ID:
                position = edge2Position;
                break;
        }
        xInit = model.getXInit(position, x, view.getWindowSize().x);
        yInit = model.getYInit(position, y, view.getWindowSize().y);

        view.removeAllExceptEdgeView();





    }
}
