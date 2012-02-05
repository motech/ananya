describe("Prompt context", function () {
    var promptContext;

    beforeEach(function() {
        var metadata = {
            "maximum.invalid.input.count": "2",
            "maximum.noinput.count": "1",
            "audio.url": "./audio/",
            "no.input.retry.audio": "error.no.input.retry.wav",
            "no.input.disconnect.audio": "error.no.input.disconnect.wav",
            "invalid.input.retry.audio": "error.wrong.input.retry.wav",
            "invalid.input.disconnect.audio": "error.wrong.input.disconnect.wav",
            "option.to.top.level.audio": "option.to.go.to.top.level.wav"
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

    it("should validate input from user based on setUp", function() {
        var validInputs = [1, 2];

        promptContext.setupForReadingInputFromUser("welcomePromptFinished", validInputs);

        expect(promptContext.inputEnteredIsValid(1)).toEqual(true);
        expect(promptContext.inputEnteredIsValid(2)).toEqual(true);
        expect(promptContext.inputEnteredIsValid(3)).toEqual(false);
        expect(promptContext.inputEnteredIsValid(0)).toEqual(false);
    });

    it("should provide form to go to after validation", function() {
        promptContext.setupForReadingInputFromUser("welcomePromptFinished", [1, 2]);

        expect(promptContext.formToProceedTo()).toEqual("welcomePromptFinished");
    });

    it("should set input from user", function() {
        promptContext.setInputFromUser(2);

        expect(promptContext.getInputFromUser()).toEqual(2);
    });
});
