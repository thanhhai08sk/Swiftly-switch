package org.de_studio.recentappswitcher.dagger

import dagger.Component
import org.de_studio.recentappswitcher.main.general.GeneralView
import javax.inject.Singleton

/**
 * Created by HaiNguyen on 6/7/17.
 */
@Singleton
@Component(modules = arrayOf(GeneralModule::class,AppModule::class))

interface GeneralComponent {
    fun inject(view: GeneralView)
}