package org.de_studio.recentappswitcher;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.de_studio.recentappswitcher.service.EdgeGestureService;
import org.de_studio.recentappswitcher.service.EdgeSettingDialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String EDGE_1_SHAREDPREFERENCE = "org.de_studio.recentappswitcher_edge_1_shared_preference";
    public static final String EDGE_2_SHAREDPREFERENCE = "org.de_studio.recentappswitcher_edge_2_shared_preference";
    public static final String DEFAULT_SHAREDPREFERENCE = "org.de_studio.recentappswitcher_sharedpreferences";
    public static final String FAVORITE_SHAREDPREFERENCE = "org.de_studio.recentappswitcher_favorite_shared_preferences";
    public static final String EXCLUDE_SHAREDPREFERENCE = "org.de_studio.recentappswitcher_exclude_shared_preferences";
    private SharedPreferences sharedPreferences1, sharedPreferences2, sharedPreferencesDefautl,sharedPreferences_favorite, sharedPreferences_exclude;
    private ArrayList<AppInfors> mAppInforsArrayList;
    Button step1Button;
    TextView step1Text;
    TextView step2Text;
    Button step2Button;
    Button step1GoToSettingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new LoadInstalledApp().execute();
        sharedPreferences1 = getSharedPreferences(EDGE_1_SHAREDPREFERENCE, 0);
        sharedPreferences2 = getSharedPreferences(EDGE_2_SHAREDPREFERENCE, 0);
        sharedPreferencesDefautl = getSharedPreferences(DEFAULT_SHAREDPREFERENCE, 0);
        sharedPreferences_favorite = getSharedPreferences(FAVORITE_SHAREDPREFERENCE,0);
        sharedPreferences_exclude = getSharedPreferences(EXCLUDE_SHAREDPREFERENCE,0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        step1Button = (Button) findViewById(R.id.step1_button);
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
        step2Button = (Button) findViewById(R.id.step2_button);
        step1Text = (TextView) findViewById(R.id.step_1_text);
        step2Text = (TextView) findViewById(R.id.step_2_text);
        step1GoToSettingButton = (Button) findViewById(R.id.step1_go_to_setting_button);
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
        setStepButtons();


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

        ImageButton addFavoriteButton = (ImageButton) findViewById(R.id.main_favorite_add_app_image_button);
        addFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                AddFavoriteAppsDialogFragment newFragment = new AddFavoriteAppsDialogFragment();
                if (mAppInforsArrayList != null) {
                    newFragment.setAppInforsArrayList(mAppInforsArrayList, AddFavoriteAppsDialogFragment.FAVORITE_MODE);
                    newFragment.show(fragmentManager, "addAppDialog");
                }

            }
        });
        setFavoriteView();

        ImageButton editExcludeButton = (ImageButton) findViewById(R.id.main_exclude_edit_image_button);
        editExcludeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                AddFavoriteAppsDialogFragment newFragment = new AddFavoriteAppsDialogFragment();
                if (mAppInforsArrayList != null) {
                    newFragment.setAppInforsArrayList(mAppInforsArrayList, AddFavoriteAppsDialogFragment.EXCLUDE_MODE);
                    newFragment.show(fragmentManager, "excludeDialogFragment");
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();
        setStepButtons();
    }

    public void showDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        EdgeSettingDialogFragment newFragment = new EdgeSettingDialogFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment)
                .addToBackStack(null).commit();
    }

    public void setFavoriteView(){
        PackageManager packageManager = getPackageManager();
        LinearLayout[] linearLayouts = new LinearLayout[6];
        linearLayouts[0] = (LinearLayout) findViewById(R.id.favorite_app_0_linear_layout);
        linearLayouts[1] = (LinearLayout) findViewById(R.id.favorite_app_1_linear_layout);
        linearLayouts[2] = (LinearLayout) findViewById(R.id.favorite_app_2_linear_layout);
        linearLayouts[3] = (LinearLayout) findViewById(R.id.favorite_app_3_linear_layout);
        linearLayouts[4] = (LinearLayout) findViewById(R.id.favorite_app_4_linear_layout);
        linearLayouts[5] = (LinearLayout) findViewById(R.id.favorite_app_5_linear_layout);
        ImageView[] icons = new ImageView[6];
        icons[0] = (ImageView) findViewById(R.id.favorite_app_0_item_image_view);
        icons[1] = (ImageView) findViewById(R.id.favorite_app_1_item_image_view);
        icons[2] = (ImageView) findViewById(R.id.favorite_app_2_item_image_view);
        icons[3] = (ImageView) findViewById(R.id.favorite_app_3_item_image_view);
        icons[4] = (ImageView) findViewById(R.id.favorite_app_4_item_image_view);
        icons[5] = (ImageView) findViewById(R.id.favorite_app_5_item_image_view);
        TextView[] labels = new TextView[6];
        labels[0] = (TextView) findViewById(R.id.favorite_app_0_item_label_text_view);
        labels[1] = (TextView) findViewById(R.id.favorite_app_1_item_label_text_view);
        labels[2] = (TextView) findViewById(R.id.favorite_app_2_item_label_text_view);
        labels[3] = (TextView) findViewById(R.id.favorite_app_3_item_label_text_view);
        labels[4] = (TextView) findViewById(R.id.favorite_app_4_item_label_text_view);
        labels[5] = (TextView) findViewById(R.id.favorite_app_5_item_label_text_view);
        Set<String> set = sharedPreferences_favorite.getStringSet(EdgeSettingDialogFragment.FAVORITE_KEY,new HashSet<String>());
        String[] favoritePackageName = new String[set.size()];
        set.toArray(favoritePackageName);
        for (int i = 0; i< 6; i++){
            if (i< favoritePackageName.length){
                try {
                    labels[i].setText(packageManager.getApplicationLabel(packageManager.getApplicationInfo(favoritePackageName[i],0)));
                    icons[i].setImageDrawable(packageManager.getApplicationIcon(favoritePackageName[i]));
                    linearLayouts[i].setVisibility(View.VISIBLE);
                }catch (PackageManager.NameNotFoundException e){
                    Log.e(LOG_TAG,"name not found");
                }

            }else {
                linearLayouts[i].setVisibility(View.GONE);
            }
        }
    }

    public void setStepButtons(){
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

        } else {
            step1Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_red));
            step1Text.setText(R.string.main_step1_text);
        }

        if (isStep2Ok) {
            step2Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_green));
            step2Text.setText(R.string.main_step2_text_success);
        } else {
            step2Button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arrow_button_red));
            step2Text.setText(R.string.main_step2_text);
        }
    }


    private class LoadInstalledApp extends AsyncTask<Void, Void, ArrayList<AppInfors>> {
        protected ArrayList<AppInfors> doInBackground(Void... voids) {
            PackageManager packageManager = getPackageManager();
            ArrayList<AppInfors> arrayList = new ArrayList<AppInfors>();
            Set<PackageInfo> set = Utility.getInstalledApps(getApplicationContext());
            PackageInfo[] array = set.toArray(new PackageInfo[set.size()]);
            for (PackageInfo pack : array){

                try {
                    AppInfors appInfors = new AppInfors();
                    appInfors.label =(String) packageManager.getApplicationLabel(pack.applicationInfo);
                    appInfors.packageName = pack.packageName;
                    appInfors.iconDrawable = packageManager.getApplicationIcon(pack.packageName);
                    appInfors.launchIntent = packageManager.getLaunchIntentForPackage(pack.packageName);
                    arrayList.add(appInfors);
                }catch (PackageManager.NameNotFoundException e){
                    Log.e(LOG_TAG, "name not found " + e);
                }
                Collections.sort(arrayList);

            }
            return arrayList;
        }
        protected void onPostExecute(ArrayList<AppInfors> result) {
            mAppInforsArrayList = result;
        }
    }


}
