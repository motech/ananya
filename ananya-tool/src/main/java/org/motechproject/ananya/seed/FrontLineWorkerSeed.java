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

    @Seed(priority = 0, version = "1.1")
    public void updateStatusOfNewlyRegistered() {
        List<FrontLineWorkerDimension> frontLineWorkerDimensions = frontLineWorkerDimensionService.getAllUnregistered();
        for(FrontLineWorkerDimension frontLineWorkerDimension : frontLineWorkerDimensions){
            frontLineWorkerService.updateRegistrationStatus(frontLineWorkerDimension.getMsisdn().toString(), RegistrationStatus.UNREGISTERED);
        }
    }

    private void loadFromCsv(String path) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(path));
        String msisdn, name, designation, currentDistrict, currentBlock, currentPanchayat;
        String[] currentRow;
        LocationList locationList = new LocationList(locationService.getAll());
        //skip header
        csvReader.readNext();
        currentRow = csvReader.readNext();
        while (currentRow != null) {
            msisdn = currentRow[0];
            name = currentRow[1];
            designation = currentRow[2];
            currentDistrict = currentRow[3];
            currentBlock = currentRow[4];
            currentPanchayat = currentRow[5];

            RegistrationResponse registrationResponse = registrationService.registerFlw(msisdn, name, designation,
                    currentDistrict, currentBlock, currentPanchayat, locationList);

            writer.write(msisdn + " : " + registrationResponse.getMessage());
            writer.newLine();
            currentRow = csvReader.readNext();
        }
        writer.close();
    }

}
