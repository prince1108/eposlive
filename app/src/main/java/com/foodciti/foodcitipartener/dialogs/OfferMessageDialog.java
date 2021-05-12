package com.foodciti.foodcitipartener.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;

import static android.content.Context.MODE_PRIVATE;
import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;


public class OfferMessageDialog extends DialogFragment {
    private static final String TAG = "OfferMessageDialog";
    private TextView capsOnOffBtn;
    private TextView qKey, wKey, eKey, rKey, tKey, yKey, uKey, iKey, oKey, pKey, aKey, sKey, dKey, fKey, gKey, hKey, jKey, kKey, lKey, zKey, xKey, cKey, vKey, bKey, nKey, mKey;
    private boolean isCapsOn = false;
    private TextView update_btn;
    private Realm myRealm;
    private static int id = 1;
    public static EditText smsMessage;
    public smsSend mListener;
    AutoCompleteTextView customertelephone;
    public static AutoCompleteTextView customerpostcode;
    private ImageView closebtn;
    public static boolean orderupdatestastus = false;
    public static boolean calleridstatus = false;
    String mobile = "";
    SharedPreferences.Editor editor;
    static final String REQ_TAG = "VACTIVITY";

    private CustomerInfo customerInfo;
    private long[] userId;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    String JsonURL = "";
    String msg = "";
    SharedPreferences shared;

    private OfferMessageDialog(){}

    public static OfferMessageDialog getInstance(boolean verify) {
        OfferMessageDialog offerMessageDialog = new OfferMessageDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("VERIFY", verify);
        bundle.putString("mobile_no", null);
        bundle.putLong("userId", -1l);
        offerMessageDialog.setArguments(bundle);
        offerMessageDialog.setCancelable(true);
        return offerMessageDialog;
    }

    public static OfferMessageDialog getInstance(String mobile, smsSend mListener) {
        Log.e(TAG, "-------numbers: " + mobile);
        OfferMessageDialog offerMessageDialog = new OfferMessageDialog();
        Bundle bundle = new Bundle();
        bundle.putString("mobile_no", mobile);
        bundle.putLong("userId", -1l);
        offerMessageDialog.setArguments(bundle);
        offerMessageDialog.mListener = mListener;
        offerMessageDialog.setCancelable(true);
        return offerMessageDialog;
    }

    public static OfferMessageDialog getInstance(long[] userId) {
        Log.e(TAG, "-------userID: " + userId);
        OfferMessageDialog addition = new OfferMessageDialog();
        Bundle bundle = new Bundle();
        bundle.putString("mobile_no", null);
        bundle.putLongArray("userIds", userId);
        addition.setArguments(bundle);
        return addition;
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.message_screen, container,
                false);

        shared = getContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        myRealm = RealmManager.getLocalInstance();
        editor = getContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedDate = df.format(date);

        editor = getContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
        //getCustomerDetails();

       /* requestQueue = RequestQueueSingleton.getInstance(getContext().getApplicationContext())
                .getRequestQueue();*/


      /*  requestQueue = Volley.newRequestQueue(getContext());


        databaseHandler=new DatabaseHandler(getContext());*/


        smsMessage = (EditText) rootView.findViewById(R.id.houseNo);

        if (getArguments() != null) {
            mobile = getArguments().getString("mobile_no");
//            customerInfo=myRealm.where(CustomerInfoActivity.class).equalTo("id", getArguments().getLong("userId")).findFirst();
            userId = getArguments().getLongArray("userIds");
        }

        closebtn = (ImageView) rootView.findViewById(R.id.close);
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        rootView.findViewById(R.id.sp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("SP");
            }
        });

        rootView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("×");
            }
        });


        qKey = rootView.findViewById(R.id.q);
        qKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("Q");
            }
        });

        wKey = rootView.findViewById(R.id.w);
        wKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("W");
            }
        });

        eKey = rootView.findViewById(R.id.e);
        eKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("E");
            }
        });

        rKey = rootView.findViewById(R.id.r);
        rKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("R");
            }
        });

        tKey = rootView.findViewById(R.id.t);
        tKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("T");
            }
        });

        yKey = rootView.findViewById(R.id.y);
        yKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("Y");
            }
        });

        uKey = rootView.findViewById(R.id.u);
        uKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("U");
            }
        });

        iKey = rootView.findViewById(R.id.i);
        iKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("I");
            }
        });

        oKey = rootView.findViewById(R.id.o);
        oKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("O");
            }
        });

        pKey = rootView.findViewById(R.id.p);
        pKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("P");
            }
        });

        aKey = rootView.findViewById(R.id.a);
        aKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("A");
            }
        });

        sKey = rootView.findViewById(R.id.s);
        sKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("S");
            }
        });

        dKey = rootView.findViewById(R.id.d);
        dKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("D");
            }
        });

        fKey = rootView.findViewById(R.id.f);
        fKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("F");
            }
        });

        gKey = rootView.findViewById(R.id.g);
        gKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("G");
            }
        });

        hKey = rootView.findViewById(R.id.h);
        hKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("H");
            }
        });

        jKey = rootView.findViewById(R.id.j);
        jKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("J");
            }
        });

        kKey = rootView.findViewById(R.id.k);
        kKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("K");
            }
        });

        lKey = rootView.findViewById(R.id.l);
        lKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("L");
            }
        });

        mKey = rootView.findViewById(R.id.m);
        mKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("M");
            }
        });

        zKey = rootView.findViewById(R.id.z);
        zKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("Z");
            }
        });

        xKey = rootView.findViewById(R.id.x);
        xKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("X");
            }
        });

        cKey = rootView.findViewById(R.id.c);
        cKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("C");
            }
        });

        vKey = rootView.findViewById(R.id.v);
        vKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("V");
            }
        });

        bKey = rootView.findViewById(R.id.b);
        bKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("B");
            }
        });

        nKey = rootView.findViewById(R.id.n);
        nKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("N");
            }
        });
        rootView.findViewById(R.id.new_line_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  setValue("@");

                setValue("NL");
            }
        });
        rootView.findViewById(R.id.dot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(".");
            }
        });
        rootView.findViewById(R.id.pound_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("£");
            }
        });
        rootView.findViewById(R.id.question_mark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("?");
            }
        });
        rootView.findViewById(R.id.dash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("-");
            }
        });
        rootView.findViewById(R.id.plus_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("+");
            }
        });

        rootView.findViewById(R.id.one1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("1");
            }
        });

        rootView.findViewById(R.id.two2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("2");
            }
        });

        rootView.findViewById(R.id.three3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("3");
            }
        });

        rootView.findViewById(R.id.four4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("4");
            }
        });

        rootView.findViewById(R.id.five5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("5");
            }
        });

        rootView.findViewById(R.id.six6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("6");
            }
        });

        rootView.findViewById(R.id.seven7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("7");
            }
        });

        rootView.findViewById(R.id.eight8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("8");
            }
        });

        rootView.findViewById(R.id.nine9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("9");
            }
        });

        rootView.findViewById(R.id.zero1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("0");
            }
        });

        rootView.findViewById(R.id.at_rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("@");
            }
        });
        rootView.findViewById(R.id.hash_tag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("#");
            }
        });
        rootView.findViewById(R.id.exclaim_mark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("!");
            }
        });

        rootView.findViewById(R.id.amPercend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("&");
            }
        });

        rootView.findViewById(R.id.astrik).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("*");
            }
        });

        final TextView dblQts = rootView.findViewById(R.id.double_qoutes);
        dblQts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(dblQts.getText().toString());
            }
        });

        final TextView percentMark = rootView.findViewById(R.id.percent_mark);
        percentMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(percentMark.getText().toString());
            }
        });

        rootView.findViewById(R.id.left_brac).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("(");
            }
        });

        rootView.findViewById(R.id.right_brac).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(")");
            }
        });

        rootView.findViewById(R.id.back_slash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue("/");
            }
        });

        capsOnOffBtn = rootView.findViewById(R.id.caps_on_off);
        capsOnOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCapsOn = !isCapsOn;
                if (isCapsOn) {
                    capsOnOffBtn.setText("abc");
                } else {
                    capsOnOffBtn.setText("ABC");
                }

                setCapsOnOff(isCapsOn);

            }
        });

        rootView.findViewById(R.id.enterButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (smsMessage.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Empty Field", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("MESSAGE_SEND", "" + smsMessage.getText().toString());
                    mListener.sendMessage(userId, smsMessage.getText().toString());
                    //throughMessage(smsMessage.getText().toString(),mobile);
                    //sendSMSMessage(smsMessage.getText().toString(),mobile);

                    /*PendingIntent pi = PendingIntent.getActivity(getContext(), 0,
                            new Intent(getContext(), CustomerMessageList.class), 0);
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(mobile, null, smsMessage.getText().toString(), pi, null);*/


                    dismiss();

                }

            }
        });

        smsMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                /*InputMethodManager mgr = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(customeraddress.getWindowToken(), 0);*/
                final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(smsMessage.getWindowToken(), 0);

                // view.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                smsMessage.requestFocus();
                // smsMessage.setSelection(smsMessage.getText().toString().length());

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Layout layout = ((TextView) view).getLayout();
                    int x = (int) motionEvent.getX();
                    int y = (int) motionEvent.getY();
                    if (layout != null) {
                        int line = layout.getLineForVertical(y);
                        int offset = layout.getOffsetForHorizontal(line, x);
                        Log.v("index", "" + offset);

                        //Toast.makeText(getContext(),""+offset,Toast.LENGTH_LONG).show();
                        smsMessage.setSelection(offset);
                    }
                }

                return true;
            }
        });

        return rootView;
    }

    private void setCapsOnOff(boolean isOn) {
        if (isOn) {
            qKey.setText(qKey.getText().toString().toUpperCase());
            wKey.setText(wKey.getText().toString().toUpperCase());
            eKey.setText(eKey.getText().toString().toUpperCase());
            rKey.setText(rKey.getText().toString().toUpperCase());
            tKey.setText(tKey.getText().toString().toUpperCase());
            yKey.setText(yKey.getText().toString().toUpperCase());
            uKey.setText(uKey.getText().toString().toUpperCase());
            iKey.setText(iKey.getText().toString().toUpperCase());
            oKey.setText(oKey.getText().toString().toUpperCase());
            pKey.setText(pKey.getText().toString().toUpperCase());
            aKey.setText(aKey.getText().toString().toUpperCase());
            sKey.setText(sKey.getText().toString().toUpperCase());
            dKey.setText(dKey.getText().toString().toUpperCase());
            fKey.setText(fKey.getText().toString().toUpperCase());
            gKey.setText(gKey.getText().toString().toUpperCase());
            hKey.setText(hKey.getText().toString().toUpperCase());
            jKey.setText(jKey.getText().toString().toUpperCase());
            kKey.setText(kKey.getText().toString().toUpperCase());
            lKey.setText(lKey.getText().toString().toUpperCase());
            zKey.setText(zKey.getText().toString().toUpperCase());
            xKey.setText(xKey.getText().toString().toUpperCase());
            cKey.setText(cKey.getText().toString().toUpperCase());
            vKey.setText(vKey.getText().toString().toUpperCase());
            bKey.setText(bKey.getText().toString().toUpperCase());
            nKey.setText(nKey.getText().toString().toUpperCase());
            mKey.setText(mKey.getText().toString().toUpperCase());
        } else {
            qKey.setText(qKey.getText().toString().toLowerCase());
            wKey.setText(wKey.getText().toString().toLowerCase());
            eKey.setText(eKey.getText().toString().toLowerCase());
            rKey.setText(rKey.getText().toString().toLowerCase());
            tKey.setText(tKey.getText().toString().toLowerCase());
            yKey.setText(yKey.getText().toString().toLowerCase());
            uKey.setText(uKey.getText().toString().toLowerCase());
            iKey.setText(iKey.getText().toString().toLowerCase());
            oKey.setText(oKey.getText().toString().toLowerCase());
            pKey.setText(pKey.getText().toString().toLowerCase());
            aKey.setText(aKey.getText().toString().toLowerCase());
            sKey.setText(sKey.getText().toString().toLowerCase());
            dKey.setText(dKey.getText().toString().toLowerCase());
            fKey.setText(fKey.getText().toString().toLowerCase());
            gKey.setText(gKey.getText().toString().toLowerCase());
            hKey.setText(hKey.getText().toString().toLowerCase());
            jKey.setText(jKey.getText().toString().toLowerCase());
            kKey.setText(kKey.getText().toString().toLowerCase());
            lKey.setText(lKey.getText().toString().toLowerCase());
            zKey.setText(zKey.getText().toString().toLowerCase());
            xKey.setText(xKey.getText().toString().toLowerCase());
            cKey.setText(cKey.getText().toString().toLowerCase());
            vKey.setText(vKey.getText().toString().toLowerCase());
            bKey.setText(bKey.getText().toString().toLowerCase());
            nKey.setText(nKey.getText().toString().toLowerCase());
            mKey.setText(mKey.getText().toString().toLowerCase());
        }
    }

    private void setValue(String s) {
        if (s.equalsIgnoreCase("×")) {
            if (smsMessage.getText().toString().length() > 0) {
                String value = smsMessage.getText().toString();
                StringBuilder sb = new StringBuilder(value);
                // Toast.makeText(getContext(),""+remark.getSelectionStart(),Toast.LENGTH_LONG).show();
                int i = smsMessage.getSelectionStart();
                if (i > 0) {
                    sb = sb.deleteCharAt(smsMessage.getSelectionStart() - 1);
                    smsMessage.setText(sb);

                    smsMessage.setSelection(i - 1);
                }
            }
        } else if (s.equalsIgnoreCase("SP")) {

            smsMessage.getText().insert(smsMessage.getSelectionStart(), " ");
        } else if (s.equalsIgnoreCase("NL")) {
            smsMessage.append("\n");
        } else {
            if (isCapsOn) {
                smsMessage.getText().insert(smsMessage.getSelectionStart(), s.toUpperCase());
            } else {
                smsMessage.getText().insert(smsMessage.getSelectionStart(), s.toLowerCase());
            }
        }
        smsMessage.setSelection(smsMessage.getSelectionStart());

        //  Toast.makeText(getContext(),""+datas[position].toString(),Toast.LENGTH_LONG).show();

        //  Toast.makeText(getContext(),""+datas[position].toString(),Toast.LENGTH_LONG).show();
    }

    /*private void StartLogin(){
        String url = "https://meraproject.in/project/mani/api/test.php";
        LayoutInflater layoutInflater = getLayoutInflater();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.trim());
                            //parseJSON(jsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Snackbar.make(parent_layout, "Server Error.. Try Again", Snackbar.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request

              //  params.put("q", "checklogin");
                params.put("text_message", smsMessage.getText().toString());  //for google and fb it will be email
                params.put("mobile_number", mobile);

                //returning parameter
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.getCache().clear();
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 16 * 1024 * 1024);
        requestQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        requestQueue.start();
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);


    }*/

   /* private void StartRegister(){
        String url= "https://meraproject.in/project/mani/api/test.php";
        LayoutInflater layoutInflater = getLayoutInflater();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //  Toast.makeText(Register.this,response.toString(),Toast.LENGTH_SHORT).show();
                       *//* try {
                          //  parseJSONRegister(new JSONObject(response.trim()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*//*


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want

                        //Snackbar.make(relativeLayout, "Server Error", Snackbar.LENGTH_SHORT).show();
                        //   Toast.makeText(Register.this, error.toString(), Toast.LENGTH_LONG).show();
                        Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request

                params.put("text_message", smsMessage.getText().toString());  //for google and fb it will be email
                params.put("mobile_number", mobile);


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.getCache().clear();
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 16 * 1024 * 1024);
        requestQueue= new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        requestQueue.start();
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);


      *//*  try {
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            String URL = "https://meraproject.in/project/mani/api/test.php";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("text_message", smsMessage.getText().toString());
            jsonBody.put("mobile_number", mobile);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }*//*
    }*/

 /*   protected void sendSMSMessage(String message,String mobile) {
       // phoneNo = txtphoneNo.getText().toString();
       // message = txtMessage.getText().toString();

        msg=message;
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getApplicationContext(),
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(mobile, null, msg, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }*/


    private void throughMessage(String message, String mobile) {

        String url = "http://35.178.91.227:3000/usersms/sendsms?dest=" + mobile + "&msg=" + message;

       /* StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(getContext(),response.toString(), Toast.LENGTH_SHORT).show();
                        try {
                            Toast.makeText(getContext(),"Message Successfully Sent !", Toast.LENGTH_SHORT).show();

                            dismiss();
                            JSONObject jsonObject=new JSONObject(response.trim());

                           // latestVersion=jsonObject.getString("latestversion");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                                  Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();

                    }
                });

        if(backQueue==null){
            if(getContext()!=null){
                backQueue= Volley.newRequestQueue(getContext().getApplicationContext());
                backQueue.getCache().clear();
                DiskBasedCache cache = new DiskBasedCache(getContext().getCacheDir(), 16 * 1024 * 1024);
                backQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
                backQueue.start();
                stringRequest.setShouldCache(false);
                backQueue.add(stringRequest);
            }
        }else{
            if(getContext()!=null){
                backQueue.getCache().clear();
                stringRequest.setShouldCache(false);
                backQueue.add(stringRequest);
            }
        }*/


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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


    public interface smsSend {
        void sendMessage(long[] users, String textMessage);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof smsSend) {
            mListener = (smsSend) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement smsSend");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
