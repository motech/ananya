package org.motechproject.ananya.support.synchroniser.base;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Repository;

import java.util.*;

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

    public void run() {
        for (Synchroniser synchroniser : getAll()) {
            SynchroniserLog synchroniserLog = synchroniser.replicate();
            synchroniserLog.print();
        }
    }

    public List<Synchroniser> getAll() {
        Collections.sort(synchronisers, new Comparator<Synchroniser>() {
            @Override
            public int compare(Synchroniser synchroniser, Synchroniser synchroniser1) {
                return synchroniser1.runPriority().compareTo(synchroniser.runPriority());
            }
        });
        return synchronisers;
    }
}
