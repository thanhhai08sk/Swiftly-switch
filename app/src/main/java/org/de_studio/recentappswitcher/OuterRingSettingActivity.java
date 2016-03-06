package org.de_studio.recentappswitcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

public class OuterRingSettingActivity extends AppCompatActivity {
    private static final String LOG_TAG = OuterRingSettingActivity.class.getSimpleName();
    private ListView mListView;
    private OuterRingAdapter mAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outter_ring_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.outer_ring_setting_list_view);
        mAdapter = new OuterRingAdapter(this);
        mListView.setAdapter(mAdapter);

    }

}
