package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.ColorItemAdapter;
import com.foodciti.foodcitipartener.keyboards.ExtendedKeyBoard;
import com.foodciti.foodcitipartener.realm_entities.ItemColor;
import com.foodciti.foodcitipartener.utils.RealmManager;

import io.realm.Realm;
import io.realm.RealmResults;

public class CategoryEditDialog extends DialogFragment implements View.OnFocusChangeListener, View.OnKeyListener {
    private static final String TAG = "CategoryEditDialog";

    private static final String ARG_POSITION = "position";
    private static final String ARG_ITEM_ID = "item_id";
    private static final String ARG_ITEM_TYPE = "item_type";
    private static final String ARG_ORDER_TYPE = "order_type";

    private int position;
    private long itemId;
    private String itemType, orderType;

    private TextView title;
    private View close;
    private Button openColorDialog;
    private EditText nameET;
    private String name = "", titleStr = "";
    private RecyclerView colorRV;
    private ExtendedKeyBoard keyBoard;
    private Callback callback;

    private Realm realm;

    private ColorItemAdapter colorItemAdapter = null;
    private RealmResults<ItemColor> itemColorRealmResultsAsync = null;

    private long selectedColorId = -1;

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /*public static AddonEditDialog newInstance(int position, long itemId, String itemType, String orderType) {
        AddonEditDialog itemEditDialog = new AddonEditDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putLong(ARG_ITEM_ID, itemId);
        args.putString(ARG_ITEM_TYPE, itemType);
        args.putString(ARG_ORDER_TYPE, orderType);
        itemEditDialog.setArguments(args);

        return itemEditDialog;
    }*/

    private CategoryEditDialog(){}

    public static CategoryEditDialog newInstance() {
        CategoryEditDialog categoryEditDialog = new CategoryEditDialog();
        categoryEditDialog.setCancelable(true);
        return categoryEditDialog;
    }

    @Override
    public void onDestroyView() {
        if (itemColorRealmResultsAsync != null)
            itemColorRealmResultsAsync.removeAllChangeListeners();
        super.onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
            itemId = getArguments().getLong(ARG_ITEM_ID);
            itemType = getArguments().getString(ARG_ITEM_TYPE);
            orderType = getArguments().getString(ARG_ORDER_TYPE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);

            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_categoryedit_layout, container, false);

        title = view.findViewById(R.id.title);
        title.setText(titleStr.trim());
        close = view.findViewById(R.id.close);
        openColorDialog = view.findViewById(R.id.openColorDialog);
        openColorDialog.setOnClickListener(v -> {
            ColorDialog colorDialog = ColorDialog.newInstance();
            colorDialog.setCallback(dialog -> {
                setColorOptions();
                dialog.dismiss();
            });
            colorDialog.show(getFragmentManager(), null);
        });
        nameET = view.findViewById(R.id.name);
        nameET.setText(name.trim());

        colorRV = view.findViewById(R.id.colorRV);
        keyBoard = view.findViewById(R.id.keyBoard);

        nameET.setShowSoftInputOnFocus(false);
        nameET.setOnFocusChangeListener(this);
        nameET.setOnKeyListener(this);


        close.setOnClickListener(v -> {
            dismiss();
        });

        realm = RealmManager.getLocalInstance();

        setColorOptions();


        return view;
    }

    private void setColorOptions() {
        itemColorRealmResultsAsync = realm.where(ItemColor.class).findAllAsync();
        itemColorRealmResultsAsync.addChangeListener(results -> {
            if (!results.isEmpty()) {
                if (colorRV.getVisibility() == View.GONE)
                    colorRV.setVisibility(View.VISIBLE);
                selectedColorId = results.get(0).getId();
                colorItemAdapter = new ColorItemAdapter(getActivity(), results);
                colorItemAdapter.setCallback(id -> {
                    selectedColorId = id;
                });
                colorRV.setLayoutManager(new GridLayoutManager(getActivity(), 8));
                colorRV.setAdapter(colorItemAdapter);
                openColorDialog.setVisibility(View.GONE);
            } else {
                colorRV.setVisibility(View.GONE);
                openColorDialog.setVisibility(View.VISIBLE);
            }
        });
    }

    private void done2() {
        if (callback != null)
            callback.onClickDone(CategoryEditDialog.this, nameET, selectedColorId);
    }
    /*private void done(long colorId) {
        String price="";
        MenuItem menuItem=realm.where(MenuItem.class).equalTo("id", itemId).findFirst();
        AtomicReference<MenuItem> atomicReference=new AtomicReference<>(menuItem);
        ItemColor itemColor=realm.where(ItemColor.class).equalTo("id", colorId).findFirst();
        if (checkbox.isChecked()) {
            price=priceET.getText().toString().trim();
//            price.append(collectionPriceET.getText().toString());
        } else {
//            price.delete(0, price.length());
            price=deliveryPriceET.getText().toString();
        }

        realm.executeTransaction(r->{
            if (atomicReference.get() == null) {
                Number maxId = r.where(MenuItem.class).max("id");
                // If there are no rows, currentId is null, so the next id must be 1
                // If currentId is not null, increment it by 1
                long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                // User object created with the new Primary key

                atomicReference.set(r.createObject(MenuItem.class, nextId));
                String name = nameET.getText().toString().trim();
                String capitalizedName = StringHelper.capitalizeEachWord(name);
                atomicReference.get().name = capitalizedName;
                atomicReference.get().last_updated = new Date().getTime();
                double collectionPrice = priceET.getText().toString().trim().equals("") ? 0 : Double.parseDouble(priceET.getText().toString().trim());
                atomicReference.get().collectionPrice = collectionPrice;
                if (deliveryPriceET.getVisibility() == View.VISIBLE) {
                    double deliveryPrice = deliveryPriceET.getText().toString().trim().equals("") ? 0 : Double.parseDouble(deliveryPriceET.getText().toString().trim());
                    atomicReference.get().deliveryPrice = deliveryPrice;
                } else
                    atomicReference.get().deliveryPrice = collectionPrice;

                if (orderType.equals(Constants.TYPE_COLLECTION))
                    atomicReference.get().price = atomicReference.get().price;
                else
                    atomicReference.get().price = atomicReference.get().deliveryPrice;
                atomicReference.get().color = Color.parseColor(itemColor.getHexCode().trim());
                atomicReference.get().type = itemType;
                Log.e(TAG, "****************************itemType: " + itemType);
                if(callback!=null)
                    callback.onClickDone(AddonEditDialog.this, position, atomicReference.get().id, true);
            } else {
                String name = nameET.getText().toString().trim();
                String capitalizedName = StringHelper.capitalizeEachWord(name);
                atomicReference.get().name = capitalizedName;
                atomicReference.get().last_updated = new Date().getTime();
                String collectionPrice = (priceET.getText().toString().trim().isEmpty()) ? "0" : priceET.getText().toString().trim();
                atomicReference.get().collectionPrice = (Double.parseDouble(collectionPrice));
                Log.e(TAG, "----------------modified name: " + nameET.getText() + "; modified price: " + priceET.getText());
                if (deliveryPriceET.getVisibility() == View.VISIBLE) {
                    String deliveryPrice = (deliveryPriceET.getText().toString().trim().isEmpty()) ? "0" : deliveryPriceET.getText().toString().trim();
                    atomicReference.get().deliveryPrice = Double.parseDouble(deliveryPrice);
                } else
                    atomicReference.get().deliveryPrice = Double.parseDouble(collectionPrice);
                if (orderType.equals(Constants.TYPE_COLLECTION))
                    atomicReference.get().price = atomicReference.get().collectionPrice;
                else
                    atomicReference.get().price = atomicReference.get().deliveryPrice;
                atomicReference.get().color = Color.parseColor(itemColor.getHexCode().trim());
                if(callback!=null)
                    callback.onClickDone(AddonEditDialog.this, position, atomicReference.get().id, false);
            }
        });
    }*/

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.e(TAG, "-----------------focus changed------------");
        EditText editText = null;
        InputConnection inputConnection = null;
        if (v instanceof EditText) {
            editText = (EditText) v;
            inputConnection = editText.onCreateInputConnection(new EditorInfo());
        }

        if (hasFocus && inputConnection != null)
            keyBoard.setInputConnection(inputConnection);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.e(TAG, "------------keyCode: " + keyCode);
        Log.e(TAG, "----------------meta state: " + event.getMetaState());
        Log.e(TAG, "----------isCapsOn: " + event.isCapsLockOn());

        EditText editText = null;
        if (v instanceof EditText)
            editText = (EditText) v;
        switch (keyCode) {
            case KeyEvent.KEYCODE_CLEAR:
                if (editText != null)
                    editText.setText("");
                return true;

            case ExtendedKeyBoard.KEYCODE_DONE:
                if (callback != null)
                    done2();
                return true;
        }
        return false;
    }

    public interface Callback {
        void onClickDone(CategoryEditDialog dialog, EditText name, long colorId);
    }
}
