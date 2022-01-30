package com.test.me;

public class MatchEngineCmdReply<T> {
    public final MatchEngineCmdReplyType engineReplyType;
    public final T payload;

    public MatchEngineCmdReply(MatchEngineCmdReplyType engineReplyType, T payload) {
        this.engineReplyType = engineReplyType;
        this.payload = payload;
    }

    public MatchEngineCmdReply(MatchEngineCmdReplyType engineReplyType) {
        this.engineReplyType = engineReplyType;
        this.payload = null;
    }
}
