package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.exceptions.WorkerDoesNotExistException;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.AllOperators;
import org.motechproject.ananya.request.CertificateCourseStateFlwRequest;
import org.motechproject.ananya.response.CallerDataResponse;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerServiceTest {

    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Mock
    private AllLocations allLocations;
    @Mock
    private SendSMSService sendSMSService; 
    @Mock
    private SMSPublisherService publisherService;
    @Mock
    private AllOperators allOperators;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerService = new FrontLineWorkerService(allFrontLineWorkers,allLocations, sendSMSService, publisherService, allOperators);
    }

    private FrontLineWorker FrontLineWorker() {
        return new FrontLineWorker("123", Designation.ANM, "123","operator");
    }

    @Test
    public void shouldTellThatUserIsRegisteredBasedOnStatusOnTheCallerData() {
        String registeredMsisdn = "123";
        FrontLineWorker registeredFrontLineWorker = FrontLineWorker();
        registeredFrontLineWorker.status(RegistrationStatus.REGISTERED);
        when(allFrontLineWorkers.findByMsisdn(registeredMsisdn)).thenReturn(registeredFrontLineWorker);
        when(allOperators.findByName("operator")).thenReturn(new Operator("operator",registeredFrontLineWorker.getCurrentJobAidUsage()-1));

        CallerDataResponse callerData = frontLineWorkerService.createCallerData(registeredMsisdn, "airtel");

        assertThat(callerData.isCallerRegistered(), is(true));
    }

    @Test
    public void shouldTellThatUserIsPartiallyRegisteredAndCreateTheFLWIfUserIsNotPresent() {
        String newMsisdn = "123";
        when(allFrontLineWorkers.findByMsisdn(newMsisdn)).thenReturn(null);

        CallerDataResponse callerData = frontLineWorkerService.createCallerData(newMsisdn, "airtel");

        assertThat(callerData.isCallerRegistered(), is(false));
        ArgumentCaptor<FrontLineWorker> frontLineWorkerArgumentCaptor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).add(frontLineWorkerArgumentCaptor.capture());
        String msisdn = frontLineWorkerArgumentCaptor.getValue().getMsisdn();
        assertEquals(newMsisdn, msisdn);
    }

    @Test
    public void shouldNotCreateANewUserIfTheUserIsAlreadyPresentAndPartiallyRegistered() {
        String registeredMsisdn = "123";
        FrontLineWorker registeredFrontLineWorker = FrontLineWorker();
        registeredFrontLineWorker.status(RegistrationStatus.PARTIALLY_REGISTERED);
        when(allFrontLineWorkers.findByMsisdn(registeredMsisdn)).thenReturn(registeredFrontLineWorker);
        when(allOperators.findByName("operator")).thenReturn(new Operator("operator",registeredFrontLineWorker.getCurrentJobAidUsage()-1));

        CallerDataResponse callerData = frontLineWorkerService.createCallerData(registeredMsisdn, "airtel");

        assertThat(callerData.isCallerRegistered(), is(false));
        ArgumentCaptor<FrontLineWorker> frontLineWorkerArgumentCaptor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers,times(0)).add(frontLineWorkerArgumentCaptor.capture());
    }

    public void shouldClearTheScoresOfTheChapterOnStartingAQuizForThatChapter(){
        String callerId = "callerId";
        int chapterIndex = 1;
        int questionIndex = 3;
        int anotherChapterIndex = 0;
        int anotherQuestionIndex = 3;
        CertificateCourseStateFlwRequest request = new CertificateCourseStateFlwRequest(chapterIndex, questionIndex, null, InteractionKeys.StartQuizInteraction, "callId", callerId);
        FrontLineWorker expectedFrontLineWorker = FrontLineWorker();
        expectedFrontLineWorker.reportCard().addScore(new ReportCard.Score(Integer.toString(chapterIndex),Integer.toString(questionIndex),true));
        expectedFrontLineWorker.reportCard().addScore(new ReportCard.Score(Integer.toString(anotherChapterIndex),Integer.toString(anotherQuestionIndex),true));
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(expectedFrontLineWorker);

        frontLineWorkerService.saveScore(request);

        assertEquals(1, expectedFrontLineWorker.reportCard().scores().size());
        verify(allFrontLineWorkers).update(expectedFrontLineWorker);
    }

    @Test
    public void shouldAddTheScoreForTheChapterOnPlayAnswerInteraction(){
        String callerId = "callerId";
        int chapterIndex = 1;
        int questionIndex = 3;
        int anotherChapterIndex = 0;
        int anotherQuestionIndex = 3;
        CertificateCourseStateFlwRequest request = new CertificateCourseStateFlwRequest(chapterIndex, questionIndex, true, InteractionKeys.PlayAnswerExplanationInteraction, "callId", callerId);
        FrontLineWorker expectedFrontLineWorker = FrontLineWorker();
        expectedFrontLineWorker.reportCard().addScore(new ReportCard.Score(Integer.toString(anotherChapterIndex),Integer.toString(anotherQuestionIndex),true));
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(expectedFrontLineWorker);

        frontLineWorkerService.saveScore(request);

        int actualChapterIndex = Integer.parseInt(expectedFrontLineWorker.reportCard().scores().get(1).chapterIndex());
        int actualQuestionIndex = Integer.parseInt(expectedFrontLineWorker.reportCard().scores().get(1).questionIndex());
        assertEquals(2, expectedFrontLineWorker.reportCard().scores().size());
        assertEquals(chapterIndex, actualChapterIndex);
        assertEquals(questionIndex, actualQuestionIndex);
        verify(allFrontLineWorkers).update(expectedFrontLineWorker);
    }

    @Test
    public void shouldIncrementTheCourseAttemptAndSendAnSMSIfScoreIsGreaterThan18OnCourseCompletion(){
        String callerId = "callerId";
        CertificateCourseStateFlwRequest request = new CertificateCourseStateFlwRequest(10, 0, null, InteractionKeys.PlayCourseResultInteraction, "callId", callerId);
        FrontLineWorker expectedFrontLineWorker = FrontLineWorker();
        setUpTestScoreSet(expectedFrontLineWorker,true);
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(expectedFrontLineWorker);

        frontLineWorkerService.saveScore(request);

        Integer courseAttemptNumber = expectedFrontLineWorker.currentCourseAttempt();
        assertEquals(1, (int) courseAttemptNumber);
        verify(allFrontLineWorkers).update(expectedFrontLineWorker);
        verify(sendSMSService).buildAndSendSMS(callerId, expectedFrontLineWorker.getLocationId(), courseAttemptNumber);
    }

    @Test
    public void shouldIncrementTheCourseAttemptButShouldNotSendAnSMSIfScoreIsLessThan18OnCourseCompletion(){
        String callerId = "callerId";
        CertificateCourseStateFlwRequest request = new CertificateCourseStateFlwRequest(10, 0, null, InteractionKeys.PlayCourseResultInteraction, "callId", callerId);
        FrontLineWorker expectedFrontLineWorker = FrontLineWorker();
        setUpTestScoreSet(expectedFrontLineWorker,false);
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(expectedFrontLineWorker);

        frontLineWorkerService.saveScore(request);

        Integer courseAttemptNumber = expectedFrontLineWorker.currentCourseAttempt();
        assertEquals(1, (int) courseAttemptNumber);
        verify(allFrontLineWorkers).update(expectedFrontLineWorker);
        verify(sendSMSService, never()).buildAndSendSMS(callerId,expectedFrontLineWorker.getLocationId(),courseAttemptNumber);
    }

    @Test
    public void shouldReturnEmptyBookmarkInCallerDataIfFrontLineWorkerDoesNotExist() {
        String msisdn = "999";
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(null);

        CallerDataResponse callerData = frontLineWorkerService.createCallerData(msisdn, "airtel");

        assertEquals("{}", callerData.getBookmark());
    }

    @Test
    public void shouldReturnBookmarkOfFrontLineWorkerInCallerData() {
        String msisdn = "999";
        FrontLineWorker frontLineWorker = FrontLineWorker();
        BookMark bookMark = new BookMark("leson", 0, 2);
        frontLineWorker.addBookMark(bookMark);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);
        when(allOperators.findByName("operator")).thenReturn(new Operator("operator",frontLineWorker.getCurrentJobAidUsage()-1));

        CallerDataResponse callerData = frontLineWorkerService.createCallerData(msisdn, "airtel");

        assertThat(callerData.getBookmark(), is(bookMark.asJson()));
    }

    @Test
    public void shouldReturnTheCorrectScoresByChapterMapWithTheCallerData(){
        String callerId = "callerId";
        CertificateCourseStateFlwRequest request = new CertificateCourseStateFlwRequest(10, 0, null, InteractionKeys.PlayCourseResultInteraction, "callId", callerId);
        FrontLineWorker expectedFrontLineWorker = FrontLineWorker();
        setUpTestScoreSet(expectedFrontLineWorker, true);
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(expectedFrontLineWorker);
        when(allOperators.findByName("operator")).thenReturn(new Operator("operator",expectedFrontLineWorker.getCurrentJobAidUsage()-1));

        CallerDataResponse callerData = frontLineWorkerService.createCallerData(callerId, "airtel");

        assertEquals(9,callerData.getScoresByChapter().size());
        assertEquals(4, (int) callerData.getScoresByChapter().get("0"));
    }

    private void setUpTestScoreSet(FrontLineWorker frontLineWorker, boolean result)
    {
        for(int chapterIndex =0; chapterIndex <9; chapterIndex++)
        {
           for(int questionIndex =0; questionIndex <4; questionIndex++)
           {
               frontLineWorker.reportCard().addScore(new ReportCard.Score(Integer.toString(chapterIndex),Integer.toString(questionIndex),result));
           }
        }
    }

    @Test
    public void shouldTellThatUserIsRegisteredBasedOnStatusOnTheJobAidCallerData() {
        String registeredMsisdn = "123";
        FrontLineWorker registeredFrontLineWorker = FrontLineWorker();
        registeredFrontLineWorker.status(RegistrationStatus.REGISTERED);
        when(allFrontLineWorkers.findByMsisdn(registeredMsisdn)).thenReturn(registeredFrontLineWorker);
        when(allOperators.findByName("operator")).thenReturn(new Operator("operator",registeredFrontLineWorker.getCurrentJobAidUsage()-1));

        JobAidCallerDataResponse callerData = frontLineWorkerService.createJobAidCallerData(registeredMsisdn, "airtel");

        assertThat(callerData.isCallerRegistered(), is(true));
    }

    @Test
    public void shouldPopulateJobAidCallerDataWithMaxUsageBeingTrueWhenUsageHasMaxedOut() {
        String msisdn = "9876543210";
        String operator = "operator";
        FrontLineWorker flw = new FrontLineWorker(msisdn, Designation.ASHA, "location", operator);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(flw);
        when(allOperators.findByName(operator)).thenReturn(new Operator(operator,flw.getCurrentJobAidUsage() - 1));

        JobAidCallerDataResponse callerData = frontLineWorkerService.createJobAidCallerData(msisdn, "airtel");

        assertEquals(true,(boolean) callerData.hasReachedMaxUsageForMonth());
    }

    @Test
    public void shouldPopulateJobAidCallerDataWithPromptsHeardForUser() {
        String msisdn = "9876543210";
        String operator = "operator";
        FrontLineWorker flw = new FrontLineWorker(msisdn, Designation.ASHA, "location", operator);
        flw.markPromptHeard("prompt1"); flw.markPromptHeard("prompt1");
        flw.markPromptHeard("prompt2");

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(flw);
        when(allOperators.findByName(operator)).thenReturn(new Operator(operator,flw.getCurrentJobAidUsage() - 1));

        JobAidCallerDataResponse callerData = frontLineWorkerService.createJobAidCallerData(msisdn, "airtel");

        assertEquals(callerData.getPromptsHeard().size(), 2);
        assertEquals((int)callerData.getPromptsHeard().get("prompt1"), 2);
        assertEquals((int)callerData.getPromptsHeard().get("prompt2"), 1);
    }

    @Test
    public void shouldResetScoresAtCertificationCourseStartInteractionWhileSavingScores(){
        String callerId = "callerId";
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, "operator");
        frontLineWorker.reportCard().addScore(new ReportCard.Score("1","1",true));
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(frontLineWorker);

        CertificateCourseStateFlwRequest request = new CertificateCourseStateFlwRequest(1,1,false, InteractionKeys.StartCertificationCourseInteraction,"callId", callerId);

        frontLineWorkerService.saveScore(request);

        assertEquals(0, frontLineWorker.reportCard().scores().size());
    }

    @Test
    public void shouldUpdatePromptsForFLW() {
        String callerId = "callerId";
        List<String> promptIds = Arrays.asList("prompt1", "prompt2");

        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, "operator");
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(frontLineWorker);

        try {
            frontLineWorkerService.updatePromptsForFLW(callerId, promptIds);
        } catch (Exception e) {}

        // TODO: trap call on FLW with prompt arguments
//        verify(frontLineWorker).markPromptHeard(promptIds.get(0));
//        verify(frontLineWorker).markPromptHeard(promptIds.get(1));

        verify(allFrontLineWorkers).update(frontLineWorker);
    }
    
    @Test(expected = WorkerDoesNotExistException.class)
    public void shouldThrowExceptionWhileUpdatingPromptsWhenFLWNotPresent() throws WorkerDoesNotExistException {
        String callerId = "callerId";
        List<String> promptIds = Arrays.asList("prompt1", "prompt2");

        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(null);

        frontLineWorkerService.updatePromptsForFLW(callerId, promptIds);
    }
}
