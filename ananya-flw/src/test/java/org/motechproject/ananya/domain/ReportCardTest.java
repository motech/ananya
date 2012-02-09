package org.motechproject.ananya.domain;

import ch.lambdaj.group.Group;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.forEach;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class ReportCardTest {
    @Test
    public void shouldAddScoresToReportCard() {
        ReportCard reportCard = new ReportCard();
        String chapterIndex = "0";
        String questionIndex = "3";
        Boolean result  = true;
        ReportCard.Score score1 = new ReportCard.Score(chapterIndex, questionIndex, result);
        reportCard.addScore(score1);
        assertThat(reportCard.scores(), hasItems(score1));
    }

    @Test
    public void shouldOverwriteScoreIfScoreExistsForTheSameQuestionInAChapter() {
        ReportCard reportCard = new ReportCard();

        ReportCard.Score scoreForChap1Que4 = new ReportCard.Score("0", "3", true);
        reportCard.addScore(scoreForChap1Que4);

        ReportCard.Score newScoreForChap1Que4 = new ReportCard.Score("0", "3", false);
        reportCard.addScore(newScoreForChap1Que4);

        assertThat(reportCard.scores().size(), is(1));
        assertThat(reportCard.scores(), hasItems(newScoreForChap1Que4));
    }

    @Test
    public void shouldReturnScoresByChapter() {
        ReportCard reportCard = new ReportCard();

        final ReportCard.Score ch1q1score = new ReportCard.Score("0", "4", true);
        final ReportCard.Score ch1q2score = new ReportCard.Score("0", "5", false);
        final ReportCard.Score ch1q3score = new ReportCard.Score("0", "6", true);

        final ReportCard.Score ch2q1score = new ReportCard.Score("1", "4", false);
        final ReportCard.Score ch2q2score = new ReportCard.Score("1", "5", false);
        final ReportCard.Score ch2q3score = new ReportCard.Score("1", "6", true);

        final ReportCard.Score ch3q1score = new ReportCard.Score("2", "4", false);
        final ReportCard.Score ch3q2score = new ReportCard.Score("2", "5", false);
        final ReportCard.Score ch3q3score = new ReportCard.Score("2", "6", false);

        reportCard.addScore(ch1q1score);
        reportCard.addScore(ch1q2score);
        reportCard.addScore(ch1q3score);

        reportCard.addScore(ch2q1score);
        reportCard.addScore(ch2q2score);
        reportCard.addScore(ch2q3score);

        reportCard.addScore(ch3q1score);
        reportCard.addScore(ch3q2score);
        reportCard.addScore(ch3q3score);

        Map<String, Integer> expectedScoresByChapter = new HashMap();

        expectedScoresByChapter.put("0", 2);
        expectedScoresByChapter.put("1", 1);
        expectedScoresByChapter.put("2", 0);

        assertThat(reportCard.scoresByChapterIndex(), is(expectedScoresByChapter));
    }

    @Test
    public void shouldClearScoresForAChapterIndex() {
        ReportCard reportCard = new ReportCard();

        final ReportCard.Score ch1q1score = new ReportCard.Score("0", "4", true);
        final ReportCard.Score ch1q2score = new ReportCard.Score("0", "5", false);
        final ReportCard.Score ch1q3score = new ReportCard.Score("0", "6", true);

        final ReportCard.Score ch2q1score = new ReportCard.Score("1", "4", false);
        final ReportCard.Score ch2q2score = new ReportCard.Score("1", "5", false);
        final ReportCard.Score ch2q3score = new ReportCard.Score("1", "6", true);

        final ReportCard.Score ch3q1score = new ReportCard.Score("2", "4", false);
        final ReportCard.Score ch3q2score = new ReportCard.Score("2", "5", false);
        final ReportCard.Score ch3q3score = new ReportCard.Score("2", "6", false);

        reportCard.addScore(ch1q1score);
        reportCard.addScore(ch1q2score);
        reportCard.addScore(ch1q3score);

        reportCard.addScore(ch2q1score);
        reportCard.addScore(ch2q2score);
        reportCard.addScore(ch2q3score);

        reportCard.addScore(ch3q1score);
        reportCard.addScore(ch3q2score);
        reportCard.addScore(ch3q3score);


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

}
