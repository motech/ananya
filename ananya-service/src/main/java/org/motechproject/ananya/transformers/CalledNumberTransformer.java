package org.motechproject.ananya.transformers;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.request.BaseRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalledNumberTransformer implements Transformer {
    private static Logger log = LoggerFactory.getLogger(CalledNumberTransformer.class);
    public static final int valid_shortcode_length = 7;

    private AllNodes allNodes;
    private String shortCode;

    @Autowired
    public CalledNumberTransformer(AllNodes allNodes, @Value("#{ananyaProperties['jobaid.shortcode']}") String shortCode) {
        this.allNodes = allNodes;
        this.shortCode = shortCode;
    }

    @Override
    public void transform(BaseRequest baseRequest) {
        String calledNumber = baseRequest.getCalledNumber();

        if (StringUtils.isEmpty(calledNumber) || calledNumber.equals(shortCode))
            return;

        if (calledNumber.length() < valid_shortcode_length) {
            log.info(baseRequest.getCallId() + "- transformed " + calledNumber + " to " + shortCode);
            baseRequest.setCalledNumber(shortCode);
            return;
        }

        calledNumber = calledNumber.substring(0, valid_shortcode_length);

        if (StringUtils.isNumeric(calledNumber)) {
            List<String> shortCodes = allNodes.findValuesForKey("shortcode", "JobAidCourse");
            if (shortCodes.contains(calledNumber)) {
                log.info(baseRequest.getCallId() + "- transformed " + baseRequest.getCalledNumber() + " to " + calledNumber);
                baseRequest.setCalledNumber(calledNumber);
                return;
            }
        }
        baseRequest.setCalledNumber(shortCode);
        log.info(baseRequest.getCallId() + "- transformed " + calledNumber + " to " + shortCode);
    }
}
