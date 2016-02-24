package org.de_studio.recentappswitcher.favoriteShortcut;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.de_studio.recentappswitcher.R;

public class ChooseShortcutActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final String LOG_TAG = ChooseShortcutActivity.class.getSimpleName();
    private int mPosition;
    private AppTabFragment mAppTabFragment;


    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getIntent().getFlags();
        Toast.makeText(getApplicationContext(),"ChooseShortcutActivity position = "+ mPosition,Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_choose_shortcut);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabbar);
        tabLayout.setupWithViewPager(mViewPager);
        AppCompatButton backButton = (AppCompatButton) findViewById(R.id.app_tab_fragment_back_button);
        AppCompatButton nextButton = (AppCompatButton) findViewById(R.id.app_tab_fragment_next_button);
        AppCompatButton okButton = (AppCompatButton) findViewById(R.id.app_tab_fragment_ok_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppTabFragment.setmPositioinToNext();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppTabFragment.setmPositionToBack();
            }
        });
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_choose_shortcut, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
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
                    return mAppTabFragment;
                case 1:
                    return PlaceholderFragment.newInstance(position + 1);
                default: return PlaceholderFragment.newInstance(position + 1);
            }

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
            }
            return null;
        }
    }


}
