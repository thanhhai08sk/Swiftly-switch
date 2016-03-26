package org.de_studio.recentappswitcher;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class PinAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.FragmentManager fragmentManager = getFragmentManager();
                FavoriteOrExcludeDialogFragment newFragment = new FavoriteOrExcludeDialogFragment();
                newFragment.show(fragmentManager, "excludeDialogFragment");
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
