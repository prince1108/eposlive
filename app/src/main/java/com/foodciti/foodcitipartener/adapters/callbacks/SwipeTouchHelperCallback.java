package com.foodciti.foodcitipartener.adapters.callbacks;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeTouchHelperCallback extends ItemTouchHelper.Callback {
    private ActionCompletionContract completionContract;
    public SwipeTouchHelperCallback(ActionCompletionContract completionContract) {
        this.completionContract = completionContract;
    }
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
//        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        int swipeFlags = ItemTouchHelper.LEFT;
//        return makeMovementFlags(dragFlags, swipeFlags);
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        completionContract.onViewSwiped(viewHolder.getAdapterPosition());
    }

    public interface ActionCompletionContract {
        void onViewSwiped(int position);
    }
}
