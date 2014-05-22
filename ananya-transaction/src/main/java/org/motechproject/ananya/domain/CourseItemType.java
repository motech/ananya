package org.motechproject.ananya.domain;

import org.apache.commons.lang.StringUtils;

public enum CourseItemType {
    CHAPTER,
    LESSON,
    QUIZ,
    COURSE,
    AUDIO;

    public static CourseItemType findFor(String entity) {
        for (CourseItemType courseItemType : CourseItemType.values()) {
            if (StringUtils.equalsIgnoreCase(courseItemType.name(), entity)) {
                return courseItemType;
            }
        }
        return null;
    }
}
