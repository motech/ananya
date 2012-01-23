
describe("Certification Course Context", function() {
    var course, context;
    var audioFileBase = "./audio/";

    beforeEach(function() {
        var metadata = {"audioFileBase": audioFileBase};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        context = new CertificationCourseContext(course, metadata);
    });

    it("should navigate to lesson if lesson bookmark exists", function() {
        var bookmark_for_lesson_2_chapter_2 = {"type" : "lesson", "chapterIndex" : "1" , "lessonIndex" : "1"};

        context.navigateToBookmark(bookmark_for_lesson_2_chapter_2);

        expect(context.currentInteraction).toEqual(course.children[1].children[1]);
    });

    it("should not navigate from currentInteraction if bookmark does not exist", function() {
        var emptyBookmark = {};

        context.navigateToBookmark(emptyBookmark);
        expect(context.currentInteraction).toEqual(course);

        context.currentInteraction = course.children[1];
        context.navigateToBookmark(emptyBookmark);
        expect(context.currentInteraction).toEqual(course.children[1]);
    });

    it("when initialized should have course as the current interaction.", function() {
        expect(context.currentInteraction).toEqual(course);
    });

    it("should know when it is at the course root", function() {
        expect(context.isAtCourseRoot()).toEqual(true);

        context.currentInteraction = course.children[0];
        expect(context.isAtCourseRoot()).toEqual(false);
    });

    it("should know when it is at a lesson", function() {
        expect(context.isAtLesson()).toEqual(false);

        context.currentInteraction = course.children[0].children[0];
        expect(context.isAtLesson()).toEqual(true);
    });

    it("should know when it is at a quiz", function() {
        expect(context.isAtQuizQuestion()).toEqual(false);

        context.currentInteraction = course.children[0].children[2];
        expect(context.isAtQuizQuestion()).toEqual(true);
    });

    it("should get the lesson content URI for current lesson", function() {
        context.currentInteraction = course.children[1].children[1];
        expect(context.currentInteractionLesson()).toEqual("./audio/chapter_2_lesson_2.wav");
    });

    it("should get the quiz header URI for current chapter", function() {
        context.currentInteraction = course.children[1].children[1];
        expect(context.currentInteractionQuizHeader()).toEqual("./audio/chapter_2_quizHeader.wav");
    });

    it("should get the quiz question URI for current chapter", function() {
        context.currentInteraction = course.children[0].children[2];
        expect(context.currentInteractionQuizQuestion()).toEqual("./audio/chapter_1_quiz_1.wav");
    });

    it("should get the menu content URI for the current interaction", function() {
        expect(context.currentInteractionMenu()).toEqual("./audio/MenuCourse.wav");

        context.currentInteraction = course.children[1];
        expect(context.currentInteractionMenu()).toEqual("./audio/chapter_2_menu.wav");

        context.currentInteraction = course.children[1].children[1];
        expect(context.currentInteractionMenu()).toEqual("./audio/chapter_2_lesson_2_menu.wav");
    });

    it("should go to the next lesson when a lesson is finished", function() {
        var lesson_1_in_chapter_1 = course.children[0].children[0];
        var lesson_2_in_chapter_1 = course.children[0].children[1];
        context.currentInteraction = lesson_1_in_chapter_1;

        context.lessonOrQuizFinished();

        expect(context.currentInteraction).toEqual(lesson_2_in_chapter_1);
    });

    it("should go to the first quiz after the last lesson in a chapter is finished", function() {
        var lesson_2_in_chapter_1 = course.children[0].children[1];
        var quiz_1_in_chapter_1 = context.currentInteraction.children[0].children[2];
        context.currentInteraction = lesson_2_in_chapter_1;

        context.lessonOrQuizFinished();

        expect(context.currentInteraction).toEqual(quiz_1_in_chapter_1);
    });

    it("should move up to chapter, after last quiz of chapter is finished", function() {
        var quiz_2_in_chapter_1 = course.children[0].children[3];
        var chapter_1 = course.children[0];
        context.currentInteraction = quiz_2_in_chapter_1;

        context.lessonOrQuizFinished();

        expect(context.currentInteraction).toEqual(chapter_1)
    });

    it("should set the hasFinishedLastQuizOfChapter flag after the last quiz of a chapter is finished", function() {
        var quiz_2_in_chapter_1 = course.children[0].children[3];

        context.currentInteraction = quiz_2_in_chapter_1;

        expect(context.hasFinishedLastQuizOfChapter).toEqual(false)

        context.lessonOrQuizFinished();

        expect(context.hasFinishedLastQuizOfChapter).toEqual(true)

    });

    it("should not set the hasFinishedLastQuizOfChapter flag after a non-last quiz of a chapter is finished", function() {
        var quiz_1_in_chapter_1 = course.children[0].children[2];
        context.currentInteraction = quiz_1_in_chapter_1;

        expect(context.hasFinishedLastQuizOfChapter).toEqual(false)

        context.lessonOrQuizFinished();

        expect(context.hasFinishedLastQuizOfChapter).toEqual(false)
    });

    it("should set the hasFinishedLastLessonOfChapter flag after a last lesson of chapter is finished", function() {
        var lesson_2_in_chapter_1 = course.children[0].children[1];
        context.currentInteraction = lesson_2_in_chapter_1;

        expect(context.hasFinishedLastLessonOfChapter).toEqual(false);

        context.lessonOrQuizFinished();

        expect(context.hasFinishedLastLessonOfChapter).toEqual(true);
    });

    it("should know if at a quiz header based on hasFinishedLastLessonOfChapter flag", function() {
        expect(context.isAtQuizHeader()).toEqual(false);

        context.hasFinishedLastLessonOfChapter = true;

        expect(context.isAtQuizHeader()).toEqual(true);
    });


    it("should go to first chapter after welcome message played", function() {
        var lesson_1_in_chapter_1 = course.children[0].children[0];

        context.welcomeFinished();

        expect(context.currentInteraction).toEqual(lesson_1_in_chapter_1);
    });

    it("should be able to go to first lesson of next chapter after score report finished", function() {
         var chapter_1 = course.children[0];
         context.currentInteraction = chapter_1;

         context.scoreReportFinished();

         var lesson_1_in_chapter_2 = course.children[1].children[0];
         expect(context.currentInteraction).toEqual(lesson_1_in_chapter_2);
    });

    it("should be able to restart chapter after the score report finished ", function() {
         var chapter_1 = course.children[0];
         context.currentInteraction = chapter_1;

         context.restartChapter();

         var lesson_1_in_chapter_1 = course.children[0].children[0];
         expect(context.currentInteraction).toEqual(lesson_1_in_chapter_1);
    });

    it("should set bookmark to lesson 2 after lesson 1 is played ", function() {
        var lesson_1_in_chapter_1 = course.children[0].children[0];
        var bookmark_for_lesson_2 = {"type" : "lesson", "chapterIndex" : "0" , "lessonIndex" : "1"};

        context.currentInteraction = lesson_1_in_chapter_1;
        context.lessonOrQuizFinished();

        context.addAfterLessonBookmark();

        expect(context.bookmark).toEqual(bookmark_for_lesson_2);
    });

    it("should set bookmark to quiz header after last lesson is played ", function() {
        var lesson_2_in_chapter_1 = course.children[0].children[1];
        var bookmark_for_quizHeader = {"type" : "quizHeader", "chapterIndex" : "0"};

        context.currentInteraction = lesson_2_in_chapter_1;
        context.lessonOrQuizFinished();

        context.addAfterLessonBookmark();

        expect(context.bookmark).toEqual(bookmark_for_quizHeader);
    });

    it("should set bookmark to chapter 1 lesson 1 after welcome message is played", function() {
        var bookmark_for_lesson_1 = {"type" : "lesson", "chapterIndex" : "0" , "lessonIndex" : "0"};

        context.addAfterWelcomeMessageBookmark();

        expect(context.bookmark).toEqual(bookmark_for_lesson_1);
    });

    it("should play correct answer explanation if the response for a question is correct", function() {
        var quiz_1_in_chapter_2 = course.children[1].children[2];
        context.currentInteraction = quiz_1_in_chapter_2;
        expect(context.evaluateAndReturnAnswerExplanation(2)).toEqual("./audio/chapter_2_quiz_1_correct.wav");
    });

    it("should play wrong answer explanation if the response for a question is incorrect", function() {
        var quiz_2_in_chapter_2 = course.children[1].children[3];
        context.currentInteraction = quiz_2_in_chapter_2;
        expect(context.evaluateAndReturnAnswerExplanation(2)).toEqual("./audio/chapter_2_quiz_2_wrong.wav");
    });
})