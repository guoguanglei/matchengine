package com.test.me;

import java.util.LinkedList;
import java.util.List;

public class MatchResult {
    public final List<Execution> executionList;

    public final List<Order> fullfilledOppositeSideOrders;

    public MatchResult() {
        this.executionList = new LinkedList<>();
        this.fullfilledOppositeSideOrders = new LinkedList<>();
    }

    public void addExecution(Execution execution){
        if(execution != null) {
            executionList.add(execution);
        }
    }

    @Override
    public String toString() {
        return "Executions{\n" +
                "executionList=\n" + executionList +
                "}\n";
    }
}
