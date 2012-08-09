package org.motechproject.ananya.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.SMSLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class AllSMSLogsTest extends SpringIntegrationTest{

    @Autowired
    AllSMSLogs allSMSLogs;

    @Test
    public void shouldFindByCallId() {
        String callId = "callId";
        SMSLog entity = new SMSLog(callId, "", "", 0);
        allSMSLogs.add(entity);
        markForDeletion(entity);

        assertNotNull(allSMSLogs.findByCallId(callId));
    }
    
    @Test
    public void shouldFindByCallerIdAndAttempts(){
        String callId = "callId";
        String callerId = "9988776655";
        int courseAttempts = 2;
        SMSLog entity = new SMSLog(callId, callerId, "", courseAttempts);
        allSMSLogs.add(entity);
        markForDeletion(entity);

        assertNotNull(allSMSLogs.findByCallerIdAndAttempts(callerId, courseAttempts));
    }
}
