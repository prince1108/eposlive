package com.foodciti.foodcitipartener.compound_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.observables.ObservableObject;
import com.foodciti.foodcitipartener.utils.CommonMethods;

import java.util.Observable;
import java.util.Observer;

public class DiscountView extends ConstraintLayout implements View.OnClickListener, Observer {
    private static final String TAG = "DiscountCalculator";

    private TextView button1, button2, button3, button4, button5, button6, button7, button8, button9, button0, buttonPoint, /*mode,*/
            discountValue, extraValue, discountLabel, extraLabel,
            computedDiscountValue, computedExtraValue, /*subtotalValue, subotalLabel,*/
            discountNExtraBtn, buttonEnter, poundPercent, item/*, conversionValue*/;
    private ImageView buttonDelete, deleteDiscount, deleteExtra;
    private ConstraintLayout discountPanel, extraPanel, topPanel;
//    private EditText editText;

    private double currentDiscount, currentExtra;
    private Double total = 0.0, newtotal = 0.0, slash = 0.0, hike = 0.0;
    private float mTextSize; // pixels

    private boolean isPercent = true;
    private boolean isDiscount = true;
    private boolean adjustmentApplied = false;
    private boolean flagDiscountPercent = false;
    private boolean flagExtraPercent = false;

    private SparseArray<String> keyValues = new SparseArray<>();
    private InputConnection inputConnection;

    private Callback callback;
    private String poundSymbol, percentSymbol;

    private ObservableObject<String> observableInput;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public DiscountView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DiscountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void togglePercentMode() {
        if (isPercent) {
            setPercentMode(false);
        } else {
            setPercentMode(true);
        }

   /*     Mode mode = (isDiscount) ? Mode.DISCOUNT : Mode.EXTRA;
        double slash = 0;
        if (isPercent)
            slash = currentDiscount * 0.01 * total;
        else
            slash = currentDiscount;*/
    }

    private void setPercentMode(boolean percentMode) {
        if (percentMode) {
            isPercent = true;
            poundPercent.setText(percentSymbol);
        } else {
            isPercent = false;
            poundPercent.setText(poundSymbol);
        }
    }

    private void toggleDiscountNextra() {
        observableInput.setValue("");
        if (isDiscount) {
            isDiscount = false;
            discountNExtraBtn.setText("Extra");
            poundPercent.setText(poundSymbol);
            isPercent = false;
        } else {
            isDiscount = true;
            discountNExtraBtn.setText("Discount");
            poundPercent.setText(percentSymbol);
            isPercent = true;
        }

        /*Mode mode = (isDiscount) ? Mode.DISCOUNT : Mode.EXTRA;
        double slash = 0;
        if (isPercent)
            slash = currentDiscount * 0.01 * total;
        else
            slash = currentDiscount;*/
    }

    private void previewCurrentAdjustment() {
        if (isDiscount) {
            if (discountPanel.getVisibility() == GONE)
                discountPanel.setVisibility(VISIBLE);
            String currentVal = String.format("%.2f", getCurrentAdjustmentInPounds(observableInput.getValue()));
            if (isPercent) {
                discountValue.setText(observableInput.getValue() + " " + percentSymbol);
                computedDiscountValue.setText(currentVal);
            } else {
                discountValue.setText("");
                computedDiscountValue.setText(currentVal);
            }
        } else {
            if (extraPanel.getVisibility() == GONE)
                extraPanel.setVisibility(VISIBLE);
            String currentVal = String.format("%.2f", getCurrentAdjustmentInPounds(observableInput.getValue()));
            if (isPercent) {
                extraValue.setText(observableInput.getValue() + " " + percentSymbol);
                computedExtraValue.setText(currentVal);
            } else {
                extraValue.setText("");
                computedExtraValue.setText(currentVal);
            }
        }
    }


    private double getCurrentAdjustmentInPounds(String s) {
        double currentAdjustment = 0.0;
        if (!s.trim().isEmpty())
            currentAdjustment = Double.parseDouble(s.trim());
        double slash = 0;
        if (isPercent)
            slash = currentAdjustment * 0.01 * total;
        else
            slash = currentAdjustment;

        return slash;
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.new_discountview_layout, this);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.DiscountView, 0, 0);

        try {
            mTextSize = a.getDimensionPixelSize(R.styleable.DiscountView_textsize, CommonMethods.convertSpToPx(context, 10f));
        } finally {
            a.recycle();
        }

        poundSymbol = context.getString(R.string.pound_symbol);
        percentSymbol = context.getString(R.string.percent_mark);

        poundPercent = findViewById(R.id.pound_percent);
        poundPercent.setTextSize(mTextSize);
        poundPercent.setOnClickListener(this);
        discountNExtraBtn = findViewById(R.id.discount_n_extra);
        discountNExtraBtn.setTextSize(mTextSize);
        discountNExtraBtn.setOnClickListener(this);
        item = findViewById(R.id.item);
        item.setTextSize(mTextSize);
        item.setOnClickListener(this);
        discountPanel = findViewById(R.id.discount_panel);
        extraPanel = findViewById(R.id.extra_panel);
        topPanel = findViewById(R.id.top_panel);

        discountValue = findViewById(R.id.value_discount);
        discountValue.setTextSize(mTextSize);
        discountLabel = findViewById(R.id.label_discount);
        discountLabel.setTextSize(mTextSize);
        extraValue = findViewById(R.id.value_extra);
        extraValue.setTextSize(mTextSize);
        extraLabel = findViewById(R.id.label_extra);
        extraLabel.setTextSize(mTextSize);
        /*subtotalValue = findViewById(R.id.value_subtotal);
        subtotalValue.setTextSize(mTextSize);
        subotalLabel = findViewById(R.id.label_subtotal);
        subotalLabel.setTextSize(mTextSize);*/
        deleteDiscount = findViewById(R.id.delete_discount);
        deleteDiscount.setOnClickListener(this);
        deleteExtra = findViewById(R.id.delete_extra);
        deleteExtra.setOnClickListener(this);
        computedDiscountValue = findViewById(R.id.computed_value_discount);
        computedExtraValue = findViewById(R.id.computed_value_extra);
        button1 = findViewById(R.id.button_1);
        button1.setTextSize(mTextSize);
        button1.setOnClickListener(this);
        button2 = findViewById(R.id.button_2);
        button2.setTextSize(mTextSize);
        button2.setOnClickListener(this);
        button3 = findViewById(R.id.button_3);
        button3.setTextSize(mTextSize);
        button3.setOnClickListener(this);
        button4 = findViewById(R.id.button_4);
        button4.setTextSize(mTextSize);
        button4.setOnClickListener(this);
        button5 = findViewById(R.id.button_5);
        button5.setTextSize(mTextSize);
        button5.setOnClickListener(this);
        button6 = findViewById(R.id.button_6);
        button6.setTextSize(mTextSize);
        button6.setOnClickListener(this);
        button7 = findViewById(R.id.button_7);
        button7.setTextSize(mTextSize);
        button7.setOnClickListener(this);
        button8 = findViewById(R.id.button_8);
        button8.setTextSize(mTextSize);
        button8.setOnClickListener(this);
        button9 = findViewById(R.id.button_9);
        button9.setTextSize(mTextSize);
        button9.setOnClickListener(this);
        button0 = findViewById(R.id.button_0);
        button0.setTextSize(mTextSize);
        button0.setOnClickListener(this);

        /*buttonDelete = findViewById(R.id.delete);
        buttonDelete.setOnClickListener(this);*/
        buttonPoint = findViewById(R.id.button_point);
        buttonPoint.setTextSize(mTextSize);
        buttonPoint.setOnClickListener(this);
        buttonEnter = findViewById(R.id.button_enter);
        buttonEnter.setTextSize(mTextSize);
        buttonEnter.setOnClickListener(this);

//        conversionValue=findViewById(R.id.conversion_value);


        keyValues.put(R.id.button_1, "1");
        keyValues.put(R.id.button_2, "2");
        keyValues.put(R.id.button_3, "3");
        keyValues.put(R.id.button_4, "4");
        keyValues.put(R.id.button_5, "5");
        keyValues.put(R.id.button_6, "6");
        keyValues.put(R.id.button_7, "7");
        keyValues.put(R.id.button_8, "8");
        keyValues.put(R.id.button_9, "9");
        keyValues.put(R.id.button_0, "0");
        keyValues.put(R.id.button_point, ".");
        keyValues.put(R.id.enter1, "\n");

        discountPanel.setVisibility(GONE);
        extraPanel.setVisibility(GONE);

        observableInput = new ObservableObject<>();
        observableInput.setValue("0");
        observableInput.addObserver((observable, o) -> {
            String currentInputValue = (String) o;
            if (currentInputValue.trim().isEmpty())
                return;

            if (currentInputValue.trim().matches("^[0-9]*[\\.]?[0-9]+")) {
                if (isDiscount)
                    currentDiscount = Double.parseDouble(observableInput.getValue().trim());
                else
                    currentExtra = Double.parseDouble(observableInput.getValue().trim());
                applyAdjustment();
            }
            Log.d(TAG, "-----------------currentDiscount: " + currentDiscount);
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    private void removeDiscount() {
        currentDiscount = 0.0;
        observableInput.setValue("0");
        discountPanel.setVisibility(GONE);
        newtotal += slash;
        callback.onDiscount(0, newtotal, isPercent, Mode.DISCOUNT);
        slash = 0.0;
    }

    private void removeExtra() {
        currentExtra = 0.0;
        observableInput.setValue("0");
        extraPanel.setVisibility(GONE);
        newtotal -= hike;
        callback.onDiscount(0, newtotal, isPercent, Mode.EXTRA);
        hike = 0.0;
    }

    @Override
    public void onClick(View v) {
        /*if (inputConnection == null)
            return;*/

        switch (v.getId()) {
            case R.id.delete:
                CharSequence selectedText = inputConnection.getSelectedText(0);

                if (TextUtils.isEmpty(selectedText)) {
                    inputConnection.deleteSurroundingText(1, 0);
                } else {
                    inputConnection.commitText("", 1);
                }
                if (observableInput.getValue().trim().isEmpty()) {
                    observableInput.setValue("0");
                    currentDiscount = 0d;
                } else {
                    Log.d(TAG, "----------------in last else block------------");
                    Log.d(TAG, "--------string: " + observableInput.getValue().trim());
                    if (observableInput.getValue().trim().endsWith(".")) {
                        inputConnection.commitText("", 1);
                    }
                    currentDiscount = Double.parseDouble(observableInput.getValue().trim());
                }
                break;

            case R.id.pound_percent:
                togglePercentMode();
                break;

            case R.id.discount_n_extra:
                toggleDiscountNextra();
                break;

            case R.id.delete_discount:
                removeDiscount();
                break;
            case R.id.delete_extra:
                removeExtra();
                break;

            case R.id.button_enter:
                applyAdjustment();
                break;

            case R.id.item:
                /*if(discountPanel.getVisibility()==VISIBLE)
                    observableInput.setValue("0");
                if(extraPanel.getVisibility()==VISIBLE)
                    observableExtraInput.setValue("0");*/
                observableInput.setValue("0");
                break;

            default:
                if (observableInput.getValue().equalsIgnoreCase("0") || observableInput.getValue().equalsIgnoreCase("0.0"))
                    observableInput.setValue("");
                String value = keyValues.get(v.getId());
                String strBeforeCommit = observableInput.getValue().trim() + value;
                Log.e(TAG, "---------string before commit: " + strBeforeCommit);
                if (!strBeforeCommit.matches("^[0-9]*[\\.]{0,1}[0-9]*"))
                    return;
                observableInput.setValue(observableInput.getValue() + value);
                if (observableInput.getValue().trim().equalsIgnoreCase(".")) {
                    observableInput.setValue("0.");
                }
                if (observableInput.getValue().matches("0+"))
                    observableInput.setValue("0");
        }
    }

    public void setInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }

    private void applyAdjustment() {
        previewCurrentAdjustment();
        if (isPercent) {
            if (isDiscount) {
                flagDiscountPercent = true;
                slash = currentDiscount * 0.01 * total;
                newtotal = total - slash + hike;
                callback.onDiscount(slash, newtotal, isPercent, Mode.DISCOUNT);
            } else {
                flagExtraPercent = true;
//                hike = currentDiscount * 0.01 * total;
                hike = currentExtra * 0.01 * total;
                newtotal = total + hike - slash;
                callback.onDiscount(hike, newtotal, isPercent, Mode.EXTRA);
            }
        } else {
            if (isDiscount) {
                flagDiscountPercent = false;
                slash = currentDiscount;
                newtotal = total - slash + hike;
                callback.onDiscount(currentDiscount, newtotal, isPercent, Mode.DISCOUNT);
            } else {
                flagExtraPercent = false;
//                hike = currentDiscount;
                hike = currentExtra;
                newtotal = total + hike - slash;
                callback.onDiscount(currentExtra, newtotal, isPercent, Mode.EXTRA);
            }
        }
        adjustmentApplied = true;
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.e(TAG, "--------------------updated value: " + arg);
        total = (Double) arg;

        if (total == 0) {
            removeDiscount();
            removeExtra();
        } else
            previewCurrentAdjustment();

        if (adjustmentApplied) {
            Log.d(TAG, "applying adjustment");
            new Handler().postDelayed(() -> {
                reapplyAdjustment();
            }, 50);
        }
    }

    private void reapplyAdjustment() {
        if (discountPanel.getVisibility() == VISIBLE) {
            if (flagDiscountPercent) {
                slash = currentDiscount * 0.01 * total;
            } else {
                slash = currentDiscount;
            }
            newtotal = total + hike - slash;
            computedDiscountValue.setText(String.format("%.2f", slash));
            callback.onDiscount(slash, newtotal, flagDiscountPercent, Mode.DISCOUNT);

        }
        if (extraPanel.getVisibility() == VISIBLE) {
            if (flagExtraPercent) {
//                hike= currentDiscount *0.01 * total;
                hike = currentExtra * 0.01 * total;
            } else {
//                hike= currentDiscount;
                hike = currentExtra;
            }
            newtotal = total + hike - slash;
            computedExtraValue.setText(String.format("%.2f", hike));
            callback.onDiscount(hike, newtotal, flagExtraPercent, Mode.EXTRA);
        }
    }

    public void reset() {
        observableInput.setValue("");
        discountPanel.setVisibility(GONE);
        extraPanel.setVisibility(GONE);
        total = 0.0;
        slash = 0.0;
        hike = 0.0;

        callback.onDiscount(0, 0, isPercent, Mode.DISCOUNT);
        callback.onDiscount(0, 0, isPercent, Mode.EXTRA);
    }

    public interface Callback {
        void onDiscount(double amountAdjusted, double newTotal, boolean isPercent, Mode mode);
    }

    public enum Mode {
        DISCOUNT, EXTRA
    }
}
