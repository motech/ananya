package org.motechproject.bbcwt.ivr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class IVRMessage {
    public static final String BBCWT_IVR_WELCOME_MESSAGE = "welcome.message";

    @Qualifier("ivrProperties")
    @Autowired
    private Properties properties;

    public String get(String key) {
        return (String) properties.get(key);
    }
}
