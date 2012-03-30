package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.requests.LogData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationMeasureService {

    private FrontLineWorkerService frontLineWorkerService;

    private AllLocationDimensions allLocationDimensions;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllTimeDimensions allTimeDimensions;
    private AllRegistrationMeasures allRegistrationMeasures;

    @Autowired
    public RegistrationMeasureService(FrontLineWorkerService frontLineWorkerService,
                                      AllLocationDimensions allLocationDimensions,
                                      AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                      AllTimeDimensions allTimeDimensions,
                                      AllRegistrationMeasures allRegistrationMeasures) {

        this.frontLineWorkerService = frontLineWorkerService;

        this.allTimeDimensions = allTimeDimensions;
        this.allLocationDimensions = allLocationDimensions;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allRegistrationMeasures = allRegistrationMeasures;
    }


    public void createRegistrationMeasure(LogData logData) {
        String callerId = logData.getDataId();
        FrontLineWorker frontLineWorker = frontLineWorkerService.findByCallerId(callerId);

        LocationDimension locationDimension = allLocationDimensions.getFor(frontLineWorker.getLocationId());

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.getOrMakeFor(
                frontLineWorker.msisdn(), frontLineWorker.getOperator(),
                frontLineWorker.name(), frontLineWorker.status().toString());
        TimeDimension timeDimension = allTimeDimensions.getFor(frontLineWorker.registeredDate());

        RegistrationMeasure registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension);
        allRegistrationMeasures.add(registrationMeasure);
    }

    public void updateRegistrationStatusAndName(LogData logData) {
        FrontLineWorker existingFlw = frontLineWorkerService.findByCallerId(logData.getDataId());
        FrontLineWorkerDimension existingFlwDimension = allFrontLineWorkerDimensions.fetchFor(existingFlw.msisdn());

        existingFlwDimension.setName(existingFlw.name());
        existingFlwDimension.setStatus(existingFlw.status().toString());
        this.allFrontLineWorkerDimensions.update(existingFlwDimension);
    }
}
