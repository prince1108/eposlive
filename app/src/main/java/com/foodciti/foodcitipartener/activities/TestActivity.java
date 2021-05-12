package com.foodciti.foodcitipartener.activities;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.foodciti.foodcitipartener.R;

public class TestActivity extends AppCompatActivity implements View.OnDragListener, View.OnTouchListener {
    private static final String TAG = "TestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        /*Realm realm=Realm.getDefaultInstance();
        RecyclerView split1=findViewById(R.id.split1);
        split1.setLayoutManager(new LinearLayoutManager(this));


        RecyclerView split2=findViewById(R.id.split2);
        split2.setLayoutManager(new LinearLayoutManager(this));

        Table table=realm.where(Table.class).equalTo("id", 1).findFirst();
        TableCheckoutItemSAdapter split1Adapter=new TableCheckoutItemSAdapter(this,new ArrayList<>(table.cartItems));
        TableCheckoutItemSAdapter split2Adapter=new TableCheckoutItemSAdapter(this,new ArrayList<>());

        split1.setAdapter(split1Adapter);
        split1.setOnDragListener(split1Adapter.getDragInstance());

        split2.setAdapter(split2Adapter);
        split2.setOnDragListener(split2Adapter.getDragInstance());*/

//        findViewById(R.id.textView).setOnDragListener(split1Adapter.getDragInstance());

       /* EditText editText = (EditText) findViewById(R.id.edit);
        PrimaryKeyboard keyboard = findViewById(R.id.keyboard);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText.setTextIsSelectable(true);

        InputConnection ic = editText.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);*/

        View panTool1, panTool2, panTool3;
        panTool1 = findViewById(R.id.pan_tool_1);
        panTool1.setOnTouchListener(this);
        /*panTool1.setOnDragListener(this);
        panTool1.setOnLongClickListener(v->{
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder,v, 0);
            return true;

        });*/
        panTool2 = findViewById(R.id.pan_tool_2);
        panTool2.setOnTouchListener(this);
        /*panTool2.setOnDragListener(this);
        panTool2.setOnLongClickListener(v->{
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder,v, 0);
            return true;

        });*/
        panTool3 = findViewById(R.id.pan_tool_3);
        panTool3.setOnTouchListener(this);
        /*panTool3.setOnDragListener(this);
        panTool3.setOnLongClickListener(v->{
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder,v, 0);
            return true;

        });*/

        left_pane = findViewById(R.id.left_pane);
        mid_pane = findViewById(R.id.mid_pane);
        right_pane = findViewById(R.id.right_pane);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        Log.e(TAG, "-------------------drag : " + event.getX() + ", " + event.getY());
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
//                Log.e(TAG, "-----------drag started: "+event.getX()+", "+event.getY());
                /*float width=getResources().getDisplayMetrics().widthPixels;
                float height=getResources().getDisplayMetrics().heightPixels;

                float widthPercent=event.getX()/width;
                Log.e(TAG, "---------------percent: "+widthPercent);
*/
                return true;
            case DragEvent.ACTION_DRAG_LOCATION: {
                Log.e(TAG, "-------------------drag entered: " + event.getX() + ", " + event.getY());
                float width = getResources().getDisplayMetrics().widthPixels;
                float height = getResources().getDisplayMetrics().heightPixels;

                float widthPercent = event.getX() / width;
                Log.e(TAG, "---------------percent: " + widthPercent);

                /*Point touchPosition = getTouchPositionFromDragEvent(v, event);
                Log.e(TAG, "---------------percent2: " + touchPosition.x/width);

                final float widthPer1=touchPosition.x/width;
                final float widthPer2=1-widthPer1;*/

                final float widthPer1 = widthPercent;
                final float widthPer2 = 1 - widthPer1;

                ConstraintLayout.LayoutParams leftPaneLP = (ConstraintLayout.LayoutParams) left_pane.getLayoutParams();
                ConstraintLayout.LayoutParams midPaneLP = (ConstraintLayout.LayoutParams) mid_pane.getLayoutParams();
                ConstraintLayout.LayoutParams rightPaneLP = (ConstraintLayout.LayoutParams) right_pane.getLayoutParams();
                leftPaneLP.horizontalWeight = widthPer1;
                midPaneLP.horizontalWeight = widthPer2 / 2;
                rightPaneLP.horizontalWeight = widthPer2 / 2;

                left_pane.setLayoutParams(leftPaneLP);
                mid_pane.setLayoutParams(midPaneLP);
                right_pane.setLayoutParams(rightPaneLP);
                return true;
            }

            case DragEvent.ACTION_DRAG_ENTERED: {

                return true;
            }
            case DragEvent.ACTION_DRAG_ENDED: {
               /* Log.e(TAG, "-------------------drag ended: " + event.getX() + ", " + event.getY());

                float width = getResources().getDisplayMetrics().widthPixels;
                float height = getResources().getDisplayMetrics().heightPixels;

                float widthPercent = event.getX() / width;
                Log.e(TAG, "---------------percent: " + widthPercent);*/
                return true;
            }
        }
        return false;
    }

    private ConstraintLayout left_pane, mid_pane, right_pane;

    public static Point getTouchPositionFromDragEvent(View item, MotionEvent event) {
        Rect rItem = new Rect();
        item.getGlobalVisibleRect(rItem);
        return new Point(rItem.left + Math.round(event.getX()), rItem.top + Math.round(event.getY()));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.e(TAG, "-------------onTouch---------------------");

        ConstraintLayout.LayoutParams leftPaneLP = (ConstraintLayout.LayoutParams) left_pane.getLayoutParams();
        ConstraintLayout.LayoutParams midPaneLP = (ConstraintLayout.LayoutParams) mid_pane.getLayoutParams();
        ConstraintLayout.LayoutParams rightPaneLP = (ConstraintLayout.LayoutParams) right_pane.getLayoutParams();

        switch (v.getId()) {
            case R.id.pan_tool_1:
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.e(TAG, "---------------ACTION_MOVE------------------");
                    Log.e(TAG, "-------------------drag entered: " + event.getX() + ", " + event.getY());
                    float width = getResources().getDisplayMetrics().widthPixels;
                    float height = getResources().getDisplayMetrics().heightPixels;

                    float widthPercent = event.getX() / width;
                    Log.e(TAG, "---------------percent: " + widthPercent);

                    Point touchPosition = getTouchPositionFromDragEvent(v, event);
                    Log.e(TAG, "---------------percent2: " + touchPosition.x / width);

                    float widthPer1 = touchPosition.x / width;
                    float widthPer2 = 1 - (touchPosition.x / width);

                    leftPaneLP.horizontalWeight = widthPer1;

                    float y = midPaneLP.horizontalWeight;
                    float z = rightPaneLP.horizontalWeight;

                    float mrRatio = y / (y + z);
                    Log.e(TAG, "------------mRatio: " + mrRatio);

                    midPaneLP.horizontalWeight = mrRatio * widthPer2;
                    rightPaneLP.horizontalWeight = widthPer2 - midPaneLP.horizontalWeight;

                    Log.e(TAG, "-----------weight1: " + leftPaneLP.horizontalWeight);
                    Log.e(TAG, "-----------weight2: " + midPaneLP.horizontalWeight);
                    Log.e(TAG, "-----------weight3: " + rightPaneLP.horizontalWeight);

                    left_pane.setLayoutParams(leftPaneLP);
                    mid_pane.setLayoutParams(midPaneLP);
                    right_pane.setLayoutParams(rightPaneLP);
                }
                break;
            case R.id.pan_tool_2:
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.e(TAG, "---------------ACTION_MOVE------------------");
                    Log.e(TAG, "-------------------drag entered: " + event.getX() + ", " + event.getY());
                    float width = getResources().getDisplayMetrics().widthPixels;
                    float height = getResources().getDisplayMetrics().heightPixels;

                    float widthPercent = event.getX() / width;
                    Log.e(TAG, "---------------percent: " + widthPercent);

                    Point touchPosition = getTouchPositionFromDragEvent(v, event);
                    Log.e(TAG, "---------------percent2: " + touchPosition.x / width);

                    final float widthPer1 = (touchPosition.x / width) - (leftPaneLP.horizontalWeight);
                    final float widthPer2 = Math.abs(1 - (widthPer1 + leftPaneLP.horizontalWeight));
                    midPaneLP.horizontalWeight = widthPer1;
                    rightPaneLP.horizontalWeight = widthPer2;

                    mid_pane.setLayoutParams(midPaneLP);
                    right_pane.setLayoutParams(rightPaneLP);
                }
                break;

            case R.id.pan_tool_3:
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.e(TAG, "---------------ACTION_MOVE------------------");
                    Log.e(TAG, "-------------------drag entered: " + event.getX() + ", " + event.getY());
                    float width = getResources().getDisplayMetrics().widthPixels;
                    float height = getResources().getDisplayMetrics().heightPixels;

                    float widthPercent = event.getX() / width;
                    Log.e(TAG, "---------------percent: " + widthPercent);

                    Point touchPosition = getTouchPositionFromDragEvent(v, event);
                    Log.e(TAG, "---------------percent2: " + touchPosition.x / width);

                    Rect rItem = new Rect();
                    mid_pane.getGlobalVisibleRect(rItem);
                    Point p = new Point(rItem.right + Math.round(event.getX()), rItem.top + Math.round(event.getY()));

                    final float widthPer1 = p.x / width;
                    final float widthPer2 = 1 - widthPer1;

                    leftPaneLP.horizontalWeight = widthPer2 / 2;
                    midPaneLP.horizontalWeight = widthPer2 / 2;
                    rightPaneLP.horizontalWeight = widthPer1;

                    left_pane.setLayoutParams(leftPaneLP);
                    mid_pane.setLayoutParams(midPaneLP);
                    right_pane.setLayoutParams(rightPaneLP);
                }
                break;
        }

        return true;
    }
}
