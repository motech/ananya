package org.motechproject.ananya.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.SMSLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class AllSMSLogsIT extends SpringIntegrationTest{

    @Autowired
    AllSMSLogs allSMSLogs;

    @Test
    public void shouldFindByCallId() {
        String callId = "callId";
        SMSLog entity = new SMSLog(callId, "", "", 0,"language");
        allSMSLogs.add(entity);
        markForDeletion(entity);

        assertNotNull(allSMSLogs.findByCallId(callId));
    }

    @Test
    public void shouldDeleteAllSMSLogsWithInvalidMsisdns() {
        String callId = "callId";
        String invalidCallerId1 = "123E+10";
        String invalidCallerId2 = "123E10";
        String validCallerId = "1234";
        SMSLog entity1 = new SMSLog(callId, invalidCallerId1, "", 0,"");
        SMSLog entity2 = new SMSLog(callId, invalidCallerId2, "", 0,"");
        SMSLog entity3 = new SMSLog(callId, validCallerId, "", 0,"");
        allSMSLogs.add(entity1);
        allSMSLogs.add(entity2);
        allSMSLogs.add(entity3);
        markForDeletion(entity3);

        allSMSLogs.deleteSMSLogsForInvalidMsisdns();

        List<SMSLog> actualSmsLogs = allSMSLogs.getAll();
        assertEquals(1, actualSmsLogs.size());
        assertEquals(validCallerId, actualSmsLogs.get(0).getCallerId());
    }
    
    @Test
    public void shouldFindByCallerIdAndAttempts(){
        String callId = "callId";
        String callerId = "9988776655";
        int courseAttempts = 2;
        String language ="language";
        SMSLog entity = new SMSLog(callId, callerId, "", courseAttempts, language);
        allSMSLogs.add(entity);
        markForDeletion(entity);

        assertNotNull(allSMSLogs.findByCallerIdAndAttempts(callerId, courseAttempts));
    }
}
