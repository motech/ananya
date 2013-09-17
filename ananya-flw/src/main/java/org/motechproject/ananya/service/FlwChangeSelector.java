package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.ReportCard;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang3.ObjectUtils.max;

public class FlwChangeSelector {
    private FrontLineWorker fromFlw;
    private FrontLineWorker toFlw;

    FlwChangeSelector(FrontLineWorker fromFlw, FrontLineWorker toFlw) {
        this.fromFlw = fromFlw;
        this.toFlw = toFlw;
    }

    public Integer getLatestCourseAttempt() {
        return toFlw == null || toFlw.currentCourseAttempts() == null ? fromFlw.currentCourseAttempts() : toFlw.currentCourseAttempts();
    }

    public DateTime getTheLatestLastJobAidAccessTime() {
        return (toFlw == null) ? null : toFlw.getLastJobAidAccessTime();
    }

    public Integer getTheLatestJobAidUsage() {
        return toFlw == null || toFlw.getCurrentJobAidUsage() == null ? fromFlw.getCurrentJobAidUsage() : toFlw.getCurrentJobAidUsage();
    }

    public String getTheLatestOperator() {
        return toFlw == null || isBlank(toFlw.getOperator()) ? fromFlw.getOperator() : toFlw.getOperator();
    }

    public BookMark getHighestBookMark() {
        if (toFlw == null) return fromFlw.bookMark();
        return max(toFlw.bookMark(), fromFlw.bookMark());
    }

    public ReportCard getHighestReportCard() {
        if (toFlw == null) return fromFlw.reportCard();
        return max(fromFlw.getReportCard(), toFlw.getReportCard());
    }

    public Map<String, Integer> getLatestPromptsHeard() {
        return toFlw == null ? new HashMap<String, Integer>() : toFlw.getPromptsHeard();
    }

    public RegistrationStatus getLatestRegistrationStatus() {
        return toFlw == null ? RegistrationStatus.UNREGISTERED : toFlw.getStatus();
    }
}
