package org.de_studio.recentappswitcher.main;

import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.de_studio.recentappswitcher.BuildConfig;
import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseActivity;
import org.de_studio.recentappswitcher.dadaSetup.DataSetupService;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerMainComponent;
import org.de_studio.recentappswitcher.dagger.MainModule;
import org.de_studio.recentappswitcher.faqs.FaqsView;
import org.de_studio.recentappswitcher.intro.IntroActivity;
import org.de_studio.recentappswitcher.main.about.AboutView;
import org.de_studio.recentappswitcher.main.moreSetting.MoreSettingView;
import org.de_studio.recentappswitcher.utils.inAppPurchase.Purchase;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subjects.PublishSubject;

public class MainView extends BaseActivity<Void,MainPresenter> implements MainPresenter.View {
    private static final String TAG = MainView.class.getSimpleName();
    static final int RC_REQUEST = 10001;

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.container)
    ViewPager viewPager;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.upgrade)
    View upgradeButton;





    @BindView(R.id.permission_missing)
    View permissionMissing;
    @Inject
    MainViewPagerAdapter pagerAdapter;

    @Inject
    @Named(Cons.SHARED_PREFERENCE_NAME)
    SharedPreferences shared;


    MaterialDialog initDataDialog;
    PublishSubject<Void> dataSetupOk = PublishSubject.create();
    GenerateDataOkReceiver generateDataOkReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utility.isFree(this)) {
            upgradeButton.setVisibility(View.VISIBLE);
            super.setUpCheckingPurchase();
        } else {
            upgradeButton.setVisibility(View.GONE);
        }
        viewPager.setOffscreenPageLimit(3);
    }



    @Override
    public void clearFirstStartAndStartIntroScreen() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor e = shared.edit();
                e.putBoolean(Cons.FIRST_START_KEY, false);
                e.putLong(Cons.DATE_START_KEY, System.currentTimeMillis());
                e.apply();
                Intent i = new Intent(MainView.this, IntroActivity.class);
                startActivity(i);
            }
        });
        t.start();
    }

    @Override
    public boolean isFirstStart() {
        return shared.getBoolean(Cons.FIRST_START_KEY, true);
    }

    @Override
    public boolean checkIf2FirstPermissionsOk() {
        boolean isOk;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isOk = isStep1Ok() && Settings.canDrawOverlays(this);
        } else {
            isOk = isStep1Ok();
        }
        return isOk;
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override
    public PublishSubject<Void> onDataSetupOk() {
        return dataSetupOk;
    }


    @Override
    public void registerForDataSetupOk() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DataSetupService.BROADCAST_GENERATE_DATA_OK);
        generateDataOkReceiver = new GenerateDataOkReceiver();
//            this.registerReceiver(receiver, filter);
        this.registerReceiver(generateDataOkReceiver, filter);
    }

    @Override
    public void displayPermissionNeeded(boolean show) {
        permissionMissing.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void restartServiceIfPossible() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Utility.restartService(this);

        } else {
            if (Settings.canDrawOverlays(this)) {
                Utility.restartService(this);
            }
        }
    }

    public void setupViewPager() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private boolean isStep1Ok() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        } else return true;


    }

    public void clear() {
        if (generateDataOkReceiver != null) {
            this.unregisterReceiver(generateDataOkReceiver);
        }
        if (initDataDialog != null) {
            initDataDialog = null;
        }

    }

    @Override
    protected void inject() {
        DaggerMainComponent.builder()
                .appModule(new AppModule(this))
                .mainModule(new MainModule(this))
                .build().inject(this);

    }

    public void showInitializingDialog(boolean visible) {
        if (visible) {
            initDataDialog = new MaterialDialog.Builder(this)
                    .title(R.string.initializing_data)
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .show();
        } else {
            if (initDataDialog != null) {
                initDataDialog.dismiss();
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.main_view;
    }

    @Override
    public void getDataFromRetainFragment() {

    }

    @Override
    public void onDestroyBySystem() {

    }

    @OnClick(R.id.more_setting)
    void moreSettingClick(){
        startActivity(new Intent(this, MoreSettingView.class));
    }

    @OnClick(R.id.permission_missing)
    void onPermissionClick(){
        Intent intent = new Intent(this, IntroActivity.class);
        intent.putExtra("page",4);
        startActivity(intent);
    }

    @OnClick(R.id.about)
    void onAboutClick(){
        startActivity(new Intent(this, AboutView.class));
    }

    @OnClick(R.id.faqs)
    void onFAQsClick(){
        startActivity(new Intent(this, FaqsView.class));
    }


    private void showProVersionInfo() {
        new MaterialDialog.Builder(this)
                .title(R.string.about_pro_version)
                .content(R.string.about_pro_text)
                .positiveText(R.string.main_buy_pro_button_text)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        Utility.getProVersion(MainView.this);
                        buyPro();
                    }
                }).show();
    }

    @OnClick(R.id.upgrade)
    void onUpgradeClick(){
        showProVersionInfo();
    }





    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }
    private void review() {
        Uri uri = Uri.parse("mbarket://details?id=" + getPackageName());
        Intent gotoMarket = new Intent(Intent.ACTION_VIEW, uri);
        gotoMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(gotoMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }


    @Override
    public void showWhatNewIfNeeded() {
        int savedAppVersion = shared.getInt(Cons.APP_VERSION_KEY, 0);
        if (savedAppVersion < BuildConfig.VERSION_CODE) {
            if (savedAppVersion > 0) {
                showWhatNew();
            }
            shared.edit().putInt(Cons.APP_VERSION_KEY, BuildConfig.VERSION_CODE).apply();
        }
    }

    @Override
    public void showWhatNew() {

    }

    protected void sendEmail() {
        Utility.sendFeedback(this,false);
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }
    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    public class GenerateDataOkReceiver extends BroadcastReceiver {
        public GenerateDataOkReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DataSetupService.BROADCAST_GENERATE_DATA_OK)) {
                Log.e(TAG, "onReceive: generate data ok");
                dataSetupOk.onNext(null);
            }
        }
    }

    


}
