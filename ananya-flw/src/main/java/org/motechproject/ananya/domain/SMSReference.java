package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.HashMap;
import java.util.Map;

@TypeDiscriminator("doc.type === 'SMSReference'")
public class SMSReference extends MotechBaseDataObject {

    @JsonProperty
    private String msisdn;

    @JsonProperty
    private String flwDocId;

    @JsonProperty
    private Map<Integer, String> referenceNumbers = new HashMap<>();

    public SMSReference() {
    }

    public SMSReference(String msisdn, String flwId) {
        this.msisdn = msisdn;
        flwDocId = flwId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void add(String smsReferenceNumber, Integer certificateCourseAttempts) {
        this.referenceNumbers.put(certificateCourseAttempts, smsReferenceNumber);
    }

    public Map<Integer, String> getReferenceNumbers() {
        return referenceNumbers;
    }

    public String referenceNumbers(int courseAttempt) {
        return referenceNumbers.get(courseAttempt);
    }

}
