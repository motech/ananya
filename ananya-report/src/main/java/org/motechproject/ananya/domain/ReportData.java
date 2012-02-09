package org.motechproject.ananya.domain;

import java.io.Serializable;

public class ReportData implements Serializable {
    private LogType type;
    private String dataId;

    public ReportData(LogType type, String dataId) {
        this.type = type;
        this.dataId = dataId;
    }

    public LogType logType() {
        return type;
    }

    public String logData() {
        return dataId;
    }

}
