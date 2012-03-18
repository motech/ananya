package org.motechproject.ananya.seed;

import liquibase.util.csv.CSVReader;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.service.LocationDimensionService;
import org.motechproject.ananya.service.LocationService;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Date;

@Component
public class LocationSeed {
    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationDimensionService locationDimensionService;

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
        int defaultCode = 0;
        Location location = new Location(FrontLineWorker.DEFAULT_LOCATION, FrontLineWorker.DEFAULT_LOCATION, FrontLineWorker.DEFAULT_LOCATION, defaultCode, defaultCode, defaultCode);
        LocationDimension locationDimension = new LocationDimension(FrontLineWorker.DEFAULT_LOCATION);
        locationService.add(location);
        locationDimensionService.add(locationDimension);
    }

    private void loadFromCsv(String path) throws IOException {

        CSVReader csvReader = new CSVReader(new FileReader(path));
        String currentDistrict, currentBlock, currentPanchayat;
        LocationList locations = new LocationList(locationService.getAll());
        String[] currentRow;

        //skip header
        csvReader.readNext();

        //first row data
        currentRow = csvReader.readNext();

        while (currentRow != null) {
            currentDistrict = currentRow[0];
            currentBlock = currentRow[1];
            currentPanchayat = currentRow[2];

            Location location = new Location(currentDistrict, currentBlock, currentPanchayat, 0, 0, 0);
            if (shouldNotCreateNewLocation(location, locations)) {
                currentRow = csvReader.readNext();
                log(currentDistrict, currentBlock, currentPanchayat);
                continue;
            }

            saveNewLocation(location, locations);
            currentRow = csvReader.readNext();
        }
        writer.close();
    }

    private void saveNewLocation(Location currentLocation, LocationList locations) {
        Location location = createNewLocation(currentLocation, locations);
        LocationDimension locationDimension = new LocationDimension(location.getExternalId(), location.getDistrict(), location.getBlock(), location.getPanchayat());

        locationService.add(location);
        locationDimensionService.add(locationDimension);
        locations.add(location);
    }

    private Location createNewLocation(Location currentLocation, LocationList locations) {
        Integer districtCodeFor = locations.getDistrictCodeFor(currentLocation);
        Integer blockCodeFor = locations.getBlockCodeFor(currentLocation);
        Integer panchayatCodeFor = locations.getPanchayatCodeFor(currentLocation);
        Location locationToSave = new Location(currentLocation.getDistrict(), currentLocation.getBlock(), currentLocation.getPanchayat(), districtCodeFor, blockCodeFor, panchayatCodeFor);
        return locationToSave;
    }

    private boolean shouldNotCreateNewLocation(Location location, LocationList locations) {
        return location.isMissingDetails() || locations.isAlreadyPresent(location);
    }

    private void log(String district, String block, String panchayat) throws IOException {
        String failureLogMessage = "Ignoring record with District: " + district + " Block: "+ block + " Panchayat : " + panchayat;
        writer.write(failureLogMessage);
        writer.newLine();
    }
}