package org.motechproject.ananya.requests;

import java.io.Serializable;

public class LogData implements Serializable {
    private LogType type;
    private String dataId;

    public LogData(LogType type, String dataId) {
        this.type = type;
        this.dataId = dataId;
    }

    public LogType getType() {
        return type;
    }

    public String getDataId() {
        return dataId;
    }

    @Override
    public String toString() {
        return "LogData{" +
                "type=" + type +
                ", dataId='" + dataId + '\'' +
                '}';
    }
}
