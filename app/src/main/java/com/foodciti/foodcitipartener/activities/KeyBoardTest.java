package com.foodciti.foodcitipartener.activities;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.keyboards.ExtendedKeyBoard;

public class KeyBoardTest extends AppCompatActivity implements View.OnFocusChangeListener, View.OnKeyListener {
    private static final String TAG = "KeyBoardTest";
    private EditText et1, et2;
    private ExtendedKeyBoard extendedKeyBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_board_test);

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        extendedKeyBoard = findViewById(R.id.keyBoard);

        et1.setShowSoftInputOnFocus(false);
        et1.setTextIsSelectable(true);
        et1.setRawInputType(InputType.TYPE_CLASS_TEXT);
        et1.setOnKeyListener(this);
        et1.setOnFocusChangeListener(this);

        et2.setShowSoftInputOnFocus(false);
        et2.setTextIsSelectable(true);
        et2.setOnKeyListener(this);
        et2.setOnFocusChangeListener(this);

        et1.requestFocus();
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
        Log.e("KeyBoardTest", "------------keyCode: " + keyCode);
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
        }
        return false;
    }
}
