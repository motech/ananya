package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

public class CertificationCourseLogItem {
    @JsonProperty
    private String contentId;
    @JsonProperty
    private String contentName;
    @JsonProperty
    private String contentType;
    @JsonProperty
    private String contentData;
    @JsonProperty
    private CourseItemState courseItemState;
    @JsonProperty
    private DateTime time;

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

    public DateTime getTime() {
        return time;
    }

    public String getContentName() {
        return contentName;
    }
}
