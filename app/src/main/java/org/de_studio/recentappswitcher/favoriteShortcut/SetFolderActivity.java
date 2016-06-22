package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.ClipData;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import org.de_studio.recentappswitcher.R;

public class SetFolderActivity extends AppCompatActivity implements AddAppToFolderDialogFragment.MyDialogCloseListener{
    private static final String LOG_TAG = SetFolderActivity.class.getSimpleName();
    private int mPosition;
    private FolderAdapter mAdapter;
    private ImageButton deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_folder);
        ListView listView = (ListView) findViewById(R.id.list_view);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mPosition = getIntent().getIntExtra("position", 0);
        mAdapter = new FolderAdapter(this, mPosition);
        deleteButton = (ImageButton) findViewById(R.id.delete_image_button);
        if (listView != null) {
            listView.setDivider(null);
            listView.setAdapter(mAdapter);
        }

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence[] items = new CharSequence[]{getString(R.string.apps),
                            getString(R.string.actions),
                            getString(R.string.contacts),
                            getString(R.string.shortcut)};
                    AlertDialog.Builder builder = new AlertDialog.Builder(SetFolderActivity.this);
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    AddAppToFolderDialogFragment newFragment = new AddAppToFolderDialogFragment();
                                    newFragment.setmPosition(mPosition);
                                    newFragment.show(fragmentManager, "addAppToFolder");
                                    break;
                                case 1:
                                    FragmentManager fragmentManager1 = getSupportFragmentManager();
                                    AddActionToFolderDialogFragment newFragment1 = new AddActionToFolderDialogFragment();
                                    newFragment1.setmPosition(mPosition);
                                    newFragment1.show(fragmentManager1, "addActionToFolder");
                                    break;
                                case 2:
                                    FragmentManager fragmentManager2 = getSupportFragmentManager();
                                    AddContactToFolderDialogFragment newFragment2 = new AddContactToFolderDialogFragment();
                                    newFragment2.setmPosition(mPosition);
                                    newFragment2.show(fragmentManager2, "addContactToFolder");
                                    break;
                                case 3:
                                    FragmentManager fragmentManager3 = getSupportFragmentManager();
                                    AddShortcutToFolderDialogFragment newFragment3 = new AddShortcutToFolderDialogFragment();
                                    newFragment3.setmPosition(mPosition);
                                    newFragment3.show(fragmentManager3, "addShortcutToFolder");
                                    break;
                            }
                        }
                    });
                    builder.create().show();
                }
            });
        }
        if (listView != null) {
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    ClipData data = ClipData.newPlainText("","");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    view.setVisibility(View.GONE);
                    deleteButton.setVisibility(View.VISIBLE);
                    mAdapter.setDragPosition(position);
                    return true;
                }
            });
        }

        deleteButton.setOnDragListener(new View.OnDragListener() {
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
                        mAdapter.removeDragItem();

                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setBackgroundResource(R.drawable.delete_button_normal);
                        v.setVisibility(View.GONE);

                    default:
                        break;
                }
                return true;
            }
        });

    }



    @Override
    public void handleDialogClose() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
