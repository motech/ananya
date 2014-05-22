package org.motechproject.ananya.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'FrontLineWorkerKey'")
public class FrontLineWorkerKey extends MotechBaseDataObject {

    public FrontLineWorkerKey() {
    }

    public FrontLineWorkerKey(String id) {
        setId(id);
    }
}
