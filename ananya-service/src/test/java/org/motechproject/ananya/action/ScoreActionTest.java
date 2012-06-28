package org.motechproject.ananya.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.ReportCard;
import org.motechproject.ananya.domain.Score;
import org.motechproject.ananya.request.CertificateCourseStateRequestList;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.springframework.test.util.ReflectionTestUtils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class ScoreActionTest {

    @Mock
    private FrontLineWorkerService frontlineWorkerService;
    private ScoreAction scoreAction;


    @Before
    public void setUp() {
        initMocks(this);
        scoreAction = new ScoreAction(frontlineWorkerService);
    }

    @Test
    public void shouldClearAllScoresForAGivenFLWWhenInteractionIsStartCertificationCourse() {
        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.reportCard().addScore(new Score());
        when(frontlineWorkerService.findByCallerId("123")).thenReturn(frontLineWorker);

        CertificateCourseStateRequestList stateRequestList = new CertificateCourseStateRequestList("123456", "123");
        String json = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"startCertificationCourse\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":0}";
        stateRequestList.add(json, "1");

        scoreAction.process(frontLineWorker, stateRequestList);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(frontlineWorkerService).updateCertificateCourseStateFor(captor.capture());
        FrontLineWorker captured = captor.getValue();
        assertTrue(captured.reportCard().scores().isEmpty());
    }

    @Test
    public void shouldClearAllScoresForAGivenFLWWhenInteractionIsStartQuiz() {
        FrontLineWorker frontLineWorker = new FrontLineWorker();
        String chapterIndexWhoseScoresShouldNotBeCleared = "2";
        frontLineWorker.reportCard().addScore(new Score("1", "1", true));
        frontLineWorker.reportCard().addScore(new Score(chapterIndexWhoseScoresShouldNotBeCleared, "1", true));
        when(frontlineWorkerService.findByCallerId("123")).thenReturn(frontLineWorker);

        CertificateCourseStateRequestList stateRequestList = new CertificateCourseStateRequestList("123456", "123");
        String json = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"startQuiz\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";
        stateRequestList.add(json, "1");

        scoreAction.process(frontLineWorker, stateRequestList);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(frontlineWorkerService).updateCertificateCourseStateFor(captor.capture());
        FrontLineWorker captured = captor.getValue();
        assertEquals(1, captured.reportCard().scores().size());
        assertEquals(chapterIndexWhoseScoresShouldNotBeCleared, captured.reportCard().scores().get(0).chapterIndex());
    }


    @Test
    public void shouldAddScoresToAChapterForAGivenFLWWhenInteractionIsisPlayAnswerExplanation() {

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        when(frontlineWorkerService.findByCallerId("123")).thenReturn(frontLineWorker);

        CertificateCourseStateRequestList stateRequestList = new CertificateCourseStateRequestList("123456", "123");
        String json = "{\"result\":true,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"playAnswerExplanation\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";
        stateRequestList.add(json, "1");

        scoreAction.process(frontLineWorker, stateRequestList);

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

        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, "airtel");
        ReportCard reportCard = mock(ReportCard.class);
        when(reportCard.totalScore()).thenReturn(FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE + 1);
        ReflectionTestUtils.setField(frontLineWorker, "reportCard", reportCard);
        when(frontlineWorkerService.findByCallerId(callerId)).thenReturn(frontLineWorker);

        CertificateCourseStateRequestList stateRequestList = new CertificateCourseStateRequestList(callId, callerId);
        String json = "{\"result\":true,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"playCourseResult\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";
        stateRequestList.add(json, "1");

        scoreAction.process(frontLineWorker, stateRequestList);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(frontlineWorkerService).updateCertificateCourseStateFor(captor.capture());
        FrontLineWorker captured = captor.getValue();
        assertEquals(new Integer(1), captured.currentCourseAttempt());
    }




}
