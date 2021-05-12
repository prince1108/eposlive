//package com.foodciti.foodcitipartener.realm_entities;
//
//import java.io.Serializable;
//
//import io.realm.RealmList;
//import io.realm.RealmObject;
//import io.realm.annotations.Ignore;
//import io.realm.annotations.PrimaryKey;
//
//public class Addonflavour extends RealmObject implements Serializable {
//    @PrimaryKey
//    public long id;
//    public String name = "";
//    public int color = -1, itemposition = -1;
//    public double price;
//    public MenuCategory menuCategory;
//    public boolean isNoAddon;
//    public boolean selected;
//    @Ignore
//    public CartItem cartItem;
//    public long last_updated = -1l;
//
//    public void copy(Addonflavour addon) {
//        this.name = addon.name;
//        this.price = addon.price;
//    }
//
//    @Override
//    public String toString() {
//        return "Addonflavour{" +
//                ", name='" + name + '\'' +
//                ", color=" + color +
//                ", price=" + price +
//                ", menuCategory=" + menuCategory +
//                ", selected=" + selected +
//                ", cartItem=" + cartItem +
//                '}';
//    }
//}
