package org.motechproject.ananya.domain;

import java.util.Date;

public class FileInfo {
    private String name;
    private Float size;
    private Date lastUpdated;

    public FileInfo(String name, long size, long lastUpdated) {
        this.name = name;
        this.lastUpdated = new Date(lastUpdated);
        this.size = getKiloBytes(size);
    }

    public String getName() {
        return name;
    }

    public Float getSize() {
        return size;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    private Float getKiloBytes(long size) {
        return size/1024f;
    }
}
