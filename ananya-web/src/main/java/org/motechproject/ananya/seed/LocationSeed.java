package org.motechproject.ananya.seed;

import liquibase.util.csv.CSVReader;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;

@Component
public class LocationSeed {


    @Autowired
    private AllLocations allLocations;

    @Autowired
    private AllLocationDimensions allLocationDimensions;

    @Value("#{ananyaProperties['seed.location.file']}")
    private String fileName;

    @Value("#{ananyaProperties['environment']}")
    private String environment;

    @Seed(priority = 0)
    public void load() throws IOException {
        String path = environment.equals("prod") ? fileName : getClass().getResource(fileName).getPath();
        loadFromCsv(path);
        loadDefaultLocation();
    }

    private void loadDefaultLocation() {
        Location location = new Location(FrontLineWorker.DEFAULT_LOCATION);
        LocationDimension locationDimension = new LocationDimension(FrontLineWorker.DEFAULT_LOCATION);
        allLocations.addOrUpdate(location);
        allLocationDimensions.addOrUpdate(locationDimension);
    }

    /*
    * CSV Structure :
    * District	District Code	Block Name	    Block Code	Panchayat Village	Village Code	Result
    *  Patna	S01D001         Dulhin Bazar	S01D001B001	Aainkha Vimanichak	S01D001B001V001
    *                                                       Achhuya Rakasiya	S01D001B001V002
    *                                                       Bharatpura          S01D001B001V003
    */
    public void loadFromCsv(String path) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(path));
        String currentDistrict = "", currentDistrictCode, currentBlock = "", currentBlockCode, currentPanchayat, currentPanchayatCode;

        Location location;
        LocationDimension locationDimension;
        String[] row;
        int rowNumber = 0;
        while (true) {
            row = csvReader.readNext();
            rowNumber++;
            // Skip header line
            if (1 == rowNumber) continue;
            // Read complete.
            if (null == row) break;
            if (!"".equals(row[0].trim())) {
                currentDistrict = row[0];
                currentDistrictCode = row[1];
                currentBlock = row[2];
                currentBlockCode = row[3];

                // Create extra locations for district and block
                location = new Location(currentDistrictCode, currentDistrict, "", "");
                locationDimension = new LocationDimension(currentDistrictCode, currentDistrict, "", "");
                allLocations.addOrUpdate(location);
                allLocationDimensions.addOrUpdate(locationDimension);

                location = new Location(currentBlockCode, currentDistrict, currentBlock, "");
                locationDimension = new LocationDimension(currentBlockCode, currentDistrict, currentBlock, "");
                allLocations.addOrUpdate(location);
                allLocationDimensions.addOrUpdate(locationDimension);
            }
            currentPanchayat = row[4];
            currentPanchayatCode = row[5];
            location = new Location(currentPanchayatCode, currentDistrict, currentBlock, currentPanchayat);
            locationDimension = new LocationDimension(currentPanchayatCode, currentDistrict, currentBlock, currentPanchayat);
            allLocations.addOrUpdate(location);
            allLocationDimensions.addOrUpdate(locationDimension);
        }
    }
}