describe("Lesson End Menu Interaction", function() {

    var metadata, course, lessonEndMenuInteraction;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";
    var courseState = new CourseState();

    beforeEach(function() {
        metadata = {"audio.url": audioFileBase, "certificate.audio.url" : certificateCourseLocation};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        lessonEndMenuInteraction = new LessonEndMenuInteraction(metadata, course, courseState);
        CertificateCourse.interactions = new Array();
    });

    it("should play the lesson end menu prompt", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(0);
        expect(lessonEndMenuInteraction.playAudio()).toEqual("./audio/certificatecourse/chapter_1_lesson_1_menu.wav");
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(1);
        expect(lessonEndMenuInteraction.playAudio()).toEqual("./audio/certificatecourse/chapter_1_lesson_2_menu.wav");
    });

    it("should take input", function() {
        expect(lessonEndMenuInteraction.doesTakeInput()).toEqual(true);
    });

    it("should validate input", function () {
       expect(lessonEndMenuInteraction.validateInput(1)).toEqual(true);
       expect(lessonEndMenuInteraction.validateInput(2)).toEqual(true);
       expect(lessonEndMenuInteraction.validateInput(3)).toEqual(false);
    });

    it("should play current lesson on input of 1", function () {
        var lessonInteraction = {};
        CertificateCourse.interactions["lesson"] = lessonInteraction;
        courseState.setChapterIndex(1);
        courseState.setLessonOrQuestionIndex(1);

        expect(lessonEndMenuInteraction.processInputAndReturnNextInteraction(1)).toEqual(lessonInteraction);
        expect(courseState.chapterIndex).toEqual(1);
        expect(courseState.lessonOrQuestionIndex).toEqual(1);
    });

    it("should play next lesson on input of 2, if current lesson is not the last.", function () {
        var lessonInteraction = {};
        CertificateCourse.interactions["lesson"] = lessonInteraction;
        courseState.setChapterIndex(1);
        courseState.setLessonOrQuestionIndex(0);

        expect(lessonEndMenuInteraction.processInputAndReturnNextInteraction(2)).toEqual(lessonInteraction);
        expect(courseState.chapterIndex).toEqual(1);
        expect(courseState.lessonOrQuestionIndex).toEqual(1);

    });

    it("should play quiz on input of 2, if current lesson is the last.", function () {
        var startQuiz = {};
        CertificateCourse.interactions["startQuiz"] = startQuiz;
        courseState.setChapterIndex(1);
        courseState.setLessonOrQuestionIndex(1);

        expect(lessonEndMenuInteraction.processInputAndReturnNextInteraction(2)).toEqual(startQuiz);
        expect(courseState.chapterIndex).toEqual(1);
    });

    it("should return start next lesson interaction on receiving no input, when not at last lesson", function () {
        var lessonInteraction = {};
        CertificateCourse.interactions["lesson"] = lessonInteraction;
        courseState.setChapterIndex(1);
        courseState.setLessonOrQuestionIndex(0);

        expect(lessonEndMenuInteraction.continueWithoutInput()).toEqual(lessonInteraction);
        expect(courseState.chapterIndex).toEqual(1);
        expect(courseState.lessonOrQuestionIndex).toEqual(1);
    });

    it("should return start next chapter interaction on receiving no input without changing the course state, when at last lesson", function () {
        var startNextChapter = {};
        CertificateCourse.interactions["startNextChapter"] = startNextChapter;
        courseState.setChapterIndex(1);
        courseState.setLessonOrQuestionIndex(1);

        expect(lessonEndMenuInteraction.continueWithoutInput()).toEqual(startNextChapter);
        expect(courseState.chapterIndex).toEqual(1);
    });
});
