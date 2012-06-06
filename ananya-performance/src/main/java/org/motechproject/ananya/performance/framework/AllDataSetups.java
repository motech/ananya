package org.motechproject.ananya.performance.framework;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

@Repository
public class AllDataSetups implements BeanPostProcessor {

    private Map<String, List<DataSetupMethod>> methodMap = new HashMap<String, List<DataSetupMethod>>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        for (Method method : bean.getClass().getDeclaredMethods())
            if (method.isAnnotationPresent(PerformanceData.class)) {
                PerformanceData annotation = method.getAnnotation(PerformanceData.class);
                String testName = annotation.testName();
                if (!methodMap.containsKey(testName))
                    methodMap.put(testName, new ArrayList<DataSetupMethod>());
                methodMap.get(testName).add(new DataSetupMethod(bean, method, annotation.description()));
            }
        return bean;
    }

    public void run(String[] args) throws Exception {
        List<String> testKeys = Arrays.asList(args);
        for (String test : methodMap.keySet()) {
            if (testKeys.size() != 0 && !testKeys.contains(test)) continue;

            List<DataSetupMethod> dataSetupMethods = methodMap.get(test);

            CompletionService completionService = new ExecutorCompletionService(
                    Executors.newFixedThreadPool(dataSetupMethods.size()));

            for (DataSetupMethod dataSetupMethod : dataSetupMethods)
                completionService.submit(new DataSetupTask(dataSetupMethod));

            for (DataSetupMethod dataSetupMethod : dataSetupMethods)
                System.out.println(completionService.take().get());
        }
    }
}
