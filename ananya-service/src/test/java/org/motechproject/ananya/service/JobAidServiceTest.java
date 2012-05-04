package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.AudioTrackerLogItem;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.request.AudioTrackerRequestList;
import org.motechproject.ananya.request.JobAidPromptRequest;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import org.motechproject.ananya.service.publish.DataPublishService;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class JobAidServiceTest {

    private JobAidService jobAidService;

    @Mock
    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private OperatorService operatorService;
    @Mock
    private DataPublishService dataPublishService;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Mock
    private AudioTrackerLogService audioTrackerLogService;


    @Before
    public void setUp() {
        initMocks(this);
        jobAidService = new JobAidService(frontLineWorkerService, operatorService, dataPublishService, audioTrackerLogService);
    }

    @Test
    public void shouldUpdateFrontLineWorkersWithPrompts() {
        String callId = "callId";
        String callerId = "callerId";
        String promptIds = "[promptIds]";
        JobAidPromptRequest jobAidPromptRequest = new JobAidPromptRequest(callId, callerId, promptIds);

        jobAidService.updateJobAidPrompts(jobAidPromptRequest);

        verify(frontLineWorkerService).updatePromptsFor(jobAidPromptRequest.getCallerId(), jobAidPromptRequest.getPromptList());
    }

    @Test
    public void shouldCreateNewFlwWithUsageAndAlsoPublishToReportModule(){
        String operator = "airtel";
        String callerId = "callerId";
        String promptKey = "prompt";
        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.markPromptHeard(promptKey);
        frontLineWorker.setCurrentJobAidUsage(new Integer(9));
        when(frontLineWorkerService.getFLWForJobAidCallerData(callerId, operator)).thenReturn(frontLineWorker);
        when(frontLineWorkerService.isNewFLW(callerId)).thenReturn(true);
        when(operatorService.findMaximumUsageFor(operator)).thenReturn(new Integer(10));

        JobAidCallerDataResponse callerData = jobAidService.createCallerData(callerId, operator);

        verify(frontLineWorkerService).getFLWForJobAidCallerData(callerId, operator);
        verify(dataPublishService).publishNewRegistration(callerId);

        assertEquals(callerData.getCurrentJobAidUsage(),new Integer(9));
        assertEquals(callerData.getMaxAllowedUsageForOperator(),new Integer(10));
        assertEquals(callerData.getPromptsHeard().get(promptKey), new Integer(1));
        assertEquals(callerData.isCallerRegistered(),false);
    }

    @Test
    public void shouldUpdateTheFrontLineWorkerWithTheNewUsage(){
        int currentUsage = 10;
        String callerId = "callerId";

        jobAidService.updateCurrentUsageAndSetLastAccessTimeForUser(callerId, currentUsage);

        verify(frontLineWorkerService).updateJobAidCurrentUsageFor(callerId, currentUsage);
        verify(frontLineWorkerService).updateJobAidLastAccessTime(callerId);
    }

    @Test
    public void shouldSaveAudioTrackerState(){
        String callerId = "callerId";
        String callId = "callId";
        String jsonString1 =
                "{" +
                        "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                        "    \"duration\" : \"123\",                             " +
                        "    \"time\" : \"123456789\"                          " +
                        "}";

        String jsonString2 =
                "{" +
                        "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926999\",     " +
                        "    \"duration\" : \"121\",                             " +
                        "    \"time\" : \"123456789\"                          " +
                        "}";

        AudioTrackerRequestList audioTrackerRequestList = new AudioTrackerRequestList(callId, callerId);
        audioTrackerRequestList.add(jsonString1, "1");
        audioTrackerRequestList.add(jsonString2, "2");

        jobAidService.saveAudioTrackerState(audioTrackerRequestList);

        ArgumentCaptor<AudioTrackerLog> captor = ArgumentCaptor.forClass(AudioTrackerLog.class);
        verify(audioTrackerLogService).createNew(captor.capture());

        AudioTrackerLog audioTrackerLog = captor.getValue();
        assertEquals(callerId,audioTrackerLog.getCallerId());
        assertEquals(callId,audioTrackerLog.getCallId());
        assertEquals(ServiceType.JOB_AID,audioTrackerLog.getServiceType());

        List<AudioTrackerLogItem> audioTrackerLogItems = audioTrackerLog.getAudioTrackerLogItems();
        assertEquals(2, audioTrackerLogItems.size());
        assertEquals("e79139b5540bf3fc8d96635bc2926f90",audioTrackerLogItems.get(0).getContentId());
        assertEquals(123456789l,audioTrackerLogItems.get(0).getTime().getMillis());
        assertEquals(123, (int)audioTrackerLogItems.get(0).getDuration());
    }
}
