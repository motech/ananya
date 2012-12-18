package org.motechproject.ananya.support.diagnostics.support.validator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.contract.*;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.mapper.CertificateCourseServiceRequestMapper;
import org.motechproject.ananya.mapper.JobAidServiceRequestMapper;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDimensions;
import org.motechproject.importer.CSVDataImporter;
import org.motechproject.importer.annotation.CSVImporter;
import org.motechproject.importer.annotation.Post;
import org.motechproject.importer.annotation.Validate;
import org.motechproject.importer.domain.ValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

@CSVImporter(entity = "FailedRecordCSVRequestForValidation", bean = FailedRecordCSVRequest.class)
@Component
public class ValidateFailedRecordsCSV {
    @Autowired
    private CSVDataImporter csvDataImporter;

    @Autowired
    private AllCourseItemDimensions allCourseItemDimensions;

    @Autowired
    private AllJobAidContentDimensions allJobAidContentDimensions;

    Logger log = LoggerFactory.getLogger(ValidateFailedRecordsCSV.class);

    public static void main(String... args) {
        String pathToCsv = args[0];

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-tool.xml");

        ValidateFailedRecordsCSV validateFailedRecordsCSV = (ValidateFailedRecordsCSV) applicationContext.getBean("validateFailedRecordsCSV");
        validateFailedRecordsCSV.validate(pathToCsv);
    }

    private void validate(String pathToCsv) {
        csvDataImporter.importData("FailedRecordCSVRequestForValidation", pathToCsv);
    }

    @Validate
    public ValidationResponse validate(List<FailedRecordCSVRequest> failedRecordCSVRequests) {
        return new ValidationResponse(true);
    }

    @Post
    public void postData(List<FailedRecordCSVRequest> failedRecordCSVRequests) {
        log.info("******************* THE START **********************");

        int rn = 0;
        HashSet<String> callIdsSet = new HashSet<>();
        for (FailedRecordCSVRequest request : failedRecordCSVRequests) {
            rn++;

            String applicationName = request.getApplicationName();
            try {
                Map<String, String> fieldsToPostMap = request.getFieldsToPostMap();
                if (applicationName.equalsIgnoreCase("CERTIFICATECOURSE")) {
                    CertificateCourseServiceRequest certificateCourseServiceRequest = CertificateCourseServiceRequestMapper.map(request);

                    if (callIdsSet.contains(certificateCourseServiceRequest.getCallId()))
                        throw new RuntimeException("CallId " + certificateCourseServiceRequest.getCallId() + " present more than once");

                    callIdsSet.add(certificateCourseServiceRequest.getCallId());

                    if (containsInvalidCCFieldsToPost(fieldsToPostMap))
                        throw new RuntimeException("Contains invalid CC fieldsToPost");

                    if (missingRequiredCCFieldsToPost(fieldsToPostMap))
                        throw new RuntimeException("Missing required CC fieldsToPost");

                    validateCC(certificateCourseServiceRequest);
                } else if (applicationName.equalsIgnoreCase("JOBAID")) {
                    JobAidServiceRequest jobAidServiceRequest = JobAidServiceRequestMapper.map(request);

                    if (callIdsSet.contains(jobAidServiceRequest.getCallId()))
                        throw new RuntimeException("CallId " + jobAidServiceRequest.getCallId() + " present more than once");

                    callIdsSet.add(jobAidServiceRequest.getCallId());

                    if (containsInvalidJAFieldsToPost(fieldsToPostMap))
                        throw new RuntimeException("Contains invalid JA fieldsToPost");

                    if (missingRequiredJAFieldsToPost(fieldsToPostMap))
                        throw new RuntimeException("Missing required JA fieldsToPost");

                    validateJA(jobAidServiceRequest);
                } else {
                    error("Invalid application name " + applicationName, rn);
                }
            } catch (RuntimeException re) {
                error(re.getMessage() == null || StringUtils.startsWith(re.getMessage(), "Incorrect") ? ExceptionUtils.getStackTrace(re) : re.getMessage(), rn);
                continue;
            }
        }

        log.info("******************* THE END **********************");
    }

    private boolean missingRequiredJAFieldsToPost(Map<String, String> fieldsToPostMap) {
        String[] requiredJAFieldsToPost = {"callId", "operator", "callDuration", "promptList"};

        for (String requiredField : requiredJAFieldsToPost) {
            if (!fieldsToPostMap.containsKey(requiredField))
                return true;
        }

        return false;
    }

    private boolean missingRequiredCCFieldsToPost(Map<String, String> fieldsToPostMap) {
        String[] requiredCCFieldsToPost = {"callId", "operator"};

        for (String requiredField : requiredCCFieldsToPost) {
            if (!fieldsToPostMap.containsKey(requiredField))
                return true;
        }

        return false;
    }

    private boolean containsInvalidCCFieldsToPost(Map<String, String> fieldsToPost) {
        String[] validCCFieldsToPost = {"callId", "circle", "operator"};

        Iterator<String> fields = fieldsToPost.keySet().iterator();
        while (fields.hasNext()) {
            String nextField = fields.next();
            if (!Arrays.asList(validCCFieldsToPost).contains(nextField))
                return true;
        }

        return false;
    }

    private boolean containsInvalidJAFieldsToPost(Map<String, String> fieldsToPost) {
        String[] validCCFieldsToPost = {"callId", "circle", "operator", "callDuration", "promptList"};

        Iterator<String> fields = fieldsToPost.keySet().iterator();
        while (fields.hasNext()) {
            String nextField = fields.next();
            if (!Arrays.asList(validCCFieldsToPost).contains(nextField))
                return true;
        }

        return false;
    }

    private void validateJA(JobAidServiceRequest jobAidServiceRequest) {
        String callId = jobAidServiceRequest.getCallId();
        validateCallId(callId);

        String callerId = jobAidServiceRequest.getCallerId();
        validateCallerId(callerId);

        String calledNumber = jobAidServiceRequest.getCalledNumber();
        validateJACalledNumber(calledNumber);

        List<String> prompts = jobAidServiceRequest.getPrompts();
        log.info("JA prompts " + prompts);

        AudioTrackerRequestList audioTrackerRequestList = jobAidServiceRequest.getAudioTrackerRequestList();
        validateAudioTrackerList(callId, callerId, audioTrackerRequestList, ServiceType.JOB_AID);

        CallDurationList callDurationList = jobAidServiceRequest.getCallDurationList();
        validateEquals(callDurationList.getCallerId(), callerId, "callerId and callDurationList callerId");
        validateEquals(callDurationList.getCallId(), callId, "callId and callDurationList callId");
        validateEquals(callDurationList.getCalledNumber(), calledNumber, "calledNum and callDurationList calledNum");
        for (CallDuration callDuration : callDurationList.all()) {
            if(callDuration.getCallEvent() == null)
                throw new RuntimeException("JA call duration call event is null");

            DateTime time = new DateTime(callDuration.getTime());
            if (time.isBefore(new DateTime().withDayOfMonth(1).withMonthOfYear(6).withYear(2012))
                    || time.isAfter(new DateTime().withDayOfMonth(31).withMonthOfYear(12).withYear(2012)))
                throw new RuntimeException("Invalid JA call duration time");
        }
    }

    private void validateAudioTrackerList(String callId, String callerId, AudioTrackerRequestList audioTrackerRequestList, ServiceType serviceType) {
        validateEquals(audioTrackerRequestList.getCallerId(), callerId, "callerId and audioTrackerList callerId");

        validateEquals(audioTrackerRequestList.getCallId(), callId, "callId and audioTrackerList callId");

        for (AudioTrackerRequest audioTrackerRequest : audioTrackerRequestList.all()) {
            validateEquals(audioTrackerRequest.getCallId(), callId, "callId and audioTrackerRequest callId");

            validateEquals(audioTrackerRequest.getCallerId(), callerId, "callerId and audioTrackerRequest callerId");

            if (serviceType.isCertificateCourse()) {
                CourseItemDimension courseItemDimension = allCourseItemDimensions.getFor(audioTrackerRequest.getContentId());
                if (courseItemDimension == null)
                    throw new RuntimeException("Invalid CC audio tracker content Id");

                if (audioTrackerRequest.getDuration() > courseItemDimension.getDuration())
                    throw new RuntimeException("CC audio tracker duration greater than actual course item duration - " + audioTrackerRequest.getCallId() + " " + audioTrackerRequest.getDuration() + " " + courseItemDimension.getDuration());

                if (audioTrackerRequest.getTimeAsDateTime() != null
                        && (audioTrackerRequest.getTimeAsDateTime().isBefore(new DateTime().withDayOfMonth(1).withMonthOfYear(6).withYear(2012))
                        || audioTrackerRequest.getTimeAsDateTime().isAfter(new DateTime().withDayOfMonth(31).withMonthOfYear(12).withYear(2012))))
                    throw new RuntimeException("Invalid CC audio tracker request time");
            } else {
                JobAidContentDimension jobAidContentDimension = allJobAidContentDimensions.findByContentId(audioTrackerRequest.getContentId());
                if (jobAidContentDimension == null)
                    throw new RuntimeException("Invalid JA audio tracker content Id");

                if (audioTrackerRequest.getDuration() > jobAidContentDimension.getDuration())
                    throw new RuntimeException("JA audio tracker duration greater than actual course item duration");

                if (audioTrackerRequest.getTimeAsDateTime() != null
                        && (audioTrackerRequest.getTimeAsDateTime().isBefore(new DateTime().withDayOfMonth(1).withMonthOfYear(6).withYear(2012))
                        || audioTrackerRequest.getTimeAsDateTime().isAfter(new DateTime().withDayOfMonth(31).withMonthOfYear(12).withYear(2012))))
                    throw new RuntimeException("Invalid JA audio tracker request time");
            }
        }
    }

    private void validateJACalledNumber(String calledNumber) {
        if (StringUtils.isEmpty(calledNumber) || !StringUtils.isNumeric(calledNumber) || StringUtils.equals(calledNumber, "5771102")
                || StringUtils.length(calledNumber) != 7 || !StringUtils.startsWith(calledNumber, "57711"))
            ;//throw new RuntimeException("Invalid JA called number");
    }

    private void error(String error, int recordNum) {
        log.error(String.format("At %s. message: %s", recordNum, error));
    }

    private void validateCC(CertificateCourseServiceRequest certificateCourseServiceRequest) {
        String callId = certificateCourseServiceRequest.getCallId();
        validateCallId(callId);

        String callerId = certificateCourseServiceRequest.getCallerId();
        validateCallerId(callerId);

        String calledNumber = certificateCourseServiceRequest.getCalledNumber();
        validateCCCalledNumber(calledNumber);

        CertificateCourseStateRequestList certificateCourseStateRequestList = certificateCourseServiceRequest.getCertificateCourseStateRequestList();

        validateEquals(certificateCourseStateRequestList.getCallerId(), callerId, "callerId and certificateCourseStateRequestList callerId");

        validateEquals(certificateCourseStateRequestList.getCallId(), callId, "callId and certificateCourseStateRequestList callId");

        for (CertificateCourseStateRequest stateRequest : certificateCourseStateRequestList.all()) {
            validateEquals(stateRequest.getCallId(), callId, "callId and certificateCourseStateRequest callId");

            validateEquals(stateRequest.getCallerId(), callerId, "callerId and certificateCourseStateRequest callerId");

            if (stateRequest.getChapterIndex() != null && (stateRequest.getChapterIndex() < 0 || stateRequest.getChapterIndex() > 9))
                throw new RuntimeException("Invalid CC chapter number");

            if (stateRequest.getLessonOrQuestionIndex() != null && (stateRequest.getLessonOrQuestionIndex() < 0 || stateRequest.getLessonOrQuestionIndex() > 7))
                throw new RuntimeException("Invalid CC lesson or question index");

            if (stateRequest.getTimeAsDateTime() != null
                    && (stateRequest.getTimeAsDateTime().isBefore(new DateTime().withDayOfMonth(1).withMonthOfYear(6).withYear(2012))
                    || stateRequest.getTimeAsDateTime().isAfter(new DateTime().withDayOfMonth(31).withMonthOfYear(12).withYear(2012))))
                throw new RuntimeException("Invalid CC request time");

            if (stateRequest.hasContentId() && CourseItemType.valueOf(stateRequest.getContentType().toUpperCase()) == null)
                throw new RuntimeException("Invalid CC content type");

            if (stateRequest.hasContentId() && isInvalidCCContentName(stateRequest.getContentName(), stateRequest.getContentType()))
                throw new RuntimeException("Invalid CC content name");

            if (StringUtils.isNotEmpty(stateRequest.getContentData()) && !StringUtils.isNumeric(stateRequest.getContentData()))
                throw new RuntimeException(("Invalid content data (score)"));

            if (stateRequest.hasContentId() && CourseItemState.valueOf(stateRequest.getCourseItemState().toUpperCase()) == null)
                throw new RuntimeException("Invalid CC item state");
        }

        validateAudioTrackerList(callId, callerId, certificateCourseServiceRequest.getAudioTrackerRequestList(), ServiceType.CERTIFICATE_COURSE);

        CallDurationList callDurationList = certificateCourseServiceRequest.getCallDurationList();
        validateEquals(callDurationList.getCallerId(), callerId, "callerId and callDurationList callerId");
        validateEquals(callDurationList.getCallId(), callId, "callId and callDurationList callId");
        validateEquals(callDurationList.getCalledNumber(), calledNumber, "calledNum and callDurationList calledNum");
        for (CallDuration callDuration : callDurationList.all()) {
            if(callDuration.getCallEvent() == null)
                throw new RuntimeException("CC call duration call event is null");

            DateTime time = new DateTime(callDuration.getTime());
            if (time.isBefore(new DateTime().withDayOfMonth(1).withMonthOfYear(6).withYear(2012))
                    || time.isAfter(new DateTime().withDayOfMonth(31).withMonthOfYear(12).withYear(2012)))
                throw new RuntimeException("Invalid CC call duration time");
        }
    }

    private boolean isInvalidCCContentName(String contentName, String contentType) {
        return null == allCourseItemDimensions.getFor(contentName, CourseItemType.valueOf(contentType.toUpperCase()));
    }

    private void validateEquals(String one, String two, String valueNames) {
        if (!StringUtils.equals(one, two))
            throw new RuntimeException(valueNames + " not equal");
    }

    private void validateCCCalledNumber(String calledNumber) {
        if (StringUtils.isEmpty(calledNumber) || !StringUtils.isNumeric(calledNumber) || !StringUtils.equals(calledNumber, "5771102"))
            ;//throw new RuntimeException("Invalid CC called number");
    }

    private void validateCallerId(String callerId) {
        if (StringUtils.isEmpty(callerId) || !StringUtils.isNumeric(callerId) || callerId.length() < 10)
            throw new RuntimeException("Invalid caller id");
    }

    private void validateCallId(String callId) {
        String[] split = callId.split("-");
        if (split.length < 2)
            throw new RuntimeException("Invalid callId");

        if (StringUtils.isEmpty(split[0]) || !StringUtils.isNumeric(split[0]) || split[0].length() < 10)
            throw new RuntimeException("Invalid callId");

        if (StringUtils.isEmpty(split[1]) || !StringUtils.isNumeric(split[1]))
            throw new RuntimeException("Invalid callId");
    }
}
