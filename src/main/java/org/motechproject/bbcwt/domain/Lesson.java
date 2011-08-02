package org.motechproject.bbcwt.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.bbcwt.util.UUIDUtil;

@TypeDiscriminator("doc.documentType == 'Lesson'")
public class Lesson extends BaseCouchEntity {
    private int number;

    private String location;

    public Lesson() {

    }

    public Lesson(int number, String location) {
        this.number = number;
        this.location = location;
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
}