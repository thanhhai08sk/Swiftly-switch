package org.de_studio.recentappswitcher;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import org.de_studio.recentappswitcher.service.EdgeSettingDialogFragment;

public class PinAppActivity extends AppCompatActivity {
    private PinAppAdapter adapter;
    private MenuItem spinnerMenu;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        setContentView(R.layout.activity_pin_app);
        DragSortListView listView = (DragSortListView) findViewById(R.id.drag_list_view);
        DragSortController controller = new DragSortController(listView);
        controller.setDragHandleId(R.id.pin_app_list_item_dragger);
        controller.setRemoveEnabled(true);
        controller.setBackgroundColor(Color.TRANSPARENT);
        listView.setFloatViewManager(controller);
        listView.setOnTouchListener(controller);
        listView.setFloatViewManager(controller);
        listView.setOnTouchListener(controller);
        listView.setDragEnabled(true);
        adapter = new PinAppAdapter(this);
        listView.setAdapter(adapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.FragmentManager fragmentManager = getFragmentManager();
                PinAppDialogFragment newFragment = new PinAppDialogFragment();
                newFragment.show(fragmentManager, "pinAppDialogFragment");
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_pin_app, menu);
        boolean isPinTop = sharedPreferences.getBoolean(EdgeSettingDialogFragment.IS_PIN_TO_TOP_KEY, false);
        MenuItem istop = menu.findItem(R.id.menu_pin_to_top);
        MenuItem isBottom = menu.findItem(R.id.menu_pin_to_bottom);
        Log.e("PinAppActivity", " isTop = " + isPinTop);
        if (isPinTop) {
            istop.setChecked(true);
        } else {
            isBottom.setChecked(true);
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_pin_to_top:
                sharedPreferences.edit().putBoolean(EdgeSettingDialogFragment.IS_PIN_TO_TOP_KEY,true).commit();
                item.setChecked(true);
                return true;
            case R.id.menu_pin_to_bottom:
                sharedPreferences.edit().putBoolean(EdgeSettingDialogFragment.IS_PIN_TO_TOP_KEY,false).commit();
                item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
