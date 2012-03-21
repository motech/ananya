package org.motechproject.ananya.domain;

public enum Designation {
    ANM,
    ASHA,
    ANGANWADI;

    public static boolean contains(String value) {
        for (Designation designation : Designation.values()) {
            if (designation.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
    
}
