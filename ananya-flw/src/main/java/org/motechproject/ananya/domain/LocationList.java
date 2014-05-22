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
        return getFor(currentLocation.getState(), currentLocation.getDistrict(), currentLocation.getBlock(), currentLocation.getPanchayat()) != null;
    }

    public Integer getStateCodeFor(Location currentLocation) {
        for (Location location : locations) {
            if (location.getState().equals(currentLocation.getState())) {
                return location.getStateCode();
            }
        }
        return getNextStateCode();
    }
    
    public Integer getDistrictCodeFor(Location currentLocation) {
        for (Location location : locations) {
        	if (location.getState().equals(currentLocation.getState()) 
        			&& location.getDistrict().equals(currentLocation.getDistrict())) {
                return location.getDistrictCode();
            }
        }
        return getNextDistrictCode();
    }

    public Integer getBlockCodeFor(Location currentLocation) {
        for (Location location : locations) {
            if (location.getState().equals(currentLocation.getState()) 
            		&& location.getDistrict().equals(currentLocation.getDistrict())
                    && location.getBlock().equals(currentLocation.getBlock())) {
                return location.getBlockCode();
            }
        }
        return getNextBlockCode(currentLocation);
    }

    public Integer getPanchayatCodeFor(Location currentLocation) {
        int maxPanchayatCode = 0;
        for (Location location : locations) {
            if (location.getState().equals(currentLocation.getState()) 
            		&& location.getDistrict().equals(currentLocation.getDistrict())
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

    public List<Location> getUniqueStateDistrictBlockLocations() {
        ArrayList<Location> uniqueStateDistrictBlockList = new ArrayList<Location>();
        String emptyPanchayat = "";

        for (Location location : locations) {
            Location defaultLocation = new Location(location.getState(), location.getDistrict(), location.getBlock(), emptyPanchayat, location.getStateCode(), location.getDistrictCode(), location.getBlockCode(), 0, LocationStatus.VALID, null);
            if (!uniqueStateDistrictBlockList.contains(defaultLocation) && !locations.contains(defaultLocation)) {
                uniqueStateDistrictBlockList.add(defaultLocation);
            }
        }
        return uniqueStateDistrictBlockList;
    }

    public Location getFor(String state, String district, String block, String panchayat) {
        Location locationToBeMatched = new Location(state, district, block, panchayat, 0, 0, 0, 0, null, null);
        for (Location location : locations) {
            if (locationToBeMatched.equals(location)) {
                return location;
            }
        }
        return null;
    }

    private Integer getNextStateCode() {
        int maxLocationCode = 0;
        for (Location location : locations) {
            if (location.getStateCode() > maxLocationCode) {
                maxLocationCode = location.getStateCode();
            }
        }
        return maxLocationCode + 1;
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
