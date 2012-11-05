package org.motechproject.ananya.domain;

import java.util.ArrayList;
import java.util.List;

public class LocationList {
    private List<Location> locations;

    public LocationList(List<Location> locations) {
        this.locations = new ArrayList<>();
        for (Location location : locations) {
            this.locations.add(location);
        }
    }

    public boolean isAlreadyPresent(Location currentLocation) {
        return getFor(currentLocation.getDistrict(), currentLocation.getBlock(), currentLocation.getPanchayat()) != null;
    }

    public Integer getDistrictCodeFor(Location currentLocation) {
        for (Location location : locations) {
            if (location.getDistrict().equals(currentLocation.getDistrict())) {
                return location.getDistrictCode();
            }
        }
        return getNextDistrictCode();
    }

    public Integer getBlockCodeFor(Location currentLocation) {
        for (Location location : locations) {
            if (location.getDistrict().equals(currentLocation.getDistrict())
                    && location.getBlock().equals(currentLocation.getBlock())) {
                return location.getBlockCode();
            }
        }
        return getNextBlockCode(currentLocation);
    }

    public Integer getPanchayatCodeFor(Location currentLocation) {
        int maxPanchayatCode = 0;
        for (Location location : locations) {
            if (location.getDistrict().equals(currentLocation.getDistrict())
                    && location.getBlock().equals(currentLocation.getBlock())
                    && location.getPanchayatCode() > maxPanchayatCode) {
                maxPanchayatCode = location.getPanchayatCode();
            }
        }
        return maxPanchayatCode + 1;
    }

    public void add(Location location) {
        locations.add(location);
    }

    public List<Location> getUniqueDistrictBlockLocations() {
        ArrayList<Location> uniqueDistrictBlockList = new ArrayList<Location>();
        String emptyPanchayat = "";

        for (Location location : locations) {
            Location defaultLocation = new Location(location.getDistrict(), location.getBlock(), emptyPanchayat, location.getDistrictCode(), location.getBlockCode(), 0, LocationStatus.VALID);
            if (!uniqueDistrictBlockList.contains(defaultLocation) && !locations.contains(defaultLocation)) {
                uniqueDistrictBlockList.add(defaultLocation);
            }
        }
        return uniqueDistrictBlockList;
    }

    public Location getFor(String district, String block, String panchayat) {
        Location locationToBeMatched = new Location(district, block, panchayat, 0, 0, 0, null);
        for (Location location : locations) {
            if (locationToBeMatched.equals(location)) {
                return location;
            }
        }
        return null;
    }

    private Integer getNextDistrictCode() {
        int maxLocationCode = 0;
        for (Location location : locations) {
            if (location.getDistrictCode() > maxLocationCode) {
                maxLocationCode = location.getDistrictCode();
            }
        }
        return maxLocationCode + 1;
    }

    private Integer getNextBlockCode(Location currentLocation) {
        int maxBlockCode = 0;
        for (Location location : locations) {
            if (location.getDistrict().equals(currentLocation.getDistrict()) && location.getBlockCode() > maxBlockCode) {
                maxBlockCode = location.getBlockCode();
            }
        }
        return maxBlockCode + 1;
    }
}
