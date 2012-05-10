package org.motechproject.ananya.support.synchroniser;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.service.AudioTrackerLogService;
import org.motechproject.ananya.service.CourseItemMeasureService;
import org.motechproject.ananya.service.JobAidContentMeasureService;
import org.motechproject.ananya.support.synchroniser.log.SynchroniserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AudioTrackerSynchroniser implements Synchroniser {

    private AudioTrackerLogService audioTrackerLogService;
    private CourseItemMeasureService courseItemMeasureService;
    private JobAidContentMeasureService jobAidContentMeasureService;

    @Autowired
    public AudioTrackerSynchroniser(AudioTrackerLogService audioTrackerLogService, CourseItemMeasureService courseItemMeasureService, JobAidContentMeasureService jobAidContentMeasureService) {
        this.audioTrackerLogService = audioTrackerLogService;
        this.courseItemMeasureService = courseItemMeasureService;
        this.jobAidContentMeasureService = jobAidContentMeasureService;
    }

    @Override
    public SynchroniserLog replicate(DateTime fromDate, DateTime toDate) {
        SynchroniserLog synchroniserLog = new SynchroniserLog("AudioTracker");
        List<AudioTrackerLog> audioTrackerLogs = audioTrackerLogService.getAll();
        for (AudioTrackerLog audioTrackerLog : audioTrackerLogs) {
            try {
                if(audioTrackerLog.getServiceType().equals(ServiceType.CERTIFICATE_COURSE))
                    courseItemMeasureService.createCourseItemMeasure(audioTrackerLog.getCallId());
                else
                    jobAidContentMeasureService.createJobAidContentMeasure(audioTrackerLog.getCallId());
                synchroniserLog.add(audioTrackerLog.getCallId(), "Success");
            } catch (Exception e) {
                synchroniserLog.add(audioTrackerLog.getCallId(), "Error:" + ExceptionUtils.getFullStackTrace(e));
            }
        }
        return synchroniserLog;
    }

    @Override
    public Priority runPriority() {
        return Priority.low;
    }
}
