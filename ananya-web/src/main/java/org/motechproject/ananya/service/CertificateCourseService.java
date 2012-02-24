package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.CertificationCourseBookmark;
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

    public void saveState(CertificationCourseBookmark certificationCourseBookmark) {
//        boolean wasAddedJustNow = allCertificationCourseLogs.addIfAbsent(certificationCourseBookmark);
//        if(!wasAddedJustNow) {
//           return;
//        }
        final Integer chapterIndex = certificationCourseBookmark.getChapterIndex();
        final Integer lessonOrQuestionIndex = certificationCourseBookmark.getLessonOrQuestionIndex();
        final Boolean result = certificationCourseBookmark.isResult();
        final String callerId = certificationCourseBookmark.getCallerId();

        final BookMark bookMark = new BookMark(certificationCourseBookmark.getInteractionKey(), chapterIndex, lessonOrQuestionIndex);

        frontLineWorkerService.addBookMark(callerId, bookMark);

        final boolean interactionIsStartQuiz = "startQuiz".equals(certificationCourseBookmark.getInteractionKey());
        if(interactionIsStartQuiz) {
            frontLineWorkerService.resetScoresForChapterIndex(callerId, chapterIndex);
        }
        final boolean interactionIsPlayAnswerExplanation = "playAnswerExplanation".equals(certificationCourseBookmark.getInteractionKey());
        if(interactionIsPlayAnswerExplanation) {
            final ReportCard.Score score = new ReportCard.Score(chapterIndex.toString(), lessonOrQuestionIndex.toString(), result);
            frontLineWorkerService.addScore(callerId, score);
        }
    }
}