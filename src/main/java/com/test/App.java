package com.test;

import com.test.adapter.MsgType;
import com.test.adapter.MsgAdapter;
import com.test.adapter.ConsoleMsgAdapter;
import com.test.adapter.OrderMsg;
import com.test.me.MatchEngineCmd;
import com.test.me.MatchEngineCmdType;
import com.test.me.MatchEngine;
import com.test.type.OrderType;
import com.test.type.Side;

import java.util.Scanner;

public class App {
    private static final int SECURITY_ID = 10001;

    public static void main(String[] args){
        try(final MatchEngine matchEngine = MatchEngine.getInstance()){

            Thread thread = new Thread(matchEngine);
            thread.setName("Match Engine");
            thread.start();

            System.out.println("Match Engine is running...");

            MsgAdapter msgAdapter = new ConsoleMsgAdapter(matchEngine);

            OrderMsg orderMsg = new OrderMsg(MsgType.Add, SECURITY_ID,1, 1, Side.Sell, OrderType.Limit, 100, 20.30d);
            msgAdapter.onMsg(orderMsg);

            orderMsg = new OrderMsg(MsgType.Add, SECURITY_ID, 2, 2, Side.Sell, OrderType.Limit,100, 20.25d);
            msgAdapter.onMsg(orderMsg);

            orderMsg = new OrderMsg(MsgType.Add, SECURITY_ID, 3, 3, Side.Sell,OrderType.Limit, 200, 20.30d);
            msgAdapter.onMsg(orderMsg);

            orderMsg = new OrderMsg(MsgType.Add, SECURITY_ID, 4, 4, Side.Buy, OrderType.Limit,100, 20.15d);
            msgAdapter.onMsg(orderMsg);

            orderMsg = new OrderMsg(MsgType.Add, SECURITY_ID, 5, 5, Side.Buy, OrderType.Limit,200, 20.20d);
            msgAdapter.onMsg(orderMsg);

            orderMsg = new OrderMsg(MsgType.Add, SECURITY_ID, 6, 6, Side.Buy, OrderType.Limit,200, 20.15d);
            msgAdapter.onMsg(orderMsg);

            matchEngine.offer(new MatchEngineCmd<>(MatchEngineCmdType.PrintBooks));

            orderMsg = new OrderMsg(MsgType.Add, SECURITY_ID, 7, 7, Side.Buy, OrderType.Limit,250, 20.35d);
            msgAdapter.onMsg(orderMsg);

            matchEngine.offer(new MatchEngineCmd<>(MatchEngineCmdType.PrintBooks));

            orderMsg = new OrderMsg(MsgType.Add, SECURITY_ID, 8, 8, Side.Buy, OrderType.Market,250, 0.0d);
            msgAdapter.onMsg(orderMsg);

            matchEngine.offer(new MatchEngineCmd<>(MatchEngineCmdType.PrintBooks));

            orderMsg = new OrderMsg(MsgType.Add, SECURITY_ID, 9, 9, Side.Sell, OrderType.Limit,250, 20.15d);
            msgAdapter.onMsg(orderMsg);

            matchEngine.offer(new MatchEngineCmd<>(MatchEngineCmdType.PrintBooks));

            orderMsg = new OrderMsg(MsgType.Add, SECURITY_ID, 10, 10, Side.Sell, OrderType.Market,450, 0.0d);
            msgAdapter.onMsg(orderMsg);

            matchEngine.offer(new MatchEngineCmd<>(MatchEngineCmdType.PrintBooks));

            orderMsg = new OrderMsg(MsgType.Add, SECURITY_ID, 11, 11, Side.Buy, OrderType.Market,100, 0.0d);
            msgAdapter.onMsg(orderMsg);

            matchEngine.offer(new MatchEngineCmd<>(MatchEngineCmdType.PrintBooks));

            System.out.println("enter key to exist...");
            final Scanner in = new Scanner(System.in);
            System.out.println(in.nextLine());
            System.out.println("now exit..");
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
