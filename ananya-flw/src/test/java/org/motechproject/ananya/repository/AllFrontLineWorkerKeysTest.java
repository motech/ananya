package org.motechproject.ananya.repository;


import org.ektorp.UpdateConflictException;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.domain.FrontLineWorkerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;

import static junit.framework.Assert.assertNotNull;

public class AllFrontLineWorkerKeysTest extends SpringBaseIT {

    @Autowired
    private AllFrontLineWorkerKeys allFrontLineWorkerKeys;

    @Before
    public void setUp(){
        allFrontLineWorkerKeys.removeAll();
    }

    @Test
    @ExpectedException(UpdateConflictException.class)
    public void shouldThrowUpIfDuplicateExists() {
        FrontLineWorkerKey worker1 = new FrontLineWorkerKey("1234");
        FrontLineWorkerKey worker2 = new FrontLineWorkerKey("1234");

        allFrontLineWorkerKeys.add(worker1);
        allFrontLineWorkerKeys.add(worker2);
    }

    @Test
    public void shouldNotThrowUpForUniqueCallerIds() {
        FrontLineWorkerKey worker1 = new FrontLineWorkerKey("1234");
        FrontLineWorkerKey worker2 = new FrontLineWorkerKey("4321");
        allFrontLineWorkerKeys.add(worker1);
        allFrontLineWorkerKeys.add(worker2);

        assertNotNull(allFrontLineWorkerKeys.get("1234"));
        assertNotNull(allFrontLineWorkerKeys.get("4321"));
    }
}
