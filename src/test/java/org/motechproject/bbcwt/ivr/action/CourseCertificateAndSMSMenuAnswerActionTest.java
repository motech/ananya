package org.motechproject.bbcwt.ivr.action;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.*;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.listeners.SendSMSHandler;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.motechproject.bbcwt.repository.ReportCardsRepository;
import org.motechproject.model.MotechEvent;
import org.powermock.api.mockito.PowerMockito;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CourseCertificateAndSMSMenuAnswerActionTest extends BaseActionTest {
    public static final String GOOD_SCORE_SUMMARY = "Good work. Remember this summary.";
    public static final String BELOW_PAR_SCORE_SUMMARY = "Could do better. Remember very well, this summary.";
    @Mock
    private MilestonesRepository milestonesRepository;
    @Mock
    private ReportCardsRepository reportCardsRepository;
    @Mock
    private SendSMSHandler sendSMSHandler;

    private CourseCertificateAndSMSMenuAnswerAction courseCertificateAndSMSMenuAnswerAction;
    private String callerId;
    private HealthWorker healthWorker;
    private Chapter chapter;
    private ReportCard reportCard;

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
        chapter.setGoodScoreSummary(GOOD_SCORE_SUMMARY);
        chapter.setBelowParScoreSummary(BELOW_PAR_SCORE_SUMMARY);

        Milestone inLastQuestion = new Milestone();
        inLastQuestion.setHealthWorker(healthWorker);
        inLastQuestion.setChapter(chapter);

        reportCard = new ReportCard();
        reportCard.recordResponse(chapter, question1, question1.getCorrectOption());
        reportCard.recordResponse(chapter, question2, question1.getCorrectOption());
        //Following records a wrong option
        reportCard.recordResponse(chapter, question3, question1.getCorrectOption()-1);

        PowerMockito.when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(callerId);
        PowerMockito.when(milestonesRepository.currentMilestoneWithLinkedReferences(callerId)).thenReturn(inLastQuestion);
        PowerMockito.when(reportCardsRepository.findByHealthWorker(healthWorker)).thenReturn(reportCard);

        courseCertificateAndSMSMenuAnswerAction = new CourseCertificateAndSMSMenuAnswerAction(milestonesRepository, reportCardsRepository, sendSMSHandler, messages);
    }

    @Test
    public void shouldSendSMSIfOptionPressedIs9() {
        setInvalidInputCountBeforeThisInputAs(0);
        setNoInputCountBeforeThisInputAs(0);
        String nextAction = courseCertificateAndSMSMenuAnswerAction.handle(new IVRRequest(null, null, null, "9"), request, response);

        verify(sendSMSHandler).sendSMS(argThat(new ArgumentMatcher<MotechEvent>() {
            @Override
            public boolean matches(Object o) {
                if(!o.getClass().equals(MotechEvent.class)) {
                    return false;
                }
                Map<String, Object> parameters = ((MotechEvent)o).getParameters();
                return callerId.equals(parameters.get("number")) &&
                BELOW_PAR_SCORE_SUMMARY.equals(parameters.get("text"));
            }
        }));
        assertThat(nextAction, is("forward:/endOfQuizMenu"));
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldPlayHelpIfRequested() {
        setInvalidInputCountBeforeThisInputAs(1);
        setNoInputCountBeforeThisInputAs(1);
        final String IVR_HELP_AUDIO = "ivr_help_audio.wav";
        when(messages.get(IVRMessage.IVR_HELP)).thenReturn(IVR_HELP_AUDIO);
        when(messages.absoluteFileLocation(IVR_HELP_AUDIO)).thenReturn(CONTENT_LOCATION + IVR_HELP_AUDIO);

        IVRRequest ivrRequest = new IVRRequest(null, null, null, "*");

        String nextAction = courseCertificateAndSMSMenuAnswerAction.handle(ivrRequest, request, response);

        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + IVR_HELP_AUDIO);
        verifyInvalidAndNoInputCountsAreReset();
    }


    @Test
    public void shouldFowardToCourseCertificateMenuAfterHelpHasBeenPlayed() {
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "*");

        String nextAction = courseCertificateAndSMSMenuAnswerAction.handle(ivrRequest, request, response);

        assertThat(nextAction, is("forward:/certificateAndSMSMenu"));
    }

    @Test
    public void shouldForwardUserToCourseCertificateMenuIfInvalidInputIsPressed() {
        final int invalidInputCountBeforeThisInput = 0;
        setInvalidInputCountBeforeThisInputAs(invalidInputCountBeforeThisInput);

        final String INVALID_INPUT = "invalid input";
        when(messages.get(IVRMessage.INVALID_INPUT)).thenReturn(INVALID_INPUT);

        String nextAction = courseCertificateAndSMSMenuAnswerAction.handle(new IVRRequest(null, null, null, "#"), request, response);

        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + INVALID_INPUT);
        assertThat(nextAction, is("forward:/certificateAndSMSMenu"));

        verifyInvalidInputCountHasBeenIncremented(invalidInputCountBeforeThisInput);
    }

    @Test
    public void shouldForwardUserToEndOfQuizMenuIfInvalidInputIsPressedMoreThanPermissibleTimes() {
        setInvalidInputCountBeforeThisInputAs(1);

        String nextAction = courseCertificateAndSMSMenuAnswerAction.handle(new IVRRequest(null, null, null, "#"), request, response);

        assertThat(nextAction, is("forward:/endOfQuizMenu"));

        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldForwardUserToCourseCertificateMenuIfNoInputIsPressed() {
        final int noInputCountBeforeThisInput = 0;
        setNoInputCountBeforeThisInputAs(noInputCountBeforeThisInput);

        String nextAction = courseCertificateAndSMSMenuAnswerAction.handle(new IVRRequest(null, null, null, ""), request, response);
        assertThat(nextAction, is("forward:/certificateAndSMSMenu"));

        verifyNoInputCountHasBeenIncremented(noInputCountBeforeThisInput);
    }

    @Test
    public void shouldForwardUserToEndOfQuizMenuIfNoInputIsGivenForMoreThanPermissibleTimes() {
        setNoInputCountBeforeThisInputAs(1);

        String nextAction = courseCertificateAndSMSMenuAnswerAction.handle(new IVRRequest(null, null, null, ""), request, response);
        assertThat(nextAction, is("forward:/endOfQuizMenu"));

        verifyInvalidAndNoInputCountsAreReset();
    }

    private void setInvalidInputCountBeforeThisInputAs(int invalidInputCountBeforeThisInput) {
        PowerMockito.when(session.getAttribute(IVR.Attributes.INVALID_INPUT_COUNT)).thenReturn(invalidInputCountBeforeThisInput);
    }

    private void setNoInputCountBeforeThisInputAs(int noInputCountBeforeThisInput) {
        PowerMockito.when(session.getAttribute(IVR.Attributes.NO_INPUT_COUNT)).thenReturn(noInputCountBeforeThisInput);
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