package org.de_studio.recentappswitcher;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import org.de_studio.recentappswitcher.service.EdgeGestureService;
import org.de_studio.recentappswitcher.service.EdgeSettingDialogFragment;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String EDGE_1_SHAREDPREFERENCE = "org.de_studio.recentappswitcher_edge_1_shared_preference";
    public static final String EDGE_2_SHAREDPREFERENCE = "org.de_studio.recentappswitcher_edge_2_shared_preference";
    public static final String DEFAULT_SHAREDPREFERENCE = "org.de_studio.recentappswitcher_sharedpreferences";
    private SharedPreferences sharedPreferences1, sharedPreferences2, sharedPreferencesDefautl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences1 = getSharedPreferences(EDGE_1_SHAREDPREFERENCE, 0);
        sharedPreferences2 = getSharedPreferences(EDGE_2_SHAREDPREFERENCE, 0);
        sharedPreferencesDefautl = getSharedPreferences(DEFAULT_SHAREDPREFERENCE, 0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Button step1Button = (Button) findViewById(R.id.step1_button);
        Switch edge1Switch = (Switch) findViewById(R.id.edge_1_switch);
        Switch edge2Switch = (Switch) findViewById(R.id.edge_2_switch);
        edge1Switch.setChecked(sharedPreferences1.getBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, true));
        edge2Switch.setChecked(sharedPreferences2.getBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, false));
        edge1Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences1.edit().putBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, isChecked).commit();

                stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                startService(new Intent(getApplicationContext(), EdgeGestureService.class));

            }
        });
        edge2Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences2.edit().putBoolean(EdgeSettingDialogFragment.EDGE_ON_KEY, isChecked).commit();

                stopService(new Intent(getApplicationContext(), EdgeGestureService.class));
                startService(new Intent(getApplicationContext(), EdgeGestureService.class));

            }
        });
        final Button step2Button = (Button) findViewById(R.id.step2_button);
        final TextView step1Text = (TextView) findViewById(R.id.step_1_text);
        final TextView step2Text = (TextView) findViewById(R.id.step_2_text);
        final Button step1GoToSettingButton = (Button) findViewById(R.id.step1_go_to_setting_button);
        final Button step2GoToSettingButton = (Button) findViewById(R.id.step2_go_to_setting_button);
        final FrameLayout stepTextFrame = (FrameLayout) findViewById(R.id.step_text_frame_layout);
        ImageButton edge1SettingButton = (ImageButton) findViewById(R.id.edge_1_setting_image_button);
        ImageButton edge2SettingButton = (ImageButton) findViewById(R.id.edge_2_setting_image_button);
        edge1SettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                EdgeSettingDialogFragment newFragment = new EdgeSettingDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(EdgeSettingDialogFragment.EDGE_NUMBER_KEY, 1);
                newFragment.setArguments(bundle);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, newFragment)
                        .addToBackStack(null).commit();

            }
        });
        edge2SettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                EdgeSettingDialogFragment newFragment = new EdgeSettingDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(EdgeSettingDialogFragment.EDGE_NUMBER_KEY, 2);
                newFragment.setArguments(bundle);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, newFragment)
                        .addToBackStack(null).commit();
            }
        });
        boolean isStep1Ok;
        boolean isStep2Ok;
        AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        isStep2Ok = manager.isEnabled();
        Log.e(LOG_TAG, "isStep2OK = " + isStep2Ok);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            isStep1Ok = true;
            step1Text.setText(R.string.main_step1_text_for_kitkat_and_below);
            step1Text.setPadding(0, 0, 0, 0);
            step1GoToSettingButton.setVisibility(View.GONE);

        } else {
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), getPackageName());
            isStep1Ok = mode == AppOpsManager.MODE_ALLOWED;
        }
        if (isStep1Ok) {
            step1Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_green));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                step1Text.setText(R.string.main_step1_text_success);
            }

        } else
            step1Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_red));
        if (isStep2Ok) {
            step2Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_green));
            step2Text.setText(R.string.main_step2_text_success);
        } else {
            step2Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_red));
        }


        step1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step1Text.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    step1GoToSettingButton.setVisibility(View.VISIBLE);
                }
                step2Text.setVisibility(View.GONE);
                step2GoToSettingButton.setVisibility(View.GONE);
                stepTextFrame.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.text_board_1));
            }
        });
        step2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step1Text.setVisibility(View.GONE);
                step1GoToSettingButton.setVisibility(View.GONE);
                step2Text.setVisibility(View.VISIBLE);
                step2GoToSettingButton.setVisibility(View.VISIBLE);
                stepTextFrame.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.text_board_2_));
            }
        });

        step1GoToSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
        });
        step2GoToSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });
        setSupportActionBar(toolbar);
        final Context context = this;


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                showDialog();

            }
        });
        startService(new Intent(this, EdgeGestureService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        EdgeSettingDialogFragment newFragment = new EdgeSettingDialogFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment)
                .addToBackStack(null).commit();
    }


}
