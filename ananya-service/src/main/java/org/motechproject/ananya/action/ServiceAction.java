package org.motechproject.ananya.action;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.InteractionKeys;
import org.motechproject.ananya.domain.Score;
import org.motechproject.ananya.request.CertificationCourseStateRequest;

public enum ServiceAction {
    CourseCompletion(InteractionKeys.PlayCourseResultInteraction) {
        @Override
        public void update(FrontLineWorker frontLineWorker, CertificationCourseStateRequest stateRequest) {
            frontLineWorker.incrementCertificateCourseAttempts();
        }
    },
    StartCourse(InteractionKeys.StartCertificationCourseInteraction) {
        @Override
        public void update(FrontLineWorker frontLineWorker, CertificationCourseStateRequest stateRequest) {
            frontLineWorker.reportCard().clearAllScores();
        }
    },
    StartQuiz(InteractionKeys.StartQuizInteraction) {
        @Override
        public void update(FrontLineWorker frontLineWorker, CertificationCourseStateRequest stateRequest) {
            String chapterIndex = stateRequest.getChapterIndex().toString();
            frontLineWorker.reportCard().clearScoresForChapterIndex(chapterIndex);
        }
    },
    PlayAnswerExplanation(InteractionKeys.PlayAnswerExplanationInteraction) {
        @Override
        public void update(FrontLineWorker frontLineWorker, CertificationCourseStateRequest stateRequest) {
            Score score = new Score(
                    stateRequest.getChapterIndex().toString(),
                    stateRequest.getLessonOrQuestionIndex().toString(),
                    stateRequest.getResult(),
                    stateRequest.getCallId());
            frontLineWorker.reportCard().addScore(score);
        }
    };

    private String interactionKey;

    ServiceAction(String interactionKey) {
        this.interactionKey = interactionKey;
    }

    public static ServiceAction findFor(String interactionKey) {
        ServiceAction.values();
        for (ServiceAction serviceAction : ServiceAction.values()) {
            if (StringUtils.endsWithIgnoreCase(serviceAction.interactionKey, interactionKey))
                return serviceAction;
        }
        return null;
    }

    public abstract void update(FrontLineWorker frontLineWorker, CertificationCourseStateRequest stateRequest);

}
