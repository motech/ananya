package org.motechproject.ananya.seed;

import liquibase.util.csv.CSVReader;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.response.LocationRegistrationResponse;
import org.motechproject.ananya.service.LocationRegistrationService;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Date;

@Component
public class LocationSeed {
    @Autowired
    private LocationRegistrationService locationRegistrationService;

    @Value("#{ananyaProperties['seed.location.file']}")
    private String inputFileName;

    @Value("#{ananyaProperties['seed.location.file.out']}")
    private String outputFileName;

    @Value("#{ananyaProperties['environment']}")
    private String environment;

    private BufferedWriter writer;

    @Seed(priority = 0)
    public void load() throws IOException {
        String inputCSVFile = environment.equals("prod") ? inputFileName : getClass().getResource(inputFileName).getPath();
        String outputFilePath = new File(inputCSVFile).getParent();
        String outputCSVFile = outputFilePath + File.separator + outputFileName + new Date().getTime();
        File file = new File(outputCSVFile);
        file.createNewFile();

        writer = new BufferedWriter(new FileWriter(outputCSVFile));

        loadDefaultLocation();
        loadFromCsv(inputCSVFile);
    }

    private void loadDefaultLocation() {
        locationRegistrationService.loadDefaultLocation();
    }

    private void loadFromCsv(String path) throws IOException {

        CSVReader csvReader = new CSVReader(new FileReader(path));
        String currentDistrict, currentBlock, currentPanchayat;
        String[] currentRow;

        //skip header
        csvReader.readNext();

        //first row data
        currentRow = csvReader.readNext();

        while (currentRow != null) {
            currentDistrict = currentRow[0];
            currentBlock = currentRow[1];
            currentPanchayat = currentRow[2];

            LocationRegistrationResponse response = locationRegistrationService.registerLocation(currentDistrict, currentBlock, currentPanchayat);
            log(currentDistrict, currentBlock, currentPanchayat, response);

            currentRow = csvReader.readNext();
        }
        writer.close();
    }


    private void log(String district, String block, String panchayat, LocationRegistrationResponse response) throws IOException {
        String failureLogMessage = response.getMessage() +" => District: " + district + " Block: "+ block + " Panchayat : " + panchayat;
        writer.write(failureLogMessage);
        writer.newLine();
    }
}