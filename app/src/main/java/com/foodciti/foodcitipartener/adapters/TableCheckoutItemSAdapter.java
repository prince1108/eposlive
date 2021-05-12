package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.content.ClipData;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.CartItem;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;

public class TableCheckoutItemSAdapter extends RecyclerView.Adapter<TableCheckoutItemSAdapter.CheckoutItemHolder> {
    private static final String TAG = "TblCheckoutItemAdapter";
    private Activity context;
    private List<CartItem> cartItems;
    private Listener mListener;
    private TextView total;
    private boolean dragEnabled = false;
    private Realm realm;

    public TableCheckoutItemSAdapter(Activity context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        realm = RealmManager.getLocalInstance();
    }

    public void setDragEnabled(boolean dragEnabled) {
        this.dragEnabled = dragEnabled;
//        notifyDataSetChanged();
    }

    public List<CartItem> getCartItems() {
        if(cartItems==null)
            cartItems = new ArrayList<>();
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
//        notifyDataSetChanged();
    }

    public void setmListener(Listener mListener) {
        this.mListener = mListener;
    }

    public DragListener getDragInstance() {
        /*if (mListener != null) {
            return new DragListener(mListener);
        } else {
            Log.e("Route Adapter: ", "Initialize listener first!");
            return null;
        }*/
        return new DragListener(mListener);
    }

    @NonNull
    @Override
    public CheckoutItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_checkoutitem_layout, parent, false);
        return new CheckoutItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutItemHolder holder, int position) {
        try {
            CartItem cartItem = cartItems.get(position);
            if (cartItem == null)
                return;
            Log.e(TAG, "ADDEDITEM: " + cartItem.toString());
            MenuItem menuItem = cartItem.menuItem;
            if (menuItem == null) {
                Log.e(TAG, "-----------------------menuitem is null------------------------");
                return;
            }

            holder.item.setText(cartItem.count + "*" + cartItem.menuItem.name);
            String price = String.format("%.2f", cartItem.count * cartItem.price);
            if (cartItem.addons.size() == 0) {
                holder.price.setText(price);
                holder.addonItem.setText("");
            } else {
                holder.price.setText(price);
                StringBuilder stringBuilder = new StringBuilder("");
                for (Addon a : cartItem.addons) {
                    if (a.price > 0 && !a.isNoAddon) {
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

            holder.cardView.setTag(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (cartItems == null)
            return 0;
        return cartItems.size();
    }

    public class CheckoutItemHolder extends RecyclerView.ViewHolder {
        TextView item, price, addonItem;
        LinearLayout addonLayout;
        CardView cardView;

        public CheckoutItemHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            price = itemView.findViewById(R.id.price);
            addonItem = itemView.findViewById(R.id.addon_item);
            addonLayout = itemView.findViewById(R.id.addon_layout);
            cardView = (CardView) itemView;
            /*cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    view.setVisibility(View.INVISIBLE);
                    return true;
                }
            });*/

            cardView.setOnDragListener(new DragListener(mListener));
            cardView.setOnClickListener(view -> {
                Log.e(TAG, "-----------------Tablecheckpititem clicked--------------");

            });

            AtomicReference<Boolean> startMove = new AtomicReference<>(false);
            cardView.setOnTouchListener((view, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                    /*    if(!dragEnabled)
                            return false;
                        ClipData data = ClipData.newPlainText("", "");
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                        view.startDrag(data, shadowBuilder, view, 0);
//                        view.setVisibility(View.INVISIBLE);
                        view.setVisibility(View.GONE);
                        return true;*/
                    }
                    break;
                    case MotionEvent.ACTION_MOVE: {
                        Log.e(TAG, "--------------------------ACTION_MOVE-----------------");
                        if (!dragEnabled) {
                            Log.e(TAG, "----------------DRAG DISABLED, RETURNING FALSE---------------");
                            return false;
                        }
                        /*if (startMove.get() == false) {
                            Log.e(TAG, "----------------------------inside startMove------------------------");
                            ClipData data = ClipData.newPlainText("", "");
                            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                            view.startDrag(data, shadowBuilder, view, 0);
//                        view.setVisibility(View.INVISIBLE);
                            view.setVisibility(View.GONE);
                            startMove.set(true);
                            return true;
                        }*/
                        Log.e(TAG, "----------------------------inside startMove------------------------");
                        ClipData data = ClipData.newPlainText("", "");
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                        view.startDrag(data, shadowBuilder, view, 0);
//                        view.setVisibility(View.INVISIBLE);
                        view.setVisibility(View.GONE);
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {

                    }
                }

                return false;
            });
        }
    }

    public class DragListener implements View.OnDragListener {

        boolean isDropped = false;
        Listener mListener;

        public DragListener(Listener listener) {
            this.mListener = listener;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;

                case DragEvent.ACTION_DRAG_ENTERED:
                    //v.setBackgroundColor(Color.LTGRAY);
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    //v.setBackgroundColor(Color.YELLOW);
                    break;

                case DragEvent.ACTION_DROP:

                    isDropped = true;
                    int positionSource = -1;
                    int positionTarget = -1;

                    View viewSource = (View) event.getLocalState();

                    Log.e(TAG, "----------------viewSource: " + viewSource.getClass().getSimpleName());
                    Log.e(TAG, "----------------view: " + v.getClass().getSimpleName());

                    int sourcePosition = (int) viewSource.getTag();
//                    int targetPosition=(int)v
                    RecyclerView source = (RecyclerView) viewSource.getParent();
                    if (!(v instanceof RecyclerView)) {
                        isDropped = false;
                        return false;
                    }
                    RecyclerView target = (RecyclerView) v;

                    if (source.equals(target)) {
                        isDropped = false;
                        return true;
                    }

                    TableCheckoutItemSAdapter sourceAdapter = (TableCheckoutItemSAdapter) source.getAdapter();
                    TableCheckoutItemSAdapter targetAdapter = (TableCheckoutItemSAdapter) target.getAdapter();

                    CartItem cartItem = sourceAdapter.getCartItems().get(sourcePosition);

                    targetAdapter.getCartItems().add(cartItem);
//                    targetAdapter.notifyItemInserted(targetAdapter.getItemCount()-1);
                    targetAdapter.notifyDataSetChanged();


                    sourceAdapter.getCartItems().remove(cartItem);
//                    sourceAdapter.notifyItemRemoved(sourcePosition);
                    sourceAdapter.notifyDataSetChanged();

                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    //v.setBackgroundColor(0);
                    break;

                default:
                    break;
            }

            if (!isDropped) {
                View vw = (View) event.getLocalState();
                vw.setVisibility(View.VISIBLE);
            }

            return true;
        }

    }

    public interface Listener {
        void setEmptyList(boolean visibility);
    }
}
