package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.request.JobAidPromptRequest;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import org.motechproject.ananya.service.publish.DataPublishService;

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
    private DataPublishService dataPublishService;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;

    @Before
    public void setUp() {
        initMocks(this);
        jobAidService = new JobAidService(frontLineWorkerService, operatorService, dataPublishService);
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
        when(frontLineWorkerService.isNewFLW(callerId)).thenReturn(true);
        when(operatorService.findMaximumUsageFor(operator)).thenReturn(new Integer(10));

        JobAidCallerDataResponse callerData = jobAidService.createCallerData(callerId, operator);

        verify(frontLineWorkerService).getFLWForJobAidCallerData(callerId, operator);
        verify(dataPublishService).publishNewRegistration(callerId);

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
