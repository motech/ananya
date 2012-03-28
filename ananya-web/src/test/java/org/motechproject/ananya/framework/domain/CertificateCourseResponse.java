package org.motechproject.ananya.framework.domain;

import org.motechproject.dao.MotechJsonReader;

import static junit.framework.Assert.assertFalse;
import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.StringUtils.removeStart;

public class CertificateCourseResponse {

    private boolean isRegistered;

    public CertificateCourseResponse confirmPartiallyRegistered() {
        assertFalse(isRegistered);
        return this;
    }

    public static CertificateCourseResponse make(String json) {
        MotechJsonReader jsonReader = new MotechJsonReader();
        String callerData = removeEnd(removeStart(json, "var callerData = "), ";");
        return (CertificateCourseResponse) jsonReader.readFromString(callerData, CertificateCourseResponse.class);
    }
}
