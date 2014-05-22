package org.motechproject.ananya.domain;


import org.apache.commons.lang.StringUtils;

public enum CourseItemState {
    START,
    END;


    public static CourseItemState findFor(String entity) {
        for (CourseItemState courseItemState : CourseItemState.values()) {
            if (StringUtils.equalsIgnoreCase(courseItemState.name(), entity)) {
                return courseItemState;
            }
        }
        return null;
    }
}
