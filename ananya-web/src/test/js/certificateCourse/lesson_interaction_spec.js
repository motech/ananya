describe("Lesson interaction", function() {

    var metadata, course, lessonInteraction, courseState;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";

    beforeEach(function() {
        metadata = {"audioFileBase": audioFileBase, "certificateCourseAudioLocation" : certificateCourseLocation};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        courseState = new CourseState();
        courseState.setChapterIndex(1);
        courseState.setLessonOrQuestionIndex(0);
        lessonInteraction = new LessonInteraction(metadata, course, courseState);
    });

    it("should play the lesson", function () {
        expect(lessonInteraction.playAudio()).toEqual("./audio/certificatecourse/chapter_2_lesson_1.wav");
    });

    it("should return lesson end menu as the next interaction", function() {
        var lessonEndMenuInteraction = new LessonEndMenuInteraction(null, null);
        CertificateCourse.interactions["lessonEndMenu"] = lessonEndMenuInteraction;

        expect(lessonInteraction.nextInteraction()).toEqual(lessonEndMenuInteraction);
    });

    it("should not take any input", function() {
        expect(lessonInteraction.doesTakeInput()).toEqual(false);
    });
});