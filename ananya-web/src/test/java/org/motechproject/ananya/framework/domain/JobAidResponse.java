package org.motechproject.ananya.framework.domain;

import org.motechproject.dao.MotechJsonReader;

import java.util.Map;

import static junit.framework.Assert.assertTrue;
import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.StringUtils.removeStart;

public class JobAidResponse {

    private boolean isRegistered;
    private Long currentJobAidUsage;
    private Long maxAllowedUsageForOperator;
    private Map<String, Integer> promptsHeard;

    public static JobAidResponse make(String json) {
        MotechJsonReader jsonReader = new MotechJsonReader();
        String callerData = removeEnd(removeStart(json, "var callerData = "), ";");
        return (JobAidResponse) jsonReader.readFromString(callerData, JobAidResponse.class);
    }

    public void verifyUserIsRegistered() {
        assertTrue(isRegistered);
    }


}
