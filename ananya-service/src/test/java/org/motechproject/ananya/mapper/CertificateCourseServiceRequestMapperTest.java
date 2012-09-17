package org.motechproject.ananya.mapper;

import org.junit.Test;
import org.motechproject.ananya.contract.CertificateCourseServiceRequest;
import org.motechproject.ananya.contract.FailedRecordCSVRequest;

import static junit.framework.Assert.assertEquals;

public class CertificateCourseServiceRequestMapperTest {

    @Test
    public void shouldMapFromFailedRecordCSVRequest() throws Exception {
        String msisdn = "1234567890";
        String calledNumber = "calledNumber";
        String dataToPost = "";
        String callId = "9886000002-1346784033040";
        String operator = "airtel";
        String circle = "bihar";
        String fieldsToPost = "callId:" + callId + ";operator:" + operator + ";circle:" + circle;
        FailedRecordCSVRequest failedrecordCsvRequest = new FailedRecordCSVRequest(msisdn, "appName",
                calledNumber, "callStartTimestamp", dataToPost,
                fieldsToPost,
                "lastUpdatedTimeStamp", "retryTimeStamp", "dataPostResponse");

        CertificateCourseServiceRequest certificateCourseServiceRequest = CertificateCourseServiceRequestMapper.map(failedrecordCsvRequest);

        assertEquals(msisdn, certificateCourseServiceRequest.getCallerId());
        assertEquals(calledNumber, certificateCourseServiceRequest.getCalledNumber());
        assertEquals(callId, certificateCourseServiceRequest.getCallId());
        assertEquals(operator, certificateCourseServiceRequest.getOperator());
        assertEquals(circle, certificateCourseServiceRequest.getCircle());
    }
}
