describe("Evaluation question response interaction", function() {

    var metadata, course, evaluateQuestionResponseInteraction;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";
    var courseState;

    beforeEach(function() {
        metadata = {"audio.url": audioFileBase, "certificate.audio.url" : certificateCourseLocation};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        courseState = new CourseState();
        CertificateCourse.interactions = new Array();
        evaluateQuestionResponseInteraction = new EvaluateQuestionResponseInteraction(metadata, course, courseState);
    });

    it("should play the right answer explanation if the question was answered right", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(2);
        var correctAnswer = 1;
        courseState.setCurrentQuestionResponse(correctAnswer);

        expect(evaluateQuestionResponseInteraction.playAudio()).toEqual("./audio/certificatecourse/chapter_1_quiz_1_correct.wav");
    });

    it("should play the wrong answer explanation if the question was answered incorrect", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(3);
        var incorrectAnswer = 1;
        courseState.setCurrentQuestionResponse(incorrectAnswer);

        expect(evaluateQuestionResponseInteraction.playAudio()).toEqual("./audio/certificatecourse/chapter_1_quiz_2_wrong.wav");
    });


    it("if not at last question, should change the state to go to the next question.", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(2);

        evaluateQuestionResponseInteraction.nextInteraction();

        expect(courseState.chapterIndex).toEqual(0);
        expect(courseState.lessonOrQuestionIndex).toEqual(3);
    });

    it("if not at last question, should return next interaction as pose question", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(2);
        CertificateCourse.interactions["poseQuestion"] = {};

        expect(evaluateQuestionResponseInteraction.nextInteraction()).toEqual(CertificateCourse.interactions["poseQuestion"]);
    });

    it("if at last question, should not change the state", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(3);

        evaluateQuestionResponseInteraction.nextInteraction();

        expect(courseState.chapterIndex).toEqual(0);
        expect(courseState.lessonOrQuestionIndex).toEqual(3);
    });

    it("if not at last question, should return next interaction as report chapter scores", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(3);
        CertificateCourse.interactions["reportChapterScore"] = {};

        expect(evaluateQuestionResponseInteraction.nextInteraction()).toEqual(CertificateCourse.interactions["reportChapterScore"]);
    });

    it("should not take any input", function() {
        expect(evaluateQuestionResponseInteraction.doesTakeInput()).toEqual(false);
    });
});