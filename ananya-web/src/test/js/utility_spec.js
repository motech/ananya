describe("Utility", function() {
    it("should stringify complex jsons", function() {
        var courseState = new CourseState();
        courseState.setChapterIndex(8);
        courseState.setLessonOrQuestionIndex(3);
        courseState.setAnswerCorrect(true);
        courseState.setCurrentQuestionResponse(2);
        courseState.setInteractionKey("dummyInteraction");
        var courseState2 = new CourseState();
        courseState2.setChapterIndex(9);
        courseState2.setLessonOrQuestionIndex(3);
        courseState2.setAnswerCorrect(true);
        courseState2.setCurrentQuestionResponse(2);
        courseState2.setInteractionKey("dummyInteraction");
        var courseState3 = new CourseState();
        courseState3.setChapterIndex(10);
        courseState3.setLessonOrQuestionIndex(3);
        courseState3.setAnswerCorrect(true);
        courseState3.setCurrentQuestionResponse(2);
        courseState3.setInteractionKey("dummyInteraction");

        var dataTransferList = new DataTransferList();
        dataTransferList.add(courseState.toJson());
        dataTransferList.add(courseState2.toJson());
        dataTransferList.add(courseState3.toJson());

        var stringified = Utility.stringify(dataTransferList.transferList);

        var courseStates = eval("(" + stringified + ")");

        expect(courseStates).toEqual(dataTransferList.transferList);
    });
});