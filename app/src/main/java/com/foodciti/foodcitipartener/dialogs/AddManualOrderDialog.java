package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.DialogFragment;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.keyboards.ExtendedKeyBoard;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.List;

import io.realm.Realm;

public class AddManualOrderDialog extends DialogFragment implements View.OnFocusChangeListener, View.OnKeyListener {
    private static final String TAG = "AddManualOrderDialog";

    private static final String ARG_CATEGORY_ID = "category_id";
    private static final String ARG_ORDER_TYPE = "order_type";

    private long categoryId;
    private String orderType;

    private EditText nameET, priceET;
    private ExtendedKeyBoard keyboard;

    private CheckBox tempItemCB;
    private AppCompatSpinner categorySpinner;

    private Realm realm;

    private Callback callback;
    List<MenuCategory> menuCategories;
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private AddManualOrderDialog(){}

    public static AddManualOrderDialog newInstance(long categoryId, String orderType) {
        Bundle args = new Bundle();
        args.putLong(ARG_CATEGORY_ID, categoryId);
        args.putString(ARG_ORDER_TYPE, orderType);
        AddManualOrderDialog addManualOrderDialog = new AddManualOrderDialog();
        addManualOrderDialog.setArguments(args);
        addManualOrderDialog.setCancelable(true);
        return addManualOrderDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getLong(ARG_CATEGORY_ID);
            orderType = getArguments().getString(ARG_ORDER_TYPE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_manual_order_layout, container, false);
        realm = RealmManager.getLocalInstance();

        keyboard = rootView.findViewById(R.id.keyBoard);

        nameET = rootView.findViewById(R.id.field_item);
        nameET.setShowSoftInputOnFocus(false);
        nameET.setOnKeyListener(this);
        nameET.setOnFocusChangeListener(this);

        priceET = rootView.findViewById(R.id.field_price);
        priceET.setShowSoftInputOnFocus(false);
        priceET.setOnKeyListener(this);
        priceET.setOnFocusChangeListener(this);

        tempItemCB = rootView.findViewById(R.id.tempItemCB);

        tempItemCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                categorySpinner.setVisibility(View.VISIBLE);
            } else {
                categorySpinner.setVisibility(View.GONE);
                categoryId = -1;
            }
        });

        rootView.findViewById(R.id.close).setOnClickListener(v -> {
            dismiss();
        });

        MenuCategory defaultCategory = realm.where(MenuCategory.class).equalTo("id", categoryId).findFirst();
        categorySpinner = rootView.findViewById(R.id.category_spinner);
        menuCategories = realm.where(MenuCategory.class).notEqualTo("name", Constants.ITEM_TYPE_COMMON).findAll();
        ArrayAdapter<MenuCategory> menuCategoryArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, menuCategories);
        categorySpinner.setAdapter(menuCategoryArrayAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MenuCategory menuCategory = menuCategories.get(position);
                categoryId = menuCategory.id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        categorySpinner.setSelection(menuCategories.indexOf(defaultCategory));

        nameET.requestFocus();
        return rootView;
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
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        Log.e(TAG, "-------------------keycode: " + keyCode);
        switch (keyCode) {
            case ExtendedKeyBoard.KEYCODE_DONE:
                if (callback != null) {
                    if (!nameET.getText().toString().trim().isEmpty() && !priceET.getText().toString().trim().isEmpty()) {
                        categoryId = tempItemCB.isChecked()?categoryId:-1;
                        if(categoryId==-1){
//                            MenuCategory menuCategory = menuCategories.get(0);
//                            categoryId = getArguments().getLong(ARG_CATEGORY_ID);
                        }
                        callback.onSubmit(nameET.getText().toString().trim(), priceET.getText().toString().trim(), categoryId);
                        dismiss();
                    } else
                        Toast.makeText(getActivity(), "All Fields are mandatory", Toast.LENGTH_SHORT).show();
                } else
                    Log.e(TAG, "------------callback is null--------------");
                return true;

            case KeyEvent.KEYCODE_CLEAR:
                if (view instanceof EditText) {
                    EditText et = (EditText) view;
                    et.setText("");
                    return true;
                }
                return false;
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.e(TAG, "------------------foucus changed---------------------");
        EditText editText = null;
        InputConnection ic = null;
        if (v instanceof EditText) {
            editText = (EditText) v;
            ic = editText.onCreateInputConnection(new EditorInfo());
        }

        if (hasFocus && ic != null) {
            keyboard.setInputConnection(ic);
        }
    }

    public interface Callback {
        void onSubmit(String name, String price, long categoryId);
    }
}
