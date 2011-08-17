package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.*;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.motechproject.bbcwt.repository.ReportCardsRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class InformScoreActionTest extends BaseActionTest {
    private InformScoreAction informScoreAction;

    @Mock
    private MilestonesRepository milestonesRepository;
    @Mock
    private ReportCardsRepository reportCardsRepository;

    private String callerId;
    private HealthWorker healthWorker;
    private ReportCard reportCard;
    private Chapter chapter;

    @Before
    public void setUp() {
        callerId = "9989989908";

        healthWorker = new HealthWorker(callerId);

        chapter = new Chapter(1);
        Question question1 = new Question(1, null, 1, null, null);
        Question question2 = new Question(2, null, 1, null, null);
        Question question3 = new Question(3, null, 2, null, null);
        chapter.addQuestion(question1);
        chapter.addQuestion(question2);
        chapter.addQuestion(question3);

        Milestone inLastQuestion = new Milestone();
        inLastQuestion.setHealthWorker(healthWorker);
        inLastQuestion.setChapter(chapter);

        reportCard = new ReportCard();
        reportCard.recordResponse(chapter, question1, question1.getCorrectOption());
        reportCard.recordResponse(chapter, question2, question1.getCorrectOption());
        //Following records a wrong option
        reportCard.recordResponse(chapter, question3, question1.getCorrectOption()-1);

        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(callerId);
        when(milestonesRepository.currentMilestoneWithLinkedReferences(callerId)).thenReturn(inLastQuestion);
        when(reportCardsRepository.findByHealthWorker(healthWorker)).thenReturn(reportCard);

        informScoreAction = new InformScoreAction(milestonesRepository, reportCardsRepository, messages);
    }

    @Test
    public void shouldRequestScoreForCurrentChapter() {
        informScoreAction.handle(new IVRRequest(), request, response);
        verify(milestonesRepository).currentMilestoneWithLinkedReferences(callerId);
        verify(reportCardsRepository).findByHealthWorker(healthWorker);
    }

    @Test
    public void shouldPlayTheScoreFetched() {
        final String QUIZ_COMPLETION_MSG = "You have completed the quiz for the chapter.";
        final String SCORE_INFORMATION_START = "Your score is: ";
        final String SCORE_INFORMATION_OUT_OF = "out of";

        when(messages.get(IVRMessage.END_OF_QUIZ_MESSAGE)).thenReturn(QUIZ_COMPLETION_MSG);
        when(messages.get(IVRMessage.INFORM_SCORE_START)).thenReturn(SCORE_INFORMATION_START);
        when(messages.get(IVRMessage.INFORM_SCORE_OUTOF)).thenReturn(SCORE_INFORMATION_OUT_OF);

        ReportCard.ScoreSummary scoreSummaryForChapter = reportCard.scoreEarned(chapter);

        informScoreAction.handle(new IVRRequest(), request, response);

        final String scoreReportFile = scoreSummaryForChapter.getScoredMarks() + "_out_of_" + scoreSummaryForChapter.getMaximumMarks() + ".wav";

        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + scoreReportFile);
    }

    @Test
    public void shouldConstructFileNameForPlayingScore() {
        assertEquals(informScoreAction.scoreReportFileName(null, 0, 4), "0_out_of_4.wav");
        assertEquals(informScoreAction.scoreReportFileName(null, 1, 4), "1_out_of_4.wav");
        assertEquals(informScoreAction.scoreReportFileName(null, 2, 4), "2_out_of_4.wav");
        assertEquals(informScoreAction.scoreReportFileName(null, 3, 4), "3_out_of_4.wav");
        assertEquals(informScoreAction.scoreReportFileName(null, 4, 4), "4_out_of_4.wav");
    }
}