package org.motechproject.ananya.service.measure;

import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.AudioTrackerLogItem;
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
import org.motechproject.ananya.service.AudioTrackerLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseAudioTrackerMeasureService {

    private static final Logger log = LoggerFactory.getLogger(CourseAudioTrackerMeasureService.class);

    private ReportDB reportDB;
    private AllTimeDimensions allTimeDimensions;
    private AllCourseItemDimensions allCourseItemDimensions;
    private AudioTrackerLogService audioTrackerLogService;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllRegistrationMeasures allRegistrationMeasures;

    public CourseAudioTrackerMeasureService() {
    }

    @Autowired
    public CourseAudioTrackerMeasureService(ReportDB reportDB,
                                            AllTimeDimensions allTimeDimensions,
                                            AllCourseItemDimensions allCourseItemDimensions,
                                            AudioTrackerLogService audioTrackerLogService,
                                            AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                            AllRegistrationMeasures allRegistrationMeasures) {
        this.reportDB = reportDB;
        this.allTimeDimensions = allTimeDimensions;
        this.allCourseItemDimensions = allCourseItemDimensions;
        this.audioTrackerLogService = audioTrackerLogService;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allRegistrationMeasures = allRegistrationMeasures;
    }

    @Transactional
    public void createFor(String callId) {
        AudioTrackerLog audioTrackerLog = audioTrackerLogService.getLogFor(callId);
        if (audioTrackerLog == null) {
            log.info(callId + "- audioTrackerLog not present");
            return;
        }

        if (audioTrackerLog.hasNoItems()) {
            log.info(callId + "- audioTrackerLog has no items");
            removeLog(callId, audioTrackerLog);
            return;
        }

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(audioTrackerLog.callerIdAsLong());
        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
        LocationDimension locationDimension = registrationMeasure.getLocationDimension();
        TimeDimension timeDimension = allTimeDimensions.getFor(audioTrackerLog.time());

        for (AudioTrackerLogItem logItem : audioTrackerLog.items()) {
            CourseItemDimension courseItemDimension = allCourseItemDimensions.getFor(logItem.getContentId());

            CourseItemMeasure courseItemMeasure = new CourseItemMeasure(callId,
                    timeDimension, courseItemDimension, frontLineWorkerDimension, locationDimension,
                    logItem.getTime(), logItem.getDuration(),
                    logItem.getPercentage(courseItemDimension.getDuration())
            );
            reportDB.add(courseItemMeasure);
        }
        log.info(callId + "- audioTrackerLog courseItemMeasures added");
        removeLog(callId, audioTrackerLog);
    }

    private void removeLog(String callId, AudioTrackerLog audioTrackerLog) {
        audioTrackerLogService.remove(audioTrackerLog);
        log.info(callId + "- audioTrackerLog removed");
    }

}
