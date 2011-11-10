package org.motechproject.bbcwt.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.bbcwt.util.UUIDUtil;

@TypeDiscriminator("doc.documentType == 'Lesson'")
public class Lesson extends BaseCouchEntity {
    private int number;

    private String fileName;

    private String endMenuFileName;

    public Lesson() {

    }

    public Lesson(int number, String fileName, String endMenuFileName) {
        this.number = number;
        this.fileName = fileName;
        this.endMenuFileName =  endMenuFileName;
        this.setId(UUIDUtil.newUUID());
    }

    public int getNumber() {
        return number;
    }

    public Lesson setNumber(int number) {
        this.number = number;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public Lesson setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getEndMenuFileName() {
        return endMenuFileName;
    }

    public void setEndMenuFileName(String endMenuFileName) {
        this.endMenuFileName = endMenuFileName;
    }
}