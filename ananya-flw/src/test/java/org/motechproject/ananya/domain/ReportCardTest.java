package org.motechproject.ananya.domain;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class ReportCardTest {
    @Test
    public void shouldAddScoresToReportCard() {
        ReportCard reportCard = new ReportCard();
        String chapterIndex = "0";
        String questionIndex = "3";
        Boolean result = true;
        Score score1 = new Score(chapterIndex, questionIndex, result);
        reportCard.addScore(score1);
        assertThat(reportCard.scores(), hasItems(score1));
    }

    @Test
    public void shouldOverwriteScoreIfScoreExistsForTheSameQuestionInAChapter() {
        ReportCard reportCard = new ReportCard();

        Score scoreForChap1Que4 = new Score("0", "3", true);
        reportCard.addScore(scoreForChap1Que4);

        Score newScoreForChap1Que4 = new Score("0", "3", false);
        reportCard.addScore(newScoreForChap1Que4);

        assertThat(reportCard.scores().size(), is(1));
        assertThat(reportCard.scores(), hasItems(newScoreForChap1Que4));
    }

    @Test
    public void shouldReturnScoresByChapter() {
        ReportCard reportCard = makeTestReportCard();

        Map<String, Integer> expectedScoresByChapter = new HashMap();

        expectedScoresByChapter.put("0", 2);
        expectedScoresByChapter.put("1", 1);
        expectedScoresByChapter.put("2", 0);

        assertThat(reportCard.scoresByChapterIndex(), is(expectedScoresByChapter));
    }

    @Test
    public void shouldClearScoresForAChapterIndex() {
        ReportCard reportCard = makeTestReportCard();

        Map<String, Integer> expectedScoresByChapter = new HashMap();
        expectedScoresByChapter.put("0", 2);
        expectedScoresByChapter.put("1", 1);
        expectedScoresByChapter.put("2", 0);
        assertThat(reportCard.scoresByChapterIndex(), is(expectedScoresByChapter));

        final String nonExistentChapter = "100";
        reportCard.clearScoresForChapterIndex(nonExistentChapter);

        reportCard.clearScoresForChapterIndex("1");
        expectedScoresByChapter.remove("1");
        assertThat(reportCard.scoresByChapterIndex(), is(expectedScoresByChapter));

        reportCard.clearScoresForChapterIndex("0");
        expectedScoresByChapter.remove("0");

        assertThat(reportCard.scoresByChapterIndex(), is(expectedScoresByChapter));

        reportCard.clearScoresForChapterIndex("2");
        expectedScoresByChapter.remove("2");
        assertThat(reportCard.scoresByChapterIndex(), is(expectedScoresByChapter));
    }

    @Test
    public void shouldCalculateTotalScore() {
        ReportCard reportCard = makeTestReportCard();

        Integer totalScore = reportCard.totalScore();

        assertEquals(new Integer(3), totalScore);
    }

    @Test
    public void shouldReturnScoresForAChapter() {
        ReportCard reportCard = makeTestReportCard();

        Collection<Score> scoreList = reportCard.getScoresForChapter("1");

        assertEquals(3, scoreList.size());
    }

    @Test
    public void testComparison() {
        ReportCard score1 = new ReportCard();
        ReportCard score2 = new ReportCard();
        assertEquals(0, score2.compareTo(score1));
        assertEquals(0, score1.compareTo(score2));
        score2.addScore(new Score("1", "1", true));
        assertEquals(score2, ObjectUtils.max(score1, score2));
        score1.addScore(new Score("1", "1", true));
        assertEquals(0, score2.compareTo(score1));
        assertEquals(0, score1.compareTo(score2));
        score1.addScore(new Score("2", "1", true));
        assertEquals(score1, ObjectUtils.max(score1, score2));
    }

    private ReportCard makeTestReportCard() {
        ReportCard reportCard = new ReportCard();

        final Score ch1q1score = new Score("0", "4", true);
        final Score ch1q2score = new Score("0", "5", false);
        final Score ch1q3score = new Score("0", "6", true);
        final Score ch2q1score = new Score("1", "4", false);
        final Score ch2q2score = new Score("1", "5", false);
        final Score ch2q3score = new Score("1", "6", true);
        final Score ch3q1score = new Score("2", "4", false);
        final Score ch3q2score = new Score("2", "5", false);
        final Score ch3q3score = new Score("2", "6", false);

        reportCard.addScore(ch1q1score);
        reportCard.addScore(ch1q2score);
        reportCard.addScore(ch1q3score);
        reportCard.addScore(ch2q1score);
        reportCard.addScore(ch2q2score);
        reportCard.addScore(ch2q3score);
        reportCard.addScore(ch3q1score);
        reportCard.addScore(ch3q2score);
        reportCard.addScore(ch3q3score);
        return reportCard;
    }

}
