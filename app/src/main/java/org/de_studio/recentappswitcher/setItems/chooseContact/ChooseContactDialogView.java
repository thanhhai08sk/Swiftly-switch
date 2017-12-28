package org.de_studio.recentappswitcher.setItems.chooseContact;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.de_studio.recentappswitcher.base.BaseChooseItemDialogView;
import org.de_studio.recentappswitcher.base.ContactLoader;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.ChooseContactDialogModule;
import org.de_studio.recentappswitcher.dagger.DaggerChooseContactDialogComponent;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class ChooseContactDialogView extends BaseChooseItemDialogView {
    private static final String TAG = ChooseContactDialogView.class.getSimpleName();



    @Override
    public void loadItems() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "loadItems: load contact ");
            ContactLoader contactLoader = new ContactLoader(getActivity().getApplicationContext());
            getLoaderManager().initLoader(0, null, contactLoader);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE},
                    121);
        }
    }

    @Override
    protected void inject() {
        DaggerChooseContactDialogComponent.builder()
                .appModule(new AppModule(getActivity()))
                .chooseContactDialogModule(new ChooseContactDialogModule(this))
                .build().inject(this);
    }
}
