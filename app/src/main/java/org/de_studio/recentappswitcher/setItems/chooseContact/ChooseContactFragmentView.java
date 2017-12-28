package org.de_studio.recentappswitcher.setItems.chooseContact;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.de_studio.recentappswitcher.base.BaseChooseItemFragmentView;
import org.de_studio.recentappswitcher.base.ContactLoader;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.ChooseContactModule;
import org.de_studio.recentappswitcher.dagger.DaggerChooseContactComponent;

/**
 * Created by HaiNguyen on 11/25/16.
 */

public class ChooseContactFragmentView extends BaseChooseItemFragmentView<ChooseContactPresenter> {
    private static final String TAG = ChooseContactFragmentView.class.getSimpleName();



    @Override
    public void loadItems() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "loadItems: permission ok");
            ContactLoader contactLoader = new ContactLoader(getActivity().getApplicationContext());
            getLoaderManager().initLoader(0, null, contactLoader);
        } else {
            Log.e(TAG, "loadItems: need contact permission");
            needContactPermissionSJ.onNext(null);
//            presenter.onNeedContactPermission();
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
