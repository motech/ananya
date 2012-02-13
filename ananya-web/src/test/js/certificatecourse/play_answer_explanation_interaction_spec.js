describe("Evaluation question response interaction", function() {

    var metadata, course, playAnswerExplanationInteraction;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";
    var courseState;

    beforeEach(function() {
        metadata = {"audio.url": audioFileBase, "certificate.audio.url" : certificateCourseLocation};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        courseState = new CourseState();
        CertificateCourse.interactions = new Array();
        playAnswerExplanationInteraction = new PlayAnswerExplanationInteraction(metadata, course, courseState);
    });

    it("should play the right answer explanation if the question was answered right", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(2);
        var isAnswerCorrect = true;
        courseState.setAnswerCorrect(isAnswerCorrect);

        expect(playAnswerExplanationInteraction.playAudio()).toEqual("./audio/certificatecourse/chapter_1_quiz_1_correct.wav");
    });

    it("should play the wrong answer explanation if the question was answered incorrect", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(3);
        var isAnswerCorrect = false;
        courseState.setAnswerCorrect(isAnswerCorrect);

        expect(playAnswerExplanationInteraction.playAudio()).toEqual("./audio/certificatecourse/chapter_1_quiz_2_wrong.wav");
    });


    it("if not at last question, should change the state to go to the next question.", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(2);

        playAnswerExplanationInteraction.nextInteraction();

        expect(courseState.chapterIndex).toEqual(0);
        expect(courseState.lessonOrQuestionIndex).toEqual(3);
    });

    it("if not at last question, should return next interaction as pose question", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(2);
        CertificateCourse.interactions[PoseQuestionInteraction.KEY] = {};

        expect(playAnswerExplanationInteraction.nextInteraction()).toEqual(CertificateCourse.interactions[PoseQuestionInteraction.KEY]);
    });

    it("if at last question, should not change the state", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(3);

        playAnswerExplanationInteraction.nextInteraction();

        expect(courseState.chapterIndex).toEqual(0);
        expect(courseState.lessonOrQuestionIndex).toEqual(3);
    });

    it("if not at last question, should return next interaction as report chapter scores", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(3);
        CertificateCourse.interactions[ReportChapterScoreInteraction.KEY] = {};

        expect(playAnswerExplanationInteraction.nextInteraction()).toEqual(CertificateCourse.interactions[ReportChapterScoreInteraction.KEY]);
    });

    it("should not take any input", function() {
        expect(playAnswerExplanationInteraction.doesTakeInput()).toEqual(false);
    });

    it("should reset the result and the current response to null when moving out of the interaction", function () {
        //This is required since when we are moving out of this interaction, we are going ahead with the next question, for which there is no user response yet.
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(3);
        var isAnswerCorrect = true;
        courseState.setAnswerCorrect(isAnswerCorrect);
        courseState.setCurrentQuestionResponse(1);

        playAnswerExplanationInteraction.nextInteraction();

        expect(courseState.isAnswerCorrect).toEqual(null);
        expect(courseState.currentQuestionResponse).toEqual(null);
    });

    it("should give its own key", function () {
        expect(playAnswerExplanationInteraction.getInteractionKey()).toEqual("playAnswerExplanation");
    });

    it("when resuming call if there are further questions, should start at the next question", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(2);
        CertificateCourse.interactions[PoseQuestionInteraction.KEY] = {};

        var interactionToResume = playAnswerExplanationInteraction.resumeCall();

        expect(interactionToResume).toEqual(CertificateCourse.interactions[PoseQuestionInteraction.KEY]);
        expect(courseState.chapterIndex).toEqual(0);
        expect(courseState.lessonOrQuestionIndex).toEqual(3);
    });

    it("when resuming call if there are no further questions, should start at report chapter scores and should not change the course state", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(3);
        CertificateCourse.interactions[ReportChapterScoreInteraction.KEY] = {};

        var interactionToResume = playAnswerExplanationInteraction.resumeCall();

        expect(interactionToResume).toEqual(CertificateCourse.interactions[ReportChapterScoreInteraction.KEY]);
        expect(courseState.chapterIndex).toEqual(0);
        expect(courseState.lessonOrQuestionIndex).toEqual(3);
    });

});