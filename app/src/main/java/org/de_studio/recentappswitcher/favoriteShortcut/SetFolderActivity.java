package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import org.de_studio.recentappswitcher.R;

public class SetFolderActivity extends AppCompatActivity {
    private int mPosition;
    private FolderAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_folder);
        ListView listView = (ListView) findViewById(R.id.list_view);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mPosition = getIntent().getIntExtra("position", 0);
        mAdapter = new FolderAdapter(this, mPosition);
        if (listView != null) {
            listView.setAdapter(mAdapter);
        }

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence[] items = new CharSequence[]{getString(R.string.apps),
                            getString(R.string.actions),
                            getString(R.string.contacts)};
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
                                    break;
                                case 2:
                                    break;
                            }
                        }
                    });
                    builder.create().show();
                }
            });
        }

    }
}
