package me.adeen.foodles.models;

public class Order extends Item{
    int quantity;

    public Order() {
    }

    public Order(String name, float price, int quantity) {
        super(name, price);
        this.quantity = quantity;
    }

    public Order(String name, float price) {
        super(name, price);
        quantity = 0;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }
}
