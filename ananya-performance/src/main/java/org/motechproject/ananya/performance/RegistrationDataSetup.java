package org.motechproject.ananya.performance;

import org.apache.commons.lang.RandomStringUtils;
import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.service.RegistrationMeasureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

@Component
public class RegistrationDataSetup {

    private static final Logger log = LoggerFactory.getLogger(RegistrationDataSetup.class);

    private Properties dataSetupProperties;
    private RegistrationMeasureService registrationMeasureService;
    private CouchDbConnector ananyaDbConnector;

    private AllLocations allLocations;
    private AllFrontLineWorkers allFrontLineWorkers;
    private AllLocationDimensions allLocationDimensions;
    private AllRegistrationMeasures allRegistrationMeasures;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    private String[] predefinedLocationCodes = new String[]{"S01D002B001V001", "S01D002B001V007", "S01D003B001V010", "S01D003B001V012", "S01D003B001V022"};

    private List<Location> locations;

    private int[] locationSplit;

    private Integer numberOfFLW;

    private DateTime startDate;

    private DateTime endDate;

    private int batchSize;

    private ArrayList<FrontLineWorker> frontLineWorkerList;

    @Autowired
    public RegistrationDataSetup(AllLocations allLocations,
                                 @Qualifier("dataSetupProperties") Properties dataSetupProperties,
                                 @Qualifier("ananyaDbConnector") CouchDbConnector ananyaDbConnector,
                                 AllFrontLineWorkers allFrontLineWorkers,
                                 RegistrationMeasureService registrationMeasureService,
                                 AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                 AllLocationDimensions allLocationDimensions,
                                 AllRegistrationMeasures allRegistrationMeasures) {
        this.allLocations = allLocations;
        this.dataSetupProperties = dataSetupProperties;
        this.ananyaDbConnector = ananyaDbConnector;
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.registrationMeasureService = registrationMeasureService;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allLocationDimensions = allLocationDimensions;
        this.allRegistrationMeasures = allRegistrationMeasures;

        populateLocationCodes();
        setupProperties();
        frontLineWorkerList = new ArrayList<FrontLineWorker>();
    }

    public void loadRegistrationData() {

        log.info("Loading registration Data");

        log.info("Clearing FLW tables from Couch and Postgres");
        // TODO: abstract this out into an initial bootstrapper for performance data.
        allFrontLineWorkers.removeAll();
        allFrontLineWorkerDimensions.removeAll();
        allRegistrationMeasures.removeAll();

        for (int flwNumber = 0; flwNumber < numberOfFLW; flwNumber++) {

            Location location = getLocation();
            FrontLineWorker frontLineWorker = new FrontLineWorker(
                    RandomStringUtils.randomNumeric(10),
                    getDesignation(),
                    location.getId(), "");
            frontLineWorker.setRegisteredDate(getRegisteredDate());
            frontLineWorker.status(RegistrationStatus.REGISTERED);
            frontLineWorker.name(RandomStringUtils.randomAlphabetic(6)); // may randomize length of name
            frontLineWorkerList.add(frontLineWorker);

            if ((flwNumber + 1) % batchSize == 0) {
                log.info("Pushing workers into DB. Counter at " + flwNumber);
                pushWorkersIntoDB();
            }
        }

        log.info("Pushing remaining workers into db.");
        pushWorkersIntoDB();

        log.info("Done. Relax.");
    }

    private void pushWorkersIntoDB() {
        if (frontLineWorkerList == null || frontLineWorkerList.size() == 0) return;

        ananyaDbConnector.executeBulk(frontLineWorkerList);
        for (FrontLineWorker frontLineWorker : frontLineWorkerList) {
            registrationMeasureService.createRegistrationMeasureWith(frontLineWorker.getMsisdn());
        }
        frontLineWorkerList.clear();
    }

    private void setupProperties() {
        locationSplit = splitProbabilityString(dataSetupProperties.getProperty("location.split"));
        startDate = DateTime.parse(dataSetupProperties.getProperty("capture.start.date"));
        endDate = DateTime.parse(dataSetupProperties.getProperty("capture.end.date"));
        numberOfFLW = Integer.parseInt(dataSetupProperties.getProperty("number.of.flw"));
        batchSize = Integer.parseInt(dataSetupProperties.getProperty("batch.size"));
    }

    private void populateLocationCodes() {
        locations = new ArrayList<Location>();
        for (String code : predefinedLocationCodes) {
            locations.add(allLocations.findByExternalId(code));
        }
    }

    private DateTime getRegisteredDate() {
        return startDate.plusMillis(new Random().nextInt((int) endDate.minus(startDate.getMillis()).getMillis()));
    }

    private Location getLocation() {
        return locations.get(getProbabilityNumber(locationSplit));
    }

    private Designation getDesignation() {
        return Designation.ANM;
    }

    private int[] splitProbabilityString(String s) {
        String[] splits = s.split(",");
        int[] arr = new int[splits.length];
        int runningValue = 0;
        for (int i = 0; i < splits.length; i++) {
            arr[i] = Integer.parseInt(splits[i]) + runningValue;
            runningValue = arr[i];
        }
        return arr;
    }

    private int getProbabilityNumber(int[] probabilityArray) {
        Random random = new Random();
        int randomNumber = random.nextInt(10);

        for (int i = 0; i < probabilityArray.length; i++) {
            if (randomNumber <= probabilityArray[i])
                return i;
        }

        return 0;
    }
}

