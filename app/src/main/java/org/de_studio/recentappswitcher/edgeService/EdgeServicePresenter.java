package org.de_studio.recentappswitcher.edgeService;

import android.util.Log;
import android.view.View;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;

import java.util.ArrayList;

/**
 * Created by HaiNguyen on 8/19/16.
 */
public class EdgeServicePresenter {
    private static final String TAG = EdgeServicePresenter.class.getSimpleName();
    EdgesServiceModel model;
    EdgeServiceView view;
    float xInit, yInit;
    int currentPosition;
    int currentEdgeMode;
    int currentShowing;
    int currentCircleIconHighlight = -1;
    int currentGridFavoriteIconHighlight = -1;
    int currentGridFolderIconHighlight = -1;

    boolean onOpeningFolder = false;
    Shortcut tempShortcut;
    String laucherPackageName;

    public EdgeServicePresenter(EdgesServiceModel model, EdgeServiceView view) {
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
        setCurrentPositionAndMode(edgeId);
        xInit = model.getXInit(currentPosition, x, view.getWindowSize().x);
        yInit = model.getYInit(currentPosition, y, view.getWindowSize().y);

        view.removeAllExceptEdgeView();
        view.showBackground();

        ArrayList<String> tempPackages = view.getRecentApps();

        switch (currentEdgeMode) {
            case Cons.MODE_ONLY_FAVORITE:
                view.showFavoriteGridView(xInit, yInit, currentPosition, -1);
                currentShowing = Cons.SHOWING_GRID;
                break;
            default:
                model.calculateCircleIconPositions(view.circleSizePxl, view.iconSizePxl, currentPosition, xInit, yInit, 6);
                view.setCircleIconsPosition(model.circleIconXs, model.circleIconYs);
                view.showCircleIconsView(model.getRecentList(tempPackages));
                currentShowing = Cons.SHOWING_RECENT_CIRCLE;

                break;
        }

        if (view.useActionDownVibrate) view.vibrate();
        if (view.useClock) view.showClock();


    }

    public void onActionMove(float x, float y, int edgeId) {
        switch (currentShowing) {
            case Cons.SHOWING_RECENT_CIRCLE:
                int iconToSwitch = model.getSemiCircleModeActivatedId(xInit, yInit, x, y, currentPosition);
                highlightCircleIconAndSwitchToGridIfNeed(iconToSwitch, edgeId);
                break;
            case Cons.SHOWING_FAVORITE_CIRCLE:
                int iconToSwitch1 = model.getSemiCircleModeActivatedId(xInit, yInit, x, y, currentPosition);
                highlightCircleIconAndSwitchToGridIfNeed(iconToSwitch1, edgeId);
                break;
            case Cons.SHOWING_GRID:
                int activatedGridIcon = model.getGridActivatedId(x, y, view.favoriteGridView.getX()
                        , view.favoriteGridView.getY(), view.gridRows, view.gridColumns, false);
                highlightFavoriteGridIcon(activatedGridIcon);
                break;
            case Cons.SHOWING_FOLDER:
                int onFolderIcon = model.getGridActivatedId(x, y, view.folderCoor[0], view.folderCoor[1],(int) view.folderCoor[2],(int) view.folderCoor[3], true);
                highlightFolderGridIcon(onFolderIcon);
        }

    }

    public void onActionUp(float x, float y, int edgeId, View v) {
        view.removeAllExceptEdgeView();
        switch (currentShowing) {
            case Cons.SHOWING_RECENT_CIRCLE:

                break;
            case Cons.SHOWING_FAVORITE_CIRCLE:
                break;
            case Cons.SHOWING_GRID:
                break;
            case Cons.SHOWING_FOLDER:
                break;
        }

    }

    private void setCurrentPositionAndMode(int edgeId) {
        switch (edgeId) {
            case Cons.EDGE_1_ID:
                Log.e(TAG, "onActionDown: edge1");
                currentPosition = view.edge1Position;
                currentEdgeMode = view.edge1Mode;
                break;
            case Cons.EDGE_2_ID:
                Log.e(TAG, "onActionDown:  edge2");
                currentPosition = view.edge2Position;
                currentEdgeMode = view.edge2Mode;
                break;
        }
    }

    public void onDestroy() {
        view.removeAll();
    }


    private void highlightCircleIconAndSwitchToGridIfNeed(int iconToSwitch, int edgeId) {

        if (iconToSwitch != currentCircleIconHighlight) {
            Log.e(TAG, "onActionMove: iconToSwitch = " + iconToSwitch);
            view.unhighlightCircleIcon(currentCircleIconHighlight, edgeId);
            view.highlightCircleIcon(iconToSwitch, edgeId, xInit, yInit);
            currentCircleIconHighlight = iconToSwitch;

            if (iconToSwitch >= 0 && iconToSwitch < model.savedRecentShortcut.length) {
                view.setIndicator(model.savedRecentShortcut[iconToSwitch], false, -1);
            } else if (iconToSwitch >= 10) {
                view.setIndicator(null, true, iconToSwitch - 10);
            } else {
                view.setIndicator(null, false, -1);
            }
            if (iconToSwitch >= 10
                    && view.edge1QuickActionViews[iconToSwitch - 10].getId() == Cons.QUICK_ACTION_ID_INSTANT_GRID) {
                view.removeCircleShortcutsView();
                view.showFavoriteGridView(xInit, yInit, currentPosition, -1);
                currentShowing = Cons.SHOWING_GRID;
                view.setIndicator(null, false, -1);
            }
        }
    }

    private void highlightFavoriteGridIcon(int activatedGridIcon) {
        if (activatedGridIcon != currentGridFavoriteIconHighlight) {
            tempShortcut = (Shortcut) view.gridFavoriteAdapter.getItem(activatedGridIcon);
            if (tempShortcut!=null && tempShortcut.getType() == Shortcut.TYPE_FOLDER) {
                view.startFolderCircleAnimation(activatedGridIcon);
                onOpeningFolder = true;
            } else if (onOpeningFolder) {
                view.folderAnimator.cancel();
                onOpeningFolder = false;
            }
            Log.e(TAG, "onActionMove: grid icon = " + activatedGridIcon);
            view.unhighlightGridFavoriteIcon(currentGridFavoriteIconHighlight);
            view.highlightGridFavoriteIcon(activatedGridIcon);
            currentGridFavoriteIconHighlight = activatedGridIcon;
            view.setIndicator(tempShortcut, false, -1);
        }
    }

    private void highlightFolderGridIcon(int iconId) {
        if (iconId == -2) {
            view.closeFolder();
            currentShowing = Cons.SHOWING_GRID;
            view.setIndicator(null, false, -1);
        }
        if (iconId != currentGridFolderIconHighlight) {
            tempShortcut = (Shortcut) view.folderAdapter.getItem(iconId);
            view.unhighlightGridFolderIcon(currentGridFolderIconHighlight);
            view.highlightGridFolderIcon(iconId);
            currentGridFolderIconHighlight = iconId;
            view.setIndicator(tempShortcut, false, -1);
        }
    }
}
