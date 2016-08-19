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
    boolean isEdge1On, isEdge2On;

    public EdgeServicePresenter(EdgeServiceModel model, EdgeServiceView view) {
        this.model = model;
        this.view = view;
    }

    public void onViewAttach() {
        edge1Position = view.getEdge1Position();
        edge2Position = view.getEdge2Position();
        view.createRecentIconsList();
        isEdge1On = view.isEdge1On();
        isEdge2On = view.isEdge2On();


    }

    public void onActionDown(int x, int y, String edgeTag) {
        int position = 0;
        switch (edgeTag) {
            case Cons.TAG_EDGE_1:
                position = edge1Position;
                break;
            case Cons.TAG_EDGE_2:
                position = edge2Position;
                break;
        }
        xInit = model.getXInit(position, x, view.getWindowSize().x);
        yInit = model.getYInit(position, y, view.getWindowSize().y);

    }
}
