package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllCertificationCourseLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateCourseService {
    private AllCertificationCourseLogs allCertificationCourseLogs;
    private FrontLineWorkerService frontLineWorkerService;

    @Autowired
    public CertificateCourseService(
            AllCertificationCourseLogs allCertificationCourseLogs, FrontLineWorkerService frontLineWorkerService) {
        this.allCertificationCourseLogs = allCertificationCourseLogs;
        this.frontLineWorkerService = frontLineWorkerService;
    }
    
    public void saveBookmark(CertificationCourseStateRequest courseStateRequest) {
        final BookMark bookMark = new BookMark(courseStateRequest.getInteractionKey(),
                courseStateRequest.getChapterIndex(), courseStateRequest.getLessonOrQuestionIndex());

        frontLineWorkerService.addBookMark(courseStateRequest.getCallerId(), bookMark);
    }

    private void saveScores(final CertificationCourseStateRequest courseStateRequest) {
        final Integer chapterIndex = courseStateRequest.getChapterIndex();
        final Integer lessonOrQuestionIndex = courseStateRequest.getLessonOrQuestionIndex();
        final Boolean result = courseStateRequest.isResult();

        final boolean interactionIsStartQuiz = "startQuiz".equals(courseStateRequest.getInteractionKey());
        if(interactionIsStartQuiz) {
            frontLineWorkerService.resetScoresForChapterIndex(courseStateRequest.getCallerId(), chapterIndex);
        }
        final boolean interactionIsPlayAnswerExplanation = "playAnswerExplanation".equals(courseStateRequest.getInteractionKey());
        if(interactionIsPlayAnswerExplanation) {
            final ReportCard.Score score = new ReportCard.Score(chapterIndex.toString(),
                    lessonOrQuestionIndex.toString(), result, courseStateRequest.getCallId());
            frontLineWorkerService.addScore(courseStateRequest.getCallerId(), score);
        }
    }
    
    public void saveState(List<CertificationCourseStateRequest> certificationCourseStateRequestCollection) {

        if (certificationCourseStateRequestCollection == null || certificationCourseStateRequestCollection.size() == 0) return;

        CertificationCourseLog courseLogDocument;
        CertificationCourseStateRequest courseStateRequest = certificationCourseStateRequestCollection.get(0);
        final String callId = courseStateRequest.getCallId();

        courseLogDocument = allCertificationCourseLogs.findByCallId(callId);
        if (courseLogDocument == null) {
            courseLogDocument = new CertificationCourseLog(courseStateRequest.getCallerId(),
                    courseStateRequest.getCalledNumber(), null, null, "", callId, courseStateRequest.getCertificateCourseId()
            );
            allCertificationCourseLogs.add(courseLogDocument);
        }

        for (CertificationCourseStateRequest certificationCourseStateRequest : certificationCourseStateRequestCollection) {
            saveScores(certificationCourseStateRequest);

            // Only a bookmark, not a log item. continue
            if (!StringUtils.isBlank(certificationCourseStateRequest.getContentId())) {
                CertificationCourseLogItem courseLogItem = new CertificationCourseLogItem(
                        certificationCourseStateRequest.getContentId(), certificationCourseStateRequest.getContentType(),
                        certificationCourseStateRequest.getContentData(), CourseItemState.valueOf(certificationCourseStateRequest.getCourseItemState().toUpperCase())
                );

                courseLogDocument.addCourseLogItem(courseLogItem);
            }
        }
        allCertificationCourseLogs.update(courseLogDocument);

        CertificationCourseStateRequest recentCourseRequest =
                certificationCourseStateRequestCollection.get(certificationCourseStateRequestCollection.size() - 1);
        saveBookmark(recentCourseRequest);
    }
}