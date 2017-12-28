package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.main.MainModel;
import org.de_studio.recentappswitcher.main.MainPresenter;
import org.de_studio.recentappswitcher.main.MainView;
import org.de_studio.recentappswitcher.main.MainViewPagerAdapter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 11/12/16.
 */
@Module
@Singleton
public class MainModule {
    MainView view;

    public MainModule(MainView view) {
        this.view = view;
    }

    @Provides
    @Singleton
    MainPresenter presenter(MainModel model){
        return new MainPresenter(model);
    }

    @Provides
    @Singleton
    MainModel model(){
        return new MainModel();
    }

    @Provides
    @Singleton
    MainViewPagerAdapter adapter() {
        return new MainViewPagerAdapter(view.getSupportFragmentManager());
    }


}
