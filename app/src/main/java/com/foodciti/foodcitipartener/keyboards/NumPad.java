package com.foodciti.foodcitipartener.keyboards;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.foodciti.foodcitipartener.R;

public class NumPad extends ConstraintLayout implements View.OnClickListener {
    private TextView key_1, key_2, key_3, key_4, key_5, key_6, key_7, key_8, key_9, key_0;

    private TextView key_TAB;
    private ImageView key_BACKSPACE;
    private SparseArray<Integer> keyValues = new SparseArray<>();
    private InputConnection inputConnection;

    private String poundSym;

    public NumPad(Context context) {
        super(context);
        init(context);
    }

    public NumPad(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NumPad(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.num_pad_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        poundSym = getContext().getString(R.string.pound_symbol);

        key_1 = findViewById(R.id.one);
        key_1.setOnClickListener(this);

        key_2 = findViewById(R.id.two);
        key_2.setOnClickListener(this);

        key_3 = findViewById(R.id.three);
        key_3.setOnClickListener(this);

        key_4 = findViewById(R.id.four);
        key_4.setOnClickListener(this);

        key_5 = findViewById(R.id.five);
        key_5.setOnClickListener(this);

        key_6 = findViewById(R.id.six);
        key_6.setOnClickListener(this);

        key_7 = findViewById(R.id.seven);
        key_7.setOnClickListener(this);

        key_8 = findViewById(R.id.eight);
        key_8.setOnClickListener(this);

        key_9 = findViewById(R.id.nine);
        key_9.setOnClickListener(this);

        key_0 = findViewById(R.id.zero);
        key_0.setOnClickListener(this);


        key_BACKSPACE = findViewById(R.id.back_space);
        key_BACKSPACE.setOnClickListener(this);

        key_TAB = findViewById(R.id.tab);
        key_TAB.setOnClickListener(this);


        keyValues.put(R.id.one, KeyEvent.KEYCODE_1);
        keyValues.put(R.id.two, KeyEvent.KEYCODE_2);
        keyValues.put(R.id.three, KeyEvent.KEYCODE_3);
        keyValues.put(R.id.four, KeyEvent.KEYCODE_4);
        keyValues.put(R.id.five, KeyEvent.KEYCODE_5);
        keyValues.put(R.id.six, KeyEvent.KEYCODE_6);
        keyValues.put(R.id.seven, KeyEvent.KEYCODE_7);
        keyValues.put(R.id.eight, KeyEvent.KEYCODE_8);
        keyValues.put(R.id.nine, KeyEvent.KEYCODE_9);
        keyValues.put(R.id.zero, KeyEvent.KEYCODE_0);
        keyValues.put(R.id.back_space, KeyEvent.KEYCODE_DEL);
        keyValues.put(R.id.tab, KeyEvent.KEYCODE_TAB);
    }

    @Override
    public void onClick(View v) {
        if (inputConnection == null)
            return;

        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyValues.get(v.getId())));

        /*if (v.getId() == R.id.back_space) {
            CharSequence selectedText = inputConnection.getSelectedText(0);

            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                inputConnection.commitText("", 1);
            }
        } else if(v.getId()==R.id.tab) {
            inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_TAB));
        } else {
            String value = keyValues.get(v.getId());
            inputConnection.commitText(value, 1);
        }*/
    }

    public void setInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }
}
