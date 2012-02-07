describe("Start course option", function() {

    var metadata, course, startCourseOption;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";

    beforeEach(function() {
        metadata = {"audio.url": audioFileBase, "certificate.audio.url" : certificateCourseLocation};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        startCourseOption = new StartCourseOption(metadata, course);
        CertificateCourse.interactions = new Array();
    });

    it("should play the menu", function () {
        expect(startCourseOption.playAudio()).toEqual("./audio/certificatecourse/MenuCourse.wav");
    });

    it("should take input", function() {
        expect(startCourseOption.doesTakeInput()).toEqual(true);
    });

    it("should validate input", function () {
       expect(startCourseOption.validateInput(1)).toEqual(true);
       expect(startCourseOption.validateInput(2)).toEqual(true);
       expect(startCourseOption.validateInput(3)).toEqual(false);
    });

    it("should return welcome interaction on receiving input 1", function () {
        var welcomeInteraction = new WelcomeInteraction(null, null);
        CertificateCourse.interactions[WelcomeInteraction.KEY] = welcomeInteraction;

        expect(startCourseOption.processInputAndReturnNextInteraction(1)).toEqual(welcomeInteraction);
    });

    it("should return start next chapter interaction on receiving input 2", function() {
        var startNextChapterInteraction = new StartNextChapter();
        CertificateCourse.interactions[StartNextChapter.KEY] = startNextChapterInteraction;

        expect(startCourseOption.processInputAndReturnNextInteraction(2)).toEqual(startNextChapterInteraction);
    });

    it("should return start next chapter interaction on receiving no input", function () {
        var startNextChapterInteraction = new StartNextChapter();
        CertificateCourse.interactions[StartNextChapter.KEY] = startNextChapterInteraction;

        expect(startCourseOption.continueWithoutInput()).toEqual(startNextChapterInteraction);
    });
});
