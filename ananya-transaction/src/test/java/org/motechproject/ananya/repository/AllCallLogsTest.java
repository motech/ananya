package org.motechproject.ananya.repository;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.CallFlow;
import org.motechproject.ananya.domain.CallLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-transaction.xml")
public class AllCallLogsTest {
    @Autowired
    private AllCallLogs allCallLogs;

    @After
    public void tearDown() {
        allCallLogs.removeAll();
    }

    @Test
    public void shouldSaveCallLogWithOnlyStartTimeIfNotPresent() {
        CallLog callLog = new CallLog("callId", "callerId", CallFlow.CALL, DateTime.now(), null);
        assertThat(callLog.getId(), is(nullValue()));
        allCallLogs.addOrUpdate(callLog);
        assertThat(callLog.getId(), is(notNullValue()));
        CallLog logFromDb = allCallLogs.get(callLog.getId());
        assertThat(logFromDb.getCallId(), is(callLog.getCallId()));
        assertThat(logFromDb.getStartTime(), is(callLog.getStartTime()));
        assertNull(logFromDb.getEndTime());
    }
    
    @Test
    public void shouldUpdateCallLogWithEndTime() {
        DateTime startTime = DateTime.now();
        DateTime endTime = DateTime.now().plusDays(1);
        
        CallLog callLog = new CallLog("callId", "callerId", CallFlow.CALL, startTime, null);
        allCallLogs.add(callLog);

        callLog = new CallLog("callId", "callerId", CallFlow.CALL, null, endTime);
        CallLog logFromDb = allCallLogs.addOrUpdate(callLog);

        assertNotNull(logFromDb.getCallId());
        assertThat(logFromDb.getStartTime(), is(startTime));
        assertThat(logFromDb.getEndTime(), is(endTime));
    }

    @Test
    public void shouldUpdateCallLogWithStartTime() {
        DateTime startTime = DateTime.now();
        DateTime endTime = DateTime.now().plusDays(1);

        CallLog callLog = new CallLog("callId", "callerId", CallFlow.CALL, null, endTime);
        allCallLogs.add(callLog);

        callLog = new CallLog("callId", "callerId", CallFlow.CALL, startTime, null);

        CallLog logFromDb = allCallLogs.addOrUpdate(callLog);
        assertNotNull(logFromDb.getCallId());
        assertThat(logFromDb.getStartTime(), is(startTime));
        assertThat(logFromDb.getEndTime(), is(endTime));
    }

}
