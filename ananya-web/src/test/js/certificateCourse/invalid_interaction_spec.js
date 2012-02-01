describe("Invalid Interaction", function(){

    var metadata, invalidInteraction, previousInteractionNeedingInput = {};
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";
    var retryAudio = "0000_error_in_pressing_number.wav";

    beforeEach(function() {
        metadata = {"audioFileBase": audioFileBase, "certificateCourseAudioLocation" : certificateCourseLocation, "invalidInputRetryAudio" : retryAudio};
        invalidInteraction = new InvalidInputInteraction(previousInteractionNeedingInput, metadata);
    });

    it("should return invalid button press audio when audio is played", function () {
        expect(invalidInteraction.playAudio()).toEqual("./audio/" + retryAudio);
    });

    it("should return the previous interaction when nextAction is requested", function () {
        expect(invalidInteraction.nextInteraction()).toEqual(previousInteractionNeedingInput);
    });

    it("should not take any input", function() {
        expect(invalidInteraction.doesTakeInput()).toEqual(false);
    });
});