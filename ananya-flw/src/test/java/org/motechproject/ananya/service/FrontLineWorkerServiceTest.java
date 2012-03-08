package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.exceptions.WorkerDoesNotExistException;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.request.CertificateCourseStateFlwRequest;
import org.motechproject.ananya.response.CallerDataResponse;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
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
   
    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerService = new FrontLineWorkerService(allFrontLineWorkers,allLocations, sendSMSService, publisherService);
    }

    @Test
    public void shouldExtractCallerIdAndSaveRecordedFilesAndSaveNewWorker() {
        String panchayatCode = "S01D001V0001";
        Location patna = new Location(panchayatCode, "Patna", "Dulhin Bazar", "Singhara Kopa");
        patna.setId("id");
        when(allLocations.findByExternalId(panchayatCode)).thenReturn(patna);

        frontLineWorkerService.createNew("msisdn", Designation.ASHA, panchayatCode, "");

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).add(captor.capture());
        FrontLineWorker captured = captor.getValue();

        assertEquals("msisdn",captured.getMsisdn());
        assertEquals("id",captured.getLocationId());
        assertTrue(captured.status().equals(RegistrationStatus.PENDING_REGISTRATION));
    }

    private FrontLineWorker FrontLineWorker() {
        return new FrontLineWorker("123", Designation.ANM, "123","");
    }

    @Test
    public void shouldTellThatUserIsRegisteredBasedOnStatusOnTheCallerData() {
        String registeredMsisdn = "123";
        FrontLineWorker registeredFrontLineWorker = FrontLineWorker();
        registeredFrontLineWorker.status(RegistrationStatus.REGISTERED);
        when(allFrontLineWorkers.findByMsisdn(registeredMsisdn)).thenReturn(registeredFrontLineWorker);

        CallerDataResponse callerData = frontLineWorkerService.getCallerData(registeredMsisdn);

        assertThat(callerData.isCallerRegistered(), is(true));
    }

    @Test
    public void shouldTellThatUserIsRegisteredIfStatusIsPendingRegistration() {
        String registeredMsisdn = "123";
        FrontLineWorker registeredFrontLineWorker = FrontLineWorker();
        registeredFrontLineWorker.status(RegistrationStatus.PENDING_REGISTRATION);
        when(allFrontLineWorkers.findByMsisdn(registeredMsisdn)).thenReturn(registeredFrontLineWorker);

        CallerDataResponse callerData = frontLineWorkerService.getCallerData(registeredMsisdn);

        assertThat(callerData.isCallerRegistered(), is(true));
    }

    @Test
    public void shouldTellThatUserIsUnRegisteredBasedOnStatus() {
        String registeredMsisdn = "123";
        FrontLineWorker registeredFrontLineWorker = FrontLineWorker();
        registeredFrontLineWorker.status(RegistrationStatus.UNREGISTERED);
        when(allFrontLineWorkers.findByMsisdn(registeredMsisdn)).thenReturn(registeredFrontLineWorker);

        CallerDataResponse callerData = frontLineWorkerService.getCallerData(registeredMsisdn);

        assertThat(callerData.isCallerRegistered(), is(false));
    }

    @Test
    public void shouldSaveNameWhenNameIsWellFormed() throws Exception {
        String msisdn = "555", name = "abcd";
        FrontLineWorker mockWorker = new FrontLineWorker(msisdn, Designation.ANGANWADI, "S01D001","");
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(mockWorker);
        
        frontLineWorkerService.saveName(msisdn, name);
        
        verify(allFrontLineWorkers).update(mockWorker);
    }

    @Test(expected=WorkerDoesNotExistException.class)
    public void shouldNotSaveNameAndThrowExceptionWhenNameIsNotWellFormed() throws Exception {
        String msisdn = "123", name = "abcd";
        
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(null);
        
        frontLineWorkerService.saveName(msisdn, name);
    }

    @Test
    public void shouldResetScoresWhenStartingCourse() throws Exception {
        String msisdn = "123" ;
        FrontLineWorker mockWorker = new FrontLineWorker(msisdn, Designation.ANGANWADI, "S01D001", "");
        mockWorker.status(RegistrationStatus.REGISTERED);
        mockWorker.reportCard().addScore(new ReportCard.Score("0","5",true));

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(mockWorker);
        frontLineWorkerService.resetScoresWhenStartingCertificateCourse(msisdn);

        assertTrue(mockWorker.reportCard().scores().size() == 0);
    }

    @Test
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
        verify(sendSMSService).buildAndSendSMS(callerId,expectedFrontLineWorker.getLocationId(),courseAttemptNumber);
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

        CallerDataResponse callerData = frontLineWorkerService.getCallerData(msisdn);

        assertEquals("{}", callerData.getBookmark());
    }

    @Test
    public void shouldReturnBookmarkOfFrontLineWorkerInCallerData() {
        String msisdn = "999";
        FrontLineWorker frontLineWorker = FrontLineWorker();
        BookMark bookMark = new BookMark("leson", 0, 2);
        frontLineWorker.addBookMark(bookMark);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);

        CallerDataResponse callerData = frontLineWorkerService.getCallerData(msisdn);

        assertThat(callerData.getBookmark(), is(bookMark.asJson()));
    }

    @Test
    public void shouldReturnTheCorrectScoresByChapterMapWithTheCallerData(){
        String callerId = "callerId";
        CertificateCourseStateFlwRequest request = new CertificateCourseStateFlwRequest(10, 0, null, InteractionKeys.PlayCourseResultInteraction, "callId", callerId);
        FrontLineWorker expectedFrontLineWorker = FrontLineWorker();
        setUpTestScoreSet(expectedFrontLineWorker,true);
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(expectedFrontLineWorker);

        CallerDataResponse callerData = frontLineWorkerService.getCallerData(callerId);

        assertEquals(9,callerData.getScoresByChapter().size());
        assertEquals(4,(int)callerData.getScoresByChapter().get("0"));
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
}
