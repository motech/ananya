package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.AudioTrackerLogItem;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.domain.CertificationCourseLogItem;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseItemMeasureService {
    private static final Logger log = LoggerFactory.getLogger(CourseItemMeasureService.class);

    private ReportDB reportDB;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllTimeDimensions allTimeDimensions;
    private AllCourseItemDimensions allCourseItemDimensions;
    private CertificateCourseLogService certificateCourseLogService;
    private AllRegistrationMeasures allRegistrationMeasures;
    private AudioTrackerLogService audioTrackerLogService;

    public CourseItemMeasureService() {
    }

    @Autowired
    public CourseItemMeasureService(ReportDB reportDB,
                                    AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                    AllTimeDimensions allTimeDimensions,
                                    AllCourseItemDimensions allCourseItemDimensions,
                                    CertificateCourseLogService certificateCourseLogService,
                                    AudioTrackerLogService audioTrackerLogService,
                                    AllRegistrationMeasures allRegistrationMeasures) {
        this.reportDB = reportDB;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allTimeDimensions = allTimeDimensions;
        this.allCourseItemDimensions = allCourseItemDimensions;
        this.certificateCourseLogService = certificateCourseLogService;
        this.audioTrackerLogService = audioTrackerLogService;
        this.allRegistrationMeasures = allRegistrationMeasures;
    }

    @Transactional
    public void createCourseItemMeasure(String callId) {
        CertificationCourseLog courseLog = certificateCourseLogService.getLogFor(callId);
        AudioTrackerLog audioTrackerLog = audioTrackerLogService.getLogFor(callId);
        Long callerId = getCallerId(courseLog, audioTrackerLog);

        if (callerId == null) return;

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(callerId);
        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
        LocationDimension locationDimension = registrationMeasure.getLocationDimension();

        addCertificateCourseItemMeasure(callId, courseLog, frontLineWorkerDimension, locationDimension);
        addAudioTrackerCourseItemMeasure(callId, audioTrackerLog, frontLineWorkerDimension, locationDimension);
    }

    private void addCertificateCourseItemMeasure(String callId, CertificationCourseLog courseLog, FrontLineWorkerDimension frontLineWorkerDimension, LocationDimension locationDimension) {
        if (courseLog == null) return;

        List<CertificationCourseLogItem> courseLogItems = courseLog.getCourseLogItems();

        TimeDimension timeDimension = allTimeDimensions.getFor(courseLogItems.get(0).getTime());

        for (CertificationCourseLogItem logItem : courseLogItems) {
            CourseItemDimension courseItemDimension = allCourseItemDimensions.getFor(
                    logItem.getContentName(),
                    logItem.getContentType());

            CourseItemMeasure courseItemMeasure = new CourseItemMeasure(
                    timeDimension,
                    courseItemDimension,
                    frontLineWorkerDimension,
                    locationDimension,
                    logItem.giveScore(),
                    logItem.getCourseItemState(),
                    callId);

            reportDB.add(courseItemMeasure);
        }
        certificateCourseLogService.remove(courseLog);
        log.info("Added Certificate CourseItemMeasures for CallId=" + callId);
    }

    private void addAudioTrackerCourseItemMeasure(String callId, AudioTrackerLog audioTrackerLog, FrontLineWorkerDimension frontLineWorkerDimension, LocationDimension locationDimension) {
        if (audioTrackerLog == null) return;

        TimeDimension timeDimension = allTimeDimensions.getFor(audioTrackerLog.getAudioTrackerLogItems().get(0).getTime());

        for (AudioTrackerLogItem logItem : audioTrackerLog.getAudioTrackerLogItems()) {
            CourseItemDimension courseItemDimension = allCourseItemDimensions.getFor(logItem.getContentId());
            Integer totalDuration = courseItemDimension.getDuration();
            CourseItemMeasure courseItemMeasure = new CourseItemMeasure(
                    timeDimension,
                    courseItemDimension,
                    frontLineWorkerDimension,
                    locationDimension,
                    logItem.getTime(),
                    logItem.getDuration(),
                    getPercentage(logItem, totalDuration),
                    callId
            );

            reportDB.add(courseItemMeasure);
        }
        audioTrackerLogService.remove(audioTrackerLog);
        log.info("Added AudioTrack CourseItemMeasures for CallId=" + callId);
    }

    private Long getCallerId(CertificationCourseLog courseLog, AudioTrackerLog audioTrackerLog) {
        if (courseLog != null)
            return courseLog.callerIdAsLong();
        else if (audioTrackerLog != null)
            return audioTrackerLog.callerIdAsLong();
        return null;
    }

    private int getPercentage(AudioTrackerLogItem logItem, Integer totalDuration) {
        return (int) Math.round((double) logItem.getDuration() * 100 / totalDuration);
    }
}
