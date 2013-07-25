package org.motechproject.ananya.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.RegistrationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class AllRegistrationLogsIT extends SpringIntegrationTest {

    @Autowired
    private AllRegistrationLogs allRegistrationLogs;

    @Test
    public void shouldFindByCallId() {
        String callerId = "123";
        String callId = "123456";
        String operator = "airtel";
        String circle = "circle";
        RegistrationLog registrationLog = new RegistrationLog(callId, callerId, operator, circle);
        allRegistrationLogs.add(registrationLog);

        RegistrationLog registrationLogFromDB = allRegistrationLogs.findByCallId(callId);
        assertEquals(callerId, registrationLogFromDB.getCallerId());
        assertEquals(operator, registrationLogFromDB.getOperator());

        allRegistrationLogs.deleteFor(callId);
        assertNull(allRegistrationLogs.findByCallId(callId));
    }

    @Test
    public void shouldDeleteRegistrationLogsWithInvalidMsisdn() {
        String invalidCallerId1 = "123E+10";
        String invalidCallerId2 = "123E2";
        String validCallerId = "12334";
        RegistrationLog registrationLog1 = new RegistrationLog(null, invalidCallerId1, null, null);
        RegistrationLog registrationLog2 = new RegistrationLog(null, invalidCallerId2, null, null);
        RegistrationLog registrationLog3 = new RegistrationLog(null, validCallerId, null, null);

        allRegistrationLogs.add(registrationLog1);
        allRegistrationLogs.add(registrationLog2);
        allRegistrationLogs.add(registrationLog3);
        markForDeletion(registrationLog3);

        allRegistrationLogs.deleteRegistrationLogsForInvalidMsisdns();

        List<RegistrationLog> actualRegistrationLogs = allRegistrationLogs.getAll();
        assertEquals(1, actualRegistrationLogs.size());
        assertEquals(validCallerId, actualRegistrationLogs.get(0).getCallerId());
    }
}
