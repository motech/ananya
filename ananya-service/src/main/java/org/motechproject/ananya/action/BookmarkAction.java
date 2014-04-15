package org.motechproject.ananya.action;

import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.contract.CertificateCourseStateRequest;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BookmarkAction implements CourseAction {

    private static Logger log = LoggerFactory.getLogger(BookmarkAction.class);

    @Override
    public void process(FrontLineWorker frontLineWorker, CertificateCourseStateRequestList stateRequestList) {
        String callId = stateRequestList.getCallId();
        CertificateCourseStateRequest lastRequest = stateRequestList.lastRequest();

        final BookMark bookMark = new BookMark(
                lastRequest.getInteractionKey(),
                lastRequest.getChapterIndex(),
                lastRequest.getLessonOrQuestionIndex());

        frontLineWorker.setBookMark(bookMark);
        log.info(callId + "- updated bookmark for " + frontLineWorker.getMsisdn());
    }
}
