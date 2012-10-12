package org.motechproject.ananya.service;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.mapper.FrontLineWorkerMapper;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
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

@Service
public class RegistrationService {
    private static Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private FrontLineWorkerService frontLineWorkerService;
    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;
    private CourseItemMeasureService courseItemMeasureService;
    private RegistrationMeasureService registrationMeasureService;
    private LocationService locationService;
    private JobAidContentMeasureService jobAidContentMeasureService;
    private CallDurationMeasureService callDurationMeasureService;
    private SMSSentMeasureService smsSentMeasureService;

    public RegistrationService() {
    }

    @Autowired
    public RegistrationService(FrontLineWorkerService frontLineWorkerService,
                               CourseItemMeasureService courseItemMeasureService,
                               FrontLineWorkerDimensionService frontLineWorkerDimensionService,
                               RegistrationMeasureService registrationMeasureService,
                               LocationService locationService,
                               JobAidContentMeasureService jobAidContentMeasureService,
                               CallDurationMeasureService callDurationMeasureService,
                               SMSSentMeasureService smsSentMeasureService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.courseItemMeasureService = courseItemMeasureService;
        this.frontLineWorkerDimensionService = frontLineWorkerDimensionService;
        this.registrationMeasureService = registrationMeasureService;
        this.locationService = locationService;
        this.jobAidContentMeasureService = jobAidContentMeasureService;
        this.callDurationMeasureService = callDurationMeasureService;
        this.smsSentMeasureService = smsSentMeasureService;
    }

    public List<RegistrationResponse> registerAllFLWs(List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        List<RegistrationResponse> registrationResponses = new ArrayList<RegistrationResponse>();
        for (FrontLineWorkerRequest frontLineWorkerRequest : frontLineWorkerRequests) {
            RegistrationResponse registrationResponse = registerFlw(StringUtils.trimToEmpty(frontLineWorkerRequest.getMsisdn()),
                    StringUtils.trimToEmpty(frontLineWorkerRequest.getName()),
                    StringUtils.trimToEmpty(frontLineWorkerRequest.getDesignation()),
                    StringUtils.trimToEmpty(frontLineWorkerRequest.getLocation().getDistrict()),
                    StringUtils.trimToEmpty(frontLineWorkerRequest.getLocation().getBlock()),
                    StringUtils.trimToEmpty(frontLineWorkerRequest.getLocation().getPanchayat()),
                    new DateTime(frontLineWorkerRequest.getLastModified()),
                    StringUtils.trimToEmpty(frontLineWorkerRequest.getFlwGuid()));
            registrationResponses.add(registrationResponse);
        }
        return registrationResponses;
    }

    public RegistrationResponse createOrUpdateFLW(FrontLineWorkerRequest frontLineWorkerRequest) {
        return registerFlw(StringUtils.trimToEmpty(frontLineWorkerRequest.getMsisdn()),
                StringUtils.trimToEmpty(frontLineWorkerRequest.getName()),
                StringUtils.trimToEmpty(frontLineWorkerRequest.getDesignation()),
                StringUtils.trimToEmpty(frontLineWorkerRequest.getLocation().getDistrict()),
                StringUtils.trimToEmpty(frontLineWorkerRequest.getLocation().getBlock()),
                StringUtils.trimToEmpty(frontLineWorkerRequest.getLocation().getPanchayat()),
                new DateTime(frontLineWorkerRequest.getLastModified()),
                StringUtils.trimToEmpty(frontLineWorkerRequest.getFlwGuid()));
    }

    public List<FrontLineWorkerResponse> getFilteredFLW(Long msisdn, String name, String status, String designation, String operator, String circle, Date activityStartDate, Date activityEndDate) {
        List<FrontLineWorkerResponse> filteredFlws = new ArrayList<FrontLineWorkerResponse>();
        List<Long> allFilteredMsisdns = new ArrayList<Long>();
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
    private RegistrationResponse registerFlw(String callerId, String name, String designation, String district, String block, String panchayat, DateTime lastModified, String flwGuid) {
        Location location = locationService.findFor(district, block, panchayat);
        RegistrationResponse registrationResponse = new RegistrationResponse();
        FrontLineWorkerValidator frontLineWorkerValidator = new FrontLineWorkerValidator();
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, name, Designation.getFor(designation), location, lastModified, flwGuid);

        FLWValidationResponse FLWValidationResponse = frontLineWorkerValidator.validate(frontLineWorker, location);
        if (FLWValidationResponse.isInValid())
            return registrationResponse.withValidationResponse(FLWValidationResponse);

        frontLineWorker = frontLineWorkerService.createOrUpdate(frontLineWorker, location);
        updateAllMeasures(frontLineWorker);

        log.info("Registered new FLW:" + callerId);
        return registrationResponse.withNewRegistrationDone();
    }

    private void updateAllMeasures(FrontLineWorker frontLineWorker) {
        registrationMeasureService.createOrUpdateFor(frontLineWorker.getMsisdn());
        courseItemMeasureService.updateLocation(frontLineWorker.msisdn(), frontLineWorker.getLocationId());
        callDurationMeasureService.updateLocation(frontLineWorker.msisdn(), frontLineWorker.getLocationId());
        jobAidContentMeasureService.updateLocation(frontLineWorker.msisdn(), frontLineWorker.getLocationId());
        smsSentMeasureService.updateLocation(frontLineWorker.msisdn(), frontLineWorker.getLocationId());
    }
}
