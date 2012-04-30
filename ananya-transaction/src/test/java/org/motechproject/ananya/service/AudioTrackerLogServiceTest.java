package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.repository.AllAudioTrackerLogs;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AudioTrackerLogServiceTest {

    @Mock
    private AllAudioTrackerLogs allAudioTrackerLogs;

    private AudioTrackerLogService audioTrackerLogService;

    @Before
    public void setUp() {
        initMocks(this);
        audioTrackerLogService = new AudioTrackerLogService(allAudioTrackerLogs);
    }

    @Test
    public void shouldAddANewItemToTheDatabase() {
        AudioTrackerLog audioTrackerLog = new AudioTrackerLog();

        audioTrackerLogService.createNew(audioTrackerLog);

        verify(allAudioTrackerLogs).add(audioTrackerLog);
    }

    @Test
    public void shouldFetchLogsBasedOnCallId() {
        String callId = "callId";

        audioTrackerLogService.getLogFor(callId);

        verify(allAudioTrackerLogs).findByCallId(callId);
    }

    @Test
    public void shouldDeleteAllCallLogsRelatedToACallId() {
        String callId = "callId";

        audioTrackerLogService.deleteLogsFor(callId);

        verify(allAudioTrackerLogs).deleteFor(callId);
    }
}
