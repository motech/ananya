describe("Certificate course controller spec", function() {

    var metadata, course;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";

    beforeEach(function() {
        metadata = {"audioFileBase": audioFileBase, "certificateCourseAudioLocation" : certificateCourseLocation};
        course = certificationCourseWithTwoLessonsInEveryChapter();
    });

    it("should get the audio to be played.", function () {
        var controller = new CertificateCourseController(course, metadata);
        expect(controller.playAudio()).toEqual("./audio/certificatecourse/Introduction.wav");
    });


});
