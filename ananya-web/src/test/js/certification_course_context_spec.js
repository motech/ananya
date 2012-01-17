describe("Certification Course Context", function() {
    var course, context;
    var audioFileBase = "./audio/";

    beforeEach(function() {
        var metadata = {"audioFileBase": audioFileBase};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        context = new CertificationCourseContext(course, metadata);
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

    it("should get the lesson content URI for current lesson", function() {
        context.currentInteraction = course.children[1].children[1];
        expect(context.currentInteractionLesson()).toEqual("./audio/chapter_2_lesson_2.wav");
    });

    it("should get the menu content URI for the current interaction", function() {
        expect(context.currentInteractionMenu()).toEqual("./audio/MenuCourse.wav");

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

    it("should stay at the last quiz of a chapter, after it is finished", function() {
        var quiz_2_in_chapter_1 = course.children[0].children[3];
        context.currentInteraction = quiz_2_in_chapter_1;

        context.lessonOrQuizFinished();

        expect(context.currentInteraction).toEqual(quiz_2_in_chapter_1);
    });

    it("should set the finishedWithChapter flag after the last quiz of a chapter is finished", function() {
        var quiz_2_in_chapter_1 = course.children[0].children[3];
        context.currentInteraction = quiz_2_in_chapter_1;

        expect(context.hasFinishedChapter).toEqual(false)

        context.lessonOrQuizFinished();

        expect(context.hasFinishedChapter).toEqual(true)
    });

    it("should not set the finishedWithChapter flag after a non-last quiz of a chapter is finished", function() {
        var quiz_1_in_chapter_1 = course.children[0].children[2];
        context.currentInteraction = quiz_1_in_chapter_1;

        expect(context.hasFinishedChapter).toEqual(false)

        context.lessonOrQuizFinished();

        expect(context.hasFinishedChapter).toEqual(false)
    });
})