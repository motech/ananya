var JobAidController = function(callerData , metadata, callContext) {

    var metadata = metadata;
    var callerData = callerData;
    var callContext = callContext;

    this.decideFlowForJobAid = function(operator) {

        if(this.hasReachedMaxUsage(operator))
            return "#max_usage";

        if (callContext.dialedViaShortCode) {
            if (!this.isCallerRegistered() && !this.hasHeardUnregisteredWelcomeMessage())
                return "#partially_registered";
            if (this.isCallerRegistered() && !this.hasHeardRegisteredWelcomeMessage())
                return "#registered";
            return "#controller";
        }

        if (this.isCallerRegistered())
            return "#registered";
        else
            return "#partially_registered";
    };

    this.hasReachedMaxUsage = function() {
        return (callerData.hasReachedMaxUsageForMonth);
    };

    this.isCallerRegistered = function() {
        return callerData.isRegistered == "true";
    };

    this.hasHeardUnregisteredWelcomeMessage = function() {
        return callerData.promptsHeard["unregistered_welcome_message"] > 0;
    };

    this.hasHeardRegisteredWelcomeMessage = function() {
        return callerData.promptsHeard["registered_welcome_message"] > 0;
    };

    this.audioFileBase = function() {
        return metadata['audio.url']+metadata['jobaid.audio.url'];
    };

    this.jobAidWelcomePromptRegistered = function() {
        return this.audioFileBase() + metadata["jobaid.welcome.registered"];
    }

    this.jobAidWelcomePromptPartiallyRegistered = function() {
        return this.audioFileBase() + metadata["jobaid.welcome.partially.registered"];
    }

    this.maxUsagePrompt = function() {
        return this.audioFileBase() + metadata["max.usage.prompt"];
    }
};
