package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.keyboards.ExtendedKeyBoard;

public class PasswordDialog extends DialogFragment implements View.OnFocusChangeListener,View.OnKeyListener{
    private static final String TAG = "PasswordDialog";
    private PasswordDialog.OnClicklistener positiveClickListener, negativeClickListener;
    private PasswordDialog.OnDismissListener onDismissListener;
    private TextView positiveButton, negativeButton;
    private TextView title, message;
    private String titleStr, messageStr, positiveButtonStr, negativeButtonStr;
    private EditText password;
    private ExtendedKeyBoard keyBoard;
    private PasswordDialog(){}

    public static PasswordDialog getInstance() {
        PasswordDialog passwordDialog = new PasswordDialog();
        passwordDialog.setCancelable(true);
        return passwordDialog;
    }

    public void setMessage(String message) {
        messageStr = message;
    }

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setPositiveButton(String str, PasswordDialog.OnClicklistener onClickListener) {
        positiveButtonStr = str;
        positiveClickListener = onClickListener;
    }

    public void setNegativeButton(String str, PasswordDialog.OnClicklistener onClickListener) {
        negativeButtonStr = str;
        negativeClickListener = onClickListener;
    }

    public void setOnDismissListener(PasswordDialog.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(onDismissListener!=null)
            onDismissListener.onDismiss();
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
        View rootView = inflater.inflate(R.layout.dialog_password_layout, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int height=display.getHeight();
        view.findViewById(R.id.close).setOnClickListener(v -> {
            dismiss();
        });
        password = view.findViewById(R.id.passwordET);
        keyBoard = view.findViewById(R.id.keyBoard);
        int hight=(int)(height*0.40);
        keyBoard.getLayoutParams().height = hight;
        keyBoard.requestLayout();
        password.setShowSoftInputOnFocus(false);
        password.setOnKeyListener(this);
        password.setOnFocusChangeListener(this);
        title = view.findViewById(R.id.title);
        title.setText("Admin Password");
        message = view.findViewById(R.id.message);
        message.setText("Enter Admin Password");
        positiveButton = view.findViewById(R.id.positiveButton);
        positiveButton.setText(positiveButtonStr);
        positiveButton.setVisibility(View.GONE);
        positiveButton.setOnClickListener(v -> {
            positiveClickListener.onClick(PasswordDialog.this, password.getText().toString().trim());
        });
        negativeButton = view.findViewById(R.id.negativeButton);
        negativeButton.setText(negativeButtonStr);
        negativeButton.setOnClickListener(v -> {
            negativeClickListener.onClick(PasswordDialog.this, password.getText().toString().trim());
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.70);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
            dialog.getWindow().setLayout(width, height);
        }
    }

    public interface OnClicklistener {
        void onClick(PasswordDialog dialog, String password);
    }

    public interface OnDismissListener {
        void onDismiss();
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
        Log.e(TAG, "----------------------keyEvent---------------------");
        switch (keyCode) {
            case ExtendedKeyBoard.KEYCODE_DONE:
                Log.e(TAG, "-------------------keycode: " + keyCode);
                positiveClickListener.onClick(PasswordDialog.this, password.getText().toString().trim());
                return true;
            case KeyEvent.KEYCODE_CLEAR:
                Log.e(TAG, "-------------------keycode: " + keyCode);
                if (v instanceof EditText) {
                    EditText et = (EditText) v;
                    et.setText("");
                }
                return true;
        }
        return false;
    }

}

