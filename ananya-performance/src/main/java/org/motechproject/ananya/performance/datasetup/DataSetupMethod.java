package org.motechproject.ananya.performance.datasetup;

import java.lang.reflect.Method;

public class DataSetupMethod {

    private Object bean;
    private Method method;

    protected DataSetupMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public void run() throws Exception {
        method.invoke(bean, null);
    }
}
