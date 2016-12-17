package org.de_studio.recentappswitcher.setItems.chooseContact;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.de_studio.recentappswitcher.base.BaseChooseItemFragmentView;
import org.de_studio.recentappswitcher.base.ContactLoader;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.ChooseContactModule;
import org.de_studio.recentappswitcher.dagger.DaggerChooseContactComponent;

/**
 * Created by HaiNguyen on 11/25/16.
 */

public class ChooseContactFragmentView extends BaseChooseItemFragmentView {
    private static final String TAG = ChooseContactFragmentView.class.getSimpleName();



    @Override
    public void loadItems() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            ContactLoader contactLoader = new ContactLoader(getActivity());
            getLoaderManager().initLoader(0, null, contactLoader);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE},
                    121);
        }
    }

    @Override
    protected void inject() {
        DaggerChooseContactComponent.builder()
                .appModule(new AppModule(getActivity()))
                .chooseContactModule(new ChooseContactModule(this))
                .build().inject(this);
    }
}
