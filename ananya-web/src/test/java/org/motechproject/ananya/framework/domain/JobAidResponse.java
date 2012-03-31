package org.motechproject.ananya.framework.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.dao.MotechJsonReader;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.StringUtils.removeStart;

public class JobAidResponse {

    private boolean isRegistered;
    private Long currentJobAidUsage;
    private Long maxAllowedUsageForOperator;
    private Map<String, Integer> promptsHeard;

    public static JobAidResponse make(String json) {
        if (StringUtils.equalsIgnoreCase(json, "var ananyaResponse = \"ANANYA_SUCCESS\";"))
            return new JobAidResponse();
        MotechJsonReader jsonReader = new MotechJsonReader();
        String callerData = removeEnd(removeStart(json, "var callerData = "), ";");
        return (JobAidResponse) jsonReader.readFromString(callerData, JobAidResponse.class);
    }

    public JobAidResponse confirmPartiallyRegistered() {
        assertFalse(isRegistered);
        return this;
    }

    public JobAidResponse verifyUserIsRegistered() {
        assertTrue(isRegistered);
        return this;
    }

    public JobAidResponse confirmMaxUsage(int expectedMinutes) {
        assertEquals(convertToMilliSec(expectedMinutes), maxAllowedUsageForOperator);
        return this;
    }

    public JobAidResponse confirmCurrentUsage(int expectedMinutes) {
        assertEquals(convertToMilliSec(expectedMinutes), currentJobAidUsage);
        return this;
    }

    private Long convertToMilliSec(int expected) {
        return new Long(expected * 60 * 1000);
    }


    public JobAidResponse confirmNoPromptsHeard() {
        assertTrue(promptsHeard.isEmpty());
        return this;
    }

    public JobAidResponse verifyPromptHeard(String maxUsagePrompt, Integer times) {
        assertEquals(times, promptsHeard.get(maxUsagePrompt));
        return this;
    }
}
