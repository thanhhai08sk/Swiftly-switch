package org.de_studio.recentappswitcher.faqs;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseActivity;

import butterknife.BindView;

/**
 * Created by HaiNguyen on 2/24/17.
 */

public class FaqsView extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    FaqsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] titles = getResources().getStringArray(R.array.faq_titles);
        String[] answers = getResources().getStringArray(R.array.faq_answers);
        adapter = new FaqsAdapter(titles, answers, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void inject() {
        // do nothing
    }

    @Override
    protected int getLayoutId() {
        return R.layout.faqs_view;
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
}
