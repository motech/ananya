package org.motechproject.ananya.domain;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Map;

public class ReportData implements Serializable{
    private String bean;
    private Map<String, Object> record;
    private DateTime time;

    public ReportData(String bean, Map<String, Object> record, DateTime time) {
        this.bean = bean;
        this.record = record;
        this.time = time;
    }

    public String bean() {
        return bean;
    }

    public Map<String, Object> record() {
        return record;
    }

}
