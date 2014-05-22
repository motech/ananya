package org.motechproject.ananya.transformers;

import org.motechproject.ananya.contract.BaseRequest;

public interface Transformer {

    void transform(BaseRequest baseRequest);
}
