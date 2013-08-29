package org.motechproject.ananya.service;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.mapper.FrontLineWorkerMapper;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.request.LocationSyncRequest;
import org.motechproject.ananya.response.FLWValidationResponse;
import org.motechproject.ananya.response.FrontLineWorkerResponse;
import org.motechproject.ananya.response.RegistrationResponse;
import org.motechproject.ananya.service.dimension.FrontLineWorkerDimensionService;
import org.motechproject.ananya.service.measure.CallDurationMeasureService;
import org.motechproject.ananya.service.measure.JobAidContentMeasureService;
import org.motechproject.ananya.service.measure.RegistrationMeasureService;
import org.motechproject.ananya.service.measure.SMSSentMeasureService;
import org.motechproject.ananya.validators.FrontLineWorkerValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang.StringUtils.isBlank;

@Service
public class FLWRegistrationService {
    private static Logger log = LoggerFactory.getLogger(FLWRegistrationService.class);

    private FrontLineWorkerService frontLineWorkerService;
    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;
    private CourseItemMeasureService courseItemMeasureService;
    private RegistrationMeasureService registrationMeasureService;
    private LocationService locationService;
    private JobAidContentMeasureService jobAidContentMeasureService;
    private CallDurationMeasureService callDurationMeasureService;
    private SMSSentMeasureService smsSentMeasureService;
    private LocationRegistrationService locationRegistrationService;

    public FLWRegistrationService() {
    }

    @Autowired
    public FLWRegistrationService(FrontLineWorkerService frontLineWorkerService,
                                  CourseItemMeasureService courseItemMeasureService,
                                  FrontLineWorkerDimensionService frontLineWorkerDimensionService,
                                  RegistrationMeasureService registrationMeasureService,
                                  LocationService locationService,
                                  JobAidContentMeasureService jobAidContentMeasureService,
                                  CallDurationMeasureService callDurationMeasureService,
                                  SMSSentMeasureService smsSentMeasureService,
                                  LocationRegistrationService locationRegistrationService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.courseItemMeasureService = courseItemMeasureService;
        this.frontLineWorkerDimensionService = frontLineWorkerDimensionService;
        this.registrationMeasureService = registrationMeasureService;
        this.locationService = locationService;
        this.jobAidContentMeasureService = jobAidContentMeasureService;
        this.callDurationMeasureService = callDurationMeasureService;
        this.smsSentMeasureService = smsSentMeasureService;
        this.locationRegistrationService = locationRegistrationService;
    }

    public List<RegistrationResponse> registerAllFLWs(List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        List<RegistrationResponse> registrationResponses = new ArrayList<>();
        for (FrontLineWorkerRequest frontLineWorkerRequest : frontLineWorkerRequests) {
            RegistrationResponse registrationResponse = registerFlw(trim(frontLineWorkerRequest));
            registrationResponses.add(registrationResponse);
        }
        return registrationResponses;
    }

    public RegistrationResponse createOrUpdateFLW(FrontLineWorkerRequest frontLineWorkerRequest) {
        return registerFlw(trim(frontLineWorkerRequest));
    }

    public List<FrontLineWorkerResponse> getFilteredFLW(Long msisdn, String name, String status, String designation, String operator, String circle, Date activityStartDate, Date activityEndDate) {
        List<FrontLineWorkerResponse> filteredFlws = new ArrayList<>();
        List<Long> allFilteredMsisdns = new ArrayList<>();
        if (activityStartDate != null && activityEndDate != null) {
            List<Long> filteredMsisdnsFromJobAid = jobAidContentMeasureService.getAllFrontLineWorkerMsisdnsBetween(activityStartDate, activityEndDate);
            List<Long> filteredMsisdnsFromCertificateCourse = courseItemMeasureService.getAllFrontLineWorkerMsisdnsBetween(activityStartDate, activityEndDate);
            allFilteredMsisdns = ListUtils.union(filteredMsisdnsFromJobAid, filteredMsisdnsFromCertificateCourse);
            if (allFilteredMsisdns.isEmpty())
                return filteredFlws;
        }

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = frontLineWorkerDimensionService.getFilteredFLW(allFilteredMsisdns, msisdn, name, status, designation, operator, circle);

        for (FrontLineWorkerDimension frontLineWorkerDimension : frontLineWorkerDimensions)
            filteredFlws.add(FrontLineWorkerMapper.mapFrom(frontLineWorkerDimension));

        return filteredFlws;
    }

    @Transactional
    private RegistrationResponse registerFlw(FrontLineWorkerRequest request) {
        RegistrationResponse registrationResponse = new RegistrationResponse();

        FLWValidationResponse FLWValidationResponse = FrontLineWorkerValidator.validate(request);
        if (FLWValidationResponse.isInValid()) {
            return registrationResponse.withValidationResponse(FLWValidationResponse);
        }

        Location location = getOrCreateLocation(request);

        String callerId = request.getMsisdn();
        UUID flwId = UUID.fromString(request.getFlwId());
        String alternateContactNumber = isBlank(request.getAlternateContactNumber()) ? null : request.getAlternateContactNumber();
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, alternateContactNumber, request.getName(),
                Designation.getFor(request.getDesignation()), location, request.getLanguage(),
                new DateTime(request.getLastModified()), flwId);
        frontLineWorker.setVerificationStatus(request.getVerificationStatusAsEnum());
        frontLineWorker = frontLineWorkerService.createOrUpdate(frontLineWorker, location);
        updateAllMeasures(frontLineWorker);
        processChangeMsisdn(request);

        log.info("Registered new FLW:" + callerId);
        return registrationResponse.withNewRegistrationDone();
    }

    private void processChangeMsisdn(FrontLineWorkerRequest request) {
        String newMsisdn = request.getNewMsisdn();
        if (isBlank(newMsisdn))
            return;
        frontLineWorkerService.changeMsisdn(request.getMsisdn(),newMsisdn);
        changeMsisdnInReportDB(request, newMsisdn);
    }

    private void changeMsisdnInReportDB(FrontLineWorkerRequest request, String newMsisdn) {
        FrontLineWorkerDimension toFlw = frontLineWorkerDimensionService.getFrontLineWorkerDimension(request.msisdn());
        FrontLineWorkerDimension fromFlw = frontLineWorkerDimensionService.getFrontLineWorkerDimension(Long.valueOf(newMsisdn));
        String operator = null;
        if (fromFlw != null) {
            operator = fromFlw.getOperator();
            courseItemMeasureService.transfer(fromFlw, toFlw);
            callDurationMeasureService.transfer(fromFlw, toFlw);
            jobAidContentMeasureService.transfer(fromFlw, toFlw);
            smsSentMeasureService.transfer(fromFlw, toFlw);
            registrationMeasureService.remove(fromFlw.getId());
            frontLineWorkerDimensionService.remove(fromFlw);
        }
        toFlw.setMsisdn(Long.valueOf(newMsisdn));
        toFlw.setOperator(operator);
        frontLineWorkerDimensionService.update(toFlw);
    }

    private Location getOrCreateLocation(FrontLineWorkerRequest frontLineWorkerRequest) {
        LocationRequest locationRequest = frontLineWorkerRequest.getLocation();
        if (locationRequest == null) return null;
        Location locationFromDb = locationService.findFor(locationRequest.getState(), locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
        if (locationFromDb == null) {
            LocationRequest request = new LocationRequest(locationRequest.getState(), locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
            locationRegistrationService.addOrUpdate(new LocationSyncRequest(request, request, LocationStatus.NOT_VERIFIED.name(), frontLineWorkerRequest.getLastModified()));
        }
        return locationService.findFor(locationRequest.getState(), locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
    }

    private FrontLineWorkerRequest trim(FrontLineWorkerRequest request) {
        LocationRequest locationRequest = request.getLocation();
        LocationRequest location = locationRequest != null ?
                new LocationRequest(StringUtils.trimToEmpty(locationRequest.getState()),
                        StringUtils.trimToEmpty(locationRequest.getDistrict()),
                        StringUtils.trimToEmpty(locationRequest.getBlock()),
                        StringUtils.trimToEmpty(locationRequest.getPanchayat()))
                : null;
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(StringUtils.trimToEmpty(request.getMsisdn()),
                request.getAlternateContactNumber(),
                StringUtils.trimToEmpty(request.getName()),
                StringUtils.trimToEmpty(request.getDesignation()),
                location,
                request.getLastModified(),
                StringUtils.trimToEmpty(request.getFlwId()),
                request.getVerificationStatus(),
                request.getLanguage(), request.getNewMsisdn());
        return frontLineWorkerRequest;
    }

    private void updateAllMeasures(FrontLineWorker frontLineWorker) {
        registrationMeasureService.createOrUpdateFor(frontLineWorker.getMsisdn());
        courseItemMeasureService.updateLocation(frontLineWorker.msisdn(), frontLineWorker.getLocationId());
        callDurationMeasureService.updateLocation(frontLineWorker.msisdn(), frontLineWorker.getLocationId());
        jobAidContentMeasureService.updateLocation(frontLineWorker.msisdn(), frontLineWorker.getLocationId());
        smsSentMeasureService.updateLocation(frontLineWorker.msisdn(), frontLineWorker.getLocationId());
    }
}
