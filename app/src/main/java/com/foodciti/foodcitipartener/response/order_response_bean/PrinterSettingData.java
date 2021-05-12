package com.foodciti.foodcitipartener.response.order_response_bean;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrinterSettingData {

    @JsonProperty("collection_cash")
    private String collectionCash;

    @JsonProperty("collection_card")
    private String collectionCard;

    public String getCollectionCash() {
        return collectionCash;
    }

    public void setCollectionCash(String collectionCash) {
        this.collectionCash = collectionCash;
    }

    public String getCollectionCard() {
        return collectionCard;
    }

    public void setCollectionCard(String collectionCard) {
        this.collectionCard = collectionCard;
    }

    public String getCollectionOnline() {
        return collectionOnline;
    }

    public void setCollectionOnline(String collectionOnline) {
        this.collectionOnline = collectionOnline;
    }

    public String getDeliveryCash() {
        return deliveryCash;
    }

    public void setDeliveryCash(String deliveryCash) {
        this.deliveryCash = deliveryCash;
    }

    public String getDeliveryCard() {
        return deliveryCard;
    }

    public void setDeliveryCard(String deliveryCard) {
        this.deliveryCard = deliveryCard;
    }

    public String getDeliveryOnline() {
        return deliveryOnline;
    }

    public void setDeliveryOnline(String deliveryOnline) {
        this.deliveryOnline = deliveryOnline;
    }

    @JsonProperty("collection_online")
    private String collectionOnline;

    @JsonProperty("delivery_cash")
    private String deliveryCash;

    @JsonProperty("delivery_card")
    private String deliveryCard;

    @JsonProperty("delivery_online")
    private String deliveryOnline;


}
