package org.motechproject.ananya.repository;

import org.junit.Test;
import org.motechproject.ananya.domain.Operator;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class AllOperatorsTest extends FrontLineWorkerBaseIT {
    @Autowired
    private AllOperators allOperators;

    @Test
    public void shouldAddAnOperator() {
        String operatorName = "operator";
        int allowedUsagePerMonth = 25;
        Operator operator = new Operator(operatorName, allowedUsagePerMonth);

        allOperators.add(operator);
        markForDeletion(operator);

        Operator byOperatorName = allOperators.findByName(operatorName);
        assertEquals(allowedUsagePerMonth, (int )byOperatorName.getAllowedUsagePerMonth());
    }
}