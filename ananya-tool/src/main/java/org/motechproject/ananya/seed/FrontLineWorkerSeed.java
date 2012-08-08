package org.motechproject.ananya.seed;

import liquibase.util.csv.CSVReader;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.response.RegistrationResponse;
import org.motechproject.ananya.seed.service.FrontLineWorkerExecutable;
import org.motechproject.ananya.seed.service.FrontLineWorkerSeedService;
import org.motechproject.ananya.service.LocationService;
import org.motechproject.ananya.service.RegistrationService;
import org.motechproject.deliverytools.seed.Seed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Date;
import java.util.List;

@Component
public class FrontLineWorkerSeed {

    private static final Logger log = LoggerFactory.getLogger(FrontLineWorkerSeed.class);
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private FrontLineWorkerSeedService seedService;
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Value("#{ananyaProperties['seed.flw.file']}")
    private String inputFileName;
    @Value("#{ananyaProperties['seed.flw.file.out']}")
    private String outputFileName;
    @Value("#{ananyaProperties['environment']}")
    private String environment;

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-tool.xml");
        FrontLineWorkerSeed frontLineWorkerSeed =
                (FrontLineWorkerSeed) context.getBean("frontLineWorkerSeed");
        frontLineWorkerSeed.correctAllDesignations();
    }

    @Seed(priority = 0, version = "1.0", comment = "FLWs pre-registration via CSV, 20988 nos [P+C]")
    public void createFrontlineWorkersFromCSVFile() throws IOException {
        String inputCSVFile = getInputCSVFile();
        String outputFilePath = new File(inputCSVFile).getParent();
        String outputCSVFile = outputFilePath + File.separator + outputFileName + new Date().getTime();

        File file = new File(outputCSVFile);
        file.createNewFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputCSVFile));
        CSVReader csvReader = new CSVReader(new FileReader(inputCSVFile));
        LocationList locationList = new LocationList(locationService.getAll());

        String msisdn, name, designation, currentDistrict, currentBlock, currentPanchayat;
        String[] currentRow;
        csvReader.readNext();
        currentRow = csvReader.readNext();

        while (currentRow != null) {
            msisdn = currentRow[0];
            name = currentRow[1];
            designation = currentRow[2];
            currentDistrict = currentRow[3];
            currentBlock = currentRow[4];
            currentPanchayat = currentRow[5];

            RegistrationResponse registrationResponse = registrationService.registerFlw(
                    msisdn, name, designation, currentDistrict, currentBlock, currentPanchayat, locationList);

            writer.write(msisdn + " : " + registrationResponse.getMessage());
            writer.newLine();
            currentRow = csvReader.readNext();
        }
        writer.close();
    }

    @Seed(priority = 7, version = "1.1", comment = "FLWs registered via calls should be now 'unregistered' status. [P+C]")
    public void updateRegistrationStatusOfFrontLineWorkersRegisteredViaCalls() {
        seedService.correctRegistrationStatusInCouchAndPostgres();
    }

    @Seed(priority = 6, version = "1.1", comment = "1) merging duplicates")
    public void updateCorrectCallerIdsCircleOperatorAndDesignation() {
        List<FrontLineWorker> allFrontLineWorkers = seedService.getAllFromCouchDb();
        seedService.correctDuplicatesInCouchAndPostgres(allFrontLineWorkers);
    }

    @Seed(priority = 5, version = "1.1", comment = "1) Appending 91 to callerIds [P+C], 2) Update missing designation, operator [P], 3) Add default circle [C] ")
    public void updateOperatorDesignationCircleAndCorrectMsisdnInPostgresAndCouchDb() {
        String defaultCircle = "bihar";
        List<FrontLineWorker> allFrontLineWorkers = seedService.getAllFromCouchDb();
        seedService.updateOperatorDesignationCircleAndCorrectMsisdnInPostgresAndCouchDb(allFrontLineWorkers, defaultCircle);
    }

    @Seed(priority = 4, version = "1.3", comment = "Correction of invalid status for AWW [P+C]. If getting rid of designation from couchdb, updated only the postgres entries.")
    public void correctInvalidDesignationsForAnganwadi() throws IOException {
        String inputCSVFile = getInputCSVFile();
        String outputFilePath = new File(inputCSVFile).getParent();
        String outputCSVFile = outputFilePath + File.separator + outputFileName + new Date().getTime();

        File file = new File(outputCSVFile);
        file.createNewFile();
        CSVReader csvReader = new CSVReader(new FileReader(inputCSVFile));

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
        List<FrontLineWorkerDimension> frontLineWorkerDimensions = seedService.getFrontLineWorkers();
        for (FrontLineWorkerDimension frontLineWorkerDimension : frontLineWorkerDimensions) {
            count++;
            seedService.correctRegistrationStatus(frontLineWorkerDimension);
            if (count % logBreak == 0)
                log.info("completed " + count + " of " + frontLineWorkerDimensions.size() + " FLWs");
        }
    }

    @Seed(priority = 1, version = "1.7", comment = "Remove invalid designations")
    public void correctAllDesignations() throws IOException {
        String inputCSVFile = getInputCSVFile();
        String msisdn, designation;
        String[] row;
        int batchSize = 100;

        CSVReader csvReader = new CSVReader(new FileReader(inputCSVFile));
        csvReader.readNext();
        row = csvReader.readNext();

        log.info("correcting flw designations using csv file...");
        int counter = 0;
        while (row != null) {
            counter++;
            msisdn = StringUtils.trim(row[0]);
            if (msisdn.length() == 10) msisdn = "91" + msisdn;
            designation = StringUtils.trim(row[2]);
            try {
                seedService.correctDesignationBasedOnCSVFile(msisdn, designation);
            } catch (Exception e) {
                log.error("error while correcting designation for: " + msisdn, e);
            }
            row = csvReader.readNext();
            if (counter % batchSize == 0)
                log.info("corrected designation for " + counter + " users");
        }

        log.info("removing invalid designations...");
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

    private String getInputCSVFile() {
        return environment.equals("prod") ? inputFileName : getClass().getResource(inputFileName).getPath();
    }

}
