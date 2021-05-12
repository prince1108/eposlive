package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foodciti.foodcitipartener.R;

public class CustomAlertDialog extends DialogFragment {
    private static final String TAG = "CustomAlertDialog";
    private OnClicklistener positiveClickListener, negativeClickListener;
    private OnDismissListener onDismissListener;
    private TextView positiveButton, negativeButton;
    private TextView title, message;
    private String titleStr, messageStr, positiveButtonStr, negativeButtonStr;

    private CustomAlertDialog(){}

    public static CustomAlertDialog getInstance() {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog();
        customAlertDialog.setCancelable(true);
        return customAlertDialog;
    }

    public void setMessage(String message) {
        messageStr = message;
    }

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setPositiveButton(String str, OnClicklistener onClickListener) {
        positiveButtonStr = str;
        positiveClickListener = onClickListener;
    }

    public void setNegativeButton(String str, OnClicklistener onClickListener) {
        negativeButtonStr = str;
        negativeClickListener = onClickListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
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
        View rootView = inflater.inflate(R.layout.custom_alert_layout, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.close).setOnClickListener(v -> {
            dismiss();
        });
        title = view.findViewById(R.id.title);
        title.setText(titleStr);
        message = view.findViewById(R.id.message);
        message.setText(messageStr);
        positiveButton = view.findViewById(R.id.positiveButton);
        positiveButton.setText(positiveButtonStr);
        positiveButton.setOnClickListener(v -> {
            positiveClickListener.onClick(CustomAlertDialog.this);
        });
        negativeButton = view.findViewById(R.id.negativeButton);
        negativeButton.setText(negativeButtonStr);
        negativeButton.setOnClickListener(v -> {
            negativeClickListener.onClick(CustomAlertDialog.this);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.45);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.35);
            dialog.getWindow().setLayout(width, height);
        }
    }

    public interface OnClicklistener {
        void onClick(CustomAlertDialog dialog);
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
