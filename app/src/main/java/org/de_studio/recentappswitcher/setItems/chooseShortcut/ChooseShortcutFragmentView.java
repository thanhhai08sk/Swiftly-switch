package org.de_studio.recentappswitcher.setItems.chooseShortcut;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import org.de_studio.recentappswitcher.base.BaseChooseItemFragmentView;
import org.de_studio.recentappswitcher.base.adapter.ShortcutListAdapter;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.ChooseShortcutModule;
import org.de_studio.recentappswitcher.dagger.DaggerChooseShortcutComponent;
import org.de_studio.recentappswitcher.model.Item;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.inject.Inject;

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
        Intent shortcutsIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        packageManager=getActivity().getPackageManager();
        resolveInfos =  packageManager.queryIntentActivities(shortcutsIntent, 0);
        adapter.setData(resolveInfos);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mResolveInfo = resolveInfos.get(position);
        ActivityInfo activity = mResolveInfo.activityInfo;
        ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
        Intent i = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setComponent(name);
        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String label = (String) data.getExtras().get(Intent.EXTRA_SHORTCUT_NAME);
            String stringIntent = ((Intent) data.getExtras().get(Intent.EXTRA_SHORTCUT_INTENT)).toUri(0);
            String packageName =  mResolveInfo.activityInfo.packageName;

            int iconResId = 0;

            Bitmap bmp = null;
            Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
            if (extra != null && extra instanceof Bitmap)
                bmp = (Bitmap) extra;
            if (bmp == null) {
                extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
                if (extra != null && extra instanceof Intent.ShortcutIconResource) {
                    try {
                        Intent.ShortcutIconResource iconResource = (Intent.ShortcutIconResource) extra;
                        packageName = iconResource.packageName;
                        Resources resources = packageManager.getResourcesForApplication(iconResource.packageName);
                        iconResId = resources.getIdentifier(iconResource.resourceName, null, null);
                    } catch (Exception e) {
                        Log.e(TAG, "onActivityResult: Could not load shortcut icon:");
                    }
                }
            }
            realm.beginTransaction();
            Item item = new Item();
            item.type = Item.TYPE_DEVICE_SHORTCUT;
            item.itemId = Item.TYPE_DEVICE_SHORTCUT + stringIntent;
            item.label = label;
            item.packageName = packageName;
            item.intent = stringIntent;
            if (bmp != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                item.iconBitmap = stream.toByteArray();
            } else {
                item.iconResourceId = iconResId;
            }
            Item realmItem = realm.copyToRealm(item);
            realm.commitTransaction();
            itemClickSubject.onNext(realmItem);

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
