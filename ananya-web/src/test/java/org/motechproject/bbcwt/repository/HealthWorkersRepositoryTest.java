package org.motechproject.bbcwt.repository;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class HealthWorkersRepositoryTest extends SpringIntegrationTest {
    @Autowired
    private HealthWorkersRepository healthWorkersRepository;

    private HealthWorker testHealthWorker;
    private String callerId;

    @Before
    public void setUp() {
        callerId = "9999988888";
        testHealthWorker=new HealthWorker(callerId);
        healthWorkersRepository.add(testHealthWorker);
        markForDeletion(testHealthWorker);
    }

    @Test
    public void testShouldPersistHealthWorker() {
        HealthWorker healthWorker = healthWorkersRepository.get(testHealthWorker.getId());

        assertNotNull(healthWorker);
        assertEquals("Caller Id should have been saved.", callerId, healthWorker.getCallerId());
    }

    @Test
    public void testShouldReturnHealthWorkerForAGivenCallerId() {
        HealthWorker healthWorker = healthWorkersRepository.findByCallerId(callerId);

        assertNotNull(healthWorker);
        assertEquals("Caller Id should be the same as what has been queried for.", callerId, healthWorker.getCallerId());
    }
}