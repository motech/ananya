package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CallDurationList;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.request.AudioTrackerRequestList;
import org.motechproject.ananya.request.JobAidServiceRequest;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import org.motechproject.ananya.service.publish.DataPublishService;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
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
    @Mock
    private CallLoggerService callLoggerService;

    @Before
    public void setUp() {
        initMocks(this);
        jobAidService = new JobAidService(frontLineWorkerService, operatorService,
                dataPublishService, audioTrackerService, registrationLogService, callLoggerService);
    }

    @Test
    public void shouldCreateNewFlwWithUsageAndAlsoPublishToReportModule() {
        String operator = "airtel";
        String callerId = "callerId";
        String promptKey = "prompt";
        String circle = "circle";
        String callId = "callId";
        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.markPromptHeard(promptKey);
        frontLineWorker.setCurrentJobAidUsage(new Integer(9));
        frontLineWorker.setModified();

        when(frontLineWorkerService.findForJobAidCallerData(callerId, operator, circle)).thenReturn(frontLineWorker);
        when(operatorService.findMaximumUsageFor(operator)).thenReturn(new Integer(10));

        JobAidCallerDataResponse callerData = jobAidService.createCallerData(callId, callerId, operator, circle);

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
    public void shouldCreateFrontLineWorkerAndRegistrationLogWhileCreatingCallerDataIfNotInOnlineDB() {
        String callerId = "1234";
        String operator = "airtel";
        String circle = "bihar";
        String callId = "callid";
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        frontLineWorker.setCircle(circle);
        frontLineWorker.setModified();

        when(frontLineWorkerService.findForJobAidCallerData(callerId, operator, circle)).thenReturn(frontLineWorker);

        jobAidService.createCallerData(callId, callerId, operator, circle);

        ArgumentCaptor<RegistrationLog> captor = ArgumentCaptor.forClass(RegistrationLog.class);
        verify(registrationLogService).add(captor.capture());

        RegistrationLog registrationLog = captor.getValue();
        assertEquals(callerId, registrationLog.getCallerId());
        assertEquals(operator, registrationLog.getOperator());
    }

    @Test
    public void shouldReturnCallerDataWithoutCreatingRegistrationLogIfFrontLineWorkerExists() {
        String callerId = "1234";
        String operator = "airtel";
        String circle = "bihar";
        String callId = "callId";
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        frontLineWorker.setCircle(circle);

        when(frontLineWorkerService.findForJobAidCallerData(callerId, operator, circle)).thenReturn(frontLineWorker);

        jobAidService.createCallerData(callId, callerId, operator, circle);
        verify(registrationLogService, never()).add(any(RegistrationLog.class));
    }

    @Test
    public void shouldUpdateUsageAndPromptsAndSaveAudioTrackerLogAndPublishCallMessage() {
        String callId = "1234";
        String callerId = "1234";
        String calledNumber = "522001";
        int callDuration = 21;

        JobAidServiceRequest jobAidServiceRequest = new JobAidServiceRequest(callId, callerId, calledNumber, "[]", "", callDuration);

        jobAidService.handleDisconnect(jobAidServiceRequest);

        verify(frontLineWorkerService).updateJobAidUsageAndAccessTime(callerId, callDuration);
        verify(frontLineWorkerService).updatePromptsFor(eq(callerId), anyListOf(String.class));
        verify(dataPublishService).publishDisconnectEvent(callId, ServiceType.JOB_AID);

        ArgumentCaptor<CallDurationList> callDurationCaptor = ArgumentCaptor.forClass(CallDurationList.class);
        ArgumentCaptor<AudioTrackerRequestList> audioTrackerRequestCaptor = ArgumentCaptor.forClass(AudioTrackerRequestList.class);

        verify(callLoggerService).saveAll(callDurationCaptor.capture());
        verify(audioTrackerService).saveAllForJobAid(audioTrackerRequestCaptor.capture());

        CallDurationList callDurationList = callDurationCaptor.getValue();
        assertThat(callDurationList.getCallId(), is(callId));
        assertThat(callDurationList.getCallerId(), is(callerId));

        AudioTrackerRequestList audioTrackerRequestList = audioTrackerRequestCaptor.getValue();
        assertThat(audioTrackerRequestList.getCallId(), is(callId));
        assertThat(audioTrackerRequestList.getCallerId(), is(callerId));
    }
}
