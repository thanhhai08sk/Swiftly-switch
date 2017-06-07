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
import android.widget.CompoundButton;

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
    @BindView(R.id.about_pro_version)
    View aboutProButton;





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
            aboutProButton.setVisibility(View.VISIBLE);
            super.setUpCheckingPurchase();
        } else {
            aboutProButton.setVisibility(View.GONE);
        }
    }



    @Override
    public void startIntroAndDataSetupIfNeeded() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isFirstStart = shared.getBoolean(Cons.FIRST_START_KEY, true);
                if (isFirstStart) {
                    SharedPreferences.Editor e = shared.edit();
                    e.putBoolean(Cons.FIRST_START_KEY, false);
                    e.putLong(Cons.DATE_START_KEY, System.currentTimeMillis());
                    e.commit();
                    Intent i = new Intent(MainView.this, IntroActivity.class);
//                    Intent intent = new Intent(MainView.this, DataSetupService.class);
//                    intent.setAction(DataSetupService.ACTION_GENERATE_DATA);
//                    startService(intent);
                    startActivity(i);
                }
            }
        });
        t.start();
    }

    @Override
    public boolean checkIfAllPermissionOk() {
        boolean isOk;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isOk = isStep1Ok() && Settings.canDrawOverlays(this) && Utility.isAccessibilityEnable(this);

        } else {
            isOk = isStep1Ok() && Utility.isAccessibilityEnable(this);
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
    @OnClick(R.id.send_feedback)
    void emailClick(){
        sendEmail();
    }
    @OnClick(R.id.review)
    void onReviewClick(){
        review();
    }

    @OnClick(R.id.faqs)
    void onFAQsClick(){
        startActivity(new Intent(this, FaqsView.class));
    }

    @OnClick(R.id.about_pro_version)
    void aboutProClick() {
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

    @OnClick(R.id.changelog)
    void whatNewClick(){
        showWhatNew();
    }

    @OnClick(R.id.translate)
    void translateClick(){
        new MaterialDialog.Builder(this)
                .title(R.string.translate_the_app)
                .content(R.string.translate_the_app_detail)
                .positiveText(R.string.go_to_page)
                .negativeText(R.string.edge_dialog_cancel_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Uri uriUrl = Uri.parse("https://www.localize.im/v/xy");
                        startActivity(new Intent(Intent.ACTION_VIEW, uriUrl));
                    }
                }).show();
    }

    @OnClick(R.id.about)
    void onAboutClick(){
        startActivity(new Intent(this, AboutView.class));
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
        new MaterialDialog.Builder(this)
                .positiveText(R.string.app_tab_fragment_ok_button)
                .negativeText(R.string.vote_now)
                .title(R.string.what_new)
                .items(R.array.what_new)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        review();
                    }
                })
                .checkBoxPromptRes(R.string.show_this_after_an_update, shared.getBoolean(Cons.AUTO_SHOW_WHAT_NEW_KEY,true), new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        shared.edit().putBoolean(Cons.AUTO_SHOW_WHAT_NEW_KEY, b).commit();
                    }
                })
                .show();

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


    public void updateFreeOrProUi(boolean isPro) {
        aboutProButton.setVisibility(isPro ? View.GONE : View.VISIBLE);
        if (!isPro) {
            shared.edit().putBoolean(Cons.EDGE_2_ON_KEY, false).commit();
        }
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
