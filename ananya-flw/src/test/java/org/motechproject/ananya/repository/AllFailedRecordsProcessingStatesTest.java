package org.motechproject.ananya.repository;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.domain.FailedRecordsProcessingState;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AllFailedRecordsProcessingStatesTest extends SpringBaseIT {

    @Autowired
    private AllFailedRecordsProcessingStates allFailedRecordsProcessingState;

    @Before
    @After
    public void setUpAndTearDown() {
        allFailedRecordsProcessingState.removeAll();
    }

    @Test
    public void shouldAddNewFailedRecordProcessingState() {
        DateTime processedDate = DateTime.now();
        FailedRecordsProcessingState failedRecordsProcessingState = new FailedRecordsProcessingState(processedDate);
        allFailedRecordsProcessingState.add(failedRecordsProcessingState);

        FailedRecordsProcessingState fromDb = allFailedRecordsProcessingState.getAll().get(0);
        assertNotNull(fromDb);
        assertEquals(processedDate.getDayOfMonth(), fromDb.getLastProcessedDate().getDayOfMonth());
        assertEquals(processedDate.getMonthOfYear(), fromDb.getLastProcessedDate().getMonthOfYear());
        assertEquals(processedDate.getYear(), fromDb.getLastProcessedDate().getYear());
    }
}
