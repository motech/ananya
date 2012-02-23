package org.motechproject.ananya.dataSetup;

import org.springframework.context.support.ClassPathXmlApplicationContext;


public class PerformanceDataSetupLauncher {

    public static void main(String[] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-service.xml");
        RegistrationDataSetup registrationDataSetup = context.getBean(RegistrationDataSetup.class);
        registrationDataSetup.loadRegistrationData();
    }
}
