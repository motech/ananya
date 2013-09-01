package org.motechproject.ananya.domain.dimension;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;

public class FrontLineWorkerHistoryTest {
    @Test
    public void shouldRetrieveId() {
        FrontLineWorkerHistory frontLineWorkerHistory = new FrontLineWorkerHistory();
        Integer id = 1;
        ReflectionTestUtils.setField(frontLineWorkerHistory, "id", id);
        assertEquals(id, frontLineWorkerHistory.id());
    }
}
