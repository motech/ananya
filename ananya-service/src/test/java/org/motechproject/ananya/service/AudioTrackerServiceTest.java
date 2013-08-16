package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.contract.AudioTrackerRequestList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AudioTrackerServiceTest {

    @Mock
    private AudioTrackerLogService audioTrackerLogService;

    private AudioTrackerService audioTrackerService;


    @Before
    public void setUp() {
        initMocks(this);
        audioTrackerService = new AudioTrackerService(audioTrackerLogService);
    }


    @Test
    public void shouldSaveAudioTrackerLogs() {
        String callid = "callid";
        String callerid = "callerid";
        String language= "language";
        String dataToken = "1";
        String jsonString =
                "{" +
                        "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                        "    \"duration\" : \"123\",                             " +
                        "    \"time\" : \"123456789\"                          " +
                        "}";
        AudioTrackerRequestList audioTrackerRequestList = new AudioTrackerRequestList(callid, callerid);
        audioTrackerRequestList.add(jsonString, dataToken, language);

        audioTrackerService.saveAudioTrackerState(audioTrackerRequestList, ServiceType.CERTIFICATE_COURSE);

        ArgumentCaptor<AudioTrackerLog> captor = ArgumentCaptor.forClass(AudioTrackerLog.class);
        verify(audioTrackerLogService).createNew(captor.capture());
        AudioTrackerLog audioTrackerLog = captor.getValue();
        assertEquals(callid, audioTrackerLog.getCallId());
        assertEquals(callerid, audioTrackerLog.getCallerId());
    }

    @Test
    public void shouldNotProceedWithSaveAudioTrackerLogsIfTheRequestListIsEmpty() {
        String callid = "callid";
        String callerid = "callerid";
        AudioTrackerRequestList audioTrackerRequestList = new AudioTrackerRequestList(callid, callerid);

        audioTrackerService.saveAudioTrackerState(audioTrackerRequestList, ServiceType.CERTIFICATE_COURSE);

        verify(audioTrackerLogService, never()).createNew(null);
    }
}
