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


public class ConfirmationDialog extends DialogFragment implements View.OnClickListener
{
    public static final String TAG = "ConfirmationDialog";
    private String titleText, msg;
    private ConfirmationDialogListener listener;
    private TextView title;
    private TextView titleDesc;

    public static ConfirmationDialog getInstance(Bundle args) {
        ConfirmationDialog dialog = new ConfirmationDialog();
        dialog.setArguments(args);
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.base_dialog, container, false);
    }

    public void setListener(ConfirmationDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.titleText);
        titleDesc = view.findViewById(R.id.title_desc);


        Bundle args = getArguments();
        if (args.containsKey("title"))
            titleText = args.getString("title");
        if (args.containsKey("message")) {
            msg = args.getString("message");
            titleDesc.setText(msg);
        }
        title.setText(titleText);

        getDialog().setTitle(titleText);

        view.findViewById(R.id.yes).setOnClickListener(this);
        view.findViewById(R.id.no).setOnClickListener(this);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yes:
                if (listener != null) {
                    listener.onOkClicked();
                }
                dismiss();
                break;

            case R.id.no:
                if (listener != null) {
                    listener.onNoClicked();
                }
                dismiss();
                break;
        }
    }

    public interface ConfirmationDialogListener {
        void onOkClicked();
        void onNoClicked();
    }
}
