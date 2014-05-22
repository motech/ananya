package org.motechproject.ananya.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.contract.CertificateCourseServiceRequest;
import org.motechproject.ananya.contract.CertificateCourseStateRequest;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;
import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.domain.CourseItemState;
import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.mapper.CertificateCourseServiceRequestMapper;
import org.motechproject.ananya.repository.dimension.AllCourseItemDetailsDimensions;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDetailsDimensions;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDimensions;
import org.motechproject.ananya.repository.dimension.AllLanguageDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class FailedCertificateCourseRecordValidator extends FailedRecordValidator {

    private HashMap<String, Boolean> fieldToPostDefinitions;

    @Autowired
    public FailedCertificateCourseRecordValidator(AllCourseItemDimensions allCourseItemDimensions, AllJobAidContentDimensions allJobAidContentDimensions, AllCourseItemDetailsDimensions allCourseItemDetailsDimensions, AllJobAidContentDetailsDimensions allJobAidContentDetailsDimensions, AllLanguageDimension allLanguageDimension) {
        super(allCourseItemDimensions, allJobAidContentDimensions, allCourseItemDetailsDimensions, allJobAidContentDetailsDimensions, allLanguageDimension);
        fieldToPostDefinitions = new HashMap<String, Boolean>() {{
            put("callId", true);
            put("operator", true);
            put("language", true);
            put("circle", false);
        }};
    }

    @Override
    public void validate(FailedRecordCSVRequest failedRecordRequest) {
        validateFields(failedRecordRequest.getFieldsToPostMap(), fieldToPostDefinitions);
        CertificateCourseServiceRequest certificateCourseServiceRequest = CertificateCourseServiceRequestMapper.map(failedRecordRequest);
        validateCertificateCourseRequest(certificateCourseServiceRequest);
    }

    private void validateCertificateCourseRequest(CertificateCourseServiceRequest certificateCourseServiceRequest) {
        String callId = certificateCourseServiceRequest.getCallId();
        validateCallId(callId);

        String callerId = certificateCourseServiceRequest.getCallerId();
        validateCallerId(callerId);

        String calledNumber = certificateCourseServiceRequest.getCalledNumber();
        validateCalledNumber(calledNumber);

        validateCourseStateRequestList(certificateCourseServiceRequest.getCertificateCourseStateRequestList());
        validateAudioTrackerList(certificateCourseServiceRequest.getAudioTrackerRequestList(), ServiceType.CERTIFICATE_COURSE);
        validateCallDurationList(certificateCourseServiceRequest.getCallDurationList());
    }

    private void validateCourseStateRequestList(CertificateCourseStateRequestList certificateCourseStateRequestList) {
        for (CertificateCourseStateRequest stateRequest : certificateCourseStateRequestList.all()) {
            validateCourseStateRequest(stateRequest);
        }
    }

    private void validateCourseStateRequest(CertificateCourseStateRequest stateRequest) {
        validateChapterIndex(stateRequest.getChapterIndex());

        validateLastLessonOrQuestionIndex(stateRequest.getLessonOrQuestionIndex());

        validateStateRequestTime(stateRequest);

        String contentType = stateRequest.getContentType();
        if (stateRequest.hasContentId() && CourseItemType.findFor(contentType) == null)
            throw new FailedRecordValidationException(String.format("Invalid request content type: %s", contentType));

        String contentName = stateRequest.getContentName();
        if (stateRequest.hasContentId() && isInvalidCertificateCourseContentName(contentName, contentType))
            throw new FailedRecordValidationException(String.format("Invalid request content name: %s", contentName));

        String contentData = stateRequest.getContentData();
        if (StringUtils.isNotEmpty(stateRequest.getContentData()) && !StringUtils.isNumeric(contentData))
            throw new FailedRecordValidationException((String.format("Invalid content data (score): %s", contentData)));

        String courseItemState = stateRequest.getCourseItemState();
        if (stateRequest.hasContentId() && CourseItemState.findFor(courseItemState) == null)
            throw new FailedRecordValidationException(String.format("Invalid request item state: %s", courseItemState));
    }

    private void validateLastLessonOrQuestionIndex(Integer lessonOrQuestionIndex) {
        if (lessonOrQuestionIndex != null && (lessonOrQuestionIndex < 0 || lessonOrQuestionIndex > 7)) {
            throw new FailedRecordValidationException(String.format("Invalid lesson or question index: %s", lessonOrQuestionIndex));
        }
    }

    private void validateChapterIndex(Integer chapterIndex) {
        if (chapterIndex != null && (chapterIndex < 0 || chapterIndex > 9)) {
            throw new FailedRecordValidationException(String.format("Invalid course chapter index: %s", chapterIndex));
        }
    }

    private void validateStateRequestTime(CertificateCourseStateRequest stateRequest) {
        boolean isValid = true;
        try {
            stateRequest.getTimeAsDateTime();
        } catch (Exception ex) {
            isValid = false;
        }
        if (!isValid) {
            throw new FailedRecordValidationException(String.format("Invalid request time: %s", stateRequest.getTime()));
        }
    }

    private boolean isInvalidCertificateCourseContentName(String contentName, String contentType) {
        return null == allCourseItemDimensions.getFor(contentName, CourseItemType.valueOf(contentType.toUpperCase()));
    }
}
