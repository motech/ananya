package org.motechproject.ananya.repository;

import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.ServiceType;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class AllAudioTrackerLogsTest extends SpringIntegrationTest{

    @Autowired
    private AllAudioTrackerLogs allAudioTrackerLogs;

    @Test
    public void shouldFindByCallId() {
        String callerId = "123";
        String callId = "123456";
        AudioTrackerLog audioTrackerLog = new AudioTrackerLog(callId, callerId, ServiceType.JOB_AID);
        allAudioTrackerLogs.add(audioTrackerLog);
        markForDeletion(audioTrackerLog);

        AudioTrackerLog trackerLog = allAudioTrackerLogs.findByCallId(callId);
        assertEquals(callerId, trackerLog.getCallerId());
    }}
