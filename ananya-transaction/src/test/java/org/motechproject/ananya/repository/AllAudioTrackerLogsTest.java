package org.motechproject.ananya.repository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.AudioTrackerLogItem;
import org.motechproject.ananya.domain.ServiceType;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AllAudioTrackerLogsTest extends SpringIntegrationTest {

    @Autowired
    private AllAudioTrackerLogs allAudioTrackerLogs;

    @Test
    public void shouldFindByCallId() {
        String callerId = "123";
        String callId = "123456";
        AudioTrackerLog audioTrackerLog = new AudioTrackerLog(callId, callerId, ServiceType.JOB_AID);
        audioTrackerLog.addItem(new AudioTrackerLogItem("contentId", DateTime.now(), 312));
        allAudioTrackerLogs.add(audioTrackerLog);
        markForDeletion(audioTrackerLog);

        AudioTrackerLog trackerLog = allAudioTrackerLogs.findByCallId(callId);
        assertEquals(callerId, trackerLog.getCallerId());
    }

    @Test
    public void shouldDeleteAudioTrackerLogsByCallId() {
        String callerId = "123";
        String differentCallId = "321";
        String callId = "123456";
        AudioTrackerLog audioTrackerLog = new AudioTrackerLog(callId, callerId, ServiceType.JOB_AID);
        AudioTrackerLog audioTrackerLogForADifferentCallId = new AudioTrackerLog(differentCallId, callerId, ServiceType.JOB_AID);
        allAudioTrackerLogs.add(audioTrackerLog);
        allAudioTrackerLogs.add(audioTrackerLogForADifferentCallId);
        markForDeletion(audioTrackerLogForADifferentCallId);

        allAudioTrackerLogs.deleteFor(callId);

        AudioTrackerLog byCallId = allAudioTrackerLogs.findByCallId(callId);
        assertNull(byCallId);
        byCallId = allAudioTrackerLogs.findByCallId(differentCallId);
        assertNotNull(byCallId);
    }
}
