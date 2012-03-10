package org.motechproject.ananya.performance;

import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Launcher {

    public static void main(String[] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-performance.xml");
        RegistrationDataSetup registrationDataSetup = context.getBean(RegistrationDataSetup.class);
        registrationDataSetup.loadRegistrationData();
    }
}
