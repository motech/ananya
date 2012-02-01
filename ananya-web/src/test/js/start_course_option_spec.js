describe("Start course option", function() {

    var metadata, course, startCourseOption;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";

    beforeEach(function() {
        metadata = {"audioFileBase": audioFileBase, "certificateCourseAudioLocation" : certificateCourseLocation};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        startCourseOption = new StartCourseOption(metadata, course);
    });

    it("should play the menu", function () {
        expect(startCourseOption.playAudio()).toEqual("./audio/certificatecourse/MenuCourse.wav");
    });

//    it("should return start course option as the next interaction", function() {
//        var startCourseOption = new StartCourseOption(null, null);
//        CertificateCourse.interactions["startCourseOption"] = startCourseOption;
//
//        expect(welcomeInteraction.nextInteraction()).toEqual(startCourseOption);
//    });

    it("should take input", function() {
        expect(startCourseOption.doesTakeInput()).toEqual(true);
    });

    it("should validate input", function () {
       expect(startCourseOption.validateInput(1)).toEqual(true);
       expect(startCourseOption.validateInput(2)).toEqual(true);
       expect(startCourseOption.validateInput(3)).toEqual(false);
    });
});
