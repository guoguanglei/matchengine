package com.test.me;

import com.test.type.OrderType;
import com.test.type.Side;

import java.util.Objects;

public class Order {
    private final int orderId;
    private final int userId;
    private final int securityId;
    private final Side side;
    private final OrderType orderType;
    private final int origQty;
    private int leaveQty;
    private final double price;
    private final long enterTime;

    public Order(final int orderId,
                 final int userId,
                 final int securityId,
                 final Side side,
                 final OrderType orderType,
                 final int origQty,
                 final double price,
                 final long enterTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.securityId = securityId;
        this.side = side;
        this.orderType = orderType;
        this.origQty = origQty;
        this.leaveQty = origQty;
        if(this.orderType == OrderType.Market) {
            if(this.side == Side.Buy){
                this.price = OrderType.BUY_MAKRET_PRICE;
            } else {
                this.price = OrderType.SELL_MAKRET_PRICE;
            }
        } else {
            this.price = price;
        }
        this.enterTime = enterTime;
    }

    public boolean fullFilled(){
        return this.leaveQty == 0;
    }

    public int getUserId() {
        return userId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getSecurityId() {
        return securityId;
    }

    public Side getSide() {
        return side;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public int getOrigQty() {
        return origQty;
    }

    public int getLeaveQty() {
        return leaveQty;
    }

    public void updateLeaveQty(final int leaveQty){
        this.leaveQty = leaveQty;
    }

    public double getPrice() {
        return price;
    }

    public long getEnterTime() {
        return enterTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderId == order.orderId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    public static String formatPrice(double price){
        return OrderType.isMarketPrice(price) ? "Market Price" : String.valueOf(price);
    }

    @Override
    public String toString() {
        return "\nOrder{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", securityId=" + securityId +
                ", side=" + side +
                ", type=" + orderType +
                ", origQty=" + origQty +
                ", leaveQty=" + leaveQty +
                ", price=" + formatPrice(price) +
                ", enterTime=" + enterTime +
                "}";
    }
}
