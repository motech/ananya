package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.ReportCard;
import org.motechproject.ananya.domain.Score;
import org.motechproject.ananya.request.CertificationCourseStateRequestList;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.motechproject.ananya.service.publish.DataPublishService;
import org.springframework.test.util.ReflectionTestUtils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.never;
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
    @Mock
    private SendSMSService sendSMSService;


    @Before
    public void setUp() {
        initMocks(this);
        certificateCourseService = new CertificateCourseService(certificateCourseLogService, frontlineWorkerService, dataPublishService, sendSMSService);
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

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(frontlineWorkerService).updateCertificateCourseStateFor(captor.capture());
        FrontLineWorker captured = captor.getValue();
        assertTrue(captured.reportCard().scores().isEmpty());
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

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(frontlineWorkerService).updateCertificateCourseStateFor(captor.capture());
        FrontLineWorker captured = captor.getValue();
        assertEquals(1, captured.reportCard().scores().size());
        assertEquals(chapterIndexWhoseScoresShouldNotBeCleared, captured.reportCard().scores().get(0).chapterIndex());
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
    public void shouldIncrementCourseAttemptsAndSendSMSForAGivenFLWWhenInteractionIsCourseCompletionAndScoreIsPassingScore() {
        CertificationCourseStateRequestList stateRequestList = new CertificationCourseStateRequestList("123456", "123");

        FrontLineWorker frontLineWorker = new FrontLineWorker("123", "airtel");
        ReportCard reportCard = mock(ReportCard.class);
        when(reportCard.totalScore()).thenReturn(FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE + 1);
        ReflectionTestUtils.setField(frontLineWorker, "reportCard", reportCard);

        when(frontlineWorkerService.findByCallerId("123")).thenReturn(frontLineWorker);

        String json = "{\"result\":true,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"playCourseResult\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"2012-03-08T12:54:57Z\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";
        stateRequestList.add(json, "1");

        certificateCourseService.saveState(stateRequestList);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(frontlineWorkerService).updateCertificateCourseStateFor(captor.capture());
        FrontLineWorker captured = captor.getValue();

        assertEquals(new Integer(1), captured.currentCourseAttempt());

        verify(sendSMSService).buildAndSendSMS("123", "S01D000B000V000", 1);
    }

    @Test
    public void shouldNotSendSMSForAGivenFLWWhenInteractionIsNotCourseCompletionAndScoreIsPassingScore() {
        CertificationCourseStateRequestList stateRequestList = new CertificationCourseStateRequestList("123456", "123");

        FrontLineWorker frontLineWorker = new FrontLineWorker("123", "airtel");
        ReportCard reportCard = mock(ReportCard.class);
        when(reportCard.totalScore()).thenReturn(FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE + 1);
        ReflectionTestUtils.setField(frontLineWorker, "reportCard", reportCard);

        when(frontlineWorkerService.findByCallerId("123")).thenReturn(frontLineWorker);

        String json = "{\"result\":true,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"playAnswerExplanation\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"2012-03-08T12:54:57Z\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";
        stateRequestList.add(json, "1");

        certificateCourseService.saveState(stateRequestList);

        verify(sendSMSService, never()).buildAndSendSMS("123", "S01D000B000V000", 1);
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

}
