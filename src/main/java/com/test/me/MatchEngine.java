package com.test.me;

import com.test.OrderFactory;
import com.test.adapter.OrderMsg;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class MatchEngine implements Runnable, Closeable {
    private final MatchAlgo matchAlgo;

    private final LinkedBlockingQueue<MatchEngineCmd<?>> matchEngineCmds;

    private final LinkedBlockingQueue<MatchEngineCmdReply<?>> matchEngineCmdReplies;

    private volatile boolean running = false;

    private static final MatchEngine MATCH_ENGINE = new MatchEngine();

    public static MatchEngine getInstance(){
        return MATCH_ENGINE;
    }

    private MatchEngine() {
        this.matchAlgo = new MatchAlgo();
        this.matchEngineCmds = new LinkedBlockingQueue<>();
        this.matchEngineCmdReplies = new LinkedBlockingQueue<>();
    }

    public void offer(final MatchEngineCmd<?> matchEngineCmd) {
        while(!matchEngineCmds.offer(matchEngineCmd)){
            Thread.yield();
        }
    }

    @Override
    public void run(){
        running = true;
        MatchEngineCmd<?> matchEngineCmd;
        OrderMsg orderMsg;
        while(running){
            if((matchEngineCmd = matchEngineCmds.poll()) != null){
                switch (matchEngineCmd.matchEngineCmdType){
                    case AddOrder:
                        orderMsg = (OrderMsg) matchEngineCmd.payload;
                        postProcess(matchAlgo.addOrder(OrderFactory.of(orderMsg)));
                        break;

                    case CancelOrder:
                        orderMsg = (OrderMsg) matchEngineCmd.payload;
                        postProcess(matchAlgo.cancelOrder(OrderFactory.of(orderMsg)));
                        break;

                    case PrintBooks:
                        matchAlgo.printBooks();
                        break;
                }

            }
        }
    }

    public void postProcess(MatchEngineCmdReply<?> matchEngineCmdReply){
        System.out.println(matchEngineCmdReply.payload);
        while(!matchEngineCmdReplies.offer(matchEngineCmdReply)){
            Thread.yield();
        }
    }

    @Override
    public void close() throws IOException {
        running = false;
    }
}
