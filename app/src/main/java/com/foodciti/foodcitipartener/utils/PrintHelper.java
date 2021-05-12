package com.foodciti.foodcitipartener.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.db.ExceptionLogger;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.CartItem;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.Driver;
import com.foodciti.foodcitipartener.realm_entities.Exlogger;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.realm_entities.OrderAddon;
import com.foodciti.foodcitipartener.realm_entities.OrderCustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.OrderMenuCategory;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.realm_entities.PurchaseEntry;
import com.foodciti.foodcitipartener.realm_entities.Table;
import com.foodciti.foodcitipartener.realm_entities.Vendor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android_serialport_api.SerialPort;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.Sort;

import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

//import static android.content.Context.MODE_PRIVATE;


public class PrintHelper {
    private static final String TAG = "PrintHelper";
    private Application mApplication;
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private PrintHelperCallback printHelperCallback;
    private SharedPreferences shared;
    private Context context;
    private Realm realm;
    ExceptionLogger logger;
    public PrintHelper(Context context) {
        this.context = context;
        logger=new ExceptionLogger(context);
        if (!(context instanceof PrintHelperCallback))
            throw new RuntimeException("Activity must implement PrintHelperCallback");
        printHelperCallback = (PrintHelperCallback) context;
        shared = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mApplication = (Application) ((Activity) context).getApplication();

        // todo uncomment the following if commented
        setupPrinterSerialPort();
    }

    private void setupPrinterSerialPort() {
        try {
            mSerialPort = mApplication.getPrintSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            /* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            Log.e("SECURITY_EXP", "" + e.toString());
        } catch (IOException e) {
            Log.e("IO_EXP", "" + e.toString());
        } catch (InvalidParameterException e) {
            Log.e("INV_PARA_EXP", "" + e.toString());
        }
    }

    protected class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        printHelperCallback.onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public interface PrintHelperCallback {
        void onDataReceived(byte[] buffer, int size);
    }

    private static byte[] setAlignment(char paramChar) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1B;
        arrayOfByte[1] = 0x61;

        switch (paramChar) {
            case '1':                   // Center
                arrayOfByte[2] = 0x01;
                break;
            case '2':                   // Right
                arrayOfByte[2] = 0x02;
                break;
            default:                    // Left
                arrayOfByte[2] = 0x00;
                break;
        }
        return arrayOfByte;
    }

    // Character size (Width)
    private static byte[] setWH(char paramChar) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1D;
        arrayOfByte[1] = 0x21;

        switch (paramChar) {
            case '1':
                arrayOfByte[2] = 0x0F;

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

    private static byte[] setHT(int...colPositions) {
        int length = colPositions.length;
        byte[] bytes = new byte[3+length];
        bytes[0] = 0x1B;
        bytes[1] = 0x44;
        int j=2;
        for(int i: colPositions) {
            bytes[j]=(byte)i;
            j++;
        }
        bytes[j]=0;

        return bytes;
    }

    private static byte[] resetTabPosition() {
        byte[] bytes=new byte[1];
        bytes[0] = 0x0D;
        return bytes;
    }

    private static byte[] setInternationalCharcters(char paramChar) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1B;
        arrayOfByte[1] = 0x52;
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

    private static byte[] getGbk(String paramString) {
        byte[] arrayOfByte = null;
        try {
            arrayOfByte = paramString.getBytes("GBK");
        } catch (Exception ex) {

        }
        return arrayOfByte;

    }

    private static byte[] setCusorPosition(int position) {
        byte[] returnText = new byte[4];
        returnText[0] = 0x1B;
        returnText[1] = 0x24;
        returnText[2] = (byte) (position % 256);
        returnText[3] = (byte) (position / 256);
        return returnText;
    }

    private static byte[] setBold(boolean paramBoolean) {
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


    private static byte[] CutPaper()   //切纸； GS V 66D 0D
    {
        byte[] arrayOfByte = new byte[]{0x1D, 0x56, 0x42, 0x00};
        return arrayOfByte;
    }

    private static byte[] OpenCash()   //开钱箱； DLE DC4 n m t
    {
        byte[] arrayOfByte = new byte[]{0x1B, 0x70, 0x00, (byte) 0xC0, (byte) 0xC0};
        return arrayOfByte;
    }

    public void openCashDrawer() {
        byte[] arrayOfByte = new byte[]{0x1B, 0x70, 0x00, (byte) 0xC0, (byte) 0xC0};
        try {
            mOutputStream.write(arrayOfByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int commacount(String s) {
        int c = 0;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == ',')
                c++;
        }
        return c;
    }

    public boolean print4(List<CartItem> cartItems, long order_id, double total, double sub_total, double discount, double extra, String payment_type,
                          String orderType, String special_note, long userId, double deliveryCharges, double serviceCharges) {

        final String provider = context.getString(R.string.provider);
        final String vendor = context.getString(R.string.vendor);
        final String tel_no = context.getString(R.string.tel_no);
        final String pin = context.getString(R.string.pin);
        final String loc = context.getString(R.string.location);
      /*  final String email = context.getString(R.string.email);
        final String website = context.getString(R.string.website);
        final String vatNo = context.getString(R.string.vat_no);*/

        realm = Realm.getDefaultInstance();

        final String POUND = "\u00A3";
        byte[] b = POUND.getBytes(Charset.forName("UTF-8"));
        boolean returnValue = true;
        try {
            int jNum = 0;

            byte[] printText22 = new byte[10240];

            byte[] oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setInternationalCharcters('3');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(order_id + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk(provider);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(vendor + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            String location = loc;
            int spacecount = commacount(location);
            oldText = setAlignment('1');
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

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk(pin + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Tel:" + tel_no + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

     /*       oldText = getGbk("VAT No:" + vatNo);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;*/

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            if (special_note != null && !special_note.trim().isEmpty()) {
                oldText = getGbk(special_note);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = getGbk("\n-----------------------\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                /*oldText = setAlignment('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(false);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
*/
            } /*else {
                oldText = getGbk("Thanks");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }*/

           /* oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;*/

            oldText = setAlignment('0');
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

            //    ---------------------------------------- From realm ------------------------------------------

            List<MenuCategory> categories = realm.where(MenuCategory.class).sort("printOrder", Sort.ASCENDING).findAll();
            Log.e(TAG, "--------categories: " + categories);

            for (MenuCategory mc : categories) {
                for (CartItem a : cartItems) {
                    long id = -1;
                    if (a.menuItem.menuCategory != null)
                        id = a.menuItem.menuCategory.id;
                    if ((id == mc.id)) {
                        StringBuilder add_item = new StringBuilder();
                        oldText = setBold(true);
                        System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                        jNum += oldText.length;
                        oldText = setWH('5');
                        System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                        jNum += oldText.length;

                        if (cartItems.size() != 0) {
                            List<Addon> addons = a.addons;
                            for (Addon addon : addons) {
                                String included = (addon.isNoAddon == false) ? "+" : "-";
                                add_item.append("  ").append(included).append(addon.name.trim() + "\n");
                            }
                            if (!a.comment.trim().isEmpty())
                                add_item.append("  *").append(a.comment.trim()).append("\n");
                            oldText = getGbk(" " + a.count + " " + a.menuItem.name);
                            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                            jNum += oldText.length;
                        } else {
                            oldText = getGbk(" " + a.menuItem.name);
                            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                            jNum += oldText.length;
                        }

                        oldText = setCusorPosition(390);
                        System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                        jNum += oldText.length;


                        if (cartItems.size() != 0) {
                            double price = (orderType.equals(Constants.TYPE_COLLECTION)) ? a.menuItem.collectionPrice : a.menuItem.deliveryPrice;
                            oldText = getGbk("        " + String.format("%.2f", (a.count) * price) + "\n");
                            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                            jNum += oldText.length;
                        } else {
                            oldText = getGbk("        " + " " + "\n");
                            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                            jNum += oldText.length;
                        }
                        oldText = setWH('7');
                        System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                        jNum += oldText.length;

                        oldText = getGbk(add_item + "\n");
                        System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                        jNum += oldText.length;
                    }
                }
            }

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('0');
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


            oldText = getGbk("      " + String.format(" %.2f", sub_total) + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (discount > 0) {
                oldText = getGbk("Discount : ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setCusorPosition(390);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = getGbk("       -" + String.format("%.2f", discount) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setAlignment('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }

            if (extra > 0) {
                oldText = getGbk("Extra : ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setCusorPosition(390);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = getGbk("       " + String.format("%.2f", extra) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setAlignment('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }

            if (deliveryCharges > 0) {
                oldText = getGbk("Delivery Charge :");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setCusorPosition(390);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = getGbk("       " + String.format("%.2f", deliveryCharges) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setAlignment('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }

            if (serviceCharges > 0) {
                oldText = getGbk("Service Charge :");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setCusorPosition(390);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = getGbk("       " + String.format("%.2f", serviceCharges) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setAlignment('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Total Price:  ");
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

            oldText = getGbk(" " + String.format("%.2f", total));
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            CustomerInfo customerInfo = realm.where(CustomerInfo.class).equalTo("id", userId).findFirst();
            String customerPhone = "";
            String customerHouseNo = "";
            String CustomerAddress = "";
            String CustomerPostalCode = "";
            String customerName = "";
            StringBuilder addressString = new StringBuilder();
            if (customerInfo != null) {
                customerName = (customerInfo.getName() == null || customerInfo.getName().isEmpty()) ? "" : customerInfo.getName().trim();
                if (!customerName.isEmpty())
                    addressString.append(customerName);
                customerHouseNo = (customerInfo.getHouse_no() == null) ? "" : customerInfo.getHouse_no().trim();

                if (!customerHouseNo.isEmpty()) {
                    if (addressString.length() != 0)
                        addressString.append(",").append(customerHouseNo);
                    else
                        addressString.append(customerHouseNo);
                }
                if (customerInfo.getPostalInfo() != null) {
                    CustomerAddress = (customerInfo.getPostalInfo().getAddress() == null) ? "" : customerInfo.getPostalInfo().getAddress().trim();
                    if (!CustomerAddress.isEmpty()) {
                        if (addressString.length() != 0) {
//                            addressString.append(",").append(CustomerAddress);
                            addressString.append(" ").append(CustomerAddress);
                        } else
                            addressString.append(CustomerAddress);
                    }
                    CustomerPostalCode = (customerInfo.getPostalInfo().getA_PostCode() == null) ? "" : customerInfo.getPostalInfo().getA_PostCode().trim();
                    if (!CustomerPostalCode.isEmpty()) {
                        if (addressString.length() != 0)
                            addressString.append(",").append(CustomerPostalCode);
                        else
                            addressString.append(CustomerAddress);
                    }
                }

                customerPhone = (customerInfo.getPhone() == null) ? "" : customerInfo.getPhone();
                if (!customerPhone.isEmpty()) {
                    if (addressString.length() != 0)
                        addressString.append(",").append(customerPhone);
                    else
                        addressString.append(customerPhone);
                }
            }


            Log.e(TAG, "----------address string: " + addressString);

            oldText = getGbk("ORDER BY " + "" + payment_type + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(orderType + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            String strTmp2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.UK).format(new Date());
            oldText = getGbk(strTmp2);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (!addressString.toString().trim().isEmpty()) {
                oldText = getGbk("\n-----------------------\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                // added later
                oldText = setAlignment('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('4');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

//                oldText = getGbk("Customer Details: " + "\n" + CustomerName + "\n" + CustomerAddress + "\n" + CustomerPostalCode + "\n" + "\n" + customerPhone);
                StringBuilder stringBuilder = new StringBuilder();
                String[] tokens = StringHelper.capitalizeEachWordAfterComma(addressString.toString()).split(",");
                List<String> stringList = Arrays.asList(tokens);
                Iterator<String> stringIterator = stringList.iterator();
                while (stringIterator.hasNext()) {
                    final String str = stringIterator.next();
                    if (stringIterator.hasNext())
                        stringBuilder.append(str.trim() + "\n");
                    else
                        stringBuilder.append(str.trim());
                }
//                oldText = getGbk("Customer Details: " + "\n" + StringHelper.capitalizeEachWordAfterComma(addressString.toString()) + "\n" + CustomerPostalCode + "\n" + "\n" + customerPhone);
                oldText = getGbk("Customer Details: " + "\n" + stringBuilder);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setAlignment('1');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('2');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('2');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setAlignment('1');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(false);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("www.foodciti.co.uk");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            String s = new String(printText22);
//            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            Log.e(TAG, "bill\n" + s);

            oldText = CutPaper();
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            /*oldText = OpenCash();
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;*/

            /*final Dialog dialog=new Dialog(context);
            dialog.setContentView(R.layout.print_preview_layout);
            TextView textView=dialog.findViewById(R.id.preview);
            final String preview=new String(printText22);
            textView.setText(preview);
            dialog.show();*/
            mOutputStream.write(printText22);

        } catch (Exception ex) {
            ex.printStackTrace();
            returnValue = false;
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            realm.close();
        }
        return returnValue;
    }

    public boolean printForTable(List<CartItem> cartItems, long order_id, double total, double sub_total, double discount, double extra, String payment_type,
                                 String orderType, String special_note, long tableId, double serviceCharges) {

        final String provider = context.getString(R.string.provider);
        final String vendor = context.getString(R.string.vendor);
        final String tel_no = context.getString(R.string.tel_no);
        final String pin = context.getString(R.string.pin);
        final String loc = context.getString(R.string.location);
   /*     final String email = context.getString(R.string.email);
        final String website = context.getString(R.string.website);
        final String vatNo = context.getString(R.string.vat_no);*/

        final String POUND = "\u00A3";
        byte[] b = POUND.getBytes(Charset.forName("UTF-8"));
        boolean returnValue = true;

        realm = Realm.getDefaultInstance();

        final Table table = realm.where(Table.class).equalTo("id", tableId).findFirst();
        final CustomerInfo customerInfo = table.getCustomerInfo();
        try {
            int jNum = 0;

            byte[] printText22 = new byte[10240];

            byte[] oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setInternationalCharcters('3');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(table.getName() + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk(provider);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(vendor + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            String location = loc;
            int spacecount = commacount(location);
            oldText = setAlignment('1');
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

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk(pin + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Tel:" + tel_no + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            /*oldText = getGbk("VAT No:" + vatNo);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;*/

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            if (special_note != null && !special_note.trim().isEmpty()) {
                oldText = getGbk(special_note);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = getGbk("\n-----------------------\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                /*oldText = setAlignment('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(false);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;*/

            } /*else {
                oldText = getGbk("Thanks");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }*/

            /*oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;*/

            oldText = setAlignment('0');
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

            //    ---------------------------------------- From realm ------------------------------------------

            List<MenuCategory> categories = realm.where(MenuCategory.class).sort("printOrder", Sort.ASCENDING).findAll();
            Log.e(TAG, "--------categories: " + categories);

            for (MenuCategory mc : categories) {
                for (CartItem a : cartItems) {
                    long id = -1;
                    if (a.menuItem.menuCategory != null)
                        id = a.menuItem.menuCategory.id;
                    if ((id == mc.id)) {
                        StringBuilder add_item = new StringBuilder();
                        oldText = setBold(true);
                        System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                        jNum += oldText.length;
                        oldText = setWH('5');
                        System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                        jNum += oldText.length;

                        if (cartItems.size() != 0) {
                            List<Addon> addons = a.addons;
                            for (Addon addon : addons) {
                                String included = (addon.isNoAddon == false) ? "+" : "-";
                                add_item.append("  ").append(included).append(addon.name.trim() + "\n");
                            }
                            if (!a.comment.trim().isEmpty())
                                add_item.append("  *").append(a.comment.trim()).append("\n");
                            oldText = getGbk(" " + a.count + " " + a.menuItem.name);
                            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                            jNum += oldText.length;
                        } else {
                            oldText = getGbk(" " + a.menuItem.name);
                            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                            jNum += oldText.length;
                        }

                        oldText = setCusorPosition(390);
                        System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                        jNum += oldText.length;


                        if (cartItems.size() != 0) {
                            double price = a.menuItem.collectionPrice;
                            oldText = getGbk("        " + String.format("%.2f", (a.count) * price) + "\n");
                            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                            jNum += oldText.length;
                        } else {
                            oldText = getGbk("        " + " " + "\n");
                            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                            jNum += oldText.length;
                        }
                        oldText = setWH('7');
                        System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                        jNum += oldText.length;

                        oldText = getGbk(add_item + "\n");
                        System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                        jNum += oldText.length;
                    }
                }
            }

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('0');
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


            oldText = getGbk("      " + String.format(" %.2f", sub_total) + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            if (discount > 0) {
                oldText = getGbk("Discount : ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setCusorPosition(390);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = getGbk("       -" + String.format("%.2f", discount) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                oldText = setAlignment('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }

            if (extra > 0) {
                oldText = getGbk("Extra : ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setCusorPosition(390);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = getGbk("       " + String.format("%.2f", extra) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                oldText = setAlignment('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }

            if (serviceCharges > 0) {
                oldText = getGbk("Service Charge : ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setCusorPosition(390);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = getGbk("       " + String.format(" %.2f", serviceCharges));
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setWH('2');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setAlignment('1');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setBold(false);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Total Price:  ");
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
            oldText = getGbk(" " + String.format("%.2f", total));
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            String customerPhone = "";
            String customerHouseNo = "";
            String CustomerAddress = "";
            String CustomerPostalCode = "";
            String customerName = "";
            StringBuilder addressString = new StringBuilder();

            if (customerInfo != null) {
                customerName = (customerInfo.getName() == null || customerInfo.getName().isEmpty()) ? "" : customerInfo.getName().trim();
                if (!customerName.isEmpty())
                    addressString.append(customerName);
                customerHouseNo = (customerInfo.getHouse_no() == null) ? "" : customerInfo.getHouse_no().trim();

                if (!customerHouseNo.isEmpty()) {
                    if (addressString.length() != 0)
                        addressString.append(",").append(customerHouseNo);
                    else
                        addressString.append(customerHouseNo);
                }
                if (customerInfo.getPostalInfo() != null) {
                    CustomerAddress = (customerInfo.getPostalInfo().getAddress() == null) ? "" : customerInfo.getPostalInfo().getAddress().trim();
                    if (!CustomerAddress.isEmpty()) {
                        if (addressString.length() != 0) {
//                            addressString.append(",").append(CustomerAddress);
                            addressString.append(" ").append(CustomerAddress);
                        } else
                            addressString.append(CustomerAddress);
                    }
                    CustomerPostalCode = (customerInfo.getPostalInfo().getA_PostCode() == null) ? "" : customerInfo.getPostalInfo().getA_PostCode().trim();
                    if (!CustomerPostalCode.isEmpty()) {
                        if (addressString.length() != 0)
                            addressString.append(",").append(CustomerPostalCode);
                        else
                            addressString.append(CustomerAddress);
                    }
                }

                customerPhone = (customerInfo.getPhone() == null) ? "" : customerInfo.getPhone();
                if (!customerPhone.isEmpty()) {
                    if (addressString.length() != 0)
                        addressString.append(",").append(customerPhone);
                    else
                        addressString.append(customerPhone);
                }
            }


            Log.e(TAG, "----------address string: " + addressString);

            oldText = getGbk("ORDER BY " + "" + payment_type + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            String strTmp2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.UK).format(new Date());
            oldText = getGbk(strTmp2);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (!addressString.toString().trim().isEmpty()) {
                oldText = getGbk("\n-----------------------\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                // added later
                oldText = setAlignment('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('4');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                StringBuilder stringBuilder = new StringBuilder();
                String[] tokens = StringHelper.capitalizeEachWordAfterComma(addressString.toString()).split(",");
                List<String> stringList = Arrays.asList(tokens);
                Iterator<String> stringIterator = stringList.iterator();
                while (stringIterator.hasNext()) {
                    final String str = stringIterator.next();
                    if (stringIterator.hasNext())
                        stringBuilder.append(str.trim() + "\n");
                    else
                        stringBuilder.append(str.trim());
                }

//                oldText = getGbk("Customer Details: " + "\n" + StringHelper.capitalizeEachWordAfterComma(addressString.toString()) + "\n" + CustomerPostalCode + "\n" + "\n" + customerPhone);
                oldText = getGbk("Customer Details: " + stringBuilder);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setAlignment('1');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('2');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('2');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setAlignment('1');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(false);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("www.foodciti.co.uk");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            String s = new String(printText22);
//            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            Log.e(TAG, "bill\n" + s);

            oldText = CutPaper();
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            /*oldText = OpenCash();
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;*/

            /*final Dialog dialog=new Dialog(context);
            dialog.setContentView(R.layout.print_preview_layout);
            TextView textView=dialog.findViewById(R.id.preview);
            final String preview=new String(printText22);
            textView.setText(preview);
            dialog.show();*/
            mOutputStream.write(printText22);

        } catch (Exception ex) {
            ex.printStackTrace();
            returnValue = false;
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            realm.close();
        }
        return returnValue;
    }

    public boolean printOrders(List<PurchaseEntry> orderTuples, long order_id, double total, double sub_total, double discount, double extra, String payment_type,
                               String orderType, String special_note, long userId, double deliveryCharges, double serviceCharges) {

        final String provider = context.getString(R.string.provider);
        final String vendor = context.getString(R.string.vendor);
        final String loc = context.getString(R.string.location);
        final String pin = context.getString(R.string.pin);
        final String tel_no = context.getString(R.string.tel_no);
/*        final String email = context.getString(R.string.email);
        final String website = context.getString(R.string.website);
        final String vatNo = context.getString(R.string.vat_no);*/

        realm = Realm.getDefaultInstance();
        final String POUND = "\u00A3";
        byte[] b = POUND.getBytes(Charset.forName("UTF-8"));
        boolean returnValue = true;
        try {
            int jNum = 0;

            byte[] printText22 = new byte[10240];

            byte[] oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setInternationalCharcters('3');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(order_id + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk(provider);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(vendor + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            String location = loc;
            int spacecount = commacount(location);
            oldText = setAlignment('1');
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

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk(pin + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Tel:" + tel_no + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

     /*       oldText = getGbk("VAT No:" + vatNo);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;*/

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            if (special_note != null && !special_note.trim().isEmpty()) {
                oldText = getGbk(special_note);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = getGbk("\n-----------------------\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                /*oldText = setAlignment('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(false);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;*/

            } /*else {
                oldText = getGbk("Thanks");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }*/

           /* oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;*/

            oldText = setAlignment('0');
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

            //    ---------------------------------------- From realm ------------------------------------------

            List<MenuCategory> categories = realm.where(MenuCategory.class).sort("printOrder", Sort.ASCENDING).findAll();
            Log.e(TAG, "--------categories: " + categories);

            for (MenuCategory mc : categories) {
                for (PurchaseEntry a : orderTuples) {
                    long id = -1;
                    if (a.getOrderMenuItem().orderMenuCategory != null) {
                        OrderMenuCategory orderMenuCategory = a.getOrderMenuItem().orderMenuCategory.first();
                        id = orderMenuCategory.id;
                    }
                    if ((id == mc.id)) {
                        StringBuilder add_item = new StringBuilder();
                        oldText = setBold(true);
                        System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                        jNum += oldText.length;
                        oldText = setWH('5');
                        System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                        jNum += oldText.length;

                        if (orderTuples.size() != 0) {
                            List<OrderAddon> addons = a.getOrderAddons();
                            for (OrderAddon addon : addons) {
                                String included = (addon.isNoAddon == false) ? "+" : "-";
                                add_item.append("  ").append(included).append(addon.name.trim() + "\n");
                            }
                            if (!a.getAdditionalNote().trim().isEmpty())
                                add_item.append("  *").append(a.getAdditionalNote().trim()).append("\n");
                            oldText = getGbk(" " + a.getCount() + " " + a.getOrderMenuItem().name);
                            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                            jNum += oldText.length;
                        } else {
                            oldText = getGbk(" \n" + a.getOrderMenuItem().name);
                            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                            jNum += oldText.length;
                        }

                        oldText = setCusorPosition(390);
                        System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                        jNum += oldText.length;


                        if (orderTuples.size() != 0) {
                            double price = (orderType.equals(Constants.TYPE_COLLECTION)) ? a.getOrderMenuItem().collectionPrice : a.getOrderMenuItem().deliveryPrice;
                            oldText = getGbk("        " + String.format("%.2f", (a.getCount()) * price) + "\n");
                            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                            jNum += oldText.length;
                        } else {
                            oldText = getGbk("        " + " " + "\n");
                            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                            jNum += oldText.length;
                        }
                        oldText = setWH('7');
                        System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                        jNum += oldText.length;

                        oldText = getGbk(add_item + "\n");
                        System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                        jNum += oldText.length;
                    }
                }
            }

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('0');
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


            oldText = getGbk("      " + String.format(" %.2f", sub_total) + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (discount > 0) {
                oldText = getGbk("Discount : ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setCusorPosition(390);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = getGbk("       -" + String.format("%.2f", discount) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setAlignment('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }

            if (extra > 0) {
                oldText = getGbk("Extra : ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setCusorPosition(390);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = getGbk("       " + String.format("%.2f", extra) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setAlignment('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }

            if (deliveryCharges > 0) {
                oldText = getGbk("Delivery Charge : ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setCusorPosition(390);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = getGbk("       " + String.format(" %.2f", deliveryCharges));
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('2');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setAlignment('1');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(false);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }

            if (serviceCharges > 0) {
                oldText = getGbk("Service Charge : ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setCusorPosition(390);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                oldText = getGbk("       " + String.format(" %.2f", serviceCharges));
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('2');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setAlignment('1');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(false);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Total Price:  ");
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

            oldText = getGbk(" " + String.format("%.2f", total));
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            OrderCustomerInfo customerInfo = realm.where(OrderCustomerInfo.class).equalTo("id", userId).findFirst();
            String customerPhone = "";
            String customerHouseNo = "";
            String CustomerAddress = "";
            String CustomerPostalCode = "";
            String customerName = "";
            StringBuilder addressString = new StringBuilder();

            if (customerInfo != null) {
                customerName = (customerInfo.getName() == null || customerInfo.getName().isEmpty()) ? "" : customerInfo.getName().trim();
                if (!customerName.isEmpty())
                    addressString.append(customerName);
                customerHouseNo = (customerInfo.getHouse_no() == null) ? "" : customerInfo.getHouse_no().trim();

                if (!customerHouseNo.isEmpty()) {
                    if (addressString.length() != 0)
                        addressString.append(",").append(customerHouseNo);
                    else
                        addressString.append(customerHouseNo);
                }
                if (customerInfo.getOrderPostalInfo() != null) {
                    CustomerAddress = (customerInfo.getOrderPostalInfo().getAddress() == null) ? "" : customerInfo.getOrderPostalInfo().getAddress().trim();
                    if (!CustomerAddress.isEmpty()) {
                        if (addressString.length() != 0) {
//                            addressString.append(",").append(CustomerAddress);
                            addressString.append(" ").append(CustomerAddress);
                        } else
                            addressString.append(CustomerAddress);
                    }
                    CustomerPostalCode = (customerInfo.getOrderPostalInfo().getA_PostCode() == null) ? "" : customerInfo.getOrderPostalInfo().getA_PostCode().trim();
                    if (!CustomerPostalCode.isEmpty()) {
                        if (addressString.length() != 0)
                            addressString.append(",").append(CustomerPostalCode);
                        else
                            addressString.append(CustomerAddress);
                    }
                }

                customerPhone = (customerInfo.getPhone() == null) ? "" : customerInfo.getPhone();
                if (!customerPhone.isEmpty()) {
                    if (addressString.length() != 0)
                        addressString.append(",").append(customerPhone);
                    else
                        addressString.append(customerPhone);
                }
            }

            Log.e(TAG, "----------address string: " + addressString);

            oldText = getGbk("ORDER BY " + "" + payment_type + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(orderType + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            String strTmp2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.UK).format(new Date());
            oldText = getGbk(strTmp2);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (!addressString.toString().trim().isEmpty()) {

                oldText = getGbk("\n-----------------------\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                // added later
                oldText = setAlignment('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('4');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                StringBuilder stringBuilder = new StringBuilder();
                String[] tokens = StringHelper.capitalizeEachWordAfterComma(addressString.toString()).split(",");
                List<String> stringList = Arrays.asList(tokens);
                Iterator<String> stringIterator = stringList.iterator();
                while (stringIterator.hasNext()) {
                    final String str = stringIterator.next();
                    if (stringIterator.hasNext())
                        stringBuilder.append(str.trim() + "\n");
                    else
                        stringBuilder.append(str.trim());
                }

//                oldText = getGbk("Customer Details: " + "\n" + StringHelper.capitalizeEachWordAfterComma(addressString.toString()) + "\n" + CustomerPostalCode + "\n" + "\n" + customerPhone);
                oldText = getGbk("Customer Details: " + stringBuilder);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setAlignment('1');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('2');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('2');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setAlignment('1');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(false);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("www.foodciti.co.uk");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            String s = new String(printText22);
//            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            Log.e(TAG, "bill\n" + s);

            oldText = CutPaper();
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            /*oldText = OpenCash();
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;*/

            /*final Dialog dialog=new Dialog(context);
            dialog.setContentView(R.layout.print_preview_layout);
            TextView textView=dialog.findViewById(R.id.preview);
            final String preview=new String(printText22);
            textView.setText(preview);
            dialog.show();*/
            mOutputStream.write(printText22);

        } catch (Exception ex) {
            ex.printStackTrace();
            returnValue = false;
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            realm.close();
        }
        return returnValue;
    }

    public boolean printDriverReport(long driverId, Date fromDate, Date toDate) {
        /*final String provider = context.getString(R.string.provider);
        final String vendor = context.getString(R.string.vendor);
        final String loc = context.getString(R.string.location);
        final String pin = context.getString(R.string.pin);
        final String tel_no = context.getString(R.string.tel_no);*/
     /*   final String email = context.getString(R.string.email);
        final String website = context.getString(R.string.website);
        final String vatNo = context.getString(R.string.vat_no);*/
        realm = Realm.getDefaultInstance();
        Vendor _vendor = realm.where(Vendor.class).findFirst();
        final String provider = _vendor.getTitle();
        final String vendor = _vendor.getName();
        final String loc = _vendor.getAddress();
        final String pin = _vendor.getPin();
        final String tel_no = _vendor.getTel_no();
        final String POUND = "\u00A3";
        byte[] b = POUND.getBytes(Charset.forName("UTF-8"));
        boolean returnValue = true;

//        Order order=realm.where(Order.class).equalTo("driver.id", driverId).findFirst();

        try {
            int jNum = 0;

            byte[] printText22 = new byte[10240];

            byte[] oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setInternationalCharcters('3');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(driverId + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk(provider);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(vendor + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            String location = loc;
            int spacecount = commacount(location);
            oldText = setAlignment('1');
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

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk(pin + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Tel:" + tel_no + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


        /*    oldText = getGbk("VAT No:" + vatNo);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;*/

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(300);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = getGbk("         " + "Orders" + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setCusorPosition(390);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = getGbk("         " + "GBP" + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            //    ---------------------------------------- From realm ------------------------------------------

            RealmQuery<Purchase> baseQuery = realm.where(Purchase.class).equalTo("driver.id", driverId).and().between("timestamp", fromDate.getTime(), toDate.getTime());
            RealmQuery<Purchase> orderRealmQueryForCollection = realm.where(Purchase.class).equalTo("driver.id", driverId).and().equalTo("orderType", Constants.TYPE_COLLECTION).and().between("timestamp", fromDate.getTime(), toDate.getTime());
            RealmQuery<Purchase> orderRealmQueryForDelivery = realm.where(Purchase.class).equalTo("driver.id", driverId).and().equalTo("orderType", Constants.TYPE_DELIVERY).and().between("timestamp", fromDate.getTime(), toDate.getTime());
            RealmQuery<Purchase> orderRealmQueryForCard = realm.where(Purchase.class).equalTo("driver.id", driverId).and().equalTo("paymentMode", Constants.PAYMENT_TYPE_CARD).and().between("timestamp", fromDate.getTime(), toDate.getTime());
            RealmQuery<Purchase> orderRealmQueryForCash = realm.where(Purchase.class).equalTo("driver.id", driverId).and().equalTo("paymentMode", Constants.PAYMENT_TYPE_CASH).and().between("timestamp", fromDate.getTime(), toDate.getTime());

            final long totalCollection = orderRealmQueryForCollection.count();
            final long totalDelivery = orderRealmQueryForDelivery.count();
            final long totalCash = orderRealmQueryForCash.count();
            final long totalCard = orderRealmQueryForCard.count();

            final double totalAmt = baseQuery.sum("total").doubleValue();
            final double collectionAmt = orderRealmQueryForCollection.sum("total").doubleValue();
            final double deliveryAmt = orderRealmQueryForDelivery.sum("total").doubleValue();
            final double cardTotal = orderRealmQueryForCard.sum("total").doubleValue();
            final double cashTotal = orderRealmQueryForCash.sum("total").doubleValue();

            Log.e(TAG, "----------result count: " + baseQuery.count());

            oldText = getGbk("Collection");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(300);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(totalCollection + "");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(390);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(String.format("%.2f", collectionAmt) + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("Delivery");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(300);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(totalDelivery + "");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(390);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(String.format("%.2f", deliveryAmt) + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Card");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setCusorPosition(300);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(totalCard + "");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(390);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(String.format("%.2f", cardTotal) + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Cash");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(300);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = getGbk(totalCash + "");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(390);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(String.format("%.2f", cashTotal) + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Total:  ");
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

            oldText = getGbk(" " + String.format("%.2f", totalAmt));
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            String strTmp2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.UK).format(new Date());
            oldText = getGbk(strTmp2);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            // added later
            oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            StringBuilder stringBuilder = new StringBuilder();
            Driver driver = realm.where(Driver.class).equalTo("id", driverId).findFirst();
            stringBuilder.append("Driver Name: " + driver.getDriver_name() + "\n");
            stringBuilder.append("Vehicle No: " + driver.getDriver_vehicle_no());
            oldText = getGbk(stringBuilder.toString());
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("www.foodciti.co.uk");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            String s = new String(printText22);
//            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            Log.e(TAG, "bill\n" + s);

            oldText = CutPaper();
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            mOutputStream.write(printText22);

        } catch (Exception ex) {
            ex.printStackTrace();
            returnValue = false;
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            realm.close();
        }
        return returnValue;
    }

    public boolean printSalesReport(Date fromDate, Date toDate) {
        final String provider = context.getString(R.string.provider);
        final String vendor = context.getString(R.string.vendor);
        final String loc = context.getString(R.string.location);
        final String pin = context.getString(R.string.pin);
        final String tel_no = context.getString(R.string.tel_no);
 /*       final String email = context.getString(R.string.email);
        final String website = context.getString(R.string.website);
        final String vatNo = context.getString(R.string.vat_no);*/

        realm = Realm.getDefaultInstance();

        final String POUND = "\u00A3";
        byte[] b = POUND.getBytes(Charset.forName("UTF-8"));
        boolean returnValue = true;

//        Order order=realm.where(Order.class).equalTo("driver.id", driverId).findFirst();


        final String myFormat = "MM/dd/yy"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        Date startDateMin = calendar.getTime();

        calendar.setTime(toDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endDateMax = calendar.getTime();


        try {
            int jNum = 0;

            byte[] printText22 = new byte[10240];

            byte[] oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setInternationalCharcters('3');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(sdf.format(startDateMin) + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk(provider);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(vendor + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            String location = loc;
            int spacecount = commacount(location);
            oldText = setAlignment('1');
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

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk(pin + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Tel:" + tel_no + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


     /*       oldText = getGbk("VAT No:" + vatNo);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;*/

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(300);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = getGbk("         " + "Orders");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setCusorPosition(390);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = getGbk("         " + "GBP" + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            //    ---------------------------------------- From realm ------------------------------------------

            RealmQuery<Purchase> realmQuery = realm.where(Purchase.class);
            RealmQuery<Purchase> realmQueryForCollection = realm.where(Purchase.class).equalTo("orderType", Constants.TYPE_COLLECTION);
            RealmQuery<Purchase> realmQueryForDelivery = realm.where(Purchase.class).equalTo("orderType", Constants.TYPE_DELIVERY);
            RealmQuery<Purchase> realmQueryForTable = realm.where(Purchase.class).equalTo("orderType", Constants.TYPE_TABLE);
            RealmQuery<Purchase> realmQueryForCard = realm.where(Purchase.class).equalTo("paymentMode", Constants.PAYMENT_TYPE_CARD);
            RealmQuery<Purchase> realmQueryForCash = realm.where(Purchase.class).equalTo("paymentMode", Constants.PAYMENT_TYPE_CASH);

            realmQuery.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForCollection.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForDelivery.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForTable.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForCard.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForCash.between("timestamp", startDateMin.getTime(), endDateMax.getTime());

            double totalAmount = realmQuery.sum("total").doubleValue();
            String formattedTotal = String.format("%.2f", totalAmount);
            double discountAmount = realmQuery.sum("discount").doubleValue();
            final String formattedDiscount = String.format("%.2f", discountAmount);
            double deliveryCharges = realmQuery.sum("deliveryCharges").doubleValue();
            final String formattedDeliveryCharges = String.format("%.2f", deliveryCharges);
            double serviceCharges = realmQuery.sum("serviceCharges").doubleValue();
            final String formattedServiceCharges = String.format("%.2f", serviceCharges);
            long totalCollectionOrders = realmQueryForCollection.count();
            long totalDeliveryOrders = realmQueryForDelivery.count();
            long totalTableOrders = realmQueryForTable.count();


            oldText = getGbk("Collection");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(300);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(realmQueryForCollection.count() + "");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(390);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(String.format("%.2f", realmQueryForCollection.sum("total")) + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("Delivery");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(300);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(realmQueryForDelivery.count() + "");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(390);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(String.format("%.2f", realmQueryForDelivery.sum("total")) + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Table");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(300);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(realmQueryForTable.count() + "");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(390);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(String.format("%.2f", realmQueryForTable.sum("total")) + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Card");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setCusorPosition(300);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(realmQueryForCard.count() + "");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(390);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(String.format("%.2f", realmQueryForCard.sum("total")) + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("Cash");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(300);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = getGbk(realmQueryForCash.count() + "");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(390);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(String.format("%.2f", realmQueryForCash.sum("total")) + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Total:  ");
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

            oldText = getGbk(" " + String.format("%.2f", realmQuery.sum("total")));
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            String strTmp2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.UK).format(new Date());
            oldText = getGbk(strTmp2);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            // added later
            oldText = setAlignment('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            /*StringBuilder stringBuilder=new StringBuilder();
            Driver driver=realm.where(Driver.class).equalTo("id", driverId).findFirst();
            stringBuilder.append("Driver Name: "+driver.getDriver_name()+"\n");
            stringBuilder.append("Vehicle No: "+driver.getDriver_vehicle_no());
            oldText = getGbk(stringBuilder.toString());
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignment('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;
*/

            oldText = getGbk("www.foodciti.co.uk");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            String s = new String(printText22);
//            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            Log.e(TAG, "bill\n" + s);

            oldText = CutPaper();
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            mOutputStream.write(printText22);

        } catch (Exception ex) {
            ex.printStackTrace();
            returnValue = false;
            Exlogger exlogger = new Exlogger();
            exlogger.setErrorType("Print Error");
            exlogger.setErrorMessage(ex.getMessage());
            exlogger.setScreenName("PrintHelper->>print4 function");
            logger.addException(exlogger);
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            realm.close();
        }
        return returnValue;
    }
}
