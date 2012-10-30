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
    public void shouldDelegateToAllOperatorsRepositoryToFetchAllOperators() {
        List<Operator> expectedOperators = Arrays.asList(new Operator());
        when(allOperators.getAll()).thenReturn(expectedOperators);

        List<Operator> operators = operatorService.getAllOperators();
        assertEquals(expectedOperators, operators);
    }

    @Test
    public void shouldGetTheUsageByPulseInMilliSecGivenDurationInMilliSec(){
        String operatorName = "airtel";
        int durationInMilliSec = 121000;
        Integer expectedUsageByPulseInMilliSec = 180000;
        when(allOperators.findByName(operatorName)).thenReturn(new Operator(operatorName, 5000000, 60000));
        Integer actualUsageByPulseInMilliSec = operatorService.usageByPulseInMilliSec(operatorName, durationInMilliSec);

        assertEquals(expectedUsageByPulseInMilliSec, actualUsageByPulseInMilliSec);
    }
}
