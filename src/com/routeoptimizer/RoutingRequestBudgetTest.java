package com.routeoptimizer;

public class RoutingRequestBudgetTest {

    public static void main(String[] args) {
        System.out.println("Running RoutingRequestBudgetTest...");
        RoutingRequestBudget budget = new RoutingRequestBudget(10);
        
        for (int i = 0; i < 10; i++) {
            if (!budget.requestExternalApi()) {
                throw new AssertionError("Budget should allow up to 10 requests");
            }
        }
        
        if (budget.requestExternalApi()) {
            throw new AssertionError("Budget should block 11th request");
        }
        
        if (!budget.isBudgetExceeded()) {
            throw new AssertionError("Budget should report as exceeded");
        }
        
        if (budget.getExternalRequests() != 11) {
            throw new AssertionError("Expected 11 external requests counted");
        }
        
        budget.reset();
        
        if (budget.isBudgetExceeded()) {
            throw new AssertionError("Budget should be reset");
        }
        
        if (budget.getExternalRequests() != 0) {
            throw new AssertionError("Expected 0 external requests counted after reset");
        }
        
        System.out.println("RoutingRequestBudgetTest: PASS");
    }
}
