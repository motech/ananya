package org.motechproject.bbcwt.ivr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class IVRMessage {
    public static final String BBCWT_IVR_NEW_USER_WC_MESSAGE = "wc.msg.new.user";
    public static final String BBCWT_IVR_NEW_USER_OPTIONS = "msg.new.user.options";
    public static final String IVR_HELP = "msg.help";
    public static final String INVALID_INPUT = "invalid.ivr.input";
    public static final String END_OF_QUIZ_OPTIONS = "msg.quiz.end.options";
    public static final String MSG_COURSE_COMPLETION = "msg.course.completion";
    public static final String CONTENT_LOCATION = "content.location";
    public static final String QUIZ_HEADER = "quiz.header";
    public static final String ALLOWED_NUMBER_OF_NO_INPUTS = "allowed.number.of.no.inputs";
    public static final String ALLOWED_NUMBER_OF_INVALID_INPUTS = "allowed.number.of.invalid.inputs";

    @Qualifier("ivrProperties")
    @Autowired
    private Properties properties;

    public String get(String key) {
        return (String) properties.get(key);
    }
}
