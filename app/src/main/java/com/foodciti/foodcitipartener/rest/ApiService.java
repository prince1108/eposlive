package com.foodciti.foodcitipartener.rest;


import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import com.foodciti.foodcitipartener.response.GenericResponse;
import com.foodciti.foodcitipartener.response.InventoryRestaurent;
import com.foodciti.foodcitipartener.response.ItemCategories;
import com.foodciti.foodcitipartener.response.Order;
import com.foodciti.foodcitipartener.response.OrderTotal;
import com.foodciti.foodcitipartener.response.Restaurent;
import com.foodciti.foodcitipartener.response.RestaurentCusine;
import com.foodciti.foodcitipartener.response.RestaurentItem;
import com.foodciti.foodcitipartener.response.RestaurentUser;
import com.foodciti.foodcitipartener.response.ReviewResponse;
import com.foodciti.foodcitipartener.response.SubInfo;
import com.foodciti.foodcitipartener.response.order_response_bean.PrinterSettingData;

import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by quflip1 on 08-02-2017.
 */

public interface ApiService {

    @GET("restaurents/{restaurent_id}/orders")
    Observable<GenericResponse<List<Order>>> getOrderList(
            @Header("Authorization") String token,
            @Path("restaurent_id") String foodTruckId);

    @FormUrlEncoded
    @POST("restaurents/users/connect")
    Observable<GenericResponse> connectUser(@Field("foodtruck_id") String foodTruckId, @Field("user_id")
            String userId);

    @GET("restaurents/{restaurentID}/forwardorders")
    Observable<GenericResponse<List<Order>>> getForwardedOrderStatus(@Path("restaurentID") String foodTruckId);


    @FormUrlEncoded
    @POST("restaurents/orders")
    Observable<GenericResponse<Order>> updateOrder(
            @Header("Authorization") String token,
            @Field("order_id") String orderId,
            @Field("order_status") int orderStatus);


    @FormUrlEncoded
    @POST("restaurents/apk/errorlogger")
    Observable<GenericResponse<String>> postErrorLogs(
            @Field("restaurantid") String restaurantid,
            @Field("errormessage") String errormessage,
            @Field("errortype") String errortype,
            @Field("screenname") String screenname,
            @Field("deviceinfo") String deviceinfo,
            @Field("networkstatus") String networkstatus,
            @Field("errordate") String errordate);


    @FormUrlEncoded
    @POST("customeradmin/restaurant/printersetting/save/{restaurantid}")
    Observable<GenericResponse<String>> postPrinterSettings(@Path("restaurantid") String restaurantid,
            @Field("collection_cash") String collection_cash,
            @Field("collection_card") String collection_card,
            @Field("collection_online") String collection_online,
            @Field("delivery_cash") String delivery_cash,
            @Field("delivery_card") String delivery_card,
            @Field("delivery_online") String delivery_online);


    @GET("restaurents/{foodtruck_id}/orders/{order_status}")
    Observable<GenericResponse<List<Order>>> getStatusOrderList(
            @Path("foodtruck_id") String foodTruckId,
            @Path("order_status") int orderStatus);

    @GET("restaurents/{foodtruck_id}/orders/details/{order_status}")
    Observable<GenericResponse<List<Order>>> getOrdersList(
            @Path("foodtruck_id") String foodTruckId,
            @Path("order_status") int orderStatus);

    @GET("restaurents/items")
    Observable<GenericResponse<InventoryRestaurent>> getItemList(
            @Header("Authorization") String token,
            @Query("restaurent_id") String foodTruckId);

    @GET("restaurents/items/search")
    Observable<GenericResponse<InventoryRestaurent>> getSearchItemList(
            @Query("restaurent_id") String foodTruckId,
            @Query("search_text") String searchText);


    @FormUrlEncoded
    @PUT("orders/cancel")
    Observable<GenericResponse> cancelOrder(
            @Header("Authorization") String token,
            @Field("order_id") String orderId);

    @FormUrlEncoded
    @POST("restaurents/status/{foodtruck_id}")
    Observable<GenericResponse> changeFoodTruckState(@Path("foodtruck_id") String foodTruckId,
                                                     @Field("open_status") int openStatus);

    @Multipart
    @POST("restaurents")
    Observable<GenericResponse<Restaurent>> registerFoodTruck(@PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST("restaurents/cusines/{restaurent_id}")
    Observable<GenericResponse> addCusines(@Path("restaurent_id") String foodTruckId,
                                           @FieldMap Map<String, String> cusineMap);

    @POST("restaurents/{restaurent_id}/opencloserestaurant/{status}")
    Observable<GenericResponse<String>> openCloseRestaurent(@Path("restaurent_id") String restaurentID, @Path("status") String status);

    @FormUrlEncoded
    @POST("items/categories/{foodtruck_id}")
    Observable<GenericResponse> addCategory(@Path("foodtruck_id") String foodTruckId,
                                            @FieldMap Map<String, String> categoryMap);

    @FormUrlEncoded
    @PUT("restaurents/{restaurent_id}/info")
    Observable<GenericResponse<InventoryRestaurent>> updateFoodTruckInfo(@Path("restaurent_id") String foodTruckId,
                                                                         @Field("restaurent_name") String foodTruckName,
                                                                         @Field("ph_no") String foodTruckphoneNumber,
                                                                         @Field("restaurent_discount_overall") String foodTruckDiscountOverall,
                                                                         @Field("restaurent_minimum_order_value") String foodTruckMinimumOrderValue,
                                                                         @Field("restaurent_delivery_charge") String foodTruckDeliveryCharge,
                                                                         @Field("restaurent_location") String foodTruckLocation,
                                                                         @Field("restaurent_tag") String foodTruckTag,
                                                                         @Field("restaurent_starting_timing_one") String foodTruckStartingTiming,
                                                                         @Field("restaurent_closing_timing_one") String foodTruckClosingTiming,
                                                                         @Field("restaurent_starting_timing_two") String foodTruckStartingTimingTwo,
                                                                         @Field("restaurent_closing_timing_two") String foodTruckClosingTimingTwo,
                                                                         @Field("restaurent_postal_code") String restaurentPostalCode,
                                                                         @Field("restaurent_delivery_range") String deliveryRange);

    @GET("restaurents/{foodtruck_id}/info")
    Observable<GenericResponse<InventoryRestaurent>> getFoodTruckInfo(@Path("foodtruck_id") String foodTruckId);

    @GET("items/categories/{foodtruck_id}")
    Observable<GenericResponse<ItemCategories>> getCategories(@Path("foodtruck_id") String foodTruckId);

    @GET("restaurents/cusines/{foodtruck_id}")
    Observable<GenericResponse<RestaurentCusine>> getCusines(@Path("foodtruck_id") String foodTruckId);


    @FormUrlEncoded
    @POST("restaurents/orders/message")
    Observable<GenericResponse<String>> notifyUser(
//            @Header("Autorization") String token,
            @Field("order_id") String orderId,
            @Field("foodtruck_id") String foodTruckId,
            @Field("warning_text") String warningText,
            @Field("status") String status
    );

    @FormUrlEncoded
    @POST("restaurents/status/{foodtruck_id}")
    Observable<GenericResponse> notifyOrders(
            @Path("foodtruck_id") String foodTruckId,
            @Field("open_status") String openstastus,
            @Field("restaurent_delivery_availability") String restaurant_delivery_availability,
            @Field("restaurent_pickup_availability") String restaurant_pickup_availability);

    @FormUrlEncoded
    @POST("restaurents/items")
    Observable<GenericResponse<List<InventoryRestaurent>>> addItem(
            @Field("restaurent_id") String foodTruckId,
            @Field("item_name") String itemName,
            @Field("item_tag") String itemTag,
            @Field("item_category") String itemCategory,
            @Field("item_stock") int itemStock,
            @Field("item_description") String itemDescription,
            @Field("item_price") String itemPrice,
            @Field("item_discount_price") String discountPrice,
            @FieldMap Map<String, String> illustrationMap);


    @FormUrlEncoded
    @PUT("restaurents/items/{item_id}")
    Observable<GenericResponse> updateIt(
            @Header("Authorization") String token,
            @Path("item_id") String itemId,
            @Field("item_name") String itemName,
            @Field("item_tag") String itemTag,
            @Field("item_category") String itemCategory,
            @Field("item_description") String itemDescription,
            @FieldMap Map<String, String> illustationMap,
            @Field("item_discount_price") String itemDiscountPrice,
            @Field("item_price") String itemPrice,
            @Field("item_status") String itemStatus);

    @DELETE("restaurents/items")
    Observable<GenericResponse<InventoryRestaurent>> deleteItem(
            @Query("item_id") String itemId,
            @Query("restaurent_id") String foodTruckId
    );

    @GET("review")
    Observable<GenericResponse<ReviewResponse>> getReviews(@Query("item_id") String itemId);


    @FormUrlEncoded
    @POST("restaurents/users/login")
    Observable<GenericResponse<RestaurentUser>> getUserInfo(@Field("email_id") String emailId,
                                                            @Field("password") String password);

    @FormUrlEncoded
    @POST("restaurents/items/options")
    Observable<GenericResponse> submitOptions(@Field("option_sub_category") String subCategory,
                                              @Field("option_cumpulsory") String optionCumpulsory,
                                              @Field("restaurent_id") String restaurentID,
                                              @FieldMap Map<String, String> itemMap,
                                              @FieldMap Map<String, String> itemPriceMap);

    @FormUrlEncoded
    @POST("pmi/spr.aspx")
    Observable<ResponseBody> testInfo(@Header("Authorization") String token,
                                      @Header("Content-Type") String contentType,
                                      @Field("CardHolderName") String name,
                                      @Field("PaymentTypeId") String paymentId,
                                      @Field("CardNumber") String cardNumber,
                                      @Field("ExpMonth") int expMonth,
                                      @Field("ExpYear") int year,
                                      @Field("CVV") int cvv,
                                      @Field("Address1") String address,
                                      @Field("City") String city,
                                      @Field("State") String state,
                                      @Field("PostalCode") String postalCode,
                                      @Field("Country") String country,
                                      @Field("CID") String cid,
                                      @Field("SettingsCipher") String settingsCipher);

    @GET("restaurents/users/connect")
    Observable<GenericResponse<String>> getConnectedFoodTruckId(@Query("user_id") String userId);

    @GET("customeradmin/restaurant/printersetting/{restaurantid}")
    Observable<GenericResponse<PrinterSettingData>> getPrinterSetting(@Path("restaurantid") String restaurantid);

    @GET("restaurents/items/options/{restaurent_id}")
    Observable<GenericResponse<List<String>>> getSubCategories(@Path("restaurent_id") String restaurentId);

    @GET("restaurents/items/options")
    Observable<GenericResponse<List<SubInfo>>> getItemsForCategories(@Query("sub_category_name") String categoryName);


    @GET("restaurents/items/options/attach")
    Observable<GenericResponse<RestaurentItem>> getConnectedItems(@Query("item_id") String itemId,
                                                                  @Query("item_sub_category") String itemSubCategory);

    @GET("restaurents/items/options/attach/items")
    Observable<GenericResponse<List<SubInfo>>> getConnectedSubItems(@Query("item_sub_category") String itemSubCategory,
                                                                    @Query("restaurent_id") String restaurentId);


    @FormUrlEncoded
    @POST("restaurents/items/options/connect")
    Observable<GenericResponse> connectSubCategories(@Field("item_id") String itemId,
                                                     @FieldMap Map<String, String> categoryMap);

    @DELETE("restaurents/items/options/{option_id}/deconnect")
    Observable<GenericResponse> detachCategory(@Path("option_id") String optionId,
                                               @Query("item_id") String itemId);

    @GET("restaurents/orders/totalCash/{restaurant_id}")
    Observable<GenericResponse<OrderTotal>> getTotalMatrix(@Path("restaurant_id") String restaurantId);

    @GET("restaurents/orders/total/details/{restaurant_id}")
    Observable<GenericResponse<OrderTotal>> getxreportTotalMatrix(@Path("restaurant_id") String restaurantId);

    @FormUrlEncoded
    @PUT("restaurents/items/options/{option_id}")
    Observable<GenericResponse<String>> updateOptionInfo(@Path("option_id") String optionId,
                                                         @Field("option_name") String optionName,
                                                         @Field("option_price") String optionPrice);

    @FormUrlEncoded
    @PUT("orders/tat")
    Observable<GenericResponse> giveCookingTime(@Field("order_id") String order_id,
                                                @Field("order_tat") String order_tat);

    @FormUrlEncoded
    @PUT("restaurents/history")
    Observable<GenericResponse> orderHistory(@Field("restaurent_id") String restaurent_id);

//    @GET("restaurents/{restaurant_id}/info")
//    Observable<MenuListResponseBean> getRestaurantMenu(@Path("restaurant_id") String restaurantId);


    @FormUrlEncoded
    @PUT("restaurents/items/itemstatus/{item_id}")
    Observable<GenericResponse<String>> updateMenuItemStatus(@Path("item_id") String itemId,
                                                             @Field("restaurent_id") String restaurantId,
                                                             @Field("item_isonline") boolean itemStatus);
}
