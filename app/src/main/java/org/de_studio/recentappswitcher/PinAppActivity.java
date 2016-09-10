package org.de_studio.recentappswitcher;

import android.Manifest;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class PinAppActivity extends AppCompatActivity {
    private PinAppAdapter adapter;
    private MenuItem spinnerMenu;
    private SharedPreferences sharedPreferences;
    private static final int MY_PERMISSIONS_REQUEST = 22;
    private AlertDialog dialog;
    private ImageView clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        setContentView(R.layout.activity_pin_app);
        ListView listView = (ListView) findViewById(R.id.pinned_shortcut_list_view);
        clearButton = (ImageView) findViewById(R.id.clear_button);


        adapter = new PinAppAdapter(this);
        listView.setAdapter(adapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,final int i, long l) {
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
                                PinRecentAddAppDialogFragment newFragment = PinRecentAddAppDialogFragment.newInstance(i);
                                newFragment.show(fragmentManager, "pinApp");
                                break;
                            case 1:
                                FragmentManager fragmentManager1 = getSupportFragmentManager();
                                PinRecentAddActionDialogFragment newFragment1 = PinRecentAddActionDialogFragment.newInstance(i);
                                newFragment1.show(fragmentManager1, "pinAction");
                                break;
                            case 2:
                                if (Utility.checkContactPermission(getApplicationContext())) {
                                    FragmentManager fragmentManager2 = getSupportFragmentManager();
                                    PinRecentAddContactDialogFragment newFragment2 = PinRecentAddContactDialogFragment.newInstance(i);
                                    newFragment2.show(fragmentManager2, "pinContact");
                                } else {
                                    ActivityCompat.requestPermissions(PinAppActivity.this,
                                            new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE},
                                            MY_PERMISSIONS_REQUEST);
                                }

                                break;
                            case 3:
                                FragmentManager fragmentManager3 = getSupportFragmentManager();
                                PinRecentAddShortcutDialogFragment newFragment3 = PinRecentAddShortcutDialogFragment.newInstance(i);
                                newFragment3.show(fragmentManager3, "pinShortcut");
                                break;
                        }
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClipData data = ClipData.newPlainText("","");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.GONE);
                clearButton.setVisibility(View.VISIBLE);
                adapter.setDragPosition(i);
                return true;
            }
        });

        clearButton.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        v.setVisibility(View.VISIBLE);
                        // do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.setBackgroundResource(R.drawable.delete_button_red);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        v.setBackgroundResource(R.drawable.delete_button_normal);
                        break;
                    case DragEvent.ACTION_DROP:
                        View view = (View) event.getLocalState();
                        view.setVisibility(View.VISIBLE);
                        adapter.removeDragItem();
                        restartService();

                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setBackgroundResource(R.drawable.delete_button_normal);
                        v.setVisibility(View.GONE);
                        View view1 = (View) event.getLocalState();
                        view1.setVisibility(View.VISIBLE);

                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        adapter.notifyDataSetChanged();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu_pin_app, menu);
//        boolean isPinTop = sharedPreferences.getBoolean(EdgeSetting.IS_PIN_TO_TOP_KEY, false);
//        MenuItem istop = menu.findItem(R.id.menu_pin_to_top);
//        MenuItem isBottom = menu.findItem(R.id.menu_pin_to_bottom);
//        Log.e("PinAppActivity", " isTop = " + isPinTop);
//        if (isPinTop) {
//            istop.setChecked(true);
//        } else {
//            isBottom.setChecked(true);
//        }
//        return true;
//
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_pin_to_top:
//                sharedPreferences.edit().putBoolean(EdgeSetting.IS_PIN_TO_TOP_KEY,true).commit();
//                item.setChecked(true);
//                return true;
//            case R.id.menu_pin_to_bottom:
//                sharedPreferences.edit().putBoolean(EdgeSetting.IS_PIN_TO_TOP_KEY,false).commit();
//                item.setChecked(true);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }

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
    private void restartService() {
        if (Utility.checkDrawPermission(getApplicationContext())) {
            Utility.restartService(getApplicationContext());
        }else Toast.makeText(PinAppActivity.this, "Lack of Draw over other apps permission", Toast.LENGTH_SHORT).show();
    }
}
