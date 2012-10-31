package org.motechproject.ananya.service.measure;

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
import org.motechproject.ananya.service.AudioTrackerLogService;
import org.motechproject.ananya.service.dimension.LocationDimensionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class JobAidContentMeasureService {

    private static final Logger log = LoggerFactory.getLogger(JobAidContentMeasureService.class);

    private AudioTrackerLogService audioTrackerLogService;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllRegistrationMeasures allRegistrationMeasures;
    private AllJobAidContentDimensions allJobAidContentDimensions;
    private AllTimeDimensions allTimeDimensions;
    private AllJobAidContentMeasures allJobAidContentMeasures;
    private LocationDimensionService locationDimensionService;

    public JobAidContentMeasureService() {
    }

    @Autowired
    public JobAidContentMeasureService(AudioTrackerLogService audioTrackerLogService,
                                       AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                       AllRegistrationMeasures allRegistrationMeasures,
                                       AllJobAidContentDimensions allJobAidContentDimensions,
                                       AllTimeDimensions allTimeDimensions,
                                       AllJobAidContentMeasures allJobAidContentMeasures, LocationDimensionService locationDimensionService) {
        this.audioTrackerLogService = audioTrackerLogService;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allRegistrationMeasures = allRegistrationMeasures;
        this.allJobAidContentDimensions = allJobAidContentDimensions;
        this.allTimeDimensions = allTimeDimensions;
        this.allJobAidContentMeasures = allJobAidContentMeasures;
        this.locationDimensionService = locationDimensionService;
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
        TimeDimension timeDimension = allTimeDimensions.getFor(audioTrackerLog.time());
        LocationDimension locationDimension = registrationMeasure.getLocationDimension();

        for (AudioTrackerLogItem audioTrackerLogItem : audioTrackerLog.items()) {
            JobAidContentDimension jobAidContentDimension = allJobAidContentDimensions.findByContentId(audioTrackerLogItem.getContentId());

            JobAidContentMeasure jobAidContentMeasure = new JobAidContentMeasure(callId,
                    frontLineWorkerDimension, locationDimension, jobAidContentDimension, timeDimension,
                    audioTrackerLogItem.getTime(),
                    audioTrackerLogItem.getDuration(),
                    audioTrackerLogItem.getPercentage(jobAidContentDimension.getDuration()));

            allJobAidContentMeasures.add(jobAidContentMeasure);
        }
        log.info(callId + "- audioTrackerLog jobAidContentMeasures added");
        removeLog(callId, audioTrackerLog);
    }

    private void removeLog(String callId, AudioTrackerLog audioTrackerLog) {
        audioTrackerLogService.remove(audioTrackerLog);
        log.info(callId + "- audioTrackerLog removed");
    }


    public List<Long> getAllFrontLineWorkerMsisdnsBetween(Date startDate, Date endDate) {
        return allJobAidContentMeasures.getFilteredFrontLineWorkerMsisdns(startDate, endDate);
    }

    public void updateLocation(long callerId, String locationId) {
        List<JobAidContentMeasure> jobAidContentMeasureList = allJobAidContentMeasures.findByCallerId(callerId);
        LocationDimension locationDimension = locationDimensionService.getFor(locationId);

        for (JobAidContentMeasure jobAidContentMeasure : jobAidContentMeasureList) {
            jobAidContentMeasure.setLocationDimension(locationDimension);
        }
        allJobAidContentMeasures.updateAll(jobAidContentMeasureList);
    }

    public void updateLocation(String oldLocationId, String newLocationId) {
        LocationDimension newLocation = locationDimensionService.getFor(newLocationId);
        List<JobAidContentMeasure> jobAidContentMeasures = allJobAidContentMeasures.findByLocationId(oldLocationId);
        for (JobAidContentMeasure jobAidContentMeasure : jobAidContentMeasures) {
            jobAidContentMeasure.setLocationDimension(newLocation);
        }
        allJobAidContentMeasures.updateAll(jobAidContentMeasures);
    }
}
