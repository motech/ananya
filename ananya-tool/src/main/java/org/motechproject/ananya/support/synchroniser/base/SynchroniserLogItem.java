package org.motechproject.ananya.support.synchroniser.base;

public class SynchroniserLogItem {

    private String id;
    private String message;

    public SynchroniserLogItem(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String print() {
        return id + ": " + message;
    }
}
