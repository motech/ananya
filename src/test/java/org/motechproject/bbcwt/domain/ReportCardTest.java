package org.motechproject.bbcwt.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.util.UUIDUtil;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

public class ReportCardTest {
    private Chapter chapter;
    private Question question1;
    private Question question2;
    private ReportCard reportCard;

    @Before
    public void setUp() {

        chapter = new Chapter(1);
        chapter.setId(UUIDUtil.newUUID());
        question1 = new Question(1, null, null, -1, null, null);
        question2 = new Question(2, null, null, -1, null, null);
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


    private Matcher<ReportCard> hasResponse(final ReportCard.HealthWorkerResponseToQuestion expectedResponseToBePresent) {
        return new BaseMatcher<ReportCard>() {
            @Override
            public boolean matches(Object item) {
                if(item instanceof ReportCard){
                    ReportCard reportCard = (ReportCard) item;
                    return CollectionUtils.exists(reportCard.getHealthWorkerResponseToQuestions(), new Predicate() {
                        @Override
                        public boolean evaluate(Object o) {
                            ReportCard.HealthWorkerResponseToQuestion someResponse = (ReportCard.HealthWorkerResponseToQuestion)o;
                            return StringUtils.equals(expectedResponseToBePresent.getChapterId(), someResponse.getChapterId()) &&
                                   StringUtils.equals(expectedResponseToBePresent.getQuestionId(), someResponse.getQuestionId()) &&
                                   (expectedResponseToBePresent.getResponse() == someResponse.getResponse()) ;
                        }
                    });
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Response for question : " + expectedResponseToBePresent.getQuestionId() + " in chapter " + expectedResponseToBePresent.getChapterId() + " with response " + expectedResponseToBePresent.getResponse() + " is not present.");
            }
        };
    }
}