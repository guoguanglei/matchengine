package com.test.adapter;

import com.test.me.MatchEngineCmdType;
import com.test.me.MatchEngineCmd;
import com.test.me.MatchEngine;

/**
 * Receive order from console...
 */
public class ConsoleMsgAdapter implements MsgAdapter {

    final MatchEngine matchEngine;

    public ConsoleMsgAdapter(final MatchEngine matchEngine){
        this.matchEngine = matchEngine;
    }

    @Override
    public void onMsg(OrderMsg orderMsg) {
        switch (orderMsg.msgType){
            case Add:
                matchEngine.offer(new MatchEngineCmd<>(MatchEngineCmdType.AddOrder, orderMsg));
                break;

            case Cancel:
                matchEngine.offer(new MatchEngineCmd<>(MatchEngineCmdType.CancelOrder, orderMsg));
                break;
        }
    }
}
