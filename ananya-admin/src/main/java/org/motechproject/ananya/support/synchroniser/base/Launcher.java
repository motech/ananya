package org.motechproject.ananya.support.synchroniser.base;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launcher {

    public static void main(String[] args) throws Exception {
        System.out.println("Synchronising data: START:");

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-admin.xml");
        AllSynchronisers allSynchronisers = (AllSynchronisers) context.getBean("allSynchronisers");
        allSynchronisers.run();

        System.out.println("Synchronising data: END:");
    }
}
