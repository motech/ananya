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

    it("should initialize course state with bookmarks and scoresByChapter passed", function () {
        var callerData = {
           "isRegistered" : "true",
           "bookmark" : {"type":"lesson","chapterIndex":0,"lessonIndex":1},
           "scoresByChapter" : {
                "0" : 2,
                "1" : 3
            }
        }
        var courseState = new CourseState(callerData);
        expect(courseState.chapterIndex).toEqual(0);
        expect(courseState.lessonOrQuestionIndex).toEqual(1);
        expect(courseState.scoresByChapter["0"]).toEqual(2);
        expect(courseState.scoresByChapter["1"]).toEqual(3);
    });

    it("should define and initialize variables with default state if relevant information is not passed", function () {
        var callerData = {"bookmark":{}, "scoresByChapter":{}};
        var courseStateWithDefaults = new CourseState(null);
        defaultValueAssertions(courseStateWithDefaults);
    });

    function defaultValueAssertions(courseStateWithDefaults) {
        expect(courseStateWithDefaults.chapterIndex).toBeNull();
        expect(courseStateWithDefaults.lessonOrQuestionIndex).toBeNull();
        expect(courseStateWithDefaults.scoresByChapter).toEqual({});
        expect(courseStateWithDefaults.interactionKey).toEqual(StartCertificationCourse.KEY);
    };

});