describe("Course State", function() {
    it("should return a json for current state", function () {
        var courseState = new CourseState();
        courseState.setChapterIndex(8);
        courseState.setLessonOrQuestionIndex(3);
        courseState.setAnswerCorrect(true);
        courseState.setCurrentQuestionResponse(2);
        courseState.setInteractionKey("dummyInteraction");

        var json = courseState.toJson();

        expect(json.chapterIndex).toEqual(8);
        expect(json.lessonOrQuestionIndex).toEqual(3);
        expect(json.result).toEqual(true);
        expect(json.questionResponse).toEqual(2);
        expect(json.interactionKey).toEqual("dummyInteraction");
    });
});