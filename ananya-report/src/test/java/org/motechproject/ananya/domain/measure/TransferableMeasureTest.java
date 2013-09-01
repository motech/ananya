package org.motechproject.ananya.domain.measure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerHistory;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransferableMeasureTest extends TransferableMeasure {

    @Mock
    private FrontLineWorkerHistory frontLineWorkerHistory;

    @Test
    public void shouldAddHistory() {
        int flwHistoryId = 1;
        when(frontLineWorkerHistory.id()).thenReturn(flwHistoryId);

        super.addFlwHistory(frontLineWorkerHistory);

        verify(frontLineWorkerHistory).id();
        assertEquals(flwHistoryId, ReflectionTestUtils.getField(this, "flwHistoryId"));
    }

    @Test
    public void shouldRetrieveFlwId() {
        Integer flwId = 1;
        FrontLineWorkerDimension flwDimension = new FrontLineWorkerDimension();
        flwDimension.setId(flwId);
        super.frontLineWorkerDimension = flwDimension;

        assertEquals(flwId, super.flwId());
    }
}
