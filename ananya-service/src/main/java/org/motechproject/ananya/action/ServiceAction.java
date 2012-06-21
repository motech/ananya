package org.motechproject.ananya.action;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.InteractionKeys;
import org.motechproject.ananya.domain.Score;
import org.motechproject.ananya.request.CertificateCourseStateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ServiceAction {

    CourseCompletion(InteractionKeys.PlayCourseResultInteraction) {
        @Override
        public void update(FrontLineWorker frontLineWorker, CertificateCourseStateRequest stateRequest) {
            frontLineWorker.incrementCertificateCourseAttempts();
            log.info("Incremented course attempts for " + frontLineWorker);
        }
    },
    StartCourse(InteractionKeys.StartCertificationCourseInteraction) {
        @Override
        public void update(FrontLineWorker frontLineWorker, CertificateCourseStateRequest stateRequest) {
            frontLineWorker.reportCard().clearAllScores();
            log.info("Cleared all scores for " + frontLineWorker);
        }
    },
    StartQuiz(InteractionKeys.StartQuizInteraction) {
        @Override
        public void update(FrontLineWorker frontLineWorker, CertificateCourseStateRequest stateRequest) {
            String chapterIndex = stateRequest.getChapterIndex().toString();
            frontLineWorker.reportCard().clearScoresForChapterIndex(chapterIndex);
            log.info("Cleared scores for chapter " + chapterIndex + " for " + frontLineWorker);
        }
    },
    PlayAnswerExplanation(InteractionKeys.PlayAnswerExplanationInteraction) {
        @Override
        public void update(FrontLineWorker frontLineWorker, CertificateCourseStateRequest stateRequest) {
            Score score = new Score(
                    stateRequest.getChapterIndex().toString(),
                    stateRequest.getLessonOrQuestionIndex().toString(),
                    stateRequest.getResult(),
                    stateRequest.getCallId());
            frontLineWorker.reportCard().addScore(score);
            log.info("Added scores for chapter for " + frontLineWorker);
        }
    },
    Default("") {
        @Override
        public void update(FrontLineWorker frontLineWorker, CertificateCourseStateRequest stateRequest) {
            //do nothing for other interactions
        }
    };

    private static Logger log = LoggerFactory.getLogger(ServiceAction.class);
    private String interactionKey;

    ServiceAction(String interactionKey) {
        this.interactionKey = interactionKey;
    }

    public static ServiceAction findFor(String interactionKey) {
        for (ServiceAction serviceAction : ServiceAction.values()) {
            if (StringUtils.endsWithIgnoreCase(serviceAction.interactionKey, interactionKey))
                return serviceAction;
        }
        return ServiceAction.Default;
    }

    public abstract void update(FrontLineWorker frontLineWorker, CertificateCourseStateRequest stateRequest);

}
