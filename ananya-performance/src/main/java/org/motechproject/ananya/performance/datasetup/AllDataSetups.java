package org.motechproject.ananya.performance.datasetup;

import org.motechproject.ananya.performance.PerformanceData;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AllDataSetups implements BeanPostProcessor {

    private List<DataSetupMethod> methods = new ArrayList<DataSetupMethod>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        for (Method method : bean.getClass().getDeclaredMethods())
            if (method.isAnnotationPresent(PerformanceData.class))
                methods.add(new DataSetupMethod(bean, method));
        return bean;
    }

    public void run() throws Exception {
        for (DataSetupMethod method : methods)
            method.run();
    }
}
