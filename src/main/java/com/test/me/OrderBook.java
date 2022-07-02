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
            if(buySidePriceLevels.containsKey(order.getPrice())){
                buySidePriceLevels.get(order.getPrice()).addOrder(order);
            } else {
                PriceLevel priceLevel = new PriceLevel(order.getPrice());
                buySidePriceLevels.put(order.getPrice(), priceLevel);
                priceLevel.addOrder(order);
            }
        } else {
            if(sellSidePriceLevels.containsKey(order.getPrice())){
                sellSidePriceLevels.get(order.getPrice()).addOrder(order);
            } else {
                PriceLevel priceLevel = new PriceLevel(order.getPrice());
                sellSidePriceLevels.put(order.getPrice(), priceLevel);
                priceLevel.addOrder(order);
            }
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

    public PriceLevel getHighestBuySidePriceLevel(){
        for(PriceLevel priceLevel : buySidePriceLevels.values()){
            if(!OrderType.isMarketPrice(priceLevel.getPrice()))
                return priceLevel;
        }
        return null;
    }

    public PriceLevel getLowestSellSidePriceLevel(){
        for(PriceLevel priceLevel : sellSidePriceLevels.values()){
            if(!OrderType.isMarketPrice(priceLevel.getPrice()))
                return priceLevel;
        }
        return null;
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
