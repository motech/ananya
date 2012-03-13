package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Operator;
import org.motechproject.ananya.repository.AllOperators;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

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
        when(allOperators.findByName(operator)).thenReturn(new Operator(operator, 10));

        Integer usage = operatorService.findMaximumUsageFor(operator);
        assertEquals(new Integer(10), usage);
    }

    @Test
    public void shouldDelegateToAllOperatorsRepositoryToFetchAllOperators(){
        List<Operator> expectedOperators = Arrays.asList(new Operator());
        when(allOperators.getAll()).thenReturn(expectedOperators);

        List<Operator> operators = operatorService.getAllOperators();
        assertEquals(expectedOperators,operators);
    }


}
