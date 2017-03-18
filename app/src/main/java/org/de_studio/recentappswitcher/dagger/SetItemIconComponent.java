package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.setItemIcon.SetItemIconView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 3/18/17.
 */
@Singleton
@Component(
        modules = {AppModule.class, SetItemIconModule.class}
)
public interface SetItemIconComponent {
    void inject(SetItemIconView view);
}
