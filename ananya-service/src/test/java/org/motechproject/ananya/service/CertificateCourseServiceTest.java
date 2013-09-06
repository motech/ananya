package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.ananya.action.AllCourseActions;
import org.motechproject.ananya.contract.AudioTrackerRequestList;
import org.motechproject.ananya.contract.CertificateCourseServiceRequest;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;
import org.motechproject.ananya.contract.FrontLineWorkerCreateResponse;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.motechproject.ananya.service.publish.DataPublishService;
import org.motechproject.ananya.transformers.AllTransformers;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

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
        frontLineWorker.setBookMark(bookMark);
        frontLineWorker.reportCard().addScore(new Score("0", "0", true));
        frontLineWorker.reportCard().addScore(new Score("0", "1", true));
        frontLineWorker.reportCard().addScore(new Score("1", "2", true));
        frontLineWorker.reportCard().addScore(new Score("1", "3", false));
        when(frontlineWorkerService.findByCallerId(callerId)).thenReturn(frontLineWorker);

        CertificateCourseCallerDataResponse callerData = certificateCourseService.createCallerData(request);

        assertEquals(bookMark.asJson(), callerData.getBookmark());
        assertEquals(2, callerData.getScoresByChapter().keySet().size());
        assertEquals(2, (int)callerData.getScoresByChapter().get("0"));
        assertEquals(1, (int)callerData.getScoresByChapter().get("1"));
        verify(allTransformers).process(request);
    }

    @Test
    public void shouldCreateCallerDataForGivenCallerIdIfFrontLineWorkerDoesNotExist() {
        String callId = "12342";
        String callerId = "123";
        String operator = "airtel";
        String circle = "circle";
        CertificateCourseServiceRequest request = new CertificateCourseServiceRequest(callId, callerId).withCircle(circle).withOperator(operator);

        when(frontlineWorkerService.findByCallerId(callerId)).thenReturn(null);

        CertificateCourseCallerDataResponse callerData = certificateCourseService.createCallerData(request);

        verify(allTransformers).process(request);

        assertEquals(0, callerData.getScoresByChapter().size());
        assertEquals("{}", callerData.getBookmark());
        assertEquals(false, callerData.isCallerRegistered());
    }

    @Test
    public void shouldCallAllServicesToHandleDisconnectData() {
        String callId = "123-456";
        String callerId = "123";
        String operator = "airtel";
        String circle = "circle";
        String language= "language";
        
        CertificateCourseStateRequestList stateRequestList = mock(CertificateCourseStateRequestList.class);
        AudioTrackerRequestList audioTrackerList = mock(AudioTrackerRequestList.class);
        CallDurationList callDurationList = mock(CallDurationList.class);
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator, circle, language);
        CertificateCourseServiceRequest request = mock(CertificateCourseServiceRequest.class);

        when(request.getCallId()).thenReturn(callId);
        when(request.getCallerId()).thenReturn(callerId);
        when(request.getCertificateCourseStateRequestList()).thenReturn(stateRequestList);
        when(request.getAudioTrackerRequestList()).thenReturn(audioTrackerList);
        when(request.getCallDurationList()).thenReturn(callDurationList);
        when(request.getOperator()).thenReturn(operator);
        when(request.getCircle()).thenReturn(circle);
        when(request.getLanguage()).thenReturn(language);
        
        when(stateRequestList.isNotEmpty()).thenReturn(true);
        when(stateRequestList.getCallerId()).thenReturn(callerId);

        when(frontlineWorkerService.createOrUpdateForCall(callerId, operator, circle, language)).thenReturn(new FrontLineWorkerCreateResponse(frontLineWorker, false));

        certificateCourseService.handleDisconnect(request);

        InOrder inOrder = inOrder(allTransformers, allCourseActions, frontlineWorkerService, audioTrackerService, callLoggerService, dataPublishService);

        inOrder.verify(allTransformers).process(request);
        inOrder.verify(allCourseActions).execute(frontLineWorker, stateRequestList);
        inOrder.verify(frontlineWorkerService).updateCertificateCourseState(frontLineWorker);
        inOrder.verify(audioTrackerService).saveAllForCourse(audioTrackerList);
        inOrder.verify(callLoggerService).saveAll(callDurationList);
        inOrder.verify(dataPublishService).publishDisconnectEvent(callId, ServiceType.CERTIFICATE_COURSE);
    }
}
