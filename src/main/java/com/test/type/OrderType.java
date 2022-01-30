package com.test.type;

public enum OrderType {
    Market,
    Limit;

    public static final double BUY_MAKRET_PRICE = Double.MAX_VALUE;

    public static final double SELL_MAKRET_PRICE = Double.MIN_VALUE;

    public static boolean isMarketPrice(final double price){
        return price == BUY_MAKRET_PRICE || price == SELL_MAKRET_PRICE;
    }
}
