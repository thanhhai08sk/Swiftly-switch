package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.service.EdgeGestureService;
import org.de_studio.recentappswitcher.service.EdgeSettingDialogFragment;

public class SetFavoriteShortcutActivity extends AppCompatActivity {
    private FavoriteShortcutAdapter mAdapter;
    private static final String LOG_TAG = SetFavoriteShortcutActivity.class.getSimpleName();
    private float mScale, mIconScale;
    private boolean isTrial = false;
    private GridView gridView;
    private SharedPreferences defaultSharedPreference;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getPackageName().equals(MainActivity.FREE_VERSION_PACKAGE_NAME)) isTrial = true;
        setContentView(R.layout.activity_set_favorite_shortcut);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        defaultSharedPreference = getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        int gridRow = defaultSharedPreference.getInt(EdgeSettingDialogFragment.NUM_OF_GRID_ROW_KEY, 5);
        int gridColumn = defaultSharedPreference.getInt(EdgeSettingDialogFragment.NUM_OF_GRID_COLUMN_KEY, 4);
        int shortcutGap = defaultSharedPreference.getInt(EdgeSettingDialogFragment.GAP_OF_SHORTCUT_KEY, 5);
        mScale = getResources().getDisplayMetrics().density;
        mIconScale = defaultSharedPreference.getFloat(EdgeSettingDialogFragment.ICON_SCALE, 1f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gridView = (GridView) findViewById(R.id.favorite_shortcut_grid_view);
        AppCompatSpinner gridRowSpinner = (AppCompatSpinner) findViewById(R.id.set_favorite_shortcut_grid_row_spinner);
        AppCompatSpinner gridColumnSpinner = (AppCompatSpinner) findViewById(R.id.set_favorite_shortcut_grid_column_spinner);
        if (isTrial) {
            gridColumnSpinner.setEnabled(false);
            gridRowSpinner.setEnabled(false);
        }
        final TextView gridGapValueTextView = (TextView) findViewById(R.id.set_favorite_shortcut_grid_gap_value_text_view);
        AppCompatSeekBar gridGapSeekBar = (AppCompatSeekBar) findViewById(R.id.favorite_shortcut_grid_gap_seek_bar);
        final TextView gridDistanceValueTextView = (TextView) findViewById(R.id.set_favorite_shortcut_grid_distance_value_text_view);
        final AppCompatSeekBar gridDistanceSeekBar = (AppCompatSeekBar) findViewById(R.id.favorite_shortcut_grid_distance_seek_bar);
        final AppCompatSeekBar gridDistanceVerticalSeekBar = (AppCompatSeekBar) findViewById(R.id.favorite_shortcut_grid_distance_vertical_seek_bar);
        final TextView gridDistanceVerticalValueTextView = (TextView) findViewById(R.id.set_favorite_shortcut_grid_distance_vertical_value_text_view);

        int currentRowSpinnerPosition = defaultSharedPreference.getInt(EdgeSettingDialogFragment.NUM_OF_GRID_ROW_KEY,5)-1;
        gridRowSpinner.setSelection(currentRowSpinnerPosition);
        gridRowSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                defaultSharedPreference.edit().putInt(EdgeSettingDialogFragment.NUM_OF_GRID_ROW_KEY, position + 1).commit();
                updateGridView();
                getApplicationContext().stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                getApplicationContext().startService(new Intent(getApplicationContext(), EdgeGestureService.class));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int currentColumnSpinnerPosition = defaultSharedPreference.getInt(EdgeSettingDialogFragment.NUM_OF_GRID_COLUMN_KEY,4)-1;
        gridColumnSpinner.setSelection(currentColumnSpinnerPosition);
        gridColumnSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                defaultSharedPreference.edit().putInt(EdgeSettingDialogFragment.NUM_OF_GRID_COLUMN_KEY, position + 1).commit();
                updateGridView();
                getApplicationContext().stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                getApplicationContext().startService(new Intent(getApplicationContext(), EdgeGestureService.class));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        int currentGridGapSeekBarProgress = defaultSharedPreference.getInt(EdgeSettingDialogFragment.GAP_OF_SHORTCUT_KEY,5);
        gridGapSeekBar.setProgress(currentGridGapSeekBarProgress);
        gridGapValueTextView.setText(currentGridGapSeekBarProgress + 10 + " dp");
        gridGapSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                defaultSharedPreference.edit().putInt(EdgeSettingDialogFragment.GAP_OF_SHORTCUT_KEY, progress).commit();
                gridGapValueTextView.setText(progress + 10 + " dp");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateGridView();
                getApplicationContext().stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                getApplicationContext().startService(new Intent(getApplicationContext(), EdgeGestureService.class));
            }
        });

        int currentGridDistanceSeekBarProgress = defaultSharedPreference.getInt(EdgeSettingDialogFragment.GRID_DISTANCE_FROM_EDGE_KEY,20) - 20;
        int currentGridDistanceVerticalSeekBarProgress = defaultSharedPreference.getInt(EdgeSettingDialogFragment.GRID_DISTANCE_VERTICAL_FROM_EDGE_KEY, 20);
        gridDistanceSeekBar.setProgress(currentGridDistanceSeekBarProgress);
        gridDistanceVerticalSeekBar.setProgress(currentGridDistanceVerticalSeekBarProgress);
        gridDistanceValueTextView.setText(currentGridDistanceSeekBarProgress + 20 + " dp");
        gridDistanceVerticalValueTextView.setText(currentGridDistanceVerticalSeekBarProgress +" dp");
        gridDistanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                defaultSharedPreference.edit().putInt(EdgeSettingDialogFragment.GRID_DISTANCE_FROM_EDGE_KEY, progress + 20).commit();
                gridDistanceValueTextView.setText(progress + 20 + " dp");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        gridDistanceVerticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                defaultSharedPreference.edit().putInt(EdgeSettingDialogFragment.GRID_DISTANCE_VERTICAL_FROM_EDGE_KEY,progress).commit();
                gridDistanceVerticalValueTextView.setText(progress + " dp");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        gridDistanceSeekBar.setEnabled(!defaultSharedPreference.getBoolean(EdgeSettingDialogFragment.IS_CENTRE_FAVORITE,false));
        gridDistanceVerticalSeekBar.setEnabled(!defaultSharedPreference.getBoolean(EdgeSettingDialogFragment.IS_CENTRE_FAVORITE,false));
        Switch placeToCenterSwitch = (Switch) findViewById(R.id.set_favorite_place_center);
        placeToCenterSwitch.setChecked(defaultSharedPreference.getBoolean(EdgeSettingDialogFragment.IS_CENTRE_FAVORITE,false));
        placeToCenterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                defaultSharedPreference.edit().putBoolean(EdgeSettingDialogFragment.IS_CENTRE_FAVORITE,isChecked).commit();
                gridDistanceSeekBar.setEnabled(!isChecked);
                gridDistanceVerticalSeekBar.setEnabled(!isChecked);
            }
        });

        gridView.setVerticalSpacing((int) (shortcutGap * mScale));
        gridView.setNumColumns(gridColumn);
        ViewGroup.LayoutParams gridParams = gridView.getLayoutParams();
        gridParams.height = (int)(mScale* (float)((EdgeGestureService.GRID_ICON_SIZE* mIconScale + EdgeGestureService.GRID_2_PADDING) * gridRow + shortcutGap* (gridRow -1)));
        gridParams.width = (int)(mScale* (float)((EdgeGestureService.GRID_ICON_SIZE * mIconScale + EdgeGestureService.GRID_2_PADDING) * gridColumn + shortcutGap* (gridColumn -1)));
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
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ClipData data = ClipData.newPlainText("","");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.GONE);
                mAdapter.setDragPosition(position);
                return true;
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e(LOG_TAG, "onResume");
        mAdapter.notifyDataSetChanged();
    }

    private void updateGridView() {
        ViewGroup.LayoutParams gridParams = gridView.getLayoutParams();
        int gridRow = defaultSharedPreference.getInt(EdgeSettingDialogFragment.NUM_OF_GRID_ROW_KEY, 5);
        int gridColumn = defaultSharedPreference.getInt(EdgeSettingDialogFragment.NUM_OF_GRID_COLUMN_KEY, 4);
        int gridGap = defaultSharedPreference.getInt(EdgeSettingDialogFragment.GAP_OF_SHORTCUT_KEY, 5);
        gridView.setVerticalSpacing((int)( gridGap*mScale));
        gridView.setNumColumns(gridColumn);
        gridParams.height = (int) (mScale * (float) ((EdgeGestureService.GRID_ICON_SIZE * mIconScale + EdgeGestureService.GRID_2_PADDING) * gridRow + gridGap * (gridRow - 1)));
        gridParams.width = (int) (mScale * (float) ((EdgeGestureService.GRID_ICON_SIZE *mIconScale + EdgeGestureService.GRID_2_PADDING) * gridColumn + gridGap * (gridColumn - 1)));
        gridView.setLayoutParams(gridParams);
    }

}
