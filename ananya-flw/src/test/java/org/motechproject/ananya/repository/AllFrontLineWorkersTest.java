package org.motechproject.ananya.repository;

import org.junit.Test;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class AllFrontLineWorkersTest extends FLWSpringIntegrationTest {
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Test
    public void shouldAddAndRetrieveRecord() {
        FrontLineWorker frontLineWorker = new FrontLineWorker("name");
        allFrontLineWorkers.add(frontLineWorker);

        markForDeletion(frontLineWorker);
        List<FrontLineWorker> frontLineWorkers = allFrontLineWorkers.getAll();
        assertEquals("name", frontLineWorkers.get(0).name());
    }
}
