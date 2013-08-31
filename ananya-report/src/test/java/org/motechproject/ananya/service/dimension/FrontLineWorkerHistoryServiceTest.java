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
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerHistory;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FrontLineWorkerHistoryServiceTest {

    @Mock
    private AllFrontLineWorkerHistory allFrontLineWorkerHistory;

    @Mock
    private FrontLineWorkerHistory currentFlwHistory;

    private FrontLineWorkerHistoryService frontLineWorkerHistoryService;

    @Before
    public void setUp() {
        frontLineWorkerHistoryService = new FrontLineWorkerHistoryService(allFrontLineWorkerHistory);
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
        inOrder.verify(currentFlwHistory).markOld();
        inOrder.verify(allFrontLineWorkerHistory).createOrUpdate(currentFlwHistory);

        ArgumentCaptor<FrontLineWorkerHistory> frontLineWorkerHistoryArgumentCaptor = ArgumentCaptor.forClass(FrontLineWorkerHistory.class);
        inOrder.verify(allFrontLineWorkerHistory).createOrUpdate(frontLineWorkerHistoryArgumentCaptor.capture());
        assertTrue(frontLineWorkerHistoryArgumentCaptor.getValue().isSame(new FrontLineWorkerHistory(registrationMeasure)));
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
        verify(currentFlwHistory).markOld();
        verify(allFrontLineWorkerHistory).createOrUpdate(currentFlwHistory);
    }
}
