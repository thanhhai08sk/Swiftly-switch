package org.de_studio.recentappswitcher.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by HaiNguyen on 12/2/16.
 */

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spacing;

    public GridSpacingItemDecoration( int spacing) {
        this.spacing = spacing / 2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        int position = parent.getChildAdapterPosition(view); // item position
//        int column = position % spanCount; // item column
//
//        if (includeEdge) {
//            outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
//            outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
//
//            if (position < spanCount) { // top edge
//                outRect.top = spacing;
//            }
//            outRect.bottom = spacing; // item bottom
//        } else {
//            outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
//            outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
//            if (position >= spanCount) {
//                outRect.top = spacing; // item top
//            }
//        }
//        RecyclerView.LayoutParams params = ((RecyclerView.LayoutParams) view.getLayoutParams());
//        params.width = params.width + spacing;
//        params.height = params.height + spacing;

        view.setPadding(spacing,spacing,spacing,spacing);
    }


}
