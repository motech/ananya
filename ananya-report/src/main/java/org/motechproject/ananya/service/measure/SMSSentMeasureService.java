package org.motechproject.ananya.service.measure;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.SMSReference;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.repository.measure.AllSMSSentMeasures;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.SMSReferenceService;
import org.motechproject.ananya.service.dimension.LocationDimensionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SMSSentMeasureService {

    private static final Logger log = LoggerFactory.getLogger(SMSSentMeasureService.class);

    private AllSMSSentMeasures allSMSSentMeasures;
    private SMSReferenceService smsReferenceService;
    private FrontLineWorkerService frontLineWorkerService;
    private AllTimeDimensions allTimeDimensions;
    private AllRegistrationMeasures allRegistrationMeasures;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private LocationDimensionService locationDimensionService;

    public SMSSentMeasureService() {
    }

    @Autowired
    public SMSSentMeasureService(AllSMSSentMeasures allSMSSentMeasures,
                                 FrontLineWorkerService frontLineWorkerService,
                                 SMSReferenceService smsReferenceService,
                                 AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                 AllTimeDimensions allTimeDimensions,
                                 AllRegistrationMeasures allRegistrationMeasures, LocationDimensionService locationDimensionService) {
        this.allSMSSentMeasures = allSMSSentMeasures;
        this.smsReferenceService = smsReferenceService;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allTimeDimensions = allTimeDimensions;
        this.frontLineWorkerService = frontLineWorkerService;
        this.allRegistrationMeasures = allRegistrationMeasures;
        this.locationDimensionService = locationDimensionService;
    }

    @Transactional
    public void createSMSSentMeasure(String callerId) {
        boolean smsSent = false;
        int courseAttempt = frontLineWorkerService.getCurrentCourseAttempt(callerId);
        SMSReference smsReference = smsReferenceService.getSMSReferenceNumber(callerId);

        String referenceNumber = null;
        if (smsReference != null) {
            referenceNumber = smsReference.referenceNumbers(courseAttempt);
            if (referenceNumber != null)
                smsSent = true;
        }
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId));
        TimeDimension timeDimension = allTimeDimensions.getFor(DateTime.now());
        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
        LocationDimension locationDimension = registrationMeasure.getLocationDimension();

        SMSSentMeasure smsSentMeasure = new SMSSentMeasure(courseAttempt, referenceNumber, smsSent,
                frontLineWorkerDimension, timeDimension, locationDimension);
        allSMSSentMeasures.save(smsSentMeasure);
        log.info("Added SMS measure for " + callerId + "[smsRefNumber" + referenceNumber + "|attempt=" + courseAttempt + "]");
    }

    public void updateLocation(long callerId, String locationId) {
        List<SMSSentMeasure> smsSentMeasureList = allSMSSentMeasures.findByCallerId(callerId);
        LocationDimension locationDimension = locationDimensionService.getFor(locationId);

        for (SMSSentMeasure smsSentMeasure : smsSentMeasureList) {
            smsSentMeasure.setLocationDimension(locationDimension);
        }
        allSMSSentMeasures.updateAll(smsSentMeasureList);
    }

    public void updateLocation(String oldLocationId, String newLocationId) {
        LocationDimension newLocation = locationDimensionService.getFor(newLocationId);
        List<SMSSentMeasure> smsSentMeasureList = allSMSSentMeasures.findByLocationId(oldLocationId);
        for(SMSSentMeasure smsSentMeasure : smsSentMeasureList) {
            smsSentMeasure.setLocationDimension(newLocation);
        }
        allSMSSentMeasures.updateAll(smsSentMeasureList);
    }
}
