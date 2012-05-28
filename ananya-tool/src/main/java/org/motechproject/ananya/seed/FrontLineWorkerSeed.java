package org.motechproject.ananya.seed;

import liquibase.util.csv.CSVReader;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.RegistrationResponse;
import org.motechproject.ananya.seed.service.FrontLineWorkerSeedService;
import org.motechproject.ananya.service.FrontLineWorkerDimensionService;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.RegistrationService;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class FrontLineWorkerSeed {

    @Autowired
    private RegistrationService registrationService;
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

    @Seed(priority = 0, version = "1.0", comment = "FLWs pre-registration via CSV, 20988 nos [P+C]")
    public void createFrontlineWorkersFromCSVFile() throws IOException {

        String inputCSVFile = environment.equals("prod") ? inputFileName : getClass().getResource(inputFileName).getPath();
        String outputFilePath = new File(inputCSVFile).getParent();
        String outputCSVFile = outputFilePath + File.separator + outputFileName + new Date().getTime();

        File file = new File(outputCSVFile);
        file.createNewFile();

        writer = new BufferedWriter(new FileWriter(outputCSVFile));

        loadFromCsv(inputCSVFile);
    }

    /*
    * Users registered with id < 20988 were imported via a CSV file and have proper registration status. Users
    * after that were defaulted to PARTIALLY_REGISTERED, but need to be updated to UNREGISTERED. This needs to happen
    * in both couch and postgres.
    */
    public void loadFromCsv(String inputCSVFile) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(inputCSVFile));
        String msisdn, name, designation, currentDistrict, currentBlock, currentPanchayat;
        String circle = null;
        String[] currentRow;
        //skip header
        csvReader.readNext();
        currentRow = csvReader.readNext();

        List<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        while (currentRow != null) {
            msisdn = currentRow[0];
            name = currentRow[1];
            designation = currentRow[2];
            currentDistrict = currentRow[3];
            currentBlock = currentRow[4];
            currentPanchayat = currentRow[5];

            frontLineWorkerRequests.add(new FrontLineWorkerRequest(msisdn,
                    name,
                    designation,
                    null,
                    circle, new LocationRequest(currentDistrict, currentBlock, currentPanchayat)));

            currentRow = csvReader.readNext();
        }
        List<RegistrationResponse> registrationResponses = registrationService.registerAllFLWs(frontLineWorkerRequests);
        logResponses(registrationResponses);
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

        String inputCSVFile = environment.equals("prod") ? inputFileName : getClass().getResource(inputFileName).getPath();
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


}
