package org.motechproject.ananya.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.LocationStatus;

public class LocationSyncRequest {
    private LocationRequest actualLocation;
    private LocationRequest newLocation;
    private String locationStatus;
    private DateTime lastModifiedTime;

    public LocationSyncRequest() {
    }

    public LocationSyncRequest(LocationRequest actualLocation, LocationRequest newLocation, String locationStatus, DateTime lastModifiedTime) {
        this.actualLocation = actualLocation;
        this.newLocation = newLocation;
        this.locationStatus = locationStatus;
        this.lastModifiedTime = lastModifiedTime;
    }

    public LocationRequest getActualLocation() {
        return actualLocation;
    }

    public LocationRequest getNewLocation() {
        return newLocation;
    }

    public String getLocationStatus() {
        return locationStatus;
    }

    public DateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public LocationStatus getLocationStatusAsEnum() {
        return LocationStatus.getFor(locationStatus);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }
}
