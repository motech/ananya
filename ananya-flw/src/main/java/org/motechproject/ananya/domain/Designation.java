package org.motechproject.ananya.domain;

public enum Designation {
    ANM,
    ASHA,
    AWW,
    INVALID;

    private static boolean contains(String value) {
        for (Designation designation : Designation.values()) {
            if (designation.name().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInValid(String designation) {
        return designation == null || !Designation.contains(designation) ? true : false;
    }
    
    public static Designation getFor(String designation) {
        return Designation.isInValid(designation) ? Designation.INVALID : Designation.valueOf(designation);
    }
}
