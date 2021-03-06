package org.de_studio.recentappswitcher.base;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/9/16.
 */

public class DragAndDropCallback extends ItemTouchHelper.Callback {
    private static final String TAG = DragAndDropCallback.class.getSimpleName();
    PublishSubject<MoveData> moveItemSubject;
    PublishSubject<DropData> dropItemSubject;
    PublishSubject<Coord> currentlyDragSubject;
    boolean isLastCallActive = true;

    public DragAndDropCallback(PublishSubject<MoveData> moveItemSubject, PublishSubject<DropData> dropItemSubject, PublishSubject<Coord> currentlyDragSubject) {
        this.dropItemSubject = dropItemSubject;
        this.moveItemSubject = moveItemSubject;
        this.currentlyDragSubject = currentlyDragSubject;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        Log.e(TAG, "onSelectedChanged: ");
        if (viewHolder != null) {
            moveItemSubject.onNext(null);
        }
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlag, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int from = viewHolder.getAdapterPosition();
        int to = target.getAdapterPosition();
        Log.e(TAG, "onMove: from " + from + " to " + to);
        MoveData moveData = new MoveData(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        moveItemSubject.onNext(moveData);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }


    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }


    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (isLastCallActive && !isCurrentlyActive ) {
            Log.e(TAG, "onChildDraw: drop item " + viewHolder.getAdapterPosition());
            Coord coord = new Coord();
            viewHolder.itemView.getLocationOnScreen(coord.xy);
            dropItemSubject.onNext(new DropData(viewHolder.getAdapterPosition(),coord.xy[0], coord.xy[1]));
//            clearView(recyclerView,viewHolder);

        }
        if (isCurrentlyActive) {
            Coord coord = new Coord();
            viewHolder.itemView.getLocationOnScreen(coord.xy);
            currentlyDragSubject.onNext(coord);
            viewHolder.itemView.setScaleX(1.3f);
            viewHolder.itemView.setScaleY(1.3f);
        } else {
            viewHolder.itemView.setScaleX(1.0f);
            viewHolder.itemView.setScaleY(1.0f);
        }
        isLastCallActive = isCurrentlyActive;

    }

    public class MoveData{
        public int from;
        public int to;
        public MoveData(int from, int to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MoveData moveData = (MoveData) o;

            if (from != moveData.from) return false;
            return to == moveData.to;

        }

        @Override
        public int hashCode() {
            int result = from;
            result = 31 * result + to;
            return result;
        }
    }

    public class DropData {
        public int position;
        public float dropX;
        public float dropY;

        public DropData(int position, float dropX, float dropY) {
            this.position = position;
            this.dropX = dropX;
            this.dropY = dropY;
        }
    }

    public class Coord{
        public int[] xy = new int[2];

        public Coord() {
        }
    }


}
