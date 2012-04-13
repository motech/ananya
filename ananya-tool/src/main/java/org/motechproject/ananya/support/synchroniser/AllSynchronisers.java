package org.motechproject.ananya.support.synchroniser;

import org.joda.time.DateTime;
import org.motechproject.ananya.support.log.SynchroniserLog;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class AllSynchronisers implements BeanPostProcessor {

    private List<Synchroniser> synchronisers = new ArrayList<Synchroniser>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        List<Class<?>> interfaces = Arrays.asList(bean.getClass().getInterfaces());
        if (interfaces.contains(Synchroniser.class))
            synchronisers.add((Synchroniser) bean);
        return bean;
    }

    public void run(DateTime fromDate, DateTime toDate) {
        for (Synchroniser synchroniser : synchronisers) {
            SynchroniserLog synchroniserLog = synchroniser.replicate(fromDate, toDate);
            synchroniserLog.print();
        }
    }

    public List<Synchroniser> getAll() {
        return synchronisers;
    }
}