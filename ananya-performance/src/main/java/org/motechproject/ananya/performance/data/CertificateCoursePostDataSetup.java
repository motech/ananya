package org.motechproject.ananya.performance.data;

import org.motechproject.ananya.performance.framework.PerformanceData;
import org.motechproject.ananya.repository.AllNodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class CertificateCoursePostDataSetup {

    @Autowired
    private AllNodes allNodes;

    @PerformanceData(testName = "certificate course", description = "Setup all data posts with current doc ids")
    public void setupAllDataPostsWithContentIds() throws IOException {
        String templateFileName = getClass().getResource("/jmeter/js/all_data_posts_template.js").getPath();
        String outputFileName = getClass().getResource("/jmeter/js/all_data_posts_jmeter.js").getPath();

        BufferedReader templateReader = new BufferedReader(new FileReader(templateFileName));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));

        writer.write(String.format("var courseData = %s;", allNodes.nodeWithoutChildrenAsJson("CertificationCourse")));
        for(int chapterNumber = 1; chapterNumber <= 9; ++chapterNumber) {
            writer.newLine();
            writer.write(String.format("courseData.children.push(%s);", allNodes.nodeAsJson("Chapter " + chapterNumber)));
        }

        String line = templateReader.readLine();
        while (line != null) {
            writer.newLine();
            writer.write(line);
            line = templateReader.readLine();
        }

        writer.close();
        templateReader.close();
    }

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-performance.xml");
        CertificateCoursePostDataSetup certificateCoursePostDataSetup = (CertificateCoursePostDataSetup) context.getBean("certificateCoursePostDataSetup");
        certificateCoursePostDataSetup.setupAllDataPostsWithContentIds();
        System.out.println("done");
    }
}
