package org.motechproject.ananya.support;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.ananya.support.synchroniser.AllSynchronisers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launcher {

    private static Logger log = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) throws Exception {
        log.info("Synchronising data: START:");

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        DateTime fromDate = formatter.parseDateTime(args[0]);
        DateTime toDate = formatter.parseDateTime(args[1]);
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-tool.xml");
        AllSynchronisers allSynchronisers = (AllSynchronisers) context.getBean("allSynchronisers");
        allSynchronisers.run(fromDate, toDate);

        log.info("Synchronising data: END:");
    }
}
