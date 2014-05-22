package org.motechproject.ananya.action;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class AllCourseActions {

    private List<CourseAction> courseActions;

    @Autowired
    public AllCourseActions(
            ScoreAction scoreAction,
            SendSMSAction sendSMSAction,
            BookmarkAction bookmarkAction,
            CourseLogAction courseLogAction) {
        courseActions = Arrays.asList(scoreAction, sendSMSAction, bookmarkAction, courseLogAction);
    }

    public void execute(FrontLineWorker frontLineWorker, CertificateCourseStateRequestList stateRequestList) {
        for (CourseAction courseAction : courseActions)
            courseAction.process(frontLineWorker, stateRequestList);
    }
}
