describe("Call Context", function() {
    var course, callContext;

    beforeEach(function() {
        course = courseWithTwoLessonsInEveryChapter();
        callContext = new CallContext(course);
    });

    it("when initialized should have course as the current interaction.", function() {
        expect(callContext.currentInteraction).toEqual(course);
    });

    it("when the current interaction is course and goToChild is called, current interaction should be set to level requested.", function() {
        var childNumber = 1;
        var level1 = course.children[0];

        callContext.goToChild(childNumber);

        expect(callContext.currentInteraction).toEqual(level1);
    });

    it("should say that level requested is invalid if the requested index is outside range.", function() {
        expect(callContext.isValidChild(0)).toEqual(false);
        expect(callContext.isValidChild(-1)).toEqual(false);
        expect(callContext.isValidChild(1)).toEqual(true);
        expect(callContext.isValidChild(2)).toEqual(true);
        expect(callContext.isValidChild(3)).toEqual(false);
    });

    it("when lesson is finished, should set the current interaction to parent of the next lesson.", function() {
        callContext.goToChild(1).goToChild(1);
        var chapter1 = callContext.currentInteraction;
        callContext.goToChild(1);
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

        callContext.goToChild(levelNeeded).goToChild(chapterNeeded).goToChild(lessonNeeded);
        callContext.lessonFinished();

        expect(callContext.currentInteraction).toEqual(chapter2);
    });

    it("when last lesson in last chapter in a level is finished, should set the current interaction to first chapter in next level.", function () {
        var level2 = course.children[1];
        var level2_chapter1 = level2.children[0];

        var levelNeeded = 1;
        var chapterNeeded = 2;
        var lessonNeeded = 2;

        callContext.goToChild(levelNeeded).goToChild(chapterNeeded).goToChild(lessonNeeded);
        callContext.lessonFinished();

        expect(callContext.currentInteraction).toEqual(level2_chapter1);
    });

    it("when last lesson in last chapter in the last level is finished, should set the current interaction to last chapter in the last level.", function () {
        var level2 = course.children[1];
        var level2_chapter2 = level2.children[1];

        var levelNeeded = 2;
        var chapterNeeded = 2;
        var lessonNeeded = 2;

        callContext.goToChild(levelNeeded).goToChild(chapterNeeded).goToChild(lessonNeeded);
        callContext.lessonFinished();

        expect(callContext.currentInteraction).toEqual(level2_chapter2);
    });
});