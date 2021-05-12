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
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.keyboards.ExtendedKeyBoard;

public class AddCustomerCommentDialog extends DialogFragment implements View.OnFocusChangeListener, View.OnKeyListener {
    private static final String TAG = "CustomerCommentDialog";

    private static final String ARG_CARTITEM_ID = "cartitem_id";
    private static final String ARG_ORDER_TYPE = "order_type";
    private static final String ARG_ORDER_NOTE = "order_note";

    private long carItemId;
    private String orderType, orderNote;

    private EditText commentET;
    private ExtendedKeyBoard keyboard;

    private Callback callback;
    private OnDismissListener onDismissListener;

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private AddCustomerCommentDialog () {}

    public static AddCustomerCommentDialog newInstance(String note) {
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_NOTE, note);
        AddCustomerCommentDialog customerCommentDialog = new AddCustomerCommentDialog();
        customerCommentDialog.setArguments(args);
        customerCommentDialog.setCancelable(true);
        return customerCommentDialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(onDismissListener!=null)
            onDismissListener.onDismiss();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderNote = getArguments().getString(ARG_ORDER_NOTE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_cartitem_comment_layout, container, false);

        keyboard = rootView.findViewById(R.id.keyBoard);

        commentET = rootView.findViewById(R.id.field_comment);
        commentET.setShowSoftInputOnFocus(false);
        commentET.setOnFocusChangeListener(this);
        commentET.setOnKeyListener(this);
        commentET.setText(orderNote);

        rootView.findViewById(R.id.close).setOnClickListener(v -> {
            dismiss();
        });

        commentET.requestFocus();
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
    public void onFocusChange(View v, boolean hasFocus) {
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

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        Log.e(TAG, "-------------------keycode: " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_ESCAPE:
                if (callback != null) {
                    //Changes by Rashmi
                    //if (!commentET.getText().toString().trim().isEmpty()) {
                    callback.onSubmit(commentET.getText().toString().trim());
                    dismiss();
                    //} else
                    //   Toast.makeText(getActivity(), "Fields is mandatory", Toast.LENGTH_SHORT).show();
                }
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

    public interface Callback {
        void onSubmit(String name);
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
