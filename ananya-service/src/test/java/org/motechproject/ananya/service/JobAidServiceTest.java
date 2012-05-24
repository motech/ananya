package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.request.AudioTrackerRequestList;
import org.motechproject.ananya.request.JobAidPromptRequest;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import org.motechproject.ananya.service.publish.DataPublishService;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
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
    private AudioTrackerService audioTrackerService;
    @Mock
    private RegistrationLogService registrationLogService;

    @Before
    public void setUp() {
        initMocks(this);
        jobAidService = new JobAidService(frontLineWorkerService, operatorService,
                                        dataPublishService, audioTrackerService,registrationLogService);
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
    public void shouldCreateNewFlwWithUsageAndAlsoPublishToReportModule() {
        String operator = "airtel";
        String callerId = "callerId";
        String promptKey = "prompt";
        String circle = "circle";
        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.markPromptHeard(promptKey);
        frontLineWorker.setCurrentJobAidUsage(new Integer(9));
        frontLineWorker.setModified();

        when(frontLineWorkerService.findForJobAidCallerData(callerId, operator, circle)).thenReturn(frontLineWorker);
        when(operatorService.findMaximumUsageFor(operator)).thenReturn(new Integer(10));

        JobAidCallerDataResponse callerData = jobAidService.createCallerData(callerId, operator, circle);

        verify(frontLineWorkerService).findForJobAidCallerData(callerId, operator, circle);

        ArgumentCaptor<RegistrationLog> captor = ArgumentCaptor.forClass(RegistrationLog.class);
                verify(registrationLogService).add(captor.capture());
        
                        RegistrationLog registrationLog = captor.getValue();
                assertEquals(callerId, registrationLog.getCallerId());
                assertEquals(operator, registrationLog.getOperator());


        assertEquals(callerData.getCurrentJobAidUsage(), new Integer(9));
        assertEquals(callerData.getMaxAllowedUsageForOperator(), new Integer(10));
        assertEquals(callerData.getPromptsHeard().get(promptKey), new Integer(1));
        assertEquals(callerData.isCallerRegistered(), false);
    }

    @Test
    public void shouldUpdateTheFrontLineWorkerWithTheNewUsage() {
        int currentUsage = 10;
        String callerId = "callerId";

        jobAidService.updateCurrentUsageAndSetLastAccessTimeForUser(callerId, currentUsage);

        verify(frontLineWorkerService).updateJobAidUsageAndAccessTime(callerId, currentUsage);
    }

    @Test
    public void shouldSaveAudioTrackerState() {
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

        verify(audioTrackerService).saveAudioTrackerState(audioTrackerRequestList, ServiceType.JOB_AID);
    }

    @Test
    public void shouldCreateFrontLineWorkerAndRegistrationLogWhileCreatingCallerDataIfNotInOnlineDB(){
        String callerId = "1234";
        String operator = "airtel";
        String circle = "bihar";
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        frontLineWorker.setCircle(circle);
        frontLineWorker.setModified();

        when(frontLineWorkerService.findForJobAidCallerData(callerId, operator, circle)).thenReturn(frontLineWorker);

        jobAidService.createCallerData(callerId, operator, circle);
        
        ArgumentCaptor<RegistrationLog> captor = ArgumentCaptor.forClass(RegistrationLog.class);
        verify(registrationLogService).add(captor.capture());

        RegistrationLog registrationLog = captor.getValue();
        assertEquals(callerId,registrationLog.getCallerId());
        assertEquals(operator,registrationLog.getOperator());
    }

    @Test
    public void shouldReturnCallerDataWithoutCreatingRegistrationLogIfFrontLineWorkerExists(){
        String callerId = "1234";
        String operator = "airtel";
        String circle = "bihar";
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        frontLineWorker.setCircle(circle);

        when(frontLineWorkerService.findForJobAidCallerData(callerId, operator, circle)).thenReturn(frontLineWorker);

        jobAidService.createCallerData(callerId, operator, circle);
        verify(registrationLogService, never()).add(any(RegistrationLog.class));
    }
}
