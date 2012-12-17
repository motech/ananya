package org.motechproject.ananya.support.diagnostics.support.synchroniser.base;

import org.joda.time.DateTime;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SynchroniserLog {
    private static Logger log = LoggerFactory.getLogger(SynchroniserLog.class);

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
        log.info(name + "records: " + now);
        for (SynchroniserLogItem logItem : items)
            log.info(logItem.print());
        log.info("-------------------------------------------------------------");
    }

    public List<SynchroniserLogItem> getItems() {
        return items;
    }
}
