package com.foodciti.foodcitipartener.keyboards;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foodciti.foodcitipartener.R;

public class PrimaryKeyboard extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "PrimaryKeyboard";
    private TextView key_1, key_2, key_3, key_4, key_5, key_6, key_7, key_8, key_9, key_0;
    private TextView key_A, key_B, key_C, key_D, key_E, key_F, key_G, key_H, key_I, key_J,
            key_K, key_L, key_M, key_N, key_O, key_P, key_Q, key_R, key_S, key_T, key_U, key_V, key_W, key_X, key_Y, key_Z;
    private TextView key_DEL, key_TAB, key_SPACE, key_ENTER, key_DOT, key_PLUS_SIGN, key_POUND, key_QUESTION_MARK, key_HYPHEN;
    private ImageView key_BACKSPACE;
    private SparseArray<String> alternateKeys = new SparseArray<>();
    private SparseArray<Integer> keys = new SparseArray<>();
    private InputConnection inputConnection;

    private String poundSym;

    public PrimaryKeyboard(Context context) {
        super(context);
        init(context);
    }

    public PrimaryKeyboard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PrimaryKeyboard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.main_keyboard_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        poundSym = getContext().getString(R.string.pound_symbol);

        key_1 = findViewById(R.id.one1);
        key_1.setOnClickListener(this);

        key_2 = findViewById(R.id.two2);
        key_2.setOnClickListener(this);

        key_3 = findViewById(R.id.three3);
        key_3.setOnClickListener(this);

        key_4 = findViewById(R.id.four4);
        key_4.setOnClickListener(this);

        key_5 = findViewById(R.id.five5);
        key_5.setOnClickListener(this);

        key_6 = findViewById(R.id.six6);
        key_6.setOnClickListener(this);

        key_7 = findViewById(R.id.seven7);
        key_7.setOnClickListener(this);

        key_8 = findViewById(R.id.eight8);
        key_8.setOnClickListener(this);

        key_9 = findViewById(R.id.nine9);
        key_9.setOnClickListener(this);

        key_0 = findViewById(R.id.zero0);
        key_0.setOnClickListener(this);


        key_A = findViewById(R.id.a);
        key_A.setOnClickListener(this);

        key_B = findViewById(R.id.b);
        key_B.setOnClickListener(this);

        key_C = findViewById(R.id.c);
        key_C.setOnClickListener(this);

        key_D = findViewById(R.id.d);
        key_D.setOnClickListener(this);

        key_E = findViewById(R.id.e);
        key_E.setOnClickListener(this);

        key_F = findViewById(R.id.f);
        key_F.setOnClickListener(this);

        key_G = findViewById(R.id.g);
        key_G.setOnClickListener(this);

        key_H = findViewById(R.id.h);
        key_H.setOnClickListener(this);

        key_I = findViewById(R.id.i);
        key_I.setOnClickListener(this);

        key_J = findViewById(R.id.j);
        key_J.setOnClickListener(this);

        key_K = findViewById(R.id.k);
        key_K.setOnClickListener(this);

        key_L = findViewById(R.id.l);
        key_L.setOnClickListener(this);

        key_M = findViewById(R.id.m);
        key_M.setOnClickListener(this);

        key_N = findViewById(R.id.n);
        key_N.setOnClickListener(this);

        key_O = findViewById(R.id.o);
        key_O.setOnClickListener(this);

        key_P = findViewById(R.id.p);
        key_P.setOnClickListener(this);

        key_Q = findViewById(R.id.q);
        key_Q.setOnClickListener(this);

        key_R = findViewById(R.id.r);
        key_R.setOnClickListener(this);

        key_S = findViewById(R.id.s);
        key_S.setOnClickListener(this);

        key_T = findViewById(R.id.t);
        key_T.setOnClickListener(this);

        key_U = findViewById(R.id.u);
        key_U.setOnClickListener(this);

        key_V = findViewById(R.id.v);
        key_V.setOnClickListener(this);

        key_W = findViewById(R.id.w);
        key_W.setOnClickListener(this);

        key_X = findViewById(R.id.x);
        key_X.setOnClickListener(this);

        key_Y = findViewById(R.id.y);
        key_Y.setOnClickListener(this);

        key_Z = findViewById(R.id.z);
        key_Z.setOnClickListener(this);


        key_BACKSPACE = findViewById(R.id.cancel);
        key_BACKSPACE.setOnClickListener(this);

        key_DEL = findViewById(R.id.del);
        key_DEL.setOnClickListener(this);

        key_TAB = findViewById(R.id.tab1);
        key_TAB.setOnClickListener(this);

        key_SPACE = findViewById(R.id.sp);
        key_SPACE.setOnClickListener(this);

        key_ENTER = findViewById(R.id.enter1);
        key_ENTER.setOnClickListener(this);

        key_DOT = findViewById(R.id.dot);
        key_DOT.setOnClickListener(this);

        key_PLUS_SIGN = findViewById(R.id.plus_sign);
        key_PLUS_SIGN.setOnClickListener(this);

        key_POUND = findViewById(R.id.pound_sign);
        key_POUND.setOnClickListener(this);

        key_QUESTION_MARK = findViewById(R.id.question_mark);
        key_QUESTION_MARK.setOnClickListener(this);

        key_HYPHEN = findViewById(R.id.dash);
        key_HYPHEN.setOnClickListener(this);


       /* alternateKeys.put(R.id.one1, "1");
        alternateKeys.put(R.id.two2, "2");
        alternateKeys.put(R.id.three3, "3");
        alternateKeys.put(R.id.four4, "4");
        alternateKeys.put(R.id.five5, "5");
        alternateKeys.put(R.id.six6, "6");
        alternateKeys.put(R.id.seven7, "7");
        alternateKeys.put(R.id.eight8, "8");
        alternateKeys.put(R.id.nine9, "9");
        alternateKeys.put(R.id.zero0, "0");

        alternateKeys.put(R.id.a, "a");
        alternateKeys.put(R.id.b, "b");
        alternateKeys.put(R.id.c, "c");
        alternateKeys.put(R.id.d, "d");
        alternateKeys.put(R.id.e, "e");
        alternateKeys.put(R.id.f, "f");
        alternateKeys.put(R.id.g, "g");
        alternateKeys.put(R.id.h, "h");
        alternateKeys.put(R.id.i, "i");
        alternateKeys.put(R.id.j, "j");
        alternateKeys.put(R.id.k, "k");
        alternateKeys.put(R.id.l, "l");
        alternateKeys.put(R.id.m, "m");
        alternateKeys.put(R.id.n, "n");
        alternateKeys.put(R.id.o, "o");
        alternateKeys.put(R.id.p, "p");
        alternateKeys.put(R.id.q, "q");
        alternateKeys.put(R.id.r, "r");
        alternateKeys.put(R.id.s, "s");
        alternateKeys.put(R.id.t, "t");
        alternateKeys.put(R.id.u, "u");
        alternateKeys.put(R.id.v, "v");
        alternateKeys.put(R.id.w, "w");
        alternateKeys.put(R.id.x, "x");
        alternateKeys.put(R.id.y, "y");
        alternateKeys.put(R.id.z, "z");*/

        keys.put(R.id.one1, KeyEvent.KEYCODE_1);
        keys.put(R.id.two2, KeyEvent.KEYCODE_2);
        keys.put(R.id.three3, KeyEvent.KEYCODE_3);
        keys.put(R.id.four4, KeyEvent.KEYCODE_4);
        keys.put(R.id.five5, KeyEvent.KEYCODE_5);
        keys.put(R.id.six6, KeyEvent.KEYCODE_6);
        keys.put(R.id.seven7, KeyEvent.KEYCODE_7);
        keys.put(R.id.eight8, KeyEvent.KEYCODE_8);
        keys.put(R.id.nine9, KeyEvent.KEYCODE_9);
        keys.put(R.id.zero0, KeyEvent.KEYCODE_0);

        keys.put(R.id.a, KeyEvent.KEYCODE_A);
        keys.put(R.id.b, KeyEvent.KEYCODE_B);
        keys.put(R.id.c, KeyEvent.KEYCODE_C);
        keys.put(R.id.d, KeyEvent.KEYCODE_D);
        keys.put(R.id.e, KeyEvent.KEYCODE_E);
        keys.put(R.id.f, KeyEvent.KEYCODE_F);
        keys.put(R.id.g, KeyEvent.KEYCODE_G);
        keys.put(R.id.h, KeyEvent.KEYCODE_H);
        keys.put(R.id.i, KeyEvent.KEYCODE_I);
        keys.put(R.id.j, KeyEvent.KEYCODE_J);
        keys.put(R.id.k, KeyEvent.KEYCODE_K);
        keys.put(R.id.l, KeyEvent.KEYCODE_L);
        keys.put(R.id.m, KeyEvent.KEYCODE_M);
        keys.put(R.id.n, KeyEvent.KEYCODE_N);
        keys.put(R.id.o, KeyEvent.KEYCODE_O);
        keys.put(R.id.p, KeyEvent.KEYCODE_P);
        keys.put(R.id.q, KeyEvent.KEYCODE_Q);
        keys.put(R.id.r, KeyEvent.KEYCODE_R);
        keys.put(R.id.s, KeyEvent.KEYCODE_S);
        keys.put(R.id.t, KeyEvent.KEYCODE_T);
        keys.put(R.id.u, KeyEvent.KEYCODE_U);
        keys.put(R.id.v, KeyEvent.KEYCODE_V);
        keys.put(R.id.w, KeyEvent.KEYCODE_W);
        keys.put(R.id.x, KeyEvent.KEYCODE_X);
        keys.put(R.id.y, KeyEvent.KEYCODE_Y);
        keys.put(R.id.z, KeyEvent.KEYCODE_Z);

        alternateKeys.put(R.id.pound_sign, poundSym);
        alternateKeys.put(R.id.question_mark, "?");

        keys.put(R.id.sp, KeyEvent.KEYCODE_SPACE);
        keys.put(R.id.dot, KeyEvent.KEYCODE_PERIOD);
        keys.put(R.id.plus_sign, KeyEvent.KEYCODE_PLUS);
        keys.put(R.id.dash, KeyEvent.KEYCODE_MINUS);
        keys.put(R.id.cancel, KeyEvent.KEYCODE_DEL);
        keys.put(R.id.del, KeyEvent.KEYCODE_CLEAR);
        keys.put(R.id.tab1, KeyEvent.KEYCODE_TAB);
        keys.put(R.id.enter1, KeyEvent.KEYCODE_ENTER);

    }

    @Override
    public void onClick(View v) {

        if (inputConnection == null)
            return;

        if (v.getId() == R.id.question_mark) {
            String value = alternateKeys.get(v.getId());
            Log.e(TAG, value);
            inputConnection.commitText(value, 1);
        } else if (v.getId() == R.id.pound_sign) {
            String value = alternateKeys.get(v.getId());
            Log.e(TAG, value);
            inputConnection.commitText(value, 1);
        }

        /*if (v.getId() == R.id.cancel) {
            Log.e(TAG, "------------------key event is BACKSPACE");
            CharSequence selectedText = inputConnection.getSelectedText(0);

            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                inputConnection.commitText("", 1);
            }
        } else if(v.getId() == R.id.del) {
            Log.e(TAG, "------------------key event is DEL");
            inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        }
        else if(v.getId() == R.id.tab1) {
            Log.e(TAG, "------------------key event is TAB");
            inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_TAB));
        } else if(v.getId() == R.id.enter) {
            Log.e(TAG, "------------------key event is ENTER");
            inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
        }*/
        else {
            /*Log.e(TAG, "------------------key event is ALPH");
            String value = alternateKeys.get(v.getId());
            Log.e(TAG, value);
            inputConnection.commitText(value, 1);*/
            inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keys.get(v.getId())));
        }
    }

    public void setInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }
}
