package org.motechproject.bbcwt.matcher;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.motechproject.bbcwt.domain.ReportCard;

public class ReportCardMatcher {
    public static Matcher<ReportCard> hasResponse(final ReportCard.HealthWorkerResponseToQuestion expectedResponseToBePresent) {
        return new BaseMatcher<ReportCard>() {
            @Override
            public boolean matches(Object item) {
                if(item instanceof ReportCard){
                    ReportCard reportCard = (ReportCard) item;
                    return CollectionUtils.exists(reportCard.getHealthWorkerResponseToQuestions(), new Predicate() {
                        @Override
                        public boolean evaluate(Object o) {
                            ReportCard.HealthWorkerResponseToQuestion someResponse = (ReportCard.HealthWorkerResponseToQuestion) o;
                            return StringUtils.equals(expectedResponseToBePresent.getChapterId(), someResponse.getChapterId()) &&
                                    StringUtils.equals(expectedResponseToBePresent.getQuestionId(), someResponse.getQuestionId()) &&
                                    (expectedResponseToBePresent.getResponse() == someResponse.getResponse());
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

    public static Matcher<ReportCard> hasResponse(final ReportCard.HealthWorkerResponseToQuestion expectedResponseToBePresentAndIncorrect, final boolean markedAs) {
        return new BaseMatcher<ReportCard>() {
            @Override
            public boolean matches(Object item) {
                if(item instanceof ReportCard){
                    ReportCard reportCard = (ReportCard) item;
                    return CollectionUtils.exists(reportCard.getHealthWorkerResponseToQuestions(), new Predicate() {
                        @Override
                        public boolean evaluate(Object o) {
                            ReportCard.HealthWorkerResponseToQuestion someResponse = (ReportCard.HealthWorkerResponseToQuestion) o;
                            return StringUtils.equals(expectedResponseToBePresentAndIncorrect.getChapterId(), someResponse.getChapterId()) &&
                                    StringUtils.equals(expectedResponseToBePresentAndIncorrect.getQuestionId(), someResponse.getQuestionId()) &&
                                    (expectedResponseToBePresentAndIncorrect.getResponse() == someResponse.getResponse()) &&
                                    expectedResponseToBePresentAndIncorrect.isCorrect() == markedAs;
                        }
                    });
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Response for question : " + expectedResponseToBePresentAndIncorrect.getQuestionId() + " in chapter " + expectedResponseToBePresentAndIncorrect.getChapterId() + " with response " + expectedResponseToBePresentAndIncorrect.getResponse() + " is not present.");
            }
        };
    }
}