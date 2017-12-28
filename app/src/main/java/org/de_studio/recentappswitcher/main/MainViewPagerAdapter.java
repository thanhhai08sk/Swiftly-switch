package org.de_studio.recentappswitcher.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.de_studio.recentappswitcher.main.edgeSetting.EdgeSettingView;
import org.de_studio.recentappswitcher.main.general.GeneralView;
import org.de_studio.recentappswitcher.model.Edge;

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
            case 1:
                return EdgeSettingView.newInstance(Edge.EDGE_1_ID);
            case 2:
                return EdgeSettingView.newInstance(Edge.EDGE_2_ID);
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
