package org.motechproject.ananya.support;

import org.joda.time.DateTime;
import org.motechproject.ananya.support.synchroniser.AllSynchronisers;
import org.motechproject.util.DateUtil;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;

public class Launcher {

    public static void main(String[] args) {
        System.out.println("Synchronising data: START:");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm");
        DateTime fromDate = DateUtil.now();
        DateTime toDate = DateUtil.now();
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-tool.xml");
        AllSynchronisers allSynchronisers = (AllSynchronisers) context.getBean("allSynchronisers");
        allSynchronisers.run(fromDate, toDate);

        System.out.println("Synchronising data: END:");
    }
}
