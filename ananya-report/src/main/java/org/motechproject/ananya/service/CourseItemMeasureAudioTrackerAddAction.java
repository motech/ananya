package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.AudioTrackerLogItem;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseItemMeasureAudioTrackerAddAction {

    private static final Logger log = LoggerFactory.getLogger(CourseItemMeasureService.class);

    private ReportDB reportDB;
    private AllTimeDimensions allTimeDimensions;
    private AllCourseItemDimensions allCourseItemDimensions;
    private AudioTrackerLogService audioTrackerLogService;

    public CourseItemMeasureAudioTrackerAddAction() {}

    @Autowired
    public CourseItemMeasureAudioTrackerAddAction(ReportDB reportDB,
                                                  AllTimeDimensions allTimeDimensions,
                                                  AllCourseItemDimensions allCourseItemDimensions,
                                                  AudioTrackerLogService audioTrackerLogService) {
        this.reportDB = reportDB;
        this.allTimeDimensions = allTimeDimensions;
        this.allCourseItemDimensions = allCourseItemDimensions;
        this.audioTrackerLogService = audioTrackerLogService;
    }

    public void process(String callId, AudioTrackerLog audioTrackerLog,
                        FrontLineWorkerDimension frontLineWorkerDimension, LocationDimension locationDimension) {
        if (audioTrackerLog == null) return;

        if (audioTrackerLog.getAudioTrackerLogItems() == null || audioTrackerLog.getAudioTrackerLogItems().isEmpty()) {
            audioTrackerLogService.remove(audioTrackerLog);
            return;
        }

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

    private int getPercentage(AudioTrackerLogItem logItem, Integer totalDuration) {
        return (int) Math.round((double) logItem.getDuration() * 100 / totalDuration);
    }
}
