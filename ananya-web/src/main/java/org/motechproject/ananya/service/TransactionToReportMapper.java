package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.log.LogData;
import org.motechproject.ananya.domain.log.RegistrationLog;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionToReportMapper {

    private AllRegistrationLogs allRegistrationLogs;
    private AllFrontLineWorkers allFrontLineWorkers;
    private AllLocations allLocations;

    private AllLocationDimensions allLocationDimensions;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllTimeDimensions allTimeDimensions;

    private ReportDB reportDB;

    @Autowired
    public TransactionToReportMapper(
            AllRegistrationLogs allRegistrationLogs, AllFrontLineWorkers allFrontLineWorkers, AllLocations allLocations,
            AllLocationDimensions allLocationDimensions, AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
            AllTimeDimensions allTimeDimensions, ReportDB reportDB) {

        this.allRegistrationLogs = allRegistrationLogs;
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.allLocations = allLocations;

        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allTimeDimensions = allTimeDimensions;
        this.allLocationDimensions = allLocationDimensions;

        this.reportDB = reportDB;
    }

    public void transformAndPushToReportingDB(LogData logData) {
        RegistrationLog registrationLog = allRegistrationLogs.findByLogId(logData.getDataId());
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(registrationLog.getCallerId());
        Location location = allLocations.findById(frontLineWorker.getLocationId());

        FrontLineWorkerDimension frontLineWorkerDimension =
                this.allFrontLineWorkerDimensions.getFrontLineWorkerDimension(Long.getLong(frontLineWorker.getMsisdn()),
                        registrationLog.getOperator(), frontLineWorker.getName(), frontLineWorker.getStatus().toString());

        LocationDimension locationDimension =
                this.allLocationDimensions.getLocationDimension(
                        location.getExternalId(), location.district(), location.blockName(), location.panchayat());

        TimeDimension timeDimension = this.allTimeDimensions.getTimeDimension(registrationLog.getStartTime());

        RegistrationMeasure registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension);
        reportDB.add(registrationMeasure);
    }
}
