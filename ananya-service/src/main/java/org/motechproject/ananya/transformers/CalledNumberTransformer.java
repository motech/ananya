package org.motechproject.ananya.transformers;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.request.BaseRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CalledNumberTransformer implements Transformer {

    private static Logger log = LoggerFactory.getLogger(CalledNumberTransformer.class);

    private static Integer size = 7;
    private static List<String> jobAidCodes = new ArrayList<String>();

    private AllNodes allNodes;
    private String jobAidRootCode;
    private String courseRootCode;

    @Autowired
    public CalledNumberTransformer(AllNodes allNodes,
                                   @Value("#{ananyaProperties['jobaid.shortcode']}") String jobAidRootCode,
                                   @Value("#{ananyaProperties['course.shortcode']}") String courseRootCode) {
        this.allNodes = allNodes;
        this.jobAidRootCode = jobAidRootCode;
        this.courseRootCode = courseRootCode;

    }

    @Override
    public void transform(BaseRequest baseRequest) {

        if (jobAidCodes.isEmpty()) {
            List<String> jobAidShortCodes = allNodes.findValuesForKey("shortcode", "JobAidCourse");
            for (String shortCode : jobAidShortCodes)
                jobAidCodes.add(jobAidRootCode + shortCode);
            jobAidCodes.add(jobAidRootCode);
        }
        if (baseRequest.getType().isCertificateCourse()) {
            log(baseRequest, courseRootCode);
            baseRequest.setCalledNumber(courseRootCode);
            return;
        }
        String calledNumber = baseRequest.getCalledNumber();
        calledNumber = StringUtils.substring(calledNumber, 0, size);
        if (jobAidCodes.contains(calledNumber)) {
            log(baseRequest, calledNumber);
            baseRequest.setCalledNumber(calledNumber);
            return;
        }
        log(baseRequest, jobAidRootCode);
        baseRequest.setCalledNumber(jobAidRootCode);
    }

    private void log(BaseRequest baseRequest, String calledNumber) {
        log.info(baseRequest.getCallId() + "- transformed " + baseRequest.getCalledNumber() + " to " + calledNumber);
    }

}
