package org.motechproject.bbcwt.tools.seed;

import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.bbcwt.repository.tree.AllNodes;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationSeed {

    @Autowired
    private AllLocations allLocations;

    @Seed(priority = 0)
    public void load() {
        allLocations.add(new Location("S01D001", "Patna", "", ""));
        allLocations.add(new Location("S01D001B001", "Patna", "Dulhin Bazar", ""));
        allLocations.add(new Location("S01D001B001V001", "Patna", "Dulhin Bazar", "Aainkha Vimanichak"));
        allLocations.add(new Location("S01D001B001V002", "Patna", "Dulhin Bazar", "Achhuya Rakasiya"));
        allLocations.add(new Location("S01D001B001V003", "Patna", "Dulhin Bazar", "Bharatpura"));
        allLocations.add(new Location("S01D001B001V004", "Patna", "Dulhin Bazar", "Dhana Nisharpura"));
        allLocations.add(new Location("S01D001B001V005", "Patna", "Dulhin Bazar", "Kab"));
        allLocations.add(new Location("S01D001B001V006", "Patna", "Dulhin Bazar", "Lal Bhadshara"));
        allLocations.add(new Location("S01D001B001V007", "Patna", "Dulhin Bazar", "Narhi Pirhi"));
        allLocations.add(new Location("S01D001B001V008", "Patna", "Dulhin Bazar", "Rajipur"));
        allLocations.add(new Location("S01D001B001V009", "Patna", "Dulhin Bazar", "Sadawah Dorawa"));
        allLocations.add(new Location("S01D001B001V010", "Patna", "Dulhin Bazar", "Selahauri Belahauri"));
        allLocations.add(new Location("S01D001B001V011", "Patna", "Dulhin Bazar", "Sihi"));
        allLocations.add(new Location("S01D001B001V012", "Patna", "Dulhin Bazar", "Singhara Kopa"));
        allLocations.add(new Location("S01D001B001V013", "Patna", "Dulhin Bazar", "Soniyawa"));
        allLocations.add(new Location("S01D001B001V014", "Patna", "Dulhin Bazar", "Ular Sorampur"));

        allLocations.add(new Location("S01D002", "Khagaria", "", ""));
        allLocations.add(new Location("S01D002B001", "Khagaria", "Mansi", ""));
        allLocations.add(new Location("S01D002B001V001", "Khagaria", "Mansi", "Amni"));
        allLocations.add(new Location("S01D002B001V002", "Khagaria", "Mansi", "Balha"));
        allLocations.add(new Location("S01D002B001V003", "Khagaria", "Mansi", "Chakhusaini"));
        allLocations.add(new Location("S01D002B001V004", "Khagaria", "Mansi", "East Thatha"));
        allLocations.add(new Location("S01D002B001V005", "Khagaria", "Mansi", "Khutia"));
        allLocations.add(new Location("S01D002B001V006", "Khagaria", "Mansi", "Saidpur"));
        allLocations.add(new Location("S01D002B001V007", "Khagaria", "Mansi", "West Thatha"));

        allLocations.add(new Location("S01D003", "West Champaran", "", ""));
        allLocations.add(new Location("S01D003B001", "West Champaran", "Majhhaulia", ""));

        allLocations.add(new Location("S01D003B001V001", "West Champaran", "Majhhaulia", "Ahawar Kuria"));
        allLocations.add(new Location("S01D003B001V002", "West Champaran", "Majhhaulia", "Amawa Majhar"));
        allLocations.add(new Location("S01D003B001V003", "West Champaran", "Majhhaulia", "Bahuarawa"));
        allLocations.add(new Location("S01D003B001V004", "West Champaran", "Majhhaulia", "Baithania Bhanachak"));
        allLocations.add(new Location("S01D003B001V005", "West Champaran", "Majhhaulia", "Bakharia"));
        allLocations.add(new Location("S01D003B001V006", "West Champaran", "Majhhaulia", "Barawa Semaraghat"));
        allLocations.add(new Location("S01D003B001V007", "West Champaran", "Majhhaulia", "Bishambharpur"));
        allLocations.add(new Location("S01D003B001V008", "West Champaran", "Majhhaulia", "Chanayan Bandh"));
        allLocations.add(new Location("S01D003B001V009", "West Champaran", "Majhhaulia", "Dhokarahan"));
        allLocations.add(new Location("S01D003B001V010", "West Champaran", "Majhhaulia", "Dumari"));
        allLocations.add(new Location("S01D003B001V011", "West Champaran", "Majhhaulia", "Garawa Harpur"));
        allLocations.add(new Location("S01D003B001V012", "West Champaran", "Majhhaulia", "Gudara"));
        allLocations.add(new Location("S01D003B001V013", "West Champaran", "Majhhaulia", "Jaukatia"));
        allLocations.add(new Location("S01D003B001V014", "West Champaran", "Majhhaulia", "Karamawa"));
        allLocations.add(new Location("S01D003B001V015", "West Champaran", "Majhhaulia", "Lal Saraiya"));
        allLocations.add(new Location("S01D003B001V016", "West Champaran", "Majhhaulia", "Madhopur"));
        allLocations.add(new Location("S01D003B001V017", "West Champaran", "Majhhaulia", "Mahanagani"));
        allLocations.add(new Location("S01D003B001V018", "West Champaran", "Majhhaulia", "Mahanwa Rampurwa"));
        allLocations.add(new Location("S01D003B001V019", "West Champaran", "Majhhaulia", "Mahoddipur"));
        allLocations.add(new Location("S01D003B001V020", "West Champaran", "Majhhaulia", "Majharia Sheikh"));
        allLocations.add(new Location("S01D003B001V021", "West Champaran", "Majhhaulia", "Majhhaulia"));
        allLocations.add(new Location("S01D003B001V022", "West Champaran", "Majhhaulia", "Nautan Khund"));
        allLocations.add(new Location("S01D003B001V023", "West Champaran", "Majhhaulia", "Parsa"));
        allLocations.add(new Location("S01D003B001V024", "West Champaran", "Majhhaulia", "Rajabhar"));
        allLocations.add(new Location("S01D003B001V025", "West Champaran", "Majhhaulia", "Ramnagar Bankat"));
        allLocations.add(new Location("S01D003B001V026", "West Champaran", "Majhhaulia", "Ratanmala"));
        allLocations.add(new Location("S01D003B001V027", "West Champaran", "Majhhaulia", "Rulahi"));
        allLocations.add(new Location("S01D003B001V028", "West Champaran", "Majhhaulia", "Sarisawa"));
        allLocations.add(new Location("S01D003B001V029", "West Champaran", "Majhhaulia", "Senuaria"));

    }
}
