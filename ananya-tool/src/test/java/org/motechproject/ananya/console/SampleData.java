package org.motechproject.ananya.console;

import org.motechproject.export.annotation.ReportValue;

public class SampleData {

    private String id;

    public SampleData(String id) {
        this.id = id;
    }

    @ReportValue(index = 0)
    public String getId() {
        return id;
    }

    @ReportValue(column = "Custom column name", index = 1)
    public String columnWithTitle() {
        return "title";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SampleData that = (SampleData) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
