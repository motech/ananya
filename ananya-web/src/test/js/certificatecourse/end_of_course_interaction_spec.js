describe("End of course interaction", function() {

    var metadata, course, endOfCourseInteraction;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";
    var courseState;

    beforeEach(function() {
        metadata = {"audio.url": audioFileBase, "certificate.audio.url" : certificateCourseLocation, "certificate.end.thanks" : "0246_thanks_p.wav"};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        courseState = new CourseState();
        CertificateCourse.interactions = new Array();
        endOfCourseInteraction = new EndOfCourseInteraction(metadata, course, courseState);
    });

    it("should play the thank you message", function () {
        expect(endOfCourseInteraction.playAudio()).toEqual("./audio/certificatecourse/0246_thanks_p.wav");
    });

    it("should not take any input", function() {
        expect(endOfCourseInteraction.doesTakeInput()).toEqual(false);
    });

    it("should return play final score as next interaction and should not change the course state", function () {
        courseState.setChapterIndex(2);
        courseState.setLessonOrQuestionIndex(4);
        CertificateCourse.interactions[PlayFinalScoreInteraction.KEY] = {};

        expect(endOfCourseInteraction.nextInteraction()).toEqual(CertificateCourse.interactions[PlayFinalScoreInteraction.KEY]);
        expect(courseState.chapterIndex).toEqual(2);
        expect(courseState.lessonOrQuestionIndex).toEqual(4);
    });
});