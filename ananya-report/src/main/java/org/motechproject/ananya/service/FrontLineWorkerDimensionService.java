package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FrontLineWorkerDimensionService {

    private static final Logger log = LoggerFactory.getLogger(CourseItemMeasureService.class);

    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    public FrontLineWorkerDimensionService() {
    }

    @Autowired
    public FrontLineWorkerDimensionService(AllFrontLineWorkerDimensions allFrontLineWorkerDimensions) {
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
    }

    @Transactional
    public List<FrontLineWorkerDimension> getAllUnregistered() {
        return allFrontLineWorkerDimensions.getAllUnregistered();
    }

    @Transactional
    public void updateRegistrationStatus(String status, int id) {
        allFrontLineWorkerDimensions.updateStatus(status, id);
    }

    @Transactional
    public void updateFrontLineWorkers(List<FrontLineWorker> allFrontLineWorkers) {
        log.info("Updating frontlineworkers in postgres with the right operators from couchdb.");
        for (FrontLineWorker frontLineWorker : allFrontLineWorkers) {
            String operator = frontLineWorker.getOperator();
            String designation = frontLineWorker.getDesignation() == null ? "" : frontLineWorker.getDesignation().toString();
            String msisdn = frontLineWorker.getMsisdn();
            if (msisdn.length() == 10) msisdn = "91" + msisdn;

            FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(frontLineWorker.msisdn());
            frontLineWorkerDimension.setOperator(operator);
            frontLineWorkerDimension.setDesignation(designation);
            frontLineWorkerDimension.setMsisdn(Long.valueOf(msisdn));

            allFrontLineWorkerDimensions.update(frontLineWorkerDimension);
            log.info("Updated operator for frontlineworker : " + frontLineWorker.getMsisdn() + "with operator : " + frontLineWorker.getOperator());
        }
    }

    public boolean exists(Long msisdn) {
        return allFrontLineWorkerDimensions.fetchFor(msisdn) != null;
    }

    @Transactional
    public FrontLineWorkerDimension createOrUpdate(Long msisdn, String operator, String circle, String name, String designation, String registrationStatus) {
        return allFrontLineWorkerDimensions.createOrUpdate(msisdn, operator, circle, name, designation, registrationStatus);
    }
}
