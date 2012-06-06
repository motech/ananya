package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseItemMeasureService {
    private static final Logger log = LoggerFactory.getLogger(CourseItemMeasureService.class);

    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private CertificateCourseLogService certificateCourseLogService;
    private AllRegistrationMeasures allRegistrationMeasures;
    private AudioTrackerLogService audioTrackerLogService;
    private CourseItemMeasureAddAction courseItemMeasureAddAction;
    private CourseItemMeasureAudioTrackerAddAction courseItemMeasureAudioTrackerAddAction;

    public CourseItemMeasureService() {
    }

    @Autowired
    public CourseItemMeasureService(AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                    CertificateCourseLogService certificateCourseLogService,
                                    AudioTrackerLogService audioTrackerLogService,
                                    AllRegistrationMeasures allRegistrationMeasures,
                                    CourseItemMeasureAddAction courseItemMeasureAddAction,
                                    CourseItemMeasureAudioTrackerAddAction courseItemMeasureAudioTrackerAddAction) {
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.certificateCourseLogService = certificateCourseLogService;
        this.audioTrackerLogService = audioTrackerLogService;
        this.allRegistrationMeasures = allRegistrationMeasures;
        this.courseItemMeasureAddAction = courseItemMeasureAddAction;
        this.courseItemMeasureAudioTrackerAddAction = courseItemMeasureAudioTrackerAddAction;
    }

    public void createCourseItemMeasure(String callId) {
        CertificationCourseLog courseLog = certificateCourseLogService.getLogFor(callId);
        AudioTrackerLog audioTrackerLog = audioTrackerLogService.getLogFor(callId);
        Long callerId = getCallerId(courseLog, audioTrackerLog);

        if (callerId == null) return;

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(callerId);
        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
        LocationDimension locationDimension = registrationMeasure.getLocationDimension();

        courseItemMeasureAddAction.process(callId, courseLog, frontLineWorkerDimension, locationDimension);
        courseItemMeasureAudioTrackerAddAction.process(callId, audioTrackerLog, frontLineWorkerDimension, locationDimension);
    }

    private Long getCallerId(CertificationCourseLog courseLog, AudioTrackerLog audioTrackerLog) {
        if (courseLog != null)
            return courseLog.callerIdAsLong();
        else if (audioTrackerLog != null)
            return audioTrackerLog.callerIdAsLong();
        return null;
    }

}
