describe("End of chapter menu Interaction", function() {

    var metadata, course, endOfChapterMenuInteraction;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";
    var courseState = new CourseState();

    beforeEach(function() {
        metadata = {"audio.url": audioFileBase, "certificate.audio.url" : certificateCourseLocation};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        endOfChapterMenuInteraction = new EndOfChapterMenuInteraction(metadata, course, courseState);
        CertificateCourse.interactions = new Array();
    });

    it("should play the lesson end menu prompt", function () {
        courseState.setChapterIndex(0);
        expect(endOfChapterMenuInteraction.playAudio()).toEqual("./audio/certificatecourse/chapter_1_menu.wav");
        courseState.setChapterIndex(1);
        expect(endOfChapterMenuInteraction.playAudio()).toEqual("./audio/certificatecourse/chapter_2_menu.wav");
    });

    it("should take input", function() {
        expect(endOfChapterMenuInteraction.doesTakeInput()).toEqual(true);
    });

    it("should validate input", function () {
       expect(endOfChapterMenuInteraction.validateInput(1)).toEqual(true);
       expect(endOfChapterMenuInteraction.validateInput(2)).toEqual(true);
       expect(endOfChapterMenuInteraction.validateInput(3)).toEqual(false);
    });

    it("should play lesson 1 of the current chapter on input of 1", function () {
        var lessonInteraction = {};
        CertificateCourse.interactions[LessonInteraction.KEY] = lessonInteraction;
        courseState.setChapterIndex(1);
        courseState.setLessonOrQuestionIndex(3);

        expect(endOfChapterMenuInteraction.processInputAndReturnNextInteraction(1)).toEqual(lessonInteraction);
        expect(courseState.chapterIndex).toEqual(1);
        expect(courseState.lessonOrQuestionIndex).toEqual(0);
    });

    it("should go to start next chapter action on input of 2.", function () {
        var startNextChapterInteraction = {};
        CertificateCourse.interactions[StartNextChapter.KEY] = startNextChapterInteraction;
        courseState.setChapterIndex(1);
        courseState.setLessonOrQuestionIndex(3);

        expect(endOfChapterMenuInteraction.processInputAndReturnNextInteraction(2)).toEqual(startNextChapterInteraction);
        expect(courseState.chapterIndex).toEqual(1);
        expect(courseState.lessonOrQuestionIndex).toEqual(3);

    });

    it("should return start next chapter interaction on receiving no input", function () {
        var startNextChapterInteraction = {};
        CertificateCourse.interactions[StartNextChapter.KEY] = startNextChapterInteraction;
        courseState.setChapterIndex(1);
        courseState.setLessonOrQuestionIndex(3);

        expect(endOfChapterMenuInteraction.continueWithoutInput()).toEqual(startNextChapterInteraction);
        expect(courseState.chapterIndex).toEqual(1);
        expect(courseState.lessonOrQuestionIndex).toEqual(3);
    });
});
