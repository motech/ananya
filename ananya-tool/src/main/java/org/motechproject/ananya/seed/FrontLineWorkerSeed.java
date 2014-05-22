package org.motechproject.ananya.seed;

import liquibase.util.csv.CSVReader;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.RegistrationResponse;
import org.motechproject.ananya.seed.service.FrontLineWorkerExecutable;
import org.motechproject.ananya.seed.service.FrontLineWorkerSeedService;
import org.motechproject.ananya.service.FLWRegistrationService;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.dimension.FrontLineWorkerDimensionService;
import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class FrontLineWorkerSeed {

    private static final Logger log = LoggerFactory.getLogger(FrontLineWorkerSeed.class);
    @Autowired
    private FLWRegistrationService flwRegistrationService;
    @Autowired
    private FrontLineWorkerService frontLineWorkerService;
    @Autowired
    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;
    @Autowired
    private FrontLineWorkerSeedService seedService;

    @Value("#{ananyaProperties['seed.flw.file']}")
    private String inputFileName;
    @Value("#{ananyaProperties['seed.flw.file.out']}")
    private String outputFileName;
    @Value("#{ananyaProperties['environment']}")
    private String environment;
    private BufferedWriter writer;
    private String DUMMY_UUID = "11111111-1111-1111-1111-111111111111";

    @Seed(priority = 0, version = "1.0", comment = "FLWs pre-registration via CSV, 20988 nos [P+C]")
    public void createFrontlineWorkersFromCSVFile() throws IOException {
        String[] row;
        String inputCSV = getInputCSV();
        String outputCSV = getOutputCSV(new File(inputCSV).getParent());
        String msisdn, name, designation, state, district, block, panchayat, language;

        File file = new File(outputCSV);
        file.createNewFile();

        writer = new BufferedWriter(new FileWriter(outputCSV));

        loadFromCsv(inputCSV);
    }

    /*
    * Users registered with id < 20988 were imported via a CSV file and have proper registration status. Users
    * after that were defaulted to PARTIALLY_REGISTERED, but need to be updated to UNREGISTERED. This needs to happen
    * in both couch and postgres.
    */
    public void loadFromCsv(String inputCSVFile) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(inputCSVFile));
        String msisdn, name, designation, currentState, currentDistrict, currentBlock, currentPanchayat, flwId, language;
        String circle = null;
        String[] currentRow;
        DateTime lastModified = DateUtil.now();

        //skip header
        csvReader.readNext();
        currentRow = csvReader.readNext();

        List<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        while (currentRow != null) {
            msisdn = currentRow[0];
            name = currentRow[1];
            designation = currentRow[2];
            currentState = currentRow[3];
            currentDistrict = currentRow[4];
            currentBlock = currentRow[5];
            currentPanchayat = currentRow[6];
            language =  currentRow[7];
            flwId = getFlwId(currentRow);

            frontLineWorkerRequests.add(new FrontLineWorkerRequest(msisdn,
                    null, name,
                    designation,
                    new LocationRequest(currentState, currentDistrict, currentBlock, currentPanchayat),
                    lastModified, flwId, null, language, null));

            currentRow = csvReader.readNext();
        }
        List<RegistrationResponse> registrationResponses = flwRegistrationService.registerAllFLWs(frontLineWorkerRequests);
        logResponses(registrationResponses);
    }

    private String getFlwId(String[] currentRow) {
        return currentRow.length > 8 && StringUtils.isNotEmpty(currentRow[8]) ? currentRow[8] : DUMMY_UUID;
    }

    private void logResponses(List<RegistrationResponse> responses) throws IOException {
        for (RegistrationResponse response : responses) {
            writer.write(response.toString());
            writer.newLine();
        }
        writer.close();
    }

    @Seed(priority = 7, version = "1.1", comment = "FLWs registered via calls should be now 'unregistered' status. [P+C]")
    public void updateRegistrationStatusOfFrontLineWorkersRegisteredViaCalls() {
        seedService.correctRegistrationStatusInCouchAndPostgres();
    }

    @Seed(priority = 6, version = "1.1", comment = "1) merging duplicates")
    public void updateCorrectCallerIdsCircleOperatorAndDesignation() {
        List<FrontLineWorker> allFrontLineWorkers = seedService.allFrontLineWorkers();
        seedService.correctDuplicatesInCouchAndPostgres(allFrontLineWorkers);
    }

    @Seed(priority = 5, version = "1.1", comment = "1) Appending 91 to callerIds [P+C], 2) Update missing designation, operator [P], 3) Add default circle [C] ")
    public void updateOperatorDesignationCircleAndCorrectMsisdnInPostgresAndCouchDb() {
        String defaultCircle = "bihar";
        List<FrontLineWorker> allFrontLineWorkers = seedService.allFrontLineWorkers();
        seedService.updateOperatorDesignationCircleAndCorrectMsisdnInPostgresAndCouchDb(allFrontLineWorkers, defaultCircle);
    }

    @Seed(priority = 4, version = "1.3", comment = "Correction of invalid status for AWW [P+C]. If getting rid of designation from couchdb, updated only the postgres entries.")
    public void correctInvalidDesignationsForAnganwadi() throws IOException {
        String inputCSV = getInputCSV();
        String outputCSV = getOutputCSV(new File(inputCSV).getParent());

        File file = new File(outputCSV);
        file.createNewFile();
        CSVReader csvReader = new CSVReader(new FileReader(inputCSV));

        String msisdn, designation;
        String[] currentRow;
        csvReader.readNext();
        currentRow = csvReader.readNext();

        while (currentRow != null) {
            msisdn = currentRow[0];
            designation = currentRow[2];
            if ("AWW".equalsIgnoreCase(designation))
                seedService.correctInvalidDesignationsForAnganwadi(msisdn);
            currentRow = csvReader.readNext();
        }
    }

    @Seed(priority = 0, version = "1.3", comment = "Clean-up of registration status of FLWs")
    public void correctInvalidRegistrationStatusForAllFLWs() {
        int count = 0;
        int logBreak = 100;
        List<FrontLineWorkerDimension> frontLineWorkerDimensions = seedService.allFrontLineWorkerDimensions();
        for (FrontLineWorkerDimension frontLineWorkerDimension : frontLineWorkerDimensions) {
            count++;
            seedService.correctRegistrationStatus(frontLineWorkerDimension);
            if (count % logBreak == 0)
                log.info("completed " + count + " of " + frontLineWorkerDimensions.size() + " FLWs");
        }
    }

    @Seed(priority = 1, version = "1.7", comment = "Remove invalid designations")
    public void correctAllDesignations() throws IOException {
        String inputCSVFile = getInputCSV();
        String msisdn, designation;
        String[] row;
        int batchSize = 100;

        CSVReader csvReader = new CSVReader(new FileReader(inputCSVFile));
        csvReader.readNext();
        row = csvReader.readNext();

        int count = 0;
        while (row != null) {
            count++;
            msisdn = StringUtils.trim(row[0]);
            designation = StringUtils.trim(row[2]);
            if (msisdn.length() == 10) msisdn = "91" + msisdn;
            try {
                seedService.correctDesignationBasedOnCSVFile(msisdn, designation);
            } catch (Exception e) {
                log.error("error while correcting designation for: " + msisdn, e);
            }
            row = csvReader.readNext();
            if (count % batchSize == 0)
                log.info("corrected designation for " + count + " users");
        }

        seedService.doWithBatch(new FrontLineWorkerExecutable() {
            @Override
            public void execute(FrontLineWorker frontLineWorker) {
                seedService.removeInvalidDesignation(frontLineWorker);
            }
        }, batchSize);
    }

    @Seed(priority = 0, version = "1.7", comment = "Changing registration status of FLWs based on new definition")
    public void activateNewRegistrationStatusesForAllFLWs() {
        int batchSize = 100;
        seedService.doWithBatch(new FrontLineWorkerExecutable() {
            @Override
            public void execute(FrontLineWorker frontLineWorker) {
                seedService.activateNewRegistrationStatusForFLW(frontLineWorker);
            }
        }, batchSize);
    }

    @Seed(priority = 2, version = "1.8", comment = "Removing conflicted FrontLineWorkers")
    public void removeConflictedFLWs() {
        seedService.doWithBatch(new FrontLineWorkerExecutable() {
            @Override
            public void execute(FrontLineWorker frontLineWorker) {
                seedService.mergeAndUpdateConflictedFLWs(frontLineWorker);
            }
        }, 100);
    }

    @Seed(priority = 1, version = "1.8", comment = "Removing duplicate FrontLineWorkers")
    public void removeDuplicateFLWs() {
        seedService.doWithBatch(new FrontLineWorkerExecutable() {
            @Override
            public void execute(FrontLineWorker frontLineWorker) {
                seedService.mergeAndRemoveDuplicateFLWs(frontLineWorker);
            }
        }, 100);
    }

    @Seed(priority = 0, version = "1.8", comment = "Syncing missing flws ")
    public void createDimensionAndRegistrationMeasureForMissingFLWs() {
        int batchSize = 100;
        seedService.doWithBatch(new FrontLineWorkerExecutable() {
            @Override
            public void execute(FrontLineWorker frontLineWorker) {
                seedService.createDimensionAndRegistrationMeasureFor(frontLineWorker);
            }
        }, batchSize);
    }

    @Seed(priority = 0, version = "1.10", comment = "Fill flw ids from FLWDimensions")
    public void copyFlwIdFromFLWDimensionToAllFlwsInCouch() {
        int batchSize = 100;
        seedService.doWithBatch(new FrontLineWorkerExecutable() {
            @Override
            public void execute(FrontLineWorker frontLineWorker) {
                seedService.copyFlwIdFromFLWDimension(frontLineWorker);
            }
        }, batchSize);
    }

    @Seed(priority = 1,version = "1.14", comment = "update location code for default location")
    public void updateAllFLWDefaultLocation() {
        String currentLocationCode="S01D000B000V000";
		String newLocationCode="S00D000B000V000";
		seedService.updateLocationCode(currentLocationCode, newLocationCode);
    }
    
    private String getInputCSV() {
        return environment.equals("prod") ? inputFileName : getClass().getResource(inputFileName).getPath();
    }

    private String getOutputCSV(String outputFilePath) {
        return outputFilePath + File.separator + outputFileName + new Date().getTime();
    }

//    public static void main(String[] args) throws IOException {
//        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-tool.xml");
//        FrontLineWorkerSeed frontLineWorkerSeed =
//                (FrontLineWorkerSeed) context.getBean("frontLineWorkerSeed");
//
//        log.info("******************Done***************");
//    }

}
