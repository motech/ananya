describe("Prompt context", function () {
    var promptContext;

    beforeEach(function() {
        var metadata = {
            "maximumInvalidInputCount": "2",
            "maximumNoInputCount": "1",
            "audioFileBase": "./audio/",
            "noInputRetryAudio": "error.no.input.retry.wav",
            "noInputDisconnectAudio": "error.no.input.disconnect.wav",
            "invalidInputRetryAudio": "error.wrong.input.retry.wav",
            "invalidInputDisconnectAudio": "error.wrong.input.disconnect.wav",
            "optionToGoToTopLevelAudio": "option.to.go.to.top.level.wav",
        };
        promptContext = new PromptContext(metadata);
    });

    it("should know when the maximum invalid-inputs have been exceeded", function () {
        expect(promptContext.hasExceededMaxInvalidInputs()).toEqual(false);

        promptContext.gotInvalidInput();
        expect(promptContext.hasExceededMaxInvalidInputs()).toEqual(false);

        promptContext.gotInvalidInput();
        expect(promptContext.hasExceededMaxInvalidInputs()).toEqual(false);

        promptContext.gotInvalidInput();
        expect(promptContext.hasExceededMaxInvalidInputs()).toEqual(true);
    });

    it("should know when the maximum no-inputs have been exceeded", function () {
        expect(promptContext.hasExceededMaxNoInputs()).toEqual(false);

        promptContext.gotNoInput();
        expect(promptContext.hasExceededMaxNoInputs()).toEqual(false);

        promptContext.gotNoInput();
        expect(promptContext.hasExceededMaxNoInputs()).toEqual(true);
    });

    it("should provide the URLs for the audio files for the invalid input conditions", function() {
        expect(promptContext.audioForInvalidInputRetry()).toEqual("./audio/error.wrong.input.retry.wav")
        expect(promptContext.audioForInvalidInputDisconnect()).toEqual("./audio/error.wrong.input.disconnect.wav")
    });

    it("should provide the URLs for the audio files for the no-input conditions", function() {
        expect(promptContext.audioForNoInputRetry()).toEqual("./audio/error.no.input.retry.wav")
        expect(promptContext.audioForNoInputDisconnect()).toEqual("./audio/error.no.input.disconnect.wav")
    });

    it("should provide the URLs for the audio files for the top-level menu option", function() {
        expect(promptContext.audioForOptionToGoToTopLevel()).toEqual("./audio/option.to.go.to.top.level.wav")
    });
});