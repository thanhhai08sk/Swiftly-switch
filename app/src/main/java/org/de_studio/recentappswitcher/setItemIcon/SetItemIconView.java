package org.de_studio.recentappswitcher.setItemIcon;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseActivity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;

import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 3/17/17.
 */

public class SetItemIconView extends BaseActivity<Void, SetItemIconPresenter> implements SetItemIconPresenter.View {
    private static final String TAG = SetItemIconView.class.getSimpleName();

    PublishSubject<String> searchSJ = PublishSubject.create();
    PublishSubject<String> drawableClickSJ = PublishSubject.create();

    final ArrayList<BitmapInfo> mAllItems = new ArrayList<BitmapInfo>();
    String iconPackPackage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iconPackPackage = getIntent().getStringExtra(Cons.PACKAGE_NAME_KEY);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SearchView searchView = new SearchView(this);
        searchView.setSearchableInfo(((SearchManager) getSystemService(Context.SEARCH_SERVICE)).getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);

        menu.add(android.R.string.search_go)
                .setIcon(android.R.drawable.ic_menu_search)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW + MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setActionView(searchView)
                .setOnActionExpandListener(this);
        return true;
    }



    @Override
    public PublishSubject<String> onSearch() {
        return searchSJ;
    }

    @Override
    public PublishSubject<String> onDrawableClick() {
        return drawableClickSJ;
    }

    @Override
    protected void inject() {

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
    public Bitmap getBitmap(String drawable) {
        return null;
    }

    @Override
    public void updateAdapter(SortedMap<String, String> sortedDrawableMap) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
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


    private static class BitmapInfo {
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
