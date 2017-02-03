package org.de_studio.recentappswitcher.edgeService;

import android.graphics.Point;
import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
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
    long holdTime;
    float xInit, yInit;
    Edge currentEdge;
    boolean onHolding;
    Showing currentShowing = new Showing();
    long highlightFrom;
    int currentHighlight = -1;
    long holdingHelper;
    String previousGridId;

    PublishSubject<Integer> highlightIdSubject = PublishSubject.create();
    PublishSubject<Void> longClickItemSubject = PublishSubject.create();
    PublishSubject<Long> longClickHelperSubject = PublishSubject.create();
    PublishSubject<String> showCollectionInstantlySubject = PublishSubject.create();
    PublishSubject<Slot> onSlot = PublishSubject.create();
    PublishSubject<Void> returnToGridSubject = PublishSubject.create();


    public NewServicePresenter(NewServiceModel model,long holdTime) {
        super(model);
        this.holdTime = holdTime;
    }

    @Override
    public void onViewAttach(final View view) {
        Log.e(TAG, "onViewAttach: ");
        super.onViewAttach(view);
        model.setup();
        view.addEdgesToWindowAndSetListener();
        view.setupNotification();
        view.setupReceiver();

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

                        onSlot.onNext(getCurrentSlot());

                        if (integer >= 10
                                && currentShowing.showWhat == Showing.SHOWING_CIRCLE_AND_ACTION
                                && currentShowing.action.slots.get(integer - 10).type.equals(Slot.TYPE_ITEM)
                                && currentShowing.action.slots.get(integer - 10).stage1Item.type.equals(Item.TYPE_SHORTCUTS_SET)) {
                            showCollectionInstantlySubject.onNext(currentShowing.action.slots.get(integer - 10).stage1Item.collectionId);
                        }
                    }
                })
        );

        addSubscription(
                onSlot.subscribe(new Action1<Slot>() {
                    @Override
                    public void call(Slot slot) {
                        //nothing now
                    }
                })
        );

        addSubscription(
                showCollectionInstantlySubject.subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e(TAG, "call: showCollectionInstantly");
                        Collection collection = model.getCollection(s);
                        if (collection.type.equals(Collection.TYPE_GRID_FAVORITE)) {
                            showGrid(collection, view);
                        }
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
                        switch (currentShowing.showWhat) {
                            case Showing.SHOWING_GRID:
                                Slot slot = currentShowing.grid.slots.get(currentHighlight);
                                if (slot.type.equals(Slot.TYPE_FOLDER)) {
                                    view.unhighlightSlot(currentShowing, currentHighlight);
                                    view.indicateCurrentShowing(currentShowing, -1);

                                    view.showFolder(currentHighlight, slot, currentShowing.grid.collectionId, currentShowing.grid.space, currentEdge.position, currentShowing);
                                    currentHighlight = -1;
                                    previousGridId = currentShowing.grid.collectionId;
                                    currentShowing.showWhat = Showing.SHOWING_FOLDER;
                                    currentShowing.folderSlotId = slot.slotId;
                                    currentShowing.folderItems = slot.items;

                                }
                                break;

                        }

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
                        currentShowing.grid = model.getCollection(previousGridId);
                        currentShowing.gridXY = view.getGridXy(previousGridId);
                        view.showCollection(currentShowing.grid.collectionId);
                    }
                })
        );
    }

    private void showGrid(Collection collection, View view) {
        currentHighlight = -1;
        view.showGrid(xInit, yInit, collection, currentEdge.position, currentShowing);
        currentShowing.showWhat = Showing.SHOWING_GRID;
        currentShowing.grid = collection;
    }

    @Override
    public void onViewDetach() {
        view.removeAll();
        super.onViewDetach();
    }

    public void onActionDown(float x, float y, int edgeId) {
        setCurrentEdge(edgeId);
        setTriggerPoint(x, y);

        ArrayList<String> tempPackages = view.getRecentApp();
        view.showBackground();
        showCollection(tempPackages);
        view.actionDownVibrate();
        view.showClock();
        holdingHelper = 0;
    }

    public void onActionMove(float x, float y) {
        switch (currentShowing.showWhat) {
            case Showing.SHOWING_CIRCLE_AND_ACTION:
                int highlight = model.getCircleAndQuickActionTriggerId(currentShowing.circleIconsXY, currentShowing.circle.radius, xInit, yInit, x, y, currentEdge.position, currentShowing.circle.slots.size());
                highlightIdSubject.onNext(highlight);
                break;
            case Showing.SHOWING_GRID:
                int onPosition = model.getGridActivatedId(x, y, currentShowing.gridXY.x, currentShowing.gridXY.y, currentShowing.grid.rowsCount, currentShowing.grid.columnCount, currentShowing.grid.space, false);
                highlightIdSubject.onNext(onPosition);
                break;
            case Showing.SHOWING_FOLDER:
                int columCount = Math.min(currentShowing.folderItems.size(), 4);
                int rowCount = currentShowing.folderItems.size() / 4 + 1;
                int folderItemHighlighted = model.getGridActivatedId(x, y, currentShowing.gridXY.x, currentShowing.gridXY.y, rowCount, columCount, Cons.DEFAULT_FAVORITE_GRID_SPACE, true);
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
        view.hideAllExceptEdges();
        if (currentHighlight >=0) {
            switch (currentShowing.showWhat) {
                case Showing.SHOWING_CIRCLE_AND_ACTION:
                    if (currentHighlight < 10) {
                        Log.e(TAG, "onActionUp: size = " + currentShowing.circleSlots.size() + "\nhighlight "+ currentHighlight) ;
                        if (currentShowing.circleSlots.size() > currentHighlight) {
                            slot = currentShowing.circleSlots.get(currentHighlight);
                        }
                    } else {
                        slot = currentShowing.action.slots.get(currentHighlight - 10);
                    }
                    break;
                case Showing.SHOWING_GRID:
                    slot = currentShowing.grid.slots.get(currentHighlight);
                    break;
                case Showing.SHOWING_FOLDER:
                    if (currentHighlight < currentShowing.folderItems.size()) {
                        view.startItem(currentShowing.folderItems.get(currentHighlight), model.getLastApp());
                    }
                    break;
            }
            if (slot != null) {
                view.startSlot(slot, model.getLastApp());
            }
            view.unhighlightSlot(currentShowing, currentHighlight);
            currentShowing.showWhat = Showing.SHOWING_NONE;
            currentHighlight = -1;
        }
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



    private void showCollection(ArrayList<String> tempPackages) {
        switch (currentEdge.mode) {
            case Edge.MODE_GRID:
                Log.e(TAG, "showCollection: grid");
                showGrid(currentEdge.grid, view);
                break;
            case Edge.MODE_RECENT_AND_QUICK_ACTION:
                Log.e(TAG, "showCollection: recent and quick action");
                currentShowing.circleIconsXY = model.calculateCircleIconPositions(currentEdge.recent.radius, currentEdge.position
                        , xInit, yInit, currentEdge.recent.slots.size());
                RealmList<Slot> slots = model.getRecent(tempPackages, currentEdge.recent.slots);
                view.showCircle(currentShowing.circleIconsXY, currentEdge.recent
                        , slots, xInit,yInit);
                currentShowing.showWhat = Showing.SHOWING_CIRCLE_AND_ACTION;
                currentShowing.circle = currentEdge.recent;
                currentShowing.circleSlots = slots;
                currentShowing.action = currentEdge.quickAction;
                break;
            case Edge.MODE_CIRCLE_FAV_AND_QUICK_ACTION:
                Log.e(TAG, "showCollection: circle and quick action");
                currentShowing.circleIconsXY = model.calculateCircleIconPositions(currentEdge.circleFav.radius, currentEdge.position
                        , xInit, yInit, currentEdge.circleFav.slots.size());
                view.showCircle(currentShowing.circleIconsXY, currentEdge.circleFav, currentEdge.circleFav.slots,xInit,yInit);
                currentShowing.showWhat = Showing.SHOWING_CIRCLE_AND_ACTION;
                currentShowing.circle = currentEdge.circleFav;
                currentShowing.circleSlots = currentShowing.circle.slots;
                currentShowing.action = currentEdge.quickAction;
                break;
        }
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
            case Cons.EDGE_1_ID_INT:
                currentEdge = model.getEdge(Edge.EDGE_1_ID);
                break;
            case Cons.EDGE_2_ID_INT:
                currentEdge = model.getEdge(Edge.EDGE_2_ID);
                break;
        }
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

    public interface View extends PresenterView, android.view.View.OnTouchListener {
        void addEdgesToWindowAndSetListener();

        void setupNotification();

        void setupReceiver();

        Point getWindowSize();

        ArrayList<String> getRecentApp();

        void showBackground();

        void showGrid(float xInit, float yInit, Collection grid, int position, Showing currentShowing);

        void showCircle(NewServiceModel.IconsXY iconsXY, Collection circle, RealmList<Slot> slots, float xInit, float yInit);

        void showFolder(int triggerPosition, Slot folder, final String gridId, int space, int edgePosition, Showing currentShowing);

        void actionDownVibrate();

        void actionMoveVibrate();

        void showClock();

        void indicateSlot(Slot slot);

        void indicateItem(Item item);

        void indicateCurrentShowing(Showing currentShowing, int id);

        void highlightSlot(Showing currentShowing, int id);

        void unhighlightSlot(Showing currentShowing, int id);

        void startSlot(Slot slot, String lastApp);

        void startItem(Item item, String lastApp);

        void hideCollection(String collectionId);

        void showCollection(String collectionId);

        Point getGridXy(String collectionId);

        void hideAllCollections();

        void hideAllExceptEdges();

        void removeAllExceptEdges();

        void removeAll();
    }

    public class Showing {
        public static final int SHOWING_GRID = 0;
        public static final int SHOWING_CIRCLE_AND_ACTION = 1;
        public static final int SHOWING_NONE = 2;
        public static final int SHOWING_FOLDER = 3;
        public int showWhat;
        public Collection grid;
        public Collection circle;
        public Collection action;
        public RealmList<Slot> circleSlots;
        public RealmList<Item> folderItems;
        public String folderSlotId;
        public NewServiceModel.IconsXY circleIconsXY;
        public Point gridXY = new Point(0, 0);
        public Showing() {
        }

    }

}
