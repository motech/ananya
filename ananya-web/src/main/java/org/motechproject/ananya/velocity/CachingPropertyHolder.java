package org.motechproject.ananya.velocity;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.web.context.support.ServletContextPropertyPlaceholderConfigurer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CachingPropertyHolder extends ServletContextPropertyPlaceholderConfigurer {
    private HashMap<String, String> resolvedProperties = new HashMap<String, String>();

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        for (Map.Entry entry : props.entrySet()) {
            resolvedProperties.put(entry.getKey().toString(), entry.getValue().toString());
        }
    }

    public Map resolvedProperties() {
        return Collections.unmodifiableMap(resolvedProperties);
    }
}
