package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'FrontLineWorkerKey'")
public class FrontLineWorkerKey extends MotechBaseDataObject {

    @JsonProperty
    private String key;

    public FrontLineWorkerKey(String key) {
        this.key = key;
    }
}
