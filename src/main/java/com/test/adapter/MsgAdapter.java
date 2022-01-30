package com.test.adapter;

/**
 * Receive order msg from different channel.
 */
public interface MsgAdapter {
    void onMsg(OrderMsg orderMsg);
}
