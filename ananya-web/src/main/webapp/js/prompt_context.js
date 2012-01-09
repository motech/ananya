var PromptContext = function (metadata) {
    this.init = function(metadata) {
        this.metadata = metadata;
        this.resetCounts();
    };

    this.gotInvalidInput = function() {
        this.invalidInputCount++;
    };

    this.hasExceededMaxInvalidInputs = function() {
        return this.invalidInputCount > this.metadata.maximumInvalidInputCount;
    };

    this.gotNoInput = function() {
        this.noInputCount++;
    };

    this.hasExceededMaxNoInputs = function() {
        return this.noInputCount > this.metadata.maximumNoInputCount;
    };

    this.resetCounts = function() {
        this.invalidInputCount = 0;
        this.noInputCount = 0;
    };

    this.audioForInvalidInputRetry = function() {
        return this.metadata.audioFileBase + this.metadata.invalidInputRetryAudio;
    };

    this.audioForInvalidInputDisconnect = function() {
        return this.metadata.audioFileBase + this.metadata.invalidInputDisconnectAudio;
    };

    this.audioForNoInputRetry = function() {
        return this.metadata.audioFileBase + this.metadata.noInputRetryAudio;
    };

    this.audioForNoInputDisconnect = function() {
        return this.metadata.audioFileBase + this.metadata.noInputDisconnectAudio;
    };

    this.audioForOptionToGoToTopLevel = function() {
        return this.metadata.audioFileBase + this.metadata.optionToGoToTopLevelAudio;
    };

    this.init(metadata);
};