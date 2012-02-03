describe("Help Interaction", function(){

    var metadata, helpInteraction, previousInteraction = {};
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";

    beforeEach(function() {
        metadata = {"audioFileBase": audioFileBase, "certificateCourseAudioLocation" : certificateCourseLocation};
        helpInteraction = new HelpInteraction(previousInteraction, metadata, certificationCourseWithTwoLessonsInEveryChapter());
    });

    it("should return help audio", function () {
        expect(helpInteraction.playAudio()).toEqual("./audio/certificatecourse/Help.wav");
    });

    it("should return the previous interaction when nextAction is requested", function () {
        expect(helpInteraction.nextInteraction()).toEqual(previousInteraction);
    });

    it("should not take any input", function() {
        expect(helpInteraction.doesTakeInput()).toEqual(false);
    });
});