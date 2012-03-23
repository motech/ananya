package org.motechproject.ananya.newfunctional;

import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.framework.domain.JobAidRequest;
import org.motechproject.ananya.framework.domain.JobAidResponse;
import org.motechproject.ananya.framework.domain.JobAidWebService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class JobAidTest extends SpringIntegrationTest {

    @Autowired
    private JobAidWebService service;

    
    @Test
    public void shouldRegisterFLWIfNotRegistered() throws IOException {

        String callerId = "123456";
        String operator = "airtel";
        JobAidRequest request = new JobAidRequest(callerId, operator);

        JobAidResponse response = service.whenRequestedForCallerData(request);
        response.verifyUserIsPartiallyRegistered();
//                .confirmMaxUsage(48l)
//                .confirmCurrentUsage(0l);


//      confirmMaxUsage(48).confirmCurrentUsage(0).verifyMaxUsagePromptsHeard(0);
//
//
//      verifyCouchWithFLWData(request);
//      verifyPostgres(request);
    }



}
