package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class ChooseShortcutActivity extends AppCompatActivity implements AppListAdapter.AppChangeListener, SettingListAdapter.SettingChangeListener, ContactCursorAdapter.ContactChangeListener{


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final String LOG_TAG = ChooseShortcutActivity.class.getSimpleName();
    private int mPosition;
    private AppTabFragment mAppTabFragment;
    private SettingTabFragment mSettingTabFragment;
    private ContactTabFragment mContactTabFragment;
    private ImageView currentShortcut;
    private Realm myRealm;
    private AppListAdapter mAppAdapter;
    private SettingListAdapter mSettingAdapter;
    private ContactCursorAdapter mContactAdapter;
    private Context mContext;
    private int mode;


    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getIntent().getFlags();
//        mode = getIntent().getIntExtra("mode",FavoriteSettingActivity.MODE_CIRCLE);
        mode = getIntent().getIntExtra("mode", FavoriteSettingActivity.MODE_GRID);
        Log.e(LOG_TAG, "mode = " + mode);
        mContext = this;
//        Toast.makeText(getApplicationContext(),"ChooseShortcutActivity position = "+ mPosition,Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_choose_shortcut);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabbar);
        tabLayout.setupWithViewPager(mViewPager);
        AppCompatImageButton backButton = (AppCompatImageButton) findViewById(R.id.app_tab_fragment_back_button);
        AppCompatImageButton nextButton = (AppCompatImageButton) findViewById(R.id.app_tab_fragment_next_button);
        AppCompatButton okButton = (AppCompatButton) findViewById(R.id.app_tab_fragment_ok_button);
        if (mode == FavoriteSettingActivity.MODE_GRID) {
            myRealm = Realm.getInstance(getApplicationContext());
        } else {
            myRealm = Realm.getInstance(new RealmConfiguration.Builder(mContext).name("circleFavo.realm").build());
        }

        final TextView positionText = (TextView) findViewById(R.id.app_tab_fragment_position_text_view);
        positionText.setText(mPosition + 1 + ".");
        currentShortcut = (ImageView) findViewById(R.id.app_tab_fragment_current_shortcut_image_view);
        setCurrentShortcutImageView();
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
                        mSettingTabFragment.setmPositioinToNext();
                        mContactTabFragment.setmPositioinToNext();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }



            }
        });
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
                    mSettingTabFragment.setmPositionToBack();
                    mContactTabFragment.setmPositionToBack();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }



            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        RealmResults<Shortcut> results = myRealm.where(Shortcut.class).findAll();
        results.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                Log.e(LOG_TAG, "onChange");
                setCurrentShortcutImageView();
            }
        });
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
                    mSettingTabFragment = SettingTabFragment.newInstance(position + 1);
                    mSettingTabFragment.setmPosition(mPosition);
                    mSettingTabFragment.setMode(mode);
                    return mSettingTabFragment;
                case 2:
                    mContactTabFragment = ContactTabFragment.newInstance(position + 1);
                    mContactTabFragment.setmPosition(mPosition);
                    return mContactTabFragment;
                default: mAppTabFragment = AppTabFragment.newInstance(position + 1);
                    mAppTabFragment.setmPosition(mPosition);
                    mAppTabFragment.setmContext(mContext);
                    mAppTabFragment.setMode(mode);
                    return mAppTabFragment;
            }

        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.choose_shortcut_apps_tab_title);
                case 1:
                    return getString(R.string.choose_shortcut_actions_tab_title);
                case 2:
                    return "Contact";
            }
            return null;
        }
    }

    private void setCurrentShortcutImageView() {
        Log.e(LOG_TAG, "setCurrentShortcutImageView");
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id", mPosition).findFirst();
        if (shortcut != null) {
            if (shortcut.getType() == Shortcut.TYPE_APP) {
                try {
                    currentShortcut.setImageDrawable(getApplicationContext().getPackageManager().getApplicationIcon(myRealm.where(Shortcut.class).equalTo("id", mPosition).findFirst().getPackageName()));

                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(LOG_TAG, "NameNotFound " + e);
                }
            } else if (shortcut.getType() == Shortcut.TYPE_SETTING) {
                switch (shortcut.getAction()) {
                    case Shortcut.ACTION_WIFI:
                        currentShortcut.setImageResource(R.drawable.ic_action_wifi_on);
                        break;
                    case Shortcut.ACTION_BLUETOOTH:
                        currentShortcut.setImageResource(R.drawable.ic_action_bluetooth_on);
                        break;
                    case Shortcut.ACTION_ROTATION:
                        currentShortcut.setImageResource(R.drawable.ic_action_rotate_on);
                        break;
                    case Shortcut.ACTION_POWER_MENU:
                        currentShortcut.setImageResource(R.drawable.ic_action_power_menu);
                        break;
                    case Shortcut.ACTION_HOME:
                        currentShortcut.setImageResource(R.drawable.ic_icon_home);
                        break;
                    case Shortcut.ACTION_BACK:
                        currentShortcut.setImageResource(R.drawable.ic_icon_back);
                        break;
                    case Shortcut.ACTION_NOTI:
                        currentShortcut.setImageResource(R.drawable.ic_icon_noti);
                        break;
                    case Shortcut.ACTION_LAST_APP:
                        currentShortcut.setImageResource(R.drawable.ic_icon_last_app);
                        break;
                    case Shortcut.ACTION_CALL_LOGS:
                        currentShortcut.setImageResource(R.drawable.ic_icon_call_log);
                        break;
                    case Shortcut.ACTION_CONTACT:
                        currentShortcut.setImageResource(R.drawable.ic_icon_contact);
                        break;
                    case Shortcut.ACTION_DIAL:
                        currentShortcut.setImageResource(R.drawable.ic_icon_dial);
                        break;
                    case Shortcut.ACTION_RECENT:
                        currentShortcut.setImageResource(R.drawable.ic_action_recent2);
                        break;
                    case Shortcut.ACTION_NONE:
                        currentShortcut.setImageDrawable(null);
                        break;
                }
            } else if (shortcut.getType() == Shortcut.TYPE_CONTACT) {

                String thumbnaiUri = shortcut.getThumbnaiUri();
                if (thumbnaiUri != null) {
                    Uri uri = Uri.parse(thumbnaiUri);
                    currentShortcut.setImageURI(uri);
                } else {
                    currentShortcut.setImageResource(R.drawable.ic_icon_home);
                }
            }
        } else {
            currentShortcut.setImageResource(R.drawable.ic_add_circle_outline_white_48dp);
        }


    }

    public void setAppAdapter(AppListAdapter adapter) {
        mAppAdapter = adapter;
        adapter.registerListener(this);
    }

    public void setSettingAdapter(SettingListAdapter adapter) {
        mSettingAdapter = adapter;
        adapter.registerListener(this);
    }

    public void setContactAdapter(ContactCursorAdapter adapter) {
        mContactAdapter = adapter;
        adapter.registerListener(this);
    }

    @Override
    public void onAppChange() {
        Log.e(LOG_TAG, "onAppChange");
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
}
