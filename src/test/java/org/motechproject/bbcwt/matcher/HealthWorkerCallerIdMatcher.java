package org.motechproject.bbcwt.matcher;

import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;
import org.motechproject.bbcwt.domain.HealthWorker;

public class HealthWorkerCallerIdMatcher extends ArgumentMatcher<HealthWorker> {
    private String callerIdToMatch;
    private String calledWith;

    public HealthWorkerCallerIdMatcher(String callerIdToMatch) {
        this.callerIdToMatch = callerIdToMatch;
    }

    @Override
    public boolean matches(Object arg) {
        if(arg instanceof HealthWorker) {
            HealthWorker arg1 = (HealthWorker) arg;
            calledWith = arg1.getCallerId();
            return calledWith.matches(callerIdToMatch);
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Expected the health worker to have caller ID: " + callerIdToMatch + " but has been called with: " + calledWith);
    }
}
