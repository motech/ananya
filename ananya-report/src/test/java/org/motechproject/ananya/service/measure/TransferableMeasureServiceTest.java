package org.motechproject.ananya.service.measure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.domain.measure.TransferableMeasure;
import org.motechproject.ananya.service.dimension.FrontLineWorkerHistoryService;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TransferableMeasureServiceTest extends TransferableMeasureService {

    @Mock
    private TransferableMeasure transferableMeasure;

    @Mock
    private FrontLineWorkerHistoryService mockFrontLineWorkerHistoryService;

    @Test
    public void shouldAddHistory() {
        ReflectionTestUtils.setField(this, "frontLineWorkerHistoryService", mockFrontLineWorkerHistoryService);

        super.addFlwHistory(transferableMeasure);

        verify(mockFrontLineWorkerHistoryService).addHistory(transferableMeasure);
    }
}
