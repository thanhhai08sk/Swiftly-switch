package org.de_studio.recentappswitcher.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.de_studio.recentappswitcher.main.general.GeneralView;

/**
 * Created by HaiNguyen on 11/5/16.
 */

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new GeneralView();
            default:
                return new GeneralView();
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "General";
            case 1:
                return "Edge 1";
            case 2:
                return "Edge 2";
        }
        return null;
    }
}
