package org.motechproject.ananya.mapper;

import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.contract.JobAidServiceRequest;

import java.util.Map;

public class JobAidServiceRequestMapper {
    public static JobAidServiceRequest map(FailedRecordCSVRequest failedRecordCSVRequest) {
        Map<String, String> fieldsToPostMap = failedRecordCSVRequest.getFieldsToPostMap();

        return new JobAidServiceRequest(
                fieldsToPostMap.get("callId"), failedRecordCSVRequest.getMsisdn(), failedRecordCSVRequest.getCalledNumber())
                .withCircle(fieldsToPostMap.get("circle"))
                .withOperator(fieldsToPostMap.get("operator"))
                .withLanguage(fieldsToPostMap.get("language"))
                .withJson(failedRecordCSVRequest.getDataToPost())
                .withCallDuration(Integer.parseInt(fieldsToPostMap.get("callDuration")))
                .withPromptList(fieldsToPostMap.get("promptList"));
    }
}