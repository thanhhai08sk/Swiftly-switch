package org.de_studio.recentappswitcher.setItemIcon;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseActivity;

import java.util.SortedMap;

import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 3/17/17.
 */

public class SetItemIconView extends BaseActivity<Void, SetItemIconPresenter> implements SetItemIconPresenter.View {


    PublishSubject<String> searchSJ = PublishSubject.create();
    PublishSubject<String> drawableClickSJ = PublishSubject.create();


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
}
