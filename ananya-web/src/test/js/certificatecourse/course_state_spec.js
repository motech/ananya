describe("Course State", function() {
    it("should return a json for current state", function () {
        var courseState = new CourseState();
        courseState.setChapterIndex(8);
        courseState.setLessonOrQuestionIndex(3);
        courseState.setAnswerCorrect(true);
        courseState.setCurrentQuestionResponse(2);
        courseState.setInteractionKey("dummyInteraction");
        courseState.contentId = "f3298014a71f8f7b76a04ff44e181b45";
        courseState.contentType = CourseType.LESSON;
        courseState.courseItemState = CourseState.START;
        courseState.contentData = "asd";

        var json = courseState.toJson();

        expect(json.chapterIndex).toEqual(8);
        expect(json.lessonOrQuestionIndex).toEqual(3);
        expect(json.result).toEqual(true);
        expect(json.questionResponse).toEqual(2);
        expect(json.contentId).toEqual("f3298014a71f8f7b76a04ff44e181b45");
        expect(json.contentType).toEqual("lesson");
        expect(json.courseItemState).toEqual("start");
        expect(json.contentData).toEqual(null);

        /*
         * should return score for reportchapterscore interaction
         */

        courseState.interactionKey = ReportChapterScoreInteraction.KEY;
        courseState.scoresByChapter[8] = 10;

        json = courseState.toJson();
        expect(json.contentData).toEqual("10");
    });

    it("should initialize course state with bookmarks and scoresByChapter passed and course data", function () {
        var callerData = {
           "isRegistered" : "true",
           "bookmark" : {"type":"lesson","chapterIndex":0,"lessonIndex":1},
           "scoresByChapter" : {
                "0" : 2,
                "1" : 3
            }
        }

        var courseState = new CourseState(callerData, certificationCourseWithTwoLessonsInEveryChapter());
        expect(courseState.chapterIndex).toEqual(0);
        expect(courseState.lessonOrQuestionIndex).toEqual(1);
        expect(courseState.scoresByChapter["0"]).toEqual(2);
        expect(courseState.scoresByChapter["1"]).toEqual(3);
        expect(courseState.courseData).toEqual(certificationCourseWithTwoLessonsInEveryChapter());
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
        expect(courseStateWithDefaults.interactionKey).toEqual(StartCertificationCourseInteraction.KEY);
    };

    it("should set contentType, interactionKey, courseItem and as passed, and contentId to null since shouldLog is false",
        function() {
            var courseState = new CourseState();
            courseState.setCourseStateForServerCall(CourseType.COURSE, StartCertificationCourseInteraction.KEY, CourseState.START, false);

            expect(courseState.contentType).toEqual(CourseType.COURSE);
            expect(courseState.interactionKey).toEqual(StartCertificationCourseInteraction.KEY);
            expect(courseState.courseItemState).toEqual(CourseState.START);
            expect(courseState.contentId).toEqual(null);
        }
    );

    it("should set contentId properly, in case of course, chapter/quiz and lesson/question", function() {
        var courseState = new CourseState(null, certificationCourseWithTwoLessonsInEveryChapter());
        courseState.setCourseStateForServerCall(CourseType.COURSE, StartCertificationCourseInteraction.KEY, CourseState.START, true);

        expect(courseState.contentId).toEqual("f3298014a71f8f7b76a04ff44e181b45");

        courseState.setChapterIndex(1);
        courseState.setCourseStateForServerCall(CourseType.QUIZ, StartQuizInteraction.KEY, CourseState.START, true);

        expect(courseState.contentId).toEqual("f3298014a71f8f7b76a04ff44e181b46");

        courseState.setLessonOrQuestionIndex(1);
        courseState.setCourseStateForServerCall(CourseType.LESSON, LessonInteraction.KEY, CourseState.START, true);

        expect(courseState.contentId).toEqual("f3298014a71f8f7b76a04ff44e181b47");
    });

});