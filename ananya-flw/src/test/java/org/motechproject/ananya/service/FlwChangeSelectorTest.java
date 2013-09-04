package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.ReportCard;
import org.motechproject.ananya.domain.Score;

import static org.junit.Assert.assertEquals;

public class FlwChangeSelectorTest {

    private FlwChangeSelector changeSelector;
    private ReportCard latestReportCard;
    private String latestOperator;
    private BookMark latestBookMark;
    private Integer latestCurrentJobAidUsage;
    private DateTime latestLastJobAidAccessTime;
    private Integer latestCourseAttempts;

    @Before
    public void setUpComparator() {
        latestReportCard = new ReportCard();
        latestReportCard.addScore(new Score("1", "1", true));
        latestOperator = "O2";
        latestBookMark = new BookMark("2", 2, 2);
        latestCurrentJobAidUsage = 1;
        latestLastJobAidAccessTime = new DateTime().plusDays(1);
        latestCourseAttempts = 1;
        changeSelector = new FlwChangeSelector(getFrontLineWorker("O1", new BookMark("1", 1, 1), new ReportCard(), 1, new DateTime(), 1)
                , getFrontLineWorker(latestOperator, latestBookMark, latestReportCard, latestCurrentJobAidUsage, latestLastJobAidAccessTime, latestCourseAttempts));
    }

    private FrontLineWorker getFrontLineWorker(String operator, BookMark bookMark, ReportCard reportCard, Integer currentJobAidUsage, DateTime lastJobAidAccessTime, int certificateCourseAttempts) {
        FrontLineWorker frontLineWorker = new FrontLineWorker(null, operator, "C1", null);
        frontLineWorker.setBookMark(bookMark);
        frontLineWorker.setReportCard(reportCard);
        frontLineWorker.setCurrentJobAidUsage(currentJobAidUsage);
        frontLineWorker.setLastJobAidAccessTime(lastJobAidAccessTime);
        frontLineWorker.setCertificateCourseAttempts(certificateCourseAttempts);
        return frontLineWorker;
    }

    @Test
    public void shouldGetLatestLastJobAidAccessTime() {
        assertEquals(latestLastJobAidAccessTime, changeSelector.getTheLatestLastJobAidAccessTime());
    }

    @Test
    public void shouldGetLatestCurrentJobAidUsage() {
        assertEquals(latestCurrentJobAidUsage, changeSelector.getTheLatestJobAidUsage());
    }

    @Test
    public void shouldGetLatestBookMark() {
        assertEquals(latestBookMark, changeSelector.getHighestBookMark());
    }

    @Test
    public void shouldGetLatestOperator() {
        assertEquals(latestOperator, changeSelector.getTheLatestOperator());
    }

    @Test
    public void shouldGetLatestReportCard() {
        assertEquals(latestReportCard, changeSelector.getHighestReportCard());
    }

    @Test
    public void shouldGetLatestCourseAttempts() {
        assertEquals(latestCourseAttempts, changeSelector.getLatestCourseAttempt());
    }
}