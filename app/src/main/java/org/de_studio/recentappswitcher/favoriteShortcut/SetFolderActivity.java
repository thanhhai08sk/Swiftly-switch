package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
                    Intent intent = new Intent(getApplicationContext(), ChooseShortcutActivity.class);
                    intent.addFlags(mPosition);
                    intent.putExtra("mode", FavoriteSettingActivity.MODE_FOLDER);
                    startActivity(intent);
                }
            });
        }

    }
}
