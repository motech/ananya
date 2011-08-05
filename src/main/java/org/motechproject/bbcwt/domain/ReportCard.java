package org.motechproject.bbcwt.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.ektorp.support.TypeDiscriminator;

import java.util.ArrayList;
import java.util.List;

@TypeDiscriminator("doc.documentType == 'ReportCard'")
public class ReportCard extends BaseCouchEntity {
    private String healthWorkerId;
    private List<HealthWorkerResponseToQuestion> healthWorkerResponseToQuestions = new ArrayList<HealthWorkerResponseToQuestion>();

    public ReportCard() {
    }

    public ReportCard(String healthWorkerId) {
        this.healthWorkerId = healthWorkerId;
    }

    public HealthWorkerResponseToQuestion recordResponse(final Chapter chapter, final Question question, int response) {
        HealthWorkerResponseToQuestion responseToQuestion = (HealthWorkerResponseToQuestion)CollectionUtils.find(healthWorkerResponseToQuestions, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                HealthWorkerResponseToQuestion someResponse = (HealthWorkerResponseToQuestion)o;
                return StringUtils.equals(chapter.getId(), someResponse.getChapterId()) && StringUtils.equals(question.getId(), someResponse.getQuestionId());
            }
        });

        if(responseToQuestion == null) {
            responseToQuestion = new HealthWorkerResponseToQuestion(chapter.getId(), question.getId(), response);
            healthWorkerResponseToQuestions.add(responseToQuestion);
        }
        responseToQuestion.setResponse(response);
        responseToQuestion.setCorrect(response == question.getCorrectOption());
        return responseToQuestion;
    }

    public String getHealthWorkerId() {
        return healthWorkerId;
    }

    public void setHealthWorkerId(String healthWorkerId) {
        this.healthWorkerId = healthWorkerId;
    }

    public List<HealthWorkerResponseToQuestion> getHealthWorkerResponseToQuestions() {
        return healthWorkerResponseToQuestions;
    }

    public void setHealthWorkerResponseToQuestions(List<HealthWorkerResponseToQuestion> healthWorkerResponseToQuestions) {
        this.healthWorkerResponseToQuestions = healthWorkerResponseToQuestions;
    }

    public static class HealthWorkerResponseToQuestion {
        private String chapterId;
        private String questionId;
        private int response;

        private boolean isCorrect;


        public HealthWorkerResponseToQuestion() {

        }

        public HealthWorkerResponseToQuestion(String chapterId, String questionId, int response) {
            this.chapterId = chapterId;
            this.questionId = questionId;
            this.response = response;
        }

        public String getChapterId() {
            return chapterId;
        }

        public void setChapterId(String chapterId) {
            this.chapterId = chapterId;
        }

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public int getResponse() {
            return response;
        }

        public void setResponse(int response) {
            this.response = response;
        }

        public boolean isCorrect() {
            return isCorrect;
        }

        public void setCorrect(boolean correct) {
            isCorrect = correct;
        }
    }
}