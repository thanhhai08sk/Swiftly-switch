package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.MyRealmMigration;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.service.EdgeSetting;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ChooseShortcutActivity extends AppCompatActivity implements AppListAdapter.AppChangeListener, ActionListAdapter.SettingChangeListener, ContactCursorAdapter.ContactChangeListener{


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final String TAG = ChooseShortcutActivity.class.getSimpleName();
    private int mPosition;
    private AppTabFragment mAppTabFragment;
    private ActionTabFragment mActionTabFragment;
    private ContactTabFragment mContactTabFragment;
    private ShortcutTabFragment mShortcutTabFragment;
    private ImageView currentShortcut;
    private Realm myRealm;
    private AppListAdapter mAppAdapter;
    private ActionListAdapter mSettingAdapter;
    private ContactCursorAdapter mContactAdapter;
    private ShortcutListAdapter mShortcutListAdapter;
    private Context mContext;
    private int mode;
    private SharedPreferences sharedPreferences;
    private IconPackManager.IconPack iconPack;


    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getIntent().getFlags();
        mode = getIntent().getIntExtra("mode", FavoriteSettingActivity.MODE_GRID);
        Log.e(TAG, "mode = " + mode);
        mContext = this;
        sharedPreferences = getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        setContentView(R.layout.activity_choose_shortcut);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabbar);
        tabLayout.setupWithViewPager(mViewPager);
        AppCompatImageButton backButton = (AppCompatImageButton) findViewById(R.id.app_tab_fragment_back_button);
        AppCompatImageButton nextButton = (AppCompatImageButton) findViewById(R.id.app_tab_fragment_next_button);
        AppCompatButton okButton = (AppCompatButton) findViewById(R.id.app_tab_fragment_ok_button);

        String iconPackPacka = sharedPreferences.getString(EdgeSetting.ICON_PACK_PACKAGE_NAME_KEY, "none");
        if (!iconPackPacka.equals("none")) {
            IconPackManager iconPackManager = new IconPackManager();
            iconPackManager.setContext(mContext);
            iconPack = iconPackManager.getInstance(iconPackPacka);
        }
        if (mode == FavoriteSettingActivity.MODE_GRID) {
            myRealm =Realm.getInstance(new RealmConfiguration.Builder()
                    .name("default.realm")
                    .schemaVersion(Cons.OLD_REALM_SCHEMA_VERSION)
                    .migration(new MyRealmMigration())
                    .build());
        } else {
            myRealm = Realm.getInstance(new RealmConfiguration.Builder()
                    .name("circleFavo.realm")
                    .schemaVersion(Cons.OLD_REALM_SCHEMA_VERSION)
                    .migration(new MyRealmMigration())
                    .build());
        }

        final TextView positionText = (TextView) findViewById(R.id.app_tab_fragment_position_text_view);
        if (positionText != null) {
//            positionText.setText(mPosition + 1 + ".");
            positionText.setText(String.format("%d.",mPosition+1));
        }
        currentShortcut = (ImageView) findViewById(R.id.app_tab_fragment_current_shortcut_image_view);
        setCurrentShortcutImageView();
        if (nextButton != null) {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int max;
                    switch (mode) {
                        case FavoriteSettingActivity.MODE_GRID:
                            max = Utility.getSizeOfFavoriteGrid(getApplicationContext())-1;
                            break;
                        case FavoriteSettingActivity.MODE_CIRCLE:
                            max = 5;
                            break;
                        default:
                            max = 5;
                            break;
                    }
                    if (mPosition < max) {
                        mPosition++;
                        positionText.setText(mPosition + 1 + ".");
                        setCurrentShortcutImageView();
                        try {
                            mAppTabFragment.setmPositioinToNext();
                            mActionTabFragment.setmPositioinToNext();
                            mContactTabFragment.setmPositioinToNext();
                            mShortcutTabFragment.setmPositioinToNext();

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                    }



                }
            });
        }
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPosition > 0) {
                        mPosition--;
                        positionText.setText(mPosition + 1 + ".");
                        setCurrentShortcutImageView();
                    }
                    try {
                        mAppTabFragment.setmPositionToBack();
                        mActionTabFragment.setmPositionToBack();
                        mContactTabFragment.setmPositionToBack();
                        mShortcutTabFragment.setmPositionToBack();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }



                }
            });
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        RealmResults<Shortcut> results = myRealm.where(Shortcut.class).findAll();
//        results.addChangeListener(new RealmChangeListener() {
//            @Override
//            public void onChange() {
//                Log.e(TAG, "onChange");
//                setCurrentShortcutImageView();
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        if (myRealm != null) {
            myRealm.close();
        }
        super.onDestroy();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    mAppTabFragment = AppTabFragment.newInstance(position + 1);
                    mAppTabFragment.setmPosition(mPosition);
                    mAppTabFragment.setMode(mode);
                    mAppTabFragment.setmContext(mContext);
                    return mAppTabFragment;
                case 1:
                    mActionTabFragment = ActionTabFragment.newInstance(position + 1);
                    mActionTabFragment.setmPosition(mPosition);
                    mActionTabFragment.setMode(mode);
                    return mActionTabFragment;
                case 2:
                    mContactTabFragment = ContactTabFragment.newInstance(position + 1);
                    mContactTabFragment.setmPosition(mPosition);
                    mContactTabFragment.setMode(mode);
                    return mContactTabFragment;
                case 3:
                    mShortcutTabFragment = ShortcutTabFragment.newInstance(position + 1);
                    mShortcutTabFragment.setMode(mode);
                    mShortcutTabFragment.setmPosition(mPosition);
                    return mShortcutTabFragment;
                default: mAppTabFragment = AppTabFragment.newInstance(position + 1);
                    mAppTabFragment.setmPosition(mPosition);
                    mAppTabFragment.setmContext(mContext);
                    mAppTabFragment.setMode(mode);
                    return mAppTabFragment;
            }

        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.choose_shortcut_apps_tab_title);
                case 1:
                    return getString(R.string.choose_shortcut_actions_tab_title);
                case 2:
                    return getString(R.string.contacts);
                case 3:
                    return getString(R.string.shortcut);
            }
            return null;
        }
    }

    private void setCurrentShortcutImageView() {
        Log.e(TAG, "setCurrentShortcutImageView");
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id", mPosition).findFirst();
        if (shortcut != null) {
            Utility.setImageForShortcut(shortcut,getPackageManager(),currentShortcut,mContext,iconPack,myRealm,false);
        } else {
            currentShortcut.setImageResource(R.drawable.ic_add_circle_outline_white_48dp);
        }


    }

    public void setAppAdapter(AppListAdapter adapter) {
        mAppAdapter = adapter;
        adapter.registerListener(this);
    }

    public void setSettingAdapter(ActionListAdapter adapter) {
        mSettingAdapter = adapter;
        adapter.registerListener(this);
    }

    public void setContactAdapter(ContactCursorAdapter adapter) {
        mContactAdapter = adapter;
        adapter.registerListener(this);
    }

    public void setShortcutShortcutAdapter(ShortcutListAdapter adapter) {
        mShortcutListAdapter = adapter;
        adapter.registerListener(this);
    }

    @Override
    public void onAppChange() {
        Log.e(TAG, "onAppChange");
        setCurrentShortcutImageView();
        if (mSettingAdapter != null) {
            mSettingAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSettingChange() {
        setCurrentShortcutImageView();
        if (mAppAdapter != null) {
            mAppAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onContactChange() {
        setCurrentShortcutImageView();
        if (mContactAdapter != null) {
            mContactAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult: ");
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
