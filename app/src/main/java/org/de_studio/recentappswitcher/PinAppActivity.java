package org.de_studio.recentappswitcher;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

public class PinAppActivity extends AppCompatActivity {
    private PinAppAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
