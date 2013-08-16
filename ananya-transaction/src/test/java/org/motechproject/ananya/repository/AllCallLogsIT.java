package org.motechproject.ananya.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CallLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class AllCallLogsIT extends SpringIntegrationTest{

    @Autowired
    private AllCallLogs allCallLogs;

    @Test
    public void shouldDeleteFLWWithInvalidMsisdn() {
        String invalidCallerId1 = "123E+11";
        String invalidCallerId2 = "123E2";
        String validCallerId = "123";
        String callId = "123456";
        CallLog callLog1 = new CallLog(callId, invalidCallerId1, "321");
        CallLog callLog2 = new CallLog(callId, invalidCallerId2, "321");
        CallLog callLog3 = new CallLog(callId, validCallerId, "321");

        allCallLogs.add(callLog1);
        allCallLogs.add(callLog2);
        allCallLogs.add(callLog3);
        markForDeletion(callLog3);

        allCallLogs.deleteCallLogsForInvalidMsisdns();

        List<CallLog> actualCallLogs = allCallLogs.getAll();
        assertEquals(1, actualCallLogs.size());
        assertEquals(validCallerId, actualCallLogs.get(0).getCallerId());
    }

    @Test
    public void shouldFindByCallId() {
        String callerId = "123";
        String callId = "123456";
        CallLog callLog = new CallLog(callId, callerId, "321");
        allCallLogs.add(callLog);
        markForDeletion(callLog);

        CallLog callLogFromDB = allCallLogs.findByCallId(callId);
        assertEquals(callerId, callLogFromDB.getCallerId());
    }

    @Test
    public void shouldReturnNullIfTheCallLogIsNotPresent() {
        String callId = "123456";

        CallLog callLogFromDB = allCallLogs.findByCallId(callId);

        assertNull(callLogFromDB);
    }
}
