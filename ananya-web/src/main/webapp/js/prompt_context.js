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
        return this.invalidInputCount > this.metadata['maximum.invalid.input.count'];
    };

    this.gotNoInput = function() {
        this.noInputCount++;
    };

    this.hasExceededMaxNoInputs = function() {
        return this.noInputCount > this.metadata['maximum.noinput.count'];
    };

    this.resetCounts = function() {
        this.invalidInputCount = 0;
        this.noInputCount = 0;
    };

    this.audioForInvalidInputRetry = function() {
        return this.audioFileBase() + this.metadata['invalid.input.retry.audio'];
    };

    this.inputEnteredIsValid = function(input) {
        for(var i = 0; i < this.validInputs.length; i++){
           if(input == this.validInputs[i])
            return true;
        }
        return false;
    };

    this.audioFileBase = function() {
        return this.metadata['audio.url'];
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
    };

    this.getInputFromUser = function() {
        return this.inputFromUser;
    };

    this.init(metadata);
};


var ShortCode = function () {

    this.getCode = function(phoneNumber) {
        return phoneNumber.substr(5);
    };
};
