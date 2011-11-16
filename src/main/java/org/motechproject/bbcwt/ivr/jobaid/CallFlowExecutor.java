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
    public CallFlowExecutor(@Qualifier("welcome") IVRAction action, IVRMessage ivrMessages) {
        this.startAction = action;
        this.ivrMessages = ivrMessages;
    }

    public IVRResponseBuilder execute(IVRContext context, IVRRequest request) {
        LOGGER.info("CallerID: " + context.getCallerId() + ", with SessionID: " + request.getSid() + " with Event: " + request.getEvent() + " and data: " + request.getData());

        IVRResponseBuilder responseBuilder = new IVRResponseBuilder();
        IVRDtmfBuilder dtmfBuilder = new IVRDtmfBuilder();
        dtmfBuilder.withTimeOutInMillis(8000);

        IVRAction actionUnderExecution = null, actionToExecute;

        if(newCall(request) || userRequestedToStartAllOverAgain(request)) {
            LOGGER.info(String.format("Starting user call flow with the new action: %s", startAction));
            startAction.processRequest(context, request, responseBuilder);
            startAction.playPrompt(context, request, dtmfBuilder);

            context.setFlowSpecificState(new JobAidFlowState());
            context.setCurrentIVRAction(startAction);
        }
        else {
           if(userHasHangedUp(request)) {
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

    private boolean userHasHangedUp(IVRRequest request) {
        return "hangup".equalsIgnoreCase(request.getEvent());
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
                IVRAction nextAction = actionUnderExecution.processAndForwardToNextState(context, request);

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

                if(context.getNoInputCount() > allowedNumberOfNoInputs) {
                    responseBuilder.withHangUp();
                    return;
                }

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
                context.incrementNoInputCount();

                int allowedNumberOfInvalidInputs = Integer.parseInt(ivrMessages.get(IVRMessage.ALLOWED_NUMBER_OF_INVALID_INPUTS));

                if(context.getInvalidInputCount() > allowedNumberOfInvalidInputs) {
                    responseBuilder.withHangUp();
                    return;
                }

                responseBuilder.addPlayAudio(ivrMessages.absoluteFileLocation(ivrMessages.get(IVRMessage.INVALID_INPUT)));
                actionUnderExecution.playPrompt(context, request, dtmfBuilder);
            }
        };

        public abstract void postValidationInteraction(IVRAction actionLastExecuted, IVRContext context, IVRRequest request, IVRResponseBuilder responseBuilder, IVRDtmfBuilder dtmfBuilder, IVRMessage ivrMessages);
    }
}

