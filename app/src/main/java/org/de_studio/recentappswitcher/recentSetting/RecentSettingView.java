package org.de_studio.recentappswitcher.recentSetting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCircleCollectionSettingView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerRecentSettingComponent;
import org.de_studio.recentappswitcher.dagger.RecentSettingModule;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class RecentSettingView extends BaseCircleCollectionSettingView<Void, RecentSettingPresenter> implements RecentSettingPresenter.View {
    private static final String TAG = RecentSettingView.class.getSimpleName();

    @Override
    protected void inject() {
        DaggerRecentSettingComponent.builder()
                .appModule(new AppModule(this))
                .recentSettingModule(new RecentSettingModule(this, collectionId))
                .build().inject(this);
    }

    @Override
    public void chooseToSetRecentOrShortcutToSlot(final int slotIndex) {
//        new MaterialDialog.Builder(this)
//                .items(R.array.recent_slot_options)
//                .itemsCallback(new MaterialDialog.ListCallback() {
//                    @Override
//                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
//                        switch (position) {
//                            case 0:
//                                presenter.setThisSlotAsRecent(slotIndex);
//                                break;
//                            case 1:
//                                presenter.setItems(slotIndex);
//                                break;
//                        }
//                    }
//                }).show();


        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                switch (index) {
                    case 0:
                        presenter.setThisSlotAsRecent(slotIndex);
                        break;
                    case 1:
                        presenter.setItems(slotIndex);
                        break;
                }
                dialog.dismiss();
            }
        });

        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.recent_app)
                .icon(R.drawable.ic_recent_app_slot)
                .backgroundColor(Color.WHITE)
                .build());
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.shortcut)
                .icon(R.drawable.ic_shortcuts)
                .backgroundColor(Color.WHITE)
                .build());


        new MaterialDialog.Builder(this)
                .adapter(adapter, null)
                .show();
    }

    public static Intent getIntent(Context context, String collectionId) {
        Intent intent = new Intent(context, RecentSettingView.class);
        intent.putExtra(Cons.COLLECTION_ID, collectionId);
        return intent;
    }
}
