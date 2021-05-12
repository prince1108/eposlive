package com.foodciti.foodcitipartener.compound_views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.foodciti.foodcitipartener.R;

/**
 * Implementation of App Widget functionality.
 */
public class CounterBox extends LinearLayout {
    private Button decrement;
    private TextView counter;
    private Button increment;
    private int count;
    private int lowerLimit, upperLimit = Integer.MAX_VALUE;
    private CounterListener listener;

    public CounterBox(Context context) {
        super(context);
        initializeViews(context);
    }

    public CounterBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public CounterBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CounterBox(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        /*LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.counter_layout, this);
        count=0;*/
        inflate(context, R.layout.counter_box, this);
        count = 0;
        lowerLimit = 0;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        /*// Sets the images for the previous and next buttons. Uses
        // built-in images so you don't need to add images, but in
        // a real application your images should be in the
        // application package so they are always available.
        decrement = (Button) findViewById(R.id.decrement);
        /*mPreviousButton
                .setBackgroundResource(android.R.drawable.ic_media_previous);*/
        this.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rectangular_shape));
        decrement = (Button) findViewById(R.id.decrement);
        counter = (TextView) findViewById(R.id.counter);
        increment = (Button) findViewById(R.id.increment);
       /* mNextButton
                .setBackgroundResource(android.R.drawable.ic_media_next);*/

        decrement.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > lowerLimit) {
                    --count;
                    counter.setText(count + "");
                    Log.i("CounterBox", "---------------count: " + count);
                    if (listener != null)
                        listener.onDecrement(count);
                    else
                        Toast.makeText(getContext(), "listener null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        increment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count < upperLimit) {
                    ++count;
                    counter.setText(count + "");
                    Log.i("CounterBox", "---------------count: " + count);
                    if (listener != null)
                        listener.onIncrement(count);
                    else
                        Toast.makeText(getContext(), "listener null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        /*bundle.putParcelable(STATE_SUPER_CLASS,
                super.onSaveInstanceState());
        bundle.putInt(STATE_SELECTED_INDEX, mSelectedIndex);*/
        super.onSaveInstanceState();
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

           /* super.onRestoreInstanceState(bundle
                    .getParcelable(STATE_SUPER_CLASS));
            setSelectedIndex(bundle.getInt(STATE_SELECTED_INDEX));*/
        } else
            super.onRestoreInstanceState(state);
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        // Makes sure that the state of the child views in the side
        // spinner are not saved since we handle the state in the
        // onSaveInstanceState.
        super.dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        // Makes sure that the state of the child views in the side
        // spinner are not restored since we handle the state in the
        // onSaveInstanceState.
        super.dispatchThawSelfOnly(container);
    }

    public void setOnCounterChangeListener(CounterListener listener) {
        this.listener = listener;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        counter.setText(count + "");
    }

    public void setLowerLimit(int lowerLimit) {
        this.lowerLimit = lowerLimit;
        setCount(lowerLimit);
    }

    public void setUpperLimit(int upperLimit) {
        this.upperLimit = upperLimit;
    }

    public interface CounterListener {
        void onIncrement(int count);

        void onDecrement(int count);
    }
}

