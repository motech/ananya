describe("Call Context", function() {
    var course, callContext, promptContext;
    var audioFileBase = "../audio/";
    var jobaidFileBase = "jobaid/";

    beforeEach(function() {
        var metadata = {
                        "audio.url": audioFileBase,
                        "jobaid.audio.url":jobaidFileBase,
                        "option.to.top.level.audio" : "option.to.go.to.top.level.wav",
                        "invalid.input.retry.audio" : "0000_error_in_pressing_number.wav"
                        };
        course = jobAidCourseWithTwoLessonsInEveryChapter();
        promptContext = new PromptContext(metadata);
        callContext = new CallContext(course, metadata, promptContext);
    });

    it("when initialized should have course as the current interaction.", function() {
        expect(callContext.currentInteraction).toEqual(course);
    });

    it("when the current interaction is course and handleInput is called, current interaction should be set to level requested.", function() {
        var childNumber = 1;
        var level1 = course.children[0];

        callContext.handleInput(childNumber);

        expect(callContext.currentInteraction).toEqual(level1);
    });

    it("should say that level requested is valid if zero or a child with that number exists.", function() {
        expect(callContext.isValidInput(0)).toEqual(true);
        expect(callContext.isValidInput(-1)).toEqual(false);
        expect(callContext.isValidInput(1)).toEqual(true);
        expect(callContext.isValidInput(2)).toEqual(true);
        expect(callContext.isValidInput(3)).toEqual(false);
    });

    it("when lesson is finished, should set the current interaction to parent of the next lesson.", function() {
        callContext.handleInput(1).handleInput(1);
        var chapter1 = callContext.currentInteraction;
        callContext.handleInput(1);
        var lesson1 = callContext.currentInteraction;

        callContext.lessonFinished();

        expect(callContext.currentInteraction).toEqual(chapter1);
    });

    it("when last lesson in first chapter is finished, should set the current interaction to second chapter.", function () {
        var level1 = course.children[0];
        var chapter2 = level1.children[1];

        var levelNeeded = 1;
        var chapterNeeded = 1;
        var lessonNeeded = 2;

        callContext.handleInput(levelNeeded).handleInput(chapterNeeded).handleInput(lessonNeeded);
        callContext.lessonFinished();

        expect(callContext.currentInteraction).toEqual(chapter2);
    });

    it("when last lesson in last chapter in a level is finished, should set the current interaction to first chapter in next level.", function () {
        var level2 = course.children[1];
        var level2_chapter1 = level2.children[0];

        var levelNeeded = 1;
        var chapterNeeded = 2;
        var lessonNeeded = 2;

        callContext.handleInput(levelNeeded).handleInput(chapterNeeded).handleInput(lessonNeeded);
        callContext.lessonFinished();

        expect(callContext.currentInteraction).toEqual(level2_chapter1);
    });

    it("when last lesson in last chapter in the last level is finished, should set the current interaction to last chapter in the last level.", function () {
        var level2 = course.children[1];
        var level2_chapter2 = level2.children[1];

        var levelNeeded = 2;
        var chapterNeeded = 2;
        var lessonNeeded = 2;

        callContext.handleInput(levelNeeded).handleInput(chapterNeeded).handleInput(lessonNeeded);
        callContext.lessonFinished();

        expect(callContext.currentInteraction).toEqual(level2_chapter2);
    });

    it("should be able to recognize whether a current interaction is a lesson", function () {
        expect(callContext.isAtALesson()).toEqual(false);
        callContext.handleInput(1);
        expect(callContext.isAtALesson()).toEqual(false);
        callContext.handleInput(1);
        expect(callContext.isAtALesson()).toEqual(false);
        callContext.handleInput(1);
        expect(callContext.isAtALesson()).toEqual(true);
    });

    it("should return the menu for the current interaction", function () {
        expect(callContext.currentInteractionMenu()).toEqual("../audio/jobaid/MenuLevels.wav");
        callContext.handleInput(1);
        expect(callContext.currentInteractionMenu()).toEqual("../audio/jobaid/MenuLevel1Chapters.wav");
    });

    it("should return the lesson for the current interaction", function () {
        var levelNeeded = 1;
        var chapterNeeded = 1;
        var lessonNeeded = 1;
        callContext.handleInput(levelNeeded).handleInput(chapterNeeded).handleInput(lessonNeeded);
        expect(callContext.currentInteractionLesson()).toEqual("../audio/jobaid/chapter_1_lesson_1.wav");
    });

    it("should navigate to course node when handleInput is called with 0", function () {
        var levelNeeded = 2;
        var chapterNeeded = 2;

        callContext.handleInput(levelNeeded).handleInput(chapterNeeded);

        callContext.handleInput(0);

        expect(callContext.currentInteraction).toEqual(course);
    });

    it("should navigate to the child number passed when handleInput is called with non-zero number", function() {
        var levelNeeded = 1;
        var chapterNeeded = 1;
        callContext.handleInput(levelNeeded).handleInput(chapterNeeded);

        var expectedLessonAfterNavigation = callContext.currentInteraction.children[1];

        callContext.handleInput(2);

        expect(callContext.currentInteraction).toEqual(expectedLessonAfterNavigation);
    });

    it("should play the introduction audio of the course root when app is initialized.", function() {
        expect(callContext.shouldPlayNextIntroduction).toEqual(true);
    });

    it("should not play the introduction audio of the course root, when 0 is input", function () {
        var levelNeeded = 2;
        var chapterNeeded = 2;
        callContext.handleInput(levelNeeded).handleInput(chapterNeeded);

        callContext.handleInput(0);

        expect(callContext.shouldPlayNextIntroduction).toEqual(false);
    });

    it("should not play the introduction audio of the chapter after playing lesson", function () {
        var levelNeeded = 2;
        var chapterNeeded = 2;
        var lessonNeeded = 1;

        callContext.handleInput(levelNeeded).handleInput(chapterNeeded).handleInput(lessonNeeded);
        callContext.lessonFinished();

        expect(callContext.shouldPlayNextIntroduction).toEqual(false);
    });

    it("should play the introduction audio of the next chapter if it is different from the chapter of the finished lesson", function () {
        var levelNeeded = 2;
        var chapterNeeded = 1;
        var lastLessonOfChapter = 2;

        callContext.handleInput(levelNeeded).handleInput(chapterNeeded).handleInput(lastLessonOfChapter);
        callContext.lessonFinished();

        expect(callContext.shouldPlayNextIntroduction).toEqual(true);
    });

    it("should play the introduction of a chapter if being accessed from level", function () {
        var levelNeeded = 2;
        var chapterNeeded = 2;

        callContext.handleInput(levelNeeded).handleInput(chapterNeeded);
        callContext.handleInput(0);
        callContext.handleInput(2).handleInput(2);

        expect(callContext.shouldPlayNextIntroduction).toEqual(true);
    });

    it("should know when current interaction is course root", function() {
        expect(callContext.isAtCourseRoot()).toEqual(true);
        callContext.handleInput(1);
        expect(callContext.isAtCourseRoot()).toEqual(false);
        callContext.handleInput(0);
        expect(callContext.isAtCourseRoot()).toEqual(true);

    });

    it("should play next introduction only if introduction present and current interaction is not last lesson", function() {
        var level1 = course.children[0];
        callContext.handleInput(1);
        expect(callContext.currentInteraction).toEqual(level1);

        expect(callContext.shouldPlayIntroduction()).toEqual(false);
    });

    it("should not play next introduction if handling Blank or Invalid inputs", function() {
        callContext.handleInput(1);
        callContext.handleInput(1);
        callContext.setBlankOrInvalidInput(true);
        expect(callContext.shouldPlayIntroduction()).toEqual(false);
    });

    it("should play next introduction when in the chapter", function() {
        var level = course.children[0].children[0];
        callContext.handleInput(1);
        callContext.handleInput(1);
        expect(callContext.currentInteraction).toEqual(level);
        expect(callContext.shouldPlayIntroduction()).toEqual(true);
    });

    it("should provide the URLs for the audio files for the top-level menu option", function() {
        expect(callContext.audioForOptionToGoToTopLevel()).toEqual("../audio/jobaid/option.to.go.to.top.level.wav")
    });

    it("should provide the URLs for audio For Invalid Input Retry", function() {
        expect(callContext.audioForInvalidInputRetry()).toEqual("../audio/0000_error_in_pressing_number.wav")
    });

    it("should find lesson node matching the short code", function() {
        var lesson = course.children[1].children[0].children[0];
        callContext.navigateTo("555551111");
        expect(callContext.currentInteraction).toEqual(lesson);
        expect(callContext.dialedViaShortCode).toEqual(true);
    });

    it("should find go to beginning of course for invalid shortcode", function() {
        callContext.navigateTo("555550000");
        expect(callContext.currentInteraction).toEqual(course);
        expect(callContext.dialedViaShortCode).toEqual(false);
    });

    it("should get and set Blank or Invalid Input", function(){
        expect(callContext.isBlankOrInvalidInput()).toEqual(false);
        callContext.setBlankOrInvalidInput(true);
        expect(callContext.isBlankOrInvalidInput()).toEqual(true);
    });
})