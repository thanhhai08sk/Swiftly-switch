package org.de_studio.recentappswitcher.folderSetting.addContactToFolder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.ContactLoader;
import org.de_studio.recentappswitcher.base.addItemsToFolder.BaseAddItemsToFolderView;
import org.de_studio.recentappswitcher.dagger.AddContactToFolderModule;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerAddContactToFolderComponent;

/**
 * Created by HaiNguyen on 12/9/16.
 */

public class AddContactToFolderView extends BaseAddItemsToFolderView {
    private static final String TAG = AddContactToFolderView.class.getSimpleName();


    public static AddContactToFolderView newInstance(String folderId) {

        Bundle args = new Bundle();
        args.putString(Cons.SLOT_ID, folderId);
        AddContactToFolderView fragment = new AddContactToFolderView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void loadItems() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
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
        DaggerAddContactToFolderComponent.builder()
                .appModule(new AppModule(getActivity()))
                .addContactToFolderModule(new AddContactToFolderModule(this, slotId))
                .build().inject(this);
    }
}
