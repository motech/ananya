package org.motechproject.ananya.domain.log;

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

}
