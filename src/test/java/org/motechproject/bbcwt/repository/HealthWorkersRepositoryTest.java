package org.motechproject.bbcwt.repository;

import org.junit.Test;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class HealthWorkersRepositoryTest extends SpringIntegrationTest {

    @Autowired
    private HealthWorkersRepository healthWorkersRepository;

    @Test
    public void testShouldPersistHealthWorker() {
        String callerId = "9999988888";
        HealthWorker testHealthWorker = new HealthWorker(callerId);
        healthWorkersRepository.add(testHealthWorker);

        HealthWorker healthWorker = healthWorkersRepository.get(testHealthWorker.getId());

        assertNotNull(healthWorker);
        assertEquals("Caller Id should have been saved.", callerId, healthWorker.getCallerId());

        markForDeletion(healthWorker);
    }

}