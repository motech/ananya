package org.motechproject.ananya.action;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.request.CertificateCourseStateRequestList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class AllCourseActions {

    private List<CourseAction> courseActions;

    @Autowired
    public AllCourseActions(BookmarkAction bookmarkAction,
                            ScoreAction scoreAction,
                            SendSMSAction sendSMSAction,
                            CourseLogAction courseLogAction) {
        courseActions = Arrays.asList(bookmarkAction, scoreAction, sendSMSAction, courseLogAction);
    }

    public void execute(FrontLineWorker frontLineWorker, CertificateCourseStateRequestList stateRequestList) {
        for (CourseAction courseAction : courseActions)
            courseAction.process(frontLineWorker, stateRequestList);
    }
}
