package com.test;

import com.test.adapter.OrderMsg;
import com.test.me.Order;

public class OrderFactory {
    public static Order of(final OrderMsg orderMsg){
        return new Order(orderMsg.orderId, orderMsg.userId,
                orderMsg.securityId, orderMsg.side,
                orderMsg.orderType,
                orderMsg.qty,
                orderMsg.price,
                System.nanoTime());
    }
}
