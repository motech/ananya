package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.*;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.motechproject.bbcwt.repository.ReportCardsRepository;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class CourseCertificateAndSMSMenuActionTest extends BaseActionTest {
    @Mock
    private MilestonesRepository milestonesRepository;
    @Mock
    private ReportCardsRepository reportCardsRepository;
    private String callerId;
    private HealthWorker healthWorker;
    private Chapter chapter;
    private ReportCard reportCard;
    private CourseCertificateAndSMSMenuAction courseCertificateAndSMSMenuAction;

    @Before
    public void setup() {
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

        courseCertificateAndSMSMenuAction = new CourseCertificateAndSMSMenuAction(milestonesRepository, reportCardsRepository, messages);
    }

    @Test
    public void shouldFetchThePromptToBePlayedFromChapter() {
        courseCertificateAndSMSMenuAction.handle(new IVRRequest(null, null, null, null), request, response);
        verify(milestonesRepository).currentMilestoneWithLinkedReferences(callerId);
        verify(reportCardsRepository).findByHealthWorker(healthWorker);
        final int scoredMarks = reportCard.scoreEarned(chapter).getScoredMarks();
        verify(ivrDtmfBuilder).addPlayAudio(CONTENT_LOCATION + chapter.getCourseSummaryPromptForScore(scoredMarks));
        verify(ivrResponseBuilder).withCollectDtmf(collectDtmf);
    }

    @Test
    public void shouldSetNextInteractionToCourseCertificateAndSMSMenuAnswerActionTest() {
         courseCertificateAndSMSMenuAction.handle(new IVRRequest(null, null, null, null), request, response);
         verify(session).setAttribute(IVR.Attributes.NEXT_INTERACTION, "/certificateAndSMSMenuAnswer");
    }
}