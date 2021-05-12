package com.foodciti.foodcitipartener.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.OrderInfoAdapter;
import com.foodciti.foodcitipartener.db.ExceptionLogger;
import com.foodciti.foodcitipartener.realm_entities.Exlogger;
import com.foodciti.foodcitipartener.response.GenericResponse;
import com.foodciti.foodcitipartener.response.InventoryRestaurent;
import com.foodciti.foodcitipartener.response.Order;
import com.foodciti.foodcitipartener.response.OrderedItem;
import com.foodciti.foodcitipartener.response.SubItem;
import com.foodciti.foodcitipartener.response.SubOptions;
import com.foodciti.foodcitipartener.rest.RetroClient;
import com.foodciti.foodcitipartener.utils.Consts;
import com.foodciti.foodcitipartener.utils.InternetConnection;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.PrintUtils;
import com.foodciti.foodcitipartener.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;


/**
 * Created by ravi on 06-10-2020.
 */

public class OrderInfo extends DialogFragment implements OrderInfoAdapter.OnItemClickListener, CookingTime.OnClickListener, View.OnClickListener {

    //    protected Application mApplication;
//    protected SerialPort mSerialPort;
//    protected OutputStream mOutputStream;
//    protected InputStream mInputStream;
//    protected ReadThread mReadThread;
    FragmentActivity activity;
    ExceptionLogger logger;
    char[] text;
    TextView mPrintReception, order_type;
    private LocalBroadcastManager localBroadcastManager;
    SharedPreferences sharedpreferences;


    @Override
    public synchronized void print() {
        sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        int printCounter=0;
        if (order.getOrderDelivery() != null) {
            if (order.getOrderDelivery().equalsIgnoreCase("Pick-up")) {
                printCounter = sharedpreferences.getInt(Preferences.NUM_PRINT_COPIES_COL, 1);
            } else {
                printCounter = sharedpreferences.getInt(Preferences.NUM_PRINT_COPIES_DEL, 1);
            }
        }
        if (printCounter > 0) {
            for (int i = 0; i < printCounter; i++) {
                print4();
            }
        }
//        else{
//            for (int i = 0; i < 2; i++) {
//                print4();
//            }
//        }

        listener.onOrderAccepted(order.getOrderId());
        dismissAllowingStateLoss();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDataReceived(final byte[] buffer, final int size) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (mPrintReception != null) {
                        // mPrintReception.append(new String(buffer, 0, size));
                        for (int i = 0; i < size; i++) {
                            String s = Integer.toHexString((int) buffer[i]);//String.valueOf(((char)buffer[i]));
                            mPrintReception.append(s + ' ');
                        }
                    }
                }
            });
        }

    }

    public static byte[] setAlignCenter(char paramChar) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1B;
        arrayOfByte[1] = 0x61;

        switch (paramChar) {
            case '1':
                arrayOfByte[2] = 0x01;
                break;
            case '2':
                arrayOfByte[2] = 0x02;
                break;
            default:
                arrayOfByte[2] = 0x00;
                break;
        }
        return arrayOfByte;
    }

    public static byte[] setWH(char paramChar) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1D;
        arrayOfByte[1] = 0x21;

        switch (paramChar) {
            case '1':
                arrayOfByte[2] = 0x09;

                break;
            case '2':
                arrayOfByte[2] = 0x10;
                break;
            case '3':
                arrayOfByte[2] = 0x01;
                break;
            case '4':
                arrayOfByte[2] = 0x11;
                break;
            default:
                arrayOfByte[2] = 0x00;
                break;
        }

        return arrayOfByte;
    }

    public static byte[] setInternationalCharcters(char paramChar) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1B;
        arrayOfByte[1] = 0x52;
        //arrayOfByte.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Consts.COMFORTAA_BOLD));
        switch (paramChar) {
            case '1':
                arrayOfByte[2] = 0x01;//France charactrer set
                break;
            case '2':
                arrayOfByte[2] = 0x02;//Germany charactrer set
                break;
            case '3':
                arrayOfByte[2] = 0x03;//UK charactrer set
                break;
            default:
                arrayOfByte[2] = 0x00;//USA charactrer set
                break;
        }

        return arrayOfByte;
    }

    public static byte[] getGbk(String paramString) {
        byte[] arrayOfByte = null;
        try {
            arrayOfByte = paramString.getBytes("GBK");
        } catch (Exception ex) {

        }
        return arrayOfByte;

    }

    public static byte[] setCusorPosition(int position) {
        byte[] returnText = new byte[4];
        returnText[0] = 0x1B;
        returnText[1] = 0x24;
        returnText[2] = (byte) (position % 256);
        returnText[3] = (byte) (position / 256);
        return returnText;
    }

    public static byte[] setBold(boolean paramBoolean) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1B;
        arrayOfByte[1] = 0x45;
        if (paramBoolean) {
            arrayOfByte[2] = 0x01;
        } else {
            arrayOfByte[2] = 0x00;
        }
        return arrayOfByte;
    }

    public static byte[] CutPaper()   //切纸； GS V 66D 0D
    {
        byte[] arrayOfByte = new byte[]{0x1D, 0x56, 0x42, 0x00};
        return arrayOfByte;
    }


    private synchronized void print4() {
//        boolean returnValue = true;
        try {
            int jNum = 0;

            byte[] printText22 = new byte[10240];

            byte[] oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setInternationalCharcters('3');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("FoodCiti");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("\nOrder No : " + order.getOrderNo());
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

//            oldText = getGbk("\n-----------------------\n");
            oldText = getGbk("\n........................\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(SessionManager.get(getActivity()).getRestaurantName() + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            String location = SessionManager.get(getActivity()).getRestaurantLocation();
            int spacecount = commacount(location);

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (spacecount >= 1) {
                oldText = getGbk(location.substring(0, location.indexOf(',')) + "\n" + location.substring(location.indexOf(',') + 1) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            } else {
                oldText = getGbk(location + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk(SessionManager.get(getActivity()).getRestaurantPostalCode() + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Tel:" + SessionManager.get(getActivity()).getRestaurantPhonenumber());
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

//            oldText = getGbk("\n-----------------------\n");
            oldText = getGbk("\n........................\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            if (order.getOrderSpecialInstruction() != null) {
                oldText = setAlignCenter('1');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('2');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = getGbk(order.getOrderSpecialInstruction());
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('2');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setAlignCenter('1');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(false);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

//            oldText = getGbk("\n-----------------------\n");
                oldText = getGbk("\n........................\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }

            oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(390);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("         " + "GBP" + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            //   Toast.makeText(getContext(),"Size "+order.getOrderedItemList().size(),Toast.LENGTH_LONG).show();

            for (int i = 0; i < order.getOrderedItemList().size(); i++) {

                OrderedItem orderedItem = order.getOrderedItemList().get(i);


                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

//                int qntity = Integer.parseInt(orderedItem.getQuantity());
                oldText = getGbk(" " + orderedItem.getQuantity() + " x " + orderedItem.getItemData().getItemName());
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                oldText = setCusorPosition(390);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                Double total_price = Double.valueOf(orderedItem.getTotalPrice()) * Double.valueOf(orderedItem.getQuantity());

                oldText = getGbk("        " + String.format(Locale.getDefault(), "%.2f", total_price) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                for (int j = 0; j < orderedItem.getSubItemList().size(); j++) {

                    oldText = setWH('5');
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                    oldText = setCusorPosition(35);
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                    SubItem subItem = orderedItem.getSubItemList().get(j);

                    String subitemname = subItem.getItemName();
                    int subItemOrderQty = Integer.parseInt(subItem.getOrderedQuantity());

                    if (subItemOrderQty > 1) {
                        oldText = getGbk(" " + subItem.getOrderedQuantity() + " x " + subitemname + "\n");
                    } else {
                        oldText = getGbk(" " + subitemname + "\n");
                    }
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;
                    // By Ravi
//                    oldText = getGbk("........................\n");
//                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
//                    jNum += oldText.length;
//////////////////////////////////////////////////////////////////////////////////////////////////////////
                    /** TODO
                     * change here for print suboptions text
                     * **/
                    //print text for suboptions items
                    if (subItem.getSubOptions() != null && subItem.getSubOptions().size() > 0) {
                        List<SubOptions> subOptions = subItem.getSubOptions();
                        for (int k = 0; k < subOptions.size(); k++) {
                            SubOptions options = subOptions.get(k);
                            oldText = getGbk("   - " + options.getName() + "\n");
                            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                            jNum += oldText.length;
                        }
                    }

                }


                oldText = setWH('2');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            /*oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;*/

                oldText = setBold(false);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

//            oldText = getGbk("-----------------------\n");
                oldText = getGbk("........................\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = getGbk("\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            }

            oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Subtotal : ");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(390);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("        " + String.format(" %.2f", Double.valueOf(order.getOrderSubtotal())) + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (Double.valueOf(order.getDiscount()) > 0) {
                oldText = setAlignCenter('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = getGbk("Discount : ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setCusorPosition(390);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                oldText = getGbk("        " + String.format(" %.2f", Double.valueOf(order.getDiscount())) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            if (Double.valueOf(order.getTax()) > 0) {
                oldText = setAlignCenter('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = getGbk("Service Charge : ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setCusorPosition(390);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (order.getTax() != null) {
                    oldText = getGbk("        " + String.format(" %.2f", Double.valueOf(order.getTax())) + "\n");
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;
                } else {
                    oldText = getGbk("        " + "0.00" + "\n");
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;
                }
            }

            if (!order.getOrderDelivery().equals(Consts.PICK_UP) && Double.valueOf(order.getDeliveryCharges()) > 0) {
                oldText = setAlignCenter('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = getGbk("Delivery Charges : ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setCusorPosition(390);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                oldText = getGbk("        " + String.format(" %.2f", Double.valueOf(order.getDeliveryCharges())) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

//            oldText = getGbk("\n-----------------------\n");
            oldText = getGbk("........................\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("TOTAL Price: ");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(370);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            //Toast.makeText(getActivity(),String.valueOf(order.getOrderTotal()),Toast.LENGTH_LONG).show();

            oldText = getGbk("  " + String.format(" %.2f", Double.valueOf(order.getOrderTotal())));
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

//            oldText = getGbk("\n-----------------------\n");
            oldText = getGbk("\n........................\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            if (order.getFreenDrinkText() != null && !TextUtils.isEmpty(order.getFreenDrinkText())) {
                oldText = setAlignCenter('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('1');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                String freeTxt = "Free " + order.getFreenDrinkText();
                oldText = getGbk(freeTxt);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

//            oldText = getGbk("\n-----------------------\n");
            oldText = getGbk("\n........................\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            /** TODO
             * change here to print payment method text
             * **/
            //print text for payment method
            if (order.getOrderPaid().equalsIgnoreCase("paypal") || order.getOrderPaid().equalsIgnoreCase("worldpay")) {
                oldText = getGbk(order.getOrderPaid() + " PAID " + "\n");
            } else {
                oldText = getGbk(order.getOrderPaid() + " NOT PAID " + "\n");
            }
//            oldText = getGbk("ORDER BY " + order.getOrderPaid() + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            if (order.getOrderDelivery().equals(Consts.PICK_UP)) {
                oldText = getGbk("COLLECTION" + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            } else {
                oldText = getGbk("DELIVERY" + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


            }

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            // String strTmp2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.UK).format(new Date());
            oldText = getGbk(getDate(order.getOrderTime()));
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

//            oldText = getGbk("\n-----------------------\n");
            oldText = getGbk("\n........................\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Customer Details: " + "\n" +
                    order.getUser().getUserName().toUpperCase() + "\n" +
                    order.getUser().getAddress().toUpperCase() + "\n" +
                    order.getUser().getCity().toUpperCase() + "\n" +
                    order.getUser().getPostalCode().toUpperCase() + "\n" +
                    order.getUser().getPhNo().toUpperCase()
            );
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

//            oldText = getGbk("\n-----------------------\n");
            oldText = getGbk("\n........................\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Ref:");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk(order.getOrderId());
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

//            oldText = getGbk("\n-----------------------\n");
            oldText = getGbk("\n........................\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("www.foodciti.co.uk");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            /*String s = new String(printText22);
            Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();*/

            oldText = CutPaper();
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            Intent intent = new Intent(PrintUtils.ACTION_PRINT_REQUEST);
            intent.putExtra(PrintUtils.PRINT_DATA, printText22);
            localBroadcastManager.sendBroadcast(intent);

//            mOutputStream.write(printText22);

        } catch (Exception ex) {
            Exlogger exlogger = new Exlogger();
            exlogger.setErrorType("Print Error");
            exlogger.setErrorMessage(ex.getMessage());
            exlogger.setScreenName("OrderInfo->>print4() function");
            logger.addException(exlogger);
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();


        }
    }

    private int commacount(String s) {
        int i;
        int c = 0;
        for (i = 0, c = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == ',')
                c++;
        }
        return c;
    }

    private String getDate(String OurDate) {

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date value = formatter.parse(OurDate);
            formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            OurDate = formatter.format(value);

            Log.e("OurDate", OurDate);
        } catch (Exception e) {
            OurDate = "00-00-0000 00:00";
        }
        return OurDate;
    }


    public static final String TAG = "OrderInfo";
    private RecyclerView recyclerView;
    private OrderInfoAdapter orderAdapter;
    private List<OrderedItem> orderedItemList;
    private Order order;
    private TextView orderId, statusText, userName, userNo,
            acceptOrder, cancelOrder, userAddress, orderItemText, orderByText, postcode, forwardOrderBtn;
    private LinearLayout statusButton, acceptLayout;
    private OnClickListener listener;
    private Button printButton;
    private RelativeLayout cross;
    private String orderStatus;
    private boolean flag = false;
    public Handler handler = new Handler();
    public static MediaPlayer mediaPlayer = null;
    private String restaurantID;


    public static OrderInfo newInstance(Order order) {
        OrderInfo orderInfo = new OrderInfo();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Consts.ORDER_INFO, order);
        orderInfo.setArguments(bundle);
        return orderInfo;
    }

    public static OrderInfo newInstance(Bundle args) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setArguments(args);
        return orderInfo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_dialoge, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
//        mApplication = (Application) getActivity().getApplication();
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        logger = new ExceptionLogger(getActivity());
        Bundle args = getArguments();
        if (args != null) {
            if (!args.getBoolean("dialogstatus")) {
                playSoundFile();
            }
        }
//        try {
//            mSerialPort = mApplication.getPrintSerialPort();
//            mOutputStream = mSerialPort.getOutputStream();
//            mInputStream = mSerialPort.getInputStream();
//
//            /* Create a receiving thread */
//            mReadThread = new ReadThread();
//            mReadThread.start();
//        } catch (SecurityException e) {
//            //DisplayError(R.string.error_security);
//        } catch (IOException e) {
//            //DisplayError(R.string.error_unknown);
//        } catch (InvalidParameterException e) {
//            //	DisplayError(R.string.error_configuration);
//        }

        activity = getActivity();
        restaurantID = SessionManager.get(activity).getFoodTruckId();
        order = getArguments().getParcelable(Consts.ORDER_INFO);
        if (order != null) {
            orderedItemList = order.getOrderedItemList();
        }
        recyclerView = rootView.findViewById(R.id.recycler_view);
        statusText = rootView.findViewById(R.id.statusText);
        userName = rootView.findViewById(R.id.userName);
        acceptOrder = rootView.findViewById(R.id.acceptOrder);
        userAddress = rootView.findViewById(R.id.userAddress);
        cancelOrder = rootView.findViewById(R.id.cancelOrder);
        forwardOrderBtn = rootView.findViewById(R.id.forwardOrder);
        forwardOrderBtn.setVisibility(View.GONE);
        acceptLayout = rootView.findViewById(R.id.acceptLayout);
        orderItemText = rootView.findViewById(R.id.orderItemText);
        orderByText = rootView.findViewById(R.id.orderByText);
        userNo = rootView.findViewById(R.id.userNo);
        order_type = rootView.findViewById(R.id.order_type);
        printButton = rootView.findViewById(R.id.printButton);
        cross = rootView.findViewById(R.id.closeScreen);
        postcode = rootView.findViewById(R.id.postcode);
        orderAdapter = new OrderInfoAdapter(getActivity(), orderedItemList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        statusButton = rootView.findViewById(R.id.statusButton);

        if (order.getUser() != null) {
            userName.setText(order.getUser().getUserName());
            userNo.setText(order.getUser().getPhNo());
            userAddress.setText(order.getUser().getAddress());
            postcode.setText(order.getUser().getPostalCode());
        }

        if (order.getOrderDelivery() != null) {
            if (order.getOrderDelivery().equalsIgnoreCase("Pick-up")) {
                order_type.setText(order.getOrderDelivery());
                rootView.findViewById(R.id.icon_order).setBackgroundResource(R.drawable.shopping_basket);
            } else {
                order_type.setText(order.getOrderDelivery());
                rootView.findViewById(R.id.icon_order).setBackgroundResource(R.drawable.delivery);
            }
        }
        recyclerView.setAdapter(orderAdapter);
        setStatus();
//        Toast.makeText(getActivity(),""+order.getOrderPaid(),Toast.LENGTH_SHORT).show();
        showHideAcceptLayout();

        if (restaurantID.equalsIgnoreCase("5afdab5cc887646c028f5b57")
                || restaurantID.equalsIgnoreCase("5b066eab600aff6e20406c6c")) {
            forwardOrderBtn.setVisibility(View.GONE);
            if (order.getForwardedRestaurantId() != null && !TextUtils.isEmpty(order.getForwardedRestaurantId())) {
                forwardOrderBtn.setText(getString(R.string.send_back));
            } else {
                forwardOrderBtn.setText(getString(R.string.forward_order));
            }
        } else {
            forwardOrderBtn.setVisibility(View.GONE);
        }

        if (!getArguments().getBoolean("dialogstatus")) {
            //timer();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    flag = true;
                    cancelOrder.performClick();
                    //cancelButtonClick();
                }
            }, 600000);
        }

        if (args != null) {
            if (args.containsKey("IS_FROM")) {
                if (args.getString("IS_FROM").equalsIgnoreCase("FORWARD")) {
                    hideButtons();
                }
            }
        }

        acceptOrder.setOnClickListener(this);
        cross.setOnClickListener(this);
        printButton.setOnClickListener(this);
        forwardOrderBtn.setOnClickListener(this);
        cancelOrder.setOnClickListener(this);

        if (TextUtils.isEmpty(SessionManager.get(getActivity()).getRestaurantName()) && TextUtils.isEmpty(SessionManager.get(getActivity()).getRestaurantLocation()) && TextUtils.isEmpty(SessionManager.get(getActivity()).getRestaurantPostalCode())) {
            getFoodTruckInfo(SessionManager.get(getActivity()).getFoodTruckId());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeScreen:
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.stop();
                    //   mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                dismiss();
                break;

            case R.id.acceptOrder:
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.stop();
                    //   mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                acceptOrder(1);
                if (handler != null) {
                    handler.removeMessages(0);
                }
                break;

            case R.id.printButton:
                print4();
                break;

            case R.id.cancelOrder:
                if (flag) {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }

                    cancelOrder();
                } else {
                    if (handler != null) {
                        handler.removeMessages(0);
                    }

                    cancelOrderConformation();
                }
                break;

            case R.id.forwardOrder:
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.stop();
                    //   mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                acceptOrder(4);
                break;
        }
    }


    private void showHideAcceptLayout() {
        if (order.getOrderStatus() == 0) {
            acceptLayout.setVisibility(View.VISIBLE);
            /*if(order.getForwardedRestaurantId()!=null && !TextUtils.isEmpty(order.getForwardedRestaurantId())){
                cancelOrder.setVisibility(View.GONE);
            }else {
                cancelOrder.setVisibility(View.VISIBLE);
            }*/
        } else {
            acceptLayout.setVisibility(View.GONE);
        }
    }

    private void hideButtons() {
        acceptLayout.setVisibility(View.GONE);
        forwardOrderBtn.setVisibility(View.GONE);
        printButton.setVisibility(View.GONE);
    }


    private void acceptOrder(final int status) {
        if (InternetConnection.checkConnection(getActivity())) {
            String token = SessionManager.get(getActivity()).getUserToken();
            Observable<GenericResponse<Order>> results = RetroClient.getApiService()
                    .updateOrder("Bearer " + token, order.getOrderId(), status);
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<Order>>() {
                        @Override
                        public void accept(GenericResponse<Order> response) throws Exception {
                            Log.e("response-accept->", response.getStatus());
                            if (!response.getStatus().equals(Consts.IS_SUCCESS)) {
                                Toast.makeText(getActivity(), "Your order could not be updated", Toast.LENGTH_LONG).show();

                            } else {

                                Log.e("ACCEPT_RESPONSE", "" + response.getData().getOrderDelivery());
                                if (status == 1) {
                                    if (!response.getData().getOrderDelivery().equals("Pick-up")) {

                                        // Toast.makeText(getContext(),"collection",Toast.LENGTH_LONG).show();
                                        CookingTime cookingTime = CookingTime.getInstance(response.getData().getOrderId(), order.getOrderDelivery());
                                        cookingTime.setCancelable(false);
                                        cookingTime.setItemListener(OrderInfo.this);
                                        cookingTime.show(getChildFragmentManager(), null);
                                    } else {
                                        //   Toast.makeText(getContext(),"Delivery",Toast.LENGTH_LONG).show();
                                        CookingTime cookingTime = CookingTime.getInstance(response.getData().getOrderId(), order.getOrderDelivery());
                                        cookingTime.setCancelable(false);
                                        cookingTime.setItemListener(OrderInfo.this);
                                        cookingTime.show(getChildFragmentManager(), null);
                                    }
                                } else if (status == 4) {
                                    Toast.makeText(getActivity(), "Order has been forwarded successfully.", Toast.LENGTH_LONG).show();
                                    if (listener != null) {
                                        Log.e("LISTENER", "" + order.getOrderId());
                                        listener.onOrderForward(order.getOrderId());
                                    }
                                    dismiss();
                                }

                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(getActivity(), "Your order could not be updated", Toast.LENGTH_LONG).show();
                            Log.e("ACCEPT_ORDER_ERROR", "" + throwable.toString());
                        }
                    }));
        } else {
            Toast.makeText(getActivity(), R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();

        }
    }


    private void cancelOrder() {
        if (InternetConnection.checkConnection(activity)) {
            String token = SessionManager.get(activity).getUserToken();
            Observable<GenericResponse> results = RetroClient.getApiService()
                    .cancelOrder("Bearer " + token, order.getOrderId());
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse>() {
                        @Override
                        public void accept(GenericResponse response) throws Exception {
                            if (response.getStatus().equals(Consts.IS_SUCCESS)) {
                                /*Intent intent = new Intent(activity, ResturantMainActivityNewPro.class);
                                startActivity(intent);
                                activity.finish();*/
                                listener.onOrderCancel(order.getOrderId());
                                dismiss();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    }));
        } else {
            Toast.makeText(getActivity(), R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();

        }

    }

    public void setItemListener(OnClickListener listener) {
        this.listener = listener;
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
            dialog.getWindow()
                    .setLayout((int) (getScreenWidth(getActivity()) * .8), (int) (getScreenWidth(getActivity()) * .6));
        }
    }

    public int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    private void setStatus() {
        if (order.getOrderStatus() == 0) {
            statusButton.setVisibility(View.GONE);
            printButton.setVisibility(View.GONE);
            cross.setVisibility(View.GONE);//GONE
//            statusText.setText("NEW");
        } else if (order.getOrderStatus() == 1) {
            statusButton.setVisibility(View.VISIBLE);
            printButton.setVisibility(View.VISIBLE);
            cross.setVisibility(View.VISIBLE);
            statusText.setText("COOKING");
            orderStatus = "COOKING";
        } else if (order.getOrderStatus() == 2) {
            statusButton.setVisibility(View.VISIBLE);
            printButton.setVisibility(View.VISIBLE);
            cross.setVisibility(View.VISIBLE);
            statusText.setText("READY");
            orderStatus = "READY";
        } else if (order.getOrderStatus() == 3) {
            statusButton.setVisibility(View.VISIBLE);
            printButton.setVisibility(View.VISIBLE);
            statusText.setText("CANCELLED");
            orderStatus = "CANCELLED";
            cross.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void itemClick(OrderedItem orderedItem) {

        listener.itemClick(orderedItem);
    }

    public interface OnClickListener {

        void itemClick(OrderedItem orderedItem);

        void onOrderAccepted(String orderId);

        void onOrderForward(String orderId);

        void onOrderCancel(String orderId);
    }

    private void getFoodTruckInfo(final String foodTruckId) {
        if (InternetConnection.checkConnection(getActivity())) {
            //If the internet is working then only make the request
            Observable<GenericResponse<InventoryRestaurent>> results = RetroClient.getApiService().
                    getFoodTruckInfo(foodTruckId);

            new CompositeDisposable().add(results.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<InventoryRestaurent>>() {
                        @Override
                        public void accept(GenericResponse<InventoryRestaurent> response) throws Exception {
                            SessionManager.get(getActivity()).setRestaurantName(response.getData().getRestaurentName());
                            SessionManager.get(getActivity()).setRestaurantLocation(response.getData().getLocation());
                            SessionManager.get(getActivity()).setRestaurantPostalCode(response.getData().getPostalCode());
                            SessionManager.get(getActivity()).setRestaurantPhonenumber("0" + response.getData().getPhoneNumber());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    }));
        } else {
//            Snackbar.make(view, R.string.string_internet_connection_warning, Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    public void cancelOrderConformation() {
        new AlertDialog.Builder(getActivity())
                .setMessage("Are you sure you want to cancel order?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cancelOrder();
                        if (mediaPlayer != null) {
                            //  if (mediaPlayer.isPlaying())
                            mediaPlayer.stop();
                            //  mediaPlayer.reset();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    private void playSoundFile() {

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getActivity(), R.raw.bell_sounds);// the song is a filename which i have pasted inside a folder **raw** created under the **res** folder.//
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }


}