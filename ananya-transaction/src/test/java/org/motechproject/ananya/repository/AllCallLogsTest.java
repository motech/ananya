package org.motechproject.ananya.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CallLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public class AllCallLogsTest extends SpringIntegrationTest{

    @Autowired
    private AllCallLogs allCallLogs;

    @Test
    public void shouldFindByCallId() {
        String callerId = "123";
        String callId = "123456";
        CallLog callLog = new CallLog(callId, callerId);
        allCallLogs.add(callLog);
        markForDeletion(callLog);

        CallLog callLogFromDB = allCallLogs.findByCallId(callId);
        assertEquals(callerId, callLogFromDB.getCallerId());
    }
}
