package org.de_studio.recentappswitcher.main;

import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.de_studio.recentappswitcher.BuildConfig;
import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseActivity;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerMainComponent;
import org.de_studio.recentappswitcher.dagger.MainModule;
import org.de_studio.recentappswitcher.faqs.FaqsView;
import org.de_studio.recentappswitcher.intro.IntroActivity;
import org.de_studio.recentappswitcher.main.about.AboutView;
import org.de_studio.recentappswitcher.main.moreSetting.MoreSettingView;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.OnClick;

public class MainView extends BaseActivity<Void,MainPresenter> implements MainPresenter.View{
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.container)
    ViewPager viewPager;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.permission_missing)
    View permissionMissing;




    @Inject
    MainViewPagerAdapter pagerAdapter;
    @Inject
    @Named(Cons.SHARED_PREFERENCE_NAME)
    SharedPreferences shared;



    @Override
    public void startIntroAndDataSetupIfNeeded() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isFirstStart = shared.getBoolean(Cons.FIRST_START_KEY, true);
                if (isFirstStart) {
                    SharedPreferences.Editor e = shared.edit();
                    e.putBoolean(Cons.FIRST_START_KEY, false);
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

    }

    @Override
    protected void inject() {
        DaggerMainComponent.builder()
                .appModule(new AppModule(this))
                .mainModule(new MainModule(this))
                .build().inject(this);

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
                        Utility.getProVersion(MainView.this);
                    }
                }).show();
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
        String[] TO = {"thanhhai08sk@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_prompt));

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainView.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    


}
