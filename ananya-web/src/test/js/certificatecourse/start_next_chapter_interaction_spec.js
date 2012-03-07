describe("Start next chapter interaction", function() {
    var metadata, course, startNextChapter;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";

    beforeEach(function() {
        metadata = {"audio.url": audioFileBase, "certificate.audio.url" : certificateCourseLocation};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        CertificateCourse.interactions = new Array();
    });

    it("should set first lesson in state and go to lesson interaction, if course state does not contain any lesson number", function () {
        var courseState = new CourseState();
        courseState.chapterIndex = 0;
        var startNextChapter = new StartNextChapterInteraction(metadata, course, courseState);

        var lessonInteraction = new LessonInteraction(null, null);
        CertificateCourse.interactions[LessonInteraction.KEY] = lessonInteraction;

        var nextState = startNextChapter.processSilentlyAndReturnNextState();

        expect(courseState.chapterIndex).toEqual(0);
        expect(courseState.lessonOrQuestionIndex).toEqual(0);
        expect(nextState).toEqual(lessonInteraction);
    });

    it("should set next chapter first lesson in state", function () {
        var courseState = new CourseState();
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(0);
        var startNextChapter = new StartNextChapterInteraction(metadata, course, courseState);

        var lessonInteraction = new LessonInteraction(null, null);
        CertificateCourse.interactions[LessonInteraction.KEY] = lessonInteraction;

        var nextState = startNextChapter.processSilentlyAndReturnNextState();

        expect(courseState.chapterIndex).toEqual(0);
        expect(courseState.lessonOrQuestionIndex).toEqual(0);
        expect(nextState).toEqual(lessonInteraction);
    });

    it("should resume call at the same place where the call was left", function () {
        var courseState = new CourseState();
        var startNextChapter = new StartNextChapterInteraction(metadata, course, courseState);
        expect(startNextChapter.resumeCall()).toEqual(startNextChapter);
    });
});