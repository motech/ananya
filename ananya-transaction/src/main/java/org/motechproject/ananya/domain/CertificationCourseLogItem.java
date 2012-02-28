package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;

public class CertificationCourseLogItem {

    @JsonProperty
    private String contentId;
    @JsonProperty
    private String contentType;
    @JsonProperty
    private CourseItemState courseItemState;
    @JsonProperty
    private String contentData;

    public CertificationCourseLogItem() {
    }

    public CertificationCourseLogItem(
            String contentId, String contentType, CourseItemState courseItemState, String contentData) {
        this.contentId = contentId;
        this.contentType = contentType;
        this.courseItemState = courseItemState;
        this.contentData = contentData;
    }

    public String getContentId() {
        return contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public CourseItemState getCourseItemState() {
        return courseItemState;
    }

    public String getContentData() {
        return contentData;
    }
}
