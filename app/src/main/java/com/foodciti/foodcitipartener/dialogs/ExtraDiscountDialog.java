package com.foodciti.foodcitipartener.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.foodciti.foodcitipartener.R;

import static android.content.Context.MODE_PRIVATE;
import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

public class ExtraDiscountDialog extends DialogFragment {
    private ImageView closebtn;
    private double totalptice = 0.0, totalActualPrice = 0.0, finalPrice = 0.0, actualTotal = 0.0;
    LinearLayout apply_btn;
    private ExtraDiscountListener mListener;
    EditText textValue;
    TextView pound, percent, extra, discount;
    TextView bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9, bt0, ok, cross, point;
    String whatType = "discount", inWhich = "percent";


    String isPoint = "no";
    SharedPreferences.Editor editor;

    private ExtraDiscountDialog(){}

    public static ExtraDiscountDialog getInstance(boolean verify, FragmentManager supportFragmentManager) {
        ExtraDiscountDialog discountDialog = new ExtraDiscountDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("VERIFY", verify);
        discountDialog.setArguments(bundle);
        discountDialog.setCancelable(true);
        return discountDialog;
    }

    public static ExtraDiscountDialog getInstance(double totalprice) {
        ExtraDiscountDialog discountDialog = new ExtraDiscountDialog();
        Bundle bundle = new Bundle();
        bundle.putDouble("totalprice", totalprice);
        discountDialog.setArguments(bundle);
        discountDialog.setCancelable(true);
        return discountDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.extra_discount_dialog, container,
                false);
        closebtn = (ImageView) rootView.findViewById(R.id.close);


        editor = getContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
        textValue = (EditText) rootView.findViewById(R.id.textValue);
        cross = (TextView) rootView.findViewById(R.id.cross);
        point = (TextView) rootView.findViewById(R.id.point);

        bt1 = rootView.findViewById(R.id.bt1);
        bt2 = rootView.findViewById(R.id.bt2);
        bt3 = rootView.findViewById(R.id.bt3);
        bt4 = rootView.findViewById(R.id.bt4);
        bt5 = rootView.findViewById(R.id.bt5);
        bt6 = rootView.findViewById(R.id.bt6);
        bt7 = rootView.findViewById(R.id.bt7);
        bt8 = rootView.findViewById(R.id.bt8);
        bt9 = rootView.findViewById(R.id.bt9);
        bt0 = rootView.findViewById(R.id.bt0);
        ok = rootView.findViewById(R.id.ok);

        pound = rootView.findViewById(R.id.pound);
        percent = rootView.findViewById(R.id.percent);
        extra = rootView.findViewById(R.id.extra);
        discount = rootView.findViewById(R.id.cash);


        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        discount.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape_color));
        percent.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape_color));


        if (getArguments() != null) {
            totalptice = getArguments().getDouble("totalprice");
            actualTotal = getArguments().getDouble("totalprice");
        }

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noteTotalValue = textValue.getText().toString() + "1";
                Log.e("Discount", noteTotalValue + "1");
                textValue.setText(noteTotalValue);
                textValue.setSelection(textValue.getText().toString().length());

            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String noteTotalValue = textValue.getText().toString();
                textValue.setText(noteTotalValue + "2");
                textValue.setSelection(textValue.getText().toString().length());

            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String noteTotalValue = textValue.getText().toString();
                textValue.setText(noteTotalValue + "3");
                textValue.setSelection(textValue.getText().toString().length());

            }
        });

        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String noteTotalValue = textValue.getText().toString();
                textValue.setText(noteTotalValue + "4");
                textValue.setSelection(textValue.getText().toString().length());
            }
        });

        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String noteTotalValue = textValue.getText().toString();
                textValue.setText(noteTotalValue + "5");
                textValue.setSelection(textValue.getText().toString().length());

            }
        });


        bt6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String noteTotalValue = textValue.getText().toString();
                textValue.setText(noteTotalValue + "6");
                textValue.setSelection(textValue.getText().toString().length());

            }
        });

        bt7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String noteTotalValue = textValue.getText().toString();
                textValue.setText(noteTotalValue + "7");
                textValue.setSelection(textValue.getText().toString().length());

            }
        });

        bt8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String noteTotalValue = textValue.getText().toString();
                textValue.setText(noteTotalValue + "8");
                textValue.setSelection(textValue.getText().toString().length());

            }
        });

        bt9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String noteTotalValue = textValue.getText().toString();
                textValue.setText(noteTotalValue + "9");
                textValue.setSelection(textValue.getText().toString().length());
            }
        });

        bt0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String noteTotalValue = textValue.getText().toString();
                textValue.setText(noteTotalValue + "0");
                textValue.setSelection(textValue.getText().toString().length());
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textValue.setText("");
                isPoint = "no";
            }
        });

        point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isPoint.equalsIgnoreCase("no")) {
                    String noteTotalValue = textValue.getText().toString();
                    textValue.setText(noteTotalValue + ".");
                    isPoint = "yes";
                }
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String noteTotalValue = textValue.getText().toString();
                if (noteTotalValue.trim().length() == 0) {

                    View focusview = textValue;
                    textValue.setError("Invalid Amount");
                    focusview.requestFocus();
                } else {


                    Double applyValue = Double.parseDouble(noteTotalValue);
                    double value = Double.parseDouble(noteTotalValue);
                    String totalPrice = String.valueOf(totalptice);

                    if (whatType.equalsIgnoreCase("discount") && inWhich.equalsIgnoreCase("percent")) {
                        // Toast.makeText(getActivity(),"discount/percent",Toast.LENGTH_LONG).show();

                        editor.putString("discountPercent", String.valueOf(value));
                        editor.putString("discountPound", "");

                        Double totalAmountbyPer = totalptice * applyValue / 100;
                        totalptice = totalptice - totalAmountbyPer;

                    } else if (whatType.equalsIgnoreCase("discount") && inWhich.equalsIgnoreCase("pound")) {
                        // Toast.makeText(getActivity(),"discount/pound",Toast.LENGTH_LONG).show();
                        editor.putString("discountPercent", "");
                        editor.putString("discountPound", String.valueOf(applyValue));

                        totalptice = totalptice - applyValue;

                    } else if (whatType.equalsIgnoreCase("extra") && inWhich.equalsIgnoreCase("percent")) {
                        //  Toast.makeText(getActivity(),"extra/percent",Toast.LENGTH_LONG).show();

                        editor.putString("extraPercent", String.valueOf(value));
                        editor.putString("extratPound", "");

                        Double totalAmountbyPer = totalptice * applyValue / 100;
                        totalptice = totalptice + totalAmountbyPer;

                    } else if (whatType.equalsIgnoreCase("extra") && inWhich.equalsIgnoreCase("pound")) {
                        //  Toast.makeText(getActivity(),"extra/pound",Toast.LENGTH_LONG).show();

                        editor.putString("extraPercent", "");
                        editor.putString("extratPound", String.valueOf(applyValue));

                        totalptice = totalptice + applyValue;
                    }
                    editor.apply();
                    mListener.updateTotalPrice(String.valueOf(totalptice), String.valueOf(applyValue), whatType, inWhich, totalPrice, String.valueOf(actualTotal));
                    dismiss();
                }


            }
        });

        pound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inWhich = "pound";

                percent.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape));
                pound.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape_color));
                // percent.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape));
            }
        });

        discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whatType = "discount";
                inWhich = "percent";

                extra.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape));
                discount.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape_color));
                percent.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape_color));
                pound.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape));
                //   discount.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape));
            }
        });

        percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inWhich = "percent";

                pound.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape));
                percent.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape_color));
                // percent.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape));
            }
        });

        extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whatType = "extra";
                inWhich = "pound";

                pound.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape_color));
                extra.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape_color));
                discount.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape));
                percent.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_shape));
            }
        });


     /*   totalActualPrice=totalptice;
        discount_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClickAddress(View view) {
                if(discount_edit.getText().toString().equalsIgnoreCase("")){

                }else{
                    discount_edit_pound.setText("");
                    total_discount_pound.setText("");
                    Double discountper=0.00;
                    String inputDiscount=discount_edit.getText().toString();
                    discountper=Double.parseDouble(inputDiscount);

                    Double totalAmountbyPer=totalptice*discountper/100;
                    totalptice=totalptice-totalAmountbyPer;

                  //  totalActualPrice=totalptice;
                    total_discount.setText("Total Amount : "+String.valueOf(totalActualPrice)+" % "+inputDiscount+" = "+String.format(" %.2f", Double.valueOf(totalptice)));

                   finalPrice= totalptice;
                }
            }
        });
*/
       /* discount_apply_pound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClickAddress(View view) {

                if(discount_edit_pound.getText().toString().equalsIgnoreCase("")){

                }else{
                    total_discount.setText("");
                    discount_edit.setText("");
                    Double discountper=0.00;
                    String inputDiscount=discount_edit_pound.getText().toString();
                    discountper=Double.parseDouble(inputDiscount);

                    Double totalAmountbyPer=totalActualPrice-discountper;

                    total_discount_pound.setText("Total Amount : "+String.valueOf(totalActualPrice)+" - "+inputDiscount+" = "+String.format(" %.2f", Double.valueOf(totalAmountbyPer)));

                    finalPrice= totalAmountbyPer;
                }

            }
        });*/

       /* add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClickAddress(View view) {

                if(extra_edit.getText().toString().equalsIgnoreCase("")){

                }else{

                    Double discountper=0.00;
                    String inputDiscount=extra_edit.getText().toString();
                    discountper=Double.parseDouble(inputDiscount);

                    Double totalAmountbyPer=totalActualPrice+discountper;
                    total_extra.setText("Total Amount : "+String.valueOf(totalActualPrice)+" + "+inputDiscount+" = "+String.format(" %.2f", Double.valueOf(totalAmountbyPer)));

                    finalPrice= totalAmountbyPer;
                }

            }
        });

        apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClickAddress(View view) {
                mListener.updateTotalPrice(String.valueOf(finalPrice));
                dismiss();
            }
        });*/


        return rootView;
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
            /*dialog.getWindow()
                    .setLayout((int) (getScreenWidth(getActivity()) * 0.99), (int) (getScreenWidth(getActivity()) * 0.99));*/
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.99);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.98);

            dialog.getWindow().setLayout(width, height);
        }
    }

    public int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    public interface ExtraDiscountListener {
        void updateTotalPrice(String totalPriceAmt, String howMuch, String whatType, String inWhich, String totalPrice, String actualTotal);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ExtraDiscountListener) {
            mListener = (ExtraDiscountListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    //  protected abstract void onDataReceived(final byte[] buffer, final int size);

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

}
