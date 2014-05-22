package org.motechproject.ananya.repository;

import org.junit.Test;
import org.motechproject.ananya.domain.Operator;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class AllOperatorsTest extends SpringBaseIT {
    @Autowired
    private AllOperators allOperators;

    @Test
    public void shouldAddAnOperator() {
        String operatorName = "operator";
        int allowedUsagePerMonth = 25;
        Operator operator = new Operator(operatorName, allowedUsagePerMonth, 0, 60000);

        allOperators.add(operator);
        markForDeletion(operator);

        Operator byOperatorName = allOperators.findByName(operatorName, null);
        assertEquals(allowedUsagePerMonth, (int )byOperatorName.getAllowedUsagePerMonth());
        assertEquals(0, (int )byOperatorName.getStartOfPulseInMilliSec());
        assertEquals(60000, (int )byOperatorName.getEndOfPulseInMilliSec());
    }
    
    @Test
    public void shouldAddAnOperatorWithCircle() {
        String operatorName = "operator";
        String circle = "circle";
        int allowedUsagePerMonth = 30;
        Operator operator = new Operator(operatorName, allowedUsagePerMonth, 0, 60000, circle);

        allOperators.add(operator);
        markForDeletion(operator);

        Operator byOperatorName = allOperators.findByName(operatorName, circle);
        assertEquals(allowedUsagePerMonth, (int )byOperatorName.getAllowedUsagePerMonth());
        assertEquals(0, (int )byOperatorName.getStartOfPulseInMilliSec());
        assertEquals(60000, (int )byOperatorName.getEndOfPulseInMilliSec());
    }
}
