package org.motechproject.ananya.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;
import org.motechproject.ananya.domain.FrontLineWorker;

import static org.mockito.Mockito.inOrder;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllCourseActionsTest {
    @Mock
    private CourseLogAction courseLogAction;
    @Mock
    private SendSMSAction sendSMSAction;
    @Mock
    private BookmarkAction bookMarkAction;
    @Mock
    private ScoreAction scoreAction;

    private AllCourseActions allCourseActions;

    @Before
    public void setUp() {
        initMocks(this);
        allCourseActions = new AllCourseActions(scoreAction, sendSMSAction, bookMarkAction, courseLogAction);
    }

    @Test
    public void shouldCallAllItsActionsToProcess() {
        FrontLineWorker frontLineWorker = new FrontLineWorker();
        CertificateCourseStateRequestList stateRequestList = new CertificateCourseStateRequestList("123-467", "123");

        allCourseActions.execute(frontLineWorker, stateRequestList);

        InOrder inOrder = inOrder(scoreAction, sendSMSAction, bookMarkAction, courseLogAction);

        inOrder.verify(scoreAction).process(frontLineWorker, stateRequestList);
        inOrder.verify(sendSMSAction).process(frontLineWorker, stateRequestList);
        inOrder.verify(bookMarkAction).process(frontLineWorker, stateRequestList);
        inOrder.verify(courseLogAction).process(frontLineWorker, stateRequestList);
    }
}
