package org.motechproject.ananya.seed.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.seed.domain.FrontLineWorkerList;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FrontLineWorkerSeedService {

    private static final Logger log = LoggerFactory.getLogger(FrontLineWorkerSeedService.class);

    private DataAccessTemplate template;
    private AllLocations allLocations;
    private AllFrontLineWorkers allFrontLineWorkers;
    private AllRegistrationMeasures allRegistrationMeasures;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllTimeDimensions allTimeDimensions;
    private AllLocationDimensions allLocationDimensions;

    public FrontLineWorkerSeedService() {
    }

    @Autowired
    public FrontLineWorkerSeedService(DataAccessTemplate template, AllFrontLineWorkers allFrontLineWorkers,
                                      AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                      AllRegistrationMeasures allRegistrationMeasures,
                                      AllLocations allLocations,
                                      AllTimeDimensions allTimeDimensions,
                                      AllLocationDimensions allLocationDimensions) {
        this.template = template;
        this.allLocations = allLocations;
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.allRegistrationMeasures = allRegistrationMeasures;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allTimeDimensions = allTimeDimensions;
        this.allLocationDimensions = allLocationDimensions;
    }


    public List<FrontLineWorker> allFrontLineWorkers() {
        return allFrontLineWorkers.getAll();
    }

    @Transactional
    public List<FrontLineWorkerDimension> allFrontLineWorkerDimensions() {
        return (List<FrontLineWorkerDimension>) template.find("select f from FrontLineWorkerDimension f");
    }

    @Transactional
    public void correctRegistrationStatusInCouchAndPostgres() {
        int lastSequenceOfPreImportedFLWs = 20988;
        template.bulkUpdate("update FrontLineWorkerDimension set status = '" + RegistrationStatus.UNREGISTERED + "' where id >= " + lastSequenceOfPreImportedFLWs);
        log.info("RegistrationStatus:postgres frontLineWorkerDimensions >= 20988 to unregistered status");

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = allFrontLineWorkerDimensions.getAllUnregistered();
        for (FrontLineWorkerDimension frontLineWorkerDimension : frontLineWorkerDimensions) {
            FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(frontLineWorkerDimension.getMsisdn().toString());
            if (frontLineWorker == null) {
                log.error("RegistrationStatus:db mismatch, couchdb missing : " + frontLineWorkerDimension.getMsisdn());
                continue;
            }
            frontLineWorker.setRegistrationStatus(RegistrationStatus.UNREGISTERED);
            allFrontLineWorkers.update(frontLineWorker);
            log.info("RegistrationStatus: updated couchdb doc to unregistered status for :" + frontLineWorker);
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
                log.info("Duplicates:merged couchdb docs from " + frontLineWorker + " to " + finalFrontLineWorker);

                FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(frontLineWorker.msisdn());
                FrontLineWorkerDimension finalFrontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(finalFrontLineWorker.msisdn());
                finalFrontLineWorkerDimension.merge(frontLineWorkerDimension);

                RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId());
                RegistrationMeasure finalRegistrationMeasure = allRegistrationMeasures.fetchFor(finalFrontLineWorkerDimension.getId());
                finalRegistrationMeasure.merge(registrationMeasure);
                allRegistrationMeasures.createOrUpdate(finalRegistrationMeasure);
                allRegistrationMeasures.remove(registrationMeasure);
                log.info("Duplicates:merged postgres measure from " + registrationMeasure + " to " + finalRegistrationMeasure);

                allFrontLineWorkerDimensions.update(finalFrontLineWorkerDimension);
                allFrontLineWorkerDimensions.remove(frontLineWorkerDimension);
                log.info("Duplicates:merged postgres dimensions from " + frontLineWorkerDimension + " to " + finalFrontLineWorkerDimension);

            } catch (Exception e) {
                log.error("Duplicates:exception while correcting duplicates for:" + msisdn + " " + ExceptionUtils.getFullStackTrace(e));
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
                log.info("Updated:correct msisdn, circle in couchdb for: " + frontLineWorker);

                FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(msisdn));
                if (frontLineWorkerDimension == null)
                    frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(Long.valueOf(correctedMsisdn));
                if (frontLineWorkerDimension == null) {
                    log.error("Updated:db mismatch, postgres missing : " + frontLineWorker.getMsisdn());
                    continue;
                }
                frontLineWorkerDimension.setOperator(frontLineWorker.getOperator());
                frontLineWorkerDimension.setDesignation(designation);
                frontLineWorkerDimension.setMsisdn(Long.valueOf(correctedMsisdn));
                frontLineWorkerDimension.setCircle(frontLineWorker.getCircle());
                allFrontLineWorkerDimensions.update(frontLineWorkerDimension);
                log.info("Updated: correct msisdn, circle in postgres dimension for: " + frontLineWorkerDimension);
            } catch (Exception e) {
                log.error("Updated: exception while updating:" + msisdn + " " + ExceptionUtils.getFullStackTrace(e));
            }
        }
    }

    @Transactional
    public void correctInvalidDesignationsForAnganwadi(String msisdn) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn("91" + msisdn);
        if (frontLineWorker == null) {
            log.error("Designation: missing flw in couchdb: " + msisdn);
            return;
        }
        frontLineWorker.setDesignation(Designation.AWW);
        if (StringUtils.isNotBlank(frontLineWorker.getName()))
            frontLineWorker.setRegistrationStatus(RegistrationStatus.REGISTERED);

        allFrontLineWorkers.update(frontLineWorker);
        log.info("Designation: corrected invalid designation in couchdb: " + frontLineWorker);

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(frontLineWorker.msisdn());
        if (frontLineWorkerDimension == null) {
            log.error("Designation: missing flw in postgres: " + msisdn);
            return;
        }
        frontLineWorkerDimension.setDesignation(frontLineWorker.designationName());
        frontLineWorkerDimension.setStatus(frontLineWorker.getStatus().toString());
        allFrontLineWorkerDimensions.update(frontLineWorkerDimension);
        log.info("Designation: corrected invalid designation in postgres: " + frontLineWorkerDimension);
    }

    @Transactional
    public void correctRegistrationStatus(FrontLineWorkerDimension frontLineWorkerDimension) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn("" + frontLineWorkerDimension.getMsisdn());
        if (frontLineWorker == null) {
            log.error("flw present in postgres but not in couch. msisdn - " + frontLineWorkerDimension.getMsisdn());
            return;
        }
        Location location = allLocations.findByExternalId(frontLineWorker.getLocationId());

        RegistrationStatus actualRegistrationStatus = frontLineWorker.getStatus();
        RegistrationStatus expectedRegistrationStatus = deduceRegistrationStatusOld(frontLineWorker, location);
        if (!frontLineWorkerDimension.statusIs(actualRegistrationStatus)) {
            log.error("postgres and couch out of sync! msisdn : " + frontLineWorkerDimension.getMsisdn() +
                    " status in postgres : " + frontLineWorkerDimension.getStatus() +
                    " status in couch : " + frontLineWorker.getStatus() +
                    " expected status : " + expectedRegistrationStatus);
            actualRegistrationStatus = null;
        }

        if (expectedRegistrationStatus != actualRegistrationStatus) {
            log.info("changing registration status of msisdn : " + frontLineWorkerDimension.getMsisdn() +
                    " status in postgres : " + frontLineWorkerDimension.getStatus() +
                    " status in couch : " + frontLineWorker.getStatus() +
                    " expected status : " + expectedRegistrationStatus);
            frontLineWorkerDimension.setStatus(expectedRegistrationStatus.toString());
            allFrontLineWorkerDimensions.update(frontLineWorkerDimension);
            frontLineWorker.setRegistrationStatus(expectedRegistrationStatus);
            allFrontLineWorkers.update(frontLineWorker);
        }
    }

    @Transactional
    public void activateNewRegistrationStatusForFLW(FrontLineWorker frontLineWorker) {
        boolean hasCalledSystem = StringUtils.isNotBlank(frontLineWorker.getOperator());
        RegistrationStatus newStatus = hasCalledSystem ?
                deduceRegistrationStatusNew(frontLineWorker, allLocations.findByExternalId(frontLineWorker.getLocationId())) :
                RegistrationStatus.UNREGISTERED;
        frontLineWorker.setRegistrationStatus(newStatus);
        allFrontLineWorkers.update(frontLineWorker);
        log.info("modified status in couch:" + frontLineWorker.getMsisdn() + "|" + frontLineWorker.getStatus() + "=>" + newStatus);

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(frontLineWorker.msisdn());
        if (frontLineWorkerDimension == null) return;
        frontLineWorkerDimension.setStatus(newStatus.toString());
        allFrontLineWorkerDimensions.update(frontLineWorkerDimension);
        log.info("modified status in postgres:" + frontLineWorker.getMsisdn() + "|" + frontLineWorker.getStatus() + "=>" + newStatus);
    }

    public RegistrationStatus deduceRegistrationStatusOld(FrontLineWorker frontLineWorker, Location location) {
        boolean locationAbsent = (Location.getDefaultLocation().equals(location));
        boolean locationIncomplete = location.isMissingDetails();
        boolean designationInvalid = Designation.isInValid(frontLineWorker.designationName());
        boolean nameInvalid = StringUtils.isBlank(frontLineWorker.getName());

        if (!(locationAbsent || locationIncomplete || designationInvalid || nameInvalid))
            return RegistrationStatus.REGISTERED;
        if (locationAbsent && designationInvalid && nameInvalid)
            return RegistrationStatus.UNREGISTERED;
        return RegistrationStatus.PARTIALLY_REGISTERED;
    }

    public RegistrationStatus deduceRegistrationStatusNew(FrontLineWorker frontLineWorker, Location location) {
        boolean locationAbsent = (Location.getDefaultLocation().equals(location));
        boolean locationIncomplete = location.isMissingDetails();
        boolean designationInvalid = Designation.isInValid(frontLineWorker.designationName());
        boolean nameInvalid = StringUtils.isBlank(frontLineWorker.getName());

        if (locationAbsent || locationIncomplete || designationInvalid || nameInvalid)
            return RegistrationStatus.PARTIALLY_REGISTERED;
        return RegistrationStatus.REGISTERED;
    }

    public void removeInvalidDesignation(FrontLineWorker frontLineWorker) {
        if (!"INVALID".equalsIgnoreCase(frontLineWorker.designationName())) return;

        frontLineWorker.setDesignation(null);
        allFrontLineWorkers.update(frontLineWorker);
        log.info("removed invalid designation in couchdb for " + frontLineWorker);

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(frontLineWorker.msisdn());
        if (frontLineWorkerDimension == null) return;
        frontLineWorkerDimension.setDesignation(null);
        allFrontLineWorkerDimensions.update(frontLineWorkerDimension);
        log.info("removed invalid designation in postgres for " + frontLineWorker);
    }

    public void correctDesignationBasedOnCSVFile(String msisdn, String designation) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        if (frontLineWorker == null) return;
        Designation expectedDesignation = Designation.getFor(designation);
        Designation actualDesignation = frontLineWorker.getDesignation();
        if (actualDesignation != expectedDesignation) {
            frontLineWorker.setDesignation(expectedDesignation);
            allFrontLineWorkers.update(frontLineWorker);
            log.info("modified designation in couch: " + msisdn + "|" + actualDesignation + "=>" + expectedDesignation);
        }

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(frontLineWorker.msisdn());
        if (frontLineWorkerDimension == null) return;
        String expectedDesignationString = (expectedDesignation == null) ? null : expectedDesignation.toString();
        String actualDesignationString = frontLineWorkerDimension.getDesignation();
        if (!StringUtils.equalsIgnoreCase(expectedDesignationString, actualDesignationString)) {
            frontLineWorkerDimension.setDesignation(expectedDesignationString);
            allFrontLineWorkerDimensions.update(frontLineWorkerDimension);
            log.info("modified designation in postgres: " + msisdn + "|" + actualDesignationString + "=>" + expectedDesignationString);
        }
    }

    public void createDimensionAndRegistrationMeasureFor(FrontLineWorker frontLineWorker) {
        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(frontLineWorker.msisdn());
        if (frontLineWorkerDimension != null) return;

        frontLineWorkerDimension = allFrontLineWorkerDimensions.createOrUpdate(
                frontLineWorker.msisdn(),
                frontLineWorker.getOperator(),
                frontLineWorker.getCircle(),
                frontLineWorker.name(),
                frontLineWorker.designationName(),
                frontLineWorker.getStatus().toString());
        log.info("frontlineWorkerDimension created for missing " + frontLineWorker);

        String callId = frontLineWorker.msisdn() + String.valueOf(DateUtil.now().getMillis());

        TimeDimension timeDimension = allTimeDimensions.getFor(frontLineWorker.getRegisteredDate());
        LocationDimension locationDimension = allLocationDimensions.getFor(frontLineWorker.getLocationId());

        RegistrationMeasure registrationMeasure = new RegistrationMeasure(
                frontLineWorkerDimension,
                locationDimension,
                timeDimension,
                callId);
        allRegistrationMeasures.createOrUpdate(registrationMeasure);
        log.info("registrationMeasure created for missing " + frontLineWorker);
    }

    public void doWithBatch(FrontLineWorkerExecutable executable, int batchSize) {
        String startKey = "";
        List<FrontLineWorker> frontLineWorkers;
        while (true) {
            frontLineWorkers = allFrontLineWorkers.getMsisdnsFrom(startKey, batchSize);
            if (frontLineWorkers.size() < batchSize) break;
            int size = frontLineWorkers.size() - 1;
            loopAndDo(executable, frontLineWorkers.subList(0, size));
            startKey = frontLineWorkers.get(size).getMsisdn();
            //required to work around socket timeout
            allFrontLineWorkers.findByMsisdn(startKey);
        }
        loopAndDo(executable, frontLineWorkers);
    }

    private void loopAndDo(FrontLineWorkerExecutable executable, List<FrontLineWorker> frontLineWorkers) {
        for (FrontLineWorker frontLineWorker : frontLineWorkers) {
            try {
                executable.execute(frontLineWorker);
            } catch (Exception e) {
                log.error("error modifying flw:" + frontLineWorker.getMsisdn(), e);
            }
        }
    }
}