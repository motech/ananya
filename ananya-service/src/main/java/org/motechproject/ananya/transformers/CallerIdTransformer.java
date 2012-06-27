package org.motechproject.ananya.transformers;

import org.motechproject.ananya.request.BaseRequest;
import org.springframework.stereotype.Service;

@Service
public class CallerIdTransformer implements Transformer {

    @Override
    public void transform(BaseRequest baseRequest) {
        String callerId = baseRequest.getCallerId();
        if (callerId.length() == 10)
            baseRequest.setCallerId("91" + callerId);
    }
}
