package org.motechproject.ananya.service.measure;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.SMSReference;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SMSSentMeasureService {

    private static final Logger log = LoggerFactory.getLogger(SMSSentMeasureService.class);

    private ReportDB reportDB;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllTimeDimensions allTimeDimensions;
    private FrontLineWorkerService frontLineWorkerService;
    private AllRegistrationMeasures allRegistrationMeasures;

    public SMSSentMeasureService() {
    }

    @Autowired
    public SMSSentMeasureService(ReportDB reportDB, AllFrontLineWorkerDimensions allFrontLineWorkerDimensions, AllTimeDimensions allTimeDimensions, FrontLineWorkerService frontLineWorkerService, AllRegistrationMeasures allRegistrationMeasures) {
        this.reportDB = reportDB;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allTimeDimensions = allTimeDimensions;
        this.frontLineWorkerService = frontLineWorkerService;
        this.allRegistrationMeasures = allRegistrationMeasures;
    }

    @Transactional
    public void createSMSSentMeasure(String callerId) {
        boolean smsSent = false;
        int courseAttempt = frontLineWorkerService.getCurrentCourseAttempt(callerId);
        SMSReference smsReference = frontLineWorkerService.getSMSReferenceNumber(callerId);

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
        reportDB.add(smsSentMeasure);
        log.info("Added SMS measure for " + callerId + "[smsRefNumber" + referenceNumber + "|attempt=" + courseAttempt + "]");
    }
}
