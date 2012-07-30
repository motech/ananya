package org.motechproject.ananya.domain;

import org.apache.commons.lang.StringUtils;

public enum Designation {
    ANM,
    ASHA,
    AWW,

    /*
     * This value is only being maintained so that the seed can de-serialize incorrect object with
     * INVALID value (for correction). It is not used anywhere else in the code.
     */
    INVALID;

    private static boolean contains(String value) {
        for (Designation designation : Designation.values()) {
            if (designation.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInValid(String designation) {
        return
                StringUtils.isBlank(designation) ||
                !Designation.contains(designation);
    }
    
    public static Designation getFor(String designation) {
        return Designation.isInValid(designation) ? null : Designation.valueOf(designation.toUpperCase());
    }
}
