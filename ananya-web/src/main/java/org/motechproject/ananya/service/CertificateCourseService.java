package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.domain.ReportCard;
import org.motechproject.ananya.repository.AllCertificationCourseLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateCourseService {
    private AllCertificationCourseLogs allCertificationCourseLogs;
    private FrontLineWorkerService frontLineWorkerService;

    @Autowired
    public CertificateCourseService(AllCertificationCourseLogs allCertificationCourseLogs, FrontLineWorkerService frontLineWorkerService) {
        this.allCertificationCourseLogs = allCertificationCourseLogs;
        this.frontLineWorkerService = frontLineWorkerService;
    }

    public void saveState(CertificationCourseLog certificationCourseLog) {
        boolean wasAddedJustNow = allCertificationCourseLogs.addIfAbsent(certificationCourseLog);
        if(!wasAddedJustNow) {
           return;
        }
        final Integer chapterIndex = certificationCourseLog.getChapterIndex();
        final Integer lessonOrQuestionIndex = certificationCourseLog.getLessonOrQuestionIndex();
        final Boolean result = certificationCourseLog.isResult();
        final String callerId = certificationCourseLog.getCallerId();

        final BookMark bookMark = new BookMark(certificationCourseLog.getInteractionKey(), chapterIndex, lessonOrQuestionIndex);

        frontLineWorkerService.addBookMark(callerId, bookMark);

        final boolean interactionIsStartQuiz = "startQuiz".equals(certificationCourseLog.getInteractionKey());
        if(interactionIsStartQuiz) {
            frontLineWorkerService.resetScoresForChapterIndex(callerId, chapterIndex);
        }
        final boolean interactionIsPlayAnswerExplanation = "playAnswerExplanation".equals(certificationCourseLog.getInteractionKey());
        if(interactionIsPlayAnswerExplanation) {
            final ReportCard.Score score = new ReportCard.Score(chapterIndex.toString(), lessonOrQuestionIndex.toString(), result);
            frontLineWorkerService.addScore(callerId, score);
        }
    }
}