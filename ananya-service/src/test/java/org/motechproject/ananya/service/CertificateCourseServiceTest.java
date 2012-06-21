package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.request.AudioTrackerRequestList;
import org.motechproject.ananya.request.CertificateCourseStateRequestList;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
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

    @Before
    public void setUp() {
        initMocks(this);
        certificateCourseService = new CertificateCourseService(certificateCourseLogService, audioTrackerService,
                frontlineWorkerService, registrationLogService, sendSMSLogService);
    }

    @Test
    public void shouldCreateCallerDataForGivenCallerId() {
        String callerId = "123";
        String callId = "123432";
        String operator = "airtel";
        String circle = "circle";

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        BookMark bookMark = new BookMark("type", 1, 2);
        frontLineWorker.addBookMark(bookMark);

        when(frontlineWorkerService.createOrUpdateUnregistered(callerId, operator, circle)).thenReturn(frontLineWorker);

        CertificateCourseCallerDataResponse callerData = certificateCourseService.createCallerData(callId, callerId, operator, circle);
        assertEquals(bookMark.asJson(), callerData.getBookmark());
    }

    @Test
    public void shouldCreateCallerDataAndRegistrationLogForGivenCallerIdIfFrontLineWorkerDoesNotExist() {
        String callerId = "123";
        String operator = "airtel";
        String circle = "circle";

        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        BookMark bookMark = new BookMark("type", 1, 2);
        frontLineWorker.addBookMark(bookMark);
        frontLineWorker.setCircle(circle);
        frontLineWorker.setModified();

        when(frontlineWorkerService.createOrUpdateUnregistered(callerId, operator, circle)).thenReturn(frontLineWorker);

        certificateCourseService.createCallerData(callerId, callerId,operator, circle);

        ArgumentCaptor<RegistrationLog> captor = ArgumentCaptor.forClass(RegistrationLog.class);
        verify(registrationLogService).add(captor.capture());
        RegistrationLog registrationLog = captor.getValue();
        assertEquals(callerId, registrationLog.getCallerId());
    }

    @Test
    public void shouldClearAllScoresForAGivenFLWWhenInteractionIsStartCertificationCourse() {
        CertificateCourseStateRequestList stateRequestList = new CertificateCourseStateRequestList("123456", "123");

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.reportCard().addScore(new Score());
        when(frontlineWorkerService.findByCallerId("123")).thenReturn(frontLineWorker);

        String json = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"startCertificationCourse\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":0}";
        stateRequestList.add(json, "1");

        certificateCourseService.saveState(stateRequestList);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(frontlineWorkerService).updateCertificateCourseStateFor(captor.capture());
        FrontLineWorker captured = captor.getValue();
        assertTrue(captured.reportCard().scores().isEmpty());
    }

    @Test
    public void shouldClearAllScoresForAGivenFLWWhenInteractionIsStartQuiz() {
        CertificateCourseStateRequestList stateRequestList = new CertificateCourseStateRequestList("123456", "123");

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        String chapterIndexWhoseScoresShouldNotBeCleared = "2";
        frontLineWorker.reportCard().addScore(new Score("1", "1", true));
        frontLineWorker.reportCard().addScore(new Score(chapterIndexWhoseScoresShouldNotBeCleared, "1", true));
        when(frontlineWorkerService.findByCallerId("123")).thenReturn(frontLineWorker);

        String json = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"startQuiz\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";
        stateRequestList.add(json, "1");

        certificateCourseService.saveState(stateRequestList);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(frontlineWorkerService).updateCertificateCourseStateFor(captor.capture());
        FrontLineWorker captured = captor.getValue();
        assertEquals(1, captured.reportCard().scores().size());
        assertEquals(chapterIndexWhoseScoresShouldNotBeCleared, captured.reportCard().scores().get(0).chapterIndex());
    }

    @Test
    public void shouldAddScoresToAChapterForAGivenFLWWhenInteractionIsisPlayAnswerExplanation() {
        CertificateCourseStateRequestList stateRequestList = new CertificateCourseStateRequestList("123456", "123");

        FrontLineWorker frontLineWorker = new FrontLineWorker();

        when(frontlineWorkerService.findByCallerId("123")).thenReturn(frontLineWorker);

        String json = "{\"result\":true,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"playAnswerExplanation\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";
        stateRequestList.add(json, "1");

        certificateCourseService.saveState(stateRequestList);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(frontlineWorkerService).updateCertificateCourseStateFor(captor.capture());

        FrontLineWorker captured = captor.getValue();
        ReportCard reportCard = captured.reportCard();
        assertEquals(1, reportCard.scores().size());

        Score score = reportCard.scores().get(0);
        assertEquals("1", score.chapterIndex());
        assertEquals("0", score.questionIndex());
        assertTrue(score.result());
    }

    @Test
    public void shouldIncrementCourseAttemptsAndLogSendSMSForAGivenFLWWhenInteractionIsCourseCompletionAndScoreIsPassingScore() {
        String callId = "123456";
        String callerId = "919986574410";
        CertificateCourseStateRequestList stateRequestList = new CertificateCourseStateRequestList(callId, callerId);

        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, "airtel");
        ReportCard reportCard = mock(ReportCard.class);
        when(reportCard.totalScore()).thenReturn(FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE + 1);
        ReflectionTestUtils.setField(frontLineWorker, "reportCard", reportCard);

        when(frontlineWorkerService.findByCallerId(callerId)).thenReturn(frontLineWorker);

        String json = "{\"result\":true,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"playCourseResult\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";
        stateRequestList.add(json, "1");

        certificateCourseService.saveState(stateRequestList);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(frontlineWorkerService).updateCertificateCourseStateFor(captor.capture());
        FrontLineWorker captured = captor.getValue();

        assertEquals(new Integer(1), captured.currentCourseAttempt());

        ArgumentCaptor<SMSLog> captorLog = ArgumentCaptor.forClass(SMSLog.class);
        verify(sendSMSLogService).add(captorLog.capture());
        SMSLog SMSLog = captorLog.getValue();

        assertEquals(callId, SMSLog.getCallId());
        assertEquals(callerId, SMSLog.getCallerId());
    }

    @Test
    public void shouldNotLogSendSMSForAGivenFLWWhenInteractionIsNotCourseCompletionAndScoreIsPassingScore() {
        CertificateCourseStateRequestList stateRequestList = new CertificateCourseStateRequestList("123456", "123");

        FrontLineWorker frontLineWorker = new FrontLineWorker("123", "airtel");
        ReportCard reportCard = mock(ReportCard.class);
        when(reportCard.totalScore()).thenReturn(FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE + 1);
        ReflectionTestUtils.setField(frontLineWorker, "reportCard", reportCard);

        when(frontlineWorkerService.findByCallerId("123")).thenReturn(frontLineWorker);

        String json = "{\"result\":true,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"playAnswerExplanation\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";
        stateRequestList.add(json, "1");

        certificateCourseService.saveState(stateRequestList);

        verify(sendSMSLogService, never()).add(Matchers.<SMSLog>any());
    }


    @Test
    public void shouldModifyFLWAggregateFromTheCertificateCourseStateRequestList() {
        CertificateCourseStateRequestList stateRequestList = new CertificateCourseStateRequestList("123456", "123");
        FrontLineWorker frontLineWorker = new FrontLineWorker();

        when(frontlineWorkerService.findByCallerId("123")).thenReturn(frontLineWorker);

        String json1 = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"startCertificationCourse\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":0}";

        String json2 = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"startQuiz\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";

        String json3 = "{\"result\":true,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"playAnswerExplanation\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";

        stateRequestList.add(json1, "1");
        stateRequestList.add(json2, "2");
        stateRequestList.add(json3, "3");

        certificateCourseService.saveState(stateRequestList);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(frontlineWorkerService).updateCertificateCourseStateFor(captor.capture());
        FrontLineWorker captured = captor.getValue();

        ReportCard reportCard = captured.reportCard();
        assertEquals(1, reportCard.scores().size());

        Score score = reportCard.scores().get(0);
        assertEquals("1", score.chapterIndex());
        assertEquals("0", score.questionIndex());
        assertTrue(score.result());
    }

    @Test
    public void shouldSaveAudioTrackerLogs() {
        String callid = "callid";
        String callerid = "callerid";
        String dataToken = "1";
        String jsonString =
                "{" +
                        "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                        "    \"duration\" : \"123\",                             " +
                        "    \"time\" : \"123456789\"                          " +
                        "}";
        AudioTrackerRequestList audioTrackerRequestList = new AudioTrackerRequestList(callid, callerid);
        audioTrackerRequestList.add(jsonString, dataToken);

        certificateCourseService.saveAudioTrackerState(audioTrackerRequestList);

        verify(audioTrackerService).saveAudioTrackerState(audioTrackerRequestList, ServiceType.CERTIFICATE_COURSE);
    }
}
