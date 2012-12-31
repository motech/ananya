package org.motechproject.ananya.contract;

public class FailedRecordCSVRequestBuilder {
    private String msisdn;
    private String applicationName;
    private String calledNumber;
    private String callStartTimestamp;
    private String dataToPost;
    private String fieldsToPost;
    private String lastUpdatedTimestamp;
    private String postLastRetryTimestamp;
    private String dataPostResponse;

    public FailedRecordCSVRequestBuilder withMsisdn(String msisdn) {
        this.msisdn = msisdn;
        return this;
    }

    public FailedRecordCSVRequestBuilder withApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public FailedRecordCSVRequestBuilder withCalledNumber(String calledNumber) {
        this.calledNumber = calledNumber;
        return this;
    }

    public FailedRecordCSVRequestBuilder withCallStartTimestamp(String callStartTimestamp) {
        this.callStartTimestamp = callStartTimestamp;
        return this;
    }

    public FailedRecordCSVRequestBuilder withDataToPost(String dataToPost) {
        this.dataToPost = dataToPost;
        return this;
    }

    public FailedRecordCSVRequestBuilder withFieldsToPost(String fieldsToPost) {
        this.fieldsToPost = fieldsToPost;
        return this;
    }

    public FailedRecordCSVRequestBuilder withLastUpdatedTimestamp(String lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
        return this;
    }

    public FailedRecordCSVRequestBuilder withPostLastRetryTimestamp(String postLastRetryTimestamp) {
        this.postLastRetryTimestamp = postLastRetryTimestamp;
        return this;
    }

    public FailedRecordCSVRequestBuilder withDataPostResponse(String dataPostResponse) {
        this.dataPostResponse = dataPostResponse;
        return this;
    }

    public FailedRecordCSVRequestBuilder withCertificateCourseDefaults() {
        msisdn = "9886000002";
        applicationName = "certificatecourse";
        calledNumber = "5771102";
        callStartTimestamp = "2012-09-04 18:33:45.0";
        dataToPost = "[{\"token\":1,\"data\":{\"time\":1346784033040,\"callEvent\":\"CALL_START\"},\"type\":\"callDuration\"}" +
                ",{\"token\":2,\"data\":{\"time\":1346784033213,\"callEvent\":\"CERTIFICATECOURSE_START\"},\"type\":\"callDuration\"}" +
                ",{\"token\":3,\"data\":{\"lessonOrQuestionIndex\":3,\"time\":1346784033215,\"interactionKey\":\"lesson\",\"courseItemState\":\"start\",\"chapterIndex\":0,\"certificateCourseId\":\"\",\"contentType\":\"lesson\",\"contentName\":\"Chapter 1 Lesson 4\",\"contentId\":\"5fc654d8ec2bac6c906be72af670b62f\"},\"type\":\"ccState\"}" +
                ",{\"token\":4,\"data\":{\"duration\":6122,\"time\":1346784039406,\"contentId\":\"7a823ae22badc42018c6542c597cced8\"},\"type\":\"audioTracker\"}" +
                ",{\"token\":5,\"data\":{\"lessonOrQuestionIndex\":3,\"time\":1346784039406,\"interactionKey\":\"lessonEndMenu\",\"courseItemState\":\"end\",\"chapterIndex\":0,\"certificateCourseId\":\"\",\"contentType\":\"lesson\",\"contentName\":\"Chapter 1 Lesson 4\",\"contentId\":\"5fc654d8ec2bac6c906be72af670b62f\"},\"type\":\"ccState\"}" +
                ",{\"token\":6,\"data\":{\"duration\":7435,\"time\":1346784046908,\"contentId\":\"7a823ae22badc42018c6542c597ccf1c\"},\"type\":\"audioTracker\"}" +
                ",{\"token\":7,\"data\":{\"lessonOrQuestionIndex\":3,\"time\":1346784046908,\"interactionKey\":\"startQuiz\",\"courseItemState\":\"start\",\"chapterIndex\":0,\"certificateCourseId\":\"\",\"contentType\":\"quiz\",\"contentName\":\"Chapter 1\",\"contentId\":\"5fc654d8ec2bac6c906be72af6704519\"},\"type\":\"ccState\"}" +
                ",{\"token\":8,\"data\":{\"duration\":4551,\"time\":1346784051568,\"contentId\":\"7a823ae22badc42018c6542c597c4d3b\"},\"type\":\"audioTracker\"}" +
                ",{\"token\":9,\"data\":{\"lessonOrQuestionIndex\":4,\"time\":1346784051568,\"interactionKey\":\"poseQuestion\",\"chapterIndex\":0,\"certificateCourseId\":\"\"},\"type\":\"ccState\"}" +
                ",{\"token\":10,\"data\":{\"duration\":3087,\"time\":1346784054727,\"contentId\":\"7a823ae22badc42018c6542c597cdcdd\"},\"type\":\"audioTracker\"}" +
                ",{\"token\":11,\"data\":{\"result\":true,\"lessonOrQuestionIndex\":4,\"time\":1346784054727,\"interactionKey\":\"playAnswerExplanation\",\"courseItemState\":\"start\",\"chapterIndex\":0,\"certificateCourseId\":\"\",\"questionResponse\":1},\"type\":\"ccState\"}" +
                ",{\"token\":12,\"data\":{\"duration\":3083,\"time\":1346784057867,\"contentId\":\"7a823ae22badc42018c6542c597cdea4\"},\"type\":\"audioTracker\"}" +
                ",{\"token\":19,\"data\":{\"time\":1346784066005,\"callEvent\":\"DISCONNECT\"},\"type\":\"callDuration\"}]";

        fieldsToPost = "callId:9886000002-1346784033040;operator:airtel";
        lastUpdatedTimestamp = "2012-09-04 18:49:18.0";
        postLastRetryTimestamp = "012-09-04 18:49:49.0";
        dataPostResponse = "Previous data post failed";
        return this;
    }

    public FailedRecordCSVRequestBuilder withJobAidDefaults() {
        msisdn = "9886000002";
        applicationName = "jobaid";
        calledNumber = "5771102";
        callStartTimestamp = "2012-09-04 18:33:45.0";
        dataToPost = "[{\"\"token\"\":1,\"\"data\"\":{\"\"time\"\":1346784033040,\"\"callEvent\"\":\"\"CALL_START\"\"},\"\"type\"\":\"\"callDuration\"\"},{\"\"token\"\":2,\"\"data\"\":{\"\"time\"\":1346784033213,\"\"callEvent\"\":\"\"CERTIFICATECOURSE_START\"\"},\"\"type\"\":\"\"callDuration\"\"},{\"\"token\"\":3,\"\"data\"\":{\"\"lessonOrQuestionIndex\"\":3,\"\"time\"\":1346784033215,\"\"interactionKey\"\":\"\"lesson\"\",\"\"courseItemState\"\":\"\"start\"\",\"\"chapterIndex\"\":0,\"\"certificateCourseId\"\":\"\"\"\",\"\"contentType\"\":\"\"lesson\"\",\"\"contentName\"\":\"\"Chapter 1 Lesson 4\"\",\"\"contentId\"\":\"\"5fc654d8ec2bac6c906be72af670b62f\"\"},\"\"type\"\":\"\"ccState\"\"},{\"\"token\"\":4,\"\"data\"\":{\"\"duration\"\":6122,\"\"time\"\":1346784039406,\"\"contentId\"\":\"\"7a823ae22badc42018c6542c597cced8\"\"},\"\"type\"\":\"\"audioTracker\"\"},{\"\"token\"\":5,\"\"data\"\":{\"\"lessonOrQuestionIndex\"\":3,\"\"time\"\":1346784039406,\"\"interactionKey\"\":\"\"lessonEndMenu\"\",\"\"courseItemState\"\":\"\"end\"\",\"\"chapterIndex\"\":0,\"\"certificateCourseId\"\":\"\"\"\",\"\"contentType\"\":\"\"lesson\"\",\"\"contentName\"\":\"\"Chapter 1 Lesson 4\"\",\"\"contentId\"\":\"\"5fc654d8ec2bac6c906be72af670b62f\"\"},\"\"type\"\":\"\"ccState\"\"},{\"\"token\"\":6,\"\"data\"\":{\"\"duration\"\":7435,\"\"time\"\":1346784046908,\"\"contentId\"\":\"\"7a823ae22badc42018c6542c597ccf1c\"\"},\"\"type\"\":\"\"audioTracker\"\"},{\"\"token\"\":7,\"\"data\"\":{\"\"lessonOrQuestionIndex\"\":3,\"\"time\"\":1346784046908,\"\"interactionKey\"\":\"\"startQuiz\"\",\"\"courseItemState\"\":\"\"start\"\",\"\"chapterIndex\"\":0,\"\"certificateCourseId\"\":\"\"\"\",\"\"contentType\"\":\"\"quiz\"\",\"\"contentName\"\":\"\"Chapter 1\"\",\"\"contentId\"\":\"\"5fc654d8ec2bac6c906be72af6704519\"\"},\"\"type\"\":\"\"ccState\"\"},{\"\"token\"\":8,\"\"data\"\":{\"\"duration\"\":4551,\"\"time\"\":1346784051568,\"\"contentId\"\":\"\"7a823ae22badc42018c6542c597c4d3b\"\"},\"\"type\"\":\"\"audioTracker\"\"},{\"\"token\"\":9,\"\"data\"\":{\"\"lessonOrQuestionIndex\"\":4,\"\"time\"\":1346784051568,\"\"interactionKey\"\":\"\"poseQuestion\"\",\"\"chapterIndex\"\":0,\"\"certificateCourseId\"\":\"\"\"\"},\"\"type\"\":\"\"ccState\"\"},{\"\"token\"\":10,\"\"data\"\":{\"\"duration\"\":3087,\"\"time\"\":1346784054727,\"\"contentId\"\":\"\"7a823ae22badc42018c6542c597cdcdd\"\"},\"\"type\"\":\"\"audioTracker\"\"},{\"\"token\"\":11,\"\"data\"\":{\"\"result\"\":true,\"\"lessonOrQuestionIndex\"\":4,\"\"time\"\":1346784054727,\"\"interactionKey\"\":\"\"playAnswerExplanation\"\",\"\"courseItemState\"\":\"\"start\"\",\"\"chapterIndex\"\":0,\"\"certificateCourseId\"\":\"\"\"\",\"\"questionResponse\"\":1},\"\"type\"\":\"\"ccState\"\"},{\"\"token\"\":12,\"\"data\"\":{\"\"duration\"\":3083,\"\"time\"\":1346784057867,\"\"contentId\"\":\"\"7a823ae22badc42018c6542c597cdea4\"\"},\"\"type\"\":\"\"audioTracker\"\"},{\"\"token\"\":13,\"\"data\"\":{\"\"result\"\":true,\"\"lessonOrQuestionIndex\"\":5,\"\"time\"\":1346784057868,\"\"interactionKey\"\":\"\"poseQuestion\"\",\"\"chapterIndex\"\":0,\"\"certificateCourseId\"\":\"\"\"\",\"\"questionResponse\"\":1},\"\"type\"\":\"\"ccState\"\"},{\"\"token\"\":14,\"\"data\"\":{\"\"duration\"\":3411,\"\"time\"\":1346784061346,\"\"contentId\"\":\"\"7a823ae22badc42018c6542c597ce839\"\"},\"\"type\"\":\"\"audioTracker\"\"},{\"\"token\"\":15,\"\"data\"\":{\"\"result\"\":true,\"\"lessonOrQuestionIndex\"\":5,\"\"time\"\":1346784061347,\"\"interactionKey\"\":\"\"playAnswerExplanation\"\",\"\"courseItemState\"\":\"\"start\"\",\"\"chapterIndex\"\":0,\"\"certificateCourseId\"\":\"\"\"\",\"\"questionResponse\"\":2},\"\"type\"\":\"\"ccState\"\"},{\"\"token\"\":16,\"\"data\"\":{\"\"duration\"\":3315,\"\"time\"\":1346784064747,\"\"contentId\"\":\"\"7a823ae22badc42018c6542c597cf360\"\"},\"\"type\"\":\"\"audioTracker\"\"},{\"\"token\"\":17,\"\"data\"\":{\"\"result\"\":true,\"\"lessonOrQuestionIndex\"\":6,\"\"time\"\":1346784064747,\"\"interactionKey\"\":\"\"poseQuestion\"\",\"\"chapterIndex\"\":0,\"\"certificateCourseId\"\":\"\"\"\",\"\"questionResponse\"\":2},\"\"type\"\":\"\"ccState\"\"},{\"\"token\"\":18,\"\"data\"\":{\"\"duration\"\":1125,\"\"time\"\":1346784066005,\"\"contentId\"\":\"\"7a823ae22badc42018c6542c597d0ca2\"\"},\"\"type\"\":\"\"audioTracker\"\"},{\"\"token\"\":19,\"\"data\"\":{\"\"time\"\":1346784066005,\"\"callEvent\"\":\"\"DISCONNECT\"\"},\"\"type\"\":\"\"callDuration\"\"}]";
        fieldsToPost = "callId:9886000002-1346784033040;operator:airtel;callDuration:12;promptList:[]";
        lastUpdatedTimestamp = "2012-09-04 18:49:18.0";
        postLastRetryTimestamp = "012-09-04 18:49:49.0";
        dataPostResponse = "Previous data post failed";
        return this;
    }

    public FailedRecordCSVRequest build() {
        return new FailedRecordCSVRequest(msisdn, applicationName, calledNumber, callStartTimestamp, dataToPost, fieldsToPost, lastUpdatedTimestamp,
                postLastRetryTimestamp, dataPostResponse);
    }
}