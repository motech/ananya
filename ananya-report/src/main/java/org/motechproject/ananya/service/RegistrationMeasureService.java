package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LogData;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.*;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationMeasureService {

    private AllRegistrationLogs allRegistrationLogs;
    private AllFrontLineWorkers allFrontLineWorkers;
    private AllLocations allLocations;

    private AllLocationDimensions allLocationDimensions;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllTimeDimensions allTimeDimensions;

    private ReportDB reportDB;

    @Autowired
    public RegistrationMeasureService(
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

    
    public void createRegistrationMeasure(LogData logData) {
        //TODO:should not access the repos directly [sush/rahul]
        RegistrationLog registrationLog = allRegistrationLogs.get(logData.getDataId());
        createRegistrationMeasureWith(registrationLog.getCallerId());
    }

    public void createRegistrationMeasureWith(String msisdn) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        Location location = allLocations.get(frontLineWorker.getLocationId());

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.getOrMakeFor(
                frontLineWorker.msisdn(), frontLineWorker.getOperator(),
                frontLineWorker.name(), frontLineWorker.status().toString());

        LocationDimension locationDimension = allLocationDimensions.getFor(location.getExternalId());
        TimeDimension timeDimension = allTimeDimensions.getFor(frontLineWorker.registeredDate());

        RegistrationMeasure registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension);
        reportDB.add(registrationMeasure);
    }

    public void updateRegistrationStatusAndName(LogData logData) {
        FrontLineWorker existingFlw = allFrontLineWorkers.get(logData.getDataId());
        FrontLineWorkerDimension existingFlwDimension = allFrontLineWorkerDimensions.fetchFor(existingFlw.msisdn());

        existingFlwDimension.setName(existingFlw.name());
        existingFlwDimension.setStatus(existingFlw.status().toString());
        this.allFrontLineWorkerDimensions.update(existingFlwDimension);
    }
}
