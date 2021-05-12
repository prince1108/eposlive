package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.FileDialogAdapter;

import java.io.File;
import java.util.Stack;

public class CustomFileDialog extends DialogFragment {
    private static final String TAG = "CustomFileDialog";

    private TextView select, title;
    private RecyclerView fileRV;
    private FileDialogAdapter fileDialogAdapter;
    private View back, open;

    private final Stack<File> fileStack = new Stack<>();
    private File parent = null;

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private CustomFileDialog(){}
    public static CustomFileDialog newInstance() {
        CustomFileDialog customFileDialog = new CustomFileDialog();
        customFileDialog.setCancelable(true);
        return customFileDialog;
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
        View rootView = inflater.inflate(R.layout.custom_file_dialog_layout, container, false);
        fileRV = rootView.findViewById(R.id.fileRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        fileRV.setLayoutManager(linearLayoutManager);
        select = rootView.findViewById(R.id.select);
        title = rootView.findViewById(R.id.title);
//        open=rootView.findViewById(R.id.open);
        back = rootView.findViewById(R.id.back);
        rootView.findViewById(R.id.close).setOnClickListener(v -> {
            dismiss();
        });

        parent = Environment.getExternalStorageDirectory();
        fileStack.push(parent);
        select.setText("Select \"" + fileStack.peek() + "\"");
        title.setText(fileStack.peek().getPath());
        fileDialogAdapter = new FileDialogAdapter(getActivity(), parent.listFiles(f -> f.isDirectory()), false);
        fileDialogAdapter.setCallback(file -> {
            if (file.isDirectory()) {
//                fileDialogAdapter.setFiles(file.listFiles(f->f.isDirectory()));
                fileDialogAdapter.setFiles(file.listFiles());
                fileDialogAdapter.notifyDataSetChanged();
                fileStack.push(file);
                select.setText("Select \"" + fileStack.peek() + "\"");
                title.setText(fileStack.peek().getPath());
            } else {
                Log.e(TAG, "------------file: " + file);
                if (callback != null)
                    callback.onSelect(CustomFileDialog.this, file);
            }

            Log.e(TAG, "-----------parent: " + fileStack.peek());
        });
        fileRV.setAdapter(fileDialogAdapter);
//        RecyclerViewItemSelectionAfterLayoutUpdate.on(fileRV);

        back.setOnClickListener(v -> {
            if (fileStack.isEmpty())
                return;
            fileStack.pop();
            if (!fileStack.isEmpty()) {
                File file = fileStack.peek();
                select.setText("Select \"" + fileStack.peek() + "\"");
                title.setText(fileStack.peek().getPath());
//                fileDialogAdapter.setFiles(file.listFiles(f->f.isDirectory()));
                fileDialogAdapter.setFiles(file.listFiles());
                fileDialogAdapter.notifyDataSetChanged();
                Log.e(TAG, "-----------parent: " + fileStack.peek());
            }
        });

        select.setOnClickListener(v -> {
            Log.e(TAG, "------------file: " + fileStack.peek());
            if (callback != null) {
                callback.onSelect(CustomFileDialog.this, fileStack.peek());
            }
        });

        /*open.setOnClickListener(v->{
            File file=fileDialogAdapter.getCurrentSelection();
            if(file.isDirectory()) {
                fileDialogAdapter.setFiles(file.listFiles(f->f.isDirectory()));
                fileDialogAdapter.notifyDataSetChanged();
                RecyclerViewItemSelectionAfterLayoutUpdate.on(fileRV);
                fileStack.push(file);
            }
            Log.e(TAG, "-----------parent: "+fileStack.peek());
        });*/

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
        void onSelect(CustomFileDialog fileDialog, File file);
    }
}
