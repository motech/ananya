package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.ReportCard;
import org.motechproject.ananya.domain.Score;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class FlwChangeSelectorTest {

    private FlwChangeSelector changeSelector;
    private ReportCard latestReportCard;
    private String latestOperator;
    private BookMark latestBookMark;
    private Integer latestCurrentJobAidUsage;
    private DateTime latestLastJobAidAccessTime;
    private Integer latestCourseAttempts;
    private HashMap<String, Integer> promptsHeard;

    @Before
    public void setUp() {
        latestReportCard = new ReportCard();
        latestReportCard.addScore(new Score("1", "1", true));
        latestOperator = "O2";
        latestBookMark = new BookMark("2", 2, 2);
        latestCurrentJobAidUsage = 1;
        latestLastJobAidAccessTime = new DateTime().plusDays(1);
        latestCourseAttempts = 1;
        promptsHeard = new HashMap<String, Integer>();
        promptsHeard.put("1", 1);
        changeSelector = new FlwChangeSelector(getFrontLineWorker("O1", new BookMark("1", 1, 1), new ReportCard(), 1, new DateTime(), 1, new HashMap<String, Integer>())
                , getFrontLineWorker(latestOperator, latestBookMark, latestReportCard, latestCurrentJobAidUsage, latestLastJobAidAccessTime, latestCourseAttempts, promptsHeard));
    }

    private FrontLineWorker getFrontLineWorker(String operator, BookMark bookMark, ReportCard reportCard, Integer currentJobAidUsage, DateTime lastJobAidAccessTime, int certificateCourseAttempts, HashMap<String, Integer> promptsHeard) {
        FrontLineWorker frontLineWorker = new FrontLineWorker(null, operator, "C1", null);
        frontLineWorker.setBookMark(bookMark);
        frontLineWorker.setReportCard(reportCard);
        frontLineWorker.setCurrentJobAidUsage(currentJobAidUsage);
        frontLineWorker.setLastJobAidAccessTime(lastJobAidAccessTime);
        frontLineWorker.setCertificateCourseAttempts(certificateCourseAttempts);
        frontLineWorker.setPromptsHeard(promptsHeard);
        return frontLineWorker;
    }

    @Test
    public void shouldGetLatestLastJobAidAccessTime() {
        assertEquals(latestLastJobAidAccessTime, changeSelector.getTheLatestLastJobAidAccessTime());
    }

    @Test
    public void shouldReturnNullDateTimeIfNewMsisdnIsNotRegistered() {
        FrontLineWorker fromFlw = new FrontLineWorker();
        fromFlw.setLastJobAidAccessTime(new DateTime());
        assertNull(new FlwChangeSelector(fromFlw, null).getTheLatestLastJobAidAccessTime());
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
    public void shouldReturnOldBookMarkIfNewMsisdnIsNotRegistered() {
        FrontLineWorker flwByOldMsisdn = new FrontLineWorker();
        BookMark bookMark = new BookMark();
        flwByOldMsisdn.setBookMark(bookMark);
        assertEquals(bookMark, new FlwChangeSelector(flwByOldMsisdn, null).getHighestBookMark());
    }

    @Test
    public void shouldReturnOldReportCardIfNewMsisdnNotRegistered() {
        FrontLineWorker flwByOldMsisdn = new FrontLineWorker();
        ReportCard reportCard = new ReportCard();
        flwByOldMsisdn.setReportCard(reportCard);
        assertEquals(reportCard, new FlwChangeSelector(flwByOldMsisdn, null).getHighestReportCard());
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

    @Test
    public void shouldGetLatestPromptHeard() {
        assertEquals(promptsHeard, changeSelector.getLatestPromptsHeard());
    }

    @Test
    public void shouldReturnEmptyPromptsIfNewMsisdnIsNotRegistered() {
        FrontLineWorker fromFlw = new FrontLineWorker();
        Map<String, Integer> promptsHeardByOldMsisdn = Collections.singletonMap("1", 1);
        fromFlw.setPromptsHeard(promptsHeardByOldMsisdn);
        assertEquals(0, new FlwChangeSelector(fromFlw, null).getLatestPromptsHeard().size());
    }

    @Test
    public void shouldGetDefaultWhenFLWsDoNotHaveAnyRecords() {
        changeSelector = new FlwChangeSelector(new FrontLineWorker()
                , new FrontLineWorker());

        assertNull(changeSelector.getTheLatestLastJobAidAccessTime());
        assertNull(changeSelector.getTheLatestOperator());

        assertNotNull(changeSelector.getTheLatestJobAidUsage());
        assertNotNull(changeSelector.getHighestReportCard());
        assertNotNull(changeSelector.getLatestCourseAttempt());
        assertNotNull(changeSelector.getHighestBookMark());
        assertNotNull(changeSelector.getLatestPromptsHeard());
    }
}