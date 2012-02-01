describe("Certificate course controller spec", function() {

    var metadata, course;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";
    var controller;

    beforeEach(function() {
        metadata = {
                        "audioFileBase": audioFileBase,
                        "certificateCourseAudioLocation" : certificateCourseLocation,
                        "maximumInvalidInputCount" : "2",
                        "maximumNoInputCount" : "2"
        };

        course = certificationCourseWithTwoLessonsInEveryChapter();
        controller = new CertificateCourseController(course, metadata);
    });

    it("should get the audio to be played", function () {
        expect(controller.playAudio()).toEqual("./audio/certificatecourse/Introduction.wav");
    });

    it("should initialize prompt context at start", function() {
        expect(controller.promptContext).toBeDefined();
    });

    it("should increment no input count in prompt context if there is no input", function() {
        expect(controller.promptContext.noInputCount).toEqual(0);
        controller.gotNoInput();
        expect(controller.promptContext.noInputCount).toEqual(1);
    });

    it("should return the interaction's no input interaction if no input count has exceeded max", function () {
        var interactionWithoutInput = "interactionToProceedWithoutInput";
        var currentInteraction = { continueWithoutInput : function() {return interactionWithoutInput;} };

        controller.setInteraction(currentInteraction);

        controller.gotNoInput();
        expect(controller.interaction).toEqual(currentInteraction);
        controller.gotNoInput();
        expect(controller.interaction).toEqual(currentInteraction);
        controller.gotNoInput();
        expect(controller.interaction).toEqual(interactionWithoutInput);
    });

    it("for interactions involving input, the interaction should progress to the next if input is valid.", function() {
        var interactionAfterInput = {};
        var interactionNeedingInput = {
                                        validateInput : function(input) { return true;},
                                        processInputAndReturnNextInteraction : function(input) { return interactionAfterInput; }
                                       };

        controller.setInteraction(interactionNeedingInput);
        controller.processInput(1);
        expect(controller.interaction).toEqual(interactionAfterInput);
    });
    
    it("for interactions involving input, if the input is invalid, should progress to invalid input interaction with interactionToReturnTo current interaction", function () {

        var interactionNeedingInput = {
                                        validateInput : function(input) { return false;},
                                       };
        controller.setInteraction(interactionNeedingInput);
        controller.processInput(1);
        expect(controller.interaction.nextInteraction()).toEqual(interactionNeedingInput);

    });
    
});
