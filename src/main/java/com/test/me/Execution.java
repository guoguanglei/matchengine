package com.test.me;

import com.test.type.Side;

public class Execution {
    private final int orderId;
    private final Side side;
    private final int oppositeSideOrderId;
    private final double price;
    private final int qty;

    public Execution(final int orderId,
                     final Side side,
                     final int oppositeSideOrderId,
                     final double price,
                     final int qty) {
        this.orderId = orderId;
        this.side = side;
        this.oppositeSideOrderId = oppositeSideOrderId;
        this.price = price;
        this.qty = qty;
    }

    public int getOrderId() {
        return orderId;
    }

    public Side getSide() {
        return side;
    }

    public int getOppositeSideOrderId() {
        return oppositeSideOrderId;
    }

    public double getPrice() {
        return price;
    }

    public int getQty() {
        return qty;
    }

    @Override
    public String toString() {
        return "\nExecution{" +
                "orderId=" + orderId +
                ", side=" + side +
                ", oppositeSideOrderId=" + oppositeSideOrderId +
                ", price=" + price +
                ", qty=" + qty +
                "}\n";
    }
}
