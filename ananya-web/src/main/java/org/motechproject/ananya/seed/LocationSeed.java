package org.motechproject.ananya.seed;

import liquibase.util.csv.CSVReader;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.Locations;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
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
        loadDefaultLocation();
        loadFromCsv(path);
    }

    private void loadDefaultLocation() {
        int defaultCode = 0;
        Location location = new Location(FrontLineWorker.DEFAULT_LOCATION, FrontLineWorker.DEFAULT_LOCATION, FrontLineWorker.DEFAULT_LOCATION, defaultCode, defaultCode, defaultCode);
        LocationDimension locationDimension = new LocationDimension(FrontLineWorker.DEFAULT_LOCATION);
        allLocations.add(location);
        allLocationDimensions.add(locationDimension);
    }

    public void loadFromCsv(String path) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(path));
        String currentDistrict, currentBlock, currentPanchayat;
        Locations locations = new Locations(allLocations.getAll());
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
                continue;
            }

            saveNewLocation(location, locations);
            currentRow = csvReader.readNext();
        }
    }

    private void saveNewLocation(Location currentLocation, Locations locations) {
        Location location = createNewLocation(currentLocation, locations);
        LocationDimension locationDimension = new LocationDimension(location.getExternalId(), location.getDistrict(), location.getBlock(), location.getPanchayat());

        allLocations.add(location);
        allLocationDimensions.add(locationDimension);
        locations.add(location);
    }

    private Location createNewLocation(Location currentLocation, Locations locations) {
        Integer districtCodeFor = locations.getDistrictCodeFor(currentLocation);
        Integer blockCodeFor = locations.getBlockCodeFor(currentLocation);
        Integer panchayatCodeFor = locations.getPanchayatCodeFor(currentLocation);
        Location locationToSave = new Location(currentLocation.getDistrict(), currentLocation.getBlock(), currentLocation.getPanchayat(), districtCodeFor, blockCodeFor, panchayatCodeFor);
        return locationToSave;
    }

    private boolean shouldNotCreateNewLocation(Location location, Locations locations) {
        return location.isMissingDetails() || locations.isAlreadyPresent(location);
    }
}