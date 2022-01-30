package com.test.me;

import com.test.type.OrderType;
import com.test.type.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchAlgoTest {
    private final static int TEST_SECURITY_ID = 1;

    MatchAlgo matchAlgo;

    @BeforeEach
    void setUp() {
        matchAlgo = new MatchAlgo();
    }

    @Test
    public void shouldExecuteWithBestPriceWhenSellMatchBuy(){
        Order order = new Order(1, 1, TEST_SECURITY_ID, Side.Buy, OrderType.Limit, 200, 20.21, 1000);
        matchAlgo.addOrder(order);

        order = new Order(2, 2, TEST_SECURITY_ID, Side.Sell, OrderType.Limit, 100, 20.11, 1001);
        MatchEngineCmdReply<?> matchEngineCmdReply = matchAlgo.addOrder(order);
        assertEquals(matchEngineCmdReply.engineReplyType, MatchEngineCmdReplyType.Success);
        List<Execution> executionList = ((MatchResult) matchEngineCmdReply.payload).executionList;
        assertEquals(1, executionList.size());
        Execution execution = executionList.get(0);
        assertEquals(20.21, execution.getPrice());
        assertEquals(100, execution.getQty());
    }

    @Test
    public void shouldExecuteWithBestPriceWhenBuyMatchSell(){
        Order order = new Order(1, 1, TEST_SECURITY_ID, Side.Sell, OrderType.Limit, 200, 20.25, 1000);
        matchAlgo.addOrder(order);

        order = new Order(2, 2, TEST_SECURITY_ID, Side.Buy, OrderType.Limit, 100, 20.30, 1001);
        MatchEngineCmdReply<?> matchEngineCmdReply = matchAlgo.addOrder(order);
        assertEquals(matchEngineCmdReply.engineReplyType, MatchEngineCmdReplyType.Success);
        List<Execution> executionList = ((MatchResult) matchEngineCmdReply.payload).executionList;
        assertEquals(1, executionList.size());
        Execution execution = executionList.get(0);
        assertEquals(20.25, execution.getPrice());
        assertEquals(100, execution.getQty());
    }

    @Test
    public void shouldExecuteWithTimePriorityWhenPriceIsSame(){
        Order order = new Order(1, 1, TEST_SECURITY_ID, Side.Sell, OrderType.Limit, 200, 20.25, 1000);
        matchAlgo.addOrder(order);

        order = new Order(3, 3, TEST_SECURITY_ID, Side.Sell, OrderType.Limit, 100, 20.25, 1010);
        matchAlgo.addOrder(order);

        order = new Order(2, 2, TEST_SECURITY_ID, Side.Buy, OrderType.Limit, 250, 20.25, 1020);
        MatchEngineCmdReply<?> matchEngineCmdReply = matchAlgo.addOrder(order);
        assertEquals(matchEngineCmdReply.engineReplyType, MatchEngineCmdReplyType.Success);
        List<Execution> executionList = ((MatchResult) matchEngineCmdReply.payload).executionList;
        assertEquals(2, executionList.size());
        Execution execution = executionList.get(0);
        assertEquals(20.25, execution.getPrice());
        assertEquals(200, execution.getQty());
        assertEquals(1, execution.getOppositeSideOrderId());

        execution = executionList.get(1);
        assertEquals(20.25, execution.getPrice());
        assertEquals(50, execution.getQty());
        assertEquals(3, execution.getOppositeSideOrderId());
    }

    @Test
    public void shouldExecuteWithLimitPriceWhenReceiveMarketBuyOrder(){
        Order order = new Order(1, 1, TEST_SECURITY_ID, Side.Sell, OrderType.Limit, 200, 20.25, 1000);
        matchAlgo.addOrder(order);

        order = new Order(1, 1, TEST_SECURITY_ID, Side.Sell, OrderType.Limit, 200, 20.26, 1000);
        matchAlgo.addOrder(order);

        order = new Order(2, 2, TEST_SECURITY_ID, Side.Buy, OrderType.Market, 300, 0.0d, 1003);
        MatchEngineCmdReply<?> matchEngineCmdReply = matchAlgo.addOrder(order);
        assertEquals(matchEngineCmdReply.engineReplyType, MatchEngineCmdReplyType.Success);
        List<Execution> executionList = ((MatchResult) matchEngineCmdReply.payload).executionList;
        assertEquals(2, executionList.size());
        Execution execution = executionList.get(0);
        assertEquals(20.25, execution.getPrice());
        assertEquals(200, execution.getQty());
        execution = executionList.get(1);
        assertEquals(20.26, execution.getPrice());
        assertEquals(100, execution.getQty());
    }

    @Test
    public void shouldExecuteWithLimitPriceWhenReceiveMarketSellOrder(){
        Order order = new Order(1, 1, TEST_SECURITY_ID, Side.Buy, OrderType.Limit, 200, 20.25, 1000);
        matchAlgo.addOrder(order);

        order = new Order(1, 1, TEST_SECURITY_ID, Side.Buy, OrderType.Limit, 200, 20.26, 1000);
        matchAlgo.addOrder(order);

        order = new Order(2, 2, TEST_SECURITY_ID, Side.Sell, OrderType.Market, 300, 0.0d, 1003);
        MatchEngineCmdReply<?> matchEngineCmdReply = matchAlgo.addOrder(order);
        assertEquals(matchEngineCmdReply.engineReplyType, MatchEngineCmdReplyType.Success);
        List<Execution> executionList = ((MatchResult) matchEngineCmdReply.payload).executionList;
        assertEquals(2, executionList.size());
        Execution execution = executionList.get(0);
        assertEquals(20.26, execution.getPrice());
        assertEquals(200, execution.getQty());

        execution = executionList.get(1);
        assertEquals(20.25, execution.getPrice());
        assertEquals(100, execution.getQty());
    }

    @Test
    public void shouldExecuteWithLimitPriceWhenMatchWithMarketOrder(){
        Order order = new Order(1, 1, TEST_SECURITY_ID, Side.Buy, OrderType.Market, 200, 0.0d, 1000);
        matchAlgo.addOrder(order);

        order = new Order(2, 2, TEST_SECURITY_ID, Side.Sell, OrderType.Limit, 100, 20.26d, 1003);
        MatchEngineCmdReply<?> matchEngineCmdReply = matchAlgo.addOrder(order);
        assertEquals(matchEngineCmdReply.engineReplyType, MatchEngineCmdReplyType.Success);
        List<Execution> executionList = ((MatchResult) matchEngineCmdReply.payload).executionList;
        assertEquals(1, executionList.size());
        Execution execution = executionList.get(0);
        assertEquals(20.26, execution.getPrice());
        assertEquals(100, execution.getQty());
    }

    @Test
    public void shouldNotExecuteWhenOnlyMarketOrderLeft(){
        Order order = new Order(1, 1, TEST_SECURITY_ID, Side.Buy, OrderType.Market, 200, 0.0d, 1000);
        matchAlgo.addOrder(order);

        order = new Order(2, 2, TEST_SECURITY_ID, Side.Sell, OrderType.Market, 100, 0.0d, 1003);
        MatchEngineCmdReply<?> matchEngineCmdReply = matchAlgo.addOrder(order);
        assertEquals(matchEngineCmdReply.engineReplyType, MatchEngineCmdReplyType.Success);
        List<Execution> executionList = ((MatchResult) matchEngineCmdReply.payload).executionList;
        assertEquals(0, executionList.size());
    }

    @Test
    public void shouldRemoveOrderWhenOrderMatched(){
        Order order = new Order(1, 1, TEST_SECURITY_ID, Side.Buy, OrderType.Limit, 200, 20.21, 1000);
        matchAlgo.addOrder(order);

        order = new Order(2, 2, TEST_SECURITY_ID, Side.Sell, OrderType.Limit, 200, 20.11, 1001);
        matchAlgo.addOrder(order);

        MatchEngineCmdReply<?> matchEngineCmdReply = matchAlgo.cancelOrder(order);
        assertEquals(matchEngineCmdReply.engineReplyType, MatchEngineCmdReplyType.Fail);
        assertEquals(MatchAlgo.ORDER_DOES_NOT_EXIST, matchEngineCmdReply.payload);
    }

    @Test
    public void shouldNotSelfMatchWhenOrderReceived(){
        Order order = new Order(1, 1, TEST_SECURITY_ID, Side.Sell, OrderType.Limit, 200, 20.25, 1000);
        matchAlgo.addOrder(order);

        order = new Order(3, 3, TEST_SECURITY_ID, Side.Sell, OrderType.Limit, 100, 20.25, 1010);
        matchAlgo.addOrder(order);

        order = new Order(1, 1, TEST_SECURITY_ID, Side.Buy, OrderType.Limit, 250, 20.25, 1020);
        MatchEngineCmdReply<?> matchEngineCmdReply = matchAlgo.addOrder(order);
        assertEquals(matchEngineCmdReply.engineReplyType, MatchEngineCmdReplyType.Success);
        List<Execution> executionList = ((MatchResult) matchEngineCmdReply.payload).executionList;
        assertEquals(1, executionList.size());
        Execution execution = executionList.get(0);
        assertEquals(20.25, execution.getPrice());
        assertEquals(100, execution.getQty());
    }

    @Test
    public void shouldRejectWhenPriceOutOfRange(){
        Order order = new Order(1, 1, TEST_SECURITY_ID, Side.Buy, OrderType.Limit, 200, 20.21, 1000);
        matchAlgo.addOrder(order);

        order = new Order(2, 2, TEST_SECURITY_ID, Side.Buy, OrderType.Limit, 100, 40.11, 1001);
        MatchEngineCmdReply<?> matchEngineCmdReply = matchAlgo.addOrder(order);
        assertEquals(matchEngineCmdReply.engineReplyType, MatchEngineCmdReplyType.Fail);
        assertEquals(MatchAlgo.PRICE_IS_OUT_OF_RANGE, matchEngineCmdReply.payload);
    }
}