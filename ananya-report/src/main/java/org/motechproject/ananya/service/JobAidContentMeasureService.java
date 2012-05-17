    package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.AudioTrackerLogItem;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.JobAidContentMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllJobAidContentMeasures;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobAidContentMeasureService {

    private static final Logger log = LoggerFactory.getLogger(CourseItemMeasureService.class);

    private AudioTrackerLogService audioTrackerLogService;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllRegistrationMeasures allRegistrationMeasures;
    private AllJobAidContentDimensions allJobAidContentDimensions;
    private AllTimeDimensions allTimeDimensions;
    private AllJobAidContentMeasures allJobAidContentMeasures;

    public JobAidContentMeasureService() { }

    @Autowired
    public JobAidContentMeasureService(AudioTrackerLogService audioTrackerLogService,
                                       AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                       AllRegistrationMeasures allRegistrationMeasures,
                                       AllJobAidContentDimensions allJobAidContentDimensions,
                                       AllTimeDimensions allTimeDimensions,
                                       AllJobAidContentMeasures allJobAidContentMeasures) {
        this.audioTrackerLogService = audioTrackerLogService;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allRegistrationMeasures = allRegistrationMeasures;
        this.allJobAidContentDimensions = allJobAidContentDimensions;
        this.allTimeDimensions = allTimeDimensions;
        this.allJobAidContentMeasures = allJobAidContentMeasures;
    }

    @Transactional
    public void createJobAidContentMeasure(String callId) {
        log.info("Creating job aid content measure for call id " + callId);

        AudioTrackerLog audioTrackerLog = audioTrackerLogService.getLogFor(callId);
        if(audioTrackerLog == null) return;

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(audioTrackerLog.callerIdAsLong());
        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
        LocationDimension locationDimension = registrationMeasure.getLocationDimension();

        TimeDimension timeDimension = allTimeDimensions.getFor(audioTrackerLog.getAudioTrackerLogItems().get(0).getTime());

        for (AudioTrackerLogItem audioTrackerLogItem : audioTrackerLog.getAudioTrackerLogItems()) {

            JobAidContentDimension jobAidContentDimension = allJobAidContentDimensions.findByContentId(audioTrackerLogItem.getContentId());

            JobAidContentMeasure jobAidContentMeasure = new JobAidContentMeasure(frontLineWorkerDimension, callId,
                    locationDimension, jobAidContentDimension, timeDimension, audioTrackerLogItem.getTime(),
                    audioTrackerLogItem.getDuration(), getPercentage(audioTrackerLogItem, jobAidContentDimension.getDuration()));

            allJobAidContentMeasures.add(jobAidContentMeasure);
        }

        audioTrackerLogService.remove(audioTrackerLog);
    }

    private int getPercentage(AudioTrackerLogItem logItem, Integer totalDuration) {
        return (logItem.getDuration() * 100 )/totalDuration;
    }
}
