describe("Pose Question Interaction", function() {

    var metadata, course, poseQuestionInteraction;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";
    var courseState = new CourseState();

    beforeEach(function() {
        metadata = {"audio.url": audioFileBase, "certificate.audio.url" : certificateCourseLocation};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        poseQuestionInteraction = new PoseQuestionInteraction(metadata, course, courseState);
        CertificateCourse.interactions = new Array();
    });

    it("should play the question", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(2);
        expect(poseQuestionInteraction.playAudio()).toEqual("./audio/certificatecourse/chapter_1_quiz_1.wav");
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(3);
        expect(poseQuestionInteraction.playAudio()).toEqual("./audio/certificatecourse/chapter_1_quiz_2.wav");
    });

    it("should take input", function() {
        expect(poseQuestionInteraction.doesTakeInput()).toEqual(true);
    });

    it("should validate input", function () {
       expect(poseQuestionInteraction.validateInput(1)).toEqual(true);
       expect(poseQuestionInteraction.validateInput(2)).toEqual(true);
       expect(poseQuestionInteraction.validateInput(3)).toEqual(false);
    });

    it("should set the answer given by user in the state", function () {
        courseState.setChapterIndex(1);
        courseState.setLessonOrQuestionIndex(1);
        courseState.setCurrentQuestionResponse(null);

        var userResponse = 1;
        poseQuestionInteraction.processInputAndReturnNextInteraction(userResponse);

        expect(courseState.currentQuestionResponse).toEqual(userResponse);
        expect(courseState.chapterIndex).toEqual(1);
        expect(courseState.lessonOrQuestionIndex).toEqual(1);
    });

    it("should evaluate the answer by user to be correct and set that in the state", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(2);

        var correctAnswer = 1;
        courseState.setCurrentQuestionResponse(correctAnswer);
        courseState.setAnswerCorrect(null);

        poseQuestionInteraction.processInputAndReturnNextInteraction(correctAnswer);

        expect(courseState.currentQuestionResponse).toEqual(correctAnswer);
        expect(courseState.chapterIndex).toEqual(0);
        expect(courseState.lessonOrQuestionIndex).toEqual(2);
        expect(courseState.isAnswerCorrect).toEqual(true);
    });

    it("should evaluate the answer by user to be wrong and set that in the state", function () {
        courseState.setChapterIndex(0);
        courseState.setLessonOrQuestionIndex(3);

        var inCorrectAnswer = 1;
        courseState.setCurrentQuestionResponse(inCorrectAnswer);
        courseState.setAnswerCorrect(null);

        poseQuestionInteraction.processInputAndReturnNextInteraction(inCorrectAnswer);

        expect(courseState.currentQuestionResponse).toEqual(inCorrectAnswer);
        expect(courseState.chapterIndex).toEqual(0);
        expect(courseState.lessonOrQuestionIndex).toEqual(3);
        expect(courseState.isAnswerCorrect).toEqual(false);
    });

    it("should return evaluate response as next interaction", function () {
        CertificateCourse.interactions["evaluateQuestionResponse"] = {};

        var userResponse = 1;
        expect(poseQuestionInteraction.processInputAndReturnNextInteraction(userResponse)).toEqual(CertificateCourse.interactions["evaluateQuestionResponse"]);
    });

    it("should return start next chapter interaction on receiving no input", function () {
        var startNextChapterInteraction = {};
        CertificateCourse.interactions["startNextChapter"] = startNextChapterInteraction;

        courseState.setChapterIndex(1);
        courseState.setLessonOrQuestionIndex(0);

        expect(poseQuestionInteraction.continueWithoutInput()).toEqual(startNextChapterInteraction);

        expect(courseState.chapterIndex).toEqual(1);
        expect(courseState.lessonOrQuestionIndex).toEqual(0);
    });
});
