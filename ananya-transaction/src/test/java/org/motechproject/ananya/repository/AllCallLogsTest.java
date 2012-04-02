package org.motechproject.ananya.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CallLogList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public class AllCallLogsTest extends SpringIntegrationTest{

    @Autowired
    private AllCallLogList allCallLogs;

    @Test
    public void shouldFindByCallId() {
        String callerId = "123";
        String callId = "123456";
        CallLogList callLogList = new CallLogList(callId, callerId);
        allCallLogs.add(callLogList);
        markForDeletion(callLogList);

        CallLogList callLogListFromDB = allCallLogs.findByCallId(callId);
        assertEquals(callerId, callLogListFromDB.getCallerId());
    }
}
