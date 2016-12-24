package org.de_studio.recentappswitcher.edgeService;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.SlotsAdapter;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import io.realm.RealmList;

/**
 * Created by HaiNguyen on 12/23/16.
 */

public class NewServiceView extends Service implements NewServicePresenter.View {


    @Inject
    IconPackManager.IconPack iconPack;
    @Inject
    @Named(Cons.GRID_PARENT_VIEW_PARA_NAME)
    WindowManager.LayoutParams collectionWindowPapams;
    @Inject
    WindowManager windowManager;
    @Inject
    float mScale;
    @Inject
    float iconScale;
    @Inject
    SharedPreferences sharedPreferences;
    HashMap<String, View> collectionViewsMap = new HashMap<>();



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public void addEdgesToWindowAndSetListener() {

    }

    @Override
    public void setupNotification() {

    }

    @Override
    public void setupReceiver() {

    }

    @Override
    public Point getWindowSize() {
        return null;
    }

    @Override
    public ArrayList<String> getRecentApp() {
        return null;
    }

    @Override
    public void showBackground() {

    }

    @Override
    public void showGrid(float xInit, float yInit, Collection grid, int position) {
        if (collectionViewsMap.get(grid.collectionId) == null) {
            RecyclerView gridView = new RecyclerView(this);
            gridView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            SlotsAdapter slotsAdapter = new SlotsAdapter(this, grid.slots, true, iconPack, Cons.ITEM_TYPE_ICON_ONLY);
            gridView.setLayoutManager(new GridLayoutManager(this, grid.columnCount));
            gridView.setAdapter(slotsAdapter);
            collectionViewsMap.put(grid.collectionId, gridView);
        }
        RecyclerView recyclerView =(RecyclerView) collectionViewsMap.get(grid.collectionId);
        if (!recyclerView.isAttachedToWindow()) {
            windowManager.addView(recyclerView,collectionWindowPapams);
        }
        Utility.setFavoriteGridViewPosition(recyclerView
                , recyclerView.getHeight()
                , recyclerView.getWidth()
                , xInit, yInit
                , mScale
                , position
                , windowManager
                , sharedPreferences
                , grid.offsetHorizontal
                , grid.offsetVertical
                , 0);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCircle(NewServiceModel.IconsXY iconsXY, Collection circle, RealmList<Slot> slots) {
        if (collectionViewsMap.get(circle.collectionId) == null) {
            FrameLayout circleView = new FrameLayout(this);
            addIconsToCircleView(slots, circleView);
            collectionViewsMap.put(circle.collectionId, circleView);
        }
        FrameLayout frameLayout = (FrameLayout) collectionViewsMap.get(circle.collectionId);
        for (int i = 0; i < frameLayout.getChildCount(); i++) {
            Utility.setSlotIcon(slots.get(i), this, (ImageView) frameLayout.getChildAt(i), getPackageManager(), iconPack);
            frameLayout.getChildAt(i).setX(iconsXY.xs[i]);
            frameLayout.getChildAt(i).setY(iconsXY.ys[i]);
        }
        if (!frameLayout.isAttachedToWindow()) {
            windowManager.addView(frameLayout, collectionWindowPapams);
        }
        frameLayout.setVisibility(View.VISIBLE);
    }

    private void addIconsToCircleView(RealmList<Slot> slots, FrameLayout circleView) {
        circleView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        for (int i = 0; i < slots.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams((int) (Cons.DEFAULT_ICON_WIDTH * mScale * iconScale), (int) (Cons.DEFAULT_ICON_WIDTH * mScale * iconScale)));
            circleView.addView(imageView);
        }
    }

    @Override
    public void actionDownVibrate() {

    }

    @Override
    public void showClock() {

    }

    @Override
    public void highlightSlot(NewServicePresenter.Showing currentShowing, int id) {

    }

    @Override
    public void unhighlightSlot(NewServicePresenter.Showing currentShowing, int id) {

    }

    @Override
    public void startSlot(Slot slot) {

    }

    @Override
    public void startItem(Item item) {

    }

    @Override
    public void hideAllExceptEdges() {

    }

    @Override
    public void removeAllExceptEdges() {

    }

    @Override
    public void removeAll() {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public class ViewParams {
        public View view;
        public WindowManager.LayoutParams params;
        public SlotsAdapter gridAdapter;

        public ViewParams(View view, WindowManager.LayoutParams params) {
            this.view = view;
            this.params = params;
        }
    }

}