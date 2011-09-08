package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.*;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.ChaptersRespository;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.motechproject.bbcwt.repository.ReportCardsRepository;
import org.motechproject.bbcwt.util.UUIDUtil;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class CollectAnswerActionTest extends BaseActionTest {
    private CollectAnswerAction collectAnswerAction;

    @Mock
    private MilestonesRepository milestonesRepository;
    @Mock
    private ReportCardsRepository reportCardsRepository;

    private Chapter chapterWithThreeQuestions;
    private HealthWorker healthWorker;

    @Before
    public void setUp() {
        chapterWithThreeQuestions = new Chapter(1);
        chapterWithThreeQuestions.setId(UUIDUtil.newUUID());
        chapterWithThreeQuestions.addQuestion(new Question(1,
                                                            "http://location/chapter/1/question/1",
                1,
                                                            "http://location/chapter/1/question/1/correct", "http://location/chapter/1/question/1/incorrect"));
        chapterWithThreeQuestions.addQuestion(new Question(2,
                                                            "http://location/chapter/1/question/2",
                2,
                                                            "http://location/chapter/1/question/2/correct", "http://location/chapter/1/question/2/incorrect"));
        chapterWithThreeQuestions.addQuestion(new Question(3,
                                                            "http://location/chapter/1/question/3",
                2,
                                                            "http://location/chapter/1/question/3/correct", "http://location/chapter/1/question/3/incorrect"));
        String callerId = "9989989980";
        healthWorker = new HealthWorker(callerId);
        healthWorker.setId(UUIDUtil.newUUID());
        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(callerId);

        collectAnswerAction = new CollectAnswerAction(milestonesRepository, reportCardsRepository, messages);
    }

    @Test
    public void shouldPlayInvalidInputAndForwardToPreviousQuestionIfInputIsNotValid() {
        Milestone atQuestion1WithLinkedRefs = new Milestone(healthWorker.getId(), chapterWithThreeQuestions.getId(), null, chapterWithThreeQuestions.getQuestionByNumber(1).getId(), new Date());
        atQuestion1WithLinkedRefs.setChapter(chapterWithThreeQuestions);

        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(atQuestion1WithLinkedRefs);
        String invalidInputMessage = "Invalid_Input.wav";
        when(messages.get(IVRMessage.INVALID_INPUT)).thenReturn(invalidInputMessage);

        String nextAction = collectAnswerAction.handle(new IVRRequest(null, null, null, "*1"), request, response);

        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + invalidInputMessage);
        assertEquals("If non-digit is pressed as response, user should be forwarded to the previous question.", "forward:/chapter/1/question/1", nextAction);
        verify(milestonesRepository, never()).markLastMilestoneFinish(healthWorker.getCallerId());
    }

    @Test
    public void shouldPlayInvalidInputAndForwardToPreviousQuestionIfAnswerIsNotAValidOption() {
        Milestone atQuestion1WithLinkedRefs = new Milestone(healthWorker.getId(), chapterWithThreeQuestions.getId(), null, chapterWithThreeQuestions.getQuestionByNumber(1).getId(), new Date());
        atQuestion1WithLinkedRefs.setChapter(chapterWithThreeQuestions);

        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(atQuestion1WithLinkedRefs);
        String invalidInputMessage = "Invalid_Input.wav";
        when(messages.get(IVRMessage.INVALID_INPUT)).thenReturn(invalidInputMessage);

        String nextAction = collectAnswerAction.handle(new IVRRequest(null, null, null, "4"), request, response);

        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + invalidInputMessage);
        assertEquals("If non-digit is pressed as response, user should be forwarded to the previous question.", "forward:/chapter/1/question/1", nextAction);
        verify(milestonesRepository, never()).markLastMilestoneFinish(healthWorker.getCallerId());
    }

    @Test
    public void shouldSubmitResposeToReportCardRepository() {
        Question question1 = chapterWithThreeQuestions.getQuestionByNumber(1);
        Milestone atQuestion1WithLinkedRefs = new Milestone(healthWorker.getId(), chapterWithThreeQuestions.getId(), null, question1.getId(), new Date());
        atQuestion1WithLinkedRefs.setChapter(chapterWithThreeQuestions);

        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(atQuestion1WithLinkedRefs);
        int keyedResponse = 1;

        ReportCard.HealthWorkerResponseToQuestion healthWorkerResponseToQuestion = new ReportCard.HealthWorkerResponseToQuestion(
                                                    chapterWithThreeQuestions.getId(), question1.getId(), keyedResponse);

        when(reportCardsRepository.addUserResponse(healthWorker.getCallerId(), chapterWithThreeQuestions.getNumber(),
                                                    question1.getNumber(), keyedResponse)).thenReturn(healthWorkerResponseToQuestion);

        String nextAction = collectAnswerAction.handle(new IVRRequest(null, null, null, ""+keyedResponse), request, response);

        verify(reportCardsRepository).addUserResponse(healthWorker.getCallerId(), chapterWithThreeQuestions.getNumber(), question1.getNumber(), keyedResponse);
        verify(milestonesRepository).markLastMilestoneFinish(healthWorker.getCallerId());
    }

    @Test
    public void shouldPlayCorrectAnswerExplanationIfTheAnswerIsCorrect() {
        Question question1 = chapterWithThreeQuestions.getQuestionByNumber(1);
        Milestone atQuestion1WithLinkedRefs = new Milestone(healthWorker.getId(), chapterWithThreeQuestions.getId(), null, question1.getId(), new Date());
        atQuestion1WithLinkedRefs.setChapter(chapterWithThreeQuestions);

        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(atQuestion1WithLinkedRefs);

        int keyedResponse = 1;

        ReportCard.HealthWorkerResponseToQuestion correctResponse = new ReportCard.HealthWorkerResponseToQuestion(
                                                    chapterWithThreeQuestions.getId(), question1.getId(), keyedResponse);
        correctResponse.setCorrect(true);

        when(reportCardsRepository.addUserResponse(healthWorker.getCallerId(), chapterWithThreeQuestions.getNumber(),
                question1.getNumber(), keyedResponse)).thenReturn(correctResponse);

        String nextAction = collectAnswerAction.handle(new IVRRequest(null, null, null, ""+keyedResponse), request, response);

        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + question1.getCorrectAnswerExplanationLocation());
        verify(milestonesRepository).markLastMilestoneFinish(healthWorker.getCallerId());
    }

    @Test
    public void shouldPlayIncorrectAnswerExplanationIfTheAnswerIsIncorrect() {
        Question question1 = chapterWithThreeQuestions.getQuestionByNumber(1);
        Milestone atQuestion1WithLinkedRefs = new Milestone(healthWorker.getId(), chapterWithThreeQuestions.getId(), null, question1.getId(), new Date());
        atQuestion1WithLinkedRefs.setChapter(chapterWithThreeQuestions);

        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(atQuestion1WithLinkedRefs);
        int keyedResponse = 2;

        ReportCard.HealthWorkerResponseToQuestion incorrectResponse = new ReportCard.HealthWorkerResponseToQuestion(
                                                    chapterWithThreeQuestions.getId(), question1.getId(), keyedResponse);
        incorrectResponse.setCorrect(false);

        when(reportCardsRepository.addUserResponse(healthWorker.getCallerId(), chapterWithThreeQuestions.getNumber(),
                                                    question1.getNumber(), keyedResponse)).thenReturn(incorrectResponse);

        String nextAction = collectAnswerAction.handle(new IVRRequest(null, null, null, ""+keyedResponse), request, response);

        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + question1.getIncorrectAnswerExplanationLocation());
        verify(milestonesRepository).markLastMilestoneFinish(healthWorker.getCallerId());
    }

    @Test
    public void shouldForwardToNextQuestionIfThereAreMoreQuestions() {
        Question question1 = chapterWithThreeQuestions.getQuestionByNumber(1);
        Milestone atQuestion1WithLinkedRefs = new Milestone(healthWorker.getId(), chapterWithThreeQuestions.getId(), null, question1.getId(), new Date());
        atQuestion1WithLinkedRefs.setChapter(chapterWithThreeQuestions);

        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(atQuestion1WithLinkedRefs);
        int keyedResponse = 1;

        ReportCard.HealthWorkerResponseToQuestion correctResponse = new ReportCard.HealthWorkerResponseToQuestion(
                                                    chapterWithThreeQuestions.getId(), question1.getId(), keyedResponse);
        correctResponse.setCorrect(true);

        when(reportCardsRepository.addUserResponse(healthWorker.getCallerId(), chapterWithThreeQuestions.getNumber(),
                question1.getNumber(), keyedResponse)).thenReturn(correctResponse);

        String nextAction = collectAnswerAction.handle(new IVRRequest(null, null, null, ""+keyedResponse), request, response);

        assertEquals("Should forward to play next question if there are more questions.", "forward:/chapter/1/question/2", nextAction);
        verify(milestonesRepository).markLastMilestoneFinish(healthWorker.getCallerId());
    }

    @Test
    public void shouldForwardToScoreReportingIfTheResponseIsForLastQuestion() {
        Question lastQuestion = chapterWithThreeQuestions.getQuestionByNumber(3);
        Milestone atQuestion1WithLinkedRefs = new Milestone(healthWorker.getId(), chapterWithThreeQuestions.getId(), null, lastQuestion.getId(), new Date());
        atQuestion1WithLinkedRefs.setChapter(chapterWithThreeQuestions);

        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(atQuestion1WithLinkedRefs);

        int keyedResponse = 1;

        ReportCard.HealthWorkerResponseToQuestion correctResponse = new ReportCard.HealthWorkerResponseToQuestion(
                                                    chapterWithThreeQuestions.getId(), lastQuestion.getId(), keyedResponse);
        correctResponse.setCorrect(true);

        when(reportCardsRepository.addUserResponse(healthWorker.getCallerId(), chapterWithThreeQuestions.getNumber(),
                lastQuestion.getNumber(), keyedResponse)).thenReturn(correctResponse);

        String nextAction = collectAnswerAction.handle(new IVRRequest(null, null, null, ""+keyedResponse), request, response);
        assertEquals("Should forward to score reporting action if there are no more questions.", "forward:/informScore", nextAction);
        verify(milestonesRepository).markLastMilestoneFinish(healthWorker.getCallerId());
    }
}
