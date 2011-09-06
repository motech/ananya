package org.motechproject.bbcwt.ivr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class IVRMessage {
    public static final String BBCWT_IVR_NEW_USER_WC_MESSAGE = "wc.msg.new.user";
    public static final String BBCWT_IVR_EXISTING_USER_WC_MESSAGE = "wc.msg.existing.user";
    public static final String BBCWT_IVR_NEW_USER_OPTIONS = "msg.new.user.options";
    public static final String IVR_HELP = "msg.help";
    public static final String INVALID_INPUT = "invalid.ivr.input";
    public static final String END_OF_QUIZ_OPTIONS = "msg.quiz.end.options";
    public static final String MSG_COURSE_COMPLETION = "msg.course.completion";
    public static final String WELCOME_BACK_BETWEEN_LESSONS = "msg.welcome.back.between.lessons";
    public static final String WELCOME_BACK_BETWEEN_LESSON_AND_QUIZ = "msg.welcome.back.between.lesson.and.quiz";
    public static final String WELCOME_BACK_BETWEEN_QUIZ_QUESTIONS = "msg.welcome.back.between.quiz.questions";
    public static final String WELCOME_BACK_RESUME_CHAPTER_1_LESSON_1 = "msg.welcome.back.ch1lesson1.resume";
    public static final String WELCOME_BACK_RESUME_CHAPTER_2_LESSON_1 = "msg.welcome.back.ch2lesson1.resume";
    public static final String CONTENT_LOCATION = "content.location";
    public static final String QUIZ_HEADER = "quiz.header";

    @Qualifier("ivrProperties")
    @Autowired
    private Properties properties;

    public String get(String key) {
        return (String) properties.get(key);
    }
}
