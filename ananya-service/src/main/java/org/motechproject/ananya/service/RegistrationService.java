package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.response.RegistrationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<RegistrationResponse> registerAllFLWs(List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        LocationList locationList = new LocationList(locationService.getAll());
        List<RegistrationResponse> registrationResponses = new ArrayList<RegistrationResponse>();
        for (FrontLineWorkerRequest frontLineWorkerRequest : frontLineWorkerRequests) {
            RegistrationResponse registrationResponse = registerFlw(frontLineWorkerRequest.getMsisdn(),
                    frontLineWorkerRequest.getName(),
                    frontLineWorkerRequest.getDesignation(),
                    frontLineWorkerRequest.getOperator(),
                    frontLineWorkerRequest.getLocation().getDistrict(),
                    frontLineWorkerRequest.getLocation().getBlock(),
                    frontLineWorkerRequest.getLocation().getPanchayat(),
                    locationList);
            registrationResponses.add(registrationResponse);
        }
        return registrationResponses;
    }

    public RegistrationResponse createOrUpdateFLW(FrontLineWorkerRequest frontLineWorkerRequest) {
        LocationList locationList = new LocationList(locationService.getAll());
        return registerFlw(frontLineWorkerRequest.getMsisdn(),
                frontLineWorkerRequest.getName(),
                frontLineWorkerRequest.getDesignation(),
                frontLineWorkerRequest.getOperator(),
                frontLineWorkerRequest.getLocation().getDistrict(),
                frontLineWorkerRequest.getLocation().getBlock(),
                frontLineWorkerRequest.getLocation().getPanchayat(),
                locationList);
    }

    private RegistrationResponse registerFlw(String callerId, String name, String designation, String operator, String district, String block, String panchayat, LocationList locationList) {
        RegistrationResponse registrationResponse = new RegistrationResponse(name, callerId, designation, operator, district, block, panchayat);

        if (isInvalidCallerId(callerId))
            return registrationResponse.withInvalidCallerId();

        Location location = locationList.findFor(district, block, panchayat);
        if (location == null)
            return registrationResponse.withInvalidLocationStatus();

        RegistrationStatus registrationStatus = StringUtils.isBlank(name) || Designation.isInValid(designation) ? RegistrationStatus.PARTIALLY_REGISTERED : RegistrationStatus.REGISTERED;
        Designation desgn = Designation.getFor(designation);

        frontLineWorkerService.createOrUpdate(callerId, name, desgn, location, registrationStatus);
        registrationMeasureService.createRegistrationMeasure(callerId);

        log.info("Registered new FLW:" + callerId);
        return registrationResponse.withNewRegistrationDone();
    }

    private boolean isInvalidCallerId(String callerId) {
        return StringUtils.isBlank(callerId) || !StringUtils.isNumeric(callerId);
    }
}
