package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.SMSReference;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SMSSentMeasureService {

    private ReportDB reportDB;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllTimeDimensions allTimeDimensions;
    private FrontLineWorkerService frontLineWorkerService;

    @Autowired
    public SMSSentMeasureService(ReportDB reportDB, AllFrontLineWorkerDimensions allFrontLineWorkerDimensions, AllTimeDimensions allTimeDimensions, FrontLineWorkerService frontLineWorkerService) {
        this.reportDB = reportDB;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allTimeDimensions = allTimeDimensions;
        this.frontLineWorkerService = frontLineWorkerService;
    }

    public void createSMSSentMeasure(String callerId) {
        boolean smsSent = false;
        final int courseAttempt = frontLineWorkerService.getCurrentCourseAttempt(callerId);
        final SMSReference smsReference = frontLineWorkerService.getSMSReferenceNumber(callerId);
        String referenceNumber = "";
        if(smsReference != null) {
            referenceNumber = smsReference.referenceNumbers(courseAttempt);
            if(referenceNumber != null) {
                smsSent = true;
            }
        }
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.getOrMakeFor(Long.valueOf(callerId),"","","");
        TimeDimension timeDimension = allTimeDimensions.getFor(DateTime.now());

        SMSSentMeasure smsSentMeasure = new SMSSentMeasure(courseAttempt, referenceNumber, smsSent, frontLineWorkerDimension, timeDimension);

        reportDB.add(smsSentMeasure);
    }
}
