package com.foodciti.foodcitipartener.gson;

import java.io.Serializable;
import java.util.List;

public class MenuCategoryJson implements Serializable {
    private Long id;
    private String name;
    private Integer color;
    private List<Long> menuItems, addons;
    private long last_updated = -1l;

    public long getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(long last_updated) {
        this.last_updated = last_updated;
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

    public List<Long> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<Long> menuItems) {
        this.menuItems = menuItems;
    }

    public List<Long> getAddons() {
        return addons;
    }

    public void setAddons(List<Long> addons) {
        this.addons = addons;
    }

    @Override
    public String toString() {
        return "MenuCategoryJson{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", menuItems=" + menuItems +
                ", addons=" + addons +
                '}';
    }
}
