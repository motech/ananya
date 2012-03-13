package org.motechproject.ananya.performance;

import org.motechproject.ananya.performance.datasetup.AllDataSetups;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Launcher {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-performance.xml");
        AllDataSetups allDataSetups = (AllDataSetups) context.getBean("allDataSetups");
        allDataSetups.run();
    }
}
