package org.motechproject.bbcwt.matcher;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;
import org.motechproject.bbcwt.domain.Milestone;

import java.util.Date;

public  class MilestoneMatcher extends ArgumentMatcher<Milestone> {
    private String chapterIdToMatch;
    private String lessonIdToMatch;
    private Date startDateToMatch;
    private String healthWorkerIdToMatch;

    private Milestone calledWith;
    private Date endDateToMatch;

    public MilestoneMatcher(String healthWorkerId, String chapterIdToMatch, String lessonIdToMatch, Date startDate, Date endDate) {
            this.healthWorkerIdToMatch = healthWorkerId;
            this.chapterIdToMatch = chapterIdToMatch;
            this.lessonIdToMatch = lessonIdToMatch;
            this.startDateToMatch = startDate;
            this.endDateToMatch = endDate;
        }


    @Override
        public boolean matches(Object arg) {
            if(arg instanceof Milestone) {
                Milestone arg1 = (Milestone) arg;
                calledWith = arg1;

                return  StringUtils.equals(arg1.getHealthWorkerId(), healthWorkerIdToMatch) &&
                        StringUtils.equals(arg1.getChapterId(), chapterIdToMatch) &&
                        StringUtils.equals(arg1.getLessonId(), lessonIdToMatch) &&
                        (arg1.getStartDate()==null ? startDateToMatch == null : arg1.getStartDate().equals(startDateToMatch)) &&
                        (arg1.getEndDate()==null ? endDateToMatch == null : arg1.getEndDate().equals(endDateToMatch));
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            StringBuffer message = new StringBuffer();
            message.append("EXPECTED passed milestone to contain:\n");
            message.append("HealthWorker: " + healthWorkerIdToMatch + "\n");
            message.append("Chapter: " + chapterIdToMatch + "\n");
            message.append("Lesson: " + lessonIdToMatch + "\n");
            message.append("Start Date: " + startDateToMatch + "\n");
            message.append("End Date: " + endDateToMatch + "\n");
            message.append("BUT the milestone passed contains:");
            message.append("HealthWorker: " + calledWith.getHealthWorkerId() + "\n");
            message.append("Chapter: " + calledWith.getChapterId() + "\n");
            message.append("Lesson: " + calledWith.getLessonId() + "\n");
            message.append("Date: " + calledWith.getStartDate() + "\n");

            description.appendText(message.toString());
        }
    }