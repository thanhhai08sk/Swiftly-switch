package org.de_studio.recentappswitcher.edgeService;

import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Edge;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.realm.RealmList;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/23/16.
 */

public class NewServicePresenter extends BasePresenter<NewServicePresenter.View, NewServiceModel> {
    private static final String TAG = NewServicePresenter.class.getSimpleName();
    private Runnable hideAllExceptEdgesRunnable = new Runnable() {
        @Override
        public void run() {
            if (view !=null && finishSectionSJ != null) {
                finishSectionSJ.onNext(null);
            }
        }
    };
    private Runnable pause10SecondRunnable = new Runnable() {
        @Override
        public void run() {
            if (view != null) {
                view.addEdgeViews();
            }
        }
    };

    Handler handler = new Handler();
    long holdTime;
    float xInit, yInit;
    Edge currentEdge;
    boolean onHolding;
    Showing currentShowing = new Showing();
    long highlightFrom;
    int currentHighlight = -1;
    long holdingHelper;
    ArrayList<String> tempRecentPackages;

    PublishSubject<Integer> highlightIdSubject = PublishSubject.create();
    PublishSubject<Void> longClickItemSubject = PublishSubject.create();
    PublishSubject<Long> longClickHelperSubject = PublishSubject.create();
    PublishSubject<String> showCollectionInstantlySubject = PublishSubject.create();
    PublishSubject<Slot> showFolderSJ = PublishSubject.create();
    PublishSubject<Slot> onSlotSJ = PublishSubject.create();
    PublishSubject<Void> returnToGridSubject = PublishSubject.create();
    PublishSubject<Void> finishSectionSJ = PublishSubject.create();
    private PublishSubject<Void> onGivingPermissionSJ = PublishSubject.create();
    PublishSubject<Slot> startSlotSJ = PublishSubject.create();



    public NewServicePresenter(NewServiceModel model,long holdTime) {
        super(model);
        this.holdTime = holdTime;
    }

    @Override
    public void onViewAttach(final View view) {
        super.onViewAttach(view);
        model.setup();
        view.addEdgesToWindowAndSetListener();
        view.setupNotification();
        view.setupReceiver();
        model.setSavedRecentShortcuts(view.getRecentApp(Cons.TIME_INTERVAL_LONG));


        addSubscription(
                highlightIdSubject.filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer!=currentHighlight;
                    }
                }).subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
//                        Log.e(TAG, "call: highlight " + integer);
                        view.unhighlightSlot(currentShowing, currentHighlight);
                        view.highlightSlot(currentShowing, integer);
                        view.indicateCurrentShowing(currentShowing,integer);
                        currentHighlight = integer;
                        highlightFrom = System.currentTimeMillis();
                        holdingHelper = holdingHelper + integer;
                        longClickHelperSubject.onNext(holdingHelper);
                        if (integer!= -1) {
                            view.actionMoveVibrate();
                        }
                        onSlotSJ.onNext(getCurrentSlot());
                    }
                })
        );

        addSubscription(
                onSlotSJ.subscribe(new Action1<Slot>() {
                    @Override
                    public void call(Slot slot) {
                        if (slot != null) {
                            if (slot.instant) {
                                startSlotSJ.onNext(slot);
                            } else if (!view.isOpenFolderDelay() && slot.type.equals(Slot.TYPE_FOLDER)) {
                                showFolderSJ.onNext(slot);
                            }
//                            else {
//                                if (slot.type.equals(Slot.TYPE_ITEM) && slot.stage1Item.type.equals(Item.TYPE_SHORTCUTS_SET)) {
//                                    showCollectionInstantlySubject.onNext(slot.stage1Item.collectionId);
//                                }
//                            }

                        }
                    }
                })
        );

        addSubscription(
                showCollectionInstantlySubject.subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e(TAG, "call: showCollectionInstantly");
                        Collection collection = model.getCollection(s);
                        switch (collection.type) {
                            case Collection.TYPE_GRID_FAVORITE:
                                view.hideAllCollections();
                                showGrid(collection, view);
                                break;
                            case Collection.TYPE_CIRCLE_FAVORITE:
                                if (tempRecentPackages == null) {
                                    tempRecentPackages = view.getRecentApp(Cons.TIME_INTERVAL_SHORT);
                                }
                                currentShowing.showWhat = Showing.SHOWING_CIRCLE_ONLY;
                                currentShowing.circle = collection;
                                currentShowing.circleSlots = collection.slots;
                                currentShowing.stayOnScreen = isStayOnScreen(collection);
                                view.hideAllCollections();
                                showCollection(Showing.SHOWING_CIRCLE_ONLY);
                                break;
                            case Collection.TYPE_RECENT:
                                if (tempRecentPackages == null) {
                                    tempRecentPackages = view.getRecentApp(Cons.TIME_INTERVAL_SHORT);
                                }
                                currentShowing.showWhat = Showing.SHOWING_CIRCLE_ONLY;
                                currentShowing.stayOnScreen = isStayOnScreen(collection);
                                currentShowing.circle = collection;
                                currentShowing.circleSlots = model.getRecent(tempRecentPackages, collection.slots, currentShowing);
                                view.hideAllCollections();
                                showCollection(currentShowing.showWhat);
                                break;
                        }
                        highlightIdSubject.onNext(-1);
                        hideAllExceptEdgesAfter10Seconds();
                    }
                })
        );

        addSubscription(
                longClickHelperSubject.delay(holdTime, TimeUnit.MILLISECONDS).filter(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return aLong == holdingHelper;
                    }
                }).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        longClickItemSubject.onNext(null);
                    }
                })
        );

        addSubscription(
                longClickItemSubject
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (currentHighlight == -1) {
                            return;
                        }
                        boolean onCircle = false;
                        switch (currentShowing.showWhat) {
                            case Showing.SHOWING_GRID:
                                Slot slot = currentShowing.grid.slots.get(currentHighlight);
                                if (slot.type.equals(Slot.TYPE_FOLDER)) {
                                    showFolderSJ.onNext(slot);
                                }
                                break;
                            case Showing.SHOWING_CIRCLE_AND_ACTION:
                                if (currentHighlight < 10) {
                                    onCircle = true;
                                }
                                break;
                            case Showing.SHOWING_CIRCLE_ONLY:
                                onCircle = true;
                                break;
                        }

                        if (onCircle && currentShowing.circle.longClickMode == Collection.LONG_CLICK_MODE_OPEN_COLLECTION &&
                                currentShowing.circle.longPressCollection != null) {
                            setInitPointByTriggerIcon();
                            view.unhighlightSlot(currentShowing,currentHighlight);
                            showCollectionInstantlySubject.onNext(currentShowing.circle.longPressCollection.collectionId);

                        }

                    }
                })
        );

        addSubscription(
                showFolderSJ.subscribe(new Action1<Slot>() {
                    @Override
                    public void call(Slot slot) {
                        view.unhighlightSlot(currentShowing, currentHighlight);
                        view.indicateCurrentShowing(currentShowing, -1);
                        view.showFolder(currentShowing.grid.slots.indexOf(slot), slot, currentShowing.grid.collectionId, currentShowing.grid.space, currentEdge.position, currentShowing);
                        currentHighlight = -1;
                        currentShowing.showWhat = Showing.SHOWING_FOLDER;
                        currentShowing.folderSlotId = slot.slotId;
                        currentShowing.folderItems = slot.items;
                    }
                })
        );

        addSubscription(
                returnToGridSubject.subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.e(TAG, "call: return to grid");
                        view.hideCollection(currentShowing.folderSlotId);
                        currentShowing.showWhat = Showing.SHOWING_GRID;
                        currentShowing.stayOnScreen = currentShowing.grid.stayOnScreen == null ? true : currentShowing.grid.stayOnScreen;
                        currentShowing.gridXY = view.getGridXy(currentShowing.grid.collectionId);
                        view.showCollection(currentShowing.grid.collectionId);
                        hideAllExceptEdgesAfter10Seconds();
                    }
                })
        );

        addSubscription(
                finishSectionSJ.subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.e(TAG, "call: finish section");
                        view.unhighlightSlot(currentShowing, currentHighlight);
                        view.hideAllExceptEdges();
                        currentHighlight = -1;
                        currentShowing.showWhat = Showing.SHOWING_NONE;
                        currentShowing.lastApp = null;
                        tempRecentPackages = null;
                        model.clearSectionData();
                        view.setFirstSectionFalse();
                    }
                })
        );

        addSubscription(
                onGivingPermissionSJ.subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.e(TAG, "call: pause for 10 second");
                        view.removeEdgeViews();
                        finishSectionSJ.onNext(null);
                        handler.postDelayed(pause10SecondRunnable, 10000);
                        view.showToast(R.string.pause_for_10_second_toast);
                    }
                })
        );

        addSubscription(
                view.onEnterOrExitFullScreen().subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        view.disableEdgeViews(aBoolean);
                    }
                })
        );

        addSubscription(
                startSlotSJ.subscribe(new Action1<Slot>() {
                    @Override
                    public void call(Slot slot) {
                        if (slot != null) {
                            switch (slot.type) {
                                case Slot.TYPE_ITEM:
                                    if (slot.stage1Item.type.equals(Item.TYPE_SHORTCUTS_SET)) {
                                        showCollectionInstantlySubject.onNext(slot.stage1Item.collectionId);
                                    } else {
                                        String lastApp = getLastApp();
                                        Log.e(TAG, "call: lastapp = " + lastApp);
                                        view.startItem(slot.stage1Item, lastApp);
                                        finishSectionSJ.onNext(null);
                                    }
                                    break;
                                case Slot.TYPE_NULL:
                                    view.setNullSlot(currentShowing.showWhat, getCurrentCollectionId());
                                    finishSectionSJ.onNext(null);
                                    break;
                                case Slot.TYPE_FOLDER:
                                    showFolderSJ.onNext(slot);
                                    break;
                            }
                        }
                    }
                })
        );

        addSubscription(
                view.onFinishTakingScreenshot().subscribe(new Action1<Uri>() {
                    @Override
                    public void call(Uri uri) {
//                        view.openFile(uri);
                        view.showScreenshotReadyButton(uri);
                    }
                })
        );


    }

    private void setInitPointByTriggerIcon() {
        switch (Utility.rightLeftOrBottom(currentShowing.edgePosition)) {
            case Cons.POSITION_BOTTOM:
                currentShowing.xInit = model.getIconCenterX(currentShowing.circleIconsXY.xs[currentHighlight]);
                break;
            default:
                currentShowing.yInit = model.getIconCenterY(currentShowing.circleIconsXY.ys[currentHighlight]);
                break;
        }
    }

    @Nullable
    private String getCurrentCollectionId() {
        String currentCollectionId = null;
        switch (currentShowing.showWhat) {
            case Showing.SHOWING_CIRCLE_AND_ACTION:
                currentCollectionId = currentShowing.circle.collectionId;
                break;
            case Showing.SHOWING_CIRCLE_ONLY:
                currentCollectionId = currentShowing.circle.collectionId;
                break;
            case Showing.SHOWING_GRID:
                currentCollectionId = currentShowing.grid.collectionId;
                break;
        }
        return currentCollectionId;
    }

    private void showGrid(Collection collection, View view) {
        currentHighlight = -1;
        view.showGrid(collection, currentEdge.position, currentShowing);
        currentShowing.showWhat = Showing.SHOWING_GRID;
        currentShowing.grid = collection;
        currentShowing.stayOnScreen = currentShowing.grid.stayOnScreen == null ? true : currentShowing.grid.stayOnScreen;
    }

    @Override
    public void onViewDetach() {
        view.removeAll();
        handler.removeCallbacks(hideAllExceptEdgesRunnable);
        handler = null;
        hideAllExceptEdgesRunnable = null;
        pause10SecondRunnable = null;
        super.onViewDetach();
    }


    public void onActionDown(float x, float y, int edgeId) {
        stopHideViewsHandler();
//        long time = System.currentTimeMillis();
        tempRecentPackages = view.getRecentApp(Cons.TIME_INTERVAL_SHORT);
        if (tempRecentPackages.size() > 0 && tempRecentPackages.get(0).equals("com.google.android.packageinstaller") && Utility.isMashmallow()) {
            onGivingPermissionSJ.onNext(null);
        } else {
            setCurrentEdgeAndCurrentShowing(edgeId);
            setTriggerPoint(x, y);

            view.showBackground(model.shouldBackgroundTouchable());
            view.actionDownVibrate();
            view.showClock();
            showCollection(currentShowing.showWhat);
            holdingHelper = 0;
//            Log.e(TAG, "onActionDown: time to finish = " + (System.currentTimeMillis() - time));

        }
    }

    public void onActionMove(float x, float y) {
        switch (currentShowing.showWhat) {
            case Showing.SHOWING_CIRCLE_AND_ACTION:
                int highlight = model.getCircleAndQuickActionTriggerId(currentShowing.circleIconsXY, currentShowing.circle.radius, xInit, yInit, x, y, currentEdge.position, currentShowing.circle.slots.size(), true,currentShowing.action.slots.size(),false);
                highlightIdSubject.onNext(highlight);
                break;
            case Showing.SHOWING_CIRCLE_ONLY:
                int highlight1 = model.getCircleAndQuickActionTriggerId(currentShowing.circleIconsXY, currentShowing.circle.radius, xInit, yInit, x, y, currentEdge.position, currentShowing.circle.slots.size(), false, -1, false);
                highlightIdSubject.onNext(highlight1);
                break;
            case Showing.SHOWING_GRID:
                int onPosition = model.getGridActivatedId(x, y, currentShowing.gridXY.x, currentShowing.gridXY.y, currentShowing.grid.rowsCount, currentShowing.grid.columnCount, currentShowing.grid.space, false,view.isRTL());
                highlightIdSubject.onNext(onPosition);
                break;
            case Showing.SHOWING_FOLDER:
                int columCount = Math.min(currentShowing.folderItems.size(), 4);
                int rowCount = currentShowing.folderItems.size() / 4 + 1;
                int folderItemHighlighted = model.getGridActivatedId(x, y, currentShowing.folderXY.x, currentShowing.folderXY.y, rowCount, columCount, Cons.DEFAULT_FAVORITE_GRID_SPACE, true, view.isRTL());
                highlightIdSubject.onNext(folderItemHighlighted);
                if (folderItemHighlighted == -2) {
                    returnToGridSubject.onNext(null);
                }
                break;
        }
    }

    public void onActionUp(float x, float y) {
        Log.e(TAG, "onActionUp: currentHighlight = " + currentHighlight);
        onHolding = false;
        Slot slot = null;
        switch (currentShowing.showWhat) {
            case Showing.SHOWING_CIRCLE_AND_ACTION:
                if (currentHighlight >= 0) {
                    if (currentHighlight < 10) {
                        Log.e(TAG, "onActionUp: size = " + currentShowing.circleSlots.size() + "\nhighlight " + currentHighlight);
                        if (currentShowing.circleSlots.size() > currentHighlight) {
                            slot = currentShowing.circleSlots.get(currentHighlight);
                        }
                    } else {
                        slot = currentShowing.action.slots.get(currentHighlight - 10);
                        if (slot.type.equals(Slot.TYPE_EMPTY) || slot.type.equals(Slot.TYPE_NULL)) {
                            slot = null;
                        }
                    }
                } else if (isStayOnScreen(currentShowing.circle)) {
                    if (currentShowing.action.visibilityOption == Collection.VISIBILITY_OPTION_VISIBLE_AFTER_LIFTING) {
                        view.showQuickActions(currentEdge.position, currentHighlight, currentShowing, false, false);
                    } else {
                        if (currentShowing.action.visibilityOption != Collection.VISIBILITY_OPTION_ALWAYS_VISIBLE) {
                            currentShowing.showWhat = Showing.SHOWING_CIRCLE_ONLY;
                        }
                    }
                }
                break;
            case Showing.SHOWING_CIRCLE_ONLY:
                if (currentHighlight >= 0) {
                    if (currentHighlight < currentShowing.circleSlots.size()) {
                        slot = currentShowing.circleSlots.get(currentHighlight);
                    }
                }
                break;
            case Showing.SHOWING_GRID:
                if (currentHighlight >= 0) {
                    slot = currentShowing.grid.slots.get(currentHighlight);
                }
                break;
            case Showing.SHOWING_FOLDER:
                if (currentHighlight >= 0) {
                    if (currentHighlight < currentShowing.folderItems.size()) {
                        view.startItem(currentShowing.folderItems.get(currentHighlight), getLastApp());
                    }
                }
                finishSectionSJ.onNext(null);
                break;
        }


        if (slot != null) {
            startSlotSJ.onNext(slot);
//            if (currentShowing.stayOnScreen && !slot.type.equals(Slot.TYPE_FOLDER)) {
//                finishSectionSJ.onNext(null);
//            }
        }

        if (!(currentShowing.stayOnScreen || (slot != null && slot.type.equals(Slot.TYPE_FOLDER)))) {
            finishSectionSJ.onNext(null);
        }
        currentHighlight = -1;
    }

    public void onActionOutSide() {
        Log.e(TAG, "onActionOutSide: ");
        view.hideAllExceptEdges();
        onHolding = false;
    }

    public void onActionCancel() {
        Log.e(TAG, "onActionCancel: ");
        view.hideAllExceptEdges();
        onHolding = false;
    }

    public void requestShowingFolder(Slot slot) {
        showFolderSJ.onNext(slot);
    }

    public void onClickBackground(float x, float y) {
        switch (currentShowing.showWhat) {
            case Showing.SHOWING_GRID:
                int onPosition = model.getGridActivatedId(x, y, currentShowing.gridXY.x, currentShowing.gridXY.y, currentShowing.grid.rowsCount, currentShowing.grid.columnCount, currentShowing.grid.space, false, view.isRTL());
                if (onPosition != -1) {
                    Slot slot = currentShowing.grid.slots.get(onPosition);
                    if (slot != null) {
                        startSlotSJ.onNext(slot);
                    }
                } else {
                    finishSectionSJ.onNext(null);
                }
                break;
            case Showing.SHOWING_FOLDER:
                int columCount = Math.min(currentShowing.folderItems.size(), 4);
                int rowCount = currentShowing.folderItems.size() / 4 + 1;
                int position1 = model.getGridActivatedId(x, y, currentShowing.folderXY.x, currentShowing.folderXY.y, rowCount, columCount, currentShowing.grid.space, true,view.isRTL());
                if (position1 >=0 && position1 <currentShowing.folderItems.size()) {
                    view.startItem(currentShowing.folderItems.get(position1), getLastApp());
                    view.hideAllExceptEdges();
                } else {
                    if (currentShowing.grid.stayOnScreen == null ? true : currentShowing.grid.stayOnScreen) {
                        returnToGridSubject.onNext(null);
                    } else {
//                        view.hideAllExceptEdges();
                        finishSectionSJ.onNext(null);
                    }
                }
                break;
            case Showing.SHOWING_CIRCLE_AND_ACTION:
                int onPosition1 = model.getCircleAndQuickActionTriggerId(currentShowing.circleIconsXY, currentShowing.circle.radius, xInit, yInit, x, y, currentShowing.edgePosition, currentShowing.circleSlots.size(), true, currentShowing.action.slots.size(),true);
                Log.e(TAG, "onClickBackground: circle and action, position = " + onPosition1);
                if (onPosition1 != -1) {
                    if (onPosition1 < 10) {
                        startSlotSJ.onNext(currentShowing.circleSlots.get(onPosition1));

                    } else {
                        startSlotSJ.onNext(currentShowing.action.slots.get(onPosition1 - 10));
                    }
                }else finishSectionSJ.onNext(null);
                break;
            case Showing.SHOWING_CIRCLE_ONLY:
                int onPosition2 = model.getCircleAndQuickActionTriggerId(currentShowing.circleIconsXY, currentShowing.circle.radius, xInit, yInit, x, y, currentShowing.edgePosition, currentShowing.circleSlots.size(), true, currentShowing.action.slots.size(),true);
                Log.e(TAG, "onClickBackground: circle only, position = " + onPosition2);
                if (onPosition2 != -1) {
                    if (onPosition2 < 10) {
                        startSlotSJ.onNext(currentShowing.circleSlots.get(onPosition2));
                    }else finishSectionSJ.onNext(null);
                }else finishSectionSJ.onNext(null);
                break;
        }
    }



    private void showCollection(int showWhat) {
        switch (showWhat) {
            case Showing.SHOWING_GRID:
                view.showGrid(currentShowing.grid, currentEdge.position, currentShowing);
                break;
            case Showing.SHOWING_CIRCLE_AND_ACTION:
                updateCircleIconPosition();
                view.showCircle(currentShowing.circleIconsXY, currentShowing.circle, currentShowing.circleSlots, xInit, yInit);
                if (currentShowing.action.visibilityOption == Collection.VISIBILITY_OPTION_ALWAYS_VISIBLE) {
                    view.showQuickActions(currentEdge.position, -1, currentShowing, true,true);
                }
                break;
            case Showing.SHOWING_CIRCLE_ONLY:
                updateCircleIconPosition();
                view.showCircle(currentShowing.circleIconsXY, currentShowing.circle, currentShowing.circleSlots, xInit, yInit);
                break;
        }
        hideAllExceptEdgesAfter10Seconds();
    }

    private void updateCircleIconPosition() {
        currentShowing.circleIconsXY = model.calculateCircleIconPositions(currentShowing.circle.radius, currentEdge.position
                , xInit, yInit, currentShowing.circle.slots.size());
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
        currentShowing.xInit = xInit;
        currentShowing.yInit = yInit;
    }

    private void hideAllExceptEdgesAfter10Seconds() {
        handler.removeCallbacks(hideAllExceptEdgesRunnable);
        handler.postDelayed(hideAllExceptEdgesRunnable, 15 * 1000);
    }

    private void stopHideViewsHandler() {
        if (handler != null) {
            handler.removeCallbacks(hideAllExceptEdgesRunnable);
        }
    }

    private void setCurrentEdgeAndCurrentShowing(int edgeId) {
        switch (edgeId) {
            case Cons.EDGE_1_ID_INT:
                currentEdge = model.getEdge(Edge.EDGE_1_ID);
                break;
            case Cons.EDGE_2_ID_INT:
                currentEdge = model.getEdge(Edge.EDGE_2_ID);
                break;
        }

        currentHighlight = -1;
        switch (currentEdge.mode) {
            case Edge.MODE_GRID:
                currentShowing.showWhat = Showing.SHOWING_GRID;
                currentShowing.grid = currentEdge.grid;
                currentShowing.stayOnScreen = isStayOnScreen(currentEdge.grid);
                break;
            case Edge.MODE_RECENT_AND_QUICK_ACTION:
                currentShowing.showWhat = Showing.SHOWING_CIRCLE_AND_ACTION;
                currentShowing.circle = currentEdge.recent;
                currentShowing.action = currentEdge.quickAction;
                currentShowing.circleSlots = model.getRecent(tempRecentPackages, currentShowing.circle.slots,currentShowing);
                currentShowing.stayOnScreen = isStayOnScreen(currentShowing.circle);
                break;
            case Edge.MODE_CIRCLE_FAV_AND_QUICK_ACTION:
                currentShowing.showWhat = Showing.SHOWING_CIRCLE_AND_ACTION;
                currentShowing.circle = currentEdge.circleFav;
                currentShowing.action = currentEdge.quickAction;
                currentShowing.circleSlots = currentShowing.circle.slots;
                currentShowing.stayOnScreen = isStayOnScreen(currentShowing.circle);
                break;
            case Edge.MODE_CIRCLE_FAVORITE_ONLY:
                currentShowing.showWhat = Showing.SHOWING_CIRCLE_ONLY;
                currentShowing.circle = currentEdge.circleFav;
                currentShowing.circleSlots = currentShowing.circle.slots;
                currentShowing.stayOnScreen = isStayOnScreen(currentShowing.circle);
                break;
            case Edge.MODE_RECENT_ONLY:
                currentShowing.showWhat = Showing.SHOWING_CIRCLE_ONLY;
                currentShowing.circle = currentEdge.recent;
                currentShowing.circleSlots = model.getRecent(tempRecentPackages, currentShowing.circle.slots,currentShowing);
                currentShowing.stayOnScreen = isStayOnScreen(currentShowing.circle);
                break;
        }
        currentShowing.edgePosition = currentEdge.position;
    }

    private boolean isStayOnScreen(Collection collection) {
        return collection.stayOnScreen == null ? true : collection.stayOnScreen;
    }


    private Slot getCurrentSlot() {
        if (currentHighlight!= -1) {
            switch (currentShowing.showWhat) {
                case Showing.SHOWING_CIRCLE_AND_ACTION:
                    if (currentHighlight < 10) {
                        if (currentHighlight < currentShowing.circleSlots.size()) {
                            return currentShowing.circleSlots.get(currentHighlight);
                        } else return null;
                    } else {
                        return currentShowing.action.slots.get(currentHighlight -10);
                    }
                case Showing.SHOWING_GRID:
                    return currentShowing.grid.slots.get(currentHighlight);
            }
        }
        return null;
    }

    private String getLastApp() {
        if (currentShowing.lastApp == null) {
            currentShowing.lastApp = model.getLastApp(tempRecentPackages);
        }
        return currentShowing.lastApp;
    }

    public void newPackageInstalled(String packageName, String label) {
        model.addPackageToData(packageName, label);
    }

    public void onUninstallPackage(String packageName) {
        model.removeAppItemFromData(packageName);
    }


    public interface View extends PresenterView, android.view.View.OnTouchListener, GestureDetector.OnGestureListener
            , android.view.View.OnSystemUiVisibilityChangeListener {

        PublishSubject<Boolean> onEnterOrExitFullScreen();

        PublishSubject<Uri> onFinishTakingScreenshot();

        void addEdgesToWindowAndSetListener();

        void setupNotification();

        void setupReceiver();

        Point getWindowSize();

        ArrayList<String> getRecentApp(long timeInterval);

        void showBackground(boolean backgroundTouchable);

        void showGrid(Collection grid, int position, Showing currentShowing);

        void showCircle(NewServiceModel.IconsXY iconsXY, Collection circle, RealmList<Slot> slots, float xInit, float yInit);

        void showFolder(int triggerPosition, Slot folder, final String gridId, int space, int edgePosition, Showing currentShowing);

        void showQuickActions(int edgePosition, int highlightPosition, NewServicePresenter.Showing currentShowing, boolean delay, boolean animate);

        void actionDownVibrate();

        void actionMoveVibrate();

        void showClock();

        void indicateSlot(Slot slot);

        void indicateItem(Item item);

        void indicateCurrentShowing(Showing currentShowing, int id);

        void highlightSlot(Showing currentShowing, int id);

        void unhighlightSlot(Showing currentShowing, int id);

//        void startSlot(Slot slot, String lastApp, int showing, String currentCollectionId);

        void startItem(Item item, String lastApp);

        void setNullSlot(int showing, String currentCollectionId);

        void hideCollection(String collectionId);

        void showCollection(String collectionId);

        Point getGridXy(String collectionId);

        void hideAllCollections();

        void hideAllExceptEdges();

        void removeAllExceptEdges();

        void removeAll();

        void addEdgeViews();

        void removeEdgeViews();

        void disableEdgeViews(boolean disable);

        void showToast(int message);

        boolean isRTL();

        boolean isOpenFolderDelay();

        void setFirstSectionFalse();

        void showScreenshotReadyButton(Uri uri);

        void removeScreenshotReadyButton();

        void openFile(Uri uri);

    }

    public class Showing {
        public static final int SHOWING_GRID = 0;
        public static final int SHOWING_CIRCLE_AND_ACTION = 1;
        public static final int SHOWING_NONE = 2;
        public static final int SHOWING_FOLDER = 3;
        public static final int SHOWING_CIRCLE_ONLY = 4;
        public int showWhat;
        public Collection grid;
        public Collection circle;
        public Collection action;
        public RealmList<Slot> circleSlots;
        public RealmList<Item> folderItems;
        public String folderSlotId;
        public NewServiceModel.IconsXY circleIconsXY;
        public Point gridXY = new Point(0, 0);
        public Point folderXY = new Point(0, 0);
        public Point gridTriggerPoint = new Point(0, 0);
        public float xInit, yInit;
        public int edgePosition;
        boolean stayOnScreen;
        String lastApp;
        public Showing() {
        }

    }

}
