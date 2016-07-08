package org.de_studio.recentappswitcher;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import org.de_studio.recentappswitcher.service.EdgeSetting;

public class PinAppActivity extends AppCompatActivity {
    private PinAppAdapter adapter;
    private MenuItem spinnerMenu;
    private SharedPreferences sharedPreferences;
    private static final int MY_PERMISSIONS_REQUEST = 22;
    private AlertDialog dialog;

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
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    android.app.FragmentManager fragmentManager = getFragmentManager();
//                    PinAppDialogFragment newFragment = new PinAppDialogFragment();
//                    newFragment.show(fragmentManager, "pinAppDialogFragment");



                    CharSequence[] items = new CharSequence[]{getString(R.string.apps),
                            getString(R.string.actions),
                            getString(R.string.contacts),
                            getString(R.string.shortcut)};
                    AlertDialog.Builder builder = new AlertDialog.Builder(PinAppActivity.this);
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    PinRecentAddAppDialogFragment newFragment = new PinRecentAddAppDialogFragment();
                                    newFragment.show(fragmentManager, "pinApp");
                                    break;
                                case 1:
                                    FragmentManager fragmentManager1 = getSupportFragmentManager();
                                    PinRecentAddActionDialogFragment newFragment1 = new PinRecentAddActionDialogFragment();
                                    newFragment1.show(fragmentManager1, "pinAction");
                                    break;
                                case 2:
                                    if (Utility.checkContactPermission(getApplicationContext())) {
                                        FragmentManager fragmentManager2 = getSupportFragmentManager();
                                        PinRecentAddContactDialogFragment newFragment2 = new PinRecentAddContactDialogFragment();
                                        newFragment2.show(fragmentManager2, "pinContact");
                                    } else {
                                        ActivityCompat.requestPermissions(PinAppActivity.this,
                                                new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE},
                                                MY_PERMISSIONS_REQUEST);
                                    }

                                    break;
                                case 3:
                                    FragmentManager fragmentManager3 = getSupportFragmentManager();
                                    PinRecentAddShortcutDialogFragment newFragment3 = new PinRecentAddShortcutDialogFragment();
                                    newFragment3.show(fragmentManager3, "pinShortcut");
                                    break;
                            }
                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                }
            });
        }
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
        boolean isPinTop = sharedPreferences.getBoolean(EdgeSetting.IS_PIN_TO_TOP_KEY, false);
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
                sharedPreferences.edit().putBoolean(EdgeSetting.IS_PIN_TO_TOP_KEY,true).commit();
                item.setChecked(true);
                return true;
            case R.id.menu_pin_to_bottom:
                sharedPreferences.edit().putBoolean(EdgeSetting.IS_PIN_TO_TOP_KEY,false).commit();
                item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dialog.show();
                }
            }
        }
    }
}
