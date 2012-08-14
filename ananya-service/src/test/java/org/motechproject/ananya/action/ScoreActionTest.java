package org.motechproject.ananya.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.ReportCard;
import org.motechproject.ananya.domain.Score;
import org.motechproject.ananya.service.FrontLineWorkerService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ScoreActionTest {

    @Mock
    private FrontLineWorkerService frontlineWorkerService;
    private ScoreAction scoreAction;


    @Before
    public void setUp() {
        initMocks(this);
        scoreAction = new ScoreAction();
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

        assertTrue(frontLineWorker.reportCard().scores().isEmpty());
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

        assertEquals(1, frontLineWorker.reportCard().scores().size());
        assertEquals(chapterIndexWhoseScoresShouldNotBeCleared, frontLineWorker.reportCard().scores().get(0).chapterIndex());
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


        ReportCard reportCard = frontLineWorker.reportCard();
        assertEquals(1, reportCard.scores().size());

        Score score = reportCard.scores().get(0);
        assertEquals("1", score.chapterIndex());
        assertEquals("0", score.questionIndex());
        assertTrue(score.result());
    }

}
