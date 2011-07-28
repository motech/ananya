package org.motechproject.bbcwt.domain;

import org.apache.commons.beanutils.BeanUtils;
import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.documentType == 'Lesson'")
public class Lesson extends BaseCouchEntity {
    private int number;

    private String location;

    public Lesson() {

    }

    public Lesson(int number, String location) {
       this.number = number;
       this.location = location;
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

    @Override
    public boolean equals(Object o) {
        if(o!=null && this.getClass().equals(o.getClass())) {
            Lesson other = (Lesson) o;
            if(this.getLocation().equals(other.getLocation()) && this.getNumber() == other.getNumber()) {
                return true;
            }
        }
        return false;
    }
}