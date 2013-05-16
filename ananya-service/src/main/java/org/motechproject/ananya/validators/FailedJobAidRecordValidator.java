package org.motechproject.ananya.validators;

import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.contract.JobAidServiceRequest;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.mapper.JobAidServiceRequestMapper;
import org.motechproject.ananya.repository.dimension.AllCourseItemDetailsDimensions;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDetailsDimensions;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDimensions;
import org.motechproject.ananya.repository.dimension.AllLanguageDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FailedJobAidRecordValidator extends FailedRecordValidator {

    private Map<String, Boolean> fieldToPostDefinitions;

    @Autowired
    public FailedJobAidRecordValidator(AllCourseItemDimensions allCourseItemDimensions, AllJobAidContentDimensions allJobAidContentDimensions, AllCourseItemDetailsDimensions allCourseItemDetailsDimensions, AllJobAidContentDetailsDimensions allJobAidContentDetailsDimensions, AllLanguageDimension allLanguageDimension) {
        super(allCourseItemDimensions, allJobAidContentDimensions, allCourseItemDetailsDimensions, allJobAidContentDetailsDimensions, allLanguageDimension);
        fieldToPostDefinitions = new HashMap<String, Boolean>() {{
            put("callId", true);
            put("operator", true);
            put("language", false);
            put("callDuration", true);
            put("promptList", true);
            put("circle", false);
        }};
    }

    @Override
    public void validate(FailedRecordCSVRequest failedRecordRequest) {
        validateFields(failedRecordRequest.getFieldsToPostMap(), fieldToPostDefinitions);
        JobAidServiceRequest jobAidServiceRequest = JobAidServiceRequestMapper.map(failedRecordRequest);
        validateJobAidRequest(jobAidServiceRequest);
    }

    private void validateJobAidRequest(JobAidServiceRequest jobAidServiceRequest) {
        String callId = jobAidServiceRequest.getCallId();
        validateCallId(callId);

        String callerId = jobAidServiceRequest.getCallerId();
        validateCallerId(callerId);

        String calledNumber = jobAidServiceRequest.getCalledNumber();
        validateCalledNumber(calledNumber);

        validatePrompts(jobAidServiceRequest);
        validateAudioTrackerList(jobAidServiceRequest.getAudioTrackerRequestList(), ServiceType.JOB_AID);
        validateCallDurationList(jobAidServiceRequest.getCallDurationList());
    }

    private void validatePrompts(JobAidServiceRequest jobAidServiceRequest) {
        boolean isValid = true;
        try {
            List<String> prompts = jobAidServiceRequest.getPrompts();
            if(prompts == null) {
                isValid = false;
            }
        } catch (Exception ex) {
            isValid = false;
        }

        if(!isValid) {
            throw new FailedRecordValidationException(String.format("Invalid JobAid prompt list %s", jobAidServiceRequest.getPromptList()));
        }
    }
}
