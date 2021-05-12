package com.foodciti.foodcitipartener.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.keyboards.ExtendedKeyBoard;


public class SpecialNoteDialog extends DialogFragment implements View.OnFocusChangeListener, View.OnKeyListener {
    private static final String TAG = "SpecialNoteDialog";
    public static EditText noteET, customeraddress;
    private SpecialNoteListener mListener;
    private ImageView closebtn;

    private static final String ARG_NOTES = "notes";

    private String notes;
    private ExtendedKeyBoard extendedKeyBoard;

    private SpecialNoteDialog(){}

    public static SpecialNoteDialog getInstance(String notes) {
        SpecialNoteDialog specialNoteDialog = new SpecialNoteDialog();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_NOTES, notes);
        specialNoteDialog.setArguments(bundle);
        specialNoteDialog.setCancelable(true);
        return specialNoteDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            notes = getArguments().getString(ARG_NOTES);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_special_note_layout, container,
                false);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        closebtn = rootView.findViewById(R.id.close);
        closebtn.setOnClickListener(v -> {
            dismiss();
        });

        extendedKeyBoard = rootView.findViewById(R.id.keyBoard);
        noteET = rootView.findViewById(R.id.houseNo);
        noteET.setShowSoftInputOnFocus(false);
        noteET.setOnFocusChangeListener(this);
        noteET.setOnKeyListener(this);
        noteET.requestFocus();
        if (notes != null && !notes.trim().isEmpty())
            noteET.setText(notes);

        return rootView;
    }

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
            extendedKeyBoard.setInputConnection(inputConnection);
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

            case KeyEvent.KEYCODE_ESCAPE:
                if (noteET.getText().toString().length() != 0) {
                    mListener.returnSpecialNotes(noteET.getText().toString().trim());
                    dismiss();
                }
                return true;
        }
        return false;
    }


    public interface SpecialNoteListener {
        void returnSpecialNotes(String returnbackpound);
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

    public int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SpecialNoteListener) {
            mListener = (SpecialNoteListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
