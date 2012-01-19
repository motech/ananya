var PromptContext = function (metadata) {
    this.init = function(metadata) {
        this.metadata = metadata;
        this.resetCounts();
        this.validInputs = [];
        this.inputFromUser = null;
        this.formToGoToAfterValidInput = null;
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

    this.inputEnteredIsValid = function(input) {
        for(var i = 0; i < this.validInputs.length; i++){
           if(input == this.validInputs[i])
            return true;
        }
        return false;
    };

    this.setupForReadingInputFromUser = function(formToGoToAfterValidInput, validInputs) {
        this.validInputs = validInputs;
        this.formToGoToAfterValidInput = formToGoToAfterValidInput;
    };

    this.formToProceedTo = function() {
        return this.formToGoToAfterValidInput;
    };

    this.setInputFromUser = function(input) {
        this.inputFromUser = input;
    }

    this.getInputFromUser = function() {
        return this.inputFromUser;
    }

    this.init(metadata);
};


var ShortCode = function () {

    this.getCode = function(baseNumber, phoneNumber) {
        return(phoneNumber.search(baseNumber) == 0) ? phoneNumber.replace(baseNumber, '') : '';
    };
};