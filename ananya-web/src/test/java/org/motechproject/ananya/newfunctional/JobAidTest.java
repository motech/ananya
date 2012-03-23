package org.motechproject.ananya.newfunctional;

import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.framework.domain.JobAidRequest;
import org.motechproject.ananya.framework.domain.JobAidResponse;
import org.motechproject.ananya.framework.domain.JobAidWebService;

import java.io.IOException;

public class JobAidTest extends SpringIntegrationTest {
    
    @Test
    public void shouldRegisterFLWIfNotRegistered() throws IOException {

        String callerId = "123456";
        String operator = "Airtel";
        JobAidRequest request = new JobAidRequest(callerId, operator);

        JobAidResponse response = new JobAidWebService().whenRequestedForCallerData(request);
        response.verifyUserIsRegistered();

//                confirmMaxUsage(48).confirmCurrentUsage(0).verifyMaxUsagePromptsHeard(0);
//
//
//        verifyCouchWithFLWData(request);
//        verifyPostgres(request);
    }



}
