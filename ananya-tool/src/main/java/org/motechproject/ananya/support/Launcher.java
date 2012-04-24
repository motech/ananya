package org.motechproject.ananya.support;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.ananya.support.synchroniser.AllSynchronisers;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launcher {

    public static void main(String[] args) throws Exception {
        System.out.println("Synchronising data: START:");

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        DateTime fromDate = formatter.parseDateTime(args[0]);
        DateTime toDate = formatter.parseDateTime(args[1]);
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-tool.xml");
        AllSynchronisers allSynchronisers = (AllSynchronisers) context.getBean("allSynchronisers");
        allSynchronisers.run(fromDate, toDate);

        System.out.println("Synchronising data: END:");
    }
}
