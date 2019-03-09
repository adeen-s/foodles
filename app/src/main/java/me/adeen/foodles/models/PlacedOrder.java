package me.adeen.foodles.models;

import java.util.List;

public class PlacedOrder {
    List<Order> orders;
    String byCustomer;
    String toRestaurant;
    String time;
    String code;
    float amount;

    public PlacedOrder() {

    }

    public PlacedOrder(List<Order> orders, String byCustomer, String toRestaurant, String time, float amount) {
        this.orders = orders;
        this.byCustomer = byCustomer;
        this.toRestaurant = toRestaurant;
        this.time = time;
        this.amount = amount;
    }


    public PlacedOrder(List<Order> orders, String byCustomer, String toRestaurant, String time, float amount,String code) {
        this.orders = orders;
        this.byCustomer = byCustomer;
        this.toRestaurant = toRestaurant;
        this.time = time;
        this.code = code;
        this.amount = amount;
    }


    public List<Order> getOrders() {
        return orders;
    }

    public String getByCustomer() {
        return byCustomer;
    }

    public String getTime() {
        return time;
    }

    public String getToRestaurant() {
        return toRestaurant;
    }

    public void setByCustomer(String byCustomer) {
        this.byCustomer = byCustomer;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setToRestaurant(String toRestaurant) {
        this.toRestaurant = toRestaurant;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
