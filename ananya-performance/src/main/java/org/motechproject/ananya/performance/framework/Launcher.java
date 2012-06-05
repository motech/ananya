package org.motechproject.ananya.performance.framework;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Launcher {

    public static void main(String[] args) throws Exception {
        DateTime start = DateTime.now();
        System.out.println("Performance data setup: START: "+start);

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-performance.xml");
        AllDataSetups allDataSetups = (AllDataSetups) context.getBean("allDataSetups");
        allDataSetups.run(args);

        DateTime end = DateTime.now();
        System.out.println("Performance data setup: END: "+end);
        Period period = new Period(start, end);
        System.out.println("Time taken: "+ period.getHours()+":"+period.getMinutes()+":"+period.getSeconds());
    }
}
