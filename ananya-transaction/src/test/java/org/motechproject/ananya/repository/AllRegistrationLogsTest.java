package org.motechproject.ananya.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.RegistrationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class AllRegistrationLogsTest extends SpringIntegrationTest {

    @Autowired
    private AllRegistrationLogs allRegistrationLogs;

    @Test
    public void shouldFindByCallId() {
        String callerId = "123";
        String callId = "123456";
        String operator = "airtel";
        String circle = "circle";
        RegistrationLog registrationLog = new RegistrationLog(callId,callerId,operator,circle);
        allRegistrationLogs.add(registrationLog);

        RegistrationLog registrationLogFromDB = allRegistrationLogs.findByCallerId(callerId);
        assertEquals(callerId, registrationLogFromDB.getCallerId());
        assertEquals(operator,registrationLogFromDB.getOperator());

        allRegistrationLogs.deleteFor(callerId);
        assertNull(allRegistrationLogs.findByCallerId(callerId));
    }
}
