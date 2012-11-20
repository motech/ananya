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
        when(allOperators.findByName(operator)).thenReturn(new Operator(operator, 10, 0, 60000));

        Integer usage = operatorService.findMaximumUsageFor(operator);
        assertEquals(new Integer(10), usage);
    }

    @Test
    public void shouldGetTheUsageByPulseInMilliSecGivenDurationInMilliSecForAOperatorWithStartOfPulseAtZero() {
        String operatorName = "airtel";
        int durationInMilliSec = 121000;
        Integer expectedUsageByPulseInMilliSec = 180000;
        when(allOperators.findByName(operatorName)).thenReturn(new Operator(operatorName, 5000000, 0, 60000));
        Integer actualUsageByPulseInMilliSec = operatorService.usageByPulseInMilliSec(operatorName, durationInMilliSec);

        assertEquals(expectedUsageByPulseInMilliSec, actualUsageByPulseInMilliSec);
    }

    @Test
    public void shouldGetTheUsageByPulseInMilliSecGivenDurationInMilliSecForAOperatorWithStartOfPulseBeingNonZero() {
        String operatorName = "airtel";
        int durationInMilliSec = 59100;
        Integer expectedUsageByPulseInMilliSec = 60000;
        when(allOperators.findByName(operatorName)).thenReturn(new Operator(operatorName, 5000000, 500, 60500));
        Integer actualUsageByPulseInMilliSec = operatorService.usageByPulseInMilliSec(operatorName, durationInMilliSec);

        assertEquals(expectedUsageByPulseInMilliSec, actualUsageByPulseInMilliSec);
    }

    @Test
    public void shouldGetTheUsageByPulseInMilliSecGivenDurationLessThanStartOfPulse() {
        String operatorName = "airtel";
        int durationInMilliSec = 400;
        Integer expectedUsageByPulseInMilliSec = 0;
        when(allOperators.findByName(operatorName)).thenReturn(new Operator(operatorName, 5000000, 500, 60500));
        Integer actualUsageByPulseInMilliSec = operatorService.usageByPulseInMilliSec(operatorName, durationInMilliSec);

        assertEquals(expectedUsageByPulseInMilliSec, actualUsageByPulseInMilliSec);
    }

    @Test
    public void shouldGetTheUsageByPulseInMilliSecGivenDurationForPerSecondPulse() {
        String operatorName = "airtel";
        int durationInMilliSec = 58900;
        Integer expectedUsageByPulseInMilliSec = 59000;
        when(allOperators.findByName(operatorName)).thenReturn(new Operator(operatorName, 5000000, 500, 1500));
        Integer actualUsageByPulseInMilliSec = operatorService.usageByPulseInMilliSec(operatorName, durationInMilliSec);

        assertEquals(expectedUsageByPulseInMilliSec, actualUsageByPulseInMilliSec);
    }

    @Test
    public void shouldGetTheUsageInPulse() {
        String operatorName = "airtel";
        int durationInMilliSec = 121000;
        Integer expectedUsageInPulse = 3;
        when(allOperators.findByName(operatorName)).thenReturn(new Operator(operatorName, 5000000, 0, 60000));
        Integer actualUsageInPulse = operatorService.usageInPulse(operatorName, durationInMilliSec);

        assertEquals(expectedUsageInPulse, actualUsageInPulse);
    }

    @Test
    public void shouldGetTheUsageByPulse_GivenDurationInMilliSecForAOperator_WithStartOfPulseBeingNonZero() {
        String operatorName = "airtel";
        int durationInMilliSec = 59100;
        Integer expectedUsageInPulse = 1;
        when(allOperators.findByName(operatorName)).thenReturn(new Operator(operatorName, 5000000, 500, 60500));
        Integer actualUsageInPulse = operatorService.usageInPulse(operatorName, durationInMilliSec);

        assertEquals(expectedUsageInPulse, actualUsageInPulse);
    }

    @Test
    public void shouldGetTheUsageByPulse_GivenDurationLessThanStartOfPulse() {
        String operatorName = "airtel";
        int durationInMilliSec = 400;
        Integer expectedUsageInPulse = 0;
        when(allOperators.findByName(operatorName)).thenReturn(new Operator(operatorName, 5000000, 500, 60500));
        Integer actualUsageInPulse = operatorService.usageInPulse(operatorName, durationInMilliSec);

        assertEquals(expectedUsageInPulse, actualUsageInPulse);
    }

    @Test
    public void shouldGetTheUsageByPulse_GivenDurationForPerSecondPulse() {
        String operatorName = "airtel";
        int durationInMilliSec = 58900;
        Integer expectedUsageInPulse = 59;
        when(allOperators.findByName(operatorName)).thenReturn(new Operator(operatorName, 5000000, 500, 1500));
        Integer actualUsageInPulse = operatorService.usageInPulse(operatorName, durationInMilliSec);

        assertEquals(expectedUsageInPulse, actualUsageInPulse);
    }
}
