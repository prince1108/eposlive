package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.callbacks.SwipeTouchHelperCallback;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.CartItem;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.List;

import io.realm.Realm;

import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemHolder> implements SwipeTouchHelperCallback.ActionCompletionContract {
    private static final String TAG = "CartItemAdapter";
    private Activity context;
    private List<CartItem> cartItems;
    private CartItemClickListener clicklistener;
    private static int selectedIndex = -1, previousIndex = -1;
    private Realm realm;
    private boolean disabled = false;
    public CartItemAdapter(Activity context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        if (!(context instanceof CartItemClickListener))
            throw new RuntimeException("context must implement CartItemClickListener");
        clicklistener = (CartItemClickListener) context;
        realm = RealmManager.getLocalInstance();
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public CartItem getSelectedCartItem() {
        Log.e(TAG, "---------last index: " + (getItemCount() - 1));
        if (selectedIndex == -1)
            return null;
        CartItem cartItem = null;
        try {
            Log.e(TAG, "-------------selected index: " + selectedIndex);
            cartItem = cartItems.get(selectedIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cartItem;
    }

    public void clearSelection() {
        selectedIndex = -1;
        notifyItemChanged(previousIndex);
    }

    public CartItem getItemAt(int position) {
        return cartItems.get(position);
    }

    public void deleteItem(CartItem cartItem) {
        realm.executeTransaction(r -> {
            cartItem.deleteFromRealm();
        });
    }

    public void deleteItemAt(int position) {
        realm.executeTransaction(r -> {
            cartItems.get(position).deleteFromRealm();
        });
    }

    public int indexOf(CartItem cartItem) {
        return cartItems.indexOf(cartItem);
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }


    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setSelection(int position) {
        previousIndex = selectedIndex;
        selectedIndex = position;
        notifyItemChanged(selectedIndex);
        if (previousIndex != -1)
            notifyItemChanged(previousIndex);
    }

    @NonNull
    @Override
    public CartItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Log.e(TAG, "ADDEDITEM: " + cartItem.toString());
        MenuItem menuItem = cartItem.menuItem;
        if (menuItem == null) {
            Log.e(TAG, "-----------------------menuitem is null------------------------");
            return;
        }
        holder.item.setText(cartItem.count + "*" + cartItem.name);
        double p=cartItem.count * cartItem.price;
        //Commented by Sourav Jha In order to fix the card addon remove issue.
        /*for(Addon a: cartItem.addons) {
            p+=a.price;
        }*/
        String price = String.format("%.2f", p);
        if (cartItem.addons.size() == 0) {
            holder.price.setText(price);
            holder.addonItem.setText("");
        } else {
            holder.price.setText(price);
            StringBuilder stringBuilder = new StringBuilder("");
            for (Addon a : cartItem.addons) {
                if (a.price > 0 && !a.isNoAddon && !cartItem.isFree) {
                    stringBuilder.append("+" + a.name);
                    stringBuilder.append(" (" + a.price + ")");
                } else if (a.isNoAddon) {
                    stringBuilder.append("-" + a.name);
                } else
                    stringBuilder.append("+" + a.name);
                stringBuilder.append("\n");
            }
            holder.addonItem.setText(stringBuilder);
        }
        if (!cartItem.comment.trim().isEmpty())
            holder.addonItem.append("\n*" + cartItem.comment.trim());

//        holder.comment.setText(cartItem.comment);
        if (position == selectedIndex) {
            setColor(holder.cardView);
        } else
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
    }

    @Override
    public int getItemCount() {
        if (cartItems == null)
            return 0;
        return cartItems.size();
    }

    @Override
    public void onViewSwiped(int position) {

    }

    public class CartItemHolder extends RecyclerView.ViewHolder {
        TextView item, price, addonItem/*, comment*/;
        LinearLayout addonLayout;
        View delete, special_note;
        CardView cardView;

        public CartItemHolder(final View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            price = itemView.findViewById(R.id.price);
            delete = itemView.findViewById(R.id.delete_item);
            special_note = itemView.findViewById(R.id.special_note);
            addonItem = itemView.findViewById(R.id.addon_item);
            addonLayout = itemView.findViewById(R.id.addon_layout);
//            comment = itemView.findViewById(R.id.comment);
            cardView = (CardView) itemView;
            itemView.setOnClickListener(v -> {
                if (disabled)
                    return;
                /*previousIndex = selectedIndex;
                selectedIndex = getAdapterPosition();
                notifyItemChanged(selectedIndex);
                if (previousIndex != -1)
                    notifyItemChanged(previousIndex);*/
                setSelection(getAdapterPosition());
                if (selectedIndex != -1 && !cartItems.get(selectedIndex).hasAddonOfOtherCatg)
                    clicklistener.onCartItemClick(cartItems.get(selectedIndex));
            });

            /*itemView.setOnLongClickListener(v -> {
                final int pos = getAdapterPosition();
                final CartItem cartItem = cartItems.get(pos);
                AddCustomerCommentDialog addCustomerCommentDialog = AddCustomerCommentDialog.newInstance(cartItem.comment);
                addCustomerCommentDialog.setCallback(comment -> {
                    realm.executeTransaction(r -> {
                        cartItem.comment = comment;
                        notifyItemChanged(pos);
                    });
                });

                AppCompatActivity appCompatActivity = (AppCompatActivity) context;
                addCustomerCommentDialog.show(appCompatActivity.getSupportFragmentManager(), null);
                return true;
            });*/

            /*itemView.setOnLongClickListener(v->{
                final int pos=getAdapterPosition();

                CustomAlertDialog customAlertDialog=CustomAlertDialog.getInstance();
                customAlertDialog.setTitle("Make this Item Free");
                customAlertDialog.setMessage("Are you sure you want to make this item free?");
                customAlertDialog.setPositiveButton("Yes", dialog->{
                    CartItem c=cartItems.get(pos);
                    realm.executeTransaction(r->{
                        c.price=0;
                        c.isFree=true;
                    });
                    setSelection(pos);
                    dialog.dismiss();
                });
                customAlertDialog.setNegativeButton("No", dialog -> {
                    dialog.dismiss();
                });

                customAlertDialog.show(((AppCompatActivity)context).getSupportFragmentManager(), null);
                return true;
            });*/

            delete.setOnClickListener(v -> {
                if (disabled)
                    return;
                try {
                    int pos = getAdapterPosition();
                    CartItem cartItem = cartItems.get(pos);
                    clicklistener.onCartItemDeleted(pos, cartItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            special_note.setOnClickListener(v->{
                final int pos = getAdapterPosition();
                final CartItem cartItem = cartItems.get(pos);
                clicklistener.onClickSpecialNote(cartItem.id, pos);
            });

            item.setOnClickListener(v -> {
                itemView.performClick();
            });

            addonLayout.setOnClickListener(v -> {
                itemView.performClick();
            });

            addonItem.setOnClickListener(v -> {
                itemView.performClick();
            });

            price.setOnClickListener(v -> {
                itemView.performClick();
            });
        }
    }

    public void removeItem(CartItem cartItem) {
        cartItems.remove(cartItem);
    }

    private void setColor(CardView view) {
        view.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorDarkGray));
    }

    public interface CartItemClickListener {
        void onCartItemClick(CartItem cartItem);

        void onCartItemLongClick(CartItem cartItem);

        void onCartItemDeleted(int position, CartItem cartItem);

        void onClickSpecialNote(long cartItemId, int position);
    }
}
