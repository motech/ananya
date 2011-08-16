package org.motechproject.bbcwt.domain;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.util.UUIDUtil;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.motechproject.bbcwt.matcher.ReportCardMatcher.hasResponse;

public class ReportCardTest {
    private Chapter chapter;
    private Question question1;
    private Question question2;
    private ReportCard reportCard;

    @Before
    public void setUp() {

        chapter = new Chapter(1);
        chapter.setId(UUIDUtil.newUUID());
        question1 = new Question(1, null, null, 1, null, null);
        question2 = new Question(2, null, null, 2, null, null);
        chapter.addQuestion(question1);
        chapter.addQuestion(question2);
        reportCard = new ReportCard();

    }

    @Test
    public void recordResponseShouldCreateANewResponseIfThereIsNoneForTheQuestion() {
        reportCard.recordResponse(chapter, question1, 1);
        ReportCard.HealthWorkerResponseToQuestion expectedResponse1ToBePresent = new ReportCard.HealthWorkerResponseToQuestion(chapter.getId(), question1.getId(), 1);


        reportCard.recordResponse(chapter, question2, 2);
        ReportCard.HealthWorkerResponseToQuestion expectedResponse2ToBePresent = new ReportCard.HealthWorkerResponseToQuestion(chapter.getId(), question2.getId(), 2);

        assertThat(reportCard, hasResponse(expectedResponse1ToBePresent));
        assertThat(reportCard, hasResponse(expectedResponse2ToBePresent));
    }

    @Test
    public void recordResponseShouldOverwriteOldResponseForAQuestion() {
        reportCard.recordResponse(chapter, question1, 1);
        reportCard.recordResponse(chapter, question1, 2);

        ReportCard.HealthWorkerResponseToQuestion oldResponse = new ReportCard.HealthWorkerResponseToQuestion(chapter.getId(), question1.getId(), 1);
        ReportCard.HealthWorkerResponseToQuestion newResponse = new ReportCard.HealthWorkerResponseToQuestion(chapter.getId(), question1.getId(), 2);

        assertThat(reportCard, hasResponse(newResponse));
        assertThat(reportCard, not(hasResponse(oldResponse)));
    }

    @Test
    public void recordResponseShouldMarkResponseAsCorrectOrIncorrectAppropriately() {
        reportCard.recordResponse(chapter, question1, 1);
        reportCard.recordResponse(chapter, question2, 1);

        ReportCard.HealthWorkerResponseToQuestion correctResponse = new ReportCard.HealthWorkerResponseToQuestion(chapter.getId(), question1.getId(), 1);
        correctResponse.setCorrect(true);
        ReportCard.HealthWorkerResponseToQuestion incorrectResponse = new ReportCard.HealthWorkerResponseToQuestion(chapter.getId(), question2.getId(), 1);
        incorrectResponse.setCorrect(false);

        assertThat(reportCard, hasResponse(correctResponse, true));
        assertThat(reportCard, hasResponse(incorrectResponse, false));
    }

    @Test
    public void scoreEarnedShouldReportScoreForRequestedChapter() {
        Chapter chapter2 = new Chapter(2);
        chapter2.setId(UUIDUtil.newUUID());
        Question ch2q1 = new Question(1, null, null, 1, null, null);
        Question ch2q2 = new Question(2, null, null, 1, null, null);
        Question ch2q3 = new Question(3, null, null, 2, null, null);
        chapter2.addQuestion(ch2q1);
        chapter2.addQuestion(ch2q2);
        chapter2.addQuestion(ch2q3);

        reportCard.recordResponse(chapter, question1, 1);
        reportCard.recordResponse(chapter, question2, 2);
        reportCard.recordResponse(chapter2, ch2q1, 1);
        reportCard.recordResponse(chapter2, ch2q2, 2);
        reportCard.recordResponse(chapter2, ch2q3, 2);

        ReportCard.ScoreSummary ch1ScoreSummary = reportCard.scoreEarned(chapter);
        assertEquals("Incorrect maximum marks: ", ch1ScoreSummary.getMaximumMarks(), 2);
        assertEquals(ch1ScoreSummary.getScoredMarks(), 2);

        ReportCard.ScoreSummary ch2ScoreSummary = reportCard.scoreEarned(chapter2);
        assertEquals(ch2ScoreSummary.getMaximumMarks(), 3);
        assertEquals(ch2ScoreSummary.getScoredMarks(), 2);
    }
}