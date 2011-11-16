package org.motechproject.bbcwt.ivr;

import org.motechproject.bbcwt.ivr.jobaid.IVRAction;

import javax.servlet.http.HttpSession;

public class IVRContext {
    private String callerId;
    private String nextInteraction;
    private int invalidInputCount;
    private int noInputCount;

    private IVRAction currentIVRAction;
    private Object flowSpecificState;

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getNextInteraction() {
        return nextInteraction;
    }

    public void setNextInteraction(String nextInteraction) {
        this.nextInteraction = nextInteraction;
    }

    public int getInvalidInputCount() {
        return invalidInputCount;
    }

    public void setInvalidInputCount(int invalidInputCount) {
        this.invalidInputCount = invalidInputCount;
    }

    public int getNoInputCount() {
        return noInputCount;
    }

    public void setNoInputCount(int noInputCount) {
        this.noInputCount = noInputCount;
    }

    public void resetInvalidInputCount() {
        invalidInputCount = 0;
    }

    public void resetNoInputCount() {
        noInputCount = 0;
    }

    public void incrementInvalidInputCount() {
        invalidInputCount ++;
    }

    public void incrementNoInputCount() {
        noInputCount ++;
    }

    public void setCurrentIVRAction(IVRAction currentIVRAction) {
        this.currentIVRAction = currentIVRAction;
    }

    public IVRAction currentIVRAction() {
        return this.currentIVRAction;
    }

    public Object flowSpecificState() {
        return flowSpecificState;
    }

    public void setFlowSpecificState(Object flowSpecificState) {
        this.flowSpecificState = flowSpecificState;
    }

    public static class SessionAndIVRContextSynchronizer {
        public void synchronizeSessionWithIVRContext(HttpSession session, IVRContext context) {
            session.setAttribute(IVR.Attributes.CALLER_ID, context.getCallerId());

            session.setAttribute(IVR.Attributes.NEXT_INTERACTION, context.getNextInteraction());

            session.setAttribute(IVR.Attributes.INVALID_INPUT_COUNT, context.getInvalidInputCount());

            session.setAttribute(IVR.Attributes.NO_INPUT_COUNT, context.getNoInputCount());

            session.setAttribute(IVR.Attributes.CURRENT_IVR_ACTION, context.currentIVRAction());

            session.setAttribute(IVR.Attributes.FLOW_SPECIFIC_STATE, context.flowSpecificState());
        }

        public IVRContext buildIVRContext(HttpSession session) {
            IVRContext ivrContext = new IVRContext();

            ivrContext.setCallerId((String)session.getAttribute(IVR.Attributes.CALLER_ID));

            ivrContext.setNextInteraction((String)session.getAttribute(IVR.Attributes.NEXT_INTERACTION));

            final Integer invalidInputCount = (Integer) session.getAttribute(IVR.Attributes.INVALID_INPUT_COUNT);
            ivrContext.setInvalidInputCount(invalidInputCount == null ? 0 : invalidInputCount);

            final Integer noInputCount = (Integer) session.getAttribute(IVR.Attributes.NO_INPUT_COUNT);
            ivrContext.setNoInputCount(noInputCount == null ? 0 : noInputCount);

            ivrContext.setCurrentIVRAction((IVRAction)session.getAttribute(IVR.Attributes.CURRENT_IVR_ACTION));
            ivrContext.setFlowSpecificState(session.getAttribute(IVR.Attributes.FLOW_SPECIFIC_STATE));

            return ivrContext;
        }
    }
}