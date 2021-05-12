package com.foodciti.foodcitipartener.keyboards;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.utils.CommonMethods;

public class ExtendedKeyBoard extends ConstraintLayout implements View.OnClickListener {
    private static final String TAG = "PrimaryKeyboard";

    private float mTextSize; // pixels

    public static final int KEYCODE_DONE = KeyEvent.KEYCODE_ESCAPE;

    private Button key_1, key_2, key_3, key_4, key_5, key_6, key_7, key_8, key_9, key_0;
    private Button key_A, key_B, key_C, key_D, key_E, key_F, key_G, key_H, key_I, key_J,
            key_K, key_L, key_M, key_N, key_O, key_P, key_Q, key_R, key_S, key_T, key_U, key_V, key_W, key_X, key_Y, key_Z;
    private Button key_TAB, key_SPACE, key_ENTER1, key_ENTER2, key_DOT, key_QUESTION_MARK, key_MINUS, key_CAPS,
            key_UNDERSCORE1, key_at_rate, key_TILDE, key_EXCLAMATION, key_AT, key_POUND2, key_STERLING_POUND, key_PERCENT2,
            key_CARET, key_AMP, key_UNDERSCORE, key_PLUS, key_EQUAL, key_LEFT_ROUND_BRACKET, key_RIGHT_ROUND_BRACKET, key_LEFT_CURLY_BRACKET,
            key_RIGHT_CURLY_BRACKET, key_LEFT_SQUARE_BRACKET, key_RIGHT_SQUARE_BRACKET, key_VERTICAL_LINE, key_BACK_SLASH, key_FORWARD_SLASH, key_LESS_THAN,
            key_GREATER_THAN, key_BACK_QUOTE, key_COMMA, key_COLON, key_SEMICOLON, key_DOUBLE_QUOTE, key_SINGLE_QUOTE, key_ASTERISK, key_CLEAR,
            key_CLEAR2, key_DONE;
    private ImageView key_BACKSPACE, key_BACKSPACE2;
    private SparseArray<String> alternateKeys = new SparseArray<>();
    private SparseArray<Integer> keys = new SparseArray<>();
    private InputConnection inputConnection;

    private String poundSym;

    private boolean isCapsOn = false;


    private ConstraintLayout layout1, layout2;
    private Button toggleSym, toggleChar;


    public ExtendedKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ExtendedKeyBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        /*if(!(context instanceof OnKeyListener) && !(context instanceof OnFocusChangeListener))
            throw new RuntimeException("Context must implement OnKeyListener & OnFocusChangeListener");*/
        inflate(context, R.layout.extended_keyboard_layout, this);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.ExtendedKeyBoard, 0, 0);

        try {
            mTextSize = a.getDimensionPixelSize(R.styleable.ExtendedKeyBoard_keyTextSize, CommonMethods.convertSpToPx(context, 10f));
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        setFocusable(false);


        poundSym = getContext().getString(R.string.pound_symbol);

        key_1 = findViewById(R.id.key_1);
        key_1.setTextSize(mTextSize);
        key_1.setOnClickListener(this);

        key_2 = findViewById(R.id.key_2);
        key_2.setTextSize(mTextSize);
        key_2.setOnClickListener(this);

        key_3 = findViewById(R.id.key_3);
        key_3.setTextSize(mTextSize);
        key_3.setOnClickListener(this);

        key_4 = findViewById(R.id.key_4);
        key_4.setTextSize(mTextSize);
        key_4.setOnClickListener(this);

        key_5 = findViewById(R.id.key_5);
        key_5.setTextSize(mTextSize);
        key_5.setOnClickListener(this);

        key_6 = findViewById(R.id.key_6);
        key_6.setTextSize(mTextSize);
        key_6.setOnClickListener(this);

        key_7 = findViewById(R.id.key_7);
        key_7.setTextSize(mTextSize);
        key_7.setOnClickListener(this);

        key_8 = findViewById(R.id.key_8);
        key_8.setTextSize(mTextSize);
        key_8.setOnClickListener(this);

        key_9 = findViewById(R.id.key_9);
        key_9.setTextSize(mTextSize);
        key_9.setOnClickListener(this);

        key_0 = findViewById(R.id.key_0);
        key_0.setTextSize(mTextSize);
        key_0.setOnClickListener(this);


        key_A = findViewById(R.id.key_A);
        key_A.setTextSize(mTextSize);
        key_A.setOnClickListener(this);

        key_B = findViewById(R.id.key_B);
        key_B.setTextSize(mTextSize);
        key_B.setOnClickListener(this);

        key_C = findViewById(R.id.key_C);
        key_C.setTextSize(mTextSize);
        key_C.setOnClickListener(this);

        key_D = findViewById(R.id.key_D);
        key_D.setTextSize(mTextSize);
        key_D.setOnClickListener(this);

        key_E = findViewById(R.id.key_E);
        key_E.setTextSize(mTextSize);
        key_E.setOnClickListener(this);

        key_F = findViewById(R.id.key_F);
        key_F.setTextSize(mTextSize);
        key_F.setOnClickListener(this);

        key_G = findViewById(R.id.key_G);
        key_G.setTextSize(mTextSize);
        key_G.setOnClickListener(this);

        key_H = findViewById(R.id.key_H);
        key_H.setTextSize(mTextSize);
        key_H.setOnClickListener(this);

        key_I = findViewById(R.id.key_I);
        key_I.setTextSize(mTextSize);
        key_I.setOnClickListener(this);

        key_J = findViewById(R.id.key_J);
        key_J.setTextSize(mTextSize);
        key_J.setOnClickListener(this);

        key_K = findViewById(R.id.key_K);
        key_K.setTextSize(mTextSize);
        key_K.setOnClickListener(this);

        key_L = findViewById(R.id.key_L);
        key_L.setTextSize(mTextSize);
        key_L.setOnClickListener(this);

        key_M = findViewById(R.id.key_M);
        key_M.setTextSize(mTextSize);
        key_M.setOnClickListener(this);

        key_N = findViewById(R.id.key_N);
        key_N.setTextSize(mTextSize);
        key_N.setOnClickListener(this);

        key_O = findViewById(R.id.key_O);
        key_O.setTextSize(mTextSize);
        key_O.setOnClickListener(this);

        key_P = findViewById(R.id.key_P);
        key_P.setTextSize(mTextSize);
        key_P.setOnClickListener(this);

        key_Q = findViewById(R.id.key_Q);
        key_Q.setTextSize(mTextSize);
        key_Q.setOnClickListener(this);

        key_R = findViewById(R.id.key_R);
        key_R.setTextSize(mTextSize);
        key_R.setOnClickListener(this);

        key_S = findViewById(R.id.key_S);
        key_S.setTextSize(mTextSize);
        key_S.setOnClickListener(this);

        key_T = findViewById(R.id.key_T);
        key_T.setTextSize(mTextSize);
        key_T.setOnClickListener(this);

        key_U = findViewById(R.id.key_U);
        key_U.setTextSize(mTextSize);
        key_U.setOnClickListener(this);

        key_V = findViewById(R.id.key_V);
        key_V.setTextSize(mTextSize);
        key_V.setOnClickListener(this);

        key_W = findViewById(R.id.key_W);
        key_W.setTextSize(mTextSize);
        key_W.setOnClickListener(this);

        key_X = findViewById(R.id.key_X);
        key_X.setTextSize(mTextSize);
        key_X.setOnClickListener(this);

        key_Y = findViewById(R.id.key_Y);
        key_Y.setTextSize(mTextSize);
        key_Y.setOnClickListener(this);

        key_Z = findViewById(R.id.key_Z);
        key_Z.setTextSize(mTextSize);
        key_Z.setOnClickListener(this);

        key_BACKSPACE = findViewById(R.id.back_space);
        key_BACKSPACE.setOnClickListener(this);

        key_SPACE = findViewById(R.id.key_SPACE);
        key_SPACE.setTextSize(mTextSize);
        key_SPACE.setOnClickListener(this);

        key_ENTER1 = findViewById(R.id.enter1);
        key_ENTER1.setTextSize(mTextSize);
        key_ENTER1.setOnClickListener(this);

        key_DOT = findViewById(R.id.key_DOT);
        key_DOT.setTextSize(mTextSize);
        key_DOT.setOnClickListener(this);

        key_CAPS = findViewById(R.id.key_caps);
        key_CAPS.setTextSize(mTextSize);
        key_CAPS.setOnClickListener(this);

        key_CLEAR = findViewById(R.id.key_CLEAR);
        key_CLEAR.setTextSize(mTextSize);
        key_CLEAR.setOnClickListener(this);

        key_CLEAR2 = findViewById(R.id.key_CLEAR2);
        key_CLEAR2.setTextSize(mTextSize);
        key_CLEAR2.setOnClickListener(this);

        key_at_rate = findViewById(R.id.key_at_rate);
        key_at_rate.setTextSize(mTextSize);
        key_at_rate.setOnClickListener(this);

        key_UNDERSCORE1 = findViewById(R.id.key_UNDERSCORE1);
        key_UNDERSCORE1.setTextSize(mTextSize);
        key_UNDERSCORE1.setOnClickListener(this);

        key_TAB = findViewById(R.id.key_TAB);
        key_TAB.setTextSize(mTextSize);
        key_TAB.setOnClickListener(this);

        key_SPACE = findViewById(R.id.key_SPACE);
        key_SPACE.setTextSize(mTextSize);
        key_SPACE.setOnClickListener(this);

        key_TILDE = findViewById(R.id.key_tilde);
        key_TILDE.setTextSize(mTextSize);
        key_TILDE.setOnClickListener(this);

        key_EXCLAMATION = findViewById(R.id.key_EXCLAMATION);
        key_EXCLAMATION.setTextSize(mTextSize);
        key_EXCLAMATION.setOnClickListener(this);

        key_AT = findViewById(R.id.key_AT);
        key_AT.setTextSize(mTextSize);
        key_AT.setOnClickListener(this);

        key_POUND2 = findViewById(R.id.key_POUND2);
        key_POUND2.setTextSize(mTextSize);
        key_POUND2.setOnClickListener(this);

        key_STERLING_POUND = findViewById(R.id.key_STERLING_POUND);
        key_STERLING_POUND.setTextSize(mTextSize);
        key_STERLING_POUND.setOnClickListener(this);

        key_PERCENT2 = findViewById(R.id.key_PERCENT2);
        key_PERCENT2.setTextSize(mTextSize);
        key_PERCENT2.setOnClickListener(this);

        key_CARET = findViewById(R.id.key_CARET);
        key_CARET.setTextSize(mTextSize);
        key_CARET.setOnClickListener(this);

        key_AMP = findViewById(R.id.key_AMP);
        key_AMP.setTextSize(mTextSize);
        key_AMP.setOnClickListener(this);

        key_BACKSPACE2 = findViewById(R.id.back_space2);
        key_BACKSPACE2.setOnClickListener(this);

        key_UNDERSCORE = findViewById(R.id.key_UNDERSCORE);
        key_UNDERSCORE.setTextSize(mTextSize);
        key_UNDERSCORE.setOnClickListener(this);

        key_PLUS = findViewById(R.id.key_PLUS);
        key_PLUS.setTextSize(mTextSize);
        key_PLUS.setOnClickListener(this);

        key_MINUS = findViewById(R.id.key_MINUS);
        key_MINUS.setTextSize(mTextSize);
        key_MINUS.setOnClickListener(this);

        key_EQUAL = findViewById(R.id.key_EQUAL);
        key_EQUAL.setTextSize(mTextSize);
        key_EQUAL.setOnClickListener(this);

        key_LEFT_ROUND_BRACKET = findViewById(R.id.key_LEFT_ROUND_BRACKET);
        key_LEFT_ROUND_BRACKET.setTextSize(mTextSize);
        key_LEFT_ROUND_BRACKET.setOnClickListener(this);

        key_RIGHT_ROUND_BRACKET = findViewById(R.id.key_RIGHT_ROUND_BRACKET);
        key_RIGHT_ROUND_BRACKET.setTextSize(mTextSize);
        key_RIGHT_ROUND_BRACKET.setOnClickListener(this);

        key_LEFT_CURLY_BRACKET = findViewById(R.id.key_LEFT_CURLY_BRACKET);
        key_LEFT_CURLY_BRACKET.setTextSize(mTextSize);
        key_LEFT_CURLY_BRACKET.setOnClickListener(this);

        key_RIGHT_CURLY_BRACKET = findViewById(R.id.key_RIGHT_CURLY_BRACKET);
        key_RIGHT_CURLY_BRACKET.setTextSize(mTextSize);
        key_RIGHT_CURLY_BRACKET.setOnClickListener(this);

        key_LEFT_SQUARE_BRACKET = findViewById(R.id.key_LEFT_SQUARE_BRACKET);
        key_LEFT_SQUARE_BRACKET.setTextSize(mTextSize);
        key_LEFT_SQUARE_BRACKET.setOnClickListener(this);

        key_RIGHT_SQUARE_BRACKET = findViewById(R.id.key_RIGHT_SQUARE_BRACKET);
        key_RIGHT_SQUARE_BRACKET.setTextSize(mTextSize);
        key_RIGHT_SQUARE_BRACKET.setOnClickListener(this);

        key_VERTICAL_LINE = findViewById(R.id.key_VERTICAL_LINE);
        key_VERTICAL_LINE.setTextSize(mTextSize);
        key_VERTICAL_LINE.setOnClickListener(this);

        key_BACK_SLASH = findViewById(R.id.key_BACK_SLASH);
        key_BACK_SLASH.setTextSize(mTextSize);
        key_BACK_SLASH.setOnClickListener(this);

        key_FORWARD_SLASH = findViewById(R.id.key_FORWARD_SLASH);
        key_FORWARD_SLASH.setTextSize(mTextSize);
        key_FORWARD_SLASH.setOnClickListener(this);

        key_LESS_THAN = findViewById(R.id.key_LESS_THAN);
        key_LESS_THAN.setTextSize(mTextSize);
        key_LESS_THAN.setOnClickListener(this);

        key_GREATER_THAN = findViewById(R.id.key_GREATER_THAN);
        key_GREATER_THAN.setTextSize(mTextSize);
        key_GREATER_THAN.setOnClickListener(this);

        key_QUESTION_MARK = findViewById(R.id.key_QUESTION_MARK);
        key_QUESTION_MARK.setTextSize(mTextSize);
        key_QUESTION_MARK.setOnClickListener(this);

        key_BACK_QUOTE = findViewById(R.id.key_BACK_QUOTE);
        key_BACK_QUOTE.setTextSize(mTextSize);
        key_BACK_QUOTE.setOnClickListener(this);

        key_COMMA = findViewById(R.id.key_COMMA);
        key_COMMA.setTextSize(mTextSize);
        key_COMMA.setOnClickListener(this);

        key_COLON = findViewById(R.id.key_COLON);
        key_COLON.setTextSize(mTextSize);
        key_COLON.setOnClickListener(this);

        key_SEMICOLON = findViewById(R.id.key_SEMICOLON);
        key_SEMICOLON.setTextSize(mTextSize);
        key_SEMICOLON.setOnClickListener(this);

        key_DOUBLE_QUOTE = findViewById(R.id.key_DOUBLE_QUOTE);
        key_DOUBLE_QUOTE.setTextSize(mTextSize);
        key_DOUBLE_QUOTE.setOnClickListener(this);

        key_SINGLE_QUOTE = findViewById(R.id.key_SINGLE_QUOTE);
        key_SINGLE_QUOTE.setTextSize(mTextSize);
        key_SINGLE_QUOTE.setOnClickListener(this);

        key_ASTERISK = findViewById(R.id.key_ASTERISK);
        key_ASTERISK.setTextSize(mTextSize);
        key_ASTERISK.setOnClickListener(this);

        key_ENTER1 = findViewById(R.id.enter1);
        key_ENTER1.setTextSize(mTextSize);
        key_ENTER1.setOnClickListener(this);

        key_ENTER2 = findViewById(R.id.enter2);
        key_ENTER2.setTextSize(mTextSize);
        key_ENTER2.setOnClickListener(this);

        key_DONE = findViewById(R.id.key_DONE);
        key_DONE.setTextSize(mTextSize);
        key_DONE.setOnClickListener(this);

        keys.put(R.id.key_1, KeyEvent.KEYCODE_1);
        keys.put(R.id.key_2, KeyEvent.KEYCODE_2);
        keys.put(R.id.key_3, KeyEvent.KEYCODE_3);
        keys.put(R.id.key_4, KeyEvent.KEYCODE_4);
        keys.put(R.id.key_5, KeyEvent.KEYCODE_5);
        keys.put(R.id.key_6, KeyEvent.KEYCODE_6);
        keys.put(R.id.key_7, KeyEvent.KEYCODE_7);
        keys.put(R.id.key_8, KeyEvent.KEYCODE_8);
        keys.put(R.id.key_9, KeyEvent.KEYCODE_9);
        keys.put(R.id.key_0, KeyEvent.KEYCODE_0);
        keys.put(R.id.key_A, KeyEvent.KEYCODE_A);
        keys.put(R.id.key_B, KeyEvent.KEYCODE_B);
        keys.put(R.id.key_C, KeyEvent.KEYCODE_C);
        keys.put(R.id.key_D, KeyEvent.KEYCODE_D);
        keys.put(R.id.key_E, KeyEvent.KEYCODE_E);
        keys.put(R.id.key_F, KeyEvent.KEYCODE_F);
        keys.put(R.id.key_G, KeyEvent.KEYCODE_G);
        keys.put(R.id.key_H, KeyEvent.KEYCODE_H);
        keys.put(R.id.key_I, KeyEvent.KEYCODE_I);
        keys.put(R.id.key_J, KeyEvent.KEYCODE_J);
        keys.put(R.id.key_K, KeyEvent.KEYCODE_K);
        keys.put(R.id.key_L, KeyEvent.KEYCODE_L);
        keys.put(R.id.key_M, KeyEvent.KEYCODE_M);
        keys.put(R.id.key_N, KeyEvent.KEYCODE_N);
        keys.put(R.id.key_O, KeyEvent.KEYCODE_O);
        keys.put(R.id.key_P, KeyEvent.KEYCODE_P);
        keys.put(R.id.key_Q, KeyEvent.KEYCODE_Q);
        keys.put(R.id.key_R, KeyEvent.KEYCODE_R);
        keys.put(R.id.key_S, KeyEvent.KEYCODE_S);
        keys.put(R.id.key_T, KeyEvent.KEYCODE_T);
        keys.put(R.id.key_U, KeyEvent.KEYCODE_U);
        keys.put(R.id.key_V, KeyEvent.KEYCODE_V);
        keys.put(R.id.key_W, KeyEvent.KEYCODE_W);
        keys.put(R.id.key_X, KeyEvent.KEYCODE_X);
        keys.put(R.id.key_Y, KeyEvent.KEYCODE_Y);
        keys.put(R.id.key_Z, KeyEvent.KEYCODE_Z);
        keys.put(R.id.key_SPACE, KeyEvent.KEYCODE_SPACE);
        keys.put(R.id.key_DOT, KeyEvent.KEYCODE_PERIOD);
        keys.put(R.id.back_space, KeyEvent.KEYCODE_DEL);
        keys.put(R.id.plus_sign, KeyEvent.KEYCODE_PLUS);
        keys.put(R.id.dash, KeyEvent.KEYCODE_MINUS);
        keys.put(R.id.cancel, KeyEvent.KEYCODE_DEL);
        keys.put(R.id.del, KeyEvent.KEYCODE_CLEAR);
        keys.put(R.id.tab1, KeyEvent.KEYCODE_TAB);
        keys.put(R.id.enter1, KeyEvent.KEYCODE_ENTER);
        keys.put(R.id.key_caps, KeyEvent.KEYCODE_CAPS_LOCK);
        keys.put(R.id.key_CLEAR, KeyEvent.KEYCODE_CLEAR);
        keys.put(R.id.key_CLEAR2, KeyEvent.KEYCODE_CLEAR);
        keys.put(R.id.key_at_rate, KeyEvent.KEYCODE_AT);
        keys.put(R.id.key_TAB, KeyEvent.KEYCODE_TAB);
        keys.put(R.id.key_SPACE, KeyEvent.KEYCODE_SPACE);
        keys.put(R.id.key_AT, KeyEvent.KEYCODE_AT);
        keys.put(R.id.key_POUND2, KeyEvent.KEYCODE_POUND);
        keys.put(R.id.back_space2, KeyEvent.KEYCODE_DEL);
        keys.put(R.id.key_PLUS, KeyEvent.KEYCODE_PLUS);
        keys.put(R.id.key_MINUS, KeyEvent.KEYCODE_MINUS);
        keys.put(R.id.key_EQUAL, KeyEvent.KEYCODE_EQUALS);
        keys.put(R.id.key_LEFT_ROUND_BRACKET, KeyEvent.KEYCODE_NUMPAD_LEFT_PAREN);
        keys.put(R.id.key_RIGHT_ROUND_BRACKET, KeyEvent.KEYCODE_NUMPAD_RIGHT_PAREN);
        keys.put(R.id.key_LEFT_SQUARE_BRACKET, KeyEvent.KEYCODE_LEFT_BRACKET);
        keys.put(R.id.key_RIGHT_SQUARE_BRACKET, KeyEvent.KEYCODE_RIGHT_BRACKET);
        keys.put(R.id.key_BACK_SLASH, KeyEvent.KEYCODE_BACKSLASH);
        keys.put(R.id.key_FORWARD_SLASH, KeyEvent.KEYCODE_SLASH);
        keys.put(R.id.key_BACK_QUOTE, KeyEvent.KEYCODE_GRAVE);
        keys.put(R.id.key_COMMA, KeyEvent.KEYCODE_COMMA);
        keys.put(R.id.key_SEMICOLON, KeyEvent.KEYCODE_SEMICOLON);
        keys.put(R.id.key_SINGLE_QUOTE, KeyEvent.KEYCODE_APOSTROPHE);
        keys.put(R.id.key_ASTERISK, KeyEvent.KEYCODE_STAR);
        keys.put(R.id.enter1, KeyEvent.KEYCODE_ENTER);
        keys.put(R.id.enter2, KeyEvent.KEYCODE_ENTER);
        keys.put(R.id.key_DONE, KeyEvent.KEYCODE_ESCAPE);

        alternateKeys.put(R.id.key_1, "1");
        alternateKeys.put(R.id.key_2, "2");
        alternateKeys.put(R.id.key_3, "3");
        alternateKeys.put(R.id.key_4, "4");
        alternateKeys.put(R.id.key_5, "5");
        alternateKeys.put(R.id.key_6, "6");
        alternateKeys.put(R.id.key_7, "7");
        alternateKeys.put(R.id.key_8, "8");
        alternateKeys.put(R.id.key_9, "9");
        alternateKeys.put(R.id.key_0, "0");

        alternateKeys.put(R.id.key_A, "a");
        alternateKeys.put(R.id.key_B, "b");
        alternateKeys.put(R.id.key_C, "c");
        alternateKeys.put(R.id.key_D, "d");
        alternateKeys.put(R.id.key_E, "e");
        alternateKeys.put(R.id.key_F, "f");
        alternateKeys.put(R.id.key_G, "g");
        alternateKeys.put(R.id.key_H, "h");
        alternateKeys.put(R.id.key_I, "i");
        alternateKeys.put(R.id.key_J, "j");
        alternateKeys.put(R.id.key_K, "k");
        alternateKeys.put(R.id.key_L, "l");
        alternateKeys.put(R.id.key_M, "m");
        alternateKeys.put(R.id.key_N, "n");
        alternateKeys.put(R.id.key_O, "o");
        alternateKeys.put(R.id.key_P, "p");
        alternateKeys.put(R.id.key_Q, "q");
        alternateKeys.put(R.id.key_R, "r");
        alternateKeys.put(R.id.key_S, "s");
        alternateKeys.put(R.id.key_T, "t");
        alternateKeys.put(R.id.key_U, "u");
        alternateKeys.put(R.id.key_V, "v");
        alternateKeys.put(R.id.key_W, "w");
        alternateKeys.put(R.id.key_X, "x");
        alternateKeys.put(R.id.key_Y, "y");
        alternateKeys.put(R.id.key_Z, "z");
        alternateKeys.put(R.id.pound_sign, poundSym);
        alternateKeys.put(R.id.question_mark, "?");

        layout1 = findViewById(R.id.layout_1);
        layout2 = findViewById(R.id.layout_2);

        toggleSym = findViewById(R.id.key_toggleSym);
        toggleSym.setTextSize(mTextSize);
        toggleChar = findViewById(R.id.key_toggleChar);
        toggleChar.setTextSize(mTextSize);

        toggleSym.setOnClickListener(v -> {
            layout2.setVisibility(VISIBLE);
            layout1.setVisibility(GONE);
        });
        toggleChar.setOnClickListener(v -> {
            layout2.setVisibility(GONE);
            layout1.setVisibility(VISIBLE);
        });
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
        } else if (v.getId() == R.id.key_caps) {
            if (isCapsOn)
                isCapsOn = false;
            else
                isCapsOn = true;

            setAllAplhabetsCaps(isCapsOn);
        } else if (v.getId() == R.id.key_QUESTION_MARK) {
            sendKeyEventWithMetaKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SLASH, KeyEvent.META_SHIFT_ON);
        } else if (v.getId() == R.id.key_EXCLAMATION) {
            sendKeyEventWithMetaKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_1, KeyEvent.META_SHIFT_ON);
        } else if (v.getId() == R.id.key_PERCENT2) {
            sendKeyEventWithMetaKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_5, KeyEvent.META_SHIFT_ON);
        } else if (v.getId() == R.id.key_CARET) {
            sendKeyEventWithMetaKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_6, KeyEvent.META_SHIFT_ON);
        } else if (v.getId() == R.id.key_AMP) {
            sendKeyEventWithMetaKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_7, KeyEvent.META_SHIFT_ON);
        } else if (v.getId() == R.id.key_UNDERSCORE || v.getId() == R.id.key_UNDERSCORE1) {
            sendKeyEventWithMetaKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MINUS, KeyEvent.META_SHIFT_ON);
        } else if (v.getId() == R.id.key_LEFT_CURLY_BRACKET) {
            sendKeyEventWithMetaKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_LEFT_BRACKET, KeyEvent.META_SHIFT_ON);
        } else if (v.getId() == R.id.key_RIGHT_CURLY_BRACKET) {
            sendKeyEventWithMetaKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_RIGHT_BRACKET, KeyEvent.META_SHIFT_ON);
        } else if (v.getId() == R.id.key_VERTICAL_LINE) {
            sendKeyEventWithMetaKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACKSLASH, KeyEvent.META_SHIFT_ON);
        } else if (v.getId() == R.id.key_DOUBLE_QUOTE) {
            sendKeyEventWithMetaKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_APOSTROPHE, KeyEvent.META_SHIFT_ON);
        } else if (v.getId() == R.id.key_COLON) {
            sendKeyEventWithMetaKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SEMICOLON, KeyEvent.META_SHIFT_ON);
        } else if (v.getId() == R.id.key_LESS_THAN) {
            sendKeyEventWithMetaKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_COMMA, KeyEvent.META_SHIFT_ON);
        } else if (v.getId() == R.id.key_GREATER_THAN) {
            sendKeyEventWithMetaKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_PERIOD, KeyEvent.META_SHIFT_ON);
        } else if (v.getId() == R.id.key_tilde) {
            sendKeyEventWithMetaKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_GRAVE, KeyEvent.META_SHIFT_ON);
        } else if (v.getId() == R.id.key_STERLING_POUND) {
            inputConnection.commitText(poundSym, 1);
        } else {
            if (isCapsOn) {
                sendKeyEventWithMetaKey(KeyEvent.ACTION_DOWN, keys.get(v.getId()), KeyEvent.META_CAPS_LOCK_ON);
            } else {
                sendKeyEvent(KeyEvent.ACTION_DOWN, keys.get(v.getId()));
            }
        }
    }

    private void sendKeyEventWithMetaKey(int action, int keyCode, int metaKeyCode) {
        long now = System.currentTimeMillis();
        inputConnection.sendKeyEvent(new KeyEvent(now, now, action, keyCode, 0, metaKeyCode));
    }

    private void sendKeyEvent(int action, int keyCode) {
        inputConnection.sendKeyEvent(new KeyEvent(action, keyCode));
    }

    private void setAllAplhabetsCaps(boolean isCapsOn) {
        key_A.setAllCaps(isCapsOn);
        key_B.setAllCaps(isCapsOn);
        key_C.setAllCaps(isCapsOn);
        key_D.setAllCaps(isCapsOn);
        key_E.setAllCaps(isCapsOn);
        key_F.setAllCaps(isCapsOn);
        key_G.setAllCaps(isCapsOn);
        key_H.setAllCaps(isCapsOn);
        key_I.setAllCaps(isCapsOn);
        key_J.setAllCaps(isCapsOn);
        key_K.setAllCaps(isCapsOn);
        key_L.setAllCaps(isCapsOn);
        key_M.setAllCaps(isCapsOn);
        key_N.setAllCaps(isCapsOn);
        key_O.setAllCaps(isCapsOn);
        key_P.setAllCaps(isCapsOn);
        key_Q.setAllCaps(isCapsOn);
        key_R.setAllCaps(isCapsOn);
        key_S.setAllCaps(isCapsOn);
        key_T.setAllCaps(isCapsOn);
        key_U.setAllCaps(isCapsOn);
        key_V.setAllCaps(isCapsOn);
        key_W.setAllCaps(isCapsOn);
        key_X.setAllCaps(isCapsOn);
        key_Y.setAllCaps(isCapsOn);
        key_Z.setAllCaps(isCapsOn);

        if (isCapsOn)
            key_CAPS.setAllCaps(false);
        else
            key_CAPS.setAllCaps(true);
    }

    public void setInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }
}
