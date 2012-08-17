package org.motechproject.ananya.domain;

import java.util.Date;

public class FileInfo implements Comparable  {
    private String name;
    private Float size;
    private Date lastUpdated;

    public FileInfo(String name, long size, long lastUpdated) {
        this.name = name;
        this.lastUpdated = new Date(lastUpdated);
        this.size = getMegaBytes(size);
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

    private Float getMegaBytes(long size) {
        return size/(1024f*1024f);
    }

    @Override
    public int compareTo(Object fileInfo) {
        return this.toString().compareTo(fileInfo.toString());
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + '\'' +
                '}';
    }
}
