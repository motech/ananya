package org.motechproject.ananya.support.log;

import org.joda.time.DateTime;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class SynchroniserLog {
    private List<SynchroniserLogItem> items = new ArrayList<SynchroniserLogItem>();
    private String name;

    public SynchroniserLog(String name) {
        this.name = name;
    }

    public void add(String id, String message) {
        items.add(new SynchroniserLogItem(id, message));
    }

    public void print() {
        DateTime now = DateUtil.now();
        System.out.println(name + "_" + now);
    }

    public List<SynchroniserLogItem> getItems() {
        return items;
    }
}
