package org.motechproject.ananya.transformers;

import org.motechproject.ananya.request.BaseRequest;

public interface Transformer {

    void transform(BaseRequest baseRequest);
}
