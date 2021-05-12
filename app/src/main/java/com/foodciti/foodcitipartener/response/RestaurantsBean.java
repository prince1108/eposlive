package com.foodciti.foodcitipartener.response;

public class RestaurantsBean
{
    private String restaurantName;
    private boolean isSelected;

    public RestaurantsBean(){

    }


    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
