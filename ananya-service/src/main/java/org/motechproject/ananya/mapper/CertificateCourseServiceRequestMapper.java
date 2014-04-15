package org.motechproject.ananya.mapper;

import org.motechproject.ananya.contract.CertificateCourseServiceRequest;
import org.motechproject.ananya.contract.FailedRecordCSVRequest;

import java.util.Map;

public class CertificateCourseServiceRequestMapper {

    public static CertificateCourseServiceRequest map(FailedRecordCSVRequest failedRecordCSVRequest) {
        Map<String, String> fieldsToPostMap = failedRecordCSVRequest.getFieldsToPostMap();

        return new CertificateCourseServiceRequest(
                fieldsToPostMap.get("callId"), failedRecordCSVRequest.getMsisdn(), failedRecordCSVRequest.getCalledNumber())
                .withCircle(fieldsToPostMap.get("circle"))
                .withOperator(fieldsToPostMap.get("operator"))
                .withLanguage(fieldsToPostMap.get("language"))
                .withJson(failedRecordCSVRequest.getDataToPost());
    }
}
