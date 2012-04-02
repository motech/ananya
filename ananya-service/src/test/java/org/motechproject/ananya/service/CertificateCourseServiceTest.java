package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.ReportCard;
import org.motechproject.ananya.domain.Score;
import org.motechproject.ananya.request.CertificateCourseStateFlwRequest;
import org.motechproject.ananya.request.CertificationCourseStateRequestList;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.motechproject.ananya.service.publish.DataPublishService;
import org.springframework.test.util.ReflectionTestUtils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;

public class CertificateCourseServiceTest {

    private CertificateCourseService certificateCourseService;
    @Mock
    private FrontLineWorkerService frontlineWorkerService;
    @Mock
    private CertificateCourseLogService certificateCourseLogService;
    @Mock
    private DataPublishService dataPublishService;


    @Before
    public void setUp() {
        initMocks(this);
        certificateCourseService = new CertificateCourseService(certificateCourseLogService, frontlineWorkerService, dataPublishService);
    }

    @Test
    public void shouldCreateCallerDataForGivenCallerId() {
        String callerId = "123";
        String operator = "airtel";

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        BookMark bookMark = new BookMark("type", 1, 2);
        frontLineWorker.addBookMark(bookMark);

        when(frontlineWorkerService.createOrUpdatePartiallyRegistered(callerId, operator)).thenReturn(frontLineWorker);

        CertificateCourseCallerDataResponse callerData = certificateCourseService.createCallerData(callerId, operator);
        assertEquals(bookMark.asJson(), callerData.getBookmark());
    }

    @Test
    public void shouldClearAllScoresForAGivenFLWWhenInteractionIsStartCertificationCourse() {
        CertificationCourseStateRequestList stateRequestList = new CertificationCourseStateRequestList("123456", "123");

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.reportCard().addScore(new Score());
        when(frontlineWorkerService.findByCallerId("123")).thenReturn(frontLineWorker);

        String json = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"startCertificationCourse\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"2012-03-08T12:54:57Z\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":0}";
        stateRequestList.add(json, "1");

        certificateCourseService.saveState(stateRequestList);

        ArgumentCaptor<CertificateCourseStateFlwRequest> captor = ArgumentCaptor.forClass(CertificateCourseStateFlwRequest.class);
        verify(frontlineWorkerService).update(captor.capture());
        CertificateCourseStateFlwRequest captured = captor.getValue();
        assertTrue(captured.getFrontLineWorker().reportCard().scores().isEmpty());
        assertFalse(captured.shouldSendSMS());
    }

    @Test
    public void shouldClearAllScoresForAGivenFLWWhenInteractionIsStartQuiz() {
        CertificationCourseStateRequestList stateRequestList = new CertificationCourseStateRequestList("123456", "123");

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        String chapterIndexWhoseScoresShouldNotBeCleared = "2";
        frontLineWorker.reportCard().addScore(new Score("1", "1", true));
        frontLineWorker.reportCard().addScore(new Score(chapterIndexWhoseScoresShouldNotBeCleared, "1", true));
        when(frontlineWorkerService.findByCallerId("123")).thenReturn(frontLineWorker);

        String json = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"startQuiz\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"2012-03-08T12:54:57Z\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";
        stateRequestList.add(json, "1");

        certificateCourseService.saveState(stateRequestList);

        ArgumentCaptor<CertificateCourseStateFlwRequest> captor = ArgumentCaptor.forClass(CertificateCourseStateFlwRequest.class);
        verify(frontlineWorkerService).update(captor.capture());
        CertificateCourseStateFlwRequest captured = captor.getValue();
        assertEquals(1, captured.getFrontLineWorker().reportCard().scores().size());
        assertEquals(chapterIndexWhoseScoresShouldNotBeCleared, captured.getFrontLineWorker().reportCard().scores().get(0).chapterIndex());
        assertFalse(captured.shouldSendSMS());
    }

    @Test
    public void shouldAddScoresToAChapterForAGivenFLWWhenInteractionIsisPlayAnswerExplanation() {
        CertificationCourseStateRequestList stateRequestList = new CertificationCourseStateRequestList("123456", "123");

        FrontLineWorker frontLineWorker = new FrontLineWorker();

        when(frontlineWorkerService.findByCallerId("123")).thenReturn(frontLineWorker);

        String json = "{\"result\":true,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"playAnswerExplanation\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"2012-03-08T12:54:57Z\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";
        stateRequestList.add(json, "1");

        certificateCourseService.saveState(stateRequestList);

        ArgumentCaptor<CertificateCourseStateFlwRequest> captor = ArgumentCaptor.forClass(CertificateCourseStateFlwRequest.class);
        verify(frontlineWorkerService).update(captor.capture());
        CertificateCourseStateFlwRequest captured = captor.getValue();
        ReportCard reportCard = captured.getFrontLineWorker().reportCard();
        assertEquals(1, reportCard.scores().size());
        Score score = reportCard.scores().get(0);
        assertEquals("1", score.chapterIndex());
        assertEquals("0", score.questionIndex());
        assertTrue(score.result());
        assertFalse(captured.shouldSendSMS());
    }

    @Test
    public void shouldIncrementCourseAttemptsAndSetSMSFlagToTrueForAGivenFLWWhenInteractionIsCourseCompletion() {
        CertificationCourseStateRequestList stateRequestList = new CertificationCourseStateRequestList("123456", "123");

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        ReportCard reportCard = mock(ReportCard.class);
        when(reportCard.totalScore()).thenReturn(FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE + 1);
        ReflectionTestUtils.setField(frontLineWorker, "reportCard", reportCard);

        when(frontlineWorkerService.findByCallerId("123")).thenReturn(frontLineWorker);

        String json = "{\"result\":true,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"playCourseResult\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"2012-03-08T12:54:57Z\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";
        stateRequestList.add(json, "1");

        certificateCourseService.saveState(stateRequestList);

        ArgumentCaptor<CertificateCourseStateFlwRequest> captor = ArgumentCaptor.forClass(CertificateCourseStateFlwRequest.class);
        verify(frontlineWorkerService).update(captor.capture());
        CertificateCourseStateFlwRequest captured = captor.getValue();

        assertEquals(new Integer(1), frontLineWorker.currentCourseAttempt());
        assertTrue(captured.shouldSendSMS());
    }

    @Test
    public void shouldModifyFLWAggregateFromTheCertificateCourseStateRequestList() {
        CertificationCourseStateRequestList stateRequestList = new CertificationCourseStateRequestList("123456", "123");

        FrontLineWorker frontLineWorker = new FrontLineWorker();

        when(frontlineWorkerService.findByCallerId("123")).thenReturn(frontLineWorker);

        String json1 = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"startCertificationCourse\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"2012-03-08T12:54:57Z\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":0}";

        String json2 = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"startQuiz\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"2012-03-08T12:54:57Z\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";

        String json3 = "{\"result\":true,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"playAnswerExplanation\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"2012-03-08T12:54:57Z\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";

        stateRequestList.add(json1, "1");
        stateRequestList.add(json2, "2");
        stateRequestList.add(json3, "3");

        certificateCourseService.saveState(stateRequestList);

        ArgumentCaptor<CertificateCourseStateFlwRequest> captor = ArgumentCaptor.forClass(CertificateCourseStateFlwRequest.class);
        verify(frontlineWorkerService).update(captor.capture());
        CertificateCourseStateFlwRequest captured = captor.getValue();

        ReportCard reportCard = captured.getFrontLineWorker().reportCard();
        assertEquals(1, reportCard.scores().size());

        Score score = reportCard.scores().get(0);
        assertEquals("1", score.chapterIndex());
        assertEquals("0", score.questionIndex());
        assertTrue(score.result());
        assertFalse(captured.shouldSendSMS());
    }

}
