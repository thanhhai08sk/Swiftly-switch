package org.de_studio.recentappswitcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.de_studio.recentappswitcher.service.EdgeGestureService;

public class MainActivity extends AppCompatActivity {
    public static final String DEFAULT_SHAREDPREFERENCE = "org.de-studio.recentappswitcher_sharedpreference";
    public static final String IS_STEP1_OK_PREFERENCE = "isStep1Ok";
    public static final String IS_STEP2_OK_PREFERENCE = "isStep2Ok";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Button step1Button = (Button) findViewById(R.id.step1_button);
        final Button step2Button = (Button) findViewById(R.id.step2_button);
        final TextView step1Text = (TextView) findViewById(R.id.step_1_text);
        final TextView step2Text = (TextView) findViewById(R.id.step_2_text);
        final Button step1GoToSettingButton = (Button) findViewById(R.id.step1_go_to_setting_button);
        final Button step2GoToSettingButton = (Button) findViewById(R.id.step2_go_to_setting_button);
        final FrameLayout stepTextFrame = (FrameLayout) findViewById(R.id.step_text_frame_layout);
        SharedPreferences sharedPreferences = getSharedPreferences(DEFAULT_SHAREDPREFERENCE, 0);
        boolean isStep1Ok;
        boolean isStep2Ok;
        isStep2Ok = sharedPreferences.getBoolean(IS_STEP2_OK_PREFERENCE,false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            isStep1Ok = sharedPreferences.getBoolean(IS_STEP1_OK_PREFERENCE,true);
            step1Text.setText(R.string.main_step1_text_for_kitkat_and_below);
            step1Text.setPadding(0,0,0,0);
            step1GoToSettingButton.setVisibility(View.GONE);

        }else {
            isStep1Ok = sharedPreferences.getBoolean(IS_STEP1_OK_PREFERENCE,false);
        }
        if (isStep1Ok){
            step1Button.setBackground(ContextCompat. getDrawable(getApplicationContext(), R.drawable.arrow_button_green));

        }else step1Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_red));
        if (isStep2Ok){
            step2Button.setBackground(ContextCompat. getDrawable(getApplicationContext(), R.drawable.arrow_button_green));
        }else step2Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_red));


        step1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step1Text.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    step1GoToSettingButton.setVisibility(View.VISIBLE);
                }
                step2Text.setVisibility(View.GONE);
                step2GoToSettingButton.setVisibility(View.GONE);
                stepTextFrame.setBackground(ContextCompat. getDrawable(getApplicationContext(), R.drawable.text_board_1));
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
}
