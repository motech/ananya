package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;

public class CertificationCourseLogItem {
    @JsonProperty
    private String contentId;
    @JsonProperty
    private String contentType;
    @JsonProperty
    private String contentData;
    @JsonProperty
    private CourseItemState courseItemState;

    public CertificationCourseLogItem() {
    }

    public CertificationCourseLogItem(String contentId, String contentType, String contentData, CourseItemState courseItemState) {
        this.contentId = contentId;
        this.contentType = contentType;
        this.contentData = contentData;
        this.courseItemState = courseItemState;
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
