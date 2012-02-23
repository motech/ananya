package org.motechproject.ananya.performance.dataSetup;

import org.springframework.context.support.ClassPathXmlApplicationContext;


public class PerformanceDataSetupLauncher {

    public static void main(String[] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-performance.xml");
        RegistrationDataSetup registrationDataSetup = context.getBean(RegistrationDataSetup.class);
        registrationDataSetup.loadRegistrationData();
    }
}
