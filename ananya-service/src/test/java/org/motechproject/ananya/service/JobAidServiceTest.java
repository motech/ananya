package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.request.JobAidPromptRequest;
import org.motechproject.ananya.response.JobAidCallerDataResponse;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class JobAidServiceTest {

    private JobAidService jobAidService;

    @Mock
    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private OperatorService operatorService;
    @Mock
    private ReportPublishService reportPublishService;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;

    @Before
    public void setUp() {
        initMocks(this);
        jobAidService = new JobAidService(frontLineWorkerService, operatorService, reportPublishService);
    }

    @Test
    public void shouldUpdateFrontLineWorkersWithPrompts() {
        String callId = "callId";
        String callerId = "callerId";
        String promptIds = "[promptIds]";
        JobAidPromptRequest jobAidPromptRequest = new JobAidPromptRequest(callId, callerId, promptIds);

        jobAidService.updateJobAidPrompts(jobAidPromptRequest);

        verify(frontLineWorkerService).updatePromptsForFLW(jobAidPromptRequest.getCallerId(),jobAidPromptRequest.getPromptList());
    }

    @Test
    public void shouldCreateNewFlwWithUsageAndAlsoPublishToReportModule(){
        String operator = "airtel";
        String callerId = "callerId";
        String promptKey = "prompt";
        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.markPromptHeard(promptKey);
        frontLineWorker.setCurrentJobAidUsage(new Integer(9));
        when(frontLineWorkerService.getFLWForJobAidCallerData(callerId, operator)).thenReturn(frontLineWorker);
        when(operatorService.findMaximumUsageFor(operator)).thenReturn(new Integer(10));

        JobAidCallerDataResponse callerData = jobAidService.createCallerData(callerId, operator);

        verify(frontLineWorkerService).getFLWForJobAidCallerData(callerId, operator);
        verify(reportPublishService).publishNewRegistration(callerId);

        assertEquals(callerData.getCurrentJobAidUsage(),new Integer(9));
        assertEquals(callerData.getMaxAllowedUsageForOperator(),new Integer(10));
        assertEquals(callerData.getPromptsHeard().get(promptKey), new Integer(1));
        assertEquals(callerData.isCallerRegistered(),false);
    }

    @Test
    public void shouldUpdateTheFrontLineWorkerWithTheNewUsage(){
        int currentUsage = 10;
        String callerId = "callerId";

        jobAidService.updateCurrentUsageAndSetLastAccessTimeForUser(callerId, currentUsage);

        verify(frontLineWorkerService).updateCurrentUsageForUser(callerId, currentUsage);
        verify(frontLineWorkerService).updateLastJobAidAccessTime(callerId);
    }
}
