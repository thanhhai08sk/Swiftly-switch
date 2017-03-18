package org.de_studio.recentappswitcher.setItemIcon;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseActivity;
import org.de_studio.recentappswitcher.dagger.DaggerSetItemIconComponent;
import org.de_studio.recentappswitcher.dagger.SetItemIconModule;
import org.de_studio.recentappswitcher.utils.GridSpacingItemDecoration;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 3/17/17.
 */

public class SetItemIconView extends BaseActivity<Void, SetItemIconPresenter> implements SetItemIconPresenter.View {
    private static final String TAG = SetItemIconView.class.getSimpleName();

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progress;


    @Inject
    DrawableAdapter adapter;
    @Inject
    PackageManager packageManager;

    final ArrayList<BitmapInfo> mAllItems = new ArrayList<BitmapInfo>();
    String iconPackPackage;
    String itemId;

    PublishSubject<Void> loadAllItemOkSJ = PublishSubject.create();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        iconPackPackage = getIntent().getStringExtra(Cons.PACKAGENAME);
        itemId = getIntent().getStringExtra(Cons.ITEM_ID);
        super.onCreate(savedInstanceState);

        recyclerView.setLayoutManager(new GridLayoutManager(this, Utility.calculateNoOfColumns(this, 56)));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration((int) (4 * getResources().getDisplayMetrics().density)));

        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SearchView searchView = new SearchView(this);
        searchView.setSearchableInfo(((SearchManager) getSystemService(Context.SEARCH_SERVICE)).getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);

        menu.add(android.R.string.search_go)
                .setIcon(android.R.drawable.ic_menu_search)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW + MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setActionView(searchView);
        return true;
    }



    @Override
    protected void inject() {
        DaggerSetItemIconComponent.builder()
                .setItemIconModule(new SetItemIconModule(itemId,this))
                .build().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.set_item_icon_view;
    }

    @Override
    public void getDataFromRetainFragment() {

    }

    @Override
    public void onDestroyBySystem() {

    }


    @Override
    public void clear() {

    }


    @Override
    public void loadAllItem() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run: on thread " + Thread.currentThread().getName() );
                if (iconPackPackage != null) {
                    try {
                        Resources res = packageManager.getResourcesForApplication(iconPackPackage);
                        if (res != null) {
                            loadThemeIcons(res, iconPackPackage);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e(TAG, "loadAllItem: name not found");
                        e.printStackTrace();
                    }
                } else {
                    loadAppIcons();
                }
                loadAllItemOkSJ.onNext(null);
            }
        });
        thread.start();


    }

    @Override
    public PublishSubject<Void> onLoadAllItemsOk() {
        return loadAllItemOkSJ;
    }

    @Override
    public void updateAdapterData() {
        adapter.updateData(mAllItems);
    }

    @Override
    public void setProgressBar(boolean visible) {
        progress.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.e(TAG, "onQueryTextChange: " + newText);
        if (TextUtils.isEmpty(newText)) {
            adapter.clearData();
            adapter.updateData(mAllItems);
            return true;
        }

        newText = newText.toLowerCase(Locale.ENGLISH);
        adapter.clearData();
        for (BitmapInfo info : mAllItems) {
            if (info.label != null && info.label.contains(newText)) {
                adapter.addItem(info);
            }
        }

        adapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }

    private void loadAppIcons() {
        PackageManager pm = getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0);
        for (ResolveInfo info : infos) {
            try {
                if (info.getIconResource() != 0) {
                    mAllItems.add(new BitmapInfo(pm.getResourcesForApplication(info.activityInfo.applicationInfo), info.getIconResource(), (info.loadLabel(pm) + "").toLowerCase(Locale.getDefault())));
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "loadAppIcons: ");
            }
        }
    }

    private void loadThemeIcons(final Resources resources, final String pkg) {
        final HashSet<String> drawables = new HashSet<String>();
        parseAssets(resources, "appfilter", pkg, drawables);
        parseAssets(resources, "drawable", pkg, drawables);

        ArrayList<String> sorted = new ArrayList<>();
        sorted.addAll(drawables);
        Collections.sort(sorted);

        for (String item : sorted) {
            int id = resources.getIdentifier(item, "drawable", pkg);
            if (id > 0) {
                mAllItems.add(new BitmapInfo(resources, id, item.toLowerCase(Locale.ENGLISH)));
            }
        }
    }

    private void parseAssets(Resources res, String assetFile, String pkg, HashSet<String> target) {
        try {
            XmlPullParser parser;
            int xmlRes = res.getIdentifier(assetFile, "xml", pkg);
            if (xmlRes != 0) {
                parser = res.getXml(xmlRes);
            } else {
                InputStream is = res.getAssets().open(assetFile + ".xml");
                XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
                xmlFactory.setNamespaceAware(true);
                parser = xmlFactory.newPullParser();
                parser.setInput(is, "UTF-8");
            }

            while(parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if ((parser.getEventType() == XmlPullParser.START_TAG) && "item".equals(parser.getName())) {
                    String drawable = parser.getAttributeValue(null, "drawable");
                    if (!TextUtils.isEmpty(drawable)) {
                        target.add(drawable);
                    }
                }
                parser.next();
            }
        } catch (Throwable e) {
            Log.e(TAG, "parseAssets: ");
        }
    }

    @Override
    public Bitmap getBitmap(BitmapInfo item) {
        Drawable drawable = item.res.getDrawable(item.resId, null);
        return ((BitmapDrawable) drawable).getBitmap();
    }

    public static Intent getIntent(String itemId, Context context, String label, String iconPackPackageName) {
        Intent intent = new Intent(context, SetItemIconView.class);
        intent.putExtra(Cons.LABEL, label);
        intent.putExtra(Cons.ITEM_ID, itemId);
        intent.putExtra(Cons.PACKAGENAME, iconPackPackageName);
        return intent;
    }

    @Override
    public PublishSubject<BitmapInfo> onItemClick() {
        return adapter.onItemClick();
    }

    public static class BitmapInfo {
        final int resId;
        final Resources res;
        final String label;

        BitmapInfo(Resources res, int resId, String lbl) {
            this.res = res;
            this.resId = resId;
            label = lbl;
        }
    }
}
