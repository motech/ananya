describe("Start quiz interaction", function() {

    var metadata, course, startQuizInteraction;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";
    var courseState = new CourseState();

    beforeEach(function() {
        metadata = {"audio.url": audioFileBase, "certificate.audio.url" : certificateCourseLocation};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        startQuizInteraction = new StartQuizInteraction(metadata, course, courseState);
    });

    it("should play the quiz header", function () {
        courseState.setChapterIndex(1);
        expect(startQuizInteraction.playAudio()).toEqual("./audio/certificatecourse/chapter_2_quizHeader.wav");
    });

    it("should not take any input", function() {
        expect(startQuizInteraction.doesTakeInput()).toEqual(false);
    });

    it("should set the current chapter's score to zero in scoresByChapter", function() {
        var currentChapterIndex = 1;
        courseState.setChapterIndex(currentChapterIndex);
        startQuizInteraction.nextInteraction();
        expect(courseState.scoresByChapter[currentChapterIndex]).toEqual(0);
    });

    it("should return pose question as the next interaction", function() {
        var poseQuestion = {};
        CertificateCourse.interactions[PoseQuestionInteraction.KEY] = poseQuestion;

        expect(startQuizInteraction.nextInteraction()).toEqual(poseQuestion);
    });

    it("should should change the courseState to first question in the chapter", function() {
        var currentChapterIndex = 0;
        var completedLessonIndex = 1;
        courseState.setChapterIndex(currentChapterIndex);
        courseState.setLessonOrQuestionIndex(completedLessonIndex);

        var indexOfFirstQuestionInTheChapter = completedLessonIndex + 1;

        startQuizInteraction.nextInteraction();

        expect(courseState.chapterIndex).toEqual(currentChapterIndex);
        expect(courseState.lessonOrQuestionIndex).toEqual(indexOfFirstQuestionInTheChapter);
    });

    it("should resume call at the same place where the call was left", function () {
        expect(startQuizInteraction.resumeCall()).toEqual(startQuizInteraction);
    });
});