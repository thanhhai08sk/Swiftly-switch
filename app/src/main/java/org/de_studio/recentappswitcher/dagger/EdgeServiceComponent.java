package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.edgeService.EdgeServiceView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 8/27/16.
 */
@Component(modules = {EdgeServiceModule.class, RealmModule.class})
@Singleton
public interface EdgeServiceComponent {
    void inject(EdgeServiceView view);
}
