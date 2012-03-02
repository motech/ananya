
var CourseState = function(callerData, courseData) {
    this.init = function(callerData, courseData){
        if(!callerData) {
            callerData = {"bookmark":{}, "scoresByChapter":{}};
        }
        this.defineStateVars();

        var bookmark = callerData.bookmark;

        this.courseData = courseData;
        this.scoresByChapter = callerData.scoresByChapter;

        if(bookmark && bookmark.type) {
            if(bookmark.chapterIndex!=null) {
                this.chapterIndex = bookmark.chapterIndex;
            }
            if(bookmark.lessonIndex!=null) {
                this.lessonOrQuestionIndex = bookmark.lessonIndex;
            }
            this.interactionKey = bookmark.type;
        }
    }

    this.defineStateVars = function() {
        this.chapterIndex = null;
        this.lessonOrQuestionIndex = null;
        this.currentQuestionResponse = null;
        this.isAnswerCorrect = null;
        this.interactionKey = StartCertificationCourse.KEY;
        this.scoresByChapter = null;
    };

    this.setChapterIndex = function(chapterIndex) {
        this.chapterIndex = chapterIndex;
    };

    this.setLessonOrQuestionIndex = function(lessonOrQuestionIndex) {
        this.lessonOrQuestionIndex = lessonOrQuestionIndex;
    };

    this.setCurrentQuestionResponse = function(currentQuestionResponse) {
        this.currentQuestionResponse = currentQuestionResponse;
    };

    this.setAnswerCorrect = function(isAnswerCorrect) {
        this.isAnswerCorrect = isAnswerCorrect;
    };

    this.setScoresByChapter = function(scoresByChapter) {
        this.scoresByChapter = scoresByChapter;
    };

    this.setInteractionKey = function(interactionKey) {
        this.interactionKey = interactionKey;
    };

    this.contentIdFunctions = { "parent" : this };

    this.contentIdFunctions[CourseType.COURSE] = function() { this["parent"].courseData.id };
    this.contentIdFunctions[CourseType.CHAPTER] = function() { this["parent"].courseData.children[this["parent"].chapterIndex].id };
    this.contentIdFunctions[CourseType.LESSON] = function() { this["parent"].courseData.children[this["parent"].chapterIndex].children[this["parent"].lessonOrQuestionIndex].id };
    this.contentIdFunctions[CourseType.QUIZ] = function() { this["parent"].courseData.children[this["parent"].chapterIndex].id };

    this.setCourseStateForServerCall = function(contentType, interactionKey, courseItemState, shouldLog) {
        this.interactionKey = interactionKey;
        this.contentType = contentType;
        this.courseItemState = courseItemState;
        if (shouldLog && this.contentIdFunctions[this.contentType]) {
            this.contentId = this.contentIdFunctions[this.contentType]();
        } else {
            this.contentId = null;
        }
    }

    this.setCourseItemState = function(courseItemState){
        this.courseItemState = courseItemState;
    };

    this.toJson = function() {

        function getStateData() {
            return (this.interactionKey == ReportChapterScoreInteraction.KEY)
                ? this.scoresByChapter[this.chapterIndex] : null;
        }

        return {
            "chapterIndex" : this.chapterIndex,
            "lessonOrQuestionIndex" : this.lessonOrQuestionIndex,
            "questionResponse" : this.currentQuestionResponse,
            "result" : this.isAnswerCorrect,
            "interactionKey": this.interactionKey,

            "contentId" : this.contentId,
            "contentType" : this.contentType,
            "courseItemState" : this.courseItemState,
            "contentData" : getStateData(),
            "certificateCourseId": ""
        };
    };

    this.init(callerData, courseData);

};

CourseState.START = "start";
CourseState.END = "end";

var CourseType = function() {};

CourseType.COURSE = "course";
CourseType.CHAPTER = "chapter";
CourseType.LESSON = "lesson";
CourseType.QUIZ = "quiz";