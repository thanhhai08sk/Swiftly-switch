package org.de_studio.recentappswitcher.folderSetting.addShortcutToFolder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.adapter.ShortcutListAdapter;
import org.de_studio.recentappswitcher.base.addItemsToFolder.BaseAddItemsToFolderView;
import org.de_studio.recentappswitcher.dagger.AddShortcutToFolderModule;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerAddShortcutToFolderComponent;
import org.de_studio.recentappswitcher.model.Item;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import io.realm.Realm;

/**
 * Created by HaiNguyen on 12/9/16.
 */

public class AddShortcutToFolderView extends BaseAddItemsToFolderView {
    private static final String TAG = AddShortcutToFolderView.class.getSimpleName();
    private PackageManager packageManager;
    private ResolveInfo mResolveInfo;
    private List<ResolveInfo> resolveInfos;
    @Inject
    ShortcutListAdapter adapter;
    private Realm realm = Realm.getDefaultInstance();

    public static AddShortcutToFolderView newInstance(String folderId) {

        Bundle args = new Bundle();
        args.putString(Cons.SLOT_ID, folderId);
        AddShortcutToFolderView fragment = new AddShortcutToFolderView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.onItemClicked().subscribe(new Consumer<ResolveInfo>() {
            @Override
            public void accept(ResolveInfo resolveInfo) throws Exception {
                mResolveInfo = resolveInfo;
                ActivityInfo activity = mResolveInfo.activityInfo;
                ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                Intent i = new Intent(Intent.ACTION_CREATE_SHORTCUT);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setComponent(name);
                startActivityForResult(i, 1);
            }
        });
    }

    @Override
    public void clear() {
        super.clear();
        realm.close();
    }

    @Override
    public void loadItems() {
        Intent shortcutsIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        packageManager=getActivity().getPackageManager();
        resolveInfos =  packageManager.queryIntentActivities(shortcutsIntent, 0);
        adapter.setData(resolveInfos);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Item realmItem = Utility.getActionItemFromResult(mResolveInfo, packageManager, realm, data);
            setItemSubject.onNext(realmItem);

        }
    }



    @Override
    protected void inject() {
        DaggerAddShortcutToFolderComponent.builder()
                .appModule(new AppModule(getActivity()))
                .addShortcutToFolderModule(new AddShortcutToFolderModule(this, slotId))
                .build()
                .inject(this);
    }
}
