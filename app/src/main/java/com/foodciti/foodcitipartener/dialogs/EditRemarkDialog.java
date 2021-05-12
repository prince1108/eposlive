package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.SpinnerAdapter;
import com.foodciti.foodcitipartener.keyboards.ExtendedKeyBoard;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.RemarkType;
import com.foodciti.foodcitipartener.utils.RealmManager;

import io.realm.Realm;
import io.realm.RealmResults;

public class EditRemarkDialog extends DialogFragment implements View.OnFocusChangeListener, View.OnKeyListener {
    private static final String TAG = "EditRemarkDialog";

    private static final String ARG_CUSTOMER_ID = "customer_id";

    private long customerId;

    private Callback callback;

    private String title;

    private ExtendedKeyBoard keyBoard;

    private CustomerInfo customerInfo;

    private EditText remarksET;
    private Spinner spinner;

    private Realm realm;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private EditRemarkDialog(){}

    public static EditRemarkDialog newInstance(long customerId) {
        Bundle args = new Bundle();
        args.putLong(ARG_CUSTOMER_ID, customerId);
        EditRemarkDialog editRemarkDialog = new EditRemarkDialog();
        editRemarkDialog.setArguments(args);
        editRemarkDialog.setCancelable(true);
        return editRemarkDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customerId = getArguments().getLong(ARG_CUSTOMER_ID);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_edit_remarks_layout, container,
                false);

        realm = RealmManager.getLocalInstance();

        customerInfo = realm.where(CustomerInfo.class).equalTo("id", customerId).findFirst();

        TextView titleTV = rootView.findViewById(R.id.title);
        if (title != null)
            titleTV.setText(title);

        rootView.findViewById(R.id.close).setOnClickListener(v -> {
            dismiss();
        });

        keyBoard = rootView.findViewById(R.id.keyBoard);
        remarksET = rootView.findViewById(R.id.remarksET);
        remarksET.setShowSoftInputOnFocus(false);
        remarksET.setOnFocusChangeListener(this);
        remarksET.setOnKeyListener(this);
        remarksET.setText(customerInfo.getRemarks());

        spinner = rootView.findViewById(R.id.spinner_remarksType);

        RealmResults<RemarkType> remarkTypes = realm.where(RemarkType.class).findAll();

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), remarkTypes);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                remarksET.setBackgroundColor(remarkTypes.get(position).getColor());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner.setAdapter(spinnerAdapter);

        RemarkType remarkType = realm.where(RemarkType.class).equalTo("type", customerInfo.getRemarkStatus()).findFirst();
        if (remarkType != null)
            spinner.setSelection(remarkTypes.indexOf(remarkType));
        else
            spinner.setSelection(0);

        remarksET.requestFocus();

        return rootView;
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
    public void onFocusChange(View v, boolean hasFocus) {
        EditText editText = null;
        InputConnection ic = null;
        if (v instanceof EditText) {
            editText = (EditText) v;
            ic = editText.onCreateInputConnection(new EditorInfo());
        }

        if (hasFocus && ic != null) {
            keyBoard.setInputConnection(ic);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        EditText editText = null;
        if (v instanceof EditText)
            editText = (EditText) v;
        switch (keyCode) {
            case ExtendedKeyBoard.KEYCODE_DONE:
                save();
                if (callback != null)
                    callback.onSubmit();
                dismiss();
                return true;
            case KeyEvent.KEYCODE_CLEAR:
                if (editText != null)
                    editText.setText("");
                return true;
        }
        return false;
    }

    public interface Callback {
        void onSubmit();
    }

    private void save() {
        realm.executeTransaction(r -> {
            customerInfo.setRemarks(remarksET.getText().toString().trim());
            RemarkType remarkType = (RemarkType) spinner.getSelectedItem();
            customerInfo.setRemarkStatus(remarkType.getType());
        });
    }
}
