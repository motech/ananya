package org.motechproject.ananya.seed;

import liquibase.util.csv.CSVReader;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.response.RegistrationResponse;
import org.motechproject.ananya.service.LocationService;
import org.motechproject.ananya.service.RegistrationService;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Date;

@Component
public class FrontLineWorkerSeed {
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private LocationService locationService;

    @Value("#{toolProperties['seed.flw.file']}")
    private String inputFileName;

    @Value("#{toolProperties['seed.flw.file.out']}")
    private String outputFileName;

    @Value("#{toolProperties['environment']}")
    private String environment;

    private String inputCSVFile;
    private BufferedWriter writer;

    @Seed(priority = 0)
    public void load() throws IOException {
        inputCSVFile = environment.equals("prod") ? inputFileName : getClass().getResource(inputFileName).getPath();
        String outputFilePath = new File(inputCSVFile).getParent();
        String outputCSVFile = outputFilePath + File.separator + outputFileName + new Date().getTime();
        File file = new File(outputCSVFile);
        file.createNewFile();

        writer = new BufferedWriter(new FileWriter(outputCSVFile));

        loadFromCsv(inputCSVFile);
    }

    private void loadFromCsv(String path) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(path));
        String msisdn, name, designation, currentDistrict, currentBlock, currentPanchayat;
        String[] currentRow;
        LocationList locationList = new LocationList(locationService.getAll());
        //skip header
        csvReader.readNext();

        //first row data
        currentRow = csvReader.readNext();

        while (currentRow != null) {
            msisdn = currentRow[0];
            name = currentRow[1];
            designation = currentRow[2];
            currentDistrict = currentRow[3];
            currentBlock = currentRow[4];
            currentPanchayat = currentRow[5];

            RegistrationResponse registrationResponse = registrationService.registerFlw(msisdn, name, designation, currentDistrict, currentBlock, currentPanchayat, locationList);
            log(msisdn, registrationResponse);

            currentRow = csvReader.readNext();
        }
        writer.close();
    }

    private void log(String msisdn, RegistrationResponse registrationResponse) throws IOException {
        String logMessage = msisdn + " : " + registrationResponse.getMessage();
        writer.write(logMessage);
        writer.newLine();
    }
}