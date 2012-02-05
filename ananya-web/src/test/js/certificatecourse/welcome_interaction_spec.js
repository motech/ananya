describe("Welcome interaction", function() {

    var metadata, course, welcomeInteraction;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";

    beforeEach(function() {
        metadata = {"audio.url": audioFileBase, "certificate.audio.url" : certificateCourseLocation};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        welcomeInteraction = new WelcomeInteraction(metadata, course);
    });

    it("should play the introduction", function () {
        expect(welcomeInteraction.playAudio()).toEqual("./audio/certificatecourse/Introduction.wav");
    });

    it("should return start course option as the next interaction", function() {
        var startCourseOption = new StartCourseOption(null, null);
        CertificateCourse.interactions["startCourseOption"] = startCourseOption;

        expect(welcomeInteraction.nextInteraction()).toEqual(startCourseOption);
    });

    it("should not take any input", function() {
        expect(welcomeInteraction.doesTakeInput()).toEqual(false);
    });
});