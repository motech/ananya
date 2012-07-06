package org.motechproject.ananya.performance.data;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.motechproject.ananya.performance.framework.PerformanceData;
import org.motechproject.ananya.repository.AllNodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class CertificateCoursePostDataSetup {

    private final int noOfChapters = 9;
    private final String usersPerOperator = "25000";
    private final String msisdnPrefix = "99";

    @Autowired
    private AllNodes allNodes;

    @PerformanceData(testName = "certificate_course-content", description = "CertificateCourse call-flow javascript generation")
    public void setupAllDataPostsWithContentIds() throws IOException {
        String jmx = FileUtils.readFileToString(new File("jmeter/certificate_course_flows.jmx"));

        jmx = createAllDataPostsFile(jmx);
        jmx = createCourseRootFile(jmx);
        jmx = setupNewUsers(jmx);
        jmx = setupUsers(jmx);
        for (int i = 1; i <= noOfChapters; i++)
            jmx = createCourseChapterFile(i, jmx);

        FileUtils.writeStringToFile(new File("jmeter/certificate_course_flows.jmx"), jmx);
    }

    private String createAllDataPostsFile(String jmx) throws IOException {
        String dataPosts = FileUtils.readFileToString(new File("jmeter/js/all_data_posts_template.js"));
        return jmx.replace("${all_data_posts}", StringEscapeUtils.escapeXml(dataPosts));
    }

    private String createCourseRootFile(String jmx) throws IOException {
        String rootJson = allNodes.nodeWithoutChildrenAsJson("CertificationCourse");
        String courseData = String.format("vars.putObject('course_data', %s)", rootJson);
        return jmx.replace("${certificate_course_without_level}", StringEscapeUtils.escapeXml(courseData));
    }

    private String createCourseChapterFile(int chapterNumber, String jmx) throws IOException {
        String json = allNodes.nodeAsJson("Chapter " + chapterNumber);
        String chapterJson = String.format("vars.getObject('course_data').children.push(%s);", json);
        return jmx.replace("${certificate_course_chapter" + chapterNumber + "}", StringEscapeUtils.escapeXml(chapterJson));
    }

    private String setupUsers(String jmx) throws IOException {
        String setupUsers = FileUtils.readFileToString(new File("jmeter/js/setup_users.js"));
        setupUsers = setupUsers.replace("${no_of_subscribers_per_operator}", usersPerOperator).replace("${msisdn_prefix}", msisdnPrefix);
        return jmx.replace("${setup_users}", StringEscapeUtils.escapeXml(setupUsers));
    }

    private String setupNewUsers(String jmx) throws IOException {
        String setupUsers = FileUtils.readFileToString(new File("jmeter/js/setup_new_users.js"));
        return jmx.replace("${setup_new_users}", StringEscapeUtils.escapeXml(setupUsers));
    }

}
