package com.foodciti.foodcitipartener.utils;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.foodciti.foodcitipartener.gson.PostalData;
import com.foodciti.foodcitipartener.gson.UserData;
import com.foodciti.foodcitipartener.migration.DataMigration;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.Order;
import com.foodciti.foodcitipartener.realm_entities.OrderTuple;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.realm_entities.PurchaseEntry;
import com.foodciti.foodcitipartener.realm_entities.RemarkType;
import com.foodciti.foodcitipartener.realm_entities.Table;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.internal.IOException;

public class RealmBackupHelper {
    private static final String TAG = "RealmBackupHelper";
    //    private File EXPORT_REALM_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private String EXPORT_REALM_PATH;


    //    private String EXPORT_REALM_FILE_NAME = "glucosio.realm";
    private String EXPORT_REALM_FILE_NAME = "backup.realm";
    private String IMPORT_REALM_FILE_NAME = "efeskebab.realm";
// Eventually replace this if you're using a custom db name

    private Activity activity;

    public RealmBackupHelper(Activity activity, String exportFileName) throws java.io.IOException {
        this.activity = activity;
        if (!exportFileName.trim().endsWith(".realm"))
            exportFileName = exportFileName + ".realm";
        EXPORT_REALM_FILE_NAME = exportFileName;


        String parentfolderName = "foodciti";
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + parentfolderName);
        /*if (!folder.exists())
            folder.mkdirs();*/

        EXPORT_REALM_PATH = folder.getPath();
/*
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + parentfolderName  + File.separator + exportFileName);
        if (!file.exists()) {
            file.createNewFile();
        }*/

    }

    public RealmBackupHelper(Activity activity) throws java.io.IOException {
        this.activity = activity;

        String parentfolderName = "foodciti";
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + parentfolderName);
        if (!folder.exists())
            folder.mkdirs();

        EXPORT_REALM_PATH = folder.getPath();

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + parentfolderName + File.separator + EXPORT_REALM_FILE_NAME);
        if (!file.exists()) {
            file.createNewFile();
        }

    }

    public boolean backup() {
        boolean success=false;
        Realm realm = Realm.getDefaultInstance();
        try {
            // create a backup file
            File exportRealmFile;
            exportRealmFile = new File(EXPORT_REALM_PATH, EXPORT_REALM_FILE_NAME);

            // if backup file already exists, delete it
            exportRealmFile.delete();

            // copy current realm to backup file
            realm.writeCopyTo(exportRealmFile);
            success = true;

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            realm.close();
        }
        
        return success;
    }

    public void restore() {
        String restoreFilePath = EXPORT_REALM_PATH + File.separator + EXPORT_REALM_FILE_NAME;

        RealmManager.closeLocalInstance();
        Log.e(TAG, "-----------------------------realm instance count: " + Realm.getLocalInstanceCount(Realm.getDefaultConfiguration()));
        copyBundledRealmFile(restoreFilePath, IMPORT_REALM_FILE_NAME);
//        restoreAlt();
//        copyNIO(restoreFilePath, IMPORT_REALM_FILE_NAME);

        long schemaVersion = Realm.getDefaultConfiguration().getSchemaVersion();
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("efeskebab.realm")
                .schemaVersion(schemaVersion)
//                .deleteRealmIfMigrationNeeded()
                .migration(new DataMigration())
                .build();
        Realm.setDefaultConfiguration(config);
        Log.d(TAG, "Data restore is done"+schemaVersion);
        try {
            migrateOldOrderHistory();
            /*new Thread(()->{
                Realm realm = RealmManager.getLocalInstance();
                deletePostcodes(realm);
                deleteUsers(realm);
                RealmManager.closeLocalInstance();
            }).start();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deletePostcodes(Realm realm) {
        realm.executeTransaction(r->{
            RealmResults<PostalInfo> pi = r.where(PostalInfo.class).findAll();
            pi.deleteAllFromRealm();
        });
    }
    private void deleteUsers(Realm realm) {
        realm.executeTransaction(r->{
            RealmResults<CustomerInfo> customerInfos = r.where(CustomerInfo.class).findAll();
            customerInfos.deleteAllFromRealm();
        });
    }

 /*   public void restore(File file) {

        copyBundledRealmFile(file, IMPORT_REALM_FILE_NAME);
        Log.d(TAG, "Data restore is done");
    }

    private String copyBundledRealmFile(File oldFilePath, String outFileName) {
        try {
            File file = new File(activity.getApplicationContext().getFilesDir(), outFileName);

            FileOutputStream outputStream = new FileOutputStream(file);

            FileInputStream inputStream = new FileInputStream(oldFilePath);

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    private void copyBundledRealmFile(String oldFilePath, String outFileName) {
        try {
//            if(!realm.isClosed())
//            realm.close();

            File file = new File(activity.getFilesDir(), outFileName);
            Log.e(TAG, "----------file length: " + file.length());
            Log.e(TAG, "----------file name: " + file.getName());


            FileOutputStream outputStream = new FileOutputStream(file);

            BufferedOutputStream bos = new BufferedOutputStream(outputStream);

            File backupFile = new File(oldFilePath);
            if (backupFile.length() <= 0) {
//                Toast.makeText(activity, "Backup File is either empty or not present", Toast.LENGTH_SHORT).show();
                bos.close();
                return;
            }
            FileInputStream inputStream = new FileInputStream(backupFile);
            BufferedInputStream bis = new BufferedInputStream(inputStream);

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buf)) > 0) {
                bos.write(buf, 0, bytesRead);
            }
            bos.flush();
            bos.close();
            bis.close();
//            return file.getAbsolutePath();
            Toast.makeText(activity, "Data Restored", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
//        return null;
    }

    private void copyNIO(String oldFile, String outFile) {
        int bufferSizeKB = 4;
        int bufferSize = bufferSizeKB * 1024;
        try (FileChannel in = new FileInputStream(oldFile).getChannel();
             FileChannel out = new FileOutputStream(outFile).getChannel()) {
            // Allocate an indirect ByteBuffer
            ByteBuffer bytebuf = ByteBuffer.allocate(bufferSize);

//            startTime = System.nanoTime();

            int bytesCount = 0;
            // Read data from file into ByteBuffer
            while ((bytesCount = in.read(bytebuf)) > 0 || bytebuf.position() > 0) {
                // flip the buffer which set the limit to current position, and position to 0.
                bytebuf.flip();
                out.write(bytebuf); // Write data from ByteBuffer to file
                bytebuf.compact(); // For the next read
            }

            /*elapsedTime = System.nanoTime() - startTime;
            System.out.println("Elapsed Time is " + (elapsedTime / 1000000.0) + " msec");*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void restoreAlt() {
       /* RealmConfiguration defaultConfig = new RealmConfiguration.Builder()
                .name(IMPORT_REALM_FILE_NAME)
                *//* .schemaVersion(1)
                 .deleteRealmIfMigrationNeeded()*//*
                .build();*/

        Log.e(TAG, "-----------------------------realm instance count: " + Realm.getLocalInstanceCount(Realm.getDefaultConfiguration()));
        Realm.deleteRealm(Realm.getDefaultConfiguration());


        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(EXPORT_REALM_FILE_NAME).directory(new File(EXPORT_REALM_PATH))
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm backupRealm = Realm.getInstance(config);
        backupRealm.writeCopyTo(activity.getFilesDir());
        backupRealm.close();
    }

    private void migrateOldOrderHistory() throws Exception {
/*        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "foodciti");
        RealmConfiguration config = new RealmConfiguration.Builder()
//                .name("backup.realm").directory(folder)
                .name("efeskebab.realm")
//                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();*/
        Realm realm = Realm.getDefaultInstance();
        OrderHistoryUtils.setRealm(realm);

        final Object[] success = new Object[2];
        try {
            realm.executeTransaction(r->{
                try {
                    RealmQuery<Order> orderRealmQuery = r.where(Order.class);
                    RealmQuery<Purchase> purchaseRealmQuery = r.where(Purchase.class);
                    if(purchaseRealmQuery.count()>0) {
                        success[0]=true;
                        return;
                    }
                    RealmResults<Order> orders = orderRealmQuery.findAll();
                    for(Order order: orders) {
                        Number maxId = r.where(Purchase.class).max("id");
                        long nextId = (maxId==null)? 1: maxId.longValue()+1;
                        Purchase purchase = r.createObject(Purchase.class, nextId);
                        purchase.setOrderCustomerInfo(OrderHistoryUtils.toOrderCustomerInfo(order.getCustomerInfo()));
                        purchase.setTotal(order.getTotal());
                        purchase.setSubTotal(order.getSubTotal());
                        purchase.setDiscount(order.getDiscount());
                        purchase.setExtra(order.getExtra());
                        purchase.setDeliveryCharges(order.getDeliveryCharges());
                        purchase.setServiceCharges(order.getServiceCharges());
                        purchase.setTimestamp(order.getTimestamp());
                        purchase.setDeliveryTime(order.getDeliveryTime());
                        purchase.setPaymentMode(order.getPaymentMode());
                        purchase.setOrderType(order.getOrderType());
                        purchase.setDriver(order.getDriver());
                        purchase.setPaid(order.isDelivered());
                        Table table = order.getTable();
                        if(table!=null) {
                            purchase.setTableId(table.getId());
                            purchase.setTableName(table.getName());
                        }

                        for(OrderTuple orderTuple: order.getOrderTuples()) {
                            Number maxPurchaseEntry = r.where(PurchaseEntry.class).max("id");
                            long nextPurchaseEntryId = (maxPurchaseEntry==null)? 1: maxPurchaseEntry.longValue()+1;
                            PurchaseEntry purchaseEntry = r.createObject(PurchaseEntry.class, nextPurchaseEntryId);
                            purchaseEntry.setOrderMenuItem(OrderHistoryUtils.toOrderMenuItem(orderTuple.getMenuItem()));
                            purchaseEntry.setAdditionalNote(orderTuple.getAdditionalNote());
                            purchaseEntry.setPrice(orderTuple.getPrice());
                            purchaseEntry.setCount(orderTuple.getCount());
                            purchaseEntry.setCustomerOrderId(orderTuple.getCustomerOrderId());
                            purchaseEntry.getOrderAddons().addAll(OrderHistoryUtils.toOrderAddons(orderTuple.getAddons()));
                            purchase.getPurchaseEntries().add(purchaseEntry);
                        }
                    }
//                    success[0] = true;
                    success[0] = true;
//                    atomicReference.get()[0] = new Boolean(true);
                    success[1] = null;
                } catch (Exception e) {
                    e.printStackTrace();
//                    success[0] = false;
                    success[0] = false;
//                    atomicReference.get()[0] = new Boolean(false);
                    success[1] = e;
                } finally {
//                    atomicReference.set(success);
                }
            });

            Log.d(TAG, "-------------atomic ref: "+success[0]);
            boolean result = (boolean)success[0];
            if(!result) {
                Throwable throwable = (Throwable)success[1];
                throw new Exception(throwable);
            }
        } finally {
            realm.close();
        }
    }

    private void initPostalCodes(Realm realm) {
        Log.d(TAG, "-------------------------initPostalCodes--------------------------");
        realm.executeTransaction(r->{
            RealmResults<PostalInfo> pi = r.where(PostalInfo.class).findAll();
            pi.deleteAllFromRealm();
            try {
                InputStream ins = activity.getResources().openRawResource(
                        activity.getResources().getIdentifier("generic_postcode",
                                "raw", activity.getPackageName()));
                Gson gson = new Gson();
                JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));

                final Type POSTALINFO_TYPE = new TypeToken<List<PostalData>>() {
                }.getType();
                List<PostalData> postalInfos = gson.fromJson(jsonReader, POSTALINFO_TYPE);
                Log.d(TAG, "----------------------gson postcodes: "+postalInfos);
                DataInitializers.initPostalInfo(r, postalInfos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void initUserInfo(Realm realm) {
        Log.d(TAG,  "---------------initUserInfo--------------------");
        realm.executeTransaction(r->{
            InputStream ins = activity.getResources().openRawResource(
                    activity.getResources().getIdentifier("generic_customerinfo",
                            "raw", activity.getPackageName()));

            Gson gson = new Gson();
            JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));

            final Type USERINFO_TYPE = new TypeToken<List<UserData>>() {
            }.getType();
            List<UserData> userInfos = gson.fromJson(jsonReader, USERINFO_TYPE);

            Log.d(TAG,"--------------gson users: "+userInfos);
            DataInitializers.initUserInfo(r, userInfos);
        });
    }
}
