package org.de_studio.recentappswitcher.dagger;

import android.content.Context;
import android.content.SharedPreferences;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 8/27/16.
 */
@Singleton
@Module
public class AppModule {
    Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    @Named(Cons.DEFAULT_SHARED_NAME)
    SharedPreferences defaultShared(){
        return context.getSharedPreferences(Cons.DEFAULT_SHARED_NAME, 0);
    }

    @Provides
    @Singleton
    @Named(Cons.EDGE_1_SHARED_NAME)
    SharedPreferences edge1Shared(){
        return context.getSharedPreferences(Cons.EDGE_1_SHARED_NAME, 0);
    }

    @Provides
    @Singleton
    @Named(Cons.EDGE_2_SHARED_NAME)
    SharedPreferences edge2Shared(){
        return context.getSharedPreferences(Cons.EDGE_2_SHARED_NAME, 0);
    }

    @Provides
    @Singleton
    @Named(Cons.EXCLUDE_SHARED_NAME)
    SharedPreferences excludeShared() {
        return context.getSharedPreferences(Cons.EXCLUDE_SHARED_NAME, 0);
    }

    @Provides
    @Singleton
    IconPackManager.IconPack iconPack(@Named(Cons.DEFAULT_SHARED_NAME) SharedPreferences defaultShared) {
        IconPackManager.IconPack iconPack = null;
        String iconPackPacka = defaultShared.getString(Cons.ICON_PACK_PACKAGE_NAME_KEY, Cons.ICON_PACK_NONE);
        if (!iconPackPacka.equals(Cons.ICON_PACK_NONE)) {
            IconPackManager iconPackManager = new IconPackManager();
            iconPackManager.setContext(context);
            iconPack = iconPackManager.getInstance(iconPackPacka);
            if (iconPack != null) {
                iconPack.load();
            }
        }
        return iconPack;
    }

}
