package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationList;
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
        if (!Designation.contains(designation))
            return registrationResponse.withInvalidDesignation();

        LocationList locationList = new LocationList(locationService.getAll());
        Location location = locationList.findFor(district, block, village);
        if (location == null)
            return registrationResponse.withInvalidLocationStatus();

        frontLineWorkerService.createOrUpdate(callerId, name, designation, location);
        registrationMeasureService.createRegistrationMeasure(new LogData(LogType.REGISTRATION, callerId));
        log.info("Registered new FLW:" + callerId);
        return registrationResponse.withNewRegistrationDone();
    }
}
