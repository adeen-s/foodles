package me.adeen.foodles.models;

public class User {
    public String number;
    public String name;
    public String address;
    public boolean isRestaurant;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String number, String name, String address, boolean isRestaurant) {
        this.number = number;
        this.name = name;
        this.address = address;
        this.isRestaurant = isRestaurant;
    }

    public String getNumber() {
        return number;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRestaurant(boolean restaurant) {
        isRestaurant = restaurant;
    }
}