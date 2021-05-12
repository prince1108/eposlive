package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.FileDialogAdapter;

import java.io.File;
import java.util.Stack;

public class CustomFilePickDialog extends DialogFragment {
    private static final String TAG = "CustomFilePickDialog";

    private RecyclerView fileRV;
    private FileDialogAdapter fileDialogAdapter;
    private View back, open;

    private final Stack<File> fileStack = new Stack<>();
    private File parent = null;

    private String fileType = "";

    private Callback callback;

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private CustomFilePickDialog(){}

    public static CustomFilePickDialog newInstance() {
        CustomFilePickDialog filePickDialog = new CustomFilePickDialog();
        filePickDialog.setCancelable(true);
        return filePickDialog;
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
        View rootView = inflater.inflate(R.layout.dialog_custom_filepick, container, false);
        fileRV = rootView.findViewById(R.id.fileRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        fileRV.setLayoutManager(linearLayoutManager);
        back = rootView.findViewById(R.id.back);
        rootView.findViewById(R.id.close).setOnClickListener(v -> {
            dismiss();
        });

        parent = Environment.getExternalStorageDirectory();
        fileStack.push(parent);
        fileDialogAdapter = new FileDialogAdapter(getActivity(), parent.listFiles(f -> f.isDirectory()));
        fileDialogAdapter.setCallback(file -> {
            if (file.isDirectory()) {
                fileDialogAdapter.setFiles(file.listFiles(f -> {
                    if (f.isDirectory())
                        return true;
                    else if (!fileType.isEmpty()) {
                        return f.getName().endsWith(fileType);
                    } else
                        return false;
                }));
                fileDialogAdapter.notifyDataSetChanged();
                fileStack.push(file);
            } else {
                Log.e(TAG, "------------file: " + file);
                if (callback != null)
                    callback.onSelect(CustomFilePickDialog.this, file);
            }

            Log.e(TAG, "-----------parent: " + fileStack.peek());
        });

        fileRV.setAdapter(fileDialogAdapter);

        back.setOnClickListener(v -> {
            if (fileStack.isEmpty())
                return;
            fileStack.pop();
            if (!fileStack.isEmpty()) {
                File file = fileStack.peek();
                fileDialogAdapter.setFiles(file.listFiles(f -> {
                    if (f.isDirectory())
                        return true;
                    else if (!fileType.isEmpty()) {
                        return f.getName().endsWith(fileType);
                    } else
                        return false;
                }));
                fileDialogAdapter.notifyDataSetChanged();
                Log.e(TAG, "-----------parent: " + fileStack.peek());
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);
            dialog.getWindow().setLayout(width, height);
        }
    }

    public interface Callback {
        void onSelect(CustomFilePickDialog fileDialog, File file);
    }
}
