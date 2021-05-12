package com.foodciti.foodcitipartener.migration;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.foodciti.foodcitipartener.realm_entities.Order;
import com.foodciti.foodcitipartener.realm_entities.OrderTuple;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.realm_entities.PurchaseEntry;
import com.foodciti.foodcitipartener.realm_entities.Table;
import com.foodciti.foodcitipartener.utils.OrderHistoryUtils;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObjectSchema;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.RealmSchema;

public class MigrateFromVersion1 implements MigrationHelper {
    private final String TAG = "MigrateFromVersion1";
    @Override
    public void migrate(DynamicRealm dynamicRealm) throws Exception {
        RealmSchema schema = dynamicRealm.getSchema();
        createOrderPostalInfo(schema);
        createOrderCustomerInfoSchema(schema);
        createOrderMenuItemSchema(schema);
        createOrderAddonSchema(schema);
        createOrderMenuCategorySchema(schema);
        createPurchaseEntrySchema(schema);
        createPurchaseSchema(schema);

//        migrateOldOrderHistory();
    }

    private void createPurchaseSchema(RealmSchema schema) throws Exception {
        Log.d(TAG, "-----------------------creating schema: Purchase");
        try {
            schema.create("Purchase")
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("timestamp", long.class)
                    .addField("deliveryTime", long.class)
                    .addField("tableId", long.class)
                    .addRealmListField("purchaseEntries", schema.get("PurchaseEntry"))
                    .addRealmObjectField("orderCustomerInfo", schema.get("OrderCustomerInfo"))
                    .addField("total", double.class)
                    .addField("subTotal", double.class)
                    .addField("discount", double.class)
                    .addField("extra", double.class)
                    .addField("deliveryCharges", double.class)
                    .addField("serviceCharges", double.class)
                    .addField("paymentMode", String.class)
                    .addField("orderType", String.class)
                    .addField("tableName", String.class)
                    .addRealmObjectField("driver", schema.get("Driver"))
                    .addField("isDelivered", boolean.class);
        } finally {

        }
    }

    private void createPurchaseEntrySchema(RealmSchema schema) throws Exception {
        Log.d(TAG, "-----------------------creating schema: try");

        try {
            schema.create("PurchaseEntry")
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("customerOrderId", long.class)
                    .addField("price", double.class)
                    .addField("count", int.class)
                    .addField("additionalNote", String.class)
//                .addRealmListField("purchases", schema.get("Purchase"))
                    .addRealmObjectField("orderMenuItem", schema.get("OrderMenuItem"))
                    .addRealmListField("orderAddons", schema.get("OrderAddon"));
        } finally {

        }
    }

    private void createOrderMenuItemSchema(RealmSchema schema) throws Exception {
        Log.d(TAG, "-----------------------creating schema: OrderMenuItem");
        try {
            schema.create("OrderMenuItem")
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("last_updated", long.class)
                    .addField("itemPosition", int.class)
                    .addField("color", int.class)
                    .addField("collectionPrice", double.class)
                    .addField("deliveryPrice", double.class)
                    .addField("price", double.class)
                    .addField("type", String.class)
                    .addField("name", String.class);
        } finally {

        }
    }

    private void createOrderMenuCategorySchema(RealmSchema schema) throws Exception {
        Log.d(TAG, "-----------------------creating schema: OrderMenuCategory");
        try {
            schema.create("OrderMenuCategory")
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("last_updated", long.class)
                    .addField("color", int.class)
                    .addField("itemposition", int.class)
                    .addField("printOrder", int.class)
                    .addField("name", String.class)
                    .addRealmListField("orderMenuItems",schema.get("OrderMenuItem"))
                    .addRealmListField("orderAddons",schema.get("OrderAddon"));
        } finally {

        }
    }

    private void createOrderAddonSchema(RealmSchema schema) throws Exception {
        Log.d(TAG, "-----------------------creating schema: OrderAddon");
        try {
            schema.create("OrderAddon")
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("last_updated", long.class)
                    .addField("color", int.class)
                    .addField("itemposition", int.class)
                    .addField("isNoAddon", boolean.class)
                    .addField("selected", boolean.class)
                    .addField("price", double.class)
                    .addField("name", String.class);
        } finally {

        }
    }

    private void createOrderCustomerInfoSchema(RealmSchema schema) throws Exception {
        Log.d(TAG, "-----------------------creating schema: OrderCustomerInfo");
        try {
            schema.create("OrderCustomerInfo")
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("name", String.class)
                    .addField("phone", String.class)
                    .addField("house_no", String.class)
                    .addField("remarks", String.class)
                    .addField("remarkStatus", String.class)
                    .addRealmObjectField("orderPostalInfo", schema.get("OrderPostalInfo"));
        } finally {

        }
    }

    private void createOrderPostalInfo(RealmSchema schema) throws Exception {
        Log.d(TAG, "-----------------------creating schema: OrderPostalInfo");
        try {
            schema.create("OrderPostalInfo")
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("address", String.class)
                    .addField("A_PostCode", String.class);
        } finally {

        }
    }

    /*private void migrateOldOrderHistory() throws Exception {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "foodciti");
        RealmConfiguration config = new RealmConfiguration.Builder()
//                .name("backup.realm").directory(folder)
                .name("efeskebab.realm")
//                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm realm = Realm.getInstance(config);
        OrderHistoryUtils.setRealm(realm);

        AtomicReference<Object[]> atomicReference = new AtomicReference<>();
        try {
            realm.executeTransaction(r->{
                Object[] success = new Object[2];
                try {
                    RealmQuery<Order> orderRealmQuery = r.where(Order.class);
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
                    success[0] = true;
                    success[1] = null;
                } catch (Exception e) {
                    success[0] = false;
                    success[1] = e;
                } finally {
                    atomicReference.set(success);
                }
            });

            boolean result = (boolean)atomicReference.get()[0];
            if(!result) {
                Throwable throwable = (Throwable)atomicReference.get()[1];
                throw new Exception(throwable);
            }
        } finally {
            realm.close();
        }
    }*/
}
