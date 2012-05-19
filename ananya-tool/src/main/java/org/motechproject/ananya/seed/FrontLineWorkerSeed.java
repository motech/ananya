package org.motechproject.ananya.seed;

import liquibase.util.csv.CSVReader;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.response.RegistrationResponse;
import org.motechproject.ananya.service.FrontLineWorkerDimensionService;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.LocationService;
import org.motechproject.ananya.service.RegistrationService;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Date;
import java.util.List;

@Component
public class FrontLineWorkerSeed {

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private LocationService locationService;
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


    @Seed(priority = 0, version = "1.0", comment = "FLWs pre-registration via CSV, 20988 nos [P+C]")
    public void createFrontlineWorkersFromCSVFile() throws IOException {

        String inputCSVFile = environment.equals("prod") ? inputFileName : getClass().getResource(inputFileName).getPath();
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

    @Seed(priority = 1, version = "1.1", comment = "FLWs registered via calls should be now 'unregistered' status. [P+C]")
    public void updateStatusOfFrontLineWorkersRegisteredViaCalls() {
        int lastSequenceOfPreImportedFLWs = 20988;
        frontLineWorkerDimensionService.updateRegistrationStatus(RegistrationStatus.UNREGISTERED.toString(), lastSequenceOfPreImportedFLWs);

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = frontLineWorkerDimensionService.getAllUnregistered();
        for (FrontLineWorkerDimension frontLineWorkerDimension : frontLineWorkerDimensions) {
            frontLineWorkerService.updateRegistrationStatus(frontLineWorkerDimension.getMsisdn().toString(), RegistrationStatus.UNREGISTERED);
        }
    }

    @Seed(priority = 0, version = "1.1", comment = "1) Appending 91 to callerIds [P+C], 2) Update missing designation, operator [P], 3) Add default circle [C] ")
    public void update_CallerIds_Circle_Operator_Designation() {
        String defaultCircle = "BIHAR";
        List<FrontLineWorker> allFrontLineWorkers = frontLineWorkerService.getAll();
        frontLineWorkerDimensionService.updateFrontLineWorkers(allFrontLineWorkers);
        frontLineWorkerService.updateFrontLineWorkerWithDefaultCircleAndCorrectMsisdn(allFrontLineWorkers, defaultCircle);
    }

}
