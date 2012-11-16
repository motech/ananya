package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Operator;
import org.motechproject.ananya.repository.AllOperators;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OperatorServiceTest {

    private OperatorService operatorService;
    @Mock
    private AllOperators allOperators;

    @Before
    public void setUp() {
        initMocks(this);
        operatorService = new OperatorService(allOperators);
    }

    @Test
    public void shouldCallAllOperatorsToFetchOperator() {
        String operator = "airtel";
        when(allOperators.findByName(operator)).thenReturn(new Operator(operator, 10, 60));

        Integer usage = operatorService.findMaximumUsageFor(operator);
        assertEquals(new Integer(10), usage);
    }

    @Test
    public void shouldGetTheUsageByPulseInMilliSecGivenDurationInMilliSec() {
        String operatorName = "airtel";
        int durationInMilliSec = 121000;
        Integer expectedUsageByPulseInMilliSec = 180000;
        when(allOperators.findByName(operatorName)).thenReturn(new Operator(operatorName, 5000000, 60000));
        Integer actualUsageByPulseInMilliSec = operatorService.usageByPulseInMilliSec(operatorName, durationInMilliSec);

        assertEquals(expectedUsageByPulseInMilliSec, actualUsageByPulseInMilliSec);
    }

    @Test
    public void shouldGetTheUsageInPulse() {
        String operatorName = "airtel";
        int durationInMilliSec = 121000;
        Integer expectedUsageInPulse = 3;
        when(allOperators.findByName(operatorName)).thenReturn(new Operator(operatorName, 5000000, 60000));
        Integer actualUsageInPulse = operatorService.usageInPulse(operatorName, durationInMilliSec);

        assertEquals(expectedUsageInPulse, actualUsageInPulse);
    }

    @Test
    public void shouldHandleZeroDurationForPulseInMilliSec() {
        String operatorName = "airtel";
        Integer expectedDurationInPulse = 60000;
        when(allOperators.findByName(operatorName)).thenReturn(new Operator(operatorName, 5000000, 60000));

        Integer durationInPulse = operatorService.usageByPulseInMilliSec(operatorName, 0);

        assertEquals(expectedDurationInPulse, durationInPulse);
    }

    @Test
    public void shouldHandleZeroDurationForUsageInPulse() {
        String operatorName = "airtel";
        Integer expectedDurationInPulse = 1;
        when(allOperators.findByName(operatorName)).thenReturn(new Operator(operatorName, 5000000, 60000));

        Integer durationInPulse = operatorService.usageInPulse(operatorName, 0);

        assertEquals(expectedDurationInPulse, durationInPulse);
    }
}
