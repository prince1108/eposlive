package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.keyboards.ExtendedKeyBoard;
import com.foodciti.foodcitipartener.realm_entities.ItemColor;
import com.foodciti.foodcitipartener.utils.RealmManager;

import io.realm.Realm;

public class ColorDialog extends DialogFragment implements View.OnFocusChangeListener, View.OnKeyListener {
    private static final String TAG = "ColorDialog";
    private int color = 0;
    private int progressRed, progressBlue, progressGreen;
    private CardView hexCodeCardView;
    private Realm realm;
    private EditText colorName, hexCode;
    private ExtendedKeyBoard extendedKeyBoard;

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private ColorDialog(){}

    public static ColorDialog newInstance() {
        ColorDialog colorDialog = new ColorDialog();
        colorDialog.setCancelable(true);
        return colorDialog;
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
        View view = inflater.inflate(R.layout.color_dialog_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        realm = RealmManager.getLocalInstance();
        extendedKeyBoard = view.findViewById(R.id.keyBoard);

        colorName = view.findViewById(R.id.colorNameET);
        colorName.setShowSoftInputOnFocus(false);
        colorName.setOnFocusChangeListener(this);
        colorName.setOnKeyListener(this);
        hexCodeCardView = view.findViewById(R.id.hex_code_container);

        AppCompatSeekBar red = view.findViewById(R.id.Red);
        AppCompatSeekBar blue = view.findViewById(R.id.Blue);
        AppCompatSeekBar green = view.findViewById(R.id.Green);

        hexCode = view.findViewById(R.id.hex_code);
        hexCode.setShowSoftInputOnFocus(false);
        hexCode.setOnFocusChangeListener(this);
        hexCode.setOnKeyListener(this);

        final TextView progressRedTV = view.findViewById(R.id.progressRed);
        final TextView progressGreenTV = view.findViewById(R.id.progressGreen);
        final TextView progressBlueTV = view.findViewById(R.id.progressBlue);

        String hexColor = String.format("#%06X", (0xFFFFFF & color));
        hexCode.setText(hexColor);
        hexCodeCardView.setCardBackgroundColor(Color.parseColor(hexColor.trim()));

        hexCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                StringBuilder str = new StringBuilder(s);
                int cl = 0;
                try {
                    if (!s.toString().startsWith("#")) {

                        str.insert(0, '#');
                        cl = Color.parseColor(str.toString());
                    }
                    cl = Color.parseColor(s.toString());
                    hexCodeCardView.setCardBackgroundColor(cl);

                    /*float r=   (cl >> 16) & 0xFF;
                    float g= (cl >> 8) & 0xFF;
                    float b=  (cl >> 0) & 0xFF;
                    float alpha= (cl >> 24) & 0xFF;*/
                    int r = Color.red(cl);
                    int g = Color.green(cl);
                    int b = Color.blue(cl);

                    red.setProgress(r);
                    green.setProgress(g);
                    blue.setProgress(b);
                    Log.d(TAG, "R: " + r);
                    Log.d(TAG, "G: " + g);
                    Log.d(TAG, "B: " + b);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressRed = progress;
                progressRedTV.setText(progress + "");
                color = Color.argb(255, progress, progressGreen, progressBlue);
                hexCodeCardView.setCardBackgroundColor(color);
                hexCode.setText(String.format("#%06X", (0xFFFFFF & color)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                color = Color.argb(255, progressRed, progressGreen, progress);
                progressBlue = progress;
                progressBlueTV.setText(progress + "");
                hexCodeCardView.setCardBackgroundColor(color);
                hexCode.setText(String.format("#%06X", (0xFFFFFF & color)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressGreen = progress;
                progressGreenTV.setText(progress + "");
                color = Color.argb(255, progressRed, progress, progressBlue);
                hexCodeCardView.setCardBackgroundColor(color);
                hexCode.setText(String.format("#%06X", (0xFFFFFF & color)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        View close = view.findViewById(R.id.close);
        close.setOnClickListener(v -> {
            dismiss();
        });

    }

    private void done() {
        try {
            Color.parseColor(hexCode.getText().toString().trim());
            final ItemColor itemColor = realm.where(ItemColor.class).equalTo("hexCode", hexCode.getText().toString().trim()).findFirst();
            if (itemColor == null) {
                realm.executeTransaction(r -> {
                    Number maxId = r.where(ItemColor.class).max("id");
                    long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                    ItemColor newColor = r.createObject(ItemColor.class, nextId);
                    newColor.setHexCode(hexCode.getText().toString().trim());
                    newColor.setName(colorName.getText().toString().trim());
                });

                if (callback != null)
                    callback.onClickDone(ColorDialog.this);

            } else {
                Toast.makeText(getActivity(), "This color Already Exists", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            /*dialog.getWindow()
                    .setLayout((int) (getScreenWidth(getActivity()) * 0.99), (int) (getScreenWidth(getActivity()) * 0.99));*/
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);

            dialog.getWindow().setLayout(width, height);
        }
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

            case ExtendedKeyBoard.KEYCODE_DONE:
                done();
                return true;
        }
        return false;
    }

    public interface Callback {
        void onClickDone(ColorDialog dialog);
    }
}
