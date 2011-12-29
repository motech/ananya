package org.motechproject.bbcwt.ivr.jobaid;

import org.apache.log4j.Logger;
import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CallFlowExecutor {
    private static final Logger LOGGER = Logger.getLogger(CallFlowExecutor.class);

    private IVRAction startAction;
    private IVRMessage ivrMessages;

    @Autowired
    public CallFlowExecutor(@Qualifier("playWelcome") IVRAction action, IVRMessage ivrMessages) {
        this.startAction = action;
        this.ivrMessages = ivrMessages;
    }

    public IVRResponseBuilder execute(IVRContext context, IVRRequest request) {
        LOGGER.info("CallerID: " + context.getCallerId() + "/" + request.getCid() + ", with SessionID: " + request.getSid() + " with Event: " + request.getEvent() + " and data: " + request.getData());

        IVRResponseBuilder responseBuilder = new IVRResponseBuilder();
        IVRDtmfBuilder dtmfBuilder = new IVRDtmfBuilder();
        dtmfBuilder.withTimeOutInMillis(8000);

        IVRAction actionUnderExecution = null;

        context.setFirstInteractionInCall(context.currentIVRAction() == null);

        if(newCall(request) || userRequestedToStartAllOverAgain(request)) {
            LOGGER.info(String.format("Starting user call flow with the new action: %s", startAction));

            context.resetInvalidInputCount();
            context.resetNoInputCount();
            if(request.getCid()!=null) {
                context.setCallerId(request.getCid());
            }
            context.setFlowSpecificState(new JobAidFlowState());
            startAction.processRequest(context, request, responseBuilder);
            startAction.playPrompt(context, request, dtmfBuilder);

            context.setCurrentIVRAction(startAction);
        }
        else {
           if(userHasHungUpOrCallIsDisconnected(request)) {
               LOGGER.info("User has hung up.");
               return null;
           }
           else {
               actionUnderExecution = context.currentIVRAction();

               LOGGER.info(String.format("%s messages and prompts have been played. Will continue with validating input...", actionUnderExecution));

               ProcessStatus validationStatus = actionUnderExecution.validateInput(context, request);

               LOGGER.info(String.format("Validation state is: %s", validationStatus));

               validationStatus.postValidationInteraction(
                       actionUnderExecution,
                       context,
                       request,
                       responseBuilder,
                       dtmfBuilder,
                       ivrMessages);

           }
        }
        responseBuilder.withCollectDtmf(dtmfBuilder.create());
        return responseBuilder;
    }

    private boolean userHasHungUpOrCallIsDisconnected(IVRRequest request) {
        final String event = request.getEvent();
        return "hangup".equalsIgnoreCase(event) || "disconnect".equalsIgnoreCase(event);
    }

    private boolean userRequestedToStartAllOverAgain(IVRRequest request) {
        final boolean userHasRequestToStartAllOverAgain = "0".equalsIgnoreCase(request.getData());
        if(userHasRequestToStartAllOverAgain) {
            LOGGER.info("User has requested to start all over again.");
        }
        return userHasRequestToStartAllOverAgain;
    }

    private boolean newCall(IVRRequest request) {
        final boolean newCall = "newcall".equalsIgnoreCase(request.getEvent());
        if(newCall) {
            LOGGER.info("This is a new call.");
        }
        return newCall;
    }


    public static enum ProcessStatus {
        OK {
            @Override
            public void postValidationInteraction(IVRAction actionUnderExecution,
                                                  IVRContext context, IVRRequest request,
                                                  IVRResponseBuilder responseBuilder,
                                                  IVRDtmfBuilder dtmfBuilder, IVRMessage ivrMessages) {
                LOGGER.info("Continuing with input processing...");
                IVRAction nextAction = actionUnderExecution.processAndForwardToNextState(context, request, responseBuilder);

                context.resetInvalidInputCount();
                context.resetNoInputCount();
                context.setCurrentIVRAction(nextAction);

                LOGGER.info(String.format("Carrying on with the next action :%s", nextAction));
                nextAction.processRequest(context, request, responseBuilder);
                nextAction.playPrompt(context, request, dtmfBuilder);

            }
        },

        NO_IP {
            @Override
            public void postValidationInteraction(IVRAction actionUnderExecution,
                                                  IVRContext context,
                                                  IVRRequest request,
                                                  IVRResponseBuilder responseBuilder,
                                                  IVRDtmfBuilder dtmfBuilder, IVRMessage ivrMessages) {
                LOGGER.info(String.format("No input received for prompts in action: %s", actionUnderExecution));
                context.incrementNoInputCount();

                int allowedNumberOfNoInputs = Integer.parseInt(ivrMessages.get(IVRMessage.ALLOWED_NUMBER_OF_NO_INPUTS));

                LOGGER.info(String.format("No input received for %d times", context.getNoInputCount()));

                if(context.getNoInputCount() > allowedNumberOfNoInputs) {
                    LOGGER.info("Exceeded max number of allowed no inputs, exiting.");
                    responseBuilder.withHangUp();
                    return;
                }

                LOGGER.info("Giving user a chance to retry.");
                actionUnderExecution.playPrompt(context, request, dtmfBuilder);
            }
        },

        INVALID_IP {
            @Override
            public void postValidationInteraction(IVRAction actionUnderExecution,
                                                  IVRContext context,
                                                  IVRRequest request,
                                                  IVRResponseBuilder responseBuilder,
                                                  IVRDtmfBuilder dtmfBuilder, IVRMessage ivrMessages) {
                context.incrementInvalidInputCount();

                int allowedNumberOfInvalidInputs = Integer.parseInt(ivrMessages.get(IVRMessage.ALLOWED_NUMBER_OF_INVALID_INPUTS));

                LOGGER.info(String.format("Invalid input received for %d times", context.getInvalidInputCount()));

                if(context.getInvalidInputCount() > allowedNumberOfInvalidInputs) {
                    LOGGER.info("Exceeded max number of allowed invalid inputs, exiting.");
                    responseBuilder.withHangUp();
                    return;
                }

                LOGGER.info("Giving user a chance to retry.");
                responseBuilder.addPlayAudio(ivrMessages.absoluteFileLocation(ivrMessages.get(IVRMessage.INVALID_INPUT)));
                actionUnderExecution.playPrompt(context, request, dtmfBuilder);
            }
        };

        public abstract void postValidationInteraction(IVRAction actionLastExecuted, IVRContext context, IVRRequest request, IVRResponseBuilder responseBuilder, IVRDtmfBuilder dtmfBuilder, IVRMessage ivrMessages);
    }
}
