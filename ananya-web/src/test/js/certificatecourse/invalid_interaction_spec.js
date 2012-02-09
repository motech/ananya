describe("Invalid Interaction", function(){

    var metadata, invalidInteraction, previousInteractionNeedingInput = {getInteractionKey:function() {return "myKey";}};
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";
    var retryAudio = "0000_error_in_pressing_number.wav";

    beforeEach(function() {
        metadata = {"audio.url": audioFileBase, "certificate.audio.url" : certificateCourseLocation, "invalid.input.retry.audio" : retryAudio};
        invalidInteraction = new InvalidInputInteraction(previousInteractionNeedingInput, metadata);
    });

    it("should return invalid button press audio when audio is played", function () {
        expect(invalidInteraction.playAudio()).toEqual("./audio/" + retryAudio);
    });

    it("should return the previous interaction when nextAction is requested", function () {
        expect(invalidInteraction.nextInteraction()).toEqual(previousInteractionNeedingInput);
    });

    it("should return the previous interaction key when getInteractionKey is invoked", function () {
        var previousInteractionKey = "myKey";
        expect(invalidInteraction.getInteractionKey()).toEqual(previousInteractionKey);
    });

    it("should not take any input", function() {
        expect(invalidInteraction.doesTakeInput()).toEqual(false);
    });
});