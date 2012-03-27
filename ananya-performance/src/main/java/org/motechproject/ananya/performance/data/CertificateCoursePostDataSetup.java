package org.motechproject.ananya.performance.data;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.performance.framework.PerformanceData;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CertificateCoursePostDataSetup {

    @Autowired
    private AllCourseItemDimensions allCourseItemDimensions;
    
    @PerformanceData(testName = "certificate course", description = "Setup all data posts with current doc ids")
    public void setupAllDataPostsWithContentIds() throws IOException {
        String templateFileName = getClass().getResource("/jmeter/js/all_data_posts_template.js").getPath();
        String outputFileName = getClass().getResource("/jmeter/js/all_data_posts_jmeter.js").getPath();

        BufferedReader templateReader = new BufferedReader(new FileReader(templateFileName));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
        
        String line = templateReader.readLine();
        int counter = 0;
        while (line != null) {

            String regex = "\\{\"token\":.*?\"contentId\":\"(\\w+)\".*?contentType\":\"(\\w+)\".*?contentName\":\"(.*?)\".*?\\}\\}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(line);

            while(matcher.find()) {

                String oldContentId = matcher.group(1);
                String contentType = matcher.group(2);
                String contentName = matcher.group(3);

                // Line to be modified with contentId from the database contains a contentId value in the JSON.
                if (oldContentId == null) continue;

                // quiz is stored in the db as chapter, so...
                if (contentType.equalsIgnoreCase("quiz")) contentType = "chapter";

                String contentId = allCourseItemDimensions.getFor(
                        contentName, CourseItemType.valueOf(contentType.toUpperCase())).getContentId();

                line = line.replaceAll("\"contentId\":\"" + oldContentId + "\"", "\"contentId\":\"" + contentId + "\"");
            }

            writer.write(line); writer.newLine();
            line = templateReader.readLine();
        }

        writer.close();
        templateReader.close();
    }

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-performance.xml");
        CertificateCoursePostDataSetup certificateCoursePostDataSetup = (CertificateCoursePostDataSetup) context.getBean("certificateCoursePostDataSetup");
        certificateCoursePostDataSetup.setupAllDataPostsWithContentIds();
    }
    
}
