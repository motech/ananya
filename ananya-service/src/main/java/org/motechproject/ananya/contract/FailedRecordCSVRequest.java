package org.motechproject.ananya.contract;

import org.apache.commons.lang.StringUtils;
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
        Map<String, String> fieldsToPostMap = new HashMap<>();

        String[] keyValuePairs = fieldsToPost.split(";");
        for(String keyValuePair : keyValuePairs) {
            String[] keyAndValue = keyValuePair.split(":");
            fieldsToPostMap.put(StringUtils.trim(keyAndValue[0]), StringUtils.trim(keyAndValue[1]));
        }

        return fieldsToPostMap;
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
}