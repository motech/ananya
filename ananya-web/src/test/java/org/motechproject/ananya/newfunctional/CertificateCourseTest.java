package org.motechproject.ananya.newfunctional;


import org.junit.After;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.framework.CouchDb;
import org.motechproject.ananya.framework.ReportDb;
import org.motechproject.ananya.framework.domain.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class CertificateCourseTest extends SpringIntegrationTest {

    @Autowired
    private CertificateCourseWebservice certificateCourseWebService;

    @Autowired
    private CouchDb couchDb;

    @Autowired
    private ReportDb reportDb;

    String callerId = "987654";
    String operator = "airtel";

    @After
    public void after() {
        clearFLWData();
    }

    private void clearFLWData() {
        reportDb.clearDimensionAndMeasures(callerId);
        couchDb.clearFLWData(callerId);
    }

    @Test
    public void onFetchingCallerData_shouldPartiallyRegisterFLW_andUpdateOperatorIfNotPresent_andUpdateCouchDB() throws IOException {
        CertificateCourseRequest request = new CertificateCourseRequest(callerId, operator);
        CertificateCourseResponse response = certificateCourseWebService.requestForCallerData(request);

        response.confirmPartiallyRegistered();

        couchDb.confirmPartiallyRegistered(callerId, operator);

        reportDb.confirmFLWDimensionForPartiallyRegistered(callerId, operator)
                .confirmRegistrationMeasureForPartiallyRegistered(callerId);
    }

//    @Test
//    public void shouldCreateTransferDataList_toSaveCertificateCourseState() throws Exception {
//
//    }
}
