package com.test.adapter;

import com.test.type.OrderType;
import com.test.type.Side;

public class OrderMsg {
    public final MsgType msgType;
    public final int orderId;
    public final int userId;
    public final int securityId;
    public final Side side;
    public final OrderType orderType;
    public final int qty;
    public final double price;

    public OrderMsg(final MsgType msgType,
                    final int securityId,
                    final int orderId,
                    final int userId,
                    final Side side,
                    final OrderType orderType,
                    final int qty,
                    final double price) {
        this.msgType = msgType;
        this.securityId = securityId;
        this.orderId = orderId;
        this.userId = userId;
        this.side = side;
        this.orderType = orderType;
        this.qty = qty;
        this.price = price;
    }
}
