package org.motechproject.ananya.performance.data;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.performance.framework.PerformanceData;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.service.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class JobAidDataSetup {

    private OperatorService operatorService;
    private AllNodes allNodes;

    @Autowired
    public JobAidDataSetup(OperatorService operatorService, AllNodes allNodes) {
        this.operatorService = operatorService;
        this.allNodes = allNodes;
    }

    @PerformanceData(testName = "jobaid-content", description = "Jobaid call-flow javascript generation")
    public void prepareDataForPosting() throws Exception {
        createJobAidTokensFile();
        updateJMeterFile();
    }

    private void createJobAidTokensFile() throws IOException {
        Node jobAidCourse = allNodes.findByName("JobAidCourse");
        BufferedReader templateReader = new BufferedReader(new FileReader("jmeter/js/job_aid_template.js"));
        BufferedWriter jobAidTokensWriter = new BufferedWriter(new FileWriter("jmeter/js/job_aid_tokens.js"));

        List<String> contentIds = new ArrayList<String>();
        recursivelyWriteAudioTrackerArrayForJobAid(jobAidCourse, contentIds);

        jobAidTokensWriter.write(String.format("var contentIds = [%s];", StringUtils.join(contentIds, ',')));
        String line = templateReader.readLine();
        while (line != null) {
            jobAidTokensWriter.newLine();
            jobAidTokensWriter.write(line);
            line = templateReader.readLine();
        }
        jobAidTokensWriter.close();
        templateReader.close();
    }

    private void updateJMeterFile() throws Exception {
        String jmx = FileUtils.readFileToString(new File("jmeter/jobaid_course_flows.jmx"));
        String tokens = FileUtils.readFileToString(new File("jmeter/js/job_aid_tokens.js"));

        String finalJmx = jmx.replace("${no_of_operators}", UserDataSetup.noOfOperators)
                .replace("${no_of_subscribers_per_operator}", String.valueOf(UserDataSetup.usersPerOperator))
                .replace("${msisdn_prefix}", UserDataSetup.msisdnPrefix)
                .replace("${job_aid_tokens}", StringEscapeUtils.escapeXml(tokens));

        FileUtils.writeStringToFile(new File("jmeter/jobaid_course_flows.jmx"), finalJmx);
    }

    private void recursivelyWriteAudioTrackerArrayForJobAid(Node node, List<String> contentArray) {
        for (String contentId : node.contentIds())
            contentArray.add("\"" + contentId + "\"");
        if (node.children().size() == 0) return;
        for (Node nextNode : node.children())
            recursivelyWriteAudioTrackerArrayForJobAid(nextNode, contentArray);
    }
}
