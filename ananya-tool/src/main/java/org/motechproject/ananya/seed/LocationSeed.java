package org.motechproject.ananya.seed;

import liquibase.util.csv.CSVReader;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.LocationRegistrationResponse;
import org.motechproject.ananya.service.LocationRegistrationService;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class LocationSeed {
    @Autowired
    private LocationRegistrationService locationRegistrationService;

    @Value("#{ananyaProperties['seed.location.file']}")
    private String inputFileName;

    @Value("#{ananyaProperties['seed.location.file.out']}")
    private String outputFileName;

    @Value("#{ananyaProperties['seed.location.file.with.odisa.data']}")
    private String inputFileNameWithOdisaData;
    
    @Value("#{ananyaProperties['environment']}")
    private String environment;

    private BufferedWriter writer;

    @Seed(priority = 1, version = "1.0", comment = "load all locations from csv file")
    public void loadLocationsFromCSVFile() throws IOException {
        String inputCSVFile = environment.equals("prod") ? inputFileName : getClass().getResource(inputFileName).getPath();
        String outputFilePath = new File(inputCSVFile).getParent();
        String outputCSVFile = outputFilePath + File.separator + outputFileName + new Date().getTime();
        File file = new File(outputCSVFile);
        file.createNewFile();

        writer = new BufferedWriter(new FileWriter(outputCSVFile));

        loadDefaultLocation();
        loadFromCsv(inputCSVFile);
    }

    @Seed(priority = 1, version = "1.12", comment = "update all existing location status to VALID")
    public void locationStatusUpdateToValid() {
        locationRegistrationService.updateAllExistingLocationStatusToValid();
    }

    @Seed(priority = 1,version = "1.13", comment = "title case all location components")
    public void updateLocationDetailsToTitleCase() {
        locationRegistrationService.updateAllLocationsToTitleCase();
    }
    
    @Seed(priority = 1,version = "1.14", comment = "uodate external id and state name of default location")
    public void updateStateAndExternalIdForDefaultLocation() {
        String currentExternalId="S01D000B000V000";
		String newExternalId="S00D000B000V000";
		String state = "C00";
		locationRegistrationService.updateExternalId(currentExternalId, newExternalId);
		locationRegistrationService.updateStateNameByExternalId(newExternalId, state);
    }
    
    @Seed(priority = 1,version = "1.15", comment = "add state name as bihar in the existing locations")
    public void updateAllExistingLocationStateNameToBihar() {
        locationRegistrationService.updateAllNullLocationStateName("Bihar");
    }
    
    @Seed(priority = 1, version = "1.16", comment = "load all locations from csv file for odisa")
    public void loadOdisaLocationsFromCSVFile() throws IOException {
    	String inputCSVFileOdisaData = environment.equals("prod") ? inputFileNameWithOdisaData : getClass().getResource(inputFileNameWithOdisaData).getPath();
    	loadFromCsv(inputCSVFileOdisaData);
    }

    private void loadDefaultLocation() {
        locationRegistrationService.loadDefaultLocation();
    }
    
    private void reInitiateWriter(String inputCSVFile) throws IOException{
        String outputFilePath = new File(inputCSVFile).getParent();
        String outputCSVFile = outputFilePath + File.separator + outputFileName + new Date().getTime();
        File file = new File(outputCSVFile);
        file.createNewFile();

        writer = new BufferedWriter(new FileWriter(outputCSVFile));
    }
    
    private void loadFromCsv(String path) throws IOException {
    	reInitiateWriter(path);
        CSVReader csvReader = new CSVReader(new FileReader(path));
        String currentState, currentDistrict, currentBlock, currentPanchayat;
        String[] currentRow;
        List<LocationRequest> locationList = new ArrayList<LocationRequest>();
        //skip header
        csvReader.readNext();
        currentRow = csvReader.readNext();
        while (currentRow != null) {
        	currentState = currentRow[0];
            currentDistrict = currentRow[1];
            currentBlock = currentRow[2];
            currentPanchayat = currentRow[3];

            locationList.add(new LocationRequest(currentState, currentDistrict, currentBlock, currentPanchayat));

            currentRow = csvReader.readNext();
        }
        List<LocationRegistrationResponse> responses = locationRegistrationService.registerAllLocationsWithDefaultLocations(locationList);
        logResponses(responses);
    }

    private void logResponses(List<LocationRegistrationResponse> responses) throws IOException {
        for (LocationRegistrationResponse response : responses) {
            writer.write(response.getMessage() + response.getLocationDetails());
            writer.newLine();
        }
        writer.close();
    }
}