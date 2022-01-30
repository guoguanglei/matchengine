package com.test.me;

import com.test.type.OrderType;
import com.test.type.Side;

import java.util.*;

public class OrderBook {
    private final int securityId;

    private final TreeMap<Double, PriceLevel> buySidePriceLevels;

    private final TreeMap<Double, PriceLevel> sellSidePriceLevels;

    private final HashMap<Integer, Order> orders;

    public OrderBook(final int securityId) {
        this.securityId = securityId;
        buySidePriceLevels = new TreeMap<>(Comparator.reverseOrder()); //downwards on buy side
        sellSidePriceLevels = new TreeMap<>();
        orders = new HashMap<>();
    }

    public int getSecurityId() {
        return securityId;
    }

    public TreeMap<Double, PriceLevel> getBuySidePriceLevels() {
        return buySidePriceLevels;
    }

    public TreeMap<Double, PriceLevel> getSellSidePriceLevels() {
        return sellSidePriceLevels;
    }

    public void addOrder(final Order order){
        if(order.getSide() == Side.Buy) {
            buySidePriceLevels.computeIfAbsent(order.getPrice(), PriceLevel::new).addOrder(order);
        } else {
            sellSidePriceLevels.computeIfAbsent(order.getPrice(), PriceLevel::new).addOrder(order);
        }
        orders.put(order.getOrderId(), order);
    }

    public void removeOrder(final Order order){
        if(order.getSide() == Side.Buy) {
            removeOrder(buySidePriceLevels, order);
        } else {
            removeOrder(sellSidePriceLevels, order);
        }
        orders.remove(order.getOrderId());
    }

    private void removeOrder(final Map<Double, PriceLevel> priceLevels, final Order order) {
        final PriceLevel priceLevel = priceLevels.get(order.getPrice());
        priceLevel.removeOrder(order);
        if(priceLevel.isExhausted()){
            priceLevels.remove(priceLevel.getPrice());
        }
    }

    public boolean containsOrder(final Order order){
        return orders.containsKey(order.getOrderId());
    }

    public Optional<PriceLevel> getHighestBuySidePriceLevel(){
        return buySidePriceLevels.values().stream().filter(priceLevel -> !OrderType.isMarketPrice(priceLevel.getPrice())).findFirst();
    }

    public Optional<PriceLevel> getLowestSellSidePriceLevel(){
        return sellSidePriceLevels.values().stream().filter(priceLevel -> !OrderType.isMarketPrice(priceLevel.getPrice())).findFirst();
    }

    @Override
    public String toString() {
        return "Book{" +
                "\nsecurityId=\n" + securityId +
                "\nbuySidePriceLevels=\n" + buySidePriceLevels.values() +
                "\n, sellSidePriceLevels=\n" + sellSidePriceLevels.values() +
                "}\n";
    }
}
