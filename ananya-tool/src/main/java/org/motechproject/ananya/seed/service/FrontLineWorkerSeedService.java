package org.motechproject.ananya.seed.service;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FrontLineWorkerSeedService {

    private static final Logger log = LoggerFactory.getLogger(FrontLineWorkerSeedService.class);

    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllFrontLineWorkers allFrontLineWorkers;
    private DataAccessTemplate template;

    public FrontLineWorkerSeedService() {
    }

    @Autowired
    public FrontLineWorkerSeedService(AllFrontLineWorkers allFrontLineWorkers,
                                      AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                      DataAccessTemplate template) {
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.template = template;
    }

    public void updateDefaultCircleAndCorrectMsisdnInCouchDb(List<FrontLineWorker> frontLineWorkers, String defaultCircle) {
        log.info("Updating frontlineWorkers in couchdb: START");
        for (FrontLineWorker frontLineWorker : frontLineWorkers) {

            String msisdn = frontLineWorker.getMsisdn();
            if (msisdn.length() == 10)
                msisdn = "91" + msisdn;

            frontLineWorker.setMsisdn(msisdn);
            frontLineWorker.setCircle(defaultCircle);
            allFrontLineWorkers.update(frontLineWorker);
            log.info("Updated Couchdb: " + frontLineWorker + "with circle: " + frontLineWorker.getCircle());
        }
        log.info("Updating frontlineWorkers in couchdb: END");
    }

    @Transactional
    public void updateOperatorDesignationCircleAndCorrectMsisdnInPostgresBasedOnCouchDb(List<FrontLineWorker> frontLineWorkers) {
        log.info("Updating frontlineWorkers in postgres: START");
        for (FrontLineWorker frontLineWorker : frontLineWorkers) {
            String designation = frontLineWorker.getDesignation() == null ? "" : frontLineWorker.getDesignation().toString();
            String msisdn = frontLineWorker.getMsisdn();
            if (msisdn.length() <= 10)
                msisdn = "91" + msisdn;

            FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(frontLineWorker.msisdn());
            if (frontLineWorkerDimension == null) {
                log.error("Db mismatch, postgres missing : " + frontLineWorker.getMsisdn());
                continue;
            }
            frontLineWorkerDimension.setOperator(frontLineWorker.getOperator());
            frontLineWorkerDimension.setDesignation(designation);
            frontLineWorkerDimension.setMsisdn(Long.valueOf(msisdn));
            frontLineWorkerDimension.setCircle(frontLineWorker.getCircle());

            allFrontLineWorkerDimensions.update(frontLineWorkerDimension);
            log.info("Updated Postgres: " + frontLineWorkerDimension.getMsisdn() + "with operator : " +
                    frontLineWorker.getOperator() + " and circle : " + frontLineWorkerDimension.getCircle());
        }
        log.info("Updating frontlineWorkers in postgres: END");
    }


    @Transactional
    public void updateUnRegisteredStatusGreaterTheGivenIDInPostgres(int id) {
        String status = RegistrationStatus.UNREGISTERED.toString();
        template.bulkUpdate("update FrontLineWorkerDimension set status = '" + status + "' where id >= " + id);
    }

    @Transactional
    public void updateUnRegisteredStatusInCouchDbBasedOnPostgres() {
        List<FrontLineWorkerDimension> frontLineWorkerDimensions = allFrontLineWorkerDimensions.getAllUnregistered();
        for (FrontLineWorkerDimension frontLineWorkerDimension : frontLineWorkerDimensions) {
            FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(frontLineWorkerDimension.getMsisdn().toString());
            if (frontLineWorker == null) {
                log.error("Db mismatch, couchdb missing : " + frontLineWorkerDimension.getMsisdn());
                continue;
            }
            frontLineWorker.setRegistrationStatus(RegistrationStatus.UNREGISTERED);
            allFrontLineWorkers.update(frontLineWorker);
        }
    }

    public List<FrontLineWorker> getAllFromCouchDb() {
        return allFrontLineWorkers.getAll();
    }
}
