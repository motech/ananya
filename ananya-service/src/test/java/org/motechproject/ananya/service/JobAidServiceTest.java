package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.ananya.contract.AudioTrackerRequestList;
import org.motechproject.ananya.contract.FrontLineWorkerCreateResponse;
import org.motechproject.ananya.contract.JobAidServiceRequest;
import org.motechproject.ananya.domain.CallDurationList;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import org.motechproject.ananya.service.publish.DataPublishService;
import org.motechproject.ananya.transformers.AllTransformers;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
    private CallLogService callLoggerService;
    @Mock
    private AllTransformers allTransformers;

    @Before
    public void setUp() {
        initMocks(this);
        jobAidService = new JobAidService(frontLineWorkerService, operatorService,
                dataPublishService, audioTrackerService, registrationLogService, callLoggerService, allTransformers);
    }

    @Test
    public void shouldReturnFlwCallerData() {
        String operator = "airtel";
        String callerId = "callerId";
        String promptKey = "prompt";
        String circle = "circle";
        String callId = "callId";
        JobAidServiceRequest jobAidServiceRequest = new JobAidServiceRequest(callId, callerId).withCircle(circle).withOperator(operator);

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.markPromptHeard(promptKey);
        frontLineWorker.setCurrentJobAidUsage(new Integer(9));

        when(frontLineWorkerService.findForJobAidCallerData(callerId)).thenReturn(frontLineWorker);
        when(operatorService.findMaximumUsageFor(operator, circle)).thenReturn(new Integer(10));

        JobAidCallerDataResponse callerData = jobAidService.getCallerData(jobAidServiceRequest);

        verify(allTransformers).process(jobAidServiceRequest);

        assertEquals(new Integer(9), callerData.getCurrentJobAidUsage());
        assertEquals(new Integer(10), callerData.getMaxAllowedUsageForOperator());
        assertEquals(new Integer(1), callerData.getPromptsHeard().get(promptKey));
        assertEquals(false, callerData.isCallerRegistered());
    }

    @Test
    public void shouldReturnBlankCallerDataIfNotInOnlineDB() {
        String callerId = "1234";
        String operator = "airtel";
        String circle = "bihar";
        String callId = "callId";
        int maxUsageOperator = 20;

        JobAidServiceRequest jobAidServiceRequest = new JobAidServiceRequest(callId, callerId).withCircle(circle).withOperator(operator);

        when(frontLineWorkerService.findForJobAidCallerData(callerId)).thenReturn(null);
        when(operatorService.findMaximumUsageFor(operator,circle)).thenReturn(maxUsageOperator);

        JobAidCallerDataResponse callerData = jobAidService.getCallerData(jobAidServiceRequest);

        verify(allTransformers).process(jobAidServiceRequest);

        assertEquals(0, callerData.getPromptsHeard().size());
        assertEquals(false, callerData.isCallerRegistered());
        assertEquals(0, (int) callerData.getCurrentJobAidUsage());
        assertEquals(maxUsageOperator, (int) callerData.getMaxAllowedUsageForOperator());
    }

    @Test
    public void shouldUpdateUsageAndPromptsAndSaveAudioTrackerLogAndPublishCallMessage() {
        String callId = "1234";
        String callerId = "1234";
        String calledNumber = "522001";
        String operator = "operator";
        int callDuration = 21;
        String circle = "circle";
        String language = "language";

        JobAidServiceRequest jobAidServiceRequest = new JobAidServiceRequest(callId, callerId, calledNumber)
                .withOperator(operator).withCircle(circle).withJson("[]").withLanguage(language).withCallDuration(callDuration);

        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator, circle, language);
        when(frontLineWorkerService.createOrUpdateForCall(callerId,operator, circle, language))
                .thenReturn(new FrontLineWorkerCreateResponse(frontLineWorker, false));

        jobAidService.handleDisconnect(jobAidServiceRequest);

        InOrder inOrder = inOrder(allTransformers, frontLineWorkerService, audioTrackerService, callLoggerService, dataPublishService);

        inOrder.verify(allTransformers).process(jobAidServiceRequest);
        inOrder.verify(frontLineWorkerService).createOrUpdateForCall(eq(callerId),eq(operator), eq(circle), eq(language));

        ArgumentCaptor<FrontLineWorker> frontLineWorkerArgumentCaptor = ArgumentCaptor.forClass(FrontLineWorker.class);
        inOrder.verify(frontLineWorkerService).updateJobAidState(frontLineWorkerArgumentCaptor.capture(), anyListOf(String.class), eq(callDuration));
        FrontLineWorker actualFrontLineWorker = frontLineWorkerArgumentCaptor.getValue();
        assertEquals(callerId, actualFrontLineWorker.getMsisdn());

        ArgumentCaptor<CallDurationList> callDurationCaptor = ArgumentCaptor.forClass(CallDurationList.class);
        ArgumentCaptor<AudioTrackerRequestList> audioTrackerRequestCaptor = ArgumentCaptor.forClass(AudioTrackerRequestList.class);

        inOrder.verify(audioTrackerService).saveAllForJobAid(audioTrackerRequestCaptor.capture());
        inOrder.verify(callLoggerService).saveAll(callDurationCaptor.capture());

        CallDurationList callDurationList = callDurationCaptor.getValue();
        assertThat(callDurationList.getCallId(), is(callId));
        assertThat(callDurationList.getCallerId(), is(callerId));

        AudioTrackerRequestList audioTrackerRequestList = audioTrackerRequestCaptor.getValue();
        assertThat(audioTrackerRequestList.getCallId(), is(callId));
        assertThat(audioTrackerRequestList.getCallerId(), is(callerId));

        inOrder.verify(dataPublishService).publishDisconnectEvent(callId, ServiceType.JOB_AID);
    }
}
