package org.de_studio.recentappswitcher.setItems;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.setItems.chooseAction.ChooseActionFragmentView;
import org.de_studio.recentappswitcher.setItems.chooseApp.ChooseAppFragmentView;
import org.de_studio.recentappswitcher.setItems.chooseContact.ChooseContactFragmentView;
import org.de_studio.recentappswitcher.setItems.chooseShortcut.ChooseShortcutFragmentView;

import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/18/16.
 */

public class SetItemsPagerAdapter extends FragmentPagerAdapter {
    BehaviorSubject<Item> currentItemChangeSubject;
    PublishSubject<Item> setItemSubject;
    Context context;

    public SetItemsPagerAdapter(Context context, FragmentManager fm, BehaviorSubject<Item> currentItemChangeSubject, PublishSubject<Item> setItemSubject) {
        super(fm);
        this.currentItemChangeSubject = currentItemChangeSubject;
        this.setItemSubject = setItemSubject;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ChooseAppFragmentView chooseAppView = new ChooseAppFragmentView();
                chooseAppView.setSubjects(currentItemChangeSubject, setItemSubject);
                return chooseAppView;
            case 1:
                ChooseActionFragmentView chooseActionView = new ChooseActionFragmentView();
                chooseActionView.setSubjects(currentItemChangeSubject, setItemSubject);
                return chooseActionView;
            case 2:
                ChooseContactFragmentView chooseContactView = new ChooseContactFragmentView();
                chooseContactView.setSubjects(currentItemChangeSubject, setItemSubject);
                return chooseContactView;
            case 3:
                ChooseShortcutFragmentView chooseShortcutView = new ChooseShortcutFragmentView();
                chooseShortcutView.setSubjects(currentItemChangeSubject, setItemSubject);
                return chooseShortcutView;
            default:
                ChooseAppFragmentView chooseAppView1 = new ChooseAppFragmentView();
                chooseAppView1.setSubjects(currentItemChangeSubject, setItemSubject);
                return chooseAppView1;
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
                return context.getString(R.string.choose_shortcut_apps_tab_title);
            case 1:
                return context.getString(R.string.choose_shortcut_actions_tab_title);
            case 2:
                return context.getString(R.string.contacts);
            case 3:
                return context.getString(R.string.shortcut);
        }
        return "";
    }
}
