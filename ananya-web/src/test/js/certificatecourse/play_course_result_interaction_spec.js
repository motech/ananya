describe("Play course result", function() {

    var metadata, course, playCourseResultInteraction;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";
    var courseState;

    beforeEach(function() {
        metadata = {"audio.url": audioFileBase, "certificate.audio.url" : certificateCourseLocation, "certificate.end.result.pass" : "0286_score_18_or_more.wav", "certificate.end.result.fail" : "0285_score_less_than_18.wav"};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        courseState = new CourseState();
        CertificateCourse.interactions = new Array();
        playCourseResultInteraction = new PlayCourseResultInteraction(metadata, course, courseState);
    });

    it("should play the pass audio file when score exceeds 18", function () {
        courseState.setChapterIndex(2);
        courseState.setLessonOrQuestionIndex(4);
        courseState.scoresByChapter = {
            "0" : 4,
            "1" : 4,
            "2" : 4,
            "3" : 4,
            "4" : 2
        };

        expect(playCourseResultInteraction.playAudio()).toEqual("./audio/certificatecourse/0286_score_18_or_more.wav");
    });

    it("should play the fail audio file when score is less than 18", function () {
        courseState.setChapterIndex(2);
        courseState.setLessonOrQuestionIndex(4);
        courseState.scoresByChapter = {
            "0" : 1,
            "1" : 1,
            "2" : 1,
            "3" : 1,
            "4" : 1
        };

        expect(playCourseResultInteraction.playAudio()).toEqual("./audio/certificatecourse/0285_score_less_than_18.wav");
    });

    it("should not take any input", function() {
        expect(playCourseResultInteraction.doesTakeInput()).toEqual(false);
    });

    it("should return course end marker as next interaction and should not change the course state", function () {
        courseState.setChapterIndex(2);
        courseState.setLessonOrQuestionIndex(4);
        CertificateCourse.interactions[CourseEndMarkerInteraction.KEY] = {};

        expect(playCourseResultInteraction.nextInteraction()).toEqual(CertificateCourse.interactions[CourseEndMarkerInteraction.KEY]);
        expect(courseState.chapterIndex).toEqual(2);
        expect(courseState.lessonOrQuestionIndex).toEqual(4);
    });
});