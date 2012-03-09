var EntryController = function(callerData , metadata) {

    var metadata = metadata;
    var callerData = callerData;

    this.decideFlowForCertificateCourse = function() {
        if (!this.isCallerRegistered())
            return "#unregistered";
        else if (this.isCallerRegistered() && callerData.bookmark.type)
            return "#registered_bookmark_present";
        else
            return "#registered_bookmark_absent";
    };

    this.decideFlowForJobAid = function(operator) {
        if (this.isCallerRegistered()){
            if(this.hasReachedMaxUsage(operator))
                return "#max_usage";
            else
                return "#registered";
        }
        else
            return "#unregistered";
    };

    this.hasReachedMaxUsage = function(operator) {
        return (callerData.hasReachedMaxUsageForMonth);
    };

    this.isCallerRegistered = function() {
        return callerData.isRegistered == "true";
    };

    this.maxUsagePrompt = function() {
        return metadata["audio.url"] +  metadata["jobaid.audio.url"] + metadata["max.usage.prompt"];
    }

    this.jobAidWelcomePrompt = function() {
        return metadata["audio.url"] +  metadata["jobaid.audio.url"] + metadata["jobaid.welcome"];
    }

    this.jobAidDetailPrompt = function() {
        return metadata["audio.url"] +  metadata["jobaid.audio.url"] + metadata["jobaid.detail"];
    }

    this.jobAidNeedToRegisterPrompt = function() {
        return metadata["audio.url"] +  metadata["jobaid.audio.url"] + metadata["jobaid.need.to.register"];
    }

    this.certificateNeedToRegisterPrompt = function() {
        return metadata["audio.url"] +  metadata["certificate.audio.url"] + metadata["certificate.need.to.register"];
    }

    this.certificateWelcomePrompt = function() {
        return metadata["audio.url"] +  metadata["certificate.audio.url"] + metadata["certificate.welcome"];
    }

    this.certificationOptionsForRegisteredPrompt = function() {
        return metadata["audio.url"] +  metadata["certificate.audio.url"] + metadata["certificate.options.for.reg"];
    }

    this.certificationOptionsForUnregisteredPrompt = function() {
        return metadata["audio.url"] +  metadata["certificate.audio.url"] + metadata["certificate.options.for.non.reg"];
    }

    this.invalidInputPrompt = function() {
        return metadata["audio.url"] +  metadata["certificate.audio.url"] + metadata["invalid.input.retry.audio"];
    }
};
