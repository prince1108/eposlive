package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.callbacks.ListItemTouchHelperCallback;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.utils.CommonMethods;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;


public class PrintOrderAdapter extends RecyclerView.Adapter<PrintOrderAdapter.CategoryHolder> implements ListItemTouchHelperCallback.ActionCompletionContract {
    private static final String TAG = "PrintOrderAdapter";
    private Context context;
    private MenuCategoryListener menuCategoryListener;
    private PopupWindow popupWindow;
    private List<MenuCategory> menuCategories;
    public int categoryIndex = -1;
    private Realm realm;

    public PrintOrderAdapter(Context context, List<MenuCategory> menuCategories) {
        this.context = context;
        this.menuCategories = menuCategories;
        this.realm = RealmManager.getLocalInstance();
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        MenuCategory category = menuCategories.get(position);
        holder.name.setText(category.getName());
        if (category.color != -1) {
            CommonMethods.setGradientDrawable(context, holder.itemView, category.color);
        }
        else {
            int color = ContextCompat.getColor(context, R.color.category_unselected);
            CommonMethods.setGradientDrawable(context, holder.itemView, color);
        }

      /*  if (category.isSelected())
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorVividTangerine));
        else {
            if (category.color != -1)
                holder.cardView.setCardBackgroundColor(category.color);
            else
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_unselected));
        }

        if (getTotalItems(category) > 0) {
            holder.counter.setVisibility(View.VISIBLE);
            holder.counter.setText(getTotalItems(category) + "");
        } else
            holder.counter.setVisibility(View.GONE);*/
    }


    @Override
    public int getItemCount() {
        return menuCategories.size();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        Log.e(TAG, "OLDPOSITION: " + oldPosition + ", NEWPOSITION: " + newPosition);
        MenuCategory item1 = null, item2 = null;
        try {
            if (oldPosition < newPosition) {
                for (int i = oldPosition; i < newPosition; i++) {
                    item1 = menuCategories.get(i);
                    item2 = menuCategories.get(i + 1);
                    swap(item1, item2);
                    Collections.swap(menuCategories, i, i + 1);
                }
            } else {
                for (int i = oldPosition; i > newPosition; i--) {
                    item1 = menuCategories.get(i);
                    item2 = menuCategories.get(i - 1);
                    swap(item1, item2);
                    Collections.swap(menuCategories, i, i - 1);
                }
            }
            notifyItemMoved(oldPosition, newPosition);
        } catch (Exception e) {
            Log.e(TAG, e + "");
            e.printStackTrace();
        }
    }

    private void swap(MenuCategory i, MenuCategory j) {
        realm.executeTransaction(r -> {
            int temp = i.getPrintOrder();
            i.setPrintOrder(j.getPrintOrder());
            j.setPrintOrder(temp);
        });
//        int temp = i.itemposition;
    }

    @Override
    public void onViewSwiped(int position) {

    }

    private void clearSelection() {
        for (MenuCategory m : menuCategories)
            m.setSelected(false);
    }

    public void setSelection(int index) {
        clearSelection();
        categoryIndex = index;
        menuCategories.get(index).setSelected(true);
        notifyDataSetChanged();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {
        TextView name, counter;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_name);
            counter = itemView.findViewById(R.id.counter);
        }
    }

    public interface MenuCategoryListener {
        void onMenuCatClick(int position, MenuCategory category);

        void onMenuCatLongClick(int position, MenuCategory category);
    }
}
