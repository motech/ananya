package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerDimensionServiceTest {

    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;

    @Mock
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerDimensionService = new FrontLineWorkerDimensionService(allFrontLineWorkerDimensions);
    }

    @Test
    public void shouldReturnAllUnregisteredFrontLineWorkerDimensions() {
        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<FrontLineWorkerDimension>();
        when(allFrontLineWorkerDimensions.getAllUnregistered()).thenReturn(frontLineWorkerDimensions);

        List<FrontLineWorkerDimension> allUnregistered = frontLineWorkerDimensionService.getAllUnregistered();

        assertEquals(frontLineWorkerDimensions, allUnregistered);
    }
}
