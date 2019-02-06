package me.adeen.foodles.models;

public class Restaurant {
    public String number;
    public String name;
    public String address;
    public String menuId;

    public Restaurant() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Restaurant(String number, String name, String address, String menuId) {
        this.number = number;
        this.name = name;
        this.address = address;
        this.menuId = menuId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getMenuId() {
        return menuId;
    }

    public String getNumber() {
        return number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
