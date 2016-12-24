package org.de_studio.recentappswitcher.edgeService;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.SlotsAdapter;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import io.realm.RealmList;

import static org.de_studio.recentappswitcher.Cons.EDGE_1_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_VIEW_NAME;

/**
 * Created by HaiNguyen on 12/23/16.
 */

public class NewServiceView extends Service implements NewServicePresenter.View {
    private static final String TAG = NewServiceView.class.getSimpleName();
    public static boolean FLASH_LIGHT_ON = false;
    @Inject
    IconPackManager.IconPack iconPack;
    @Inject
    @Named(Cons.GRID_PARENT_VIEW_PARA_NAME)
    WindowManager.LayoutParams collectionWindowPapams;
    @Inject
    @Named(Cons.CLOCK_PARENTS_VIEW_NAME)
    FrameLayout backgroundView;
    @Inject
    @Named(EDGE_1_VIEW_NAME)
    View edge1View;
    @Inject
    @Named(EDGE_2_VIEW_NAME)
    View edge2View;
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
        if (!backgroundView.isAttachedToWindow()) {
            windowManager.addView(backgroundView, collectionWindowPapams);
        }
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
        if (!sharedPreferences.getBoolean(Cons.DISABLE_HAPTIC_FEEDBACK_KEY, true)) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(sharedPreferences.getInt(Cons.VIBRATION_DURATION_KEY, Cons.DEFAULT_VIBRATE_DURATION));
        }
    }

    @Override
    public void actionMoveVibrate() {
        if (sharedPreferences.getBoolean(Cons.HAPTIC_ON_ICON_KEY, false)) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(sharedPreferences.getInt(Cons.VIBRATION_DURATION_KEY, Cons.DEFAULT_VIBRATE_DURATION));
        }
    }

    @Override
    public void showClock() {
        backgroundView.findViewById(R.id.indicator_frame_layout).setVisibility(View.GONE);
        if (!sharedPreferences.getBoolean(Cons.DISABLE_CLOCK_KEY,false)) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMMM");
            backgroundView.findViewById(R.id.clock_linear_layout).setVisibility(View.VISIBLE);
            TextView hourTextView = (TextView) backgroundView.findViewById(R.id.clock_time_in_hour);
            TextView dateTextView = (TextView) backgroundView.findViewById(R.id.clock_time_in_date);
            TextView batteryLifeTextView = (TextView) backgroundView.findViewById(R.id.clock_battery_life);
            String batteryString = getApplicationContext().getString(R.string.batterylife) + " " + Utility.getBatteryLevel(getApplicationContext()) + "%";
            if (batteryLifeTextView != null) {
                batteryLifeTextView.setText(batteryString);
            }
            if (dateTextView != null) {
                dateTextView.setText(dateFormat.format(c.getTime()));
            }
            if (!DateFormat.is24HourFormat(getApplicationContext())) {
                SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm");
                if (hourTextView != null) {
                    hourTextView.setText(hourFormat.format(c.getTime()));
                }
            } else {
                SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
                if (hourTextView != null) {
                    hourTextView.setText(hourFormat.format(c.getTime()));
                }
            }
        }
    }

    @Override
    public void highlightSlot(NewServicePresenter.Showing currentShowing, int id) {

    }

    @Override
    public void unhighlightSlot(NewServicePresenter.Showing currentShowing, int id) {

    }

    @Override
    public void startSlot(Slot slot, String lastApp) {
        Utility.startSlot(slot, lastApp, this, sharedPreferences.getInt(Cons.CONTACT_ACTION, Cons.DEFAULT_CONTACT_ACTION));
    }

    @Override
    public void startItem(Item item, String lastApp) {
        Utility.startItem(item, lastApp, this, sharedPreferences.getInt(Cons.CONTACT_ACTION, Cons.DEFAULT_CONTACT_ACTION));
    }

    @Override
    public void hideAllExceptEdges() {
        Set<String> collectionIds = collectionViewsMap.keySet();
        for (String collectionId : collectionIds) {
            collectionViewsMap.get(collectionId).setVisibility(View.GONE);
        }

        if (backgroundView != null) {
            backgroundView.setVisibility(View.GONE);
        }
    }

    @Override
    public synchronized void removeAllExceptEdges() {
        Set<String> collectionIds = collectionViewsMap.keySet();
        for (String collectionId : collectionIds) {
            try {
                windowManager.removeView(collectionViewsMap.get(collectionId));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "removeAllExceptEdges: can not remove " + collectionId);
            }
        }

        if (backgroundView != null && backgroundView.isAttachedToWindow()) {
            windowManager.removeView(backgroundView);
        }
    }

    @Override
    public synchronized void removeAll() {
        if (edge1View != null) {
            edge1View.setOnTouchListener(null);
            if (edge1View.isAttachedToWindow()) {
                windowManager.removeView(edge1View);
            }
        }

        if (edge2View != null) {
            edge2View.setOnClickListener(null);
            if (edge2View.isAttachedToWindow()) {
                windowManager.removeView(edge2View);
            }
        }
        removeAllExceptEdges();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }


}
