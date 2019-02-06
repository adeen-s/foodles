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
}