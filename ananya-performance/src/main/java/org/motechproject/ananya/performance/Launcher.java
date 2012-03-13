package org.motechproject.ananya.performance;

import org.motechproject.ananya.performance.datasetup.AllDataSetups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Launcher {
    private static Logger log = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) throws Exception {
        log.info("Performance data setup: START");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-performance.xml");
        AllDataSetups allDataSetups = (AllDataSetups) context.getBean("allDataSetups");
        allDataSetups.run();
        log.info("Performance data setup: END");
    }
}
