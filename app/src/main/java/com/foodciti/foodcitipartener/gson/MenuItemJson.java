package com.foodciti.foodcitipartener.gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuItemJson implements Serializable {
    private Long id, menuCategoryId, parentId;
    private String type;
    private Integer itemPosition, color;
    private String name;
    private Double collectionPrice, deliveryPrice, price;
    private final List<MenuItemJson> flavours = new ArrayList<>();
    private long last_updated = -1;

    public long getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(long last_updated) {
        this.last_updated = last_updated;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMenuCategoryId() {
        return menuCategoryId;
    }

    public void setMenuCategoryId(Long menuCategoryId) {
        this.menuCategoryId = menuCategoryId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public Integer getItemPosition() {
        return itemPosition;
    }

    public void setItemPosition(Integer itemPosition) {
        this.itemPosition = itemPosition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCollectionPrice() {
        return collectionPrice;
    }

    public void setCollectionPrice(Double collectionPrice) {
        this.collectionPrice = collectionPrice;
    }

    public Double getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(Double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<MenuItemJson> getFlavours() {
        return flavours;
    }


    @Override
    public String toString() {
        return "MenuItemJson{" +
                "id=" + id +
                ", menuCategoryId=" + menuCategoryId +
                ", type='" + type + '\'' +
                ", itemPosition=" + itemPosition +
                ", name='" + name + '\'' +
                ", collectionPrice=" + collectionPrice +
                ", deliveryPrice=" + deliveryPrice +
                ", price=" + price +
                ", flavours=" + flavours +
                '}';
    }
}
