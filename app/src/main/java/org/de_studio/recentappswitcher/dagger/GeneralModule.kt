package org.de_studio.recentappswitcher.dagger

import dagger.Module
import dagger.Provides
import org.de_studio.recentappswitcher.main.general.GeneralPresenter
import org.de_studio.recentappswitcher.main.general.GeneralView
import javax.inject.Singleton

/**
 * Created by HaiNguyen on 6/7/17.
 */
@Module
class GeneralModule {
    val view: GeneralView

    constructor(view: GeneralView) {
        this.view = view
    }

    @Provides
    @Singleton
    fun presenter(): GeneralPresenter {
        return GeneralPresenter(null)
    }
}