package org.motechproject.ananya.transformers;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.contract.BaseRequest;
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

    private static List<String> jobAidCodes = new ArrayList<String>();
    private String jobAidRootCode;
    private String courseRootCode;
    private String jobAidLongCode;
    private String courseLongCode;
    private Integer codeSize;
    private AllNodes allNodes;

    @Autowired
    public CalledNumberTransformer(AllNodes allNodes,
                                   @Value("#{ananyaProperties['jobaid.shortcode']}") String jobAidRootCode,
                                   @Value("#{ananyaProperties['course.shortcode']}") String courseRootCode,
                                   @Value("#{ananyaProperties['jobaid.longcode']}") String jobAidLongCode,
                                   @Value("#{ananyaProperties['course.longcode']}") String courseLongCode,
                                   @Value("#{ananyaProperties['code.size']}") Integer codeSize) {
        this.jobAidRootCode = jobAidRootCode;
        this.courseRootCode = courseRootCode;
        this.jobAidLongCode = jobAidLongCode;
        this.courseLongCode = courseLongCode;
        this.codeSize = codeSize;
        this.allNodes = allNodes;
    }

    @Override
    public void transform(BaseRequest baseRequest) {
        String calledNumber = baseRequest.getCalledNumber();
        if (baseRequest.hasEmptyCalledNumber() || calledNumber.equals(courseLongCode) || calledNumber.equals(jobAidLongCode)) {
            return;
        }
        if (baseRequest.getType().isCertificateCourse()) {
            log(baseRequest, courseRootCode);
            baseRequest.setCalledNumber(courseRootCode);
            return;
        }
        initJobAidCodes();
        String trimmedNumber = StringUtils.substring(calledNumber, 0, codeSize);

        if (jobAidCodes.contains(trimmedNumber)) {
            log(baseRequest, trimmedNumber);
            baseRequest.setCalledNumber(trimmedNumber);
            return;
        }
        log(baseRequest, jobAidRootCode);
        baseRequest.setCalledNumber(jobAidRootCode);
    }

    private void initJobAidCodes() {
        if (jobAidCodes.isEmpty()) {
            List<String> jobAidShortCodes = allNodes.findValuesForKey("shortcode", "JobAidCourse");
            for (String shortCode : jobAidShortCodes)
                jobAidCodes.add(jobAidRootCode + shortCode);
            jobAidCodes.add(jobAidRootCode);
        }
    }

    private void log(BaseRequest baseRequest, String calledNumber) {
        log.info(baseRequest.getCallId() + "- transformed " + baseRequest.getCalledNumber() + " to " + calledNumber);
    }

}
