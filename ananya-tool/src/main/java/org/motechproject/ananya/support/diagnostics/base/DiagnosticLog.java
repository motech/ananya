package org.motechproject.ananya.support.diagnostics.base;

import org.hibernate.exception.ExceptionUtils;

import java.util.Map;

public class DiagnosticLog {

    private String entity;

    private StringBuilder log = new StringBuilder();

    public DiagnosticLog(String entity) {
        this.entity = entity;
    }

    public void add(String message) {
        log.append(message + "\n");
    }

    public void add(Map<String, String> map) {
        for (String key : map.keySet())
            add(key + ":" + map.get(key));
    }

    @Override
    public String toString() {
        return "\n" + "#--------------------------# "
                + entity + " #-------------------------#" + "\n\n" + log;
    }

    public void addError(Exception e) {
        log.append("EXCEPTION: " + ExceptionUtils.getFullStackTrace(e) + "\n\n");
    }
}
