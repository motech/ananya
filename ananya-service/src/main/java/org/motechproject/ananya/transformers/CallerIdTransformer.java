package org.motechproject.ananya.transformers;

import org.motechproject.ananya.contract.BaseRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CallerIdTransformer implements Transformer {

    private static Logger log = LoggerFactory.getLogger(CallerIdTransformer.class);

    @Override
    public void transform(BaseRequest baseRequest) {
        String callerId = baseRequest.getCallerId();
        if (callerId.length() == 10) {
            baseRequest.setCallerId("91" + callerId);
            log.info(baseRequest.getCallId() + "- transformed " + callerId + " to " + baseRequest.getCallerId());
        }
    }
}
