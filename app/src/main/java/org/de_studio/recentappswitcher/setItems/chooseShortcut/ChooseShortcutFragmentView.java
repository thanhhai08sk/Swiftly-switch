package org.de_studio.recentappswitcher.setItems.chooseShortcut;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseChooseItemFragmentView;
import org.de_studio.recentappswitcher.base.adapter.ShortcutListAdapter;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.ChooseShortcutModule;
import org.de_studio.recentappswitcher.dagger.DaggerChooseShortcutComponent;
import org.de_studio.recentappswitcher.model.Item;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import io.realm.Realm;

/**
 * Created by HaiNguyen on 11/25/16.
 */

public class ChooseShortcutFragmentView extends BaseChooseItemFragmentView<ChooseShortcutPresenter> {
    private static final String TAG = ChooseShortcutFragmentView.class.getSimpleName();
    private PackageManager packageManager;
    private ResolveInfo mResolveInfo;
    private List<ResolveInfo> resolveInfos;
    @Inject
    ShortcutListAdapter adapter;
    private Realm realm = Realm.getDefaultInstance();


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setAdapter(adapter);
        adapter.onItemClicked().subscribe(new Consumer<ResolveInfo>() {
            @Override
            public void accept(ResolveInfo resolveInfo) throws Exception {
                mResolveInfo = resolveInfo;
                ActivityInfo activity = mResolveInfo.activityInfo;
                ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                Intent i = new Intent(Intent.ACTION_CREATE_SHORTCUT);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setComponent(name);
                try {
                    startActivityForResult(i, 1);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void clear() {
        super.clear();
        realm.close();
    }

    @Override
    public void setProgressBar(boolean show) {
        //do nothing
    }

    @Override
    public void setCurrentItem(Item item) {
        //do nothing
    }

    @Override
    public void loadItems() {
        Log.e(TAG, "loadItems: ");
        Intent shortcutsIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        packageManager=getActivity().getPackageManager();
        resolveInfos =  packageManager.queryIntentActivities(shortcutsIntent, 0);
        adapter.setData(resolveInfos);
        Log.e(TAG, "loadItems: finish loading item, size = " + resolveInfos.size());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Item realmItem = Utility.getActionItemFromResult(mResolveInfo, packageManager, realm, data);
            if (realmItem != null) {
                itemClickSubject.onNext(realmItem);
            }else Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void inject() {
        DaggerChooseShortcutComponent.builder()
                .appModule(new AppModule(getActivity()))
                .chooseShortcutModule(new ChooseShortcutModule(this))
                .build().inject(this);
    }

}
