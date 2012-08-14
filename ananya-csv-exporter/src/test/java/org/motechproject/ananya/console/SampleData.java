package org.motechproject.ananya.console;

import org.motechproject.export.annotation.ExportValue;

public class SampleData {

    private String msisdn;

    public SampleData(String msisdn) {
        this.msisdn = msisdn;
    }

    @ExportValue(index = 0)
    public String getmsisdn() {
        return msisdn;
    }

    @ExportValue(column = "Custom column name", index = 1)
    public String columnWithTitle() {
        return "title";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SampleData that = (SampleData) o;

        if (msisdn != null ? !msisdn.equals(that.msisdn) : that.msisdn != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return msisdn != null ? msisdn.hashCode() : 0;
    }
}
