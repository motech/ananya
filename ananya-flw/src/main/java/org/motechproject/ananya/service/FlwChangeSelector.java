package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.ReportCard;

import static org.apache.commons.lang.StringUtils.isBlank;

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
        return toFlw == null || toFlw.getLastJobAidAccessTime() == null ? fromFlw.getLastJobAidAccessTime() : toFlw.getLastJobAidAccessTime();

    }

    public Integer getTheLatestJobAidUsage() {
        return toFlw == null || toFlw.getCurrentJobAidUsage() == null ? fromFlw.getCurrentJobAidUsage() : toFlw.getCurrentJobAidUsage();
    }

    public String getTheLatestOperator() {
        return toFlw == null || isBlank(toFlw.getOperator()) ? fromFlw.getOperator() : toFlw.getOperator();
    }

    public BookMark getHighestBookMark() {
        BookMark oldBookMark = fromFlw.getBookmark();
        if (toFlw == null || toFlw.getBookmark() == null) return oldBookMark;
        BookMark newBookMark = toFlw.getBookmark();
        if (oldBookMark.getChapterIndex().equals(newBookMark.getChapterIndex()))
            return oldBookMark.getLessonIndex() < newBookMark.getChapterIndex() ? newBookMark : oldBookMark;
        return oldBookMark.getChapterIndex() < newBookMark.getChapterIndex() ? newBookMark : oldBookMark;
    }

    public ReportCard getHighestReportCard() {
        ReportCard oldReportCard = fromFlw.getReportCard();
        if (toFlw == null || toFlw.getReportCard() == null) return oldReportCard;
        ReportCard newReportCard = toFlw.getReportCard();
        return oldReportCard.totalScore() < newReportCard.totalScore() ? newReportCard : oldReportCard;
    }
}
