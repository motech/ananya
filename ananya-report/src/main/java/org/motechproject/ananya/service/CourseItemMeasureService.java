package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.service.helpers.CourseItemMeasureServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Transactional
    public void createCourseItemMeasure(
            String callId, CourseItemMeasureServiceHelper courseItemMeasureServiceHelper) {
        courseItemMeasureAddAction.process(callId, courseItemMeasureServiceHelper.getCourseLog(),
                courseItemMeasureServiceHelper.getFrontLineWorkerDimension(),
                courseItemMeasureServiceHelper.getLocationDimension());
    }

    @Transactional
    public void createCourseItemMeasureAudioTracker(
            String callId, CourseItemMeasureServiceHelper courseItemMeasureServiceHelper) {
        courseItemMeasureAudioTrackerAddAction.process(callId, courseItemMeasureServiceHelper.getAudioTrackerLog(),
                courseItemMeasureServiceHelper.getFrontLineWorkerDimension(),
                courseItemMeasureServiceHelper.getLocationDimension());
    }

    public CourseItemMeasureServiceHelper getCourseItemMeasureServiceHelper(String callId) {
        CertificationCourseLog courseLog = certificateCourseLogService.getLogFor(callId);
        AudioTrackerLog audioTrackerLog = audioTrackerLogService.getLogFor(callId);
        Long callerId = getCallerId(courseLog, audioTrackerLog);

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(callerId);
        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
        LocationDimension locationDimension = registrationMeasure.getLocationDimension();

        return new CourseItemMeasureServiceHelper(courseLog, frontLineWorkerDimension, locationDimension, audioTrackerLog);
    }

    private Long getCallerId(CertificationCourseLog courseLog, AudioTrackerLog audioTrackerLog) {
        if (courseLog != null)
            return courseLog.callerIdAsLong();
        else if (audioTrackerLog != null)
            return audioTrackerLog.callerIdAsLong();
        return null;
    }

}
