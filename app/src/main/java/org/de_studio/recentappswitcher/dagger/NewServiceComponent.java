package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.edgeService.NewServiceView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 12/24/16.
 */
@Singleton
@Component(modules = {AppModule.class,NewServiceModule.class})
public interface NewServiceComponent {
    void inject(NewServiceView view);
}
