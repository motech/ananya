package org.motechproject.ananya.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

public class CertificationCourseLogItem {
    @JsonProperty
    private String contentId;
    @JsonProperty
    private String contentName;
    @JsonProperty
    private CourseItemType contentType;
    @JsonProperty
    private String contentData;
    @JsonProperty
    private CourseItemState courseItemState;
    @JsonProperty
    private DateTime time;

    public CertificationCourseLogItem() {
    }

    public CertificationCourseLogItem(String contentId, CourseItemType contentType, String contentName, String contentData, CourseItemState courseItemState, DateTime time) {
        this.contentId = contentId;
        this.contentType = contentType;
        this.contentName = contentName;
        this.contentData = contentData;
        this.courseItemState = courseItemState;
        this.time = time;
    }

    public String getContentId() {
        return contentId;
    }

    public CourseItemType getContentType() {
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

    public Integer giveScore() {
        return StringUtils.isEmpty(contentData) ? null : Integer.valueOf(getContentData());
    }
}
