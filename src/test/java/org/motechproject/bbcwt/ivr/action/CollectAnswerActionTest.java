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
import org.motechproject.bbcwt.util.UUIDUtil;

import java.util.Date;

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
    public void shouldPlayInvalidInputAndSetPostHelpInteractionToPreviousQuestionIfInputIsNotNumeric() {
        final int invalidInputCountBeforeThisInput = 1;
        setInvalidInputCountBeforeThisInputAs(invalidInputCountBeforeThisInput);

        Milestone atQuestion1WithLinkedRefs = new Milestone(healthWorker.getId(), chapterWithThreeQuestions.getId(), null, chapterWithThreeQuestions.getQuestionByNumber(1).getId(), new Date());
        atQuestion1WithLinkedRefs.setChapter(chapterWithThreeQuestions);

        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(atQuestion1WithLinkedRefs);
        String invalidInputMessage = "Invalid_Input.wav";
        when(messages.get(IVRMessage.INVALID_INPUT)).thenReturn(invalidInputMessage);

        String nextAction = collectAnswerAction.handle(new IVRRequest(null, null, null, "#1"), request, response);

        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + invalidInputMessage);
        verify(session).setAttribute(IVR.Attributes.NAVIGATION_POST_HELP, "forward:/chapter/1/question/1");
        verifyThatEveryActionIsForwardingToHelpHandlerSoThatHelpIsEnabled();
        verify(milestonesRepository, never()).markLastMilestoneFinish(healthWorker.getCallerId());

        verifyInvalidInputCountHasBeenIncremented(invalidInputCountBeforeThisInput);
    }

    private void verifyThatEveryActionIsForwardingToHelpHandlerSoThatHelpIsEnabled() {
        verify(session).setAttribute(IVR.Attributes.NEXT_INTERACTION, "/collectAnswer/helpHandler");
    }

    @Test
    public void shouldPlayInvalidInputAndSetPostHelpInteractionToPreviousQuestionIfAnswerIsNotAValidOption() {
        final int invalidInputCountBeforeThisInput = 1;
        setInvalidInputCountBeforeThisInputAs(invalidInputCountBeforeThisInput);

        Milestone atQuestion1WithLinkedRefs = new Milestone(healthWorker.getId(), chapterWithThreeQuestions.getId(), null, chapterWithThreeQuestions.getQuestionByNumber(1).getId(), new Date());
        atQuestion1WithLinkedRefs.setChapter(chapterWithThreeQuestions);

        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(atQuestion1WithLinkedRefs);
        String invalidInputMessage = "Invalid_Input.wav";
        when(messages.get(IVRMessage.INVALID_INPUT)).thenReturn(invalidInputMessage);

        String nextAction = collectAnswerAction.handle(new IVRRequest(null, null, null, "4"), request, response);

        verifyThatEveryActionIsForwardingToHelpHandlerSoThatHelpIsEnabled();
        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + invalidInputMessage);
        verify(session).setAttribute(IVR.Attributes.NAVIGATION_POST_HELP, "forward:/chapter/1/question/1");
        verify(milestonesRepository, never()).markLastMilestoneFinish(healthWorker.getCallerId());

        verifyInvalidInputCountHasBeenIncremented(invalidInputCountBeforeThisInput);
    }

    @Test
    public void shouldPlayHelpAndSetPostHelpInteractionToExistingUserActionIfHelpIsRequested() {
        final String IVR_HELP_AUDIO = "ivr_help_audio.wav";
        when(messages.get(IVRMessage.IVR_HELP)).thenReturn(IVR_HELP_AUDIO);
        when(messages.absoluteFileLocation(IVR_HELP_AUDIO)).thenReturn(CONTENT_LOCATION + IVR_HELP_AUDIO);

        String nextAction = collectAnswerAction.handle(new IVRRequest(null, null, null, "%"), request, response);

        verifyThatEveryActionIsForwardingToHelpHandlerSoThatHelpIsEnabled();
        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + IVR_HELP_AUDIO);
        verify(session).setAttribute(IVR.Attributes.NAVIGATION_POST_HELP, "forward:/existingUserHandler");
    }

    @Test
    public void shouldSkipQuizAndProceedToNextChapterIfInvalidInputsAreGivenMoreThanPermissibleNumberOfTimes() {
        setInvalidInputCountBeforeThisInputAs(Integer.parseInt(ALLOWED_NUMBER_OF_INVALID_INPUT));

        Milestone atQuestion1WithLinkedRefs = new Milestone(healthWorker.getId(), chapterWithThreeQuestions.getId(), null, chapterWithThreeQuestions.getQuestionByNumber(1).getId(), new Date());
        atQuestion1WithLinkedRefs.setChapter(chapterWithThreeQuestions);

        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(atQuestion1WithLinkedRefs);

        String nextAction = collectAnswerAction.handle(new IVRRequest(null, null, null, "#1"), request, response);

        verifyThatEveryActionIsForwardingToHelpHandlerSoThatHelpIsEnabled();
        verify(session).setAttribute(IVR.Attributes.NAVIGATION_POST_HELP, "forward:/startNextChapter");
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldForwardToPreviousQuestionIfAnswerIsNotGiven() {
        int noInputCountBeforeThisInput = 1;
        setNoInputCountBeforeThisInputAs(1);

        Milestone atQuestion1WithLinkedRefs = new Milestone(healthWorker.getId(), chapterWithThreeQuestions.getId(), null, chapterWithThreeQuestions.getQuestionByNumber(1).getId(), new Date());
        atQuestion1WithLinkedRefs.setChapter(chapterWithThreeQuestions);

        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(atQuestion1WithLinkedRefs);

        String nextAction = collectAnswerAction.handle(new IVRRequest(null, null, null, ""), request, response);

        verifyThatEveryActionIsForwardingToHelpHandlerSoThatHelpIsEnabled();
        verify(session).setAttribute(IVR.Attributes.NAVIGATION_POST_HELP, "forward:/chapter/1/question/1");
        verify(milestonesRepository, never()).markLastMilestoneFinish(healthWorker.getCallerId());

        verifyNoInputCountHasBeenIncremented(noInputCountBeforeThisInput);
    }

    @Test
    public void shouldSkipQuizAndProceedToNextChapterIfNoInputsAreGivenMoreThanPermissibleNumberOfTimes() {
        setNoInputCountBeforeThisInputAs(Integer.parseInt(ALLOWED_NUMBER_OF_NO_INPUT));

        Milestone atQuestion1WithLinkedRefs = new Milestone(healthWorker.getId(), chapterWithThreeQuestions.getId(), null, chapterWithThreeQuestions.getQuestionByNumber(1).getId(), new Date());
        atQuestion1WithLinkedRefs.setChapter(chapterWithThreeQuestions);

        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(atQuestion1WithLinkedRefs);

        String nextAction = collectAnswerAction.handle(new IVRRequest(null, null, null, ""), request, response);

        verifyThatEveryActionIsForwardingToHelpHandlerSoThatHelpIsEnabled();
        verify(session).setAttribute(IVR.Attributes.NAVIGATION_POST_HELP, "forward:/startNextChapter");
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldSubmitResposeToReportCardRepository() {
        setInvalidInputCountBeforeThisInputAs(1);
        setNoInputCountBeforeThisInputAs(1);

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

        verifyThatEveryActionIsForwardingToHelpHandlerSoThatHelpIsEnabled();
        verify(reportCardsRepository).addUserResponse(healthWorker.getCallerId(), chapterWithThreeQuestions.getNumber(), question1.getNumber(), keyedResponse);
        verify(milestonesRepository).markLastMilestoneFinish(healthWorker.getCallerId());
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldPlayCorrectAnswerExplanationIfTheAnswerIsCorrect() {
        setInvalidInputCountBeforeThisInputAs(1);
        setNoInputCountBeforeThisInputAs(1);

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

        verifyThatEveryActionIsForwardingToHelpHandlerSoThatHelpIsEnabled();
        verify(ivrDtmfBuilder).addPlayAudio(CONTENT_LOCATION + question1.getCorrectAnswerExplanationLocation());
        verify(milestonesRepository).markLastMilestoneFinish(healthWorker.getCallerId());
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldPlayIncorrectAnswerExplanationIfTheAnswerIsIncorrect() {
        setInvalidInputCountBeforeThisInputAs(1);
        setNoInputCountBeforeThisInputAs(1);

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

        verifyThatEveryActionIsForwardingToHelpHandlerSoThatHelpIsEnabled();
        verify(ivrDtmfBuilder).addPlayAudio(CONTENT_LOCATION + question1.getIncorrectAnswerExplanationLocation());
        verify(milestonesRepository).markLastMilestoneFinish(healthWorker.getCallerId());
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldForwardToNextQuestionIfThereAreMoreQuestions() {
        setInvalidInputCountBeforeThisInputAs(1);
        setNoInputCountBeforeThisInputAs(1);

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

        verifyThatEveryActionIsForwardingToHelpHandlerSoThatHelpIsEnabled();
        verify(session).setAttribute(IVR.Attributes.NAVIGATION_POST_HELP, "forward:/chapter/1/question/2");
        verify(milestonesRepository).markLastMilestoneFinish(healthWorker.getCallerId());
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldForwardToScoreReportingIfTheResponseIsForLastQuestion() {
        setInvalidInputCountBeforeThisInputAs(1);
        setNoInputCountBeforeThisInputAs(1);

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

        verifyThatEveryActionIsForwardingToHelpHandlerSoThatHelpIsEnabled();
        verify(session).setAttribute(IVR.Attributes.NAVIGATION_POST_HELP, "forward:/informScore");
        verify(milestonesRepository).markLastMilestoneFinish(healthWorker.getCallerId());
        verifyInvalidAndNoInputCountsAreReset();
    }

    private void setInvalidInputCountBeforeThisInputAs(int invalidInputCountBeforeThisInput) {
        when(session.getAttribute(IVR.Attributes.INVALID_INPUT_COUNT)).thenReturn(invalidInputCountBeforeThisInput);
    }

    private void setNoInputCountBeforeThisInputAs(int noInputCountBeforeThisInput) {
        when(session.getAttribute(IVR.Attributes.NO_INPUT_COUNT)).thenReturn(noInputCountBeforeThisInput);
    }

    private void verifyInvalidAndNoInputCountsAreReset() {
        verify(session).setAttribute(IVR.Attributes.INVALID_INPUT_COUNT, 0);
        verify(session).setAttribute(IVR.Attributes.NO_INPUT_COUNT, 0);
    }

    private void verifyInvalidInputCountHasBeenIncremented(int invalidInputCountBeforeThisInput) {
        verify(session).setAttribute(IVR.Attributes.INVALID_INPUT_COUNT, invalidInputCountBeforeThisInput+1);
    }

    private void verifyNoInputCountHasBeenIncremented(int noInputCountBeforeThisInput) {
        verify(session).setAttribute(IVR.Attributes.NO_INPUT_COUNT, noInputCountBeforeThisInput+1);
    }
}
