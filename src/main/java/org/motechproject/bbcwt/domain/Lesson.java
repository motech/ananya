package org.motechproject.bbcwt.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.bbcwt.util.UUIDUtil;

@TypeDiscriminator("doc.documentType == 'Lesson'")
public class Lesson extends BaseCouchEntity {
    private int number;

    private String location;

    private String endMenuLocation;

    public Lesson() {

    }

    public Lesson(int number, String location, String endMenuLocation) {
        this.number = number;
        this.location = location;
        this.endMenuLocation =  endMenuLocation;
        this.setId(UUIDUtil.newUUID());
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEndMenuLocation() {
        return endMenuLocation;
    }

    public void setEndMenuLocation(String endMenuLocation) {
        this.endMenuLocation = endMenuLocation;
    }
}