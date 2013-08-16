package org.motechproject.ananya.validators;

import org.joda.time.DateTime;
import org.motechproject.ananya.contract.AudioTrackerRequest;
import org.motechproject.ananya.contract.AudioTrackerRequestList;
import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.domain.CallDuration;
import org.motechproject.ananya.domain.CallDurationList;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.domain.dimension.CourseItemDetailsDimension;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.JobAidContentDetailsDimension;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.domain.dimension.LanguageDimension;
import org.motechproject.ananya.repository.dimension.AllCourseItemDetailsDimensions;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDetailsDimensions;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDimensions;
import org.motechproject.ananya.repository.dimension.AllLanguageDimension;

import java.util.Map;

public abstract class FailedRecordValidator {

    protected final AllCourseItemDimensions allCourseItemDimensions;
    protected final AllCourseItemDetailsDimensions allCourseItemDetailsDimensions;
    protected final AllJobAidContentDimensions allJobAidContentDimensions;
    protected final AllJobAidContentDetailsDimensions allJobAidContentDetailsDimensions;
    protected final AllLanguageDimension allLanguageDimension;

    public FailedRecordValidator(AllCourseItemDimensions allCourseItemDimensions, AllJobAidContentDimensions allJobAidContentDimensions,
    		AllCourseItemDetailsDimensions allCourseItemDetailsDimensions, AllJobAidContentDetailsDimensions allJobAidContentDetailsDimensions, AllLanguageDimension allLanguageDimension) {
        this.allCourseItemDimensions = allCourseItemDimensions;
        this.allJobAidContentDimensions = allJobAidContentDimensions;
        this.allCourseItemDetailsDimensions=allCourseItemDetailsDimensions;
        this.allJobAidContentDetailsDimensions = allJobAidContentDetailsDimensions;
        this.allLanguageDimension=allLanguageDimension;
    }

    public abstract void validate(FailedRecordCSVRequest failedRecord);

    protected void validateCallerId(String callerId) {
        if (!ValidationUtils.isValidCallerId(callerId)) {
            throw new FailedRecordValidationException(String.format("Invalid caller id: %s", callerId));
        }
    }

    protected void validateCalledNumber(String calledNumber) {
        if (!ValidationUtils.isValidCalledNumber(calledNumber)) {
            throw new FailedRecordValidationException(String.format("Invalid called number: %s", calledNumber));
        }
    }

    protected void validateCallId(String callId) {
        if (!ValidationUtils.isValidCallId(callId)) {
            throw new FailedRecordValidationException(String.format("Invalid call id: %s", callId));
        }
    }

    protected void validateAudioTrackerList(AudioTrackerRequestList audioTrackerRequestList, ServiceType serviceType) {
        for (AudioTrackerRequest audioTrackerRequest : audioTrackerRequestList.all()) {
            String contentId = audioTrackerRequest.getContentId();

            if (serviceType.isCertificateCourse()) {
            	LanguageDimension languageDimension = allLanguageDimension.getFor(audioTrackerRequest.getLanguage());
            	CourseItemDetailsDimension courseItemDetailsDimension = allCourseItemDetailsDimensions.getFor(contentId, languageDimension.getId());
                CourseItemDimension courseItemDimension = allCourseItemDimensions.getFor(contentId);
                if (courseItemDimension == null || courseItemDetailsDimension == null)
                    throw new FailedRecordValidationException(String.format("Invalid audio tracker content id: %s", contentId));

                if (audioTrackerRequest.getDuration() > courseItemDetailsDimension.getDuration() && 232450 != courseItemDetailsDimension.getDuration())
                    // commenting out check for courseItem with duration 232450, because,
                    // already there are many measures in db whose duration is greater than
                    // the corresponding course item dimension, this needs to be analysed separately
                    throw new FailedRecordValidationException(String.format("Audio tracker duration greater than actual course item duration: %s, actual: %s, content id: %s", audioTrackerRequest.getDuration(), courseItemDetailsDimension.getDuration(), contentId));


                validateAudioTrackerRequestTime(audioTrackerRequest, contentId);
            } else {
            	LanguageDimension languageDimension = allLanguageDimension.getFor(audioTrackerRequest.getLanguage());
            	JobAidContentDetailsDimension jobAidContentDetailsDimension = allJobAidContentDetailsDimensions.getFor(contentId, languageDimension.getId());
                JobAidContentDimension jobAidContentDimension = allJobAidContentDimensions.findByContentId(contentId);
                if (jobAidContentDimension == null)
                    throw new FailedRecordValidationException(String.format("Invalid audio tracker content id: %s", contentId));

                if (audioTrackerRequest.getDuration() > jobAidContentDetailsDimension.getDuration())
                    throw new FailedRecordValidationException(String.format("Audio tracker duration greater than actual job aid content duration: %s, actual: %s, content id: %s", audioTrackerRequest.getDuration(), jobAidContentDetailsDimension.getDuration(), contentId));
            }
            validateAudioTrackerRequestTime(audioTrackerRequest, contentId);
        }
    }

    private void validateAudioTrackerRequestTime(AudioTrackerRequest audioTrackerRequest, String contentId) {
        boolean isValid = true;
        try {
            audioTrackerRequest.getTimeAsDateTime();
        } catch (Exception ex) {
            isValid = false;
        }
        if (!isValid) {
            throw new FailedRecordValidationException(String.format("Invalid audio tracker request time: %s, content id: %s", audioTrackerRequest.getTime(), contentId));
        }
    }

    protected void validateCallDurationList(CallDurationList callDurationList) {
        for (CallDuration callDuration : callDurationList.all()) {
            if (callDuration.getCallEvent() == null)
                throw new FailedRecordValidationException("Call duration call event is null");

            new DateTime(callDuration.getTime());
        }
    }

    protected void validateFields(Map<String, String> fields, Map<String, Boolean> fieldDefinitions) {
        for (String fieldToPost : fields.keySet()) {
            if (!fieldDefinitions.containsKey(fieldToPost)) {
                throw new FailedRecordValidationException(String.format("Invalid field to post: %s", fieldToPost));
            }
        }

        for (String field : fieldDefinitions.keySet()) {
            boolean isMandatory = fieldDefinitions.get(field);
            if (isMandatory && !fields.containsKey(field)) {
                throw new FailedRecordValidationException(String.format("Missing mandatory field to post: %s", field));
            }
        }
    }

}
