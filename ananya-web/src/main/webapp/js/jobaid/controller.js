var JobAidController = function(callerData , metadata) {

    var metadata = metadata;
    var callerData = callerData;

    this.decideFlowForJobAid = function(operator) {
        if (this.isCallerRegistered()){
            if(this.hasReachedMaxUsage(operator))
                return "#max_usage";
            else
                return "#registered";
        }
        else
            return "#partially_registered";
    };

    this.hasReachedMaxUsage = function() {
        return (callerData.hasReachedMaxUsageForMonth);
    };

    this.isCallerRegistered = function() {
        return callerData.isRegistered == "true";
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
