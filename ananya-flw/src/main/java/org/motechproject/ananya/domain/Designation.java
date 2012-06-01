package org.motechproject.ananya.domain;

import org.apache.commons.lang.StringUtils;

public enum Designation {
    ANM,
    ASHA,
    AWW,
    INVALID;

    public static boolean isInValid(String designation) {
        return designation == null || !Designation.contains(designation);
    }

    public static Designation getFor(String designation) {
        return Designation.isInValid(designation) ? Designation.INVALID : Designation.valueOf(StringUtils.trimToEmpty(designation).toUpperCase());
    }

    private static boolean contains(String value) {
        for (Designation designation : Designation.values()) {
            if (designation.name().equals(StringUtils.trimToEmpty(value).toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
