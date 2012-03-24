package org.motechproject.ananya.newfunctional;

import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.framework.CouchDb;
import org.motechproject.ananya.framework.ReportDb;
import org.motechproject.ananya.framework.domain.JobAidRequest;
import org.motechproject.ananya.framework.domain.JobAidResponse;
import org.motechproject.ananya.framework.domain.JobAidWebService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class JobAidTest extends SpringIntegrationTest {

    @Autowired
    private JobAidWebService jobAidService;

    @Autowired
    private CouchDb couchDb;

    @Autowired
    private ReportDb reportDb;

    @Test
    public void onFetchingCallerData_shouldPartiallyRegisterFLW_andPersistInCouchAndPostgres() throws IOException {
        String callerId = "123456";
        String operator = "airtel";
        int expectedMaxUsage = 39;
        int expectedCurrentUsage = 0;

        JobAidRequest request = new JobAidRequest(callerId, operator);
        JobAidResponse response = jobAidService.whenRequestedForCallerData(request);

        response.confirmPartiallyRegistered()
                .confirmMaxUsage(expectedMaxUsage)
                .confirmCurrentUsage(expectedCurrentUsage);

        couchDb.confirmPartiallyRegistered(callerId, operator)
                .confirmUsage(callerId, expectedCurrentUsage, expectedMaxUsage);

        reportDb.confirmFLWDimensionForPartiallyRegistered(callerId, operator)
                .confirmRegistrationMeasureForPartiallyRegistered(callerId);
    }


}
