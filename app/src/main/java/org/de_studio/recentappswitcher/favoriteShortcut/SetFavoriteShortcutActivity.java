package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.service.EdgeSettingDialogFragment;

public class SetFavoriteShortcutActivity extends AppCompatActivity {
    private FavoriteShortcutAdapter mAdapter;
    private static final String LOG_TAG = SetFavoriteShortcutActivity.class.getSimpleName();
    private float mScale;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_favorite_shortcut);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences defaultSharedPreference = getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        int gridRow = defaultSharedPreference.getInt(EdgeSettingDialogFragment.NUM_OF_GRID_ROW_KEY, 5);
        int gridColumn = defaultSharedPreference.getInt(EdgeSettingDialogFragment.NUM_OF_GRID_COLUMN_KEY, 4);
        int shortcutGap = defaultSharedPreference.getInt(EdgeSettingDialogFragment.GAP_OF_SHORTCUT_KEY, 22);
        mScale = getResources().getDisplayMetrics().density;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GridView gridView = (GridView) findViewById(R.id.favorite_shortcut_grid_view);
        ViewGroup.LayoutParams gridParams = gridView.getLayoutParams();
        gridParams.height = (int)(mScale* (float)(48 * gridRow + shortcutGap* (gridRow -1)));
        gridParams.width = (int)(mScale* (float)(48 * gridColumn + shortcutGap* (gridColumn -1)));
        gridView.setLayoutParams(gridParams);
        mAdapter = new FavoriteShortcutAdapter(this);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ChooseShortcutActivity.class);
                intent.addFlags(position);
                startActivity(intent);
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e(LOG_TAG, "onResume");
        mAdapter.notifyDataSetChanged();
    }

}
