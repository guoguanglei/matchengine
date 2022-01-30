package com.test.me;

import java.util.*;

public class PriceLevel {
    private final double price;
    private final TreeSet<Order> orders;

    public PriceLevel(final double price) {
        this.price = price;
        orders = new TreeSet<>(Comparator.comparingLong(Order::getEnterTime));
    }

    public void addOrder(final Order order){
        orders.add(order);
    }

    public void removeOrder(final Order order){
        orders.remove(order);
    }

    public Set<Order> getOrders(){
        return orders;
    }

    public boolean isExhausted(){
        return orders.size() == 0;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Level{" +
                "price=" + Order.formatPrice(price) +
                ", orders=" + orders +
                "\n}";
    }
}
