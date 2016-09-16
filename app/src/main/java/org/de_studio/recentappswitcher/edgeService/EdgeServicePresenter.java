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
    int currentEdgeId;
    int currentIconToSwitch;
    int currentCircleIconHighlight = -1;
    int currentGridFavoriteIconHighlight = -1;
    int currentGridFolderIconHighlight = -1;
    long startHoldingTime;
    boolean onHolding = false;

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

    void onStartCommand() {
        view.setupNotification();
        view.setupReceiver();

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
                view.setCircleIconsPosition(model.circleIconXs, model.circleIconYs, xInit,yInit);
                view.showCircleIconsView(model.getRecentList(tempPackages));
                currentShowing = Cons.SHOWING_RECENT_CIRCLE;

                break;
        }

        if (view.useActionDownVibrate) view.vibrate();
        view.showClock();


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
                if (currentCircleIconHighlight >= 10) {
                    view.executeQuickAction(currentCircleIconHighlight - 10, v);
                } else if (currentCircleIconHighlight >= 0 && currentCircleIconHighlight < model.savedRecentShortcut.length) {
                    view.executeShortcut(model.savedRecentShortcut[currentCircleIconHighlight], v,0);
                }
                view.unhighlightCircleIcon(currentCircleIconHighlight, edgeId, model.circleIconXs, model.circleIconYs);
                currentCircleIconHighlight = -1;
                break;
            case Cons.SHOWING_FAVORITE_CIRCLE:
                if (currentCircleIconHighlight >= 10) {
                    view.executeQuickAction(currentCircleIconHighlight - 10, v);
                }else if (currentCircleIconHighlight != -1) {
                    view.executeShortcut(view.getCircleFavoriteShorcut(currentCircleIconHighlight),v,Cons.MODE_CIRCLE_FAVORITE);
                }
                view.unhighlightCircleIcon(currentCircleIconHighlight, edgeId, model.circleIconXs, model.circleIconYs);
                currentCircleIconHighlight = -1;
                break;
            case Cons.SHOWING_GRID:
                if (currentGridFavoriteIconHighlight != -1) {
                    view.executeShortcut((Shortcut) view.gridFavoriteAdapter.getItem(currentGridFavoriteIconHighlight), v, Cons.MODE_DEFAULT);
                }
                break;
            case Cons.SHOWING_FOLDER:
                view.executeShortcut((Shortcut) view.folderAdapter.getItem(currentGridFolderIconHighlight), v, -1);
                break;
        }

    }

    void onActionOutSide() {
        view.removeAllExceptEdgeView();
    }

    void onActionCancel() {
        view.removeAllExceptEdgeView();
    }

    private void setCurrentPositionAndMode(int edgeId) {
        currentEdgeId = edgeId;
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

    public void onSwitch(int iconToSwitch) {
        if (currentEdgeMode == Cons.MODE_CIRCLE_FAVORITE) {
            view.unhighlightCircleIcon(currentCircleIconHighlight, currentEdgeId,model.circleIconXs, model.circleIconYs);
            view.showCircleFavorite();
            currentCircleIconHighlight = -1;
            currentShowing = Cons.SHOWING_FAVORITE_CIRCLE;

        } else {
            view.unhighlightCircleIcon(currentCircleIconHighlight, currentEdgeId, model.circleIconXs, model.circleIconYs);
            currentCircleIconHighlight = -1;
            view.removeCircleShortcutsView();
            view.showFavoriteGridView(xInit, yInit, currentPosition, iconToSwitch);
            currentShowing = Cons.SHOWING_GRID;
            view.setIndicator(null, false, -1);
        }
    }

    public void onDestroy() {
        view.removeAll();
        view.asyncTask.cancel(true);
        view.asyncTask.clear();
    }


    private void highlightCircleIconAndSwitchToGridIfNeed(int iconToSwitch, int edgeId) {

        if (iconToSwitch != currentCircleIconHighlight) {
            Log.e(TAG, "onActionMove: iconToSwitch = " + iconToSwitch);
            view.unhighlightCircleIcon(currentCircleIconHighlight, edgeId,model.circleIconXs, model.circleIconYs);
            view.highlightCircleIcon(iconToSwitch, edgeId, xInit, yInit, model.circleIconXs, model.circleIconYs);
            currentCircleIconHighlight = iconToSwitch;
            if (currentShowing == Cons.SHOWING_RECENT_CIRCLE) {

                if (iconToSwitch >= 0 && iconToSwitch < model.savedRecentShortcut.length) {
                    onHolding = true;
                    startHoldingTime = System.currentTimeMillis();
                    view.setIndicator(model.savedRecentShortcut[iconToSwitch], false, -1);
                } else if (iconToSwitch >= 10) {
                    view.setIndicator(null, true, iconToSwitch - 10);
                } else {
                    view.setIndicator(null, false, -1);
                }
            } else if (currentShowing == Cons.SHOWING_FAVORITE_CIRCLE) {
                if (iconToSwitch >= 0 && iconToSwitch < Cons.CIRCLE_ICON_NUMBER_DEFAULT) {
                    view.setIndicator(view.getCircleFavoriteShorcut(iconToSwitch), false, -1);
                } else if (iconToSwitch >= 10) {
                    view.setIndicator(null, true, iconToSwitch - 10);
                } else {
                    view.setIndicator(null, false, -1);
                }
            }
            if (iconToSwitch >= 10
                    && view.edge1QuickActionViews[iconToSwitch - 10].getId() == Cons.QUICK_ACTION_ID_INSTANT_GRID) {
                onHolding = false;
                view.removeCircleShortcutsView();
                view.showFavoriteGridView(xInit, yInit, currentPosition, -1);
                currentShowing = Cons.SHOWING_GRID;
                view.setIndicator(null, false, -1);
                view.unhighlightCircleIcon(iconToSwitch, edgeId,model.circleIconXs, model.circleIconYs);
                currentCircleIconHighlight = -1;
            }
            clearAsyncTask();
            if (iconToSwitch < 10 && iconToSwitch != -1) {
                startAsyncTask(iconToSwitch);
            }
            if (view.useActionMoveVibrate && iconToSwitch != -1) {
                view.vibrate();
            }
        }
//        else if (iconToSwitch < 10
//                && onHolding
//                && currentCircleIconHighlight != -1
//                && System.currentTimeMillis() - startHoldingTime > view.holdTime) {
//
//            if (currentEdgeMode == Cons.MODE_CIRCLE_FAVORITE) {
//                view.unhighlightCircleIcon(currentCircleIconHighlight, edgeId,model.circleIconXs, model.circleIconYs);
//                view.showCircleFavorite();
//                currentCircleIconHighlight = -1;
//                currentShowing = Cons.SHOWING_FAVORITE_CIRCLE;
//
//            } else {
//                view.unhighlightCircleIcon(currentCircleIconHighlight, edgeId, model.circleIconXs, model.circleIconYs);
//                currentCircleIconHighlight = -1;
//                view.removeCircleShortcutsView();
//                view.showFavoriteGridView(xInit, yInit, currentPosition, iconToSwitch);
//                currentShowing = Cons.SHOWING_GRID;
//                view.setIndicator(null, false, -1);
//            }
//        }

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
            if (view.useActionMoveVibrate && activatedGridIcon != -1) {
                view.vibrate();
            }
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

    private void clearAsyncTask() {
        view.asyncTask.cancel(true);
        view.asyncTask.clear();
    }

    private void startAsyncTask(int iconToSwitch) {
        view.asyncTask = new DelayToSwitchAsyncTask(view.holdTime, this);
        view.asyncTask.execute(iconToSwitch);
    }
}
