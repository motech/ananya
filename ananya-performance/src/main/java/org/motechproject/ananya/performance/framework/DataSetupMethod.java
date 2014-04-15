package org.motechproject.ananya.performance.framework;

import java.lang.reflect.Method;

public class DataSetupMethod {
    private Object bean;
    private Method method;
    private String description;

    public  DataSetupMethod(Object bean, Method method, String description) {
        this.bean = bean;
        this.method = method;
        this.description = description;
    }

    public void run() throws Exception {
        method.invoke(bean, null);
    }

    public String description() {
        return description;
    }
}
