package org.motechproject.ananya.support.diagnostics;

public class DiagnosticLog {

    private String entity;

    private StringBuilder log = new StringBuilder();

    public DiagnosticLog(String entity) {
        this.entity = entity;
    }

    public void add(String message) {
        log.append(message + "\n");
    }

    @Override
    public String toString() {
        return "====== DiagnosticLog " + entity + " ===" + "\n" + log;
    }
}
