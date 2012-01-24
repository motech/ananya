package org.motechproject.ananya.domain;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

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
        assertThat(reportCard.scores(), hasItems(new ScoreMatcher(score1)));
    }

    @Test
    public void shouldOverwriteScoreIfScoreExistsForTheSameQuestionInAChapter() {
        ReportCard reportCard = new ReportCard();

        ReportCard.Score scoreForChap1Que4 = new ReportCard.Score("0", "3", true);
        reportCard.addScore(scoreForChap1Que4);

        ReportCard.Score newScoreForChap1Que4 = new ReportCard.Score("0", "3", false);
        reportCard.addScore(newScoreForChap1Que4);

        assertThat(reportCard.scores().size(), is(1));
        assertThat(reportCard.scores(), hasItems(new ScoreMatcher(newScoreForChap1Que4)));
    }

    public static class ScoreMatcher extends BaseMatcher<ReportCard.Score> {
        private ReportCard.Score actualScore;
        private ReportCard.Score expectedScore;

        public ScoreMatcher(ReportCard.Score expectedScore) {
            this.expectedScore = expectedScore;
        }

        @Override
        public boolean matches(Object o) {
            actualScore = (ReportCard.Score)o;
            return actualScore.chapterIndex().equals(this.expectedScore.chapterIndex()) && actualScore.questionIndex().equals(this.expectedScore.questionIndex()) && actualScore.result() == expectedScore.result();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(String.format("Expected chapterIndex, questionIndex, result: %s, %s, %s but Got: %s, %s, %s", expectedScore.chapterIndex(), expectedScore.questionIndex(), expectedScore.result(), actualScore.chapterIndex(), actualScore.questionIndex(), actualScore.result()));
        }
    }
}
