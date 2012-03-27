package org.motechproject.ananya.performance.data;

import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CertificateCoursePostDataSetupTest {

    private CertificateCoursePostDataSetup certificateCoursePostDataSetup;

    @Before
    public void setUp() {
        certificateCoursePostDataSetup = new CertificateCoursePostDataSetup();
    }
    
    @Test
    public void shouldParsePostDataTextProperly() {
        String line = "tokens[37] = [{\"token\":40,\"type\":\"ccState\",\"data\":{\"result\":null,\"questionResponse\":null,\"contentId\":\"ff1300036fb38ded71f17be045ded6f8\",\"contentType\":\"chapter\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"startNextChapter\",\"courseItemState\":\"start\",\"contentName\":\"Chapter 3\",\"time\":\"2012-03-08T12:56:59Z\",\"chapterIndex\":2,\"lessonOrQuestionIndex\":0}},{\"token\":41,\"type\":\"ccState\",\"data\":{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6cab5f\",\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"lesson\",\"courseItemState\":\"start\",\"contentName\":\"Chapter 3 Lesson 1\",\"time\":\"2012-03-08T12:56:59Z\",\"chapterIndex\":2,\"lessonOrQuestionIndex\":0}}];";

        String regex = "\\{\"token\":.*?\"contentId\":\"(\\w+)\".*?contentType\":\"(\\w+)\".*?contentName\":\"(.*?)\".*?\\}\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        while(matcher.find()) {
//            System.out.println("matcher.group(): " + matcher.group());
//            System.out.println("matcher.groupCount():" +  matcher.groupCount());
//            for (int i = 0; i <= matcher.groupCount(); i++) {
//                System.out.println(matcher.group(i));
//            }

            String contentId = matcher.group(1);
            String contentType = matcher.group(2);
            String contentName = matcher.group(3);



        }


//        // Line to be modified with contentId from the database contains a contentId value in the JSON.
//        if (StringUtils.contains(line, "\"contentId\":") &&
//                !StringUtils.contains(line, "\"contentId\":null")) {
//
//            String contentName = StringUtils.substringBetween(line, "\"contentName\":\"", "\",\"");
//            String contentType = StringUtils.substringBetween(line, "\"contentType\":\"", "\",\"");
//
//            // quiz is stored in the db as chapter, so...
//            if (contentType.equalsIgnoreCase("quiz")) contentType = "chapter";
//
//            String contentId =
//
//            line = line.replaceFirst("\"contentId\":\"\\w+\"", "\"contentId\":\"" + contentId + "\"");
//        }
    }

}
