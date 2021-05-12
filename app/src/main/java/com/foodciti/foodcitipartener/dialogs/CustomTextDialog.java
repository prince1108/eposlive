package com.foodciti.foodcitipartener.dialogs;


import android.app.Dialog;
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

public class CustomTextDialog extends DialogFragment {
    private static final String TAG = "CustomAlertDialog";
    private CustomAlertDialog.OnClicklistener positiveClickListener, negativeClickListener;
    private TextView positiveButton, negativeButton;
    private TextView title, message;
    private String titleStr, messageStr, positiveButtonStr, negativeButtonStr;

    private CustomTextDialog(){}

    public static CustomTextDialog getInstance() {
        CustomTextDialog textDialog = new CustomTextDialog();
        textDialog.setCancelable(true);
        return textDialog;
    }

    public void setMessage(String message) {
        messageStr = message;
    }

    public void setTitle(String title) {
        titleStr = title;
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
        View rootView = inflater.inflate(R.layout.remarks_layout, container, false);
        View close = rootView.findViewById(R.id.close);
        close.setOnClickListener(v -> {
            dismiss();
        });
        TextView title = rootView.findViewById(R.id.title);
        title.setText("Remarks");
        TextView remarks = rootView.findViewById(R.id.remarks);
        remarks.setText(messageStr);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.4);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.3);
            dialog.getWindow().setLayout(width, height);
        }
    }
}
