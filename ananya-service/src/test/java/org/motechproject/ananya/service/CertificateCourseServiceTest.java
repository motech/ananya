package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.action.AllCourseActions;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.request.AudioTrackerRequestList;
import org.motechproject.ananya.request.CertificateCourseServiceRequest;
import org.motechproject.ananya.request.CertificateCourseStateRequestList;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.motechproject.ananya.service.publish.DataPublishService;
import org.motechproject.ananya.transformers.AllTransformers;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class CertificateCourseServiceTest {

    private CertificateCourseService certificateCourseService;
    @Mock
    private FrontLineWorkerService frontlineWorkerService;
    @Mock
    private CertificateCourseLogService certificateCourseLogService;
    @Mock
    private AudioTrackerService audioTrackerService;
    @Mock
    private RegistrationLogService registrationLogService;
    @Mock
    private SMSLogService sendSMSLogService;
    @Mock
    private CallLogService callLoggerService;
    @Mock
    private DataPublishService dataPublishService;
    @Mock
    private AllTransformers allTransformers;
    @Mock
    private AllCourseActions allCourseActions;

    @Before
    public void setUp() {
        initMocks(this);
        certificateCourseService = new CertificateCourseService(audioTrackerService, frontlineWorkerService,
                registrationLogService, callLoggerService, dataPublishService, allTransformers, allCourseActions);
    }

    @Test
    public void shouldCreateCallerDataForGivenCallerId() {
        String callId = "123432";
        String callerId = "123";
        String operator = "airtel";
        String circle = "circle";
        CertificateCourseServiceRequest request = new CertificateCourseServiceRequest(callId, callerId).withCircle(circle).withOperator(operator);

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        BookMark bookMark = new BookMark("type", 1, 2);
        frontLineWorker.addBookMark(bookMark);
        when(frontlineWorkerService.createOrUpdateForCall(callerId, operator, circle)).thenReturn(frontLineWorker);

        CertificateCourseCallerDataResponse callerData = certificateCourseService.createCallerData(request);

        assertEquals(bookMark.asJson(), callerData.getBookmark());
        verify(allTransformers).process(request);
        verify(registrationLogService, never()).add(any(RegistrationLog.class));
    }

    @Test
    public void shouldCreateCallerDataAndRegistrationLogForGivenCallerIdIfFrontLineWorkerDoesNotExist() {
        String callId = "12342";
        String callerId = "123";
        String operator = "airtel";
        String circle = "circle";
        CertificateCourseServiceRequest request = new CertificateCourseServiceRequest(callId, callerId).withCircle(circle).withOperator(operator);

        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        BookMark bookMark = new BookMark("type", 1, 2);
        frontLineWorker.addBookMark(bookMark);
        frontLineWorker.setCircle(circle);
        frontLineWorker.setModified();

        when(frontlineWorkerService.createOrUpdateForCall(callerId, operator, circle)).thenReturn(frontLineWorker);

        certificateCourseService.createCallerData(request);

        verify(allTransformers).process(request);
        ArgumentCaptor<RegistrationLog> captor = ArgumentCaptor.forClass(RegistrationLog.class);
        verify(registrationLogService).add(captor.capture());
        RegistrationLog registrationLog = captor.getValue();
        assertEquals(callerId, registrationLog.getCallerId());
    }

    @Test
    public void shouldCallAllServicesToHandleDisconnectData() {
        String callId = "123-456";
        String callerId = "123";
        String operator = "airtel";

        CertificateCourseStateRequestList stateRequestList = mock(CertificateCourseStateRequestList.class);
        AudioTrackerRequestList audioTrackerList = mock(AudioTrackerRequestList.class);
        CallDurationList callDurationList = mock(CallDurationList.class);
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        CertificateCourseServiceRequest request = mock(CertificateCourseServiceRequest.class);

        when(request.getCallId()).thenReturn(callId);
        when(request.getCallerId()).thenReturn(callerId);
        when(request.getCertificateCourseStateRequestList()).thenReturn(stateRequestList);
        when(request.getAudioTrackerRequestList()).thenReturn(audioTrackerList);
        when(request.getCallDurationList()).thenReturn(callDurationList);

        when(stateRequestList.isNotEmpty()).thenReturn(true);
        when(stateRequestList.getCallerId()).thenReturn(callerId);

        when(frontlineWorkerService.findByCallerId(callerId)).thenReturn(frontLineWorker);

        certificateCourseService.handleDisconnect(request);

        verify(allTransformers).process(request);
        verify(allCourseActions).execute(frontLineWorker, stateRequestList);
        verify(audioTrackerService).saveAllForCourse(audioTrackerList);
        verify(callLoggerService).saveAll(callDurationList);
        verify(dataPublishService).publishDisconnectEvent(callId, ServiceType.CERTIFICATE_COURSE);

    }


}
