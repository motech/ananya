package org.motechproject.ananya.seed.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.seed.domain.FrontLineWorkerList;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class    FrontLineWorkerSeedService {

    private static final Logger log = LoggerFactory.getLogger(FrontLineWorkerSeedService.class);

    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllFrontLineWorkers allFrontLineWorkers;
    private DataAccessTemplate template;
    private AllRegistrationMeasures allRegistrationMeasures;
    private AllLocations allLocations;
    private FrontLineWorkerService frontLineWorkerService;

    public FrontLineWorkerSeedService() {

    }

    @Autowired
    public FrontLineWorkerSeedService(AllFrontLineWorkers allFrontLineWorkers,
                                      AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                      DataAccessTemplate template, AllRegistrationMeasures allRegistrationMeasures, AllLocations allLocations, FrontLineWorkerService frontLineWorkerService) {
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.template = template;
        this.allRegistrationMeasures = allRegistrationMeasures;
        this.allLocations = allLocations;
        this.frontLineWorkerService = frontLineWorkerService;
    }


    @Transactional
    public void correctRegistrationStatusInCouchAndPostgres() {
        int lastSequenceOfPreImportedFLWs = 20988;
        template.bulkUpdate("update FrontLineWorkerDimension set status = '" + RegistrationStatus.UNREGISTERED + "' where id >= " + lastSequenceOfPreImportedFLWs);
        log.info("RegistrationStatus:Postgres FrontLineWorkerDimensions >= 20988 to unregistered status");

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = allFrontLineWorkerDimensions.getAllUnregistered();
        for (FrontLineWorkerDimension frontLineWorkerDimension : frontLineWorkerDimensions) {
            FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(frontLineWorkerDimension.getMsisdn().toString());
            if (frontLineWorker == null) {
                log.error("RegistrationStatus: Db mismatch, Couchdb missing : " + frontLineWorkerDimension.getMsisdn());
                continue;
            }
            frontLineWorker.setRegistrationStatus(RegistrationStatus.UNREGISTERED);
            allFrontLineWorkers.update(frontLineWorker);
            log.info("RegistrationStatus: Updated Couchdb doc to unregistered status for :" + frontLineWorker);
        }
    }

    @Transactional
    public void correctDuplicatesInCouchAndPostgres(List<FrontLineWorker> frontLineWorkers) {
        FrontLineWorkerList frontLineWorkerList = new FrontLineWorkerList(frontLineWorkers);

        for (FrontLineWorker frontLineWorker : frontLineWorkers) {
            String msisdn = frontLineWorker.getMsisdn();
            if (msisdn.length() > 10) continue;
            try {
                FrontLineWorker finalFrontLineWorker = frontLineWorkerList.findLongCodeDuplicate(msisdn);
                if (finalFrontLineWorker == null) continue;

                finalFrontLineWorker.merge(frontLineWorker);
                allFrontLineWorkers.update(finalFrontLineWorker);
                allFrontLineWorkers.remove(frontLineWorker);
                log.info("Duplicates: Merged Couchdb docs from : " + frontLineWorker + " to final version : " + finalFrontLineWorker);

                FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(frontLineWorker.msisdn());
                FrontLineWorkerDimension finalFrontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(finalFrontLineWorker.msisdn());
                finalFrontLineWorkerDimension.merge(frontLineWorkerDimension);

                RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
                RegistrationMeasure finalRegistrationMeasure = allRegistrationMeasures.fetchFor(finalFrontLineWorkerDimension.getId());
                finalRegistrationMeasure.merge(registrationMeasure);
                allRegistrationMeasures.createOrUpdate(finalRegistrationMeasure);
                allRegistrationMeasures.remove(registrationMeasure);
                log.info("Duplicates: Merged Postgres measure from : " + registrationMeasure + " to final version : " + finalRegistrationMeasure);

                allFrontLineWorkerDimensions.update(finalFrontLineWorkerDimension);
                allFrontLineWorkerDimensions.remove(frontLineWorkerDimension);
                log.info("Duplicates: Merged Postgres dimensions from : " + frontLineWorkerDimension + " to final version : " + finalFrontLineWorkerDimension);

            } catch (Exception e) {
                log.error("Duplicates: Exception while correcting duplicates for:" + msisdn + " " + ExceptionUtils.getFullStackTrace(e));
            }
        }
    }

    @Transactional
    public void updateOperatorDesignationCircleAndCorrectMsisdnInPostgresAndCouchDb(List<FrontLineWorker> frontLineWorkers, String defaultCircle) {
        for (FrontLineWorker frontLineWorker : frontLineWorkers) {
            String msisdn = frontLineWorker.getMsisdn();
            try {
                String designation = frontLineWorker.getDesignation() == null ? "" : frontLineWorker.getDesignation().toString();
                String correctedMsisdn = msisdn.length() == 10 ? "91" + msisdn : msisdn;

                frontLineWorker.setMsisdn(correctedMsisdn);
                frontLineWorker.setCircle(defaultCircle);
                allFrontLineWorkers.update(frontLineWorker);
                log.info("Updated: Correct msisdn, circle in Couchdb for: " + frontLineWorker);

                FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(msisdn));
                if (frontLineWorkerDimension == null)
                    frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(correctedMsisdn));
                if (frontLineWorkerDimension == null) {
                    log.error("Updated: Db mismatch, Postgres missing : " + frontLineWorker.getMsisdn());
                    continue;
                }
                frontLineWorkerDimension.setOperator(frontLineWorker.getOperator());
                frontLineWorkerDimension.setDesignation(designation);
                frontLineWorkerDimension.setMsisdn(Long.valueOf(correctedMsisdn));
                frontLineWorkerDimension.setCircle(frontLineWorker.getCircle());
                allFrontLineWorkerDimensions.update(frontLineWorkerDimension);
                log.info("Updated: Correct msisdn, circle in Postgres dimension for: " + frontLineWorkerDimension);
            } catch (Exception e) {
                log.error("Updated: Exception while updating:" + msisdn + " " + ExceptionUtils.getFullStackTrace(e));
            }
        }
    }

    public List<FrontLineWorker> getAllFromCouchDb() {
        return allFrontLineWorkers.getAll();
    }

    @Transactional
    public void correctInvalidDesignationsForAnganwadi(String msisdn) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn("91" + msisdn);
        if (frontLineWorker == null) {
            log.error("Designation: Missing FrontlineWorker in couchdb: " + msisdn);
            return;
        }
        frontLineWorker.setDesignation(Designation.AWW);
        if (StringUtils.isNotBlank(frontLineWorker.getName()))
            frontLineWorker.setRegistrationStatus(RegistrationStatus.REGISTERED);

        allFrontLineWorkers.update(frontLineWorker);
        log.info("Designation: Corrected invalid designation in couchdb: " + frontLineWorker);

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(frontLineWorker.msisdn());
        if (frontLineWorkerDimension == null) {
            log.error("Designation: Missing FrontlineWorker in postgres: " + msisdn);
            return;
        }
        frontLineWorkerDimension.setDesignation(frontLineWorker.designationName());
        frontLineWorkerDimension.setStatus(frontLineWorker.getStatus().toString());
        allFrontLineWorkerDimensions.update(frontLineWorkerDimension);
        log.info("Designation: Corrected invalid designation in postgres: " + frontLineWorkerDimension);
    }

    @Transactional
    public List<FrontLineWorkerDimension> getFrontLineWorkers() {
        return (List<FrontLineWorkerDimension>)template.find("select f from FrontLineWorkerDimension f");
    }

    @Transactional
    public void correctRegistrationStatus(FrontLineWorkerDimension frontLineWorkerDimension) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn("" + frontLineWorkerDimension.getMsisdn());
        if (frontLineWorker == null) {
            System.out.println("FATAL: user present in postgres but not in couch. msisdn - " + frontLineWorkerDimension.getMsisdn());
            return;
        }
        Location location = allLocations.findByExternalId(frontLineWorker.getLocationId());

        RegistrationStatus actualRegistrationStatus = frontLineWorker.getStatus();
        RegistrationStatus expectedRegistrationStatus = frontLineWorkerService.deduceRegistrationStatus(frontLineWorker, location);
        if (!frontLineWorkerDimension.statusIs(actualRegistrationStatus)) {
            System.out.println("FATAL: postgres and couch out of sync! msisdn : " + frontLineWorkerDimension.getMsisdn() +
                    " status in postgres : " + frontLineWorkerDimension.getStatus() +
                    " status in couch : " + frontLineWorker.getStatus() +
                    " expected status : " + expectedRegistrationStatus);
            // to ensure both couch and postgres get updated.
            actualRegistrationStatus = null;
        }

        if (expectedRegistrationStatus != actualRegistrationStatus) {
            System.out.println("Changing registration status of msisdn : " + frontLineWorkerDimension.getMsisdn() +
                    " status in postgres : " + frontLineWorkerDimension.getStatus() +
                    " status in couch : " + frontLineWorker.getStatus() +
                    " expected status : " + expectedRegistrationStatus);
            frontLineWorkerDimension.setStatus(expectedRegistrationStatus.toString());
            allFrontLineWorkerDimensions.update(frontLineWorkerDimension);
            frontLineWorker.setRegistrationStatus(expectedRegistrationStatus);
            allFrontLineWorkers.update(frontLineWorker);
        }
    }

}