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
        String allDataPosts = getClass().getResource("/jmeter/js/all_data_posts_jmeter.js").getPath();
        String withoutLevel = getClass().getResource("/jmeter/js/certificate_course_without_levels.js").getPath();
        String chapter1 = getClass().getResource("/jmeter/js/certificate_course_chapter1.js").getPath();
        String chapter2 = getClass().getResource("/jmeter/js/certificate_course_chapter2.js").getPath();
        String chapter3 = getClass().getResource("/jmeter/js/certificate_course_chapter3.js").getPath();
        String chapter4 = getClass().getResource("/jmeter/js/certificate_course_chapter4.js").getPath();
        String chapter5 = getClass().getResource("/jmeter/js/certificate_course_chapter5.js").getPath();
        String chapter6 = getClass().getResource("/jmeter/js/certificate_course_chapter6.js").getPath();
        String chapter7 = getClass().getResource("/jmeter/js/certificate_course_chapter7.js").getPath();
        String chapter8 = getClass().getResource("/jmeter/js/certificate_course_chapter8.js").getPath();
        String chapter9 = getClass().getResource("/jmeter/js/certificate_course_chapter9.js").getPath();

        BufferedReader templateReader = new BufferedReader(new FileReader(templateFileName));
        BufferedWriter allDataPostsWriter = new BufferedWriter(new FileWriter(allDataPosts));
        BufferedWriter withoutLevelWriter = new BufferedWriter(new FileWriter(withoutLevel));
        BufferedWriter chapter1Writer = new BufferedWriter(new FileWriter(chapter1));
        BufferedWriter chapter2Writer = new BufferedWriter(new FileWriter(chapter2));
        BufferedWriter chapter3Writer = new BufferedWriter(new FileWriter(chapter3));
        BufferedWriter chapter4Writer = new BufferedWriter(new FileWriter(chapter4));
        BufferedWriter chapter5Writer = new BufferedWriter(new FileWriter(chapter5));
        BufferedWriter chapter6Writer = new BufferedWriter(new FileWriter(chapter6));
        BufferedWriter chapter7Writer = new BufferedWriter(new FileWriter(chapter7));
        BufferedWriter chapter8Writer = new BufferedWriter(new FileWriter(chapter8));
        BufferedWriter chapter9Writer = new BufferedWriter(new FileWriter(chapter9));

        withoutLevelWriter.write(String.format("vars.putObject('course_data', %s)", allNodes.nodeWithoutChildrenAsJson("CertificationCourse")));
        chapter1Writer.write(String.format("vars.getObject('course_data').children.push(%s);", allNodes.nodeAsJson("Chapter " + 1)));
        chapter2Writer.write(String.format("vars.getObject('course_data').children.push(%s);", allNodes.nodeAsJson("Chapter " + 2)));
        chapter3Writer.write(String.format("vars.getObject('course_data').children.push(%s);", allNodes.nodeAsJson("Chapter " + 3)));
        chapter4Writer.write(String.format("vars.getObject('course_data').children.push(%s);", allNodes.nodeAsJson("Chapter " + 4)));
        chapter5Writer.write(String.format("vars.getObject('course_data').children.push(%s);", allNodes.nodeAsJson("Chapter " + 5)));
        chapter6Writer.write(String.format("vars.getObject('course_data').children.push(%s);", allNodes.nodeAsJson("Chapter " + 6)));
        chapter7Writer.write(String.format("vars.getObject('course_data').children.push(%s);", allNodes.nodeAsJson("Chapter " + 7)));
        chapter8Writer.write(String.format("vars.getObject('course_data').children.push(%s);", allNodes.nodeAsJson("Chapter " + 8)));
        chapter9Writer.write(String.format("vars.getObject('course_data').children.push(%s);", allNodes.nodeAsJson("Chapter " + 9)));

        String line = templateReader.readLine();
        while (line != null) {
            allDataPostsWriter.newLine();
            allDataPostsWriter.write(line);
            line = templateReader.readLine();
        }

        allDataPostsWriter.close();
        withoutLevelWriter.close();
        chapter1Writer.close();
        chapter2Writer.close();
        chapter3Writer.close();
        chapter4Writer.close();
        chapter5Writer.close();
        chapter6Writer.close();
        chapter7Writer.close();
        chapter8Writer.close();
        chapter9Writer.close();
        templateReader.close();
    }

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-performance.xml");
        CertificateCoursePostDataSetup certificateCoursePostDataSetup = (CertificateCoursePostDataSetup) context.getBean("certificateCoursePostDataSetup");
        certificateCoursePostDataSetup.setupAllDataPostsWithContentIds();
        System.out.println("done");
    }
}
