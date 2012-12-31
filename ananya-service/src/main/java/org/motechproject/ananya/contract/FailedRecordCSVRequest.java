package org.motechproject.ananya.contract;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.utils.CSVRecord;
import org.motechproject.ananya.validators.FailedRecordValidationException;
import org.motechproject.importer.annotation.ColumnName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FailedRecordCSVRequest implements Serializable {

    @ColumnName(name = "MSISDN")
    private String msisdn;

    @ColumnName(name = "APPLICATION_NAME")
    private String applicationName;

    @ColumnName(name = "CALLED_NUMBER")
    private String calledNumber;

    @ColumnName(name = "CALL_START_TS")
    private String callStartTimestamp;

    @ColumnName(name = "DATA_TO_POST")
    private String dataToPost;

    @ColumnName(name = "FIELDS_TO_POST")
    private String fieldsToPost;

    @ColumnName(name = "LAST_UPDATED_TS")
    private String lastUpdatedTimestamp;

    @ColumnName(name = "POST_LAST_RETRY_TS")
    private String postLastRetryTimestamp;

    @ColumnName(name = "DATA_POST_RESPONSE")
    private String dataPostResponse;

    private Map<String, String> fieldsToPostMap;

    public FailedRecordCSVRequest() {
    }

    public FailedRecordCSVRequest(String msisdn, String applicationName, String calledNumber, String callStartTimestamp,
                                  String dataToPost, String fieldsToPost, String lastUpdatedTimestamp,
                                  String postLastRetryTimestamp, String dataPostResponse) {
        this.msisdn = msisdn;
        this.applicationName = applicationName;
        this.calledNumber = calledNumber;
        this.callStartTimestamp = callStartTimestamp;
        this.dataToPost = dataToPost;
        this.fieldsToPost = fieldsToPost;
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
        this.postLastRetryTimestamp = postLastRetryTimestamp;
        this.dataPostResponse = dataPostResponse;
    }

    public Map<String, String> getFieldsToPostMap() {
        if(fieldsToPostMap != null) {
            return fieldsToPostMap;
        }

        fieldsToPostMap = new HashMap<>();

        String[] keyValuePairs = fieldsToPost.split(";");
        for(String keyValuePair : keyValuePairs) {
            keyValuePair = keyValuePair.trim();
            String[] keyAndValue = keyValuePair.split(":");
            validateFieldToPost(keyAndValue);
            fieldsToPostMap.put(StringUtils.trim(keyAndValue[0]), StringUtils.trim(keyAndValue[1]));
        }

        return fieldsToPostMap;
    }

    private void validateFieldToPost(String[] keyAndValue) {
        if(keyAndValue.length != 2) {
            throw new FailedRecordValidationException(String.format("Invalid fields to post: %s", fieldsToPost));
        }
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getCalledNumber() {
        return calledNumber;
    }

    public String getCallStartTimestamp() {
        return callStartTimestamp;
    }

    public String getDataToPost() {
        return dataToPost;
    }

    public String getFieldsToPost() {
        return fieldsToPost;
    }

    public String getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    public String getPostLastRetryTimestamp() {
        return postLastRetryTimestamp;
    }

    public String getDataPostResponse() {
        return dataPostResponse;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setCalledNumber(String calledNumber) {
        this.calledNumber = calledNumber;
    }

    public void setCallStartTimestamp(String callStartTimestamp) {
        this.callStartTimestamp = callStartTimestamp;
    }

    public void setDataToPost(String dataToPost) {
        this.dataToPost = dataToPost;
    }

    public void setFieldsToPost(String fieldsToPost) {
        this.fieldsToPost = fieldsToPost;
    }

    public void setLastUpdatedTimestamp(String lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public void setPostLastRetryTimestamp(String postLastRetryTimestamp) {
        this.postLastRetryTimestamp = postLastRetryTimestamp;
    }

    public void setDataPostResponse(String dataPostResponse) {
        this.dataPostResponse = dataPostResponse;
    }

    public CSVRecord toCSVRecord() {
        return new CSVRecord(true)
                .append(msisdn).append(applicationName).append(calledNumber)
                .append(callStartTimestamp).append(dataToPost).append(fieldsToPost)
                .append(lastUpdatedTimestamp).append(postLastRetryTimestamp).append(dataPostResponse);
    }

    public static String csvHeader() {
        return new CSVRecord(true)
                .append("MSISDN").append("APPLICATION_NAME").append("CALLED_NUMBER")
                .append("CALL_START_TS").append("DATA_TO_POST").append("FIELDS_TO_POST")
                .append("LAST_UPDATED_TS").append("POST_LAST_RETRY_TS").append("DATA_POST_RESPONSE")
                .toString();
    }
}