package com.test.me;

import com.test.type.OrderType;
import com.test.type.Side;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

public class MatchAlgo {
    private static final double VALID_PRICE_RANGE = 10.0d;

    public static final String PRICE_IS_OUT_OF_RANGE = "Price is out of range.";

    public static final String ORDER_DOES_NOT_EXIST = "Order does not exist.";

    private final HashMap<Integer, OrderBook> orderBooks;

    public MatchAlgo() {
        this.orderBooks = new HashMap<>();
    }

    public MatchEngineCmdReply<?> addOrder(final Order order) {
        final OrderBook orderBook = orderBooks.computeIfAbsent(order.getSecurityId(), OrderBook::new);
        if(order.getSide() == Side.Buy) {
            return addBuyOrder(order, orderBook);
        } else {
            return addSellOrder(order, orderBook);
        }
    }

    public MatchEngineCmdReply<?> addBuyOrder(final Order order, final OrderBook orderBook){
        final Optional<String> result = validateBuyOrder(order, orderBook);
        if (result.isPresent()) {
            return new MatchEngineCmdReply<>(MatchEngineCmdReplyType.Fail, result.get());
        } else {
            //terminate when sell price greater than buy price
            return new MatchEngineCmdReply<>(MatchEngineCmdReplyType.Success,
                    addOrder(order,
                            orderBook,
                            orderBook.getSellSidePriceLevels(),
                            ((buyPrice, sellPrice) -> buyPrice < sellPrice)));
        }
    }

    private Optional<String> validateBuyOrder(final Order order, final OrderBook orderBook) {
        if(order.getOrderType() == OrderType.Limit) {
            final Optional<PriceLevel> priceLevel = orderBook.getHighestBuySidePriceLevel();
            if (priceLevel.isPresent()) {
                final double highestBuyPriceLevel = priceLevel.get().getPrice();
                if (order.getPrice() > highestBuyPriceLevel + VALID_PRICE_RANGE) {
                    return Optional.of(PRICE_IS_OUT_OF_RANGE);
                }
            }
        }
        return Optional.empty();
    }

    public MatchEngineCmdReply<?> addSellOrder(final Order order, final OrderBook orderBook){
        final Optional<String> result = validateSellOrder(order, orderBook);
        if (result.isPresent()) {
            return new MatchEngineCmdReply<>(MatchEngineCmdReplyType.Fail, result.get());
        } else {
            //terminate when buy price less than sell price
            return new MatchEngineCmdReply<>(MatchEngineCmdReplyType.Success,
                    addOrder(order,
                            orderBook,
                            orderBook.getBuySidePriceLevels(),
                            ((sellPrice, buyPrice) -> sellPrice > buyPrice)));
        }
    }

    private Optional<String> validateSellOrder(final Order order, final OrderBook orderBook) {
        if(order.getOrderType() == OrderType.Limit) {
            final Optional<PriceLevel> priceLevel = orderBook.getLowestSellSidePriceLevel();
            if (priceLevel.isPresent()) {
                final double lowestSellPrice = priceLevel.get().getPrice();
                if (order.getPrice() < lowestSellPrice - VALID_PRICE_RANGE) {
                    return Optional.of(PRICE_IS_OUT_OF_RANGE);
                }
            }
        }
        return Optional.empty();
    }

    private MatchResult addOrder(final Order order,
                                 final OrderBook orderBook,
                                 final Map<Double, PriceLevel> oppositeSidePriceLevels,
                                 final BiPredicate<Double, Double> terminateCondition) {
        final MatchResult matchResult = match(order, oppositeSidePriceLevels, terminateCondition);
        if(!order.fullFilled()) {
            orderBook.addOrder(order);
        }
        updateOrderBook(orderBook, matchResult);
        return matchResult;
    }

    private MatchResult match(Order order,
                              Map<Double, PriceLevel> oppositeSidePriceLevels,
                              BiPredicate<Double, Double> terminateCondition) {
        PriceLevel oppositeSidePriceLevel;
        Iterator<Map.Entry<Double, PriceLevel>> iterator = oppositeSidePriceLevels.entrySet().iterator();
        final MatchResult matchResult = new MatchResult();
        while(iterator.hasNext()){
            oppositeSidePriceLevel = iterator.next().getValue();
            if(terminateCondition.test(order.getPrice() , oppositeSidePriceLevel.getPrice()))
                break;
            match(order, oppositeSidePriceLevel, matchResult);
            if(order.fullFilled())
                break;
        }
        return matchResult;
    }

    private void match(final Order order,
                       final PriceLevel oppositeSidePriceLevel,
                       final MatchResult matchResult){
        Iterator<Order> iterator = oppositeSidePriceLevel.getOrders().iterator();
        Order oppositeSideOrder;
        while(iterator.hasNext()) {
            oppositeSideOrder = iterator.next();
            match(order, oppositeSideOrder).ifPresent(matchResult::addExecution);
            if(oppositeSideOrder.fullFilled()){
                matchResult.fullfilledOppositeSideOrders.add(oppositeSideOrder);
            }
            if(order.fullFilled())
                break;
        }
    }

    private Optional<Execution> match(final Order order, final Order oppositeSideOrder) {
        //prevent self match
        if(oppositeSideOrder.getUserId() == order.getUserId()){
            return Optional.empty();
        }
        //all market order on both side
        if (oppositeSideOrder.getOrderType() == OrderType.Market && order.getOrderType() == OrderType.Market) {
            return Optional.empty();
        }
        double execPrice = oppositeSideOrder.getPrice();
        if(oppositeSideOrder.getOrderType() == OrderType.Market){
            execPrice = order.getPrice();
        }
        final Optional<Execution> execution;
        final int execQty = Math.min(order.getLeaveQty(), oppositeSideOrder.getLeaveQty());
        execution = Optional.of(new Execution(order.getOrderId(), order.getSide(),
                oppositeSideOrder.getOrderId(), execPrice, execQty));
        order.updateLeaveQty(order.getLeaveQty() - execQty);
        oppositeSideOrder.updateLeaveQty(oppositeSideOrder.getLeaveQty() - execQty);
        return execution;
    }

    private void updateOrderBook(final OrderBook orderBook, final MatchResult matchResult) {
        matchResult.fullfilledOppositeSideOrders.forEach(orderBook::removeOrder);
    }

    public MatchEngineCmdReply<?> cancelOrder(final Order order) {
        final OrderBook orderBook = orderBooks.get(order.getSecurityId());
        if(orderBook == null || !orderBook.containsOrder(order)){
            return new MatchEngineCmdReply<>(MatchEngineCmdReplyType.Fail, ORDER_DOES_NOT_EXIST);
        }
        orderBook.removeOrder(order);
        return new MatchEngineCmdReply<>(MatchEngineCmdReplyType.Success);
    }

    public void printBooks(){
        orderBooks.forEach((key, value)->{
            System.out.printf("---------------order book for '%s'------------------%n", key);
            System.out.println(value);
        });
    }
}
