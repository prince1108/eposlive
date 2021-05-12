package com.foodciti.foodcitipartener.gson;

import java.io.Serializable;

public class AddonJson implements Serializable {
    private Long id;
    private String name;
    private Integer color;
    private Double price;
    private Long menuCategoryId;
    private boolean isNoAddon;
    private long last_updated = -1l;

    public long getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(long last_updated) {
        this.last_updated = last_updated;
    }

    public boolean isNoAddon() {
        return isNoAddon;
    }

    public void setNoAddon(boolean noAddon) {
        isNoAddon = noAddon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getMenuCategoryId() {
        return menuCategoryId;
    }

    public void setMenuCategoryId(Long menuCategoryId) {
        this.menuCategoryId = menuCategoryId;
    }

    @Override
    public String toString() {
        return "AddonJson{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", price=" + price +
                ", menuCategoryId=" + menuCategoryId +
                '}';
    }
}
