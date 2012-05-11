package org.motechproject.ananya.requests;

import java.io.Serializable;
import java.util.Map;

public class LogData implements Serializable {
    private LogType type;
    private Map<String,String> dataMap;

    public LogData(LogType type, String dataId) {
        this.type = type;
        this.dataMap = dataId;
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
