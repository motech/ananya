package org.motechproject.ananya.service.dimension;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerHistory;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.domain.measure.TransferableMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerHistory;

import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FrontLineWorkerHistoryServiceTest {

    @Mock
    private AllFrontLineWorkerHistory allFrontLineWorkerHistory;

    @Mock
    private FrontLineWorkerHistory currentFlwHistory;

    @Mock
    private TransferableMeasure transferableMeasure;

    private FrontLineWorkerHistoryService frontLineWorkerHistoryService;

    @Before
    public void setUp() {
        frontLineWorkerHistoryService = new FrontLineWorkerHistoryService(allFrontLineWorkerHistory);
    }

    @Test
    public void shouldAddFlwHistory() {
        int flwId = 1;
        when(transferableMeasure.flwId()).thenReturn(flwId);
        FrontLineWorkerHistory frontLineWorkerHistory = new FrontLineWorkerHistory();
        when(allFrontLineWorkerHistory.getCurrent(flwId)).thenReturn(frontLineWorkerHistory);

        frontLineWorkerHistoryService.addHistory(transferableMeasure);

        verify(transferableMeasure).flwId();
        verify(allFrontLineWorkerHistory).getCurrent(flwId);
        verify(transferableMeasure).addFlwHistory(frontLineWorkerHistory);
    }

    @Test
    public void shouldCreateFlwHistory() {
        int flwDimensionId = 1;
        FrontLineWorkerDimension frontLineWorkerDimension = getFrontLineWorkerDimension(flwDimensionId);
        RegistrationMeasure registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, new LocationDimension(), new TimeDimension(), "callId");
        when(allFrontLineWorkerHistory.getCurrent(flwDimensionId)).thenReturn(currentFlwHistory);

        frontLineWorkerHistoryService.create(registrationMeasure);

        InOrder inOrder = inOrder(allFrontLineWorkerHistory, currentFlwHistory);
        inOrder.verify(allFrontLineWorkerHistory).getCurrent(flwDimensionId);
        inOrder.verify(currentFlwHistory).markAsOld();
        inOrder.verify(allFrontLineWorkerHistory).createOrUpdate(currentFlwHistory);

        ArgumentCaptor<FrontLineWorkerHistory> frontLineWorkerHistoryArgumentCaptor = ArgumentCaptor.forClass(FrontLineWorkerHistory.class);
        inOrder.verify(allFrontLineWorkerHistory).createOrUpdate(frontLineWorkerHistoryArgumentCaptor.capture());
        FrontLineWorkerHistory argument = frontLineWorkerHistoryArgumentCaptor.getValue();
        assertTrue(reflectionEquals(argument, new FrontLineWorkerHistory(registrationMeasure), new String[]{"timestamp"}));
    }

    private FrontLineWorkerDimension getFrontLineWorkerDimension(int flwDimensionId) {
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension();
        frontLineWorkerDimension.setId(flwDimensionId);
        return frontLineWorkerDimension;
    }

    @Test
    public void shouldMarkCurrentHistoryAsOld() {
        int flwDimensionId = 1;
        FrontLineWorkerDimension frontLineWorkerDimension = getFrontLineWorkerDimension(flwDimensionId);
        when(allFrontLineWorkerHistory.getCurrent(flwDimensionId)).thenReturn(currentFlwHistory);

        frontLineWorkerHistoryService.markCurrentAsOld(frontLineWorkerDimension);

        verify(allFrontLineWorkerHistory).getCurrent(flwDimensionId);
        verify(currentFlwHistory).markAsOld();
        verify(allFrontLineWorkerHistory).createOrUpdate(currentFlwHistory);
    }

    @Test
    public void shouldMarkCurrentHistoryAsOldOnlyIfItExists() {
        int flwDimensionId = 1;
        FrontLineWorkerDimension frontLineWorkerDimension = getFrontLineWorkerDimension(flwDimensionId);
        when(allFrontLineWorkerHistory.getCurrent(flwDimensionId)).thenReturn(null);

        frontLineWorkerHistoryService.markCurrentAsOld(frontLineWorkerDimension);

        verify(allFrontLineWorkerHistory).getCurrent(flwDimensionId);
        verify(allFrontLineWorkerHistory, never()).createOrUpdate(any(FrontLineWorkerHistory.class));
    }

    @Test
    public void shouldUpdateFlwHistoryWhenOperatorIsNotSet() {
        String operator = "ABC";
        FrontLineWorkerDimension flw = new FrontLineWorkerDimension();
        flw.setId(1);
        flw.setOperator(operator);
        FrontLineWorkerHistory frontLineWorkerHistory = new FrontLineWorkerHistory();
        frontLineWorkerHistory.setOperator(null);
        when(allFrontLineWorkerHistory.getCurrent(flw.getId())).thenReturn(frontLineWorkerHistory);

        frontLineWorkerHistoryService.updateOperatorIfNotSet(flw);

        assertEquals(operator, frontLineWorkerHistory.getOperator());
        verify(allFrontLineWorkerHistory).getCurrent(flw.getId());
        verify(allFrontLineWorkerHistory).createOrUpdate(frontLineWorkerHistory);
    }

    @Test
    public void shouldNotUpdateFlwHistoryWhenOperatorIsThere() {
        String operator = "ABC";
        FrontLineWorkerDimension flw = new FrontLineWorkerDimension();
        flw.setId(1);
        FrontLineWorkerHistory frontLineWorkerHistory = new FrontLineWorkerHistory();
        frontLineWorkerHistory.setOperator(operator);
        when(allFrontLineWorkerHistory.getCurrent(flw.getId())).thenReturn(frontLineWorkerHistory);

        frontLineWorkerHistoryService.updateOperatorIfNotSet(flw);

        verify(allFrontLineWorkerHistory).getCurrent(flw.getId());
        verify(allFrontLineWorkerHistory, never()).createOrUpdate(frontLineWorkerHistory);
    }

}
