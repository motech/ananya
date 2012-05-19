package org.motechproject.ananya.seed;

import liquibase.util.csv.CSVReader;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.RegistrationResponse;
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
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;

    @Autowired
    private FrontLineWorkerService frontLineWorkerService;

    @Value("#{ananyaProperties['seed.flw.file']}")
    private String inputFileName;

    @Value("#{ananyaProperties['seed.flw.file.out']}")
    private String outputFileName;

    @Value("#{ananyaProperties['environment']}")
    private String environment;

    private static String DEFAULTCIRCLE = "BIHAR";
    private String inputCSVFile;
    private BufferedWriter writer;

    @Seed(priority = 0, version = "1.0")
    public void load() throws IOException {
        inputCSVFile = environment.equals("prod") ? inputFileName : getClass().getResource(inputFileName).getPath();
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
    @Seed(priority = 1, version = "1.1")
    public void updateStatusOfNewlyRegistered() {

        frontLineWorkerDimensionService.updateStatus(RegistrationStatus.UNREGISTERED.toString(), 20988);

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = frontLineWorkerDimensionService.getAllUnregistered();
        for (FrontLineWorkerDimension frontLineWorkerDimension : frontLineWorkerDimensions) {
            frontLineWorkerService.updateRegistrationStatus(frontLineWorkerDimension.getMsisdn().toString(), RegistrationStatus.UNREGISTERED);
        }
    }

    @Seed(priority = 0, version = "1.1")
    public void updateOperatorInReportDbFromCouchdb() {
        List<FrontLineWorker> allFrontLineWorkers = frontLineWorkerService.getAll();
        frontLineWorkerDimensionService.updateFrontLineWorkers(allFrontLineWorkers);
    }

    @Seed(priority = 0, version = "1.1")
    public void loadCircle() {
        List<FrontLineWorker> frontLineWorkers = allFrontLineWorkers.getAll();
        frontLineWorkerService.updateFrontLineWorkerWithDefaultCircle(frontLineWorkers, DEFAULTCIRCLE);
    }

    private void loadFromCsv(String path) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(path));
        String msisdn, name, designation, currentDistrict, currentBlock, currentPanchayat;
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
                    new LocationRequest(currentDistrict, currentBlock, currentPanchayat)));

            currentRow = csvReader.readNext();
        }
        List<RegistrationResponse> registrationResponses = registrationService.registerAllFLWs(frontLineWorkerRequests);
        logResponses(registrationResponses);
    }

    private void logResponses(List<RegistrationResponse> responses) throws IOException {
        for (RegistrationResponse response : responses) {
            writer.write(response.getMessage() + response.getFrontLineWorkerDetails());
            writer.newLine();
        }
        writer.close();
    }

}
