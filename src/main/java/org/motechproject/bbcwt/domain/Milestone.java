package org.motechproject.bbcwt.domain;

import org.ektorp.support.TypeDiscriminator;

import java.util.Date;

@TypeDiscriminator("doc.documentType == 'Milestone'")
public class Milestone extends BaseCouchEntity {
    private String healthWorkerId;
    private String chapterId;
    private String lessonId;
    private Date startDate;
    private Date endDate;

    public Milestone()
    {

    }

    public Milestone(String healthWorkerId, String chapterId, String lessonId, Date startDate) {
        this.healthWorkerId = healthWorkerId;
        this.chapterId = chapterId;
        this.lessonId = lessonId;
        this.startDate = startDate;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getHealthWorkerId() {
        return healthWorkerId;
    }

    public void setHealthWorkerId(String healthWorkerId) {
        this.healthWorkerId = healthWorkerId;
    }
}