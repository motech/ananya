package org.motechproject.ananya.mapper;

import org.junit.Test;
import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.contract.FailedRecordCSVRequestBuilder;
import org.motechproject.ananya.contract.JobAidServiceRequest;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class JobAidServiceRequestMapperTest {

    @Test
    public void shouldMapFromFailedRecordRequest() {
        String msisdn = "1234567890";
        String calledNumber = "calledNumber";
        String dataToPost = "";
        String callId = "9886000002-1346784033040";
        String operator = "airtel";
        String circle = "bihar";
        Integer callDuration = 4;
        List<String> promptList = new ArrayList<String>(){{
            add("prompt1");
            add("prompt2");
        }
        };
        String fieldsToPost = "callId:" + callId + ";operator:" + operator + ";circle:" + circle + ";callDuration:" + callDuration + ";promptList:" + promptList;
        FailedRecordCSVRequest failedrecordCsvRequest = new FailedRecordCSVRequestBuilder().withMsisdn(msisdn).withApplicationName("appName").withCalledNumber(calledNumber).withCallStartTimestamp("callStartTimestamp").withDataToPost(dataToPost).withFieldsToPost(fieldsToPost).withLastUpdatedTimestamp("lastUpdatedTimeStamp").withPostLastRetryTimestamp("retryTimeStamp").withDataPostResponse("dataPostResponse").build();

        JobAidServiceRequest jobAidServiceRequest = JobAidServiceRequestMapper.map(failedrecordCsvRequest);

        assertEquals(msisdn, jobAidServiceRequest.getCallerId());
        assertEquals(calledNumber, jobAidServiceRequest.getCalledNumber());
        assertEquals(callId, jobAidServiceRequest.getCallId());
        assertEquals(operator, jobAidServiceRequest.getOperator());
        assertEquals(circle, jobAidServiceRequest.getCircle());
        assertEquals(promptList, jobAidServiceRequest.getPrompts());
        assertEquals(callDuration, jobAidServiceRequest.getCallDuration());
    }
}