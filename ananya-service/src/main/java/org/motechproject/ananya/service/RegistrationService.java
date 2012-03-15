package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.response.RegistrationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private static Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private FrontLineWorkerService frontLineWorkerService;
    private RegistrationMeasureService registrationMeasureService;
    private LocationService locationService;

    @Autowired
    public RegistrationService(FrontLineWorkerService frontLineWorkerService,
                               RegistrationMeasureService registrationMeasureService,
                               LocationService locationService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.registrationMeasureService = registrationMeasureService;
        this.locationService = locationService;
    }

    public RegistrationResponse registerFlw(String callerId, String name, String designation, String district, String block, String village) {
        RegistrationResponse registrationResponse = new RegistrationResponse();

        if (StringUtils.isBlank(callerId))
            return registrationResponse.withInvalidCallerId();
        if (StringUtils.isBlank(name))
            return registrationResponse.withInvalidName();

        Location location = locationService.fetchFor(district, block, village);
        if (location == null)
            return registrationResponse.withInvalidLocationStatus();

        FrontLineWorker frontLineWorker = frontLineWorkerService.findByCallerId(callerId);
        if (frontLineWorker != null) {
            frontLineWorkerService.updateLocation(callerId, location);
            log.info("Updated location for FLW:" + callerId);
            return registrationResponse.withLocationUpdated();
        }

        frontLineWorkerService.createNew(callerId, name, designation, location);
        registrationMeasureService.createRegistrationMeasure(new LogData(LogType.REGISTRATION, callerId));
        log.info("Registered new FLW:" + callerId);
        return registrationResponse.withNewRegistrationDone();
    }
}
