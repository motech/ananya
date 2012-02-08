package org.motechproject.ananya.seed;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SetupSeedData {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        System.out.println(context.getBeanDefinitionCount());
        return;
    }

}
