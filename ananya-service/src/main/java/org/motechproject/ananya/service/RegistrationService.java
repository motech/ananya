package org.motechproject.ananya.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.requests.FLWStatusChangeRequest;
import org.motechproject.ananya.service.dimension.FrontLineWorkerDimensionService;
import org.motechproject.ananya.service.measure.CallDurationMeasureService;
import org.motechproject.ananya.service.measure.JobAidContentMeasureService;
import org.motechproject.ananya.service.measure.RegistrationMeasureService;
import org.motechproject.ananya.service.measure.SMSSentMeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistrationService {
    private RegistrationMeasureService registrationMeasureService;
    private CourseItemMeasureService courseItemMeasureService;
    private CallDurationMeasureService callDurationMeasureService;
    private JobAidContentMeasureService jobAidContentMeasureService;
    private SMSSentMeasureService smsSentMeasureService;
    private FrontLineWorkerService frontLineWorkerService;
    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;

    public RegistrationService() {
    }

    @Autowired
    public RegistrationService(RegistrationMeasureService registrationMeasureService,
                               CourseItemMeasureService courseItemMeasureService,
                               CallDurationMeasureService callDurationMeasureService,
                               JobAidContentMeasureService jobAidContentMeasureService,
                               SMSSentMeasureService smsSentMeasureService,
                               FrontLineWorkerService frontLineWorkerService,
                               FrontLineWorkerDimensionService frontLineWorkerDimensionService) {
        this.registrationMeasureService = registrationMeasureService;
        this.courseItemMeasureService = courseItemMeasureService;
        this.callDurationMeasureService = callDurationMeasureService;
        this.jobAidContentMeasureService = jobAidContentMeasureService;
        this.smsSentMeasureService = smsSentMeasureService;
        this.frontLineWorkerService = frontLineWorkerService;
        this.frontLineWorkerDimensionService = frontLineWorkerDimensionService;
    }

    @Transactional
    public void updateAllLocationReferences(String oldLocationCode, String newLocationCode) {
        registrationMeasureService.updateLocation(oldLocationCode, newLocationCode);
        courseItemMeasureService.updateLocation(oldLocationCode, newLocationCode);
        callDurationMeasureService.updateLocation(oldLocationCode, newLocationCode);
        jobAidContentMeasureService.updateLocation(oldLocationCode, newLocationCode);
        smsSentMeasureService.updateLocation(oldLocationCode, newLocationCode);
    }


    public void updateLocationOnFLW(Location oldLocation, Location newLocation) {
        List<FrontLineWorker> frontLineWorkers = frontLineWorkerService.updateLocation(oldLocation, newLocation);
        List<FLWStatusChangeRequest> flwStatusChangeRequests = (List<FLWStatusChangeRequest>) CollectionUtils.collect(frontLineWorkers, new Transformer() {
            @Override
            public Object transform(Object input) {
                FrontLineWorker frontLineWorker = (FrontLineWorker) input;
                return new FLWStatusChangeRequest(frontLineWorker.msisdn(), frontLineWorker.getStatus().name());
            }
        });
        frontLineWorkerDimensionService.updateStatus(flwStatusChangeRequests);
    }
}
