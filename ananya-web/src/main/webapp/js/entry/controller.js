var EntryController = function(callerData , metaData , pathToRoot) {

    var pathToRoot = pathToRoot;
    var metaData = metaData;
    var callerData = callerData;

    this.decideFlowForCertificateCourse = function() {
        if (!this.isCallerRegistered())
            return "#unregistered";
        else if (this.isCallerRegistered() && callerData.bookmark.type)
            return "#registered_bookmark_present";
        else
            return "#registered_bookmark_absent";
    };

    this.decideFlowForJobAid = function() {
        if (this.isCallerRegistered())
            return "#registered";
        else
            return "#unregistered";
    };

    this.isCallerRegistered = function() {
        return callerData.isRegistered == "true";
    };

    this.jobAidWelcomePrompt = function() {
        return Utility.resourceUrl(pathToRoot,this.metadata["audio.url"] +  metadata["jobaid.audio.url"] + metadata["jobaid.welcome"]);
    }

    this.jobAidDetailPrompt = function() {
        return Utility.resourceUrl(pathToRoot,this.metadata["audio.url"] +  metadata["jobaid.audio.url"] + metadata["jobaid.detail"]);
    }

    this.jobAidNeedToRegisterPrompt = function() {
        return Utility.resourceUrl(pathToRoot,this.metadata["audio.url"] +  metadata["jobaid.audio.url"] + metadata["jobaid.need.to.register"]);
    }

    this.certificateNeedToRegisterPrompt = function() {
        return Utility.resourceUrl(pathToRoot,this.metadata["audio.url"] +  metadata["certificate.audio.url"] + metadata["certificate.need.to.register"]);
    }

    this.certificateWelcomePrompt = function() {
        return Utility.resourceUrl(pathToRoot,this.metadata["audio.url"] +  metadata["certificate.audio.url"] + metadata["certificate.welcome"]);
    }

    this.certificationOptionsForRegisteredPrompt = function() {
        return Utility.resourceUrl(pathToRoot,this.metadata["audio.url"] +  metadata["certificate.audio.url"] + metadata["certificate.options.for.reg"]);
    }

    this.certificationOptionsForUnregisteredPrompt = function() {
        return Utility.resourceUrl(pathToRoot,this.metadata["audio.url"] +  metadata["certificate.audio.url"] + metadata["certificate.options.for.non.reg"]);
    }

    this.invalidInputPrompt = function() {
        return Utility.resourceUrl(pathToRoot,this.metadata["audio.url"] +  metadata["certificate.audio.url"] + metadata["invalid.input.retry.audio"]);
    }

    this.register = function() {
        return
    }
};
