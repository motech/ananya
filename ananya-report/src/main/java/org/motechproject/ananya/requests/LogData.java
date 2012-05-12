package org.motechproject.ananya.requests;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LogData implements Serializable {
    private LogType type;
    private Map<String,String> dataMap;
    private static String CALLID = "callId";
    private static String CALLERID = "callerId";

    
    public LogData(LogType type, String callId, String callerId) {
        this.type = type;
        this.dataMap = new HashMap<String, String>();
        dataMap.put(CALLID, callId);
        dataMap.put(CALLERID, callerId);
    }

    public LogData(LogType type, String callerId) {
        this.type = type;
        this.dataMap = new HashMap<String, String>();
        dataMap.put(CALLERID, callerId);
    }


    public LogType getType() {
        return type;
    }

    public String getCallId() {
        return dataMap.get(CALLID);
    }
    
    public String getCallerId() {
        return dataMap.get(CALLERID);
    }

    @Override
    public String toString() {
        return "LogData{" +
                "type=" + type +
                ", dataMap='" + dataMap + '\'' +
                '}';
    }
}
