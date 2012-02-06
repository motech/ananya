package org.motechproject.ananya.domain;

import org.joda.time.DateTime;

import java.util.Map;

public class ReportData {
    private String table;
    private Map<String, Object> record;
    private DateTime time;

    public ReportData(String table, Map<String, Object> record, DateTime time) {
        this.table = table;
        this.record = record;
        this.time = time;
    }
}
