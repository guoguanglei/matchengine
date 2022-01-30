package com.test.me;

public class MatchEngineCmd<T> {
    public final MatchEngineCmdType matchEngineCmdType;
    public final T payload;

    public MatchEngineCmd(MatchEngineCmdType matchEngineCmdType, T payload) {
        this.matchEngineCmdType = matchEngineCmdType;
        this.payload = payload;
    }

    public MatchEngineCmd(MatchEngineCmdType matchEngineCmdType) {
        this.matchEngineCmdType = matchEngineCmdType;
        this.payload = null;
    }
}
