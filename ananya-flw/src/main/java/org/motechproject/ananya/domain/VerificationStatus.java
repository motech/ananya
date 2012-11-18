package org.motechproject.ananya.domain;

import org.apache.commons.lang.StringUtils;

public enum VerificationStatus {
    SUCCESS,
    INVALID,
    OTHER;

    public static VerificationStatus from(String string) {
        return VerificationStatus.valueOf(StringUtils.trimToEmpty(string).toUpperCase());
    }

    public static boolean isValid(String status) {
        try {
            from(status);
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    public static VerificationStatus findFor(String status) {
        for (VerificationStatus verificationStatus : VerificationStatus.values()) {
            if (StringUtils.equalsIgnoreCase(verificationStatus.name(), (StringUtils.trimToEmpty(status)))) {
                return verificationStatus;
            }
        }
        return null;
    }
}
