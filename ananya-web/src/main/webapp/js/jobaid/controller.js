var JobAidController = function(callerData , metadata, callContext) {

    var metadata = metadata;
    var callerData = callerData;
    var callContext = callContext;
    var startTime = new Date().valueOf();

    var MILLISECONDS_PER_MINUTE = 60000

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
        return (callerData.currentJobAidUsage >= callerData.maxAllowedUsageForOperator);
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

    this.timeRemainingFileBase = function() {
        return this.audioFileBase() + metadata["jobaid.time.remaining.url"];
    }

    this.currentCallDuration = function() {
        return new Date().valueOf() - startTime;
    }

    this.totalCallDuration = function() {
        return callerData.currentJobAidUsage + this.currentCallDuration();
    }

    this.remainingCallDuration = function() {
        return callerData.maxAllowedUsageForOperator - this.totalCallDuration();
    }

    this.remainingCallDurationAsRoundedMinutes = function() {
        return Math.floor(this.remainingCallDuration() / MILLISECONDS_PER_MINUTE);
    }

    this.playRemainingTimePrompt = function() {
        return this.timeRemainingFileBase() + this.remainingCallDurationAsRoundedMinutes() + ".wav";
    }

    this.playRemainingTimePromptStart = function() {
        return this.timeRemainingFileBase() + metadata["jobaid.time.remaining.start"];
    }
};
